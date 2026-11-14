# 5. Low-Level Data Flow

This is the lowest level of the documentation: **which function of one class calls
which function of another class, and what value crosses that boundary**. It is
written as tables so it can be read in a diff without a PlantUML renderer; the
same flows are drawn in [`diagrams/`](diagrams) (`level-4-sequence-*.puml` and
`level-5-function-data-flow-*.puml`).

## 5.1 Reading the tables

Each row is one call edge. Columns:

- **Caller** — the function that makes the call, as `Class.method`.
- **Callee** — the function that receives it.
- **Data in** — the values/objects passed, including units and coordinate space.
- **Data out / effect** — the returned value and/or the state written.
- **Thread** — `client`, `server` (integrated server), or `io`
  (`minecraftgit-io`). A chain that hops threads marks each hop.

Notation: `worldId` = `WorldContext.id`; `owner` = player UUID; `origin` =
absolute `BlockPos`; `sel` = `Selection`; `snap` = `Snapshot`;
`req` = `CommitRequest`; `plan` = `PullPlan`/`ApplyPlan` as named.

The tables deliberately omit trivial `Objects.requireNonNull`, logging and pure
getter/setter edges; they include every edge that carries build data or changes
persistent/world state.

---

## 5.2 Boot and teardown

| Caller | Callee | Data in | Data out / effect | Thread |
|--------|--------|---------|-------------------|--------|
| `MinecraftGitClient.onInitializeClient` | `ModConfig.load` | config root `Path` | `ModConfig` (defaults filled, saved) | client |
| `MinecraftGitClient.onInitializeClient` | `DebugLogger.<init>` | `logsRoot`, `debug`, `debugBlocks` | logger (lazy file) | client |
| `MinecraftGitClient.onInitializeClient` | `GitServices.<init>` | root `Path`, `ModConfig`, `DebugLogger` | fully wired service graph + IO executor | client |
| `GitServices.<init>` | `RepoManager.<init>` | config, debug | repo manager + locator/store | client |
| `GitServices.<init>` | `SelectionService.<init>` | config | selection service + metadata store | client |
| `GitServices.<init>` | `SelectionManager.<init>` | selections, repos | per-player session map | client |
| `GitServices.<init>` | `SnapshotService.<init>` | config, debug | capture service | client |
| `GitServices.<init>` | `StagingService.<init>` | staging root, `SnapshotStore`, debug | staging service | client |
| `GitServices.<init>` | `CommitService.<init>` | repos, staging, config, debug, modVersion | commit service + lock | client |
| `GitServices.<init>` | `StatusService.<init>` | repos, staging, debug | status service | client |
| `GitServices.<init>` | `BackupService.<init>` | backups root, maxBackups | backup service | client |
| `GitServices.<init>` | `PullService.<init>` | tmp root | pull/checkout preparation service | client |
| `GitServices.<init>` | `ApplyService.<init>` | snapshots, backups, config, debug, io | apply service | client |
| `GitCommand.register` | `RepoCommands.attach`, `SelectionCommands.attach`, ... | root literal, services | Brigadier command tree | client |
| `MinecraftGitClient` stop callback | `GitServices.close` | — | closes repos, debug writer, IO pool | client |
| `GitServices.close` | `RepoManager.close` | — | closes every open `RepoService` | client |

---

## 5.3 Wand clicks and selection corners

