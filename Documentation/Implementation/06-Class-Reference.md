# 6. Class Reference

One entry per production class. For the fuller per-class auto-generated API and
SDD pages, follow the `Docs` link where one exists. Entries are grouped by source
set and package; the order matches the architecture.

---

## 6.1 Root

### `MinecraftGit` — [source](../../src/main/java/org/almond/minecraftgit/MinecraftGit.java) · [docs](../main/java/org/almond/minecraftgit/MinecraftGit.md)

Shared mod identity for both source sets. Holds `MOD_ID = "minecraftgit"`,
`LOGGER` (SLF4J) and `id(path)` -> `ResourceLocation`. Deliberately lives in
`src/main` so repository/serialization code can refer to the mod id without
loading client classes.

---

## 6.2 `model` — pure build data

### `BlockPos` — [source](../../src/main/java/org/almond/minecraftgit/model/BlockPos.java)

Immutable integer world position (`record`), comparable by `(x, y, z)`.
`ORIGIN = (0,0,0)`; `add`/`subtract`/`relativeTo`/`absoluteFrom`; `toString` is
`"x, y, z"`. Used for both absolute and relative coordinates; it does not know
which one it holds.

### `Cuboid` — [source](../../src/main/java/org/almond/minecraftgit/model/Cuboid.java)

Inclusive box (`record` with `minX..maxX`, etc.). The canonical constructor
normalises the corners. `SMALLEST_CORNER_ORDER` is the selection-sort key.
Methods: `of`, `smallestCorner`, `largestCorner`, `sizeX/Y/Z`, saturated
`volume`, `contains`, `intersects`, `union`, `expand`, `corners`, `dimensions`,
`toString`. Used by selections, capture, apply plans.

### `Selection` — [source](../../src/main/java/org/almond/minecraftgit/model/Selection.java) · [docs](../main/java/org/almond/minecraftgit/model/Selection.md)

Named, player-owned, multi-cuboid region with an origin and a tracked flag.
`normalize` repairs loaded data; `addCuboid` seeds the origin and sorts;
`removeCuboidAt` leaves the origin in place (E2); `reorigin` moves it to the
smallest corner (E3); `volume` saturates; `contains`, `relative`, `absolute`,
`boundsMin/Max`, `byName`, `updatedAt`/`touch`. Persisted by
`SelectionService`; never committed.

### `BlockSnapshot` — [source](../../src/main/java/org/almond/minecraftgit/model/BlockSnapshot.java)

One captured block: absolute `(x,y,z)`, relative `(rx,ry,rz)`, canonical
`blockData` string, optional `blockEntity` payload. `at(absolute, origin, ...)`
computes relative coordinates; `ORDER` sorts by `(y, x, z)`; `isAir`,
`hasBlockEntity`.

### `EntitySnapshot` — [source](../../src/main/java/org/almond/minecraftgit/model/EntitySnapshot.java)

One captured non-player entity: absolute and relative doubles, yaw/pitch,
registry `type`, serialized `data` (`nbt:<SNBT>`). `ORDER` sorts by `(y, x, z)`;
`relativeBlock` floors relative coordinates. Entity identity for diffing is the
whole record (type + position + rotation + payload).

### `Snapshot` — [source](../../src/main/java/org/almond/minecraftgit/model/Snapshot.java) · [docs](../main/java/org/almond/minecraftgit/model/Snapshot.md)

Immutable capture: selection name, origin, sorted blocks/entities, capture time.
`empty`; `rebase(newOrigin)`; `retain(selection)`; `byRelative`/`byAbsolute`;
`blockCount`/`entityCount`/`isEmpty`; `dimensions`; `volume` (block count).
The central data type between capture, staging, commit, diff and apply.

### `BuildDiff` — [source](../../src/main/java/org/almond/minecraftgit/model/BuildDiff.java) · [docs](../main/java/org/almond/minecraftgit/model/BuildDiff.md)

