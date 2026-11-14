# WorldAccess — Software Detailed Design

> **API Documentation:** [WorldAccess.md](./WorldAccess.md)  
> **Source File:** [WorldAccess.java](../../../../../../../../src/client/java/org/almond/minecraftgit/client/world/WorldAccess.java)

## Table of Contents

- [Usage](#usage)
- [1. Overview](#1-overview)
- [2. Package Declaration & Imports](#2-package-declaration-imports)
- [3. Class Declaration](#3-class-declaration)
- [4. Constructor](#4-constructor)
- [5. `capture()`](#5-capture)
- [6. `capturePositions()`](#6-capturepositions)
- [7. `captureOnClientThread()`](#7-captureonclientthread)

## Usage

`WorldAccess` is never instantiated; `StagingCommands.add` and `StatusCommands.status`/`diff` call the static `capture` method from the client thread, and the returned future is consumed in a `whenComplete` that hops back to the client. `capturePositions` mirrors the same routing for exact-position reads, although `ApplyService.begin` currently calls `SnapshotService.capturePositions` directly because it already runs on the integrated-server thread.

```java
WorldAccess.capture(services, world, selection)
        .thenCompose(result -> services.async(() -> services.staging().stage(selection, result.snapshot())))
        .whenComplete((staged, error) -> client.execute(() -> handle(staged, error)));
```

---

## 1. Overview

Chooses where a capture runs. In single-player the integrated server owns the
authoritative world and can load chunks; in multiplayer only client-loaded
chunks are available.

---

## 2. Package Declaration & Imports

```java
package org.almond.minecraftgit.client.world;
```

```java
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import org.almond.minecraftgit.client.GitServices;
import org.almond.minecraftgit.client.WorldContext;
import org.almond.minecraftgit.model.BlockPos;
import org.almond.minecraftgit.model.Selection;
import org.almond.minecraftgit.model.Snapshot;
import org.almond.minecraftgit.util.UserFacingException;
import org.almond.minecraftgit.world.CaptureResult;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
```

| Import | Purpose |
|--------|---------|
| `Minecraft` | Provides `getInstance()`, `getSingleplayerServer()`, the current `level`, the thread check and `execute`. |
| `MinecraftServer` | The integrated server whose `execute` method schedules server-thread captures. |
| `ServerLevel` | The authoritative server-side level passed to `SnapshotService`. |
| [`GitServices`](../GitServices.md) | Service locator that supplies `snapshots()`; the facade does not own the service graph. |
| [`WorldContext`](../WorldContext.md) | Decides single-player versus multiplayer and maps the dimension to a `ServerLevel`. |
| [`BlockPos`](../../../../../../../main/java/org/almond/minecraftgit/model/BlockPos.md) | Origin and position coordinates for the exact-position capture. |
| [`Selection`](../../../../../../../main/java/org/almond/minecraftgit/model/Selection.md) | Input to the full selection capture. |
| [`Snapshot`](../../../../../../../main/java/org/almond/minecraftgit/model/Snapshot.md) | Result type of the exact-position capture. |
| [`UserFacingException`](../../../../../../../main/java/org/almond/minecraftgit/util/UserFacingException.md) | Chat-safe error used in failed futures for a closed world or unavailable integrated server. |
| [`CaptureResult`](../../../../../../../main/java/org/almond/minecraftgit/world/CaptureResult.md) | Result type of the full selection capture, including skip counts and warnings. |
| `Collection` | Parameter type for the exact positions to read. |
| `CompletableFuture` | Async return contract; captures may complete on the server or client thread. |

---

## 3. Class Declaration

```java
public final class WorldAccess {
```

`WorldAccess` is a `final` utility facade with only static methods and a private constructor, so it cannot be instantiated or subclassed and holds no state. Its single design responsibility is choosing the owning thread for a read: the integrated-server thread when `WorldContext.singleplayer()` is true (which also unlocks chunk forcing), and the client thread otherwise. It does not cache `Minecraft` or the server; each call re-resolves them so a world change between calls cannot leave a stale reference.

---

## 4. Constructor

```java
private WorldAccess()
```

The constructor is private and empty, enforcing the utility-class contract: all API is static and the compiler prevents accidental `new WorldAccess()`. There is no resource to initialise or close.

---

## 5. `capture()`

```java
public static CompletableFuture<CaptureResult> capture(GitServices services, WorldContext world,
                                                           Selection selection)
```

The method branches on `world.singleplayer()`. In single-player it obtains `client.getSingleplayerServer()` and `world.serverLevel(client)`; if either is null (for example the dimension is still loading) it returns a failed future with `UserFacingException("The integrated server for this dimension is not available.")`. Otherwise it allocates a `CompletableFuture`, submits a task with `server.execute` that calls `SnapshotService.capture(selection, level, true)`, and completes or completes exceptionally from the server thread; the `true` flag is what allows the authoritative server to force unloaded chunks so staging and status do not silently truncate.

In multiplayer it delegates to `captureOnClientThread(client, () -> services.snapshots().capture(selection, client.level, false))`. That helper handles the no-world case, runs inline when already on the client thread, and otherwise schedules with `client.execute`; the `false` flag is required because a client-only mod cannot ask a remote server to load chunks, so unloaded chunks are simply skipped and counted in the `CaptureResult`.

---

## 6. `capturePositions()`

```java
public static CompletableFuture<Snapshot> capturePositions(GitServices services, WorldContext world,
                                                               String selectionName, BlockPos origin,
                                                               Collection<BlockPos> positions)
```

The single-player path duplicates the capability check from `capture` (server and dimension level must both exist) and then submits `SnapshotService.capturePositions(level, selectionName, origin, positions, true)` to the server thread with a fresh future; `true` lets a pre-apply backup cover positions in chunks the player is not near. The catching of `Throwable` and `completeExceptionally` keeps errors inside the future contract.

The multiplayer path differs deliberately: it checks `client.level` and returns a failed `UserFacingException("Open a world first.")` if it is null, but then calls `SnapshotService.capturePositions(client.level, ..., false)` directly and wraps the value in `CompletableFuture.completedFuture`. It does not use `captureOnClientThread`, so it assumes the caller is already on the client thread (commands are) and a failure propagates synchronously instead of becoming an exceptional future. It never forces chunks because only the remote server could do that.

---

## 7. `captureOnClientThread()`

```java
private static <T> CompletableFuture<T> captureOnClientThread(Minecraft client,
                                                                  java.util.function.Supplier<T> task)
```

A small generic adapter used only by the multiplayer `capture` path. It rejects a null `client.level` with a failed `UserFacingException`, avoiding an NPE deeper in `SnapshotService`. If `client.isSameThread()` is true it evaluates the supplier inline (still catching `Throwable` into a failed future); otherwise it allocates a future and calls `client.execute`, completing it from the client thread. This makes the async contract uniform: callers never receive a thrown exception, only a future that may fail, and the task never touches `ClientLevel` from a non-client thread.

---
