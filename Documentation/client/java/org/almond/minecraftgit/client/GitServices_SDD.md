# GitServices — Software Detailed Design

> **API Documentation:** [GitServices.md](./GitServices.md)  
> **Source File:** [GitServices.java](../../../../../../../src/client/java/org/almond/minecraftgit/client/GitServices.java)

## Table of Contents

- [Usage](#usage)
- [1. Overview](#1-overview)
- [2. Package Declaration & Imports](#2-package-declaration-imports)
- [3. Class Declaration](#3-class-declaration)
- [4. Instance Fields](#4-instance-fields)
- [5. Constructor](#5-constructor)
- [6. `root()`](#6-root)
- [7. `config()`](#7-config)
- [8. `debug()`](#8-debug)
- [9. `messages()`](#9-messages)
- [10. `repos()`](#10-repos)
- [11. `selections()`](#11-selections)
- [12. `selectionManager()`](#12-selectionmanager)
- [13. `snapshots()`](#13-snapshots)
- [14. `staging()`](#14-staging)
- [15. `commits()`](#15-commits)
- [16. `status()`](#16-status)
- [17. `backups()`](#17-backups)
- [18. `pull()`](#18-pull)
- [19. `apply()`](#19-apply)
- [20. `modVersion()`](#20-modversion)
- [21. `io()`](#21-io)
- [22. `async()`](#22-async)
- [23. `asyncRun()`](#23-asyncrun)
- [24. `close()`](#24-close)

## Usage

TODO: Describe how `GitServices` is instantiated and used.

---

## 1. Overview

Service graph for the client mod. Repository, selection, staging and commit
services are added here as phases land; the IO executor is created once so
JGit work never runs on the render thread.

---

## 2. Package Declaration & Imports

```java
package org.almond.minecraftgit.client;
```

```java
import net.fabricmc.loader.api.FabricLoader;
import org.almond.minecraftgit.MinecraftGit;
import org.almond.minecraftgit.backup.BackupService;
import org.almond.minecraftgit.commit.CommitService;
import org.almond.minecraftgit.config.ModConfig;
import org.almond.minecraftgit.client.selection.SelectionManager;
import org.almond.minecraftgit.client.world.ApplyService;
import org.almond.minecraftgit.debug.DebugLogger;
import org.almond.minecraftgit.repo.RepoManager;
import org.almond.minecraftgit.selection.SelectionService;
import org.almond.minecraftgit.staging.SnapshotStore;
import org.almond.minecraftgit.staging.StagingService;
import org.almond.minecraftgit.status.StatusService;
import org.almond.minecraftgit.sync.PullService;
import org.almond.minecraftgit.world.SnapshotService;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
```

| Import | Purpose |
|--------|---------|
| `FabricLoader` | TODO: describe purpose |
| [`MinecraftGit`](../../../../../../main/java/org/almond/minecraftgit/MinecraftGit.md) | TODO: describe purpose |
| [`BackupService`](../../../../../../main/java/org/almond/minecraftgit/backup/BackupService.md) | TODO: describe purpose |
| [`CommitService`](../../../../../../main/java/org/almond/minecraftgit/commit/CommitService.md) | TODO: describe purpose |
| [`ModConfig`](../../../../../../main/java/org/almond/minecraftgit/config/ModConfig.md) | TODO: describe purpose |
| [`SelectionManager`](selection/SelectionManager.md) | TODO: describe purpose |
| [`ApplyService`](world/ApplyService.md) | TODO: describe purpose |
| [`DebugLogger`](../../../../../../main/java/org/almond/minecraftgit/debug/DebugLogger.md) | TODO: describe purpose |
| [`RepoManager`](../../../../../../main/java/org/almond/minecraftgit/repo/RepoManager.md) | TODO: describe purpose |
| [`SelectionService`](../../../../../../main/java/org/almond/minecraftgit/selection/SelectionService.md) | TODO: describe purpose |
| [`SnapshotStore`](../../../../../../main/java/org/almond/minecraftgit/staging/SnapshotStore.md) | TODO: describe purpose |
| [`StagingService`](../../../../../../main/java/org/almond/minecraftgit/staging/StagingService.md) | TODO: describe purpose |
| [`StatusService`](../../../../../../main/java/org/almond/minecraftgit/status/StatusService.md) | TODO: describe purpose |
| [`PullService`](../../../../../../main/java/org/almond/minecraftgit/sync/PullService.md) | TODO: describe purpose |
| [`SnapshotService`](../../../../../../main/java/org/almond/minecraftgit/world/SnapshotService.md) | TODO: describe purpose |
| `Path` | TODO: describe purpose |
| `CompletableFuture` | TODO: describe purpose |
| `ExecutorService` | TODO: describe purpose |
| `Executors` | TODO: describe purpose |
| `ThreadFactory` | TODO: describe purpose |
| `TimeUnit` | TODO: describe purpose |
| `Supplier` | TODO: describe purpose |

---

## 3. Class Declaration

```java
public final class GitServices implements AutoCloseable {
```

- **`implements AutoCloseable`** — TODO: explain interfaces.

---

## 4. Instance Fields

```java
private final Path root;
private final ModConfig config;
private final DebugLogger debug;
private final Messages messages;
private final RepoManager repos;
private final SelectionService selections;
private final SelectionManager selectionManager;
private final SnapshotService snapshots;
private final StagingService staging;
private final CommitService commits;
private final StatusService status;
private final BackupService backups;
private final PullService pull;
private final ApplyService apply;
private final ExecutorService io;
```

| Field | Type | Description |
|-------|------|-------------|
| `root` | `Path` | TODO: describe |
| `config` | [`ModConfig`](../../../../../../main/java/org/almond/minecraftgit/config/ModConfig.md) | TODO: describe |
| `debug` | [`DebugLogger`](../../../../../../main/java/org/almond/minecraftgit/debug/DebugLogger.md) | TODO: describe |
| `messages` | [`Messages`](Messages.md) | TODO: describe |
| `repos` | [`RepoManager`](../../../../../../main/java/org/almond/minecraftgit/repo/RepoManager.md) | TODO: describe |
| `selections` | [`SelectionService`](../../../../../../main/java/org/almond/minecraftgit/selection/SelectionService.md) | TODO: describe |
| `selectionManager` | [`SelectionManager`](selection/SelectionManager.md) | TODO: describe |
| `snapshots` | [`SnapshotService`](../../../../../../main/java/org/almond/minecraftgit/world/SnapshotService.md) | TODO: describe |
| `staging` | [`StagingService`](../../../../../../main/java/org/almond/minecraftgit/staging/StagingService.md) | TODO: describe |
| `commits` | [`CommitService`](../../../../../../main/java/org/almond/minecraftgit/commit/CommitService.md) | TODO: describe |
| `status` | [`StatusService`](../../../../../../main/java/org/almond/minecraftgit/status/StatusService.md) | TODO: describe |
| `backups` | [`BackupService`](../../../../../../main/java/org/almond/minecraftgit/backup/BackupService.md) | TODO: describe |
| `pull` | [`PullService`](../../../../../../main/java/org/almond/minecraftgit/sync/PullService.md) | TODO: describe |
| `apply` | [`ApplyService`](world/ApplyService.md) | TODO: describe |
| `io` | `ExecutorService` | TODO: describe |

---

## 5. Constructor

```java
public GitServices(Path root, ModConfig config, DebugLogger debug)
```

TODO: Provide detailed design explanation for `GitServices`.

---

## 6. `root()`

```java
public Path root()
```

TODO: Provide detailed design explanation for `root`.

---

## 7. `config()`

```java
public ModConfig config()
```

TODO: Provide detailed design explanation for `config`.

---

## 8. `debug()`

```java
public DebugLogger debug()
```

TODO: Provide detailed design explanation for `debug`.

---

## 9. `messages()`

```java
public Messages messages()
```

TODO: Provide detailed design explanation for `messages`.

---

## 10. `repos()`

```java
public RepoManager repos()
```

TODO: Provide detailed design explanation for `repos`.

---

## 11. `selections()`

```java
public SelectionService selections()
```

TODO: Provide detailed design explanation for `selections`.

---

## 12. `selectionManager()`

```java
public SelectionManager selectionManager()
```

TODO: Provide detailed design explanation for `selectionManager`.

---

## 13. `snapshots()`

```java
public SnapshotService snapshots()
```

TODO: Provide detailed design explanation for `snapshots`.

---

## 14. `staging()`

```java
public StagingService staging()
```

TODO: Provide detailed design explanation for `staging`.

---

## 15. `commits()`

```java
public CommitService commits()
```

TODO: Provide detailed design explanation for `commits`.

---

## 16. `status()`

```java
public StatusService status()
```

TODO: Provide detailed design explanation for `status`.

---

## 17. `backups()`

```java
public BackupService backups()
```

TODO: Provide detailed design explanation for `backups`.

---

## 18. `pull()`

```java
public PullService pull()
```

TODO: Provide detailed design explanation for `pull`.

---

## 19. `apply()`

```java
public ApplyService apply()
```

TODO: Provide detailed design explanation for `apply`.

---

## 20. `modVersion()`

```java
public static String modVersion()
```

TODO: Provide detailed design explanation for `modVersion`.

---

## 21. `io()`

```java
public ExecutorService io()
```

TODO: Provide detailed design explanation for `io`.

---

## 22. `async()`

```java
public <T> CompletableFuture<T> async(Supplier<T> task)
```

TODO: Provide detailed design explanation for `async`.

---

## 23. `asyncRun()`

```java
public CompletableFuture<Void> asyncRun(Runnable task)
```

TODO: Provide detailed design explanation for `asyncRun`.

---

## 24. `close()`

```java
@Override
public void close()
```

TODO: Provide detailed design explanation for `close`.

---
