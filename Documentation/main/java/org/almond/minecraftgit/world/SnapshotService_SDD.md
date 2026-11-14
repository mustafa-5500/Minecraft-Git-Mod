# SnapshotService — Software Detailed Design

> **API Documentation:** [SnapshotService.md](./SnapshotService.md)  
> **Source File:** [SnapshotService.java](../../../../../../../src/main/java/org/almond/minecraftgit/world/SnapshotService.java)

## Table of Contents

- [Usage](#usage)
- [1. Overview](#1-overview)
- [2. Package Declaration & Imports](#2-package-declaration-imports)
- [3. Class Declaration](#3-class-declaration)
- [4. Instance Fields](#4-instance-fields)
- [5. Constructor](#5-constructor)
- [6. `capture()`](#6-capture)
- [7. `capture()`](#7-capture)
- [8. `capturePositions()`](#8-capturepositions)
- [9. `captureEntities()`](#9-captureentities)
- [10. `ensureLoaded()`](#10-ensureloaded)
- [11. `chunkKey()`](#11-chunkkey)
- [12. `warn()`](#12-warn)

## Usage

`GitServices` constructs one `SnapshotService(config, debug)` and exposes it as `services.snapshots()`. `WorldAccess` is the only production caller of `capture`: in single-player it submits `capture(selection, serverLevel, true)` to the integrated-server thread so missing chunks can be generated, while in multiplayer it runs `capture(selection, clientLevel, false)` on the client thread and accepts that only loaded chunks are visible. `ApplyService.begin` calls `capturePositions(..., true)` on the server thread for a pre-apply backup, while `StagingCommands` stages the captured snapshot and `StatusCommands` uses it as the working tree snapshot.

```java
// server thread, single-player
CaptureResult result = services.snapshots().capture(selection, serverLevel, true);
// server thread, pre-apply backup
Snapshot backup = services.snapshots().capturePositions(serverLevel, plan.selectionName(),
        plan.targetOrigin(), plan.allPositions(), true);
```

---

## 1. Overview

Reads blocks, block entities and entities from a `Level`. The caller
must run this on the owning thread: the client thread for a
`ClientLevel`, or the server thread for an integrated-server level.
Unloaded chunks are skipped and counted. When `loadChunks` is true
and the level is a `ServerLevel`, chunks are loaded on demand (used by
single-player staging/status/backups so a commit does not silently truncate).

---

## 2. Package Declaration & Imports

```java
package org.almond.minecraftgit.world;
```

```java
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import org.almond.minecraftgit.config.ModConfig;
import org.almond.minecraftgit.debug.DebugLogger;
import org.almond.minecraftgit.model.BlockPos;
import org.almond.minecraftgit.model.BlockSnapshot;
import org.almond.minecraftgit.model.Cuboid;
import org.almond.minecraftgit.model.EntitySnapshot;
import org.almond.minecraftgit.model.Selection;
import org.almond.minecraftgit.model.Snapshot;
import org.almond.minecraftgit.selection.SelectionException;
import org.almond.minecraftgit.serialization.BlockEntityCodec;
import org.almond.minecraftgit.serialization.BlockStateCodec;
import org.almond.minecraftgit.serialization.EntityCodec;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
```

| Import | Purpose |
|--------|---------|
| `ServerLevel` | Type-checked in `ensureLoaded` so chunks can be forced with `getChunk`. |
| `Entity` | Base type of the `getEntitiesOfClass` scan and the entities filtered as non-players. |
| `EntityType` | Produces the registry key string stored in `EntitySnapshot.type()`. |
| `Player` | Excluded from capture because player entities are not part of a build. |
| `Level` | Common read target for client and server levels. |
| `BlockEntity` | Tile entity fetched at each captured position before NBT encoding. |
| `AABB` | Converts an inclusive cuboid into a continuous box for entity intersection checks. |
| [`ModConfig`](../config/ModConfig.md) | Provides `maxBlocksPerCommit`, `maxEntitiesPerSelection` and `maxBlockEntityBytes`. |
| [`DebugLogger`](../debug/DebugLogger.md) | Receives per-block reads and the per-capture summary line. |
| [`BlockPos`](../model/BlockPos.md) | Absolute coordinates in the block map, the snapshot origin and the position collection. |
| [`BlockSnapshot`](../model/BlockSnapshot.md) | Immutable per-block record holding absolute and relative coordinates plus state/NBT. |
| [`Cuboid`](../model/Cuboid.md) | Inclusive regions iterated for blocks and converted to AABBs for entities. |
| [`EntitySnapshot`](../model/EntitySnapshot.md) | Immutable per-entity record holding position, rotation, type and payload. |
| [`Selection`](../model/Selection.md) | Supplies the selection name, origin and cuboids, and is validated before capture. |
| [`Snapshot`](../model/Snapshot.md) | The immutable result handed to staging, status, backups and commits. |
| [`SelectionException`](../selection/SelectionException.md) | Thrown for a missing origin, no cuboids, or a volume above the capture limit. |
| [`BlockEntityCodec`](../serialization/BlockEntityCodec.md) | Encodes tile-entity NBT to the `nbt:` SNBT payload and measures its UTF-8 bytes. |
| [`BlockStateCodec`](../serialization/BlockStateCodec.md) | Encodes a `BlockState` to the canonical `namespace:block[props]` repository string. |
| [`EntityCodec`](../serialization/EntityCodec.md) | Encodes an entity to SNBT and measures the payload against the byte cap. |
| `Instant` | Stamps `Snapshot.capturedAt()` with the capture time. |
| `ArrayList` | Builds the warning list, the capped entity list and the snapshot block list. |
| `Collection` | Parameter type of the exact positions captured by `capturePositions`. |
| `HashSet` | Tracks unloaded chunk keys, forced chunk keys and seen entity UUIDs. |
| `LinkedHashMap` | Keeps captured blocks in deterministic insertion order, including overlap deduplication. |
| `List` | Declares warnings, entities and snapshot block arguments. |
| `Map` | Type of the absolute-position-to-block table. |
| `Set` | Type of the unloaded/forced chunk-key sets and the seen-UUID set. |
| `TreeSet` | Sorts the positions passed to `capturePositions` so reads are deterministic. |
| `UUID` | Deduplicates entities that intersect more than one cuboid. |

---

## 3. Class Declaration

```java
public final class SnapshotService {
```

`SnapshotService` is an immutable `final` service: the only state is the injected `ModConfig` and `DebugLogger`, so a single instance can serve client and server captures as long as each call runs on the owning thread. It performs no locking and no caching; every capture builds fresh maps and sets, which keeps a long capture from retaining world data and makes repeated calls independent.

---

## 4. Instance Fields

```java
private static final int MAX_WARNINGS;
private final ModConfig config;
private final DebugLogger debug;
```

| Field | Type | Description |
|-------|------|-------------|
| `MAX_WARNINGS` | `int` | Constant `20`; the cap applied by `warn` so per-block or per-entity problems cannot make the result unbounded. |
| `config` | [`ModConfig`](../config/ModConfig.md) | Read on every capture for the volume, entity-count and byte limits; never cached, so a config reload takes effect on the next scan. |
| `debug` | [`DebugLogger`](../debug/DebugLogger.md) | Shared logger; `blockRead` calls are gated by its `blocks` flag and the summary is one `capture` line. |

---

## 5. Constructor

```java
public SnapshotService(ModConfig config, DebugLogger debug)
```

The constructor only assigns the two references; it has no IO, no thread checks and no validation. This matches the service-graph design where `GitServices` creates all services eagerly at client start and each capture method enforces its own preconditions and thread contract.

---

## 6. `capture()`

```java
public CaptureResult capture(Selection selection, Level level)
```

A forwarding overload: it calls `capture(selection, level, false)` and exists so callers that only have a `Level` do not need to pass a boolean. It therefore never forces chunks, which makes it safe for client-side status reads but unsuitable for single-player staging where unloaded chunks must be included; `WorldAccess` deliberately uses the three-argument form with `true` for that case.

---

## 7. `capture()`

```java
public CaptureResult capture(Selection selection, Level level, boolean loadChunks)
```

The main capture path runs entirely on the caller's thread and performs no synchronization. It first rejects, with `SelectionException`, a selection with no origin, one with no cuboids, or one whose volume exceeds `config.maxBlocksPerCommit`, so an oversized build never starts a world scan. It then walks `selection.cuboids()` in `y`, then `x`, then `z` order, skipping blocks outside `level.getMinBuildHeight()`/`getMaxBuildHeight()` and incrementing `skippedBlocks`. Chunk availability is decided by `ensureLoaded`: an unavailable chunk is recorded once in `unloadedChunks` and each of its blocks increments `skippedBlocks`. Because overlapping cuboids can revisit a position, an absolute `blocks` map entry causes an early continue.

For each new position the block state is encoded with `BlockStateCodec`, logged through `debug.blockRead`, and the block entity (if any) is encoded to a native `nbt:` payload. A payload larger than `config.maxBlockEntityBytes` is dropped with a warning and the block is stored without NBT; a runtime failure while reading NBT also only warns, so one broken tile entity cannot abort a commit. `BlockSnapshot.at` records the absolute position and the coordinates relative to `selection.origin()`.

Entities are captured afterwards by `captureEntities`; if the count exceeds `config.maxEntitiesPerSelection`, the sorted list is truncated and the remainder is reported as `skippedEntities`. A single warning is appended when unloaded chunks were skipped. The method then builds `Snapshot(name, origin, blocks, entities, Instant.now())`, logs a summary with block/entity/skip/chunk/byte counts, and returns `CaptureResult` carrying that snapshot and the diagnostics. Forcing chunks through `getChunk` only happens for a `ServerLevel` and blocks the calling thread while generation completes.

---

## 8. `capturePositions()`

```java
public Snapshot capturePositions(Level level, String selectionName, BlockPos origin,
                                     Collection<BlockPos> positions, boolean loadChunks)
```

Capture exactly the given positions, for a pre-apply backup. Entities are
not captured here; apply restores blocks and block entities.

The positions are copied into a `TreeSet`, which removes duplicates and gives deterministic read order; out-of-build-height and unloaded positions are silently skipped rather than counted because a backup covers only what apply can write. Chunk forcing reuses `ensureLoaded` with `loadChunks`, so a single-player backup can read positions in chunks the player is not currently near. Block entities are best-effort: only payloads within `config.maxBlockEntityBytes` are kept and a runtime encoding failure is ignored, leaving the block state intact. The returned `Snapshot` has the supplied selection name and origin, no entities, and a fresh capture instant.

---

## 9. `captureEntities()`

```java
private List<EntitySnapshot> captureEntities(Level level, Selection selection, BlockPos origin,
                                                 List<String> warnings)
```

Each cuboid is inflated into an `AABB` from `min` to `max + 1.0` so block coordinates become continuous positions, then `level.getEntitiesOfClass(Entity.class, box, e -> !(e instanceof Player))` returns candidates. A `seen` UUID set and `isRemoved()` check suppress duplicates and stale entities, and the candidate's own bounding box must intersect the box because the query is broad. The entity is serialized with `EntityCodec`; the payload is compared to `config.maxBlockEntityBytes` (the same cap used for block entities) and an oversized entity is warned and skipped. Accepted entities retain absolute `x/y/z`, relative `rx/ry/rz` computed against `origin`, yaw and pitch, the registry type key and the SNBT payload, and the list is sorted with `EntitySnapshot.ORDER`. A runtime serialization failure is warned and only that entity is lost.

---

## 10. `ensureLoaded()`

```java
private static boolean ensureLoaded(Level level, int x, int z, boolean loadChunks, Set<Long> loadedChunks)
```

The helper first decides whether loading is both requested and possible: only a `ServerLevel` exposes `getChunk`, and `loadedChunks` ensures the call happens once per chunk for the whole capture instead of once per block. It then returns `level.isLoaded(...)` at the minimum build height, which reports the actual chunk state in both client and server levels. The blocking `getChunk` call means the method inherits the server-thread requirement; calling it for a client level simply reports the chunk as unloaded when it is not resident.

---

## 11. `chunkKey()`

```java
private static long chunkKey(int chunkX, int chunkZ)
```

Packs the two 32-bit chunk coordinates into one 64-bit value by shifting `chunkX` left 32 and XOR-ing the unsigned low bits of `chunkZ`. Within the set it guarantees a unique key per chunk coordinate and avoids allocating a record for each key; the value is never persisted or compared for ordering.

---

## 12. `warn()`

```java
private static void warn(List<String> warnings, String warning)
```

A capacity-checked `add`: while the list has fewer than `MAX_WARNINGS` entries the message is appended, otherwise it is dropped. `CaptureResult` still exposes the independent `skippedBlocks`/`skippedEntities` counters and `partial()`, so truncating the human-readable list does not hide that the capture was incomplete.

---
