# BuildDiff — Software Detailed Design

> **API Documentation:** [BuildDiff.md](./BuildDiff.md)  
> **Source File:** [BuildDiff.java](../../../../../../../src/main/java/org/almond/minecraftgit/model/BuildDiff.java)

## Table of Contents

- [Usage](#usage)
- [1. Overview](#1-overview)
- [2. Package Declaration & Imports](#2-package-declaration-imports)
- [3. Class Declaration](#3-class-declaration)
- [4. Instance Fields](#4-instance-fields)
- [5. Constructor](#5-constructor)
- [6. `between()`](#6-between)
- [7. `sameBlock()`](#7-sameblock)
- [8. `mergeBlocks()`](#8-mergeblocks)
- [9. `added()`](#9-added)
- [10. `changed()`](#10-changed)
- [11. `removed()`](#11-removed)
- [12. `entitiesAdded()`](#12-entitiesadded)
- [13. `entitiesRemoved()`](#13-entitiesremoved)
- [14. `unchanged()`](#14-unchanged)
- [15. `totalBlockChanges()`](#15-totalblockchanges)
- [16. `totalEntityChanges()`](#16-totalentitychanges)
- [17. `isEmpty()`](#17-isempty)
- [18. `summary()`](#18-summary)

## Usage

`BuildDiff` is created through the static `between` factory; the private constructor guarantees that callers receive lists that are frozen and canonically sorted. `CommitService` compares the last committed snapshot with the staged snapshot to decide whether a selection changed and to accumulate commit totals, while `StatusService` compares HEAD, staged and current captures for staged/working status. The result is read-only and independent of the input snapshots, so it can be passed between services or reported directly.

---

## 1. Overview

Coordinate-level difference between two snapshots, using relative
coordinates. The final state of a coordinate wins; there is no line-based
three-way merge at this layer.

---

## 2. Package Declaration & Imports

```java
package org.almond.minecraftgit.model;
```

```java
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
```

| Import | Purpose |
|--------|---------|
| `ArrayList` | Accumulates the added/changed/removed block lists and builds mutable copies of the entity lists inside `between`. |
| `LinkedHashMap` | Preserves argument order when `mergeBlocks` combines coordinate maps. |
| `List` | Declares the immutable change lists exposed by the accessors. |
| `Map` | Indexes snapshots by relative coordinate and is the return type of `mergeBlocks`. |

---

## 3. Class Declaration

```java
public final class BuildDiff {
```

`final` plus a private constructor make `BuildDiff` a closed value type: callers cannot subclass it or construct inconsistent instances, and `between` is the only factory. Every change list is frozen before the instance escapes, so a diff can be reported, cached or handed to another service without copying. Because the keys are relative coordinates, comparisons are independent of the world origin.

---

## 4. Instance Fields

```java
private final List<BlockSnapshot> added;
private final List<BlockSnapshot> changed;
private final List<BlockSnapshot> removed;
private final List<EntitySnapshot> entitiesAdded;
private final List<EntitySnapshot> entitiesRemoved;
private final int unchanged;
```

| Field | Type | Description |
|-------|------|-------------|
| `added` | `List<BlockSnapshot>` | Immutable, sorted after-state blocks for coordinates new in `after`. |
| `changed` | `List<BlockSnapshot>` | Immutable, sorted after-state blocks whose coordinate existed but whose payload differs. |
| `removed` | `List<BlockSnapshot>` | Immutable, sorted before-state blocks for coordinates dropped from `after`. |
| `entitiesAdded` | `List<EntitySnapshot>` | Immutable, sorted entities unmatched in `before`. |
| `entitiesRemoved` | `List<EntitySnapshot>` | Immutable, sorted entities unmatched in `after`. |
| `unchanged` | `int` | Count of identical block coordinates; it is a scalar because there is no per-coordinate list. |

---

## 5. Constructor

```java
private BuildDiff(List<BlockSnapshot> added, List<BlockSnapshot> changed, List<BlockSnapshot> removed,
                      List<EntitySnapshot> entitiesAdded, List<EntitySnapshot> entitiesRemoved, int unchanged)
```

The private constructor only copies and freezes: `List.copyOf` rejects null lists and null elements and makes each list immutable, while `unchanged` is stored by value. It performs no sorting or validation because `between` has already produced ordered lists, keeping the constructor a trivial data holder and confining diff semantics to the factory.

---

## 6. `between()`

```java
public static BuildDiff between(Snapshot before, Snapshot after)
```

The factory converts each non-null snapshot to a `LinkedHashMap<BlockPos, BlockSnapshot>` via `byRelative()`; a `null` snapshot becomes `Map.of()`. It scans the after map to classify missing keys as `added`, unequal keys (`sameBlock`) as `changed`, and equal keys into the `unchanged` counter, then scans the old map to collect keys absent from the new map as `removed`. Absence is meaningful: a coordinate present only in `after` is added, one present only in `before` is removed, and a coordinate that still exists but became `minecraft:air` is merely changed, preserving the tracked-air versus untracked-empty distinction. Entities are handled by value: a mutable copy of the before list is built, each after entity is removed from that copy (record equality), entities that fail to remove are additions, and the remainder is removals. Every list is finally sorted with the canonical `ORDER` comparators, making output stable even though the coordinate maps are insertion-ordered, and the private constructor freezes the result.

---

## 7. `sameBlock()`

```java
private static boolean sameBlock(BlockSnapshot a, BlockSnapshot b)
```

Private helper implementing the final-state comparison: `blockData` strings must be equal and block-entity payloads must be equal via `Objects.equals`, with `null` meaning no payload. Coordinates are intentionally not compared because the map key already establishes position; as a consequence, a coordinate whose block state is unchanged but whose block-entity payload changed still counts as `changed`.

---

## 8. `mergeBlocks()`

```java
public static Map<BlockPos, BlockSnapshot> mergeBlocks(Map<BlockPos, BlockSnapshot>... maps)
```

Merge maps keyed by relative coordinate, final state wins.

The implementation is deliberately shallow: one `LinkedHashMap` receives `putAll` from each map in argument order, so the final state of each coordinate wins and the first insertion position of a key is retained for deterministic iteration. There is no three-way merge, conflict detection or coordinate diffing here; commit-time reconciliation compares whole snapshots in `between`, and `ApplyPlan` derives placements and removals from relative-coordinate maps instead.

---

## 9. `added()`

```java
public List<BlockSnapshot> added()
```

Accessor only; the list is already immutable, sorted and null-free after `List.copyOf`. Commit code uses its size for totals, while serialization can use the entries to know which coordinates are genuinely new relative to the previous commit.

---

## 10. `changed()`

```java
public List<BlockSnapshot> changed()
```

Accessor for modified coordinates, holding the after state so a writer can emit the new block data without consulting the source snapshot. Each entry is unique by relative coordinate because the after map is keyed that way.

---

## 11. `removed()`

```java
public List<BlockSnapshot> removed()
```

Accessor for deleted coordinates, holding the before state because the after snapshot has no block at that key. Consumers use these entries to clear the coordinate in the world or to describe the deletion in a commit message.

---

## 12. `entitiesAdded()`

```java
public List<EntitySnapshot> entitiesAdded()
```

Accessor for entity additions. Entity identity is the whole `EntitySnapshot` record (position, rotation, type and data) rather than a UUID, so a moved or edited entity is reported here instead of in a hypothetical changed list.

---

## 13. `entitiesRemoved()`

```java
public List<EntitySnapshot> entitiesRemoved()
```

Accessor for entity removals; it is the leftover of the before-list copy after matching after entities were removed. A moved entity therefore contributes one entry to this list and one to `entitiesAdded`.

---

## 14. `unchanged()`

```java
public int unchanged()
```

Exposes the counter accumulated during the after-map scan, reporting how much of the build is untouched. It is stored separately because it has no per-coordinate list, and entity matching does not affect it.

---

## 15. `totalBlockChanges()`

```java
public long totalBlockChanges()
```

Adds the three block list sizes with `long` arithmetic to avoid overflow on very large builds, providing one aggregate figure for callers that want it. It excludes entity changes by design; current commit and status code reads the individual lists.

---

## 16. `totalEntityChanges()`

```java
public long totalEntityChanges()
```

Adds `entitiesAdded().size()` and `entitiesRemoved().size()` as a `long`; a moved entity contributes two because it appears in both lists.

---

## 17. `isEmpty()`

```java
public boolean isEmpty()
```

Checks the five change lists for emptiness and ignores `unchanged`; it is the 'nothing changed' gate used by commit and status code. It is intentionally independent of origin metadata, which commit code compares separately for re-origin commits.

---

## 18. `summary()`

```java
public String summary()
```

Formats only the three block counts into a single line for command output. Callers that need entity counts read `totalEntityChanges()` separately, so the summary can look empty while entity changes exist.

---