Coordinate-level diff between two snapshots by relative coordinate: `added`,
`changed`, `removed`, `entitiesAdded/Removed`, `unchanged`; `between`, `mergeBlocks`,
`totalBlockChanges`, `totalEntityChanges`, `isEmpty`, `summary`. Same-block means
equal `blockData` and equal `blockEntity`; final state wins (no three-way merge).

---

## 6.3 `serialization` — repository text and codecs

### `Quadrant` — [source](../../src/main/java/org/almond/minecraftgit/serialization/Quadrant.java) · [docs](../main/java/org/almond/minecraftgit/serialization/Quadrant.md)

`PP`, `PN`, `NP`, `NN`; maps relative X/Z to row/column indices and back
(`rowToX`, `columnToZ`, `rowForX`, `columnForZ`), `fromOffsets`, `fromCode`.
The boundary coordinate 0 belongs to the positive quadrants; negative rows start
at `-1`.

### `CsvLine` — [source](../../src/main/java/org/almond/minecraftgit/serialization/CsvLine.java) · [docs](../main/java/org/almond/minecraftgit/serialization/CsvLine.md)

Minimal RFC-4180 `format`/`escape`/`parse`. Quotes fields containing `,`, `"`,
`\n` or `\r`; doubles quotes. Block-state strings contain commas, so this is not
a `split(",")`.

### `QuadrantCsvWriter` — [source](../../src/main/java/org/almond/minecraftgit/serialization/QuadrantCsvWriter.java) · [docs](../main/java/org/almond/minecraftgit/serialization/QuadrantCsvWriter.md)

Writes a `Snapshot` into the committed tree: group blocks by relative Y and
quadrant, one CSV per non-empty quadrant, header with origin/Y/quadrant, rows X
and columns Z; writes `entities.csv` when entities exist; `cleanPrevious`
removes old generated CSVs/Y dirs but preserves `meta.json`. Empty cells mean
untracked; `minecraft:air` is a tracked cell.

### `QuadrantCsvReader` — [source](../../src/main/java/org/almond/minecraftgit/serialization/QuadrantCsvReader.java) · [docs](../main/java/org/almond/minecraftgit/serialization/QuadrantCsvReader.md)

Reads a selection directory back into a `Snapshot`: validates every CSV header
(`minecraftgit-csv v1 ...`) and the entities header, decodes cells
(`SnapshotCell`) and entity rows, and rejects mismatched origins across files.
Missing directory -> empty snapshot at the fallback origin.

### `SnapshotCell` — [source](../../src/main/java/org/almond/minecraftgit/serialization/SnapshotCell.java) · [docs](../main/java/org/almond/minecraftgit/serialization/SnapshotCell.md)

Encodes a CSV cell as `blockData` or `blockData|nbt|<base64>`; `decode` splits
on the first `|nbt|` and Base64-decodes the payload. Keeps commas/quotes/newlines
out of the CSV layer.

### `BlockStateCodec` — [source](../../src/main/java/org/almond/minecraftgit/serialization/BlockStateCodec.java) · [docs](../main/java/org/almond/minecraftgit/serialization/BlockStateCodec.md)

`BlockState` <-> `namespace:block[prop=value,...]`; properties sorted by name for
reproducibility; unknown block/property decodes to air/default (`decode`),
`isKnown` checks the registry, `isAir` checks the decoded state. Registry access
is through `BuiltInRegistries.BLOCK`.

### `BlockEntityCodec` — [source](../../src/main/java/org/almond/minecraftgit/serialization/BlockEntityCodec.java) · [docs](../main/java/org/almond/minecraftgit/serialization/BlockEntityCodec.md)

`BlockEntity` -> `nbt:<SNBT>` using `saveWithFullMetadata`
(`encode`); `apply` parses the `nbt:` payload and calls
`loadWithComponents`, returning false for plugin JSON/unknown payloads;
`payloadBytes` for the byte cap. `isNative` tests the prefix.

### `EntityCodec` — [source](../../src/main/java/org/almond/minecraftgit/serialization/EntityCodec.java) · [docs](../main/java/org/almond/minecraftgit/serialization/EntityCodec.md)