| Caller | Callee | Data in | Data out / effect | Thread |
|--------|--------|---------|-------------------|--------|
| Fabric attack/use event | `WandEvents` lambda | player, world, hand, pos/direction/hit | `InteractionResult.FAIL` when a wand click is consumed | client |
| `WandEvents` lambda | `WandItem.isWand` | main-hand `ItemStack`, config, player | `boolean` (custom data or sneak fallback) | client |
| `WandEvents.handleCorner` | `WorldContext.current` | `Minecraft` | `WorldContext` (world id, dimension, singleplayer flag) | client |
| `WandEvents.handleCorner` | `SelectionManager.setCorner` | player, `first`, absolute `BlockPos` | updates `Session.cornerA/B`; `Optional<Cuboid>` when both exist | client |
| `WandEvents.completeCuboid` | `SelectionManager.active` | world, player | active selection name or `null` | client |
| `SelectionManager.active` | `RepoManager.lastSelection` | worldId | remembered name (from `RepoMetadata`) | client |
| `SelectionManager.active` | `SelectionService.find` | worldId, owner, name | `Optional<Selection>` (validates the remembered name) | client |
| `WandEvents.completeCuboid` | `SelectionManager.setPending` | player, cuboid | `Session.pending` (when no active selection) | client |
| `WandEvents.completeCuboid` | `SelectionService.addCuboid` | worldId, owner, name, cuboid | `Selection` (persisted) | client |
| `SelectionService.addCuboid` | `Selection.addCuboid` | cuboid | cuboid appended, sorted, origin seeded if unset | client |
| `SelectionService.addCuboid` | `SelectionMetadataStore.save` | worldId, owner, ownerName, `List<Selection>` | atomic JSON write | client |
| `WandEvents.completeCuboid` | `SelectionManager.setActive` | world, player, name | `RepoManager.setLastSelection` | client |
| `RepoManager.setLastSelection` | `RepoMetadataStore.save` | `RepoMetadata` | atomic JSON write | client |
| `WandEvents.completeCuboid` | `Player.sendSystemMessage` | chat `Component` | feedback | client |

`SelectionCommands.position` (`pos1`/`pos2`) calls
`WandEvents.handleCorner(services, player, BlockPos, first)` with the targeted
block, so it shares the entire chain above.

---

## 5.4 Selection management commands

| Caller | Callee | Data in | Data out / effect | Thread |
|--------|--------|---------|-------------------|--------|
| `SelectionCommands.create` | `SelectionManager.takePending` | player | `Cuboid` or `null` (pending cleared) | client |
| `SelectionCommands.create` | `SelectionService.create` | worldId, owner, ownerName, name | `Selection` persisted | client |
| `SelectionCommands.create` | `SelectionService.addCuboid` | worldId, owner, name, pending | adds the pending cuboid; rollback `delete` on failure | client |
| `SelectionCommands.add` | `SelectionService.addCuboid` | worldId, owner, name, pending | persisted; pending restored on failure | client |
| `SelectionCommands.remove` | `SelectionService.removeCuboid` | worldId, owner, name, oneBasedIndex | removed `Cuboid`; selection persisted | client |
| `SelectionCommands.remove` | `StagingService.restrictStaged` | worldId, owner, `Selection` | staged `.snap` rewritten with `Snapshot.retain` + `rebase` | io |
| `SelectionCommands.reorigin` | `SelectionService.reorigin` | worldId, owner, name | previous `BlockPos`; origin moved; persisted | client |
| `SelectionCommands.reorigin` | `StagingService.rebaseStaged` | worldId, owner, name, newOrigin | staged `.snap` rewritten via `Snapshot.rebase` | io |
| `SelectionCommands.delete` | `SelectionService.delete` | worldId, owner, name | selection removed + persisted; active cleared | client |
| `SelectionCommands.delete` | `StagingService.unstage` | worldId, owner, name | `.snap` deleted | io |
| `SelectionCommands.select` | `SelectionManager.setActive` | world, player, name | `RepoMetadata.lastSelection` persisted | client |
| `SelectionCommands.show` | `SelectionRenderer.highlight` | worldId, name, seconds | static highlight state for the renderer | client |
| `SelectionCommands.wand` | `WandItem.give` | player, config, client | `boolean` (false in multiplayer); item added on the server thread | client -> server |
| `SelectionCommands.info` | `Selection.boundsMin/boundsMax/volume` | — | formatted chat | client |

---

## 5.5 `/git add` — capture and stage

