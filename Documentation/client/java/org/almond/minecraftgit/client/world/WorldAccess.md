# WorldAccess

> **Software Detailed Documentation:** [WorldAccess_SDD.md](./WorldAccess_SDD.md)  
> **Source File:** [WorldAccess.java](../../../../../../../../src/client/java/org/almond/minecraftgit/client/world/WorldAccess.java)

## Table of Contents

- [Usage](#usage)
- [WorldAccess (Constructor)](#worldaccess-constructor)
- [capture](#capture)
- [capturePositions](#capturepositions)
- [captureOnClientThread](#captureonclientthread)
- [See Also](#see-also)

## Usage

Call `WorldAccess.capture(services, world, selection)` from command handlers; it returns a future that completes after the capture, and in single-player the work runs on the integrated-server thread with chunk loading while in multiplayer it runs on the client thread over loaded chunks only. Completion may arrive on either thread, so callbacks that touch chat or screens should hop back with `client.execute`.

```java
var client = source.getClient();
WorldAccess.capture(services, world, selection).whenComplete((capture, error) ->
        client.execute(() -> {
            if (error != null) {
                CommandSupport.handleError(source, CommandSupport.unwrap(error));
                return;
            }
            services.messages().send(source, capture.summary());
            for (int i = 0; i < Math.min(5, capture.warnings().size()); i++) {
                services.messages().warn(source, capture.warnings().get(i));
            }
        }));
```

Chooses where a capture runs. In single-player the integrated server owns the
authoritative world and can load chunks; in multiplayer only client-loaded
chunks are available.

---

## WorldAccess (Constructor)

### Signature
```java
private WorldAccess()
```

### Description
The private constructor makes `WorldAccess` a non-instantiable static facade. All behaviour lives in the static capture methods, which read `Minecraft.getInstance()` and the supplied `WorldContext` rather than holding per-instance state.

### Parameters
None.

### Returns
`WorldAccess` instance.

---

## capture

### Signature
```java
public static CompletableFuture<CaptureResult> capture(GitServices services, WorldContext world,
                                                           Selection selection)
```

### Description
Routes a full selection capture by world type. In single-player it resolves the integrated server and the `ServerLevel` for `world`'s dimension; if either is missing it returns a failed future with `UserFacingException`, otherwise it submits `services.snapshots().capture(selection, level, true)` with `server.execute` so chunks can be generated and the authoritative world is read on its own thread. In multiplayer it uses `captureOnClientThread` to run `capture(selection, client.level, false)` on the client thread and therefore captures only loaded chunks. Exceptions from the server-side capture are caught and complete the future exceptionally.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `services` | [`GitServices`](../GitServices.md) | Provides the shared `SnapshotService` instance. |
| `world` | [`WorldContext`](../WorldContext.md) | Supplies the dimension and whether the integrated server is available. |
| `selection` | [`Selection`](../../../../../../../main/java/org/almond/minecraftgit/model/Selection.md) | Named, cuboid selection to capture; validation errors surface through the future. |

### Returns
`CompletableFuture<CaptureResult>` — completes with the snapshot and skip/warning data, or exceptionally with a `UserFacingException` (including `SelectionException` for invalid selections).

---

## capturePositions

### Signature
```java
public static CompletableFuture<Snapshot> capturePositions(GitServices services, WorldContext world,
                                                               String selectionName, BlockPos origin,
                                                               Collection<BlockPos> positions)
```

### Description
The exact-position variant used for pre-apply style reads. In single-player it resolves the integrated server and dimension level, failing with `UserFacingException` when they are unavailable, then runs `SnapshotService.capturePositions(..., true)` on the server thread with chunk loading and completes the future from there. In multiplayer it requires `client.level`, returns a failed future with `UserFacingException` when no world is open, and otherwise performs `capturePositions(..., false)` synchronously on the calling thread (normally the client thread), wrapping the result in an already-completed future; unlike `captureOnClientThread`, an exception from that synchronous call propagates to the caller.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `services` | [`GitServices`](../GitServices.md) | Provides the shared `SnapshotService`. |
| `world` | [`WorldContext`](../WorldContext.md) | Selects the integrated-server dimension or the multiplayer client level. |
| `selectionName` | `String` | Name recorded in the returned snapshot and used in debug lines. |
| `origin` | [`BlockPos`](../../../../../../../main/java/org/almond/minecraftgit/model/BlockPos.md) | Origin stored in and used to relativise the snapshot. |
| `positions` | `Collection<BlockPos>` | Absolute positions to read; deduplicated and sorted by `SnapshotService`. |

### Returns
`CompletableFuture<Snapshot>` — completes with the block/block-entity snapshot, or exceptionally with `UserFacingException` when the world or integrated-server level is unavailable.

---

## captureOnClientThread

### Signature
```java
private static <T> CompletableFuture<T> captureOnClientThread(Minecraft client,
                                                                  java.util.function.Supplier<T> task)
```

### Description
Ensures a client-side capture runs on the client thread: it returns a failed future when `client.level` is null (message "Open a world first."), runs the supplier inline when `client.isSameThread()` is already true, and otherwise submits it with `client.execute`, completing the future from the client thread. Any `Throwable` from the task is caught and used to complete the future exceptionally, so callers never see the exception thrown out of the command.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `client` | `Minecraft` | Used for the thread check, the level-presence check and `execute`. |
| `task` | `java.util.function.Supplier<T>` | Capture work to run on the client thread, normally a capture against `client.level` without chunk loading. |

### Returns
`<T> CompletableFuture<T>` — the task value, or a future completed exceptionally with the task's error.

---

## See Also

- **Software Detailed Design:** [WorldAccess_SDD.md](./WorldAccess_SDD.md)
