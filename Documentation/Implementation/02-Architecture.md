# 2. Architecture

## 2.1 Layers

The code is organised as a client-facing shell around a game-independent core.
The split is enforced by the Fabric source sets, not just by convention:

```
┌──────────────────────────────────────────────────────────────────────────┐
│ src/client — client-only code (never loaded on a dedicated server)       │
│                                                                          │
│  MinecraftGitClient  ──►  GitServices  ──►  commands / wand / renderer   │
│                               │                                          │
│                               ├─► client.world.ApplyService               │
│                               ├─► client.world.WorldAccess                │
│                               ├─► client.selection.SelectionManager       │
│                               └─► client.WorldContext / Messages          │
└───────────────────────────────┬──────────────────────────────────────────┘
                                │ calls down; no core class calls up
┌───────────────────────────────▼──────────────────────────────────────────┐
│ src/main — game-common code (model, persistence, Git, capture)            │
│                                                                          │
│  commands ──►  services (selection, staging, commit, status, sync,        │
│                           backup, repo)  ──►  serialization / JGit        │
│  WorldAccess ──► SnapshotService ──► Model (Selection, Snapshot, ...)     │
└──────────────────────────────────────────────────────────────────────────┘
```

**Dependency rule:** `src/main` may use common Minecraft classes (for example
`Level`, `BlockState`, `BlockEntity`, `ResourceLocation`) but never client-only
classes (`Minecraft`, `ClientLevel`, `Screen`, Fabric client events). `src/client`
may use both. Within `src/main`, the repository layer is deliberately free of
Minecraft types where possible (`RepoOptions`, `RepoInfo`, `CommitInfo`,
`RemoteInfo`, `RepoProgress`) so JGit work can be tested in a plain JVM.

The rules are easy to verify by inspection:

| Package | May depend on | Must not depend on |
|---------|---------------|--------------------|
| `model` | JDK only | Minecraft, JGit, client |
| `serialization` | Minecraft registry/NBT classes, `model` | client, repo |
| `repo` | JGit, Gson, `model` (only for `CommitMeta`), util | client, world, commands |
| `selection` | Gson, `model`, util | client, repo, world |
| `staging` | `model`, util | client, repo |
| `commit` | `repo`, `staging`, `model`, `serialization` | client, world |
| `status` | `repo`, `staging`, `model`, `serialization` | client, world |
| `sync` | `repo`, `model`, `serialization` | client, world |
| `backup` | `staging.SnapshotStore`, `model` | client |
| `world` | Minecraft level classes, `model`, `serialization` | client, repo |
| `client.command` | all of the above, client services | server-only code |
| `client.selection` | Minecraft client, `model`, `selection` | repo internals |
| `client.world` | Minecraft client + integrated server, `world`, `backup` | commands |

## 2.2 Package inventory

### `org.almond.minecraftgit` (main)

- `MinecraftGit` — mod id (`minecraftgit`), SLF4J logger, `id(path)` helper.

### `model`

- `BlockPos` — immutable integer block position, comparable, add/subtract.
- `Cuboid` — inclusive box; normalises corners; volume saturates; ordering key
  `SMALLEST_CORNER_ORDER`; `contains`, `intersects`, `union`, `expand`, `corners`.
- `Selection` — named, owned, multi-cuboid region; origin; tracked flag; timestamps;
  origin seeding/re-origin rules; `contains`, `relative`, `absolute`, `volume`,
  bounds.
- `BlockSnapshot` — one block: absolute and relative coordinates, block-state
  string, optional block-entity payload.
- `EntitySnapshot` — one entity: absolute and relative doubles, yaw/pitch, type id,
  serialized payload.
- `Snapshot` — immutable captured selection: origin, blocks, entities, capture
  time; `rebase`, `retain`, `byRelative`, `byAbsolute`, `dimensions`.
- `BuildDiff` — coordinate-level difference between two snapshots; added, changed,
  removed blocks and entity set differences; `mergeBlocks`.

### `serialization`

- `Quadrant` — enum `PP/PN/NP/NN`; maps relative X/Z to rows/columns and back.
- `CsvLine` — minimal RFC-4180 format/parse.
- `QuadrantCsvWriter` — writes the committed `<selection>/y_<y>/<quadrant>.csv`
  tree and `entities.csv`; cleans previous generated files but preserves
  `meta.json`.
- `QuadrantCsvReader` — reads the tree back into a `Snapshot`; validates headers,
  origins and entity rows.