| Caller | Callee | Data in | Data out / effect | Thread |
|--------|--------|---------|-------------------|--------|
| `StagingCommands.add` | `SelectionManager.active` | world, player | active name when no explicit argument | client |
| `StagingCommands.add` | `SelectionService.get` | worldId, owner, name | `Selection` | client |
| `StagingCommands.add` | `WorldAccess.capture` | services, world, selection | `CompletableFuture<CaptureResult>` | client |
| `WorldAccess.capture` (SP) | `MinecraftServer.execute` | lambda | hops to the server thread | client -> server |
| `WorldAccess.capture` (SP) | `SnapshotService.capture` | selection, `ServerLevel`, `loadChunks=true` | `CaptureResult` | server |
| `WorldAccess.capture` (MP) | `SnapshotService.capture` | selection, `ClientLevel`, `loadChunks=false` | `CaptureResult` | client |
| `SnapshotService.capture` | `BlockStateCodec.encode` | `BlockState` | canonical block-state string | server/client |
| `SnapshotService.capture` | `BlockEntityCodec.encode` | `BlockEntity`, registry access | `nbt:<SNBT>` or `null`; byte count checked | server/client |
| `SnapshotService.capture` | `BlockSnapshot.at` | absolute pos, origin, block data, payload | `BlockSnapshot` (absolute + relative) | server/client |
| `SnapshotService.capture` | `EntityCodec.encode` | `Entity` | `nbt:<SNBT>`; byte count checked | server/client |
| `SnapshotService.capture` | `new Snapshot` | selection name, origin, blocks, entities, now | immutable `Snapshot` | server/client |
| `SnapshotService.capture` | `new CaptureResult` | snapshot, skipped chunks/blocks/entities, warnings | `CaptureResult` | server/client |
| `StagingCommands.add` callback | `StagingService.stage` | selection, captured `Snapshot` | `StagedSelection`; writes the `.snap` | io |
| `StagingService.stage` | `Snapshot.rebase` | selection origin | rebased `Snapshot` if origins differ | io |
| `StagingService.stage` | `SnapshotStore.write` | file `Path`, `Snapshot` | gzipped binary file (atomic replace) | io |
| `StagingCommands.onStaged` | `SelectionService.setTracked` | worldId, owner, name, `true` | `Selection.tracked=true`; persisted | client |
| `StagingCommands.onStaged` | `SelectionManager.setActive` | world, player, name | `RepoMetadata.lastSelection` persisted | client |

`/git rm` is the reverse:

| Caller | Callee | Data in | Data out / effect | Thread |
|--------|--------|---------|-------------------|--------|
| `StagingCommands.rm` | `SelectionService.get` | worldId, owner, name | `Selection` | client |
| `StagingCommands.rm` | `StagingService.unstage` | worldId, owner, name | `.snap` deleted | io |
| `StagingCommands.rm` callback | `SelectionService.setTracked` | ..., `false` | `Selection.tracked=false`; persisted | client |

---

## 5.6 `/git commit`

| Caller | Callee | Data in | Data out / effect | Thread |
|--------|--------|---------|-------------------|--------|
| `CommitCommands.commit` | `SelectionService.list` | worldId, owner | `List<Selection>` | client |
| `CommitCommands.commit` | `StagingService.hasStaged` | worldId, owner, name | `boolean` per selection | client |
| `CommitCommands.commit` | `new CommitRequest` | player name/uuid, worldId, game mode, staged list, message, allowEmpty | immutable request | client |
| `CommitCommands.commit` | `CommitService.commit` | `CommitRequest` | `CompletableFuture<CommitResult>` | io |
| `CommitService.commit` | `RepoManager.service` | worldId | `RepoService` (created/opened lazily) | io |
| `CommitService.commit` | `RepoService.isInitialized` | — | `boolean` (`.git` directory) | io |
| `CommitService.commit` | `StagingService.load` | worldId, owner, name | `Optional<StagedSelection>` (snapshot + file + time) | io |
| `CommitService.commit` | `CommitMeta.read` | old `meta.json` path | `Optional<CommitMeta>` (old origin) | io |
| `CommitService.commit` | `QuadrantCsvReader.readSelection` | old selection dir, name, fallback origin | old `Snapshot` (HEAD contents) | io |
| `CommitService.commit` | `BuildDiff.between` | old snapshot, staged snapshot | added/changed/removed/entity counts | io |
| `CommitService.commit` | `new CommitMeta` | selection, request, origin, previousOrigin, counts, relative cuboids, modVersion | commit metadata | io |
| `CommitService.commit` | `QuadrantCsvWriter.writeSelection` | temp dir, staged snapshot | CSV tree in temp | io |
| `CommitService.commit` | `CommitMeta.write` | temp `meta.json` path | JSON file | io |
| `CommitService.commit` | `collectFiles` | temp dir, selection name | `Map<String, byte[]>` keyed `<selection>/<relative path>` | io |
| `CommitService.commit` | `RepoService.commitWorkspace` | files map, selection dirs, message, allowEmpty | commit id or `null` | io |
| `RepoService.commitWorkspace` | `safeResolve` / `deleteRecursively` | relative paths | deletes old selection dirs, writes new files | io |
| `RepoService.commitWorkspace` | `stageAllLocked` | — | `git.add(".")` + `git.add(--update ".")` | io |
| `RepoService.commitWorkspace` | `statusLocked` | — | JGit `Status`; clean + !allowEmpty -> `null` | io |
| `RepoService.commitWorkspace` | `identityLocked` | author name/email | `PersonIdent` for author+committer | io |
| `CommitService.commit` | `StagingService.deleteAll` | worldId, owner, name | removes `.snap` for each requested selection | io |
| `CommitService.commit` | `new CommitResult` | id, short id, names, counts, message | result | io |
| callback | `Messages.success`/`raw` | result fields | chat feedback | client |

