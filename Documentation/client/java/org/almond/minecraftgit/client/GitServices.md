# GitServices

> **Software Detailed Documentation:** [GitServices_SDD.md](./GitServices_SDD.md)  
> **Source File:** [GitServices.java](../../../../../../../src/client/java/org/almond/minecraftgit/client/GitServices.java)

The top level service controlling

## Table of Contents

- [Usage](#usage)
- [Fields](#fields)
- [GitServices (Constructor)](#gitservices-constructor)
- [root](#root)
- [config](#config)
- [debug](#debug)
- [messages](#messages)
- [repos](#repos)
- [selections](#selections)
- [selectionManager](#selectionmanager)
- [snapshots](#snapshots)
- [staging](#staging)
- [commits](#commits)
- [status](#status)
- [backups](#backups)
- [pull](#pull)
- [apply](#apply)
- [modVersion](#modversion)
- [io](#io)
- [async](#async)
- [asyncRun](#asyncrun)
- [close](#close)
- [See Also](#see-also)

## Usage

Constructed once by the client entrypoint as `new GitServices(root, config, debug)` and published through `MinecraftGitClient.services()`; commands, wand callbacks and the renderer resolve their collaborators through that single instance. For example, `services.async(() -> services.selections().list(world.id(), player.getUUID()))` performs the disk-backed selection read on the shared daemon IO pool while the render thread stays free. Do not construct a second graph: each instance owns its own executor and repository handles, and `close()` is only called from the client-stopping hook.

Service graph for the client mod. Repository, selection, staging and commit
services are added here as phases land; the IO executor is created once so
JGit work never runs on the render thread.

---

## Fields

- `root` (`Path`) — Normalized absolute data root (`<config dir>/minecraftgit` by default); every service path is derived from `config` beneath it. Immutable for the lifetime of the graph.
- `config` ([`ModConfig`](../../../../../../main/java/org/almond/minecraftgit/config/ModConfig.md)) — The loaded `ModConfig` shared by every service; supplies data roots, caps, wand item, chat prefix and render budget. Read-mostly after construction, though flags such as `renderSelections` are honoured live.
- `debug` ([`DebugLogger`](../../../../../../main/java/org/almond/minecraftgit/debug/DebugLogger.md)) — Session `DebugLogger` writing under `config.logsRoot()`; closed by `close()` after the repository manager.
- `messages` ([`Messages`](Messages.md)) — Chat formatter built from the same config, so command feedback uses `config.prefix`.
- `repos` ([`RepoManager`](../../../../../../main/java/org/almond/minecraftgit/repo/RepoManager.md)) — Per-world-id `RepoManager` owning JGit handles and local repo metadata; closed first in `close()`.
- `selections` ([`SelectionService`](../../../../../../main/java/org/almond/minecraftgit/selection/SelectionService.md)) — Durable per-world and per-player selection list; `selectionManager` layers transient wand state on top of it.
- `selectionManager` ([`SelectionManager`](selection/SelectionManager.md)) — Per-player session state: the two corners, the pending cuboid and the remembered active selection name.
- `snapshots` ([`SnapshotService`](../../../../../../main/java/org/almond/minecraftgit/world/SnapshotService.md)) — Snapshot capture service for blocks, block entities and entities; world capture feeds staging and `ApplyService` reuses it when restoring.
- `staging` ([`StagingService`](../../../../../../main/java/org/almond/minecraftgit/staging/StagingService.md)) — Staging area rooted at `config.stagingRoot()` over a fresh `SnapshotStore`, recording which snapshots are staged per world and player.
- `commits` ([`CommitService`](../../../../../../main/java/org/almond/minecraftgit/commit/CommitService.md)) — Commit service wired to `repos`, `staging`, config, logger and `modVersion()`; turns staged snapshots into JGit commits.
- `status` ([`StatusService`](../../../../../../main/java/org/almond/minecraftgit/status/StatusService.md)) — Status and diff service over `repos` and `staging`, used by `/git status` and `/git diff`.
- `backups` ([`BackupService`](../../../../../../main/java/org/almond/minecraftgit/backup/BackupService.md)) — Backup archive manager under `config.backupsRoot()`, pruning to `config.maxBackups`.
- `pull` ([`PullService`](../../../../../../main/java/org/almond/minecraftgit/sync/PullService.md)) — Pull and import service using `config.tmpRoot()` for downloaded temporary trees.
- `apply` ([`ApplyService`](world/ApplyService.md)) — World apply service that replays an `ApplyPlan` through the integrated server in tick batches; shares the daemon IO pool.
- `io` (`ExecutorService`) — Fixed pool of two daemon `minecraftgit-io` threads created in the constructor; the only executor for JGit and file IO. Shut down by `close()`.

---

## GitServices (Constructor)

### Signature
```java
public GitServices(Path root, ModConfig config, DebugLogger debug)
```

### Description
The constructor function, taking the root of the Repository, the Configuration, and a Debug Logger. Furthermore creating a `ThreadFactory` data struct, basically a function that is stored in the object which can be run to start a new `minecraftgit` thread and set it as a `daemon` 

### Parameters
| Name     | Type                                                                                      | Description    |
| -------- | ----------------------------------------------------------------------------------------- | -------------- |
| `root`   | `Path`                                                                                    | Mod data root (normally `<config dir>/minecraftgit`); normalized to an absolute path and used as the base for all service paths. |
| `config` | [`ModConfig`](../../../../../../main/java/org/almond/minecraftgit/config/ModConfig.md)    | Loaded `ModConfig` providing data roots, caps, wand item, prefix and render budget. |
| `debug`  | [`DebugLogger`](../../../../../../main/java/org/almond/minecraftgit/debug/DebugLogger.md) | Session `DebugLogger` used by the repository, snapshot, staging, status and apply services. |

### Returns
`GitServices` instance.

---

## root

### Signature
```java
public Path root()
```

### Description
The `root` getter.

### Parameters
None.

### Returns
`Path` — The Path to the repository root.

---

## config

### Signature
```java
public ModConfig config()
```

### Description
The `config` getter.

### Parameters
None.

### Returns
[`ModConfig`](../../../../../../main/java/org/almond/minecraftgit/config/ModConfig.md) — Returns the configurations.

---

## debug

### Signature
```java
public DebugLogger debug()
```

### Description
The `debug` logger getter.

### Parameters
None.

### Returns
[`DebugLogger`](../../../../../../main/java/org/almond/minecraftgit/debug/DebugLogger.md) — The mod's logger.

---

## messages

### Signature
```java
public Messages messages()
```

### Description
Returns the `Messages` formatter created at construction, which every command uses for prefixed chat output.

### Parameters
None.

### Returns
[`Messages`](Messages.md) — the shared chat formatter for this service graph.

---

## repos

### Signature
```java
public RepoManager repos()
```

### Description
Returns the `RepoManager` for this client session, which lazily creates one `RepoService` and its metadata per world id.

### Parameters
None.

### Returns
[`RepoManager`](../../../../../../main/java/org/almond/minecraftgit/repo/RepoManager.md) — owner of per-world repository handles and local metadata; closed by `close()`.

---

## selections

### Signature
```java
public SelectionService selections()
```

### Description
Returns the durable `SelectionService` that loads, mutates and persists named selections per world id and player.

### Parameters
None.

### Returns
[`SelectionService`](../../../../../../main/java/org/almond/minecraftgit/selection/SelectionService.md) — the selection store rooted at `config.selectionsRoot()`.

---

## selectionManager

### Signature
```java
public SelectionManager selectionManager()
```

### Description
Returns the per-player wand session manager that tracks both corners, the pending cuboid and the remembered active selection name.

### Parameters
None.

### Returns
[`SelectionManager`](selection/SelectionManager.md) — wand and corner state layered over `selections()` and `repos()`.

---

## snapshots

### Signature
```java
public SnapshotService snapshots()
```

### Description
Returns the `SnapshotService` that reads blocks, block entities and entities from the current level; callers run it on the thread that owns that level.

### Parameters
None.

### Returns
[`SnapshotService`](../../../../../../main/java/org/almond/minecraftgit/world/SnapshotService.md) — snapshot capture and restore entry point.

---

## staging

### Signature
```java
public StagingService staging()
```

### Description
Returns the `StagingService` rooted at `config.stagingRoot()`; it records which snapshot ids are staged for a world and player.

### Parameters
None.

### Returns
[`StagingService`](../../../../../../main/java/org/almond/minecraftgit/staging/StagingService.md) — staging area over the `SnapshotStore` created at construction.

---

## commits

### Signature
```java
public CommitService commits()
```

### Description
Returns the `CommitService` wired to the repository manager, staging service, config, debug logger and `modVersion()`.

### Parameters
None.

### Returns
[`CommitService`](../../../../../../main/java/org/almond/minecraftgit/commit/CommitService.md) — creates commits from staged snapshots.

---

## status

### Signature
```java
public StatusService status()
```

### Description
Returns the `StatusService` that computes working-tree status and diffs from repository and staging state.

### Parameters
None.

### Returns
[`StatusService`](../../../../../../main/java/org/almond/minecraftgit/status/StatusService.md) — status and diff reporting for a world repository.

---

## backups

### Signature
```java
public BackupService backups()
```

### Description
Returns the `BackupService` that archives the world before an apply and prunes older archives to `config.maxBackups`.

### Parameters
None.

### Returns
[`BackupService`](../../../../../../main/java/org/almond/minecraftgit/backup/BackupService.md) — backup archive manager rooted at `config.backupsRoot()`.

---

## pull

### Signature
```java
public PullService pull()
```

### Description
Returns the `PullService` that fetches remote content into `config.tmpRoot()` before it is inspected or applied.

### Parameters
None.

### Returns
[`PullService`](../../../../../../main/java/org/almond/minecraftgit/sync/PullService.md) — pull and import helper using the configured temporary directory.

---

## apply

### Signature
```java
public ApplyService apply()
```

### Description
Returns the `ApplyService` that replays an `ApplyPlan` through the integrated server in tick batches; it is single-player only and shares the IO pool for snapshot work.

### Parameters
None.

### Returns
[`ApplyService`](world/ApplyService.md) — apply orchestration bound to snapshots, backups, config, logger and the IO executor.

---

## modVersion

### Signature
```java
public static String modVersion()
```

### Description
Reads the mod's friendly version string from the Fabric loader, returning `development` when the mod container is absent (plain-JVM tests) or the loader throws.

### Parameters
None.

### Returns
`String` — the resolved mod version, or `development`; never null.

---

## io

### Signature
```java
public ExecutorService io()
```

### Description
Returns the shared two-thread daemon executor (`minecraftgit-io`) created in the constructor and used for all JGit and filesystem work. Exposed for services such as `ApplyService` that schedule their own work; do not shut it down independently.

### Parameters
None.

### Returns
`ExecutorService` — the live IO pool, owned and shut down by `close()`.

---

## async

### Signature
```java
public <T> CompletableFuture<T> async(Supplier<T> task)
```

### Description
Schedules a value-producing task on the IO pool and returns a future completed when it finishes; a thrown exception completes the future exceptionally. The task runs off the render thread and must not touch Minecraft world state directly.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `task` | `Supplier<T>` | Task to run on the IO pool. |

### Returns
`<T> CompletableFuture<T>` — result of `task`, completed on an IO thread or completed exceptionally if it throws.

---

## asyncRun

### Signature
```java
public CompletableFuture<Void> asyncRun(Runnable task)
```

### Description
Schedules a fire-and-forget `Runnable` on the IO pool, used for cleanup such as discarding a pulled temporary directory. Failures are captured in the returned future rather than logged automatically.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `task` | `Runnable` | Work to run on the IO pool. |

### Returns
`CompletableFuture<Void>` — completes when `task` finishes, or exceptionally when it throws.

---

## close

### Signature
```java
@Override
public void close()
```

### Description
Shuts the graph down in order: closes the repository manager (releasing every `RepoService`), closes the debug logger, then shuts the IO pool down and waits up to two seconds before forcing `shutdownNow`. A thread interrupt during that wait also forces `shutdownNow` and restores the interrupt flag, so it is safe to call from the `CLIENT_STOPPING` hook.

### Parameters
None.

### Returns
`void`

---

## See Also

- **Software Detailed Design:** [GitServices_SDD.md](./GitServices_SDD.md)
