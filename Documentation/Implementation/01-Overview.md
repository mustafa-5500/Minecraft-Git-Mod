# 1. System Overview

## 1.1 What Minecraft Git is

Minecraft Git is a **client-side Fabric mod** for Minecraft **1.21** that treats a
Minecraft build as a Git repository. A player selects regions of a world with a
wand (or the `pos1`/`pos2` commands), captures the blocks and entities inside
them, stages the capture, and commits it to a real Git repository. The same
repository can be pushed to, pulled from, and fast-forwarded in another
single-player world.

The mod is the client-side counterpart of the Paper
[Minecraft Git Plugin](https://github.com/mustafa-5500/Minecraft-Git-Plugin):
both use the same repository tree (`<selection>/y_<y>/<quadrant>.csv`,
`<selection>/entities.csv`, `<selection>/meta.json`), so builds can move between
the mod and the plugin. The format itself is specified in
[`../Git-Format.md`](../Git-Format.md).

There is **no server plugin and no server entrypoint**. `fabric.mod.json` marks
the mod `"environment": "client"` and registers exactly one entrypoint,
`org.almond.minecraftgit.client.MinecraftGitClient`. Everything the mod does is
either local client work, JGit file/network work, or work executed through the
integrated server that owns a single-player world.

## 1.2 What a user can do

| Capability | Commands | Implementation entry points |
|------------|----------|-----------------------------|
| Create/reopen a per-world repository | `/git init`, `/git repo` | `RepoCommands` -> `RepoManager` -> `RepoService` |
| Configure identity, debug logging, effective settings | `/git config ...` | `GitCommand`, `RepoCommands`, `ModConfig`, `DebugLogger` |
| Select builds with a wand or from the targeted block | `/git wand`, `/git selection wand/pos1/pos2`, click events | `WandItem`, `WandEvents`, `SelectionCommands`, `SelectionManager` |
| Manage named multi-cuboid selections | `/git selection create/list/select/show/add/remove/reorigin/delete/info` | `SelectionCommands` -> `SelectionService` |
| Capture and stage a selection | `/git add`, `/git rm` | `StagingCommands` -> `WorldAccess` -> `SnapshotService` -> `StagingService` |
| Commit staged selections | `/git commit`, `/git commit --empty` | `CommitCommands` -> `CommitService` -> `RepoService` |
| Compare HEAD, staging and the world | `/git status`, `/git diff` | `StatusCommands` -> `WorldAccess` -> `StatusService` |
| Work with remotes and history | `/git remote ...`, `/git fetch`, `/git push`, `/git log` | `RepoCommands` -> `RepoService` |
| Pull a build into a single-player world | `/git pull [name] [--remote r]` | `PullCommands` -> `RepoService` -> `PullService` -> `ApplyService` |
| Undo the last apply | `/git pull --abort [name]` | `PullCommands` -> `BackupService` -> `ApplyPlan.restore` -> `ApplyService` |
| Apply an old commit without moving the branch | `/git checkout <commit> [name]` | `PullCommands` -> `RepoService` -> `PullService` -> `ApplyService` |

The full command list, with argument syntax, is in [`../../README.md`](../../README.md).

## 1.3 Runtime topology

```
Player
  │  /git ... or wand clicks
  ▼
Minecraft client process
  ├─ client thread        commands, wand/render events, client-thread callbacks
  ├─ integrated server    authoritative world in single-player; capture/apply on its thread
  └─ minecraftgit-io      2 daemon threads: JGit, staging files, CSV/backup IO
        │
        ├─ config/minecraftgit/  local data (repos, metadata, selections, staging, backups, logs, tmp)
        └─ Git remote            local path or HTTP(S) URL, reached through bundled JGit
```

The mod never runs on a dedicated server. In multiplayer the mod can still
capture client-loaded chunks, commit, fetch, push and browse history, but it
refuses to write to the server's world; apply operations require the integrated
server that a single-player world provides.

## 1.4 The core data flow

All player-visible features are variations of one pipeline:

```
world blocks/entities
        │  SnapshotService.capture (client or integrated-server thread)
        ▼
   Snapshot (relative coordinates, origin)
        │  StagingService.stage (gzipped binary under staging/)
        ▼
 StagedSelection on disk
        │  CommitService.commit (on the IO executor)
        ▼
 Git commit tree: CSV + entities.csv + meta.json
        │  RepoService.push / fetch / fastForward (JGit)
        ▼
 Git remote <──────────────► another world's repository
        │  PullService.prepare + ApplyPlan.between (IO executor)
        ▼
 ApplyService.apply (integrated-server thread, tick-batched)
        │
        └─ BackupService wrote the pre-apply snapshot first
```

Status and diff are the read-only counterpart: they capture the world, read HEAD
from the repository worktree, read the staged snapshot, and compare all three with
`BuildDiff`.

## 1.5 Design constraints that explain the implementation

These constraints are deliberate. Most of the unusual choices in the code follow
from them.

1. **Client-only.** There is no server component to register blocks, items or
   commands. The wand is a vanilla item with custom data; in multiplayer the
   fallback is sneaking while holding the configured item. Commands are Fabric
   client commands, so no operator permission is involved.
2. **Single-player only for world writes.** A client cannot safely mutate a
   remote server's world. `PullCommands` checks `WorldContext.singleplayer()`
   before pulling, aborting or checking out; `ApplyService` returns a failed
   future when `getSingleplayerServer()` is null.
3. **The world is authoritative in single-player.** Captures and applies run on
   the integrated-server thread so they see and mutate the same authoritative
   level. Multiplayer captures run on the client thread and only see loaded
   chunks.
4. **Relative coordinates are canonical.** A repository stores positions relative
   to the selection origin. That is what lets a build be moved to a new origin,
   and what lets pull apply it at the player's local origin instead of the
   original absolute coordinates. See `Selection`, `Snapshot.rebase`,
   `Snapshot.retain`, `ApplyPlan`.
5. **Fast-forward only, and apply before moving HEAD.** `/git pull` never merges.
   `RepoService.planPull` classifies the branches as `UP_TO_DATE`,
   `FAST_FORWARD` or `DIVERGED`; `PullCommands` only applies a fast-forward. The
   branch is moved by `RepoService.fastForward` **after** every selected build was
   applied, so a failed world write cannot leave Git ahead of the world.
6. **A failed apply is recoverable.** Every apply can write a pre-apply backup
   (`ApplyService.begin`), and `/git pull --abort` restores the latest backup with
   `ApplyPlan.restore`.
7. **Durable local state, with bounded memory.** Staged snapshots, selections and
   backups live under `config/minecraftgit/` and survive restarts. Captures are
   volume/entity/byte capped (`ModConfig`) to keep a client tick or frame from
   stalling.
8. **The repository format is portable text.** The commit tree is ordinary CSV
   and JSON, so normal Git tooling can inspect it and the plugin can read it.
   Staging files are deliberately *not* the commit format: they are local,
   machine-only, possibly large, and therefore gzipped binary (`SnapshotStore`).
9. **Local metadata is not committed.** Selection metadata, repository metadata
   and staging contain absolute coordinates and player identity and stay outside
   the repository. Only `meta.json` inside a selection directory is committed,
   and it is diagnostic/portable metadata, not local state.
10. **No external Git binary.** JGit 6.10 and JavaEWAH are nested in the mod jar,
    so a player does not need Git installed.

## 1.6 Technology

| Layer | Choice |
|-------|--------|
| Loader / API | Fabric Loader 0.19.5, Fabric API 0.102.0+1.21 |
| Game | Minecraft 1.21, Mojang official mappings |
| Language / runtime | Java 21 bytecode; Gradle/Loom run on JDK 25 |
| Git | JGit 6.10 (pure Java), JavaEWAH 1.2.3, both bundled |
| JSON | Gson |
| Commands | Brigadier / Fabric client command API |
| Persistence | JGit repositories + JSON metadata + gzipped binary snapshots + CSV commit trees |
| Tests | JUnit 5, pure JVM (no game process) |

Build, release and bundling details are in [`../DEVOPS.md`](../DEVOPS.md).

## 1.7 Where data lives

Everything is under `config/minecraftgit/` in the game directory:

```text
config/minecraftgit/
├── config.json                      # ModConfig (persisted schema)
├── repos/<world>__<dimension>/      # real Git repositories (JGit)
├── meta/<world>__<dimension>.json   # RepoMetadata: branch, remote, author, last selection
├── selections/<world>__<dimension>/<player-uuid>.json
├── staging/<world>__<dimension>/<player-uuid>/<selection>.snap
├── backups/<world>__<dimension>/<player-uuid>/<selection>/*.snap
├── tmp/                             # transient commit/pull/checkout trees
└── logs/debug-<millis>.log          # optional
```

A world key includes the dimension, so the Overworld, Nether and End never share
coordinates. In multiplayer it also includes the server address, so two servers
with the same display name do not share a repository or selection list
(`WorldContext.current`). Repositories live outside the save folder, mirroring the
plugin's decision: world backups never capture a hot `.git` directory, and a map
copy does not carry a repository with it (`RepoLocator`).

## 1.8 Related documentation

- [02-Architecture.md](02-Architecture.md) — layers, services, threads and lifecycle.
- [03-Data-Model-and-Format.md](03-Data-Model-and-Format.md) — the data itself.
- [04-Workflows.md](04-Workflows.md) — every operation end to end.
- [05-Low-Level-Data-Flow.md](05-Low-Level-Data-Flow.md) — function-level call/data tables.
- [06-Class-Reference.md](06-Class-Reference.md) — all production classes.
- [07-Error-Handling-and-Concurrency.md](07-Error-Handling-and-Concurrency.md) — failure and threading rules.
- [`../Git-Format.md`](../Git-Format.md) — repository format specification.