---

## 5.7 `/git status`

| Caller | Callee | Data in | Data out / effect | Thread |
|--------|--------|---------|-------------------|--------|
| `StatusCommands.status` | `SelectionService.list` | worldId, owner | `List<Selection>` | client |
| `StatusCommands.status` | `RepoManager.repoPath` + `Files.isDirectory` | worldId + name | `inHead` boolean | client |
| `StatusCommands.status` | `StagingService.hasStaged` | worldId, owner, name | `staged` boolean | client |
| `StatusCommands.status` | `Selection.volume` | — | compared with `config.statusMaxVolume` | client |
| `StatusCommands.status` | `WorldAccess.capture` | services, world, selection | `CaptureResult`; partial -> `incomplete` | client/server |
| `StatusCommands.status` | `StatusService.report` | worldId, owner, all selections, `Map<String, Snapshot>`, tooLarge, incomplete | `Report` | io |
| `StatusService.report` | `RepoService.currentBranch` / `headCommit` | — | branch + short HEAD | io |
| `StatusService.statusOf` | `StagingService.load` | worldId, owner, selection name | `Optional<StagedSelection>` | io |
| `StatusService.readHead` | `CommitMeta.read` | `meta.json` path | `Optional<CommitMeta>` (origin) | io |
| `StatusService.readHead` | `QuadrantCsvReader.readSelection` | selection dir, name, origin | HEAD `Snapshot` or `null` | io |
| `StatusService.statusOf` | `BuildDiff.between` | HEAD/staged/current snapshots | `DiffCounts` for staged and working | io |
| `StatusService.report` | `new Report` | initialized, branch, head, statuses | immutable report | io |
| callback | `StatusCommands.showStatus` | `Report` | chat lines + summary | client |

---

## 5.8 `/git diff [name]`

| Caller | Callee | Data in | Data out / effect | Thread |
|--------|--------|---------|-------------------|--------|
| `StatusCommands.diff` | `SelectionService.get` | worldId, owner, name | `Selection` | client |
| `StatusCommands.diff` | `WorldAccess.capture` | services, world, selection | `CaptureResult` or `null` when too large | client/server |
| `StatusCommands.diff` | `StatusService.diff` | worldId, owner, selection, current snapshot, tooLarge, incomplete | `DiffReport` | io |
| `StatusService.diff` | `readHead` | repo, selection | HEAD `Snapshot` or `null` | io |
| `StatusService.diff` | `loadStaged` | worldId, owner, selection | `Optional<StagedSelection>` | io |
| `StatusService.diff` | `BuildDiff.between` | (HEAD/empty, staged/base) and (base, current) | `stagedDiff`, `workingDiff` | io |
| `StatusService.diff` | `statusOf` | initialized, repo, selection, current, tooLarge, incomplete | `SelectionStatus` | io |
| callback | `showDiff` | `DiffReport` | chat summary + up to 6 samples per list | client |

---

## 5.9 `/git pull`

This is the longest cross-class chain. Each numbered block is a thread hop.