- `SnapshotCell` — encodes/decodes `blockData|nbt|base64` cells.
- `BlockStateCodec` — block state <-> canonical `namespace:block[prop=value,...]`.
- `BlockEntityCodec` — block entity <-> `nbt:<SNBT>` payload.
- `EntityCodec` — entity <-> `nbt:<SNBT>` payload; creates entities on apply.
- `SerializationException` — corrupt/unsupported repository data.

### `repo`

- `RepoService` — one JGit repository facade, serialized by a `ReentrantLock`:
  init/open, info, commit/stage, log/HEAD, author, remotes, fetch, push, pull
  planning, fast-forward, resolve/export.
- `RepoManager` — owns per-world `RepoService`s plus a metadata cache; `init`
  adopts the local player identity; `lastSelection` persistence.
- `RepoLocator` — repo path and metadata path for a world id.
- `RepoMetadata` — local-only per-world state (branch, remote, author, last
  selection, created time).
- `RepoMetadataStore` — atomic JSON read/write of `RepoMetadata`.
- `RepoOptions` — immutable branch/default remote/author config passed into
  `RepoService`.
- `RepoInfo`, `CommitInfo`, `RemoteInfo` — read-only result records.
- `PullPlan` (+ nested `Type`) — fetch result and branch relationship.
- `RepoProgress` / `JGitProgressMonitor` — progress sink adapter.
- `RepoException` — user-facing repository error.

### `selection`

- `SelectionService` — in-memory cache keyed by `worldId|player`, backed by
  `SelectionMetadataStore`; every mutation persists immediately.
- `SelectionMetadataStore` — per-world/per-player JSON wrapper
  `{format, world, owner, ownerName, selections}`; atomic writes.
- `SelectionException` — invalid/missing selection or corrupt store.

### `staging`

- `StagingService` — durable staged snapshots at
  `<root>/<safeWorld>/<owner>/<selection>.snap`; stage/load/has/list/unstage,
  rebase after re-origin, restrict after cuboid removal.
- `SnapshotStore` — gzipped binary format with magic `MCGITSNP`, version 1,
  size bounds and atomic replace.
- `StagedSelection` — snapshot + file + staged time.
- `StagingException` — missing/corrupt/unwritable staging file.

### `commit`

- `CommitService` — staged snapshot -> temp CSV/meta tree -> `RepoService`
  workspace commit; diff counts, message, empty/unchanged rules; commit lock.
- `CommitRequest` — immutable inputs (player, world, selections, message,
  allowEmpty).
- `CommitResult` — commit id/short id, committed names, diff counts, message.
- `CommitException` — nothing staged/unchanged/changed tree errors.

### `status`

- `StatusService` — three-way report per selection (HEAD, staged, working) and
  per-selection diff report; `DiffCounts`, `SelectionStatus`, `Report`,
  `DiffReport`.
- `StatusService` has no world access; the command layer supplies the current
  snapshots.

### `sync`

- `PullService` — exports remote commit trees to temp dirs, reads them with
  `QuadrantCsvReader`, reads local HEAD, returns `PreparedPull` /
  `PreparedCheckout` records; refuses partial pulls when the remote has several
  selections; cleans temp dirs.
- `PullException` — pull/checkout/backup preconditions.

### `backup`

- `BackupService` — pre-apply snapshots at
  `<root>/<safeWorld>/<owner>/<selection>/<millis>-<seq>.snap`; latest/list;
  prune to `maxBackups`.
- `BackupException` — backup creation/list failure.

### `world`

- `SnapshotService` — reads blocks, block entities and entities from a `Level`;
  volume/entity/byte caps; unloaded-chunk accounting; `capturePositions` for
  backups.
- `ApplyPlan` — absolute placements/removals/entity changes between two
  snapshots, using relative coordinates and origins; `restore` for backups.
- `ApplyResult`, `CaptureResult` — result records with warnings.
- `ApplyException` — apply not possible (not single-player, server stopping,
  backup failure).

### `config`, `debug`, `util`

- `ModConfig` — persisted schema, load/normalize/atomic save, path helpers.
- `DebugLogger` — optional buffered debug log; per-block read/write logging.
- `Names` — selection-name regex/validation, world-id sanitisation.
- `UserFacingException` — base for safe chat errors.

### `src/client`

- `MinecraftGitClient` — client entrypoint; loads config, creates `GitServices`,
  checks JGit, registers commands/wand/renderer, closes on `CLIENT_STOPPING`.
