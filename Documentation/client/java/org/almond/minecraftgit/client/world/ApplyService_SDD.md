# ApplyService — Software Detailed Design

> **API Documentation:** [ApplyService.md](./ApplyService.md)  
> **Source File:** [ApplyService.java](../../../../../../../../src/client/java/org/almond/minecraftgit/client/world/ApplyService.java)

## Table of Contents

- [Usage](#usage)
- [1. Overview](#1-overview)
- [2. Package Declaration & Imports](#2-package-declaration-imports)
- [3. Class Declaration](#3-class-declaration)
- [4. Instance Fields](#4-instance-fields)
- [5. Constructor](#5-constructor)
- [6. `apply()`](#6-apply)
- [7. `begin()`](#7-begin)
- [8. `beginWrites()`](#8-beginwrites)
- [9. `process()`](#9-process)
- [10. `finish()`](#10-finish)
- [11. `removeEntities()`](#11-removeentities)
- [12. `placeEntities()`](#12-placeentities)
- [13. `outOfRange()`](#13-outofrange)
- [14. `warn()`](#14-warn)
- [15. `BlockWrite()`](#15-blockwrite)

## Usage

`GitServices` creates one `ApplyService(snapshots, backups, config, debug, io)` and exposes it as `services.apply()`. `/git pull`, `/git checkout` and `/git pull --abort` all build an `ApplyPlan` on the client thread and call `apply`, then marshal the completion back to the client thread for chat output. Because the plan may be large, the world writes are split into `config.blocksPerTick` batches, and a backup is captured and stored before the first block is changed.

```java
ApplyPlan plan = ApplyPlan.between(selection, localHead, selection.origin(), target,
        selection.origin(), true);
services.apply().apply(world, plan).whenComplete((result, error) -> client.execute(() -> {
    if (error != null) {
        services.messages().error(source, CommandSupport.unwrap(error).getMessage());
    } else {
        services.messages().success(source, "Applied: " + result.summary());
    }
}));
```

---

## 1. Overview

Applies an `ApplyPlan` through the integrated server in tick batches.
This is deliberately single-player only: in multiplayer the server is
authoritative and a client-only mod cannot safely write blocks or entities.

---

## 2. Package Declaration & Imports

```java
package org.almond.minecraftgit.client.world;
```

```java
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.almond.minecraftgit.backup.BackupService;
import org.almond.minecraftgit.client.WorldContext;
import org.almond.minecraftgit.config.ModConfig;
import org.almond.minecraftgit.debug.DebugLogger;
import org.almond.minecraftgit.model.BlockPos;
import org.almond.minecraftgit.model.BlockSnapshot;
import org.almond.minecraftgit.model.EntitySnapshot;
import org.almond.minecraftgit.model.Snapshot;
import org.almond.minecraftgit.serialization.BlockEntityCodec;
import org.almond.minecraftgit.serialization.BlockStateCodec;
import org.almond.minecraftgit.serialization.EntityCodec;
import org.almond.minecraftgit.world.ApplyException;
import org.almond.minecraftgit.world.ApplyPlan;
import org.almond.minecraftgit.world.ApplyResult;
import org.almond.minecraftgit.world.SnapshotService;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
```

| Import | Purpose |
|--------|---------|
| `Minecraft` | Client singleton used to reach `getSingleplayerServer()` and to detect the integrated-server environment. |
| `MinecraftServer` | Provides `execute` hops onto the server thread and the `isRunning` lifecycle checks between batches. |
| `ServerLevel` | Authoritative level that receives the block and entity writes. |
| `Entity` | Query base for locating entities to discard by type and proximity. |
| `EntityType` | Supplies the registry key compared against `EntitySnapshot.type()`. |
| `Player` | Excluded from removal so an apply can never discard a player. |
| `Block` | Provides `Block.UPDATE_CLIENTS` for `setBlock`. |
| `BlockEntity` | Fetched after placement so the native NBT payload can be loaded into the existing tile entity. |
| `BlockState` | Target state type stored in `BlockWrite` and passed to `setBlock`. |
| `AABB` | The ±1.5-block search box used to find an entity to remove. |
| [`BackupService`](../../../../../../../main/java/org/almond/minecraftgit/backup/BackupService.md) | Stores the pre-apply snapshot off-thread and returns the file path reported to the player. |
| [`WorldContext`](../WorldContext.md) | Supplies the dimension, the world id for the backup folder and the single-player state. |
| [`ModConfig`](../../../../../../../main/java/org/almond/minecraftgit/config/ModConfig.md) | Read for `blocksPerTick`, which controls the batch size. |
| [`DebugLogger`](../../../../../../../main/java/org/almond/minecraftgit/debug/DebugLogger.md) | Optional `block-write` audit of every successful placement or removal. |
| [`BlockPos`](../../../../../../../main/java/org/almond/minecraftgit/model/BlockPos.md) | Absolute coordinates in the write list and backup position set. |
| [`BlockSnapshot`](../../../../../../../main/java/org/almond/minecraftgit/model/BlockSnapshot.md) | Carries the target block state and optional block-entity payload for a placement. |
| [`EntitySnapshot`](../../../../../../../main/java/org/almond/minecraftgit/model/EntitySnapshot.md) | Describes entities to remove (with relative position) and to place (with NBT and rotation). |
| [`Snapshot`](../../../../../../../main/java/org/almond/minecraftgit/model/Snapshot.md) | The pre-apply backup capture passed to `BackupService.store`. |
| [`BlockEntityCodec`](../../../../../../../main/java/org/almond/minecraftgit/serialization/BlockEntityCodec.md) | Applies a native `nbt:` payload to a placed block entity and reports unsupported payloads. |
| [`BlockStateCodec`](../../../../../../../main/java/org/almond/minecraftgit/serialization/BlockStateCodec.md) | Checks whether a state id is known and decodes it into a `BlockState` (unknown ids become air). |
| [`EntityCodec`](../../../../../../../main/java/org/almond/minecraftgit/serialization/EntityCodec.md) | Creates and adds an entity from its snapshot at an absolute position. |
| [`ApplyException`](../../../../../../../main/java/org/almond/minecraftgit/world/ApplyException.md) | Chat-safe failure type used to complete the future for missing single-player, a stopping or stopped server, or backup errors. |
| [`ApplyPlan`](../../../../../../../main/java/org/almond/minecraftgit/world/ApplyPlan.md) | Input describing placements, removals, entity changes and whether a backup is required. |
| [`ApplyResult`](../../../../../../../main/java/org/almond/minecraftgit/world/ApplyResult.md) | Immutable outcome with placed/removed/skipped/entity counts, backup path and warnings. |
| [`SnapshotService`](../../../../../../../main/java/org/almond/minecraftgit/world/SnapshotService.md) | Captures the block and block-entity state at every planned position for the backup. |
| `Path` | Type of the backup file path carried from `BackupService` into `ApplyResult`. |
| `ArrayList` | Builds the mutable write list and warning list. |
| `Comparator` | Sorts writes by Y, then X, then Z before batching. |
| `List` | Declares the write and warning collections. |
| `CompletableFuture` | The asynchronous result contract exposed to command handlers. |
| `ExecutorService` | The `minecraftgit-io` pool used only for the blocking backup write. |

---

## 3. Class Declaration

```java
public final class ApplyService {
```

`ApplyService` is a `final` coordinator with no superclass: it owns the collaborators but not the threads, delegating server scheduling to `MinecraftServer.execute` and file IO to the injected `ExecutorService`. Its only mutable state lives in the private inner `Job`, so each `apply` call has an independent write cursor and counters. Thread ownership is explicit: `apply` runs on the client thread, `begin`/`beginWrites`/`process`/`finish` run on the integrated-server thread, and only `BackupService.store` runs on an IO thread before hopping back.

---

## 4. Instance Fields

```java
private static final int MAX_WARNINGS;
private final SnapshotService snapshots;
private final BackupService backups;
private final ModConfig config;
private final DebugLogger debug;
private final ExecutorService io;
```

| Field | Type | Description |
|-------|------|-------------|
| `MAX_WARNINGS` | `int` | Constant `20`; the cap enforced by `warn` for the result's warning list. |
| `snapshots` | [`SnapshotService`](../../../../../../../main/java/org/almond/minecraftgit/world/SnapshotService.md) | Used in `begin` to capture `plan.allPositions()` with chunk loading before writing. |
| `backups` | [`BackupService`](../../../../../../../main/java/org/almond/minecraftgit/backup/BackupService.md) | Persists the backup and prunes old files; only called when the plan requests a backup. |
| `config` | [`ModConfig`](../../../../../../../main/java/org/almond/minecraftgit/config/ModConfig.md) | Supplies `blocksPerTick`, read in `process` to bound each batch. |
| `debug` | [`DebugLogger`](../../../../../../../main/java/org/almond/minecraftgit/debug/DebugLogger.md) | Receives `blockWrite` calls from the server thread; disabled by default. |
| `io` | `ExecutorService` | Shared daemon pool; used for `BackupService.store` so the server thread is never blocked on disk. |

---

## 5. Constructor

```java
public ApplyService(SnapshotService snapshots, BackupService backups, ModConfig config,
                        DebugLogger debug, ExecutorService io)
```

Pure dependency injection: it stores all five references and creates nothing. `GitServices` calls it after constructing `SnapshotService`, `BackupService` and the two-thread IO pool, so the apply service shares the same executor as staging and JGit work and inherits their daemon-thread shutdown behaviour.

---

## 6. `apply()`

```java
public CompletableFuture<ApplyResult> apply(WorldContext world, ApplyPlan plan)
```

The method enforces the single-player-only constraint by resolving `Minecraft.getSingleplayerServer()` and `world.serverLevel(client)`; if either is null it returns `CompletableFuture.failedFuture(new ApplyException(...))` rather than throwing, so every caller handles the same future shape. It does not check `world.singleplayer()` directly because the presence of both objects is the actual capability test. It then allocates the result future and calls `server.execute(() -> begin(server, level, world, plan, future))`, which transfers ownership to the integrated-server thread; the method itself performs no world access and returns before any block is written.

---

## 7. `begin()`

```java
private void begin(MinecraftServer server, ServerLevel level, WorldContext world, ApplyPlan plan,
                       CompletableFuture<ApplyResult> future)
```

This is the backup-before-apply phase, running on the server thread, and it re-checks `server.isRunning()` because the world may have begun closing between `apply` and the scheduled task. When `plan.createBackup()` is false it skips straight to `beginWrites` with a null path. Otherwise it calls `snapshots.capturePositions(level, plan.selectionName(), plan.targetOrigin(), plan.allPositions(), true)` so the backup covers every coordinate the apply can change, including chunks that need forcing; a runtime failure is wrapped in `ApplyException` and the future fails without writing anything. The snapshot is then written by `CompletableFuture.supplyAsync(..., io)` and the completion callback re-enters the server thread with `server.execute`; this keeps disk IO off the server thread while ensuring `beginWrites` runs on the server thread. A write failure also fails the future, and success passes the stored path into `beginWrites`.

---

## 8. `beginWrites()`

```java
private void beginWrites(MinecraftServer server, ServerLevel level, ApplyPlan plan, Path backup,
                             CompletableFuture<ApplyResult> future)
```

This method only builds the job's write list; no world mutation occurs here. Removals are appended first as air writes with `removal=true`, and out-of-height removals simply increment `skipped` without a warning because a removal outside the world is a no-op. Placements are then checked with `outOfRange` (skip plus a warning naming the min/max build height), warned when `BlockStateCodec.isKnown` is false, decoded with `BlockStateCodec.decode` (unknown state becomes air), and stored with the raw `blockEntity` payload so NBT is applied after the block exists. The combined list is sorted stably by Y, then X, then Z, which writes a build bottom-up and gives deterministic debug output; finally a new `Job` is created and `process()` is invoked, still on the server thread.

---

## 9. `process()`

```java
private void process()
```

One invocation is one tick's slice: it checks `server.isRunning()` first and completes exceptionally if the world is closing, then loops while `index < writes.size()` and fewer than `config.blocksPerTick` entries have been processed. For each entry it compares `level.getBlockState(mcPos)` with the target so an already-correct block does not trigger `setBlock`, but the comparison result is OR-ed into `changed`, meaning a "current" block still counts as a success and still receives its block-entity NBT. A rejected `setBlock` or any `RuntimeException` increments `failed` and records a warning without aborting the batch. Successful non-removals with a payload fetch the existing `BlockEntity`, warn when none exists or `BlockEntityCodec.apply` returns false (unsupported plugin payload), and successful writes increment `placed`/`removed` and emit `debug.blockWrite`. If work remains it calls `server.execute(this::process)` rather than looping, so the server can tick between batches; otherwise it calls `finish`.

---

## 10. `finish()`

```java
private void finish()
```

Called exactly once on the server thread after the last block write. It performs entity removals before entity placements, then completes the future with `ApplyResult(placed, removed, skipped + failed, entitiesPlaced, entitiesRemoved, backup, warnings)`; block-write failures and range/unknown-state skips are therefore merged into the single `skipped` counter. Partial failures are reported through counts and warnings rather than an exceptional completion, so a mostly successful apply still returns a result the command can act on while surfacing the problems.

---

## 11. `removeEntities()`

```java
private int removeEntities()
```

Returns 0 immediately when `plan.entityRemovals()` is empty or `plan.oldEntityOrigin()` is null (for example a fresh apply with no old build). For each snapshot it computes `oldOrigin + relative` and searches a ±1.5-block `AABB` for non-player entities whose registry type key equals `snapshot.type()`, skipping candidates with `distanceToSqr > 9.0` so only entities within three blocks are discarded. Matching by type and proximity instead of UUID is deliberate: the old entity may have been recreated or have a different UUID after a world reload, but a moved build still needs its old copy cleaned up.

---

## 12. `placeEntities()`

```java
private int placeEntities()
```

Iterates `plan.entityPlacements()`, delegates creation to `EntityCodec.apply(level, placement.snapshot(), placement.x(), placement.y(), placement.z())` and counts a success only when a non-null entity is returned. A `RuntimeException` while restoring one entity is caught, warned with the entity type, and not counted, so one malformed payload does not prevent the remaining entities from being placed. It runs after block placement so entities land in a finished build.

---

## 13. `outOfRange()`

```java
private static boolean outOfRange(ServerLevel level, BlockPos position)
```

A bounds predicate used while building the write list: it returns true when `position.y()` is below `level.getMinBuildHeight()` or at/above `level.getMaxBuildHeight()`, the two values the warning text also prints. Coordinates outside that range cannot be written by `setBlock` and are excluded before the job starts.

---

## 14. `warn()`

```java
private static void warn(List<String> warnings, String warning)
```

Appends only while the list size is below `MAX_WARNINGS`, silencing the rest. Because the numeric counters are independent, dropping messages never hides that writes failed or were skipped; it only bounds the chat lines shown after an apply.

---

## 15. `BlockWrite()`

```java
private record BlockWrite(BlockPos position, BlockState state, String blockEntity, boolean removal)
```

A private immutable value record produced by `beginWrites` and consumed by `process`. It pre-decodes and pre-classifies each operation so the hot batch loop performs only state comparison, `setBlock`, optional NBT application and counter updates; the `removal` flag selects the air-write path and controls whether the NBT branch and `placed`/`removed` counter are used.

---