**(a) Plan — client -> io**

| Caller | Callee | Data in | Data out / effect | Thread |
|--------|--------|---------|-------------------|--------|
| `PullCommands.pull` | `WorldContext.current` | `Minecraft` | world (must be single-player) | client |
| `PullCommands.pull` | `RepoManager.isInitialized` / `service` | worldId | `RepoService` | client |
| `PullCommands.pull` | `remoteFor` | services, world, requested remote | remote name (explicit > metadata > config) | client |
| `PullCommands.pull` | `SelectionService.list` | worldId, owner | local `List<Selection>` | client |
| `PullCommands.pull` | `RepoService.planPull` | remote, `RepoProgress.NOOP` | `PullPlan` | io |
| `RepoService.planPull` | `fetchLocked` | remote, progress | fetched refs (JGit `FetchResult`) | io |
| `RepoService.planPull` | `repository.resolve` | `refs/remotes/<remote>/<branch>`, `HEAD` | local/remote `ObjectId` | io |
| `RepoService.planPull` | `RevWalk.isMergedInto` / `RevFilter.MERGE_BASE` | local/remote commits | `FAST_FORWARD`/`UP_TO_DATE`/`DIVERGED`, merge base | io |
| `RepoService.planPull` | `isPristineInitLocked` | local commit | true for a README+`.gitattributes`-only init commit | io |
| `RepoService.planPull` | `new PullPlan` | remote, branch, heads, base, type, fetched refs | plan | io |

**(b) Prepare — client -> io**

| Caller | Callee | Data in | Data out / effect | Thread |
|--------|--------|---------|-------------------|--------|
| `PullCommands.onPlan` | `PullService.prepare` | worldId, local selections, player origin, repo, plan, onlySelection | `List<PreparedPull>` | io |
| `PullService.prepare` | `RepoService.listSelectionsAt` | `plan.remoteHead()` | `Set<String>` selection names in the commit | io |
| `PullService.prepare` | `RepoService.exportSelectionAt` | commit id, name, temp dir | writes the selection tree into the temp dir; `boolean found` | io |
| `PullService.prepare` | `CommitMeta.read` | temp `meta.json` | optional metadata (origin, relative cuboids) | io |
| `PullService.prepare` | `QuadrantCsvReader.readSelection` | temp dir, name, origin | target `Snapshot` | io |
| `PullService.prepare` | `QuadrantCsvReader.readSelection` | local repo selection dir | local HEAD `Snapshot` + origin | io |
| `PullService.prepare` | `new PreparedPull` | name, target, meta, local selection, local origin, local head, head origin, temp dir | prepared record | io |

**(c) Apply each selection — client -> server/io**

| Caller | Callee | Data in | Data out / effect | Thread |
|--------|--------|---------|-------------------|--------|
| `PullCommands.applyNext` | `ensureSelection` | services, world, player, `PreparedPull` | `Selection` created/tracked with cuboids | client |
| `ensureSelection` | `SelectionService.createAt` | worldId, owner, ownerName, name, localOrigin | new `Selection` | client |
| `ensureSelection` | `SelectionService.addCuboid` | worldId, owner, name, absolute `Cuboid` | cuboids from metadata or block bounds | client |
| `ensureSelection` | `SelectionService.setTracked` | ..., `true` | tracked flag persisted | client |
| `PullCommands.applyNext` | `ApplyPlan.between` | selection, localHead, localOrigin, target, localOrigin, `true` | `ApplyPlan` (placements/removals/entity changes) | client |
| `PullCommands.applyNext` | `ApplyService.apply` | world, plan | `CompletableFuture<ApplyResult>` | client -> server |
| `ApplyService.apply` | `MinecraftServer.execute` | `begin(...)` | hops to server thread | client -> server |
| `ApplyService.begin` | `SnapshotService.capturePositions` | level, name, targetOrigin, `plan.allPositions()`, `true` | backup `Snapshot` | server |
| `ApplyService.begin` | `BackupService.store` | worldId, owner, name, backup snapshot | backup file `Path` | io |
| `ApplyService.beginWrites` | `BlockStateCodec.decode` | block-state string | `BlockState` (unknown -> air) | server |
| `ApplyService.Job.process` | `Level.setBlock` | `BlockPos`, `BlockState`, `Block.UPDATE_CLIENTS` | writes one block per tick batch | server |
| `ApplyService.Job.process` | `BlockEntityCodec.apply` | `BlockEntity`, payload, registry access | applies NBT; false for unsupported payload | server |
| `ApplyService.Job.finish` | `EntityCodec.apply` | `ServerLevel`, `EntitySnapshot`, absolute x/y/z | spawned `Entity` / `null` | server |
| `ApplyService.Job.finish` | `new ApplyResult` | counts, backup path, warnings | result | server |