- `GitServices` — the service graph: config, debug, messages, repos, selections,
  selection manager, snapshots, staging, commits, status, backups, pull, apply,
  and the shared 2-thread daemon IO pool; `async`/`asyncRun`.
- `WorldContext` — world id/display name/dimension/singleplayer flag; integrated
  server level lookup; game mode; current screen.
- `Messages` — chat prefix/colour/format helpers.
- `command.*` — Brigadier command tree and all command handlers.
- `selection.*` — wand item, click events, per-player session state, particle
  renderer.
- `world.*` — `WorldAccess` (choose capture thread) and `ApplyService`
  (tick-batched apply job).

## 2.3 The service graph

`GitServices` is constructed once in `MinecraftGitClient.onInitializeClient` and
is the only place where the long-lived services are wired:

```java
GitServices(root, config, debug)
  ├─ Messages(config)
  ├─ RepoManager(config, debug)
  │    ├─ RepoLocator(config.reposRoot(), config.metaRoot())
  │    └─ RepoMetadataStore(locator.metadataRoot(), debug)
  ├─ SelectionService(config)
  │    └─ SelectionMetadataStore(config.selectionsRoot())
  ├─ SelectionManager(selections, repos)
  ├─ SnapshotService(config, debug)
  ├─ StagingService(config.stagingRoot(), new SnapshotStore(), debug)
  ├─ CommitService(repos, staging, config, debug, modVersion())
  ├─ StatusService(repos, staging, debug)
  ├─ BackupService(config.backupsRoot(), config.maxBackups)
  ├─ PullService(config.tmpRoot())
  ├─ ApplyService(snapshots, backups, config, debug, io)
  └─ ExecutorService io = Executors.newFixedThreadPool(2, daemon "minecraftgit-io")
```

Properties of the graph:

- Everything is a singleton for the client session. There is no per-world service
  graph; `RepoManager` lazily creates one `RepoService` per world id.
- `GitServices` owns the only executor. `RepoService`, `CommitService` and
  `StagingService` do not create threads; they rely on callers to run IO work on
  the executor. This keeps thread creation in one place and makes shutdown simple.
- `MinecraftGitClient` closes `RepoManager` (which closes all `RepoService`s) and
  the debug writer, then shuts the executor down (2 s grace, then `shutdownNow`).
- `MinecraftGitClient.services()` throws `IllegalStateException` before
  initialization or after shutdown; commands are only registered after the graph
  exists.

## 2.4 Threading model

The mod has three execution contexts, and each piece of state has one owner.

| Context | Owns / may touch | Never does |
|---------|------------------|------------|
| **Client thread** | `Minecraft` client state, chat feedback, `ClientLevel`, `SelectionManager` sessions, `SelectionRenderer`, command parsing, the client-side capture in multiplayer, result callbacks | Blocking JGit/staging IO; writing the world |
| **Integrated server thread** | `ServerLevel`, authoritative block/entity reads and writes, `MinecraftServer.execute` jobs, apply job ticks, pre-apply capture | Chat feedback (the future result is marshalled back to the client thread) |
| **`minecraftgit-io` (2 daemon threads)** | JGit calls, `SnapshotStore` read/write, CSV tree read/write, backup file IO, temp-directory cleanup | Touching `Level` or client state |

Coordination rules:

- `CommandSupport.complete` / `completeFuture` run a task via
  `services.async(...)` and then hop back with `client.execute(...)`; if the
  player left the world (`client.player == null || client.level == null`) the
  result is silently dropped.
- `WorldAccess.capture` is the boundary that chooses the thread: in single-player
  it submits `SnapshotService.capture(selection, serverLevel, true)` to
  `server.execute(...)`; in multiplayer it runs the capture on the client thread
  (or the current thread if already on it) with `loadChunks=false`.
- `ApplyService.apply` submits `begin` to `server.execute(...)`, captures the
  backup on the server thread, writes it via `supplyAsync(..., io)`, then hops
  back to the server thread to start the write job. The job processes up to
  `config.blocksPerTick` writes and re-submits itself with `server.execute`.
- `RepoService` serializes all public calls with one `ReentrantLock` because a
  JGit `Repository` is not safe for concurrent mutation. `CommitService` adds a
  separate `commitLock` so two commits cannot interleave their read/diff/write
  sequence even if callers race.
