# 4. Workflows

This chapter follows each player-visible operation from the command/wand entry
point to the final state change. It is the mid-level view: it names the classes
and methods involved and the data that crosses each boundary. The exact
function-by-function call/data tables are in
[05-Low-Level-Data-Flow.md](05-Low-Level-Data-Flow.md); the sequence diagrams are
in [`diagrams/`](diagrams).

A useful reading convention:

- **client** = client thread, **server** = integrated-server thread,
  **io** = one of the two `minecraftgit-io` daemon threads.
- A method shown as `Class.method()` is the entry point of that step; the
  low-level document expands the nested calls.
- "world id" is `WorldContext.id` (safe world name + dimension, plus server
  address in multiplayer).

---

## 4.1 Client boot and service wiring

**Trigger:** Fabric calls `MinecraftGitClient.onInitializeClient()`.

1. Compute `root = FabricLoader.getConfigDir()/minecraftgit`.
2. `ModConfig.load(root)` reads or creates `config.json`, normalises it and saves
   it. `DebugLogger` is created from the config (`logsRoot`, `debug`,
   `debugBlocks`).
3. `new GitServices(root, config, debug)` constructs the service graph and the
   2-thread daemon IO pool (see [02-Architecture.md §2.3](02-Architecture.md#23-the-service-graph)).
4. `Class.forName("org.eclipse.jgit.api.Git")`; a missing JGit is logged but does
   not abort startup.
5. Register:
   - `ClientCommandRegistrationCallback` -> `GitCommand.register(dispatcher, services)`
   - `WandEvents.register(services)`
   - `SelectionRenderer.register(services)`
6. On `CLIENT_STOPPING`, `GitServices.close()` closes every `RepoService`, the
   debug writer, then the executor (2 s grace, then `shutdownNow`).

**State touched:** `config/minecraftgit/config.json` (created/normalised), debug
writer (lazy), command dispatcher, event buses.

**Failure modes:** unreadable/corrupt config -> defaults, old file preserved;
missing JGit -> error log, repository commands fail later; missing config
directory -> created.

---

## 4.2 `/git init` — create the per-world repository

**Preconditions:** none beyond an open world (`WorldContext.current`).

1. **client** `RepoCommands.init` -> `WorldContext.current` -> world id/display
   name.
2. **io** `CommandSupport.complete` runs `RepoManager.init(worldId, playerName,
   null)`:
   - `RepoManager.metadata(worldId)` loads/creates `RepoMetadata` with defaults
     from `RepoOptions` (`branch`, `defaultRemote`, author).
   - If the identity is still the default (`MinecraftGit`) and a player name is
     present, it adopts the player name and a derived
     `<sanitized-player>@minecraftgit.local` email.
   - `RepoService.init()`:
     - if `.git` exists: `ensureOpenLocked()` and return `infoLocked()`;
     - else `Git.init().setDirectory(repoPath).setInitialBranch(branch)`,
       configure `user.name`/`user.email`, write `README.md` and
       `.gitattributes`, `git.add(".")` + `git.add(--update ".")`, and commit
       `"Initialize MinecraftGit repository"` with the configured identity.
   - `RepoManager.init` writes the branch/author/remote back into `RepoMetadata`
     and saves it.
3. **client** the result is shown: repository path, branch, HEAD summary, and a
   multiplayer warning that applying builds requires single-player.

**Output state:** `repos/<world>/` is a real Git repository with one commit;
`meta/<world>.json` exists; no selection data is committed by init.

**Failure modes:** IO/JGit error -> `RepoException` with a friendly message
(`RepoService.error`); invalid remote later, not here; a repository that exists
but is not writable fails on the first write.

---

## 4.3 Selection creation with the wand

The wand is a vanilla item carrying custom data (`WandItem.TAG =
"minecraftgit_wand"`). The mod cannot register a server item, so
`WandItem.give` creates the stack through the integrated server's
`ServerPlayer` inventory; in multiplayer the player uses the configured item while
sneaking, or the explicit `pos1`/`pos2` commands.

### 4.3.1 Corner clicks

1. **client** `AttackBlockCallback` (left click) or `UseBlockCallback` (right
   click) fires. `WandEvents` filters:
   - only `ClientLevel` and main hand,
   - only the local player,
   - only when `WandItem.isWand(mainHand, config, player)` is true.
2. `WandEvents.handleCorner(services, player, BlockPos, first)`:
   - `WorldContext.current` (errors are swallowed for a non-world context),
   - `SelectionManager.setCorner(player, first, pos)` stores `cornerA`/`cornerB`
     in the player's `Session`; once both are present it clears them and returns
     `Cuboid.of(a, b)`.
3. `WandEvents.completeCuboid`:
   - if there is no active selection: `SelectionManager.setPending(player, cuboid)`
     and a chat hint to create/select a selection and run `/git selection add`;
   - else `SelectionService.addCuboid(worldId, owner, active, cuboid)` (persists
     immediately), `SelectionManager.setActive`, and a chat confirmation.
   - A rejected cuboid (limit/volume) is kept pending and the error is shown.
4. The callback returns `InteractionResult.FAIL` so the click never breaks/uses
   the block (important: a remote server never sees a wand edit).

The renderer (`SelectionRenderer`) draws the active selection, the pending
cuboid, and a temporary show highlight every 10 client ticks, up to
`config.renderBudget` particles.

### 4.3.2 Selection commands

| Command | Handler steps | Persistence |
|---------|---------------|-------------|
| `create <name>` | Take pending cuboid -> `SelectionService.create` -> optionally `addCuboid` (rollback the selection if it fails) -> `SelectionManager.setActive` | `selections/...json` + `meta/...json` |
| `list` | `SelectionService.list` + `SelectionManager.active` | none |
| `select <name>` | `SelectionService.get` -> `SelectionManager.setActive` | `meta/...json` |
| `show [name]` | Resolve -> `SelectionRenderer.highlight(world, name, 8)` | none (temporary) |
| `add [name]` | Take pending -> `SelectionService.addCuboid` (restore pending on failure) -> `setActive` | `selections/...json` |
| `remove <index> [name]` | `SelectionService.removeCuboid` -> **io** `StagingService.restrictStaged` | `selections/...json` + staged `.snap` |
| `reorigin [name]` | `SelectionService.reorigin` -> **io** `StagingService.rebaseStaged` | `selections/...json` + staged `.snap` |
| `delete <name>` | `SelectionService.delete`, clear active if needed -> **io** `StagingService.unstage` | `selections/...json`, `meta/...json`, staging |
| `info [name]` | Resolve -> print origin/bounds/cuboids/volume | none |
| `wand` | `WandItem.give` (single-player) or multiplayer hint | none |
| `pos1` / `pos2` | `Minecraft.hitResult` block -> `WandEvents.handleCorner` | same as a click |

`SelectionService` validates the name (`Names.requireSelectionName`), the
cuboid-count and volume caps, and writes through to
`SelectionMetadataStore` on every mutation. The store rewrites the per-player
JSON wrapper atomically.

---

## 4.4 `/git add` — capture and stage

**Preconditions:** initialized repository, selection with at least one cuboid.

1. **client** `StagingCommands.add` resolves the selection (explicit name or
   `SelectionManager.active`), checks `cuboidCount > 0`, and prints whether the
   capture runs on the integrated server or loaded client chunks.
2. `WorldAccess.capture(services, world, selection)`:
   - **single-player:** `server.execute(() -> services.snapshots().capture(selection, serverLevel, true))`;
   - **multiplayer:** `client.execute`/current thread -> `SnapshotService.capture(selection, client.level, false)`.
3. `SnapshotService.capture`:
   - validates origin and cuboid count, rejects `volume > maxBlocksPerCommit`;
   - iterates cuboids in X/Y/Z order, skips out-of-height blocks, optionally
     loads chunks on an integrated `ServerLevel`, skips already-seen positions;
   - encodes the block state (`BlockStateCodec`) and block entity
     (`BlockEntityCodec`, byte-capped, warnings collected);
   - captures non-player entities in each cuboid's AABB (`EntityCodec`,
     byte-capped, de-duplicated by UUID, sorted);
   - returns `CaptureResult(snapshot, skippedChunks, skippedBlocks,
     skippedEntities, warnings)`.
4. **client** the capture summary + first warnings are shown.
5. **io** `CommandSupport.complete` -> `StagingService.stage(selection, snapshot)`:
   - requires origin/owner,
   - rebases the snapshot if its origin differs from the selection origin,
   - writes `staging/<world>/<owner>/<selection>.snap` (`SnapshotStore`),
   - returns `StagedSelection`.
6. **client** `onStaged`: `SelectionService.setTracked(true)`,
   `SelectionManager.setActive`, success message, "Next: /git commit".

**Output state:** a durable gzipped snapshot; selection marked tracked; active
selection remembered in `meta/<world>.json`.

**Failure modes:** unloaded chunks -> partial capture (warnings, `partial()`
true); oversized block-entity/entity payload -> skipped with a warning; volume
over the cap -> `SelectionException`; staging write error -> `StagingException`.
A partial single-player capture is not fatal, but `status`/`diff` mark it
incomplete.

---

## 4.5 `/git rm` — untrack and discard staging

1. **client** `StagingCommands.rm` resolves the selection, requires an
   initialized repo.
2. **io** `StagingService.unstage` deletes the `.snap` (if present).
3. **client** `SelectionService.setTracked(false)` and a success message.

Note: this does not delete committed data from HEAD; it only removes local
staging and the tracked flag. A later commit of other selections leaves the
committed selection directory untouched in the repository.

---

## 4.6 `/git commit` — write staged selections to Git

**Preconditions:** initialized repository; at least one staged selection, unless
`--empty` is used.

1. **client** `CommitCommands.commit`:
   - lists the player's selections and keeps those with a staged `.snap`
     (`StagingService.hasStaged`),
   - if none and not `--empty` -> `SelectionException`,
   - builds `CommitRequest(playerName, uuid, worldId, gameMode, selections,
     message, allowEmpty)`,
   - **io** `CommitService.commit(request)`.
2. `CommitService.commit` (guarded by `commitLock`):
   - `RepoManager.service(worldId)`; require `.git`.
   - For each staged selection:
     - load the staged snapshot (`StagingService.load`);
     - read the old HEAD directory `repo.path()/<selection>` if it exists:
       `CommitMeta.read(meta.json)` for the old origin, then
       `QuadrantCsvReader.readSelection(...)` for the old snapshot; otherwise
       `Snapshot.empty(...)`;
     - `BuildDiff.between(old, staged)`; if empty and the origin did not change,
       mark unchanged and skip;
     - otherwise accumulate diff counts and build `CommitMeta`
       (`buildMeta`: relative cuboids, dimensions, origin/previousOrigin/
       reorigined, player/game mode/mod version/timestamps);
     - write the CSV tree + `meta.json` into a temp dir under `config/tmp`,
       collect the bytes into `files` keyed by `<selection>/<path>`, delete the
       temp dir.
   - If no committed names and not `allowEmpty` -> `CommitException("Nothing to
     commit...")`.
   - `commitMessage(...)` creates the human header plus the
     `MinecraftGit-Commit: v1` trailer block (selections, +/- counts, entities).
   - `RepoService.commitWorkspace(files, directories, message, allowEmpty)`:
     delete each replaced selection directory, write the new files
     (`safeResolve` path check), `git.add(".")` + `git.add(--update ".")`,
     commit with the configured identity, `allowEmpty` as requested.
   - Delete every staged `.snap` for the requested selections (note: it deletes
     staging even for unchanged selections, because `/git add` overwrote them).
   - Return `CommitResult`.
3. **client** `showResult`: short id, counts, first message line; an empty commit
   is called out.

**Output state:** a new commit containing the changed selection directories;
staging cleared; HEAD advanced on the current branch.

**Failure modes:** nothing staged (`CommitException`); selection not staged
(`CommitException`); no changes and not empty (`CommitException`); JGit/IO error
(`RepoException`/`CommitException`); a failure leaves the working tree partially
rewritten but does not create a commit unless JGit succeeded.

---

## 4.7 `/git status` and `/git diff`

Both are read-only: they capture the current world and compare it with HEAD and
staging. They never write the repository or the world.

### 4.7.1 `/git status`

1. **client** `StatusCommands.status`:
   - lists the player's selections; none -> hint and return;
   - computes candidates: has cuboids and (`tracked` or in HEAD or staged);
   - selections over `config.statusMaxVolume` go into `tooLarge` and are not
     captured;
   - builds a sequential `CompletableFuture` chain over candidates:
     `WorldAccess.capture(...)`; a partial capture is recorded in `incomplete`,
     the snapshot is put into a map keyed by selection name;
   - then **io** `StatusService.report(worldId, owner, selections, current,
     tooLarge, incomplete)`.
2. `StatusService.report`:
   - repo initialized? branch/HEAD via `RepoService`;
   - for each selection: `statusOf` loads staging, reads HEAD
     (`CommitMeta` origin + `QuadrantCsvReader`), computes staged-vs-HEAD and
     current-vs-base diff counts, and notes why a working diff is absent
     (too large / incomplete / not captured);
   - returns `Report(initialized, branch, head, statuses)`.
3. **client** prints one line per selection (`[untracked]`, `[clean]`,
   `[dirty]`, cuboids/volume, staged/working summaries, notes) and a summary.

### 4.7.2 `/git diff [name]`

1. **client** resolve selection; `tooLarge` if volume > `statusMaxVolume`.
2. If not too large: `WorldAccess.capture`; otherwise a completed `null`.
3. **io** `StatusService.diff`:
   - read HEAD snapshot (`readHead`);
   - load staged snapshot;
   - choose `base = staged != null ? staged : head != null ? head : empty`;
   - `stagedDiff = BuildDiff.between(head or empty, staged or base)`;
   - `workingDiff = BuildDiff.between(base, current)` unless no capture or
     incomplete;
   - `SelectionStatus`; return `DiffReport`.
4. **client** `showDiff`: HEAD/staged/current presence, notes, both summaries,
   and up to 6 sample blocks per added/changed/removed list.

**Failure modes:** a selection with no cuboids can still be listed; a corrupt
HEAD selection is logged in debug and treated as absent (`readHead` returns
null); a capture failure surfaces through `CommandSupport.handleError`.

---

## 4.8 Remote operations: `remote`, `fetch`, `push`, `log`

All run through `RepoService` on the **io** executor; results are delivered on the
client thread.

- `/git remote add <name> <url>`:
  `RepoService.remoteAdd` validates the remote name
  (`[A-Za-z0-9][A-Za-z0-9_.-]{0,63}`), converts local paths with
  `toUri` (Windows drive/UNC/POSIX paths become `file:` URIs), calls
  `git.remoteAdd`, and ensures the standard fetch refspec
  `+refs/heads/*:refs/remotes/<name>/*` exists.
- `/git remote remove/list` — `git.remoteRemove` / `git.remoteList`.
- `/git fetch [remote]`:
  `fetchLocked` requires a configured remote, fetches
  `+refs/heads/*:refs/remotes/<remote>/*`, removes deleted refs, and reports the
  tracking-ref update count.
- `/git push [--force]`:
  `RepoService.push` validates the remote, requires an attached branch and a
  non-empty HEAD, pushes `refs/heads/<branch>:refs/heads/<branch>`, maps
  `OK/UP_TO_DATE/REJECTED_*` statuses, throws on rejections (with a `--force`
  hint), and records the branch's remote/merge in the JGit config.
- `/git log [count]`:
  `RepoService.log` reads up to `count` commits and maps them to `CommitInfo`
  (id, short id, author, email, instant, first message line).
- `/git repo`:
  `RepoService.info` reads branch, HEAD, HEAD summary, configured remote, author
  and clean/dirty status.
- `/git config author <name> <email>`:
  `RepoManager.setAuthor` updates `RepoMetadata` and, if the service exists, the
  JGit `user.*` config.

**Failure modes:** invalid remote name/URL (`RepoException`); missing remote;
network/auth errors translated by `RepoService.friendlyMessage`; push
non-fast-forward -> explicit rejection message; detached HEAD.

---

## 4.9 `/git pull` — apply a remote build and fast-forward

**Preconditions:** single-player world; initialized repository.

1. **client** `PullCommands.pull`:
   - require player/world, reject multiplayer;
   - require `.git`;
   - resolve remote: explicit `--remote`, else `RepoMetadata.remoteName`, else
     `config.defaultRemote`;
   - `playerOrigin = player.blockPosition()`;
   - `localSelections = SelectionService.list(worldId, owner)`;
   - **io** `RepoService.planPull(remote, NOOP)`.
2. `RepoService.planPull`:
   - `fetchLocked` (fetch refs);
   - require attached branch; resolve `refs/remotes/<remote>/<branch>`;
   - classify:
     - no local HEAD -> `FAST_FORWARD`;
     - equal -> `UP_TO_DATE`;
     - local ancestor of remote -> `FAST_FORWARD`;
     - remote ancestor of local -> `UP_TO_DATE`;
     - local is a pristine init commit (README + `.gitattributes` only) ->
       `FAST_FORWARD` with no merge base;
     - otherwise `DIVERGED`.
3. **client** `onPlan`:
   - `UP_TO_DATE` -> message and stop;
   - `!canFastForward()` -> `PullException` (diverged);
   - **io** `PullService.prepare(worldId, localSelections, playerOrigin, repo, plan, onlySelection)`:
     - list remote selection names at `plan.remoteHead()`;
     - explicit `onlySelection`: must exist; if the remote has more than one
       selection, refuse the partial pull;
     - for each remote selection: export the tree to a temp dir
       (`RepoService.exportSelectionAt`), read `CommitMeta`, read the target
       `Snapshot` (`QuadrantCsvReader`), find the local selection/origin, read the
       local HEAD snapshot if the directory exists; produce `PreparedPull`.
4. **client** `applyNext` for each `PreparedPull`:
   - `ensureSelection`: create the selection at the local origin if absent, set
     the origin if unset, add cuboids (from `meta.relativeCuboids` or the target
     block bounds) if it has none, mark tracked;
   - `ApplyPlan.between(selection, localHead, localOrigin, target, localOrigin, true)`;
   - `ApplyService.apply(world, plan)` (see §4.10);
   - on success show the apply result and move to the next selection;
   - on failure call `fail`: async cleanup of the temp dirs and a message that the
     local branch was **not** moved.
5. **io** `finishPull`:
   - `RepoService.fastForward(plan)` -> `git.reset(HARD, plan.remoteHead)`;
   - `PullService.cleanup(prepared)`;
   - **client** success message (or an error if the branch could not be moved).
   - If the remote has no selections, the branch is still fast-forwarded.

**Ordering guarantee:** the branch is moved only after every apply succeeded.
A world write failure leaves HEAD at the old commit and the temp dirs are cleaned.

**Failure modes:** multiplayer; no repo; no remote branch; diverged history;
partial pull refused; apply/backup failure; branch changed during the pull;
fast-forward failure after apply (world changed, branch not moved — recover with
`--abort` and retry).

---

## 4.10 Apply mechanics (`/git pull`, `/git pull --abort`, `/git checkout`)

All three build an `ApplyPlan` and call the same `ApplyService.apply`.

1. **client** `ApplyService.apply`:
   - `Minecraft.getSingleplayerServer()` and `world.serverLevel(client)` must be
     non-null, otherwise a failed future with `ApplyException`;
   - `server.execute(() -> begin(server, level, world, plan, future))`.
2. **server** `begin`:
   - if the server is stopping -> failed future;
   - if `plan.createBackup()`: `SnapshotService.capturePositions(level,
     plan.selectionName(), plan.targetOrigin(), plan.allPositions(), true)` —
     blocks and block entities only, at every placement/removal position;
   - **io** `BackupService.store(worldId, owner, selection, backup)`;
   - back on the server thread, `beginWrites(server, level, plan, backup, future)`.
3. **server** `beginWrites`:
   - convert `plan.removals()` to air writes (`removal=true`);
   - convert `plan.placements()` to writes, decode block state
     (`BlockStateCodec.decode`; unknown -> air + warning), keep block-entity
     payload; skip out-of-height positions with warnings;
   - sort by `(y, x, z)`;
   - create a `Job` and `process()`.
4. **server** `Job.process`:
   - while fewer than `config.blocksPerTick` writes: set the block
     (`level.setBlock(..., Block.UPDATE_CLIENTS)`), skip if already the same,
     apply block-entity NBT if present (`BlockEntityCodec.apply`), count
     placed/removed/failed, collect warnings;
   - if more writes remain: `server.execute(this::process)` (yields between
     ticks); else `finish()`.
5. **server** `finish`:
   - remove old entities (`plan.entityRemovals()` matched by type and position
     within 3 blocks, `discard()`);
   - place new entities (`EntityCodec.apply` on the server level);
   - complete `ApplyResult(placed, removed, skipped+failed, entitiesPlaced,
     entitiesRemoved, backup, warnings)`.
6. **client** the completion callback shows the summary and up to 5 warnings;
   `--abort`/`checkout` additionally clean temp dirs asynchronously.

### 4.10.1 `ApplyPlan` construction

`ApplyPlan.between(selection, old, oldOrigin, target, targetOrigin, createBackup)`
uses **relative** coordinates:

- `old == null` or the origin moved: place every target block at
  `targetOrigin + relative`; remove every old block at `oldOrigin + relative`
  (for a move, remove at the old origin).
- same origin: place only new/changed relative positions; remove only old
  relative positions that are gone and not re-placed.
- entities: if old is null or moved, remove all old entities and place all target
  entities at `targetOrigin + relative`; otherwise diff the entity records.
- `restore(selection, backup)` treats the backup's block snapshots as already
  absolute placements and creates placements for its entities.

---

## 4.11 `/git pull --abort` — restore the latest pre-apply backup

1. **client** `PullCommands.abort`:
   - require single-player; resolve the selection (explicit or active);
   - `BackupService.latest(worldId, owner, name)` -> `Backup(snapshot, file)` or
     `PullException("No pre-apply backup exists...")`;
   - `ApplyPlan.restore(selection, backup.snapshot())`;
   - `ApplyService.apply(world, plan)` (which itself creates a new backup before
     restoring, by design);
   - show the apply result.

**Limitation:** backups contain blocks and block entities, not entities. Abort
does not remove/restore entities.

---

## 4.12 `/git checkout <commit> [name]` — apply an old commit without moving HEAD

1. **client** `PullCommands.checkout`:
   - require single-player and an initialized repo;
   - resolve selection; require an origin;
   - `RepoService.resolve(revision)` -> `ObjectId` or `PullException`;
   - **io** `PullService.prepareCheckout(selection, repo, commit)`:
     - export the selection tree at the commit to a temp dir
       (`exportSelectionAt`), read `CommitMeta` and the target snapshot;
     - read local HEAD for the selection and its origin;
     - return `PreparedCheckout(target, meta, localHead, localHeadOrigin, tempDir)`.
2. **client** build
   `ApplyPlan.between(selection, localHead, selection.origin(), target,
   selection.origin(), true)` — the local build is applied at the **selection's
   local origin**, not the absolute origin recorded in the old commit.
3. `ApplyService.apply`; show result; **io** cleanup of the temp dir.

HEAD/branch are untouched; the old commit's files are not written into the
worktree. A checkout is therefore a read-only repository operation plus a world
write.

---

## 4.13 Configuration and debug toggles

- `/git config show` prints effective settings and the data root
  (`GitCommand.showConfig`).
- `/git config debug` flips `config.debug`, updates `DebugLogger.setEnabled`,
  saves the config and prints the log file path.
- `/git config blocks` flips `config.debugBlocks` and `DebugLogger.setBlocks`
  (per-block logging; only effective when the debug log is enabled).
- `/git version` prints the mod and Minecraft versions from Fabric Loader
  (`GitCommand.version`); `GitServices.modVersion()` is also written into
  `meta.json`.
- `/git help` is a static list in `GitCommand.help`.

---

## 4.14 Failure and recovery summary

| Situation | What the mod does | Recovery |
|-----------|-------------------|----------|
| Evidence/capture missed chunks | `CaptureResult.partial()` + warnings; status marks incomplete | Load chunks, re-add |
| Staging file corrupt/truncated | `StagingException` | `/git add` again |
| Nothing staged / unchanged | `CommitException` | `/git add` or `--empty` |
| Push rejected (non-fast-forward) | `RepoException` with fetch/pull or `--force` hint | Fetch/pull or force intentionally |
| Diverged histories | `PullException`, no merge | Push local work or clone fresh |
| Partial pull (remote has several selections) | `PullException` | Pull all |
| Apply fails mid-way | future fails; branch not moved; backup may exist | `/git pull --abort`, retry |
| Branch reset fails after apply | error message; world changed, HEAD old | `/git pull --abort`, retry |
| Abort with no backup | `PullException` | Nothing to restore |
| Player leaves during async work | result silently dropped (`CommandSupport`) | Re-run the command |
| Config corrupt | defaults, old file preserved | Fix `config.json` |

The code-level version of this table is in
[07-Error-Handling-and-Concurrency.md](07-Error-Handling-and-Concurrency.md).