**(d) Fast-forward and clean up — client -> io**

| Caller | Callee | Data in | Data out / effect | Thread |
|--------|--------|---------|-------------------|--------|
| `PullCommands.finishPull` | `RepoService.fastForward` | `PullPlan` | `git.reset(HARD, plan.remoteHead())`; `boolean moved` | io |
| `PullCommands.finishPull` | `PullService.cleanup` | `List<PreparedPull>` | deletes the temp root | io |
| `PullCommands.fail` | `PullService.cleanup` | prepared list | deletes temp dirs; branch not moved | io |
| callback | `Messages.success`/`error` | counts/ids | chat feedback | client |

---

## 5.10 `/git pull --abort`

| Caller | Callee | Data in | Data out / effect | Thread |
|--------|--------|---------|-------------------|--------|
| `PullCommands.abort` | `SelectionManager.active` / `SelectionService.get` | world, player, requested name | `Selection` | client |
| `PullCommands.abort` | `BackupService.latest` | worldId, owner, name | `Optional<Backup>` (snapshot + file) | io |
| `BackupService.latest` | `SnapshotStore.read` | backup file path | `Snapshot` | io |
| `PullCommands.abort` | `ApplyPlan.restore` | selection, backup snapshot | `ApplyPlan` (absolute placements) | client |
| `PullCommands.abort` | `ApplyService.apply` | world, plan | `ApplyResult` (and a new backup) | client -> server |
| callback | `sendApplyResult` | `ApplyResult` | chat summary + warnings | client |

---

## 5.11 `/git checkout`

| Caller | Callee | Data in | Data out / effect | Thread |
|--------|--------|---------|-------------------|--------|
| `PullCommands.checkout` | `RepoService.resolve` | revision string | `Optional<ObjectId>` | io |
| `PullCommands.checkout` | `PullService.prepareCheckout` | selection, repo, commit | `PreparedCheckout` | io |
| `PullService.prepareCheckout` | `RepoService.exportSelectionAt` | commit, name, temp dir | target tree on disk; `boolean found` | io |
| `PullService.prepareCheckout` | `CommitMeta.read` | temp `meta.json` | optional metadata | io |
| `PullService.prepareCheckout` | `QuadrantCsvReader.readSelection` | temp dir, name, origin | target `Snapshot` | io |
| `PullService.prepareCheckout` | `QuadrantCsvReader.readSelection` | local repo dir | local HEAD `Snapshot` + origin | io |
| `PullCommands.checkout` | `ApplyPlan.between` | selection, localHead, `selection.origin()`, target, `selection.origin()`, `true` | `ApplyPlan` at the local origin | client |
| `PullCommands.checkout` | `ApplyService.apply` | world, plan | `ApplyResult` | client -> server |
| callback | `PullService.cleanup` | temp dir | deletes the export | io |

---

## 5.12 `/git init` and remote operations

