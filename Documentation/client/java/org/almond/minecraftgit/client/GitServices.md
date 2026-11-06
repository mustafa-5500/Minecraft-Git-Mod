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

TODO: Add usage examples for `GitServices`.

Service graph for the client mod. Repository, selection, staging and commit
services are added here as phases land; the IO executor is created once so
JGit work never runs on the render thread.

---

## Fields

- `root` (`Path`) — TODO: describe field
- `config` ([`ModConfig`](../../../../../../main/java/org/almond/minecraftgit/config/ModConfig.md)) — TODO: describe field
- `debug` ([`DebugLogger`](../../../../../../main/java/org/almond/minecraftgit/debug/DebugLogger.md)) — TODO: describe field
- `messages` ([`Messages`](Messages.md)) — TODO: describe field
- `repos` ([`RepoManager`](../../../../../../main/java/org/almond/minecraftgit/repo/RepoManager.md)) — TODO: describe field
- `selections` ([`SelectionService`](../../../../../../main/java/org/almond/minecraftgit/selection/SelectionService.md)) — TODO: describe field
- `selectionManager` ([`SelectionManager`](selection/SelectionManager.md)) — TODO: describe field
- `snapshots` ([`SnapshotService`](../../../../../../main/java/org/almond/minecraftgit/world/SnapshotService.md)) — TODO: describe field
- `staging` ([`StagingService`](../../../../../../main/java/org/almond/minecraftgit/staging/StagingService.md)) — TODO: describe field
- `commits` ([`CommitService`](../../../../../../main/java/org/almond/minecraftgit/commit/CommitService.md)) — TODO: describe field
- `status` ([`StatusService`](../../../../../../main/java/org/almond/minecraftgit/status/StatusService.md)) — TODO: describe field
- `backups` ([`BackupService`](../../../../../../main/java/org/almond/minecraftgit/backup/BackupService.md)) — TODO: describe field
- `pull` ([`PullService`](../../../../../../main/java/org/almond/minecraftgit/sync/PullService.md)) — TODO: describe field
- `apply` ([`ApplyService`](world/ApplyService.md)) — TODO: describe field
- `io` (`ExecutorService`) — TODO: describe field

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
| `root`   | `Path`                                                                                    | TODO: describe |
| `config` | [`ModConfig`](../../../../../../main/java/org/almond/minecraftgit/config/ModConfig.md)    | TODO: describe |
| `debug`  | [`DebugLogger`](../../../../../../main/java/org/almond/minecraftgit/debug/DebugLogger.md) | TODO: describe |

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
.

### Parameters
None.

### Returns
[`Messages`](Messages.md) — TODO: describe return value.

---

## repos

### Signature
```java
public RepoManager repos()
```

### Description
TODO: Describe what `repos` does.

### Parameters
None.

### Returns
[`RepoManager`](../../../../../../main/java/org/almond/minecraftgit/repo/RepoManager.md) — TODO: describe return value.

---

## selections

### Signature
```java
public SelectionService selections()
```

### Description
TODO: Describe what `selections` does.

### Parameters
None.

### Returns
[`SelectionService`](../../../../../../main/java/org/almond/minecraftgit/selection/SelectionService.md) — TODO: describe return value.

---

## selectionManager

### Signature
```java
public SelectionManager selectionManager()
```

### Description
TODO: Describe what `selectionManager` does.

### Parameters
None.

### Returns
[`SelectionManager`](selection/SelectionManager.md) — TODO: describe return value.

---

## snapshots

### Signature
```java
public SnapshotService snapshots()
```

### Description
TODO: Describe what `snapshots` does.

### Parameters
None.

### Returns
[`SnapshotService`](../../../../../../main/java/org/almond/minecraftgit/world/SnapshotService.md) — TODO: describe return value.

---

## staging

### Signature
```java
public StagingService staging()
```

### Description
TODO: Describe what `staging` does.

### Parameters
None.

### Returns
[`StagingService`](../../../../../../main/java/org/almond/minecraftgit/staging/StagingService.md) — TODO: describe return value.

---

## commits

### Signature
```java
public CommitService commits()
```

### Description
TODO: Describe what `commits` does.

### Parameters
None.

### Returns
[`CommitService`](../../../../../../main/java/org/almond/minecraftgit/commit/CommitService.md) — TODO: describe return value.

---

## status

### Signature
```java
public StatusService status()
```

### Description
TODO: Describe what `status` does.

### Parameters
None.

### Returns
[`StatusService`](../../../../../../main/java/org/almond/minecraftgit/status/StatusService.md) — TODO: describe return value.

---

## backups

### Signature
```java
public BackupService backups()
```

### Description
TODO: Describe what `backups` does.

### Parameters
None.

### Returns
[`BackupService`](../../../../../../main/java/org/almond/minecraftgit/backup/BackupService.md) — TODO: describe return value.

---

## pull

### Signature
```java
public PullService pull()
```

### Description
TODO: Describe what `pull` does.

### Parameters
None.

### Returns
[`PullService`](../../../../../../main/java/org/almond/minecraftgit/sync/PullService.md) — TODO: describe return value.

---

## apply

### Signature
```java
public ApplyService apply()
```

### Description
TODO: Describe what `apply` does.

### Parameters
None.

### Returns
[`ApplyService`](world/ApplyService.md) — TODO: describe return value.

---

## modVersion

### Signature
```java
public static String modVersion()
```

### Description
TODO: Describe what `modVersion` does.

### Parameters
None.

### Returns
`String` — TODO: describe return value.

---

## io

### Signature
```java
public ExecutorService io()
```

### Description
TODO: Describe what `io` does.

### Parameters
None.

### Returns
`ExecutorService` — TODO: describe return value.

---

## async

### Signature
```java
public <T> CompletableFuture<T> async(Supplier<T> task)
```

### Description
TODO: Describe what `async` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `task` | `Supplier<T>` | TODO: describe |

### Returns
`<T> CompletableFuture<T>` — TODO: describe return value.

---

## asyncRun

### Signature
```java
public CompletableFuture<Void> asyncRun(Runnable task)
```

### Description
TODO: Describe what `asyncRun` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `task` | `Runnable` | TODO: describe |

### Returns
`CompletableFuture<Void>` — TODO: describe return value.

---

## close

### Signature
```java
@Override
public void close()
```

### Description
TODO: Describe what `close` does.

### Parameters
None.

### Returns
`void`

---

## See Also

- **Software Detailed Design:** [GitServices_SDD.md](./GitServices_SDD.md)