- `DebugLogger.log` is `synchronized`; block-level logging is opt-in because a
  large capture would otherwise write millions of lines.

The detailed invariants and failure ordering are in
[07-Error-Handling-and-Concurrency.md](07-Error-Handling-and-Concurrency.md).

## 2.5 Lifecycle

1. Fabric Loader constructs `MinecraftGitClient` and calls
   `onInitializeClient()`.
2. The config root is `FabricLoader.getConfigDir()/minecraftgit`.
   `ModConfig.load` creates/normalises `config.json` and writes it back.
3. `DebugLogger` is created from the config (enabled/blocks flags, logs root).
4. `GitServices` builds the service graph and the IO executor.
5. A reflective `Class.forName("org.eclipse.jgit.api.Git")` check logs an error if
   JGit was not nested in the jar; repository commands would then fail.
6. Registration:
   - `ClientCommandRegistrationCallback` -> `GitCommand.register(dispatcher, services)`
   - `WandEvents.register(services)` (attack/use block callbacks)
   - `SelectionRenderer.register(services)` (end-client-tick particle outline)
7. On `ClientLifecycleEvents.CLIENT_STOPPING`, `services.close()` runs and the
   static reference is cleared. A later `services()` call throws.
8. There is no server entrypoint and no per-world init hook. The first command
   that needs a repository calls `RepoManager.init` (via `/git init`) or
   `RepoManager.service` (which lazily opens the per-world repository).

## 2.6 How features map onto the architecture

| Feature | Client entry | Core services | Persistence touched |
|---------|--------------|---------------|---------------------|
| Selection creation | `SelectionCommands`, `WandEvents` | `SelectionManager`, `SelectionService` | `selections/...json`, `meta/...json` (last selection) |
| Staging | `StagingCommands` | `WorldAccess`, `SnapshotService`, `StagingService` | `staging/...snap`, `selections/...json` (tracked) |
| Commit | `CommitCommands` | `CommitService`, `QuadrantCsvWriter`, `CommitMeta`, `RepoService` | temp tree, repository `.git`, staging removal |
| Status/diff | `StatusCommands` | `WorldAccess`, `StatusService`, `QuadrantCsvReader`, `BuildDiff` | repository worktree, staging |
| Pull/apply | `PullCommands` | `RepoService`, `PullService`, `ApplyPlan`, `ApplyService`, `BackupService` | remote refs, repo worktree, world, backups |
| Checkout | `PullCommands` | `RepoService`, `PullService`, `ApplyService` | repository objects, world, backups |
| Abort | `PullCommands` | `BackupService`, `ApplyPlan.restore`, `ApplyService` | backups, world |
| Remote ops | `RepoCommands` | `RepoService`, `RepoMetadataStore` | repository config, remote refs |

## 2.7 Extending the mod

- **A new command** belongs in `src/client/.../command`, attaches to the root
  literal in `GitCommand.register`, and returns `CommandSupport.run`/`runInt`
  (synchronous) or uses `complete`/`completeFuture` (IO). It must not block the
  client thread.
- **A new persistent artifact** belongs under `config/minecraftgit/` via a
  `ModConfig` path helper and must use an atomic write (write temp, move
  `ATOMIC_MOVE`, fallback replace) as the existing stores do.
- **A new commit-tree file** should go through `QuadrantCsvWriter`/`CommitMeta`
  rather than writing into `RepoService` directly, so the format stays in one
  place.
- **A new service** should be constructed in `GitServices` and exposed by a
  getter; it receives its collaborators through the constructor.
- **A new thread is normally wrong.** Use `GitServices.async`/`asyncRun` for IO
  and `MinecraftServer.execute`/`Minecraft.execute` for thread hops.

## 2.8 Architectural trade-offs

- **Two source sets** add boilerplate (a service is often referenced from both)
  but guarantee that client-only classes never load on a dedicated server.
- **JGit facade with a single lock** is simple and safe but serialises all
  repository work for one world; the 2-thread IO pool means two worlds can still
  make progress while one repository is busy.
- **Durable staging instead of an in-memory index** costs disk and a gzip
  round-trip but survives restarts and matches the "Git index" mental model.
- **No merge in v1** keeps the apply path deterministic; diverged histories are
  reported rather than silently merged.
- **Fast-forward after apply** means a crash between apply and `reset --hard`
  leaves the world changed but the branch unmoved. The backup/abort command is
  the recovery path; the branch is not advanced on failure.
