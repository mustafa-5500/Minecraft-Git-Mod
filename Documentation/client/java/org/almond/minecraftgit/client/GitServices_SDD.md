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

Constructed once in `MinecraftGitClient.onInitializeClient()` after `ModConfig.load` and `DebugLogger` exist, then published via `MinecraftGitClient.services()` and passed to `GitCommand.register`, `WandEvents.register` and `SelectionRenderer.register`. Callers resolve collaborators through getters such as `services.selections()`, and `CommandSupport.complete` uses `services.async(...)` to move blocking work off the render thread before returning to the client thread. Exactly one instance is expected: `MinecraftGitClient` closes it and nulls the static reference on `CLIENT_STOPPING`.

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
| `FabricLoader` | Reads the mod container version in `modVersion()`; the call is wrapped so the loader's absence in unit tests degrades to `development`. |
| [`MinecraftGit`](../../../../../../main/java/org/almond/minecraftgit/MinecraftGit.md) | Supplies `MOD_ID`, the id used for the version lookup. |
| [`BackupService`](../../../../../../main/java/org/almond/minecraftgit/backup/BackupService.md) | Constructed with `config.backupsRoot()` and `config.maxBackups`, then shared with `ApplyService`. |
| [`CommitService`](../../../../../../main/java/org/almond/minecraftgit/commit/CommitService.md) | Constructed from repos, staging, config, debug and `modVersion()`; commits staged snapshots. |
| [`ModConfig`](../../../../../../main/java/org/almond/minecraftgit/config/ModConfig.md) | The persisted client configuration supplying every data root, cap and display flag used by the graph. |
| [`SelectionManager`](selection/SelectionManager.md) | Client-side wand session state layered over `SelectionService` and `RepoManager`. |
| [`ApplyService`](world/ApplyService.md) | Writes an `ApplyPlan` through the integrated server and receives the shared IO executor. |
| [`DebugLogger`](../../../../../../main/java/org/almond/minecraftgit/debug/DebugLogger.md) | Session diagnostics log shared by repository, snapshot, staging, status and apply services. |
| [`RepoManager`](../../../../../../main/java/org/almond/minecraftgit/repo/RepoManager.md) | Per-world JGit repository manager and metadata cache, closed first on shutdown. |
| [`SelectionService`](../../../../../../main/java/org/almond/minecraftgit/selection/SelectionService.md) | Durable selection store handed to `SelectionManager` and used by commands. |
| [`SnapshotStore`](../../../../../../main/java/org/almond/minecraftgit/staging/SnapshotStore.md) | Fresh in-memory snapshot store handed to `StagingService`. |
| [`StagingService`](../../../../../../main/java/org/almond/minecraftgit/staging/StagingService.md) | Staging area rooted at `config.stagingRoot()` for snapshots awaiting a commit. |
| [`StatusService`](../../../../../../main/java/org/almond/minecraftgit/status/StatusService.md) | Status and diff reporting over the repository manager and staging area. |
| [`PullService`](../../../../../../main/java/org/almond/minecraftgit/sync/PullService.md) | Remote pull and import helper rooted at the configured temp directory. |
| [`SnapshotService`](../../../../../../main/java/org/almond/minecraftgit/world/SnapshotService.md) | World snapshot capture service, also used by `ApplyService`. |
| `Path` | Type of the normalized data root and the only path the graph itself stores. |
| `CompletableFuture` | Return type of the `async` and `asyncRun` helpers. |
| `ExecutorService` | Field type of the shared IO pool and a constructor argument to `ApplyService`. |
| `Executors` | Creates the fixed-size thread pool in the constructor. |
| `ThreadFactory` | Names the pool threads `minecraftgit-io` and marks them daemon so they never keep the JVM alive. |
| `TimeUnit` | Supplies `TimeUnit.SECONDS` for the bounded `awaitTermination` in `close()`. |
| `Supplier` | Parameter type of `async`, the value-producing IO task. |

---

## 3. Class Declaration

```java
public final class GitServices implements AutoCloseable {
```

- **`implements AutoCloseable`** — lets `MinecraftGitClient` release repository handles, the debug log and the IO pool with one `close()` call, and makes the graph usable in try-with-resources tests.

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
| `root` | `Path` | Normalized absolute data root captured at construction; `root()` reports it while services derive their paths from `config`. |
| `config` | [`ModConfig`](../../../../../../main/java/org/almond/minecraftgit/config/ModConfig.md) | The loaded `ModConfig` retained for `config()`, `messages` and every service. |
| `debug` | [`DebugLogger`](../../../../../../main/java/org/almond/minecraftgit/debug/DebugLogger.md) | Session `DebugLogger`; closed after the repositories so their shutdown messages still reach the log. |
| `messages` | [`Messages`](Messages.md) | Created from `config` so feedback honours `config.prefix`. |
| `repos` | [`RepoManager`](../../../../../../main/java/org/almond/minecraftgit/repo/RepoManager.md) | Per-world-id `RepoManager` built from `config` and `debug`. |
| `selections` | [`SelectionService`](../../../../../../main/java/org/almond/minecraftgit/selection/SelectionService.md) | Durable selection store built from `config` and shared with `selectionManager`. |
| `selectionManager` | [`SelectionManager`](selection/SelectionManager.md) | Wand session manager built over `selections` and `repos`. |
| `snapshots` | [`SnapshotService`](../../../../../../main/java/org/almond/minecraftgit/world/SnapshotService.md) | Snapshot capture service built from `config` and `debug`. |
| `staging` | [`StagingService`](../../../../../../main/java/org/almond/minecraftgit/staging/StagingService.md) | Staging service over `config.stagingRoot()` and a new `SnapshotStore`. |
| `commits` | [`CommitService`](../../../../../../main/java/org/almond/minecraftgit/commit/CommitService.md) | Commit service depending on repos, staging, config, debug and the mod version. |
| `status` | [`StatusService`](../../../../../../main/java/org/almond/minecraftgit/status/StatusService.md) | Status service depending on repos and staging. |
| `backups` | [`BackupService`](../../../../../../main/java/org/almond/minecraftgit/backup/BackupService.md) | Backup service rooted at `config.backupsRoot()` with a `config.maxBackups` cap. |
| `pull` | [`PullService`](../../../../../../main/java/org/almond/minecraftgit/sync/PullService.md) | Pull service using `config.tmpRoot()`. |
| `apply` | [`ApplyService`](world/ApplyService.md) | Apply service sharing `snapshots`, `backups`, config, debug and the IO executor. |
| `io` | `ExecutorService` | Fixed pool of two daemon threads created after the services; owns all asynchronous work and is shut down by `close()`. |

