# Snapshot

> **Software Detailed Documentation:** [Snapshot_SDD.md](./Snapshot_SDD.md)  
> **Source File:** [Snapshot.java](../../../../../../../src/main/java/org/almond/minecraftgit/model/Snapshot.java)

## Table of Contents

- [Usage](#usage)
- [Fields](#fields)
- [Snapshot (Constructor)](#snapshot-constructor)
- [empty](#empty)
- [selection](#selection)
- [origin](#origin)
- [blocks](#blocks)
- [entities](#entities)
- [capturedAt](#capturedat)
- [blockCount](#blockcount)
- [entityCount](#entitycount)
- [isEmpty](#isempty)
- [rebase](#rebase)
- [retain](#retain)
- [byRelative](#byrelative)
- [byAbsolute](#byabsolute)
- [volume](#volume)
- [dimensions](#dimensions)
- [toString](#tostring)
- [See Also](#see-also)

## Usage

`Snapshot` is the immutable in-memory form of one selection captured at a point in time. Services build it directly or with `Snapshot.empty` when there is no committed state, then pass it to staging, commit and status code; `rebase` and `retain` return new snapshots instead of mutating the receiver.

```java
BlockPos origin = new BlockPos(100, 64, -20);
Snapshot captured = new Snapshot("castle", origin,
        List.of(BlockSnapshot.at(new BlockPos(100, 64, -20), origin, "minecraft:stone", null)),
        List.of(), Instant.now());

Snapshot rebased = captured.rebase(new BlockPos(0, 64, 0));
Map<BlockPos, BlockSnapshot> gitCoordinates = rebased.byRelative();
```

Immutable capture of one selection at one moment. Blocks and entities carry
coordinates relative to `.origin()` so the repository representation is
portable between worlds.

---

## Fields

- `selection` (`String`) — Name of the selection the snapshot was captured from; it is informational, not validated, and may be `null`. `retain` replaces it with the supplied selection's name.
- `origin` ([`BlockPos`](BlockPos.md)) — Absolute world position from which all relative coordinates are measured. It is never `null`: the constructor rejects a `null` origin, and `rebase` installs a new origin without changing absolute block positions.
- `blocks` (`List<BlockSnapshot>`) — Defensively copied, null-filtered list of tracked block coordinates, sorted by `BlockSnapshot.ORDER` (Y, then X, then Z) for reproducible output. It is exposed read-only through `blocks()`; tracked air is kept as a `minecraft:air` entry while untracked coordinates are simply absent.
- `entities` (`List<EntitySnapshot>`) — Defensively copied, null-filtered list of captured entities, sorted by `EntitySnapshot.ORDER` (Y, then X, then Z). It is exposed read-only through `entities()`; each entry keeps absolute and origin-relative doubles plus yaw, pitch, registry type and serialized data.
- `capturedAt` (`Instant`) — Capture timestamp kept as informational metadata; `null` becomes `Instant.now()` in the constructor. It is preserved unchanged by `rebase` and `retain` and is not used for ordering or equality.

---

## Snapshot (Constructor)

### Signature
```java
public Snapshot(String selection, BlockPos origin, List<BlockSnapshot> blocks,
                    List<EntitySnapshot> entities, Instant capturedAt)
```

### Description
Builds an immutable snapshot from the supplied blocks and entities, validating the origin and normalising both lists. The lists are defensively copied, `null` elements are removed, and each list is sorted with its canonical comparator; a `null` list is treated as empty and a `null` timestamp is replaced with `Instant.now()`.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `selection` | `String` | Selection name to record; informational, may be `null`. |
| `origin` | [`BlockPos`](BlockPos.md) | Absolute origin for relative coordinates; must not be `null` or an `IllegalArgumentException` is thrown. |
| `blocks` | `List<BlockSnapshot>` | Tracked blocks to capture; copied, null-filtered and sorted, with `null` treated as an empty list. |
| `entities` | `List<EntitySnapshot>` | Entities to capture; copied, null-filtered and sorted, with `null` treated as an empty list. |
| `capturedAt` | `Instant` | Capture timestamp; `null` is replaced by `Instant.now()`. |

### Returns
`Snapshot` instance.

---

## empty

### Signature
```java
public static Snapshot empty(String selection, BlockPos origin)
```

### Description
Creates a snapshot with no blocks and no entities, used as the base when there is no committed or staged state to compare against. Diffing against it reports every coordinate in the other snapshot as added.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `selection` | `String` | Selection name to record on the empty snapshot. |
| `origin` | [`BlockPos`](BlockPos.md) | Absolute origin for the empty snapshot; must not be `null`. |

### Returns
`Snapshot` — an empty snapshot with the given name and origin, no blocks or entities, and a fresh `Instant.now()` timestamp.

---

## selection

### Signature
```java
public String selection()
```

### Description
Returns the recorded selection name exactly as it was supplied to the constructor (or `selection.name()` after `retain`). The value is not validated and may be `null`.

### Parameters
None.

### Returns
`String` — recorded selection name, possibly `null`.

---

## origin

### Signature
```java
public BlockPos origin()
```

### Description
Returns the absolute world position that the snapshot's relative coordinates are measured from. `rebase` changes this basis while keeping absolute positions fixed, so callers should convert with `BlockPos.add`/`subtract` rather than assuming a fixed origin.

### Parameters
None.

### Returns
[`BlockPos`](BlockPos.md) — non-null absolute origin for the snapshot's relative coordinates.

---

## blocks

### Signature
```java
public List<BlockSnapshot> blocks()
```

### Description
Returns the tracked blocks in canonical `BlockSnapshot.ORDER` (Y, then X, then Z). The result is an unmodifiable view of the internal list and the block records are immutable, so callers cannot mutate the snapshot through it.

### Parameters
None.

### Returns
`List<BlockSnapshot>` — unmodifiable, sorted list of tracked blocks; empty when nothing was tracked.

---

## entities

### Signature
```java
public List<EntitySnapshot> entities()
```

### Description
Returns the captured entities in canonical `EntitySnapshot.ORDER` (Y, then X, then Z). The result is an unmodifiable view of the internal list; each entry carries absolute and relative doubles so apply can reposition the entity at another origin.

### Parameters
None.

### Returns
`List<EntitySnapshot>` — unmodifiable, sorted list of captured entities; empty when none were captured.

---

## capturedAt

### Signature
```java
public Instant capturedAt()
```

### Description
Returns the capture instant recorded at construction, defaulting to the construction time when none was supplied. It is informational metadata that is carried unchanged through `rebase` and `retain`; ordering and equality use the snapshot contents instead.

### Parameters
None.

### Returns
`Instant` — non-null capture timestamp.

---

## blockCount

### Signature
```java
public int blockCount()
```

### Description
Returns the number of tracked block entries (`blocks.size()`), including entries that record `minecraft:air`. It is not the geometric volume of the selection's cuboids and does not exclude air.

### Parameters
None.

### Returns
`int` — number of tracked blocks in the snapshot.

---

## entityCount

### Signature
```java
public int entityCount()
```

### Description
Returns the number of captured entities (`entities.size()`). It is used in log lines, command output and commit metadata.

### Parameters
None.

### Returns
`int` — number of entities in the snapshot.

---

## isEmpty

### Signature
```java
public boolean isEmpty()
```

### Description
Reports whether both the block and entity lists are empty; the origin and selection name are ignored. A snapshot that only records tracked air is not empty, because tracked air is a real coordinate.

### Parameters
None.

### Returns
`boolean` — `true` only when there are no blocks and no entities.

---

## rebase

### Signature
```java
public Snapshot rebase(BlockPos newOrigin)
```

### Description
Return an equivalent snapshot measured from a new origin. Absolute world
positions do not change; only the relative coordinates do.

The implementation rebuilds every block and entity with its stored absolute position and computes the new relative position as `absolute - newOrigin`; a `null` origin throws `IllegalArgumentException`. It returns a new snapshot and never mutates the receiver, so callers can hold both the old and rebased views.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `newOrigin` | [`BlockPos`](BlockPos.md) | Absolute origin the returned snapshot should be measured from; must not be `null`. |

### Returns
`Snapshot` — a new snapshot with the same name, blocks, entities and timestamp, origin set to `newOrigin`, and relative coordinates recomputed as `absolute - newOrigin` (absolute positions unchanged).

---

## retain

### Signature
```java
public Snapshot retain(Selection selection)
```

### Description
Keep only blocks and entities inside the selection's current cuboids.

Blocks are tested with `selection.contains(block.absolute())` and entities by flooring their absolute position to a block coordinate. Kept records are reused unchanged, so their relative coordinates still refer to the snapshot's old origin until the result is rebased onto `selection.origin()`; `StagingService` performs exactly that `retain(...).rebase(...)` sequence after a cuboid is removed.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `selection` | [`Selection`](Selection.md) | Selection whose current cuboids define what is kept; blocks are tested by absolute position and entities by their floored absolute position. |

### Returns
`Snapshot` — a new snapshot containing only the blocks and entities inside the cuboids, named after `selection` and reporting `selection.origin()`. Kept entries retain their old relative coordinates, so callers such as `StagingService` chain `rebase(selection.origin())` afterwards.

---

## byRelative

### Signature
```java
public Map<BlockPos, BlockSnapshot> byRelative()
```

### Description
Indexes the blocks by relative coordinate (`BlockSnapshot.relative()`) in canonical sorted order, making it the Git-facing view used by diffing and apply planning. The returned `LinkedHashMap` is fresh and mutable; if two blocks shared a relative key the later one would win.

### Parameters
None.

### Returns
`Map<BlockPos, BlockSnapshot>` — mutable insertion-ordered map from relative `BlockPos` to `BlockSnapshot`, one entry per tracked coordinate.

---

## byAbsolute

### Signature
```java
public Map<BlockPos, BlockSnapshot> byAbsolute()
```

### Description
Indexes the blocks by absolute world coordinate (`BlockSnapshot.absolute()`), which apply and debugging tooling use to address the real world. The returned `LinkedHashMap` is fresh and mutable; duplicate absolute keys would resolve to the last block in sorted order.

### Parameters
None.

### Returns
`Map<BlockPos, BlockSnapshot>` — mutable insertion-ordered map from absolute `BlockPos` to `BlockSnapshot`.

---

## volume

### Signature
```java
public long volume()
```

### Description
Returns `blocks.size()`, the number of tracked block entries, as a `long`. Despite the name it is not the geometric volume of the enclosing cuboids and does not count only non-air blocks; entities are excluded.

### Parameters
None.

### Returns
`long` — tracked block count.

---

## dimensions

### Signature
```java
public String dimensions()
```

### Description
Computes the axis-aligned bounding size of the tracked blocks from their relative coordinates as `widthX x heightY x depthZ`, using `max - min + 1` on each axis. Empty block lists yield `0x0x0`; entities and internal gaps are ignored, so sparse snapshots report their bounding box rather than a filled shape.

### Parameters
None.

### Returns
`String` — dimension string such as `10x5x3`, or `0x0x0` when there are no blocks.

---

## toString

### Signature
```java
@Override
public String toString()
```

### Description
Returns a short debug string of the form `selection @ origin (N blocks, M entities)`. `BlockPos.toString()` renders the origin as `x, y, z`, so a typical result is `castle @ 0, 64, 0 (2 blocks, 0 entities)`.

### Parameters
None.

### Returns
`String` — human-readable summary used in logs and command output.

---

## See Also

- **Software Detailed Design:** [Snapshot_SDD.md](./Snapshot_SDD.md)
