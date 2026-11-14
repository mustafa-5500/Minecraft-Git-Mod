# ApplyService

> **Software Detailed Documentation:** [ApplyService_SDD.md](./ApplyService_SDD.md)  
> **Source File:** [ApplyService.java](../../../../../../../../src/client/java/org/almond/minecraftgit/client/world/ApplyService.java)

## Table of Contents

- [Usage](#usage)
- [Fields](#fields)
- [ApplyService (Constructor)](#applyservice-constructor)
- [apply](#apply)
- [begin](#begin)
- [beginWrites](#beginwrites)
- [process](#process)
- [finish](#finish)
- [removeEntities](#removeentities)
- [placeEntities](#placeentities)
- [outOfRange](#outofrange)
- [warn](#warn)
- [BlockWrite](#blockwrite)
- [See Also](#see-also)

## Usage

Obtain the service from `GitServices.apply()` and call it on the client thread with a plan built by `ApplyPlan.between` or `ApplyPlan.restore`; it is deliberately single-player only, so a multiplayer call fails with `ApplyException`. The returned future completes on the integrated-server thread after the tick-batched job finishes, so command handlers marshal chat output back with `client.execute`.

```java
ApplyPlan plan = ApplyPlan.between(selection, current.localHead(), current.localOrigin(),
        current.target(), current.localOrigin(), true);
services.apply().apply(world, plan).whenComplete((result, error) -> client.execute(() -> {
    if (error != null) {
        services.messages().error(source, CommandSupport.unwrap(error).getMessage());
        return;
    }
    services.messages().success(source, "Applied: " + result.summary()
            + (result.backup() == null ? "" : " (backup " + result.backup().getFileName() + ")") + ".");
}));
```

Applies an `ApplyPlan` through the integrated server in tick batches.
This is deliberately single-player only: in multiplayer the server is
authoritative and a client-only mod cannot safely write blocks or entities.

---

## Fields

- `MAX_WARNINGS` (`int`) — Private constant `20`; caps the warning list carried by `ApplyResult` so a large broken apply cannot flood chat.
- `snapshots` ([`SnapshotService`](../../../../../../../main/java/org/almond/minecraftgit/world/SnapshotService.md)) — Captures the exact positions the apply will touch before any write, on the integrated-server thread.
- `backups` ([`BackupService`](../../../../../../../main/java/org/almond/minecraftgit/backup/BackupService.md)) — Persists that pre-apply snapshot as a gzipped file and returns the path reported in `ApplyResult.backup()`.
- `config` ([`ModConfig`](../../../../../../../main/java/org/almond/minecraftgit/config/ModConfig.md)) — Supplies `blocksPerTick`, the batch size used by the apply job.
- `debug` ([`DebugLogger`](../../../../../../../main/java/org/almond/minecraftgit/debug/DebugLogger.md)) — Receives one `block-write` line per successful placement or removal when block logging is enabled.
- `io` (`ExecutorService`) — The shared `minecraftgit-io` pool, used only for the blocking backup-file write so it never runs on the server thread.

---

## ApplyService (Constructor)

### Signature
```java
public ApplyService(SnapshotService snapshots, BackupService backups, ModConfig config,
                        DebugLogger debug, ExecutorService io)
```

### Description
Stores the injected collaborators and does not create threads, open files or touch the world. `GitServices` constructs it after `SnapshotService` and `BackupService`, sharing the same two-thread IO pool used for JGit and staging work.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `snapshots` | [`SnapshotService`](../../../../../../../main/java/org/almond/minecraftgit/world/SnapshotService.md) | Reads the pre-apply backup from the live world on the server thread. |
| `backups` | [`BackupService`](../../../../../../../main/java/org/almond/minecraftgit/backup/BackupService.md) | Writes and prunes the backup snapshot when `plan.createBackup()` is true. |
| `config` | [`ModConfig`](../../../../../../../main/java/org/almond/minecraftgit/config/ModConfig.md) | Read for `blocksPerTick` when the job starts. |
| `debug` | [`DebugLogger`](../../../../../../../main/java/org/almond/minecraftgit/debug/DebugLogger.md) | Optional per-block write audit. |
| `io` | `ExecutorService` | Executor for the blocking backup write; must not be the client or server thread. |

### Returns
`ApplyService` instance.

---

## apply

### Signature
```java
public CompletableFuture<ApplyResult> apply(WorldContext world, ApplyPlan plan)
```

### Description
Requires both `Minecraft.getSingleplayerServer()` and `world.serverLevel(client)`; otherwise it returns an already-failed future with `ApplyException` explaining that applying needs single-player while repository commands still work in multiplayer. On success it creates a future and submits `begin` to the integrated server via `server.execute`, returning immediately so the client thread never blocks. The future is completed by the job, normally on the server thread, with either `ApplyResult` or an `ApplyException`.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `world` | [`WorldContext`](../WorldContext.md) | Supplies the dimension whose `ServerLevel` is resolved; a multiplayer context fails the future. |
| `plan` | [`ApplyPlan`](../../../../../../../main/java/org/almond/minecraftgit/world/ApplyPlan.md) | Absolute placements, removals, entity changes and the backup flag to apply. |

### Returns
`CompletableFuture<ApplyResult>` — completes with the placement/removal/entity counts, backup path and warnings, or completes exceptionally with `ApplyException`.

---

## begin

### Signature
```java
private void begin(MinecraftServer server, ServerLevel level, WorldContext world, ApplyPlan plan,
                       CompletableFuture<ApplyResult> future)
```

### Description
Runs on the integrated-server thread and first refuses to start if `server.isRunning()` is false. When `plan.createBackup()` is false it goes straight to `beginWrites` with a null backup path; otherwise it captures the current blocks and block entities at `plan.allPositions()` with chunk loading enabled and stores that snapshot through `BackupService` on the IO executor. Completion is marshalled back to the server thread: a capture or write failure completes the future exceptionally with `ApplyException`, and success passes the backup path to `beginWrites`. Backup-before-apply is what `/git pull --abort` restores.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `server` | `MinecraftServer` | Integrated server used for the running check, `execute` hops and backup scheduling. |
| `level` | `ServerLevel` | Authoritative level whose positions are captured for the backup. |
| `world` | [`WorldContext`](../WorldContext.md) | Supplies the world id passed to `BackupService.store`. |
| `plan` | [`ApplyPlan`](../../../../../../../main/java/org/almond/minecraftgit/world/ApplyPlan.md) | Provides the position set, target origin and backup flag. |
| `future` | `CompletableFuture<ApplyResult>` | Completes with the result or an `ApplyException`; never completed on the IO thread. |

### Returns
`void`

---

## beginWrites

### Signature
```java
private void beginWrites(MinecraftServer server, ServerLevel level, ApplyPlan plan, Path backup,
                             CompletableFuture<ApplyResult> future)
```

### Description
Translates the plan into an ordered write list without touching the world. Every removal becomes an air `BlockWrite` flagged as a removal, but out-of-height removals are counted as skipped silently; every placement is range-checked with a warning, decoded through `BlockStateCodec`, warned if the block id is unknown (it then applies as air), and paired with its optional native block-entity payload. The list is sorted by Y, then X, then Z so a build grows bottom-up, and a new `Job` is created and `process()` is started on the server thread.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `server` | `MinecraftServer` | Used to schedule subsequent batches and checked for shutdown. |
| `level` | `ServerLevel` | Target level; build-height bounds come from it. |
| `plan` | [`ApplyPlan`](../../../../../../../main/java/org/almond/minecraftgit/world/ApplyPlan.md) | Source of removals, placements and entity changes. |
| `backup` | `Path` | Backup file path to report in the result, or null when no backup was requested. |
| `future` | `CompletableFuture<ApplyResult>` | Completed by `finish` after all writes and entity changes. |

### Returns
`void`

---

## process

### Signature
```java
private void process()
```

### Description
A single tick's batch: it first fails the future if the integrated server stopped, then writes up to `config.blocksPerTick` entries. A position whose block already equals the target is treated as changed without calling `setBlock`, but its block-entity NBT is still applied when one is present; otherwise it calls `setBlock(..., Block.UPDATE_CLIENTS)` and counts a failure (with warning) if the level rejects it. Successful writes increment `placed` or `removed`, emit `debug.blockWrite`, and block-entity payloads are loaded through `BlockEntityCodec.apply`, warning when the block has no tile entity or the payload format is unsupported. Every runtime exception is caught per write and counted as a failure. If entries remain it reschedules itself with `server.execute`, yielding to the server tick; otherwise it calls `finish`.

### Parameters
None.

### Returns
`void`

---

## finish

### Signature
```java
private void finish()
```

### Description
Runs entity work only after all block writes have been attempted: it removes old entities, places new ones, then completes the future with `ApplyResult(placed, removed, skipped + failed, entitiesPlaced, entitiesRemoved, backup, warnings)`. Failed block writes are therefore folded into the `skipped` count. It does not complete the future exceptionally for partial failures; those are reported as counts and warnings.

### Parameters
None.

### Returns
`void`

---

## removeEntities

### Signature
```java
private int removeEntities()
```

### Description
No-op returning 0 when there are no entity removals or the plan has no old entity origin. For each removed snapshot it computes the absolute position from `oldEntityOrigin + relative`, queries a ±1.5-block box for non-player entities of the same registry type, ignores candidates farther than 3 blocks (squared distance > 9) and calls `discard()`. Matching by type and proximity rather than UUID is what allows a moved build to clean up its old location.

### Parameters
None.

### Returns
`int` — number of entities discarded.

---

## placeEntities

### Signature
```java
private int placeEntities()
```

### Description
Creates each planned entity with `EntityCodec.apply(level, snapshot, x, y, z)` on the server level and counts non-null results. A runtime failure while loading one entity is warned and skipped, so the rest of the build still applies; failures are not added to the `skipped` block count.

### Parameters
None.

### Returns
`int` — number of entities successfully created.

---

## outOfRange

### Signature
```java
private static boolean outOfRange(ServerLevel level, BlockPos position)
```

### Description
Tests the position's Y against the level's current build range: true when below `getMinBuildHeight()` or at/above `getMaxBuildHeight()`. Such writes cannot be applied and are skipped rather than failing the job.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `level` | `ServerLevel` | Supplies the dimension's min/max build height. |
| `position` | [`BlockPos`](../../../../../../../main/java/org/almond/minecraftgit/model/BlockPos.md) | Absolute position to test. |

### Returns
`boolean` — `true` when the Y coordinate lies outside the level's build height.

---

## warn

### Signature
```java
private static void warn(List<String> warnings, String warning)
```

### Description
Appends a message only while the list holds fewer than `MAX_WARNINGS` (20) entries, silently dropping extras. The counters in `ApplyResult` remain authoritative, so truncation does not hide the number of failed or skipped writes.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `warnings` | `List<String>` | Mutable warning list shared by the job and result. |
| `warning` | `String` | Message to append when capacity remains. |

### Returns
`void`

---

## BlockWrite

### Signature
```java
private record BlockWrite(BlockPos position, BlockState state, String blockEntity, boolean removal)
```

### Description
A small private value record representing one queued world write, produced in `beginWrites` and consumed in `process`. Grouping the target state with the optional NBT payload and the removal flag keeps the batch loop free of block-state decoding and of repeated plan lookups.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `position` | [`BlockPos`](../../../../../../../main/java/org/almond/minecraftgit/model/BlockPos.md) | Absolute target position; also used for ordering and debug output. |
| `state` | `BlockState` | Decoded target state, or air for a removal. |
| `blockEntity` | `String` | Native `nbt:` payload to load after placement, or null for removals and blocks without data. |
| `removal` | `boolean` | Distinguishes a removal from a placement so counters, debug output and NBT handling differ. |

### Returns
`record` — a value record consumed by the apply loop.

---

## See Also

- **Software Detailed Design:** [ApplyService_SDD.md](./ApplyService_SDD.md)