`Entity` -> `nbt:<SNBT>` (`saveWithoutId`); `apply(ServerLevel, snapshot, x, y,
z)` parses SNBT, writes the `id`, `EntityType.loadEntityRecursive`, sets
position/yaw/pitch, adds the entity; returns null when unloadable;
`payloadBytes`. Position/rotation live in `EntitySnapshot`, not in the NBT.

### `SerializationException` — [source](../../src/main/java/org/almond/minecraftgit/serialization/SerializationException.java) · [docs](../main/java/org/almond/minecraftgit/serialization/SerializationException.md)

Unchecked exception for corrupt/unrecognized repository data, invalid Base64,
invalid NBT, mismatched CSV origins. Extends `RuntimeException` (not
`UserFacingException`), so callers often wrap it in a user-facing error.

---

## 6.4 `repo` — JGit facade and local metadata

### `RepoService` — [source](../../src/main/java/org/almond/minecraftgit/repo/RepoService.java) · [docs](../main/java/org/almond/minecraftgit/repo/RepoService.md)

The JGit facade for one repository, serialized by a `ReentrantLock`. Lifecycle:
`init`, `open`, `close`, `isInitialized`, `path`. Read: `info`,
`currentBranch`, `headId`, `headCommit`, `log`. Write: `commitAll`,
`commitEmpty`, `commitWorkspace` (delete/write directories + files, stage,
commit, path-escape check), `setAuthor`. Remotes/transport: `remoteAdd`,
`remoteRemove`, `remoteList`, `remote`, `fetch`, `push`. Pull: `planPull`
(fetch + classify + pristine-init special case), `fastForward` (`reset --hard`).
History/export: `resolve`, `headObject`, `listSelectionsAt`,
`exportSelectionAt` (TreeWalk, path checks). Errors are translated by
`error`/`friendlyMessage` into `RepoException`. Nested records `PushSummary` and
`FetchSummary`.

### `RepoManager` — [source](../../src/main/java/org/almond/minecraftgit/repo/RepoManager.java) · [docs](../main/java/org/almond/minecraftgit/repo/RepoManager.md)

Owns per-world `RepoService`s (`ConcurrentHashMap`) and `RepoMetadata` cache.
`repoPath`, `isInitialized`, `metadata`, `saveMetadata`, `service`, `init`
(adopts the player identity, creates/opens, persists metadata), `setAuthor`,
`lastSelection`/`setLastSelection`, `reload`, `close`.

### `RepoLocator` — [source](../../src/main/java/org/almond/minecraftgit/repo/RepoLocator.java) · [docs](../main/java/org/almond/minecraftgit/repo/RepoLocator.md)

Pure path policy: `repoRoot`, `metadataRoot`, `repoPath(worldId)`,
`metadataPath(worldId)` using `Names.safeWorldName`. Repositories live outside
the world save folder.

### `RepoMetadata` — [source](../../src/main/java/org/almond/minecraftgit/repo/RepoMetadata.java) · [docs](../main/java/org/almond/minecraftgit/repo/RepoMetadata.md)

Local-only per-world state: world, branch, remote name/url, author name/email,
last selection, created time. `defaults(world, options)` and `normalize` repair
missing fields; getters/setters for the store and manager.

### `RepoMetadataStore` — [source](../../src/main/java/org/almond/minecraftgit/repo/RepoMetadataStore.java) · [docs](../main/java/org/almond/minecraftgit/repo/RepoMetadataStore.md)

Reads/writes `meta/<world>.json` with Gson. `load` returns defaults when missing,
normalises, and throws `RepoException` for invalid JSON/IO; `save` writes a temp
file then atomically replaces; `file(world)`.

### `RepoOptions` — [source](../../src/main/java/org/almond/minecraftgit/repo/RepoOptions.java)

Immutable constructor input for `RepoService`: `branch`, `defaultRemote`,
`authorName`, `authorEmail`, with blank-to-default normalisation. No Minecraft
types, so the repo layer is testable in a plain JVM.

### `RepoInfo` — [source](../../src/main/java/org/almond/minecraftgit/repo/RepoInfo.java)

