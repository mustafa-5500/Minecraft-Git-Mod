# Snapshot — Software Detailed Design

> **API Documentation:** [Snapshot.md](./Snapshot.md)  
> **Source File:** [Snapshot.java](../../../../../../../src/main/java/org/almond/minecraftgit/model/Snapshot.java)

## Table of Contents

- [Usage](#usage)
- [1. Overview](#1-overview)
- [2. Package Declaration & Imports](#2-package-declaration-imports)
- [3. Class Declaration](#3-class-declaration)
- [4. Instance Fields](#4-instance-fields)
- [5. Constructor](#5-constructor)
- [6. `empty()`](#6-empty)
- [7. `selection()`](#7-selection)
- [8. `origin()`](#8-origin)
- [9. `blocks()`](#9-blocks)
- [10. `entities()`](#10-entities)
- [11. `capturedAt()`](#11-capturedat)
- [12. `blockCount()`](#12-blockcount)
- [13. `entityCount()`](#13-entitycount)
- [14. `isEmpty()`](#14-isempty)
- [15. `rebase()`](#15-rebase)
- [16. `retain()`](#16-retain)
- [17. `byRelative()`](#17-byrelative)
- [18. `byAbsolute()`](#18-byabsolute)
- [19. `volume()`](#19-volume)
- [20. `dimensions()`](#20-dimensions)
- [21. `toString()`](#21-tostring)

## Usage

`Snapshot` is produced by `SnapshotService` during capture, by `QuadrantCsvReader` when a committed tree is read back, and by `SnapshotStore` when staged data is loaded; `Snapshot.empty` stands in when no committed state exists. Staging rebases a snapshot onto the selection origin before persisting it, `CommitService` diffs a committed snapshot against a staged one, and `StatusService`/`ApplyPlan` consume `byRelative()` maps for status and apply planning. Because instances are immutable, the same snapshot can be shared across those services without defensive copying.

---

## 1. Overview

Immutable capture of one selection at one moment. Blocks and entities carry
coordinates relative to `.origin()` so the repository representation is
portable between worlds.

---

## 2. Package Declaration & Imports

```java
package org.almond.minecraftgit.model;
```

```java
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
```

| Import | Purpose |
|--------|---------|
| `Instant` | Records `capturedAt` and supplies `Instant.now()` when a timestamp is absent. |
| `ArrayList` | Builds defensive list copies in the constructor and accumulates rebased and retained entries. |
| `Collections` | Wraps the internal block and entity lists as unmodifiable views returned by the accessors. |
| `LinkedHashMap` | Backs `byRelative()` and `byAbsolute()` so coordinate maps keep deterministic canonical order. |
| `List` | Declares the block and entity collection types used by the constructor, fields and accessors. |
| `Map` | Return type of the coordinate index methods `byRelative()` and `byAbsolute()`. |

---

## 3. Class Declaration

```java
public final class Snapshot {
```

`final` prevents subclassing, and every field is `final`, so the only way to change any part of a snapshot is to construct a new one; `rebase` and `retain` follow that rule and return new instances. The two list fields are exposed only through unmodifiable views, which makes a snapshot safe to share between staging, commit and status code without defensive copying. It behaves as a value object in use, although it does not override `equals`/`hashCode`: its meaningful state is the selection name, origin, contents and timestamp.

---

## 4. Instance Fields

```java
private final String selection;
private final BlockPos origin;
private final List<BlockSnapshot> blocks;
private final List<EntitySnapshot> entities;
private final Instant capturedAt;
```

| Field | Type | Description |
|-------|------|-------------|
| `selection` | `String` | Recorded selection name; informational only, not validated and may be `null`. |
| `origin` | [`BlockPos`](BlockPos.md) | Absolute origin for relative coordinates; non-null and replaced only by `rebase`. |
| `blocks` | `List<BlockSnapshot>` | Internal sorted, null-free block list; mutable only inside the class and exposed read-only. |
| `entities` | `List<EntitySnapshot>` | Internal sorted, null-free entity list; exposed read-only. |
| `capturedAt` | `Instant` | Capture instant; defaults to now and is carried unchanged through rebases and retains. |

---

## 5. Constructor

```java
public Snapshot(String selection, BlockPos origin, List<BlockSnapshot> blocks,
                    List<EntitySnapshot> entities, Instant capturedAt)
```

The constructor enforces the only hard invariant, a non-null origin (`IllegalArgumentException` otherwise). A `null` list is treated as empty, both lists are copied with `new ArrayList<>(...)`, null elements are removed, and blocks/entities are sorted by `BlockSnapshot.ORDER`/`EntitySnapshot.ORDER` so serialization and diffing see a deterministic order regardless of capture order. Because the lists are copied, later changes to the caller's collections cannot affect the snapshot; `capturedAt` falls back to `Instant.now()` when absent.

---

## 6. `empty()`

```java
public static Snapshot empty(String selection, BlockPos origin)
```

Static factory that delegates to the constructor with empty lists and a fresh timestamp. `CommitService` uses it when a selection has no previous commit, and `StatusService` uses it when a selection has neither staged data nor a previous HEAD; because `BuildDiff.between` treats such a base as having no coordinates, every coordinate in the compared snapshot becomes an addition.

---

## 7. `selection()`

```java
public String selection()
```

Plain accessor returning the stored name with no validation or copying. `retain` is the only method that changes it (to `Selection.name()`), and `toString()` reads it directly, so the field is a pure label rather than an identifier used for lookup.

---

## 8. `origin()`

```java
public BlockPos origin()
```

Returns the absolute `BlockPos` that serves as the subtraction basis for relative coordinates. Callers combine it with `BlockPos.add`/`subtract` (or `Selection.relative`/`absolute`) instead of recomputing coordinates themselves, and the only supported way to move it on an existing snapshot is `rebase`, which preserves absolute block positions.

---

## 9. `blocks()`

```java
public List<BlockSnapshot> blocks()
```

Returns `Collections.unmodifiableList(blocks)`, a live read-only view of the sorted internal list; the field is never reassigned after construction, so repeated calls are stable. The elements are immutable `BlockSnapshot` records carrying both absolute (`x,y,z`) and relative (`rx,ry,rz`) coordinates, so iteration exposes both representations without conversion.

---

## 10. `entities()`

```java
public List<EntitySnapshot> entities()
```

Uses the same unmodifiable-view pattern as `blocks()`. Each `EntitySnapshot` stores absolute and relative doubles, yaw, pitch, registry type and serialized entity data; ordering follows `EntitySnapshot.ORDER` (Y, then X, then Z), the same order the CSV writer sees when it iterates and the diff sorts by.

---

## 11. `capturedAt()`

```java
public Instant capturedAt()
```

Returns the timestamp recorded at capture. It is informational only: ordering and equality are based on contents, and the value is preserved unchanged through `rebase` and `retain` so a rebased or trimmed snapshot still reports when it was originally taken.

---

## 12. `blockCount()`

```java
public int blockCount()
```

O(1) `blocks.size()`; it counts tracked coordinates, including tracked-air entries, which matches the number of coordinates the serializer will emit. It is not a non-air count and is unrelated to `Selection.volume()`.

---

## 13. `entityCount()`

```java
public int entityCount()
```

O(1) `entities.size()`; surfaced in log messages, `toString()` and commit bookkeeping so callers do not have to reach into the entity list just to count it.

---

## 14. `isEmpty()`

```java
public boolean isEmpty()
```

Returns `blocks.isEmpty() && entities.isEmpty()`, the cheap gate used before serializing or reporting a snapshot. Tracked air counts as content because the distinction between tracked and untracked coordinates is meaningful to the repository format.

---

## 15. `rebase()`

```java
public Snapshot rebase(BlockPos newOrigin)
```

Return an equivalent snapshot measured from a new origin. Absolute world
positions do not change; only the relative coordinates do.

The implementation trusts each block's stored absolute coordinates as the source of truth: every `BlockSnapshot` is rebuilt with the same `absolute()` position but a relative position of `absolute - newOrigin`, and every entity is rebuilt with `rx/ry/rz` recomputed as `x/y/z - newOrigin` while yaw, pitch, type and data are preserved. A `null` origin is rejected with `IllegalArgumentException`, and the returned snapshot keeps the selection name, `capturedAt` and canonical sorting; the receiver is never mutated.

---

## 16. `retain()`

```java
public Snapshot retain(Selection selection)
```

Keep only blocks and entities inside the selection's current cuboids.

`retain` tests blocks with `selection.contains(block.absolute())` and floors each entity's absolute position (`Math.floor` per axis) into a `BlockPos` for the same test. The surviving records are reused unchanged, so their relative coordinates still refer to the snapshot's previous origin even though the result reports `selection.origin()`; `StagingService.restrictStaged` therefore calls `retain(selection)` followed by `rebase(selection.origin())`. This is what lets a removed cuboid trim staged data without moving the selection origin (E2/E6).

---

## 17. `byRelative()`

```java
public Map<BlockPos, BlockSnapshot> byRelative()
```

Builds a fresh `LinkedHashMap` keyed by `block.relative()` in the snapshot's existing sorted order, making it the canonical index for `BuildDiff.between` and `ApplyPlan`. Nothing is cached, so the map always reflects the current immutable list; duplicate relative keys would take the last entry, which a well-formed snapshot avoids.

---

## 18. `byAbsolute()`

```java
public Map<BlockPos, BlockSnapshot> byAbsolute()
```

Builds a fresh `LinkedHashMap` keyed by `block.absolute()` for apply planning, backup and debugging code where the real world position matters. Like `byRelative()` it is a mutable copy, and later entries with the same absolute key win.

---

## 19. `volume()`

```java
public long volume()
```

Returns `blocks.size()` widened to `long`; despite the name it is the tracked block count, not geometric volume (`Selection.volume()` computes cuboid volume) and not a count of non-air blocks. Entities are excluded from the figure.

---

## 20. `dimensions()`

```java
public String dimensions()
```

Scans the blocks once for min/max `rx`, `ry` and `rz`, then renders `(maxX-minX+1)x(maxY-minY+1)x(maxZ-minZ+1)`, short-circuiting to `0x0x0` for an empty block list. Relative coordinates make the result origin-independent; the computation measures the bounding box, so sparse gaps are included in the reported size.

---

## 21. `toString()`

```java
@Override
public String toString()
```

Composes `selection + " @ " + origin + " (" + blocks.size() + " blocks, " + entities.size() + " entities)"` for logs and command output. `BlockPos.toString()` formats the origin as `x, y, z`, so a typical result is `castle @ 0, 64, 0 (2 blocks, 0 entities)`.

---