---

## 5. Constructor

```java
public GitServices(Path root, ModConfig config, DebugLogger debug)
```

Builds the graph eagerly in dependency order: messages, repos, selections, selectionManager, snapshots, staging, commits, status, then the IO pool, then backups, pull and apply (which needs both `backups` and `io`). `root` is normalized to an absolute path, and the `ThreadFactory` creates daemon `minecraftgit-io` threads so an unfinished JGit operation cannot keep the game process alive. No repository IO happens at construction; `RepoManager` opens a repository lazily per world id.

---

## 6. `root()`

```java
public Path root()
```

Returns the normalized absolute root captured at construction. No IO, no allocation, and the value never changes.

---

## 7. `config()`

```java
public ModConfig config()
```

Returns the same `ModConfig` instance the services hold, so toggling a field such as `renderSelections` is visible to the renderer and commands on their next read.

---

## 8. `debug()`

```java
public DebugLogger debug()
```

Returns the shared logger; services write through it only when `config.debug` or `config.debugBlocks` enables the relevant category.

---

## 9. `messages()`

```java
public Messages messages()
```

Returns the chat formatter created for this graph. Commands call `services.messages()` instead of building components, which keeps prefix and colour handling in one place.

---

## 10. `repos()`

```java
public RepoManager repos()
```

Exposes the `RepoManager` that lazily creates and caches a `RepoService` plus `RepoMetadata` per world id, keeping JGit handles alive for the session until `close()`.

---

## 11. `selections()`

```java
public SelectionService selections()
```

Returns the durable selection store; `SelectionManager` holds the same reference when validating the remembered active name.

---

## 12. `selectionManager()`

```java
public SelectionManager selectionManager()
```

Returns the client-only wand manager, which keeps a `ConcurrentHashMap` of per-player sessions and persists the active name through `RepoManager.setLastSelection`.

---

## 13. `snapshots()`

```java
public SnapshotService snapshots()
```

Returns the snapshot service used by staging capture and by `ApplyService` when restoring blocks and entities.

---

## 14. `staging()`

```java
public StagingService staging()
```

Returns the staging service; commands mutate it on the client thread, while longer snapshot work is dispatched to the IO pool by callers such as `CommandSupport.complete`.

---

## 15. `commits()`

```java
public CommitService commits()
```

Returns the commit service that turns staged snapshots into a JGit commit using the configured branch and committer identity.

---

## 16. `status()`

```java
public StatusService status()
```

Returns the status service; `/git status` and `/git diff` invoke it through `async` because it scans world and staged snapshot state.

---

## 17. `backups()`

```java
public BackupService backups()
```

Returns the backup service; `ApplyService` archives the affected region before writing and the service prunes archives beyond `maxBackups`.

---

## 18. `pull()`

```java
public PullService pull()
```

Returns the pull service used by pull commands to fetch into the temp root before inspecting or applying remote content.

---

## 19. `apply()`

```java
public ApplyService apply()
```

Returns the apply service that executes an `ApplyPlan` on the integrated server in tick batches and completes a future with the result or an `ApplyException`.

---

## 20. `modVersion()`

```java
public static String modVersion()
```

Looks the version up with `FabricLoader.getModContainer(MinecraftGit.MOD_ID)` and takes the friendly string; a missing container or any runtime failure is caught and reported as `development`, which is what plain-JVM unit tests see. `CommitService` captures the value once at construction.

---

## 21. `io()`

```java
public ExecutorService io()
```

Returns the sole executor for JGit and filesystem work. It holds two daemon threads, so parallel operations queue instead of spawning unbounded threads, and the pool is owned by `close()`.

---

## 22. `async()`

```java
public <T> CompletableFuture<T> async(Supplier<T> task)
```

Wraps `CompletableFuture.supplyAsync(task, io)`; callers such as `CommandSupport.complete` attach a stage that hops back to the client thread with `Minecraft.execute`. Exceptions are captured in the future and must be unwrapped before display.

---

## 23. `asyncRun()`

```java
public CompletableFuture<Void> asyncRun(Runnable task)
```

Wraps `CompletableFuture.runAsync(task, io)` for cleanup work such as `services.pull().cleanup(prepared)`; the future exists so callers can observe completion or failure.

---

## 24. `close()`

```java
@Override
public void close()
```

Closes `repos` (releasing every `RepoService`), closes `debug`, then shuts the IO pool down, waits up to two seconds and escalates to `shutdownNow` on timeout. `InterruptedException` also forces `shutdownNow` and re-sets the interrupt flag. Called from the `CLIENT_STOPPING` hook in `MinecraftGitClient`.

---