Read-only snapshot for `/git repo`: path, branch, head id/summary, remote
name/url, author, `hasCommits`, `clean`.

### `CommitInfo` — [source](../../src/main/java/org/almond/minecraftgit/repo/CommitInfo.java)

One `/git log` row: full id, short id, author name/email, `Instant`, first
message line.

### `RemoteInfo` — [source](../../src/main/java/org/almond/minecraftgit/repo/RemoteInfo.java)

Remote name + URL, returned by `RepoService.remoteList`.

### `PullPlan` — [source](../../src/main/java/org/almond/minecraftgit/repo/PullPlan.java) · [docs](../main/java/org/almond/minecraftgit/repo/Type.md)

Fetch result + relationship for a pull: `remote`, `branch`, `localHead`,
`remoteHead`, `mergeBase`, `type` (`UP_TO_DATE`, `FAST_FORWARD`, `DIVERGED`),
`fetchedRefs`; `canFastForward()`. The nested `Type` enum is the `Type.md` doc.

### `RepoProgress` / `JGitProgressMonitor` — [source](../../src/main/java/org/almond/minecraftgit/repo/RepoProgress.java) · [docs](../main/java/org/almond/minecraftgit/repo/RepoProgress.md)

`RepoProgress` is a transport progress sink (`update(task, completed, total)`,
`done`, `NOOP`). `JGitProgressMonitor` adapts it to JGit's `ProgressMonitor`
(worker-thread callbacks). Commands currently pass `NOOP`; the interface is the
hook for a future progress UI.

### `RepoException` — [source](../../src/main/java/org/almond/minecraftgit/repo/RepoException.java) · [docs](../main/java/org/almond/minecraftgit/repo/RepoException.md)

User-facing repository/transport failure (extends `UserFacingException`).

---

## 6.5 `selection` — durable selections

### `SelectionService` — [source](../../src/main/java/org/almond/minecraftgit/selection/SelectionService.java) · [docs](../main/java/org/almond/minecraftgit/selection/SelectionService.md)

Per-world/per-player selection cache (`Map<"world|uuid", List<Selection>>`) over
`SelectionMetadataStore`. `list`, `listAll`, `find`, `get`, `create`,
`createAt`, `addCuboid` (count/volume caps), `removeCuboid`, `reorigin`,
`delete`, `setTracked`, `save`, `tracked`, `invalidate`. Every mutation persists
immediately; volumes saturate; names are validated via `Names`.

### `SelectionMetadataStore` — [source](../../src/main/java/org/almond/minecraftgit/selection/SelectionMetadataStore.java) · [docs](../main/java/org/almond/minecraftgit/selection/SelectionMetadataStore.md)

JSON store at `selections/<safeWorld>/<uuid>.json`, wrapper
`{format, world, owner, ownerName, selections}`. `load`, `loadFile` (repairs via
`Selection.normalize`), `loadAll` (all players in a world), `save` (atomic),
`file`. Corrupt JSON -> `SelectionException` with a delete/reset hint.

### `SelectionException` — [source](../../src/main/java/org/almond/minecraftgit/selection/SelectionException.java) · [docs](../main/java/org/almond/minecraftgit/selection/SelectionException.md)

User-facing selection error (missing, duplicate, limit, no cuboids/origin).

---

## 6.6 `staging` — durable local index

### `StagingService` — [source](../../src/main/java/org/almond/minecraftgit/staging/StagingService.java) · [docs](../main/java/org/almond/minecraftgit/staging/StagingService.md)

Durable staging at `staging/<safeWorld>/<owner>/<selection>.snap`. `file`,
`stage` (normalise/rebase, write), `load` (read + mtime), `hasStaged`,
`stagedNames`, `unstage`, `rebaseStaged`, `restrictStaged` (retain + rebase after
cuboid removal), `deleteAll`, `store`. Survives restarts.

### `SnapshotStore` — [source](../../src/main/java/org/almond/minecraftgit/staging/SnapshotStore.java) · [docs](../main/java/org/almond/minecraftgit/staging/SnapshotStore.md)