| Caller | Callee | Data in | Data out / effect | Thread |
|--------|--------|---------|-------------------|--------|
| `RepoCommands.init` | `WorldContext.current` | client | world id/display name | client |
| `RepoCommands.init` | `RepoManager.init` | worldId, playerName, email `null` | `RepoInfo` | io |
| `RepoManager.init` | `RepoMetadataStore.load` | worldId, defaults | metadata (defaults filled) | io |
| `RepoManager.init` | `RepoService.init` | — | `RepoInfo`; creates/opens the repo | io |
| `RepoService.init` | `Git.init` / `ensureOpenLocked` | repo path, branch | JGit `Git`/`Repository` | io |
| `RepoService.init` | `configureAuthorLocked` | author name/email | `user.name`/`user.email` in JGit config | io |
| `RepoService.init` | `writeBootstrapFiles` | repo path | `README.md`, `.gitattributes` | io |
| `RepoService.init` | `stageAllLocked` / `git.commit` | message, identity | initial commit id | io |
| `RepoService.init` | `infoLocked` | — | branch, HEAD, remote, author, clean | io |
| `RepoManager.init` | `RepoMetadataStore.save` | metadata | atomic JSON write | io |
| `RepoCommands.remoteAdd` | `RepoService.remoteAdd` | name, URL | JGit remote + fetch refspec | io |
| `RepoCommands.fetch` | `RepoService.fetch` | remote, progress | `FetchSummary` | io |
| `RepoCommands.push` | `RepoService.push` | remote, force, progress | `PushSummary` | io |
| `RepoCommands.log` | `RepoService.log` | count | `List<CommitInfo>` | io |

---

## 5.13 The end-to-end data handoff

The following is the shortest summary of the data types that cross module
boundaries. It is the "spinal cord" of the implementation:

```text
World / ClientLevel / ServerLevel
   │ SnapshotService.capture
   ▼
CaptureResult { Snapshot, skippedChunks, skippedBlocks, skippedEntities, warnings }
   │ StagingService.stage -> SnapshotStore.write
   ▼
StagedSelection { Snapshot, file, stagedAt }
   │ CommitService.commit -> QuadrantCsvWriter/CommitMeta
   ▼
Map<String, byte[]> + List<String> replaced directories + commit message
   │ RepoService.commitWorkspace
   ▼
ObjectId (commit) + CommitResult
   │ RepoService.push / fetch
   ▼
Git remote
   │ RepoService.fetch + planPull
   ▼
PullPlan { remote, branch, localHead, remoteHead, mergeBase, type }
   │ PullService.prepare -> RepoService.exportSelectionAt + QuadrantCsvReader
   ▼
List<PreparedPull> { name, target Snapshot, CommitMeta, local Selection, localOrigin, localHead, tempDir }
   │ ApplyPlan.between
   ▼
ApplyPlan { placements, removals, entityPlacements, entityRemovals, createBackup }
   │ ApplyService.apply (server thread, tick batches)
   ▼
ApplyResult { placed, removed, skipped, entitiesPlaced, entitiesRemoved, backup, warnings }
```

Read-only path:

```text
world capture + staging .snap + repo HEAD tree
   │ StatusService.report/diff -> BuildDiff
   ▼
DiffCounts / DiffReport / Report -> chat
```

---

## 5.14 What is class-local (and therefore not above)

These calls stay inside one class and are intentionally omitted from the
cross-class tables, even though they matter for the detailed design:

- `Selection.normalize`, `addCuboid`, `removeCuboidAt`, `reorigin`, `volume`,
  `boundsMin/Max`, `contains`, `relative/absolute`, `byName`.
- `Snapshot.rebase`, `retain`, `byRelative`, `byAbsolute`, `dimensions`.
- `BuildDiff.between` internals: `sameBlock`, map/set comparison, sorting.
- `RepoService` internals: `ensureOpenLocked`, `stageAllLocked`, `statusLocked`,
  `safeResolve`, `deleteRecursively`, `identityLocked`,
  `isPristineInitLocked`, `listSelectionsAtLocked`, `exportSelectionAt`'s
  `TreeWalk` loop, `friendlyMessage`.
- `QuadrantCsvWriter.writeQuadrant/writeEntities/cleanPrevious`; reader's
  `readBlockFile`/`readEntityFile`/`mergeOrigin`.
- `SnapshotStore.write/read` field encoding and bounds checks.
- `ApplyService.Job.process/finish/removeEntities/placeEntities`.
- `SelectionRenderer.drawCuboid/draw`.
- `WandItem.create/isWand/give`.
- Command handlers' argument parsing and formatting.

The per-class generated docs (`Documentation/main/...`, `Documentation/client/...`)
cover those class-local details. The `Utils/decomposition-generator` can also
emit a per-file PlantUML function-call decomposition; see
[08-Diagram-Guide.md](08-Diagram-Guide.md).
