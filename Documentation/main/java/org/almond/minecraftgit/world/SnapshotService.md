# SnapshotService

> **Software Detailed Documentation:** [SnapshotService_SDD.md](./SnapshotService_SDD.md)  
> **Source File:** [SnapshotService.java](../../../../../../../src/main/java/org/almond/minecraftgit/world/SnapshotService.java)

## Table of Contents

- [Usage](#usage)
- [Fields](#fields)
- [SnapshotService (Constructor)](#snapshotservice-constructor)
- [capture](#capture)
- [capturePositions](#capturepositions)
- [captureEntities](#captureentities)
- [ensureLoaded](#ensureloaded)
- [chunkKey](#chunkkey)
- [warn](#warn)
- [See Also](#see-also)

## Usage

Construct one service in the service graph (`new SnapshotService(config, debug)`) and call `capture` from the thread that owns the level: `WorldAccess` submits it to the integrated-server thread with `loadChunks=true` in single-player and runs it on the client thread with `loadChunks=false` in multiplayer. `capturePositions` reads only the positions an apply will touch and is used to build a pre-apply backup; it never captures entities.

```java
SnapshotService snapshots = new SnapshotService(config, debug);
CaptureResult result = snapshots.capture(selection, serverLevel, true);
if (result.partial()) {
    for (int i = 0; i < Math.min(5, result.warnings().size()); i++) {
        services.messages().warn(source, result.warnings().get(i));
    }
}
Snapshot backup = snapshots.capturePositions(serverLevel, selection.name(),
        selection.origin(), plan.allPositions(), true);
```

Reads blocks, block entities and entities from a `Level`. The caller
must run this on the owning thread: the client thread for a
`ClientLevel`, or the server thread for an integrated-server level.
Unloaded chunks are skipped and counted. When `loadChunks` is true
and the level is a `ServerLevel`, chunks are loaded on demand (used by
single-player staging/status/backups so a commit does not silently truncate).

---

## Fields

- `MAX_WARNINGS` (`int`) — Private constant `20`; caps the warning list so a badly broken selection cannot flood chat while the important skip counts still reach `CaptureResult`.
- `config` ([`ModConfig`](../config/ModConfig.md)) — Supplies the hard limits checked during capture: `maxBlocksPerCommit` for selection volume, `maxEntitiesPerSelection` for the entity cap and `maxBlockEntityBytes` for block-entity and entity payloads.
- `debug` ([`DebugLogger`](../debug/DebugLogger.md)) — Optional debug sink; records each decoded block through `blockRead` and writes one `capture` summary line with block, entity, skip and byte counts.

---

## SnapshotService (Constructor)

### Signature
```java
public SnapshotService(ModConfig config, DebugLogger debug)
```

### Description
Stores the config and debug logger references; it allocates no buffers, opens no files and takes no thread affinity. Callers are responsible for invoking the capture methods on the level's owning thread.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `config` | [`ModConfig`](../config/ModConfig.md) | Read at capture time for volume, entity-count and payload-byte limits. |
| `debug` | [`DebugLogger`](../debug/DebugLogger.md) | Receives per-block read lines (when block logging is on) and the capture summary. |

### Returns
`SnapshotService` instance.

---

## capture

### Signature
```java
public CaptureResult capture(Selection selection, Level level)
```

### Description
Convenience overload that delegates to `capture(selection, level, false)`, so it never loads chunks even when `level` is a `ServerLevel`; it is suitable for client-side calls where only already-loaded chunks should be read. Validation is identical: a missing origin, zero cuboids or a volume above `config.maxBlocksPerCommit` throws `SelectionException` before any world read.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `selection` | [`Selection`](../model/Selection.md) | Source of the name, origin and inclusive cuboids to capture; must have an origin and at least one cuboid. |
| `level` | `Level` | Level to read; the caller must run on the owning thread (client thread, or integrated-server thread for a `ServerLevel`). |

### Returns
[`CaptureResult`](CaptureResult.md) — the snapshot plus counts of skipped chunks, blocks and entities, and a capped warning list.

---

## capture

### Signature
```java
public CaptureResult capture(Selection selection, Level level, boolean loadChunks)
```

### Description
Captures every block and block entity inside the selection's cuboids and every non-player entity intersecting them, deduplicating overlapping cuboids and returning a snapshot measured from `selection.origin()`. Blocks outside the build height are skipped and counted; chunks not loaded are skipped and counted once, unless `loadChunks` is true and the level is a `ServerLevel`, in which case `ServerLevel.getChunk` forces them in. Block entities whose SNBT payload exceeds `config.maxBlockEntityBytes` are stored without their payload and warned, entities are capped at `config.maxEntitiesPerSelection`, and broken payloads become warnings rather than failures.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `selection` | [`Selection`](../model/Selection.md) | Named, multi-cuboid selection; its origin becomes the snapshot origin and its cuboids are walked inclusively. |
| `level` | `Level` | Owning level; a `ServerLevel` allows on-demand chunk loading, a client level cannot. |
| `loadChunks` | `boolean` | When true and `level` is a `ServerLevel`, loads each missing chunk through `getChunk` before reading it; ignored for client levels. |

### Returns
[`CaptureResult`](CaptureResult.md) — the immutable snapshot, `skippedChunks`, `skippedBlocks`, `skippedEntities` and the capped warning list.

---

## capturePositions

### Signature
```java
public Snapshot capturePositions(Level level, String selectionName, BlockPos origin,
                                     Collection<BlockPos> positions, boolean loadChunks)
```

### Description
Capture exactly the given positions, for a pre-apply backup. Entities are
not captured here; apply restores blocks and block entities.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `level` | `Level` | Level to read on its owning thread; only a `ServerLevel` can load chunks. |
| `selectionName` | `String` | Name recorded in the snapshot and used in per-block debug lines. |
| `origin` | [`BlockPos`](../model/BlockPos.md) | Origin stored in the snapshot and used to derive relative coordinates. |
| `positions` | `Collection<BlockPos>` | Absolute positions to read; duplicates are collapsed and the set is traversed in sorted order. |
| `loadChunks` | `boolean` | When true and `level` is a `ServerLevel`, loads missing chunks before reading each position. |

### Returns
[`Snapshot`](../model/Snapshot.md) — blocks and block entities at the readable, in-height positions, with an empty entity list.

---

## captureEntities

### Signature
```java
private List<EntitySnapshot> captureEntities(Level level, Selection selection, BlockPos origin,
                                                 List<String> warnings)
```

### Description
Scans each cuboid as a continuous `AABB` (maximum corner plus one) for non-player entities whose bounding box intersects it, skipping removed entities and duplicate UUIDs across overlapping cuboids. Each entity is serialized with `EntityCodec`; payloads above `config.maxBlockEntityBytes` are skipped with a warning, while serialization failures warn and continue. The result stores absolute and origin-relative doubles plus yaw, pitch, type id and SNBT payload, sorted by `EntitySnapshot.ORDER`.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `level` | `Level` | Level queried with `getEntitiesOfClass`; must be read on its owning thread. |
| `selection` | [`Selection`](../model/Selection.md) | Provides the cuboids that define the scan boxes. |
| `origin` | [`BlockPos`](../model/BlockPos.md) | Reference point subtracted to fill `rx`, `ry` and `rz`. |
| `warnings` | `List<String>` | Mutable warning sink, already subject to `MAX_WARNINGS`. |

### Returns
`List<EntitySnapshot>` — the capturable non-player entities, sorted by Y, then X, then Z.

---

## ensureLoaded

### Signature
```java
private static boolean ensureLoaded(Level level, int x, int z, boolean loadChunks, Set<Long> loadedChunks)
```

### Description
Returns whether the chunk containing `(x, z)` is loaded, optionally forcing it first: when `loadChunks` is true and `level` is a `ServerLevel`, it calls `getChunk` once per chunk key recorded in `loadedChunks`. The final check uses `level.isLoaded` at the minimum build height, so it is a pure loaded-state query. Forcing a chunk performs generation/IO and must therefore run on the integrated-server thread.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `level` | `Level` | Level whose chunks are checked; only `ServerLevel` supports forcing. |
| `x` | `int` | Absolute block X whose chunk is required. |
| `z` | `int` | Absolute block Z whose chunk is required. |
| `loadChunks` | `boolean` | Whether on-demand loading is permitted. |
| `loadedChunks` | `Set<Long>` | Per-capture memo of chunk keys already forced, so `getChunk` is called once per chunk. |

### Returns
`boolean` — `true` when the chunk is loaded after the optional load attempt, `false` when it must be skipped.

---

## chunkKey

### Signature
```java
private static long chunkKey(int chunkX, int chunkZ)
```

### Description
Packs a chunk coordinate pair into one `long` by shifting `chunkX` into the high 32 bits and XOR-ing the masked `chunkZ` into the low bits. The value is only used as an identity key in `HashSet`; it is not persisted or ordered.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `chunkX` | `int` | Chunk X coordinate (block X shifted right by 4). |
| `chunkZ` | `int` | Chunk Z coordinate (block Z shifted right by 4). |

### Returns
`long` — a collision-free key for the chunk within the map's use.

---

## warn

### Signature
```java
private static void warn(List<String> warnings, String warning)
```

### Description
Appends a warning only while the list holds fewer than `MAX_WARNINGS` (20) entries; extra warnings are silently dropped, while `CaptureResult` still carries the numeric skip counts. This keeps a large broken selection from producing an unbounded result and chat spam.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `warnings` | `List<String>` | Mutable result warning list shared by the capture helpers. |
| `warning` | `String` | Message to append when capacity remains. |

### Returns
`void`

---

## See Also

- **Software Detailed Design:** [SnapshotService_SDD.md](./SnapshotService_SDD.md)