Gzipped binary `Snapshot` persistence with magic `MCGITSNP`, version 1, length
bounds, atomic replace. `write(Path, Snapshot)` and `read(Path)`; throws
`StagingException` for missing/corrupt/truncated files. Also used by backups.

### `StagedSelection` — [source](../../src/main/java/org/almond/minecraftgit/staging/StagedSelection.java)

Record: `snapshot`, `file`, `stagedAt`; convenience `selectionName`,
`blockCount`, `entityCount`.

### `StagingException` — [source](../../src/main/java/org/almond/minecraftgit/staging/StagingException.java) · [docs](../main/java/org/almond/minecraftgit/staging/StagingException.md)

User-facing staging error (no origin/owner, corrupt/unwritable file).

---

## 6.7 `commit` — staged snapshot to Git

### `CommitService` — [source](../../src/main/java/org/almond/minecraftgit/commit/CommitService.java) · [docs](../main/java/org/almond/minecraftgit/commit/CommitService.md)

`commit(CommitRequest)` guarded by `commitLock`: require repo; for each staged
selection load the snapshot, read old HEAD (meta + CSV), diff, skip unchanged,
build `CommitMeta`, write temp CSV/meta tree, collect bytes; reject empty unless
allowed; build message; `RepoService.commitWorkspace`; unstage; return
`CommitResult`. Helpers: `buildMeta`, `commitMessage`, `collectFiles`,
`deleteRecursively`.

### `CommitRequest` — [source](../../src/main/java/org/almond/minecraftgit/commit/CommitRequest.java)

Immutable command input: player name/uuid, world id, game mode, selected
`List<Selection>`, message, `allowEmpty`.

### `CommitResult` — [source](../../src/main/java/org/almond/minecraftgit/commit/CommitResult.java)

Commit id/short id, committed selection names, added/changed/removed/entity
counts, full message.

### `CommitException` — [source](../../src/main/java/org/almond/minecraftgit/commit/CommitException.java) · [docs](../main/java/org/almond/minecraftgit/commit/CommitException.md)

User-facing commit error (nothing staged, selection not staged, unchanged).

---

## 6.8 `status` — three-way comparison

### `StatusService` — [source](../../src/main/java/org/almond/minecraftgit/status/StatusService.java) · [docs](../main/java/org/almond/minecraftgit/status/StatusService.md)

`report(worldId, owner, selections, currentSnapshots, tooLarge, incomplete)` ->
`Report`; `diff(...)` -> `DiffReport`. Internal `statusOf`, `loadStaged`,
`readHead`; records `DiffCounts`, `SelectionStatus`, `Report`, `DiffReport`.
Compares HEAD (repo worktree), staged `.snap`, and the captured current world.
No world access itself.

---

## 6.9 `sync` — reading remote trees for apply

### `PullService` — [source](../../src/main/java/org/almond/minecraftgit/sync/PullService.java) · [docs](../main/java/org/almond/minecraftgit/sync/PullService.md)

`prepare(worldId, localSelections, playerOrigin, repo, plan, onlySelection)`:
list remote selection names, refuse partial multi-selection pulls, export each
tree to a temp dir, read `CommitMeta`/target snapshot, find local selection and
HEAD snapshot -> `List<PreparedPull>`. `prepareCheckout(selection, repo, commit)`
-> `PreparedCheckout`. `cleanup(Path)` / `cleanup(List)` delete temp dirs.
`shortId` helper.

### `PullException` — [source](../../src/main/java/org/almond/minecraftgit/sync/PullException.java) · [docs](../main/java/org/almond/minecraftgit/sync/PullException.md)

User-facing pull/checkout error.

---

## 6.10 `backup` — pre-apply snapshots

### `BackupService` — [source](../../src/main/java/org/almond/minecraftgit/backup/BackupService.java) · [docs](../main/java/org/almond/minecraftgit/backup/BackupService.md)

`store(world, owner, selection, snapshot)` -> file path (writes then prunes to
`maxBackups`); `latest`, `list`, `directory`; `Backup` record
(`snapshot`, `file`). Naming is `millis-sequence.snap`, so lexicographic order is
chronological.

### `BackupException` — [source](../../src/main/java/org/almond/minecraftgit/backup/BackupException.java) · [docs](../main/java/org/almond/minecraftgit/backup/BackupException.md)

User-facing backup error.

---

## 6.11 `config` and `debug`

### `ModConfig` — [source](../../src/main/java/org/almond/minecraftgit/config/ModConfig.java) · [docs](../main/java/org/almond/minecraftgit/config/ModConfig.md)

Persisted schema (`config.json`) with all limits/toggles; `load` (create,
normalise, save), `normalize`, `save` (atomic), and path helpers (`reposRoot`,
`metaRoot`, `selectionsRoot`, `stagingRoot`, `backupsRoot`, `tmpRoot`,
`logsRoot`). Fields are public because Gson serialises them directly.

### `DebugLogger` — [source](../../src/main/java/org/almond/minecraftgit/debug/DebugLogger.java) · [docs](../main/java/org/almond/minecraftgit/debug/DebugLogger.md)

Optional buffered log at `logs/debug-<millis>.log`; `log(category, format, args)`
is `synchronized`; `setEnabled`/`setBlocks`; `blockRead`/`blockWrite` gated by
the blocks flag; `close` flushes. A write failure is reported once and ignored.

---

## 6.12 `util`

### `Names` — [source](../../src/main/java/org/almond/minecraftgit/util/Names.java) · [docs](../main/java/org/almond/minecraftgit/util/Names.md)

`SELECTION = [A-Za-z0-9][A-Za-z0-9_-]{0,31}`; `isValidSelectionName`,
`requireSelectionName`, `safeWorldName` (replaces disallowed characters with
`_`), `lower`, `shorten`. Selection names become directory names, so they are
validated instead of sanitised.

### `UserFacingException` — [source](../../src/main/java/org/almond/minecraftgit/util/UserFacingException.java) · [docs](../main/java/org/almond/minecraftgit/util/UserFacingException.md)

Base for failures that are safe to show in chat; `CommandSupport` prints these
without the "see the game log" suffix.

---

## 6.13 `world` — capture and apply plan

### `SnapshotService` — [source](../../src/main/java/org/almond/minecraftgit/world/SnapshotService.java) · [docs](../main/java/org/almond/minecraftgit/world/SnapshotService.md)

`capture(selection, level, loadChunks)`: validate origin/cuboids/volume; iterate
cuboids, skip out-of-height and unloaded chunks (optionally load on a
`ServerLevel`), de-duplicate positions, encode block states/block entities,
capture non-player entities in AABBs, apply entity cap, return `CaptureResult`
with warnings/skips. `capturePositions(level, name, origin, positions,
loadChunks)` for backups (blocks + block entities, no entities). Must run on the
owning thread.

### `ApplyPlan` — [source](../../src/main/java/org/almond/minecraftgit/world/ApplyPlan.java)

Record describing the absolute writes to turn one snapshot into another:
`selectionName`, `owner`, `targetOrigin`, `oldEntityOrigin`, `placements`
(`BlockPos -> BlockSnapshot`), `removals`, `entityRemovals`,
`entityPlacements`, `createBackup`. `between(...)` uses relative coordinates;
`restore(...)` for backups; `allPositions()` for the backup capture;
`EntityPlacement` record.

### `ApplyResult` — [source](../../src/main/java/org/almond/minecraftgit/world/ApplyResult.java)

`placed`, `removed`, `skipped`, `entitiesPlaced`, `entitiesRemoved`, `backup`
path, `warnings`; `changed()`, `summary()`.

### `CaptureResult` — [source](../../src/main/java/org/almond/minecraftgit/world/CaptureResult.java)

`snapshot`, skipped chunks/blocks/entities, warnings; `partial()`, `summary()`.

### `ApplyException` — [source](../../src/main/java/org/almond/minecraftgit/world/ApplyException.java) · [docs](../main/java/org/almond/minecraftgit/world/ApplyException.md)

User-facing apply error (not single-player, server stopping, backup failure).

---

## 6.14 `src/client` — client shell

### `MinecraftGitClient` — [source](../../src/client/java/org/almond/minecraftgit/client/MinecraftGitClient.java) · [docs](../client/java/org/almond/minecraftgit/client/MinecraftGitClient.md)

Client entrypoint: config root, `ModConfig.load`, `DebugLogger`, `GitServices`,
JGit presence check, register commands/wand/renderer, close on
`CLIENT_STOPPING`; static `services()` accessor (throws before init/after close).

### `GitServices` — [source](../../src/client/java/org/almond/minecraftgit/client/GitServices.java) · [docs](../client/java/org/almond/minecraftgit/client/GitServices.md)

The service graph and shared IO executor (2 daemon threads). Getters for every
service; `async`/`asyncRun`; `modVersion()` from Fabric Loader (falls back to
`"development"` in plain JVM tests); `close()` (repos, debug, executor).

### `WorldContext` — [source](../../src/client/java/org/almond/minecraftgit/client/WorldContext.java)

Record of `id`, `displayName`, `dimension`, `singleplayer`; `current(Minecraft)`
builds the world id (safe world name + dimension; multiplayer also appends the
server address), `serverLevel`, `gameMode`, `currentScreen`.

### `Messages` — [source](../../src/client/java/org/almond/minecraftgit/client/Messages.java) · [docs](../client/java/org/almond/minecraftgit/client/Messages.md)

Chat formatting: `send`, `success`, `warn`, `error`, `raw`, `format`, with the
configured prefix and colour.

### `CommandSupport` — [source](../../src/client/java/org/almond/minecraftgit/client/command/CommandSupport.java) · [docs](../client/java/org/almond/minecraftgit/client/command/CommandSupport.md)

Shared command plumbing: `requirePlayer`, `run`/`runInt` (catch -> chat),
`complete` (IO task + client-thread callback + player-left drop), `completeFuture`,
`handleError` (user-facing vs generic + log), `unwrap`, `join`, `lower`.

### `GitCommand` — [source](../../src/client/java/org/almond/minecraftgit/client/command/GitCommand.java) · [docs](../client/java/org/almond/minecraftgit/client/command/GitCommand.md)

Builds the root `/git` literal, help/version/config show/debug/blocks, and
attaches all subcommand groups. `register(dispatcher, services)`.

### `RepoCommands` — [source](../../src/client/java/org/almond/minecraftgit/client/command/RepoCommands.java) · [docs](../client/java/org/almond/minecraftgit/client/command/RepoCommands.md)

`/git init`, `repo`, `remote add/remove/list`, `log`, `push [--force]`,
`fetch [remote]`, plus `config author`; all repository/transport work via
`RepoService` on the IO executor.

### `SelectionCommands` — [source](../../src/client/java/org/almond/minecraftgit/client/command/SelectionCommands.java) · [docs](../client/java/org/almond/minecraftgit/client/command/SelectionCommands.md)

`/git selection create/list/select/show/add/remove/reorigin/delete/info` and
`wand`/`pos1`/`pos2`; owns the pending-cuboid and active-selection command UX.

### `StagingCommands` — [source](../../src/client/java/org/almond/minecraftgit/client/command/StagingCommands.java) · [docs](../client/java/org/almond/minecraftgit/client/command/StagingCommands.md)

`/git add` (capture -> stage -> track -> activate) and `/git rm` (unstage ->
untrack).

### `CommitCommands` — [source](../../src/client/java/org/almond/minecraftgit/client/command/CommitCommands.java) · [docs](../client/java/org/almond/minecraftgit/client/command/CommitCommands.md)

`/git commit [message]` and `/git commit --empty`; discovers staged selections,
builds `CommitRequest`, calls `CommitService`, prints the result.

### `StatusCommands` — [source](../../src/client/java/org/almond/minecraftgit/client/command/StatusCommands.java) · [docs](../client/java/org/almond/minecraftgit/client/command/StatusCommands.md)

`/git status` and `/git diff [name]`; captures candidates (respecting the status
volume cap), calls `StatusService`, prints report/diff samples.

### `PullCommands` — [source](../../src/client/java/org/almond/minecraftgit/client/command/PullCommands.java) · [docs](../client/java/org/almond/minecraftgit/client/command/PullCommands.md)

`/git pull [name] [--remote r]`, `/git pull --abort [name]`,
`/git checkout <commit> [name]`; orchestrates plan -> prepare -> ensure selection
-> apply each -> fast-forward (last), with cleanup on failure.

### `SelectionManager` — [source](../../src/client/java/org/almond/minecraftgit/client/selection/SelectionManager.java) · [docs](../client/java/org/almond/minecraftgit/client/selection/SelectionManager.md)

Per-player wand session (`Session` with `cornerA`, `cornerB`, `pending`), active
selection lookup/persistence via `RepoManager.lastSelection`, and pending
cuboid take/peek/set/clear.

### `WandItem` — [source](../../src/client/java/org/almond/minecraftgit/client/selection/WandItem.java) · [docs](../client/java/org/almond/minecraftgit/client/selection/WandItem.md)

Creates the custom-data wand (`minecraftgit_wand`), tests it (or the
sneak+configured-item fallback), and gives it through the integrated server's
player inventory. `configuredItem` falls back to `wooden_shovel` for invalid ids.

### `WandEvents` — [source](../../src/client/java/org/almond/minecraftgit/client/selection/WandEvents.java) · [docs](../client/java/org/almond/minecraftgit/client/selection/WandEvents.md)

Registers attack/use block callbacks; filters to client world/local player/main
hand/wand; routes corners to `SelectionManager`; completes a cuboid into the
active selection or keeps it pending; returns `InteractionResult.FAIL` to cancel
the vanilla click.

### `SelectionRenderer` — [source](../../src/client/java/org/almond/minecraftgit/client/selection/SelectionRenderer.java) · [docs](../client/java/org/almond/minecraftgit/client/selection/SelectionRenderer.md)

End-client-tick particle outline for the active selection, pending cuboid and
temporary `/git selection show` highlight; every 10 ticks, shared
`renderBudget`, `renderSelections` toggle, `ParticleTypes.END_ROD` at corners.

### `ApplyService` — [source](../../src/client/java/org/almond/minecraftgit/client/world/ApplyService.java) · [docs](../client/java/org/almond/minecraftgit/client/world/ApplyService.md)

Single-player apply on the integrated-server thread: optional pre-apply backup
(`SnapshotService.capturePositions` + `BackupService.store`), convert plan to
writes, sort by `(y,x,z)`, process `config.blocksPerTick` per tick, apply
block-entity NBT, then remove/place entities; complete `ApplyResult`. Nested
`Job`, `BlockWrite`.

### `WorldAccess` — [source](../../src/client/java/org/almond/minecraftgit/client/world/WorldAccess.java) · [docs](../client/java/org/almond/minecraftgit/client/world/WorldAccess.md)

Chooses the capture thread: single-player -> integrated-server
`server.execute(...SnapshotService.capture(..., true))`; multiplayer -> client
thread with `loadChunks=false`. `capturePositions` for backups has the same
split. Fails the future with `UserFacingException` when no level/server.

---

## 6.15 Tests

The pure-JVM test suite mirrors the layers above under `src/test`:
model (`CuboidTest`, `SelectionTest`, `BuildDiffTest`), serialization
(`CsvLineTest`, `QuadrantTest`, `QuadrantCsvTest`, `BlockStateCodecTest`,
`CommitMetaTest`), repository (`RepoServiceTest`), staging
(`SnapshotStoreTest`, `StagingServiceTest`), commit/status/apply
(`CommitServiceTest`, `StatusServiceTest`, `ApplyPlanTest`, `BackupServiceTest`,
`PullServiceTest`, `SelectionMetadataStoreTest`). See
[`../Testing.md`](../Testing.md) for the current counts and how to run them.
