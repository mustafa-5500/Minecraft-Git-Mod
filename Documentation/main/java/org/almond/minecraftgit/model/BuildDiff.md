# BuildDiff

> **Software Detailed Documentation:** [BuildDiff_SDD.md](./BuildDiff_SDD.md)  
> **Source File:** [BuildDiff.java](../../../../../../../src/main/java/org/almond/minecraftgit/model/BuildDiff.java)

## Table of Contents

- [Usage](#usage)
- [Fields](#fields)
- [BuildDiff (Constructor)](#builddiff-constructor)
- [between](#between)
- [sameBlock](#sameblock)
- [mergeBlocks](#mergeblocks)
- [added](#added)
- [changed](#changed)
- [removed](#removed)
- [entitiesAdded](#entitiesadded)
- [entitiesRemoved](#entitiesremoved)
- [unchanged](#unchanged)
- [totalBlockChanges](#totalblockchanges)
- [totalEntityChanges](#totalentitychanges)
- [isEmpty](#isempty)
- [summary](#summary)
- [See Also](#see-also)

## Usage

`BuildDiff` is produced only by the static `between(before, after)` factory because its constructor is private. Commit, status and diff code use it to classify relative coordinates between a committed snapshot and a staged or freshly captured one, then read the lists or counts.

```java
Snapshot before = Snapshot.empty("castle", BlockPos.ORIGIN);
Snapshot after = new Snapshot("castle", BlockPos.ORIGIN,
        List.of(BlockSnapshot.at(new BlockPos(0, 0, 0), BlockPos.ORIGIN, "minecraft:stone", null)),
        List.of(), Instant.now());

BuildDiff diff = BuildDiff.between(before, after);
System.out.println(diff.summary()); // 1 added, 0 changed, 0 removed
```

Coordinate-level difference between two snapshots, using relative
coordinates. The final state of a coordinate wins; there is no line-based
three-way merge at this layer.

---

## Fields

- `added` (`List<BlockSnapshot>`) — Blocks present in `after` at a relative coordinate absent from `before`; each entry is the after-state block. The list is immutable and sorted by `BlockSnapshot.ORDER`.
- `changed` (`List<BlockSnapshot>`) — Blocks whose relative coordinate exists in both snapshots but whose `blockData` or `blockEntity` differs according to `sameBlock`; each entry holds the after state.
- `removed` (`List<BlockSnapshot>`) — Relative coordinates that existed in `before` but not in `after`; each entry holds the before-state block, since no after value exists.
- `entitiesAdded` (`List<EntitySnapshot>`) — Entities found in `after` that could not be matched one-for-one against `before` by full record equality. The list is immutable and sorted by `EntitySnapshot.ORDER`.
- `entitiesRemoved` (`List<EntitySnapshot>`) — Entities left unmatched in `before` after added entities were consumed. The list is immutable and sorted by `EntitySnapshot.ORDER`.
- `unchanged` (`int`) — Count of relative coordinates present in both snapshots with identical block data and block-entity payload. Entities have no unchanged counter.

---

## BuildDiff (Constructor)

### Signature
```java
private BuildDiff(List<BlockSnapshot> added, List<BlockSnapshot> changed, List<BlockSnapshot> removed,
                      List<EntitySnapshot> entitiesAdded, List<EntitySnapshot> entitiesRemoved, int unchanged)
```

### Description
Private constructor used only by `between`; it freezes every list with `List.copyOf`, giving the diff immutable collections that reject `null` entries. It does not sort or validate, because the factory is responsible for producing canonically ordered lists.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `added` | `List<BlockSnapshot>` | After-state blocks for newly tracked coordinates. |
| `changed` | `List<BlockSnapshot>` | After-state blocks whose data changed at an existing coordinate. |
| `removed` | `List<BlockSnapshot>` | Before-state blocks for coordinates no longer tracked. |
| `entitiesAdded` | `List<EntitySnapshot>` | Entities matched only in `after`. |
| `entitiesRemoved` | `List<EntitySnapshot>` | Entities matched only in `before`. |
| `unchanged` | `int` | Number of identical block coordinates; entities are not counted. |

### Returns
`BuildDiff` instance.

---

## between

### Signature
```java
public static BuildDiff between(Snapshot before, Snapshot after)
```

### Description
Computes the coordinate-level difference between two snapshots using relative coordinates, so a pure origin move yields an empty diff. Both snapshots are indexed with `byRelative()`, the new map is scanned to classify added/changed/unchanged blocks, and the old map supplies removed coordinates; `null` arguments are treated as empty snapshots. Entity changes are computed separately by consuming matching records from a copy of `before`, and all result lists are sorted before the diff is returned.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `before` | [`Snapshot`](Snapshot.md) | Baseline snapshot; `null` is treated as empty so every block and entity in `after` is reported as added. |
| `after` | [`Snapshot`](Snapshot.md) | Target snapshot; `null` is treated as empty so everything in `before` is reported as removed. |

### Returns
`BuildDiff` — an immutable diff whose lists are sorted in canonical block/entity order; comparing a snapshot with itself returns an empty diff.

---

## sameBlock

### Signature
```java
private static boolean sameBlock(BlockSnapshot a, BlockSnapshot b)
```

### Description
Equality predicate used by `between` for coordinates present in both snapshots. It compares `blockData()` with `equals` and `blockEntity()` with `Objects.equals` (so an absent payload equals only another absent payload) and deliberately ignores coordinates, because the caller already keyed the comparison by relative coordinate.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `a` | [`BlockSnapshot`](BlockSnapshot.md) | Block from the before snapshot at the coordinate being compared. |
| `b` | [`BlockSnapshot`](BlockSnapshot.md) | Block from the after snapshot at the same coordinate. |

### Returns
`boolean` — `true` when both blocks have the same block state and the same block-entity payload.

---

## mergeBlocks

### Signature
```java
public static Map<BlockPos, BlockSnapshot> mergeBlocks(Map<BlockPos, BlockSnapshot>... maps)
```

### Description
Merge maps keyed by relative coordinate, final state wins.

Maps are applied in argument order into a fresh `LinkedHashMap`, so a later map replaces an earlier block at the same `BlockPos`; no diffing, conflict detection or three-way merge is performed. The returned map is mutable, which lets callers continue merging or adjusting the final state.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `` | `Map<BlockPos` | Prefix of the varargs element type; each argument maps a relative `BlockPos` to a `BlockSnapshot`. |
| `maps` | `BlockSnapshot>...` | Suffix of the varargs element type; the maps are merged in argument order, with later entries winning per relative coordinate. |

### Returns
`Map<BlockPos, BlockSnapshot>` — a new mutable `LinkedHashMap` keyed by relative coordinate, where later maps take precedence.

---

## added

### Signature
```java
public List<BlockSnapshot> added()
```

### Description
Returns the immutable, sorted list of blocks that exist only in `after`. Entries are ordered by `BlockSnapshot.ORDER` (Y, then X, then Z), so output is reproducible and suitable for commit totals.

### Parameters
None.

### Returns
`List<BlockSnapshot>` — added after-state blocks; never `null`.

---

## changed

### Signature
```java
public List<BlockSnapshot> changed()
```

### Description
Returns the immutable, sorted list of blocks whose coordinate exists in both snapshots but whose state or block-entity payload differs. Each entry is the after state, so serialization can write the new value while the presence of the coordinate is already known.

### Parameters
None.

### Returns
`List<BlockSnapshot>` — changed after-state blocks; never `null`.

---

## removed

### Signature
```java
public List<BlockSnapshot> removed()
```

### Description
Returns the immutable, sorted list of blocks whose relative coordinates disappeared. Entries are before-state records, because there is no after value for those coordinates.

### Parameters
None.

### Returns
`List<BlockSnapshot>` — removed before-state blocks; never `null`.

---

## entitiesAdded

### Signature
```java
public List<EntitySnapshot> entitiesAdded()
```

### Description
Returns the immutable, sorted entities that appear only in `after`. Matching uses full `EntitySnapshot` record equality, so an entity that moved or changed its data shows up here rather than in a modification list.

### Parameters
None.

### Returns
`List<EntitySnapshot>` — added after-state entities; never `null`.

---

## entitiesRemoved

### Signature
```java
public List<EntitySnapshot> entitiesRemoved()
```

### Description
Returns the immutable, sorted entities that were unmatched in `before` after the after-list was consumed; it is the leftover of the copy used for matching. A moved entity therefore appears in both `entitiesRemoved()` and `entitiesAdded()`.

### Parameters
None.

### Returns
`List<EntitySnapshot>` — removed before-state entities; never `null`.

---

## unchanged

### Signature
```java
public int unchanged()
```

### Description
Returns the count of relative block coordinates that exist in both snapshots and compare equal via `sameBlock`. Entities are not counted, so a diff that only changes entities can still report a positive unchanged count.

### Parameters
None.

### Returns
`int` — number of identical block coordinates.

---

## totalBlockChanges

### Signature
```java
public long totalBlockChanges()
```

### Description
Sums `added().size() + changed().size() + removed().size()` using `long` arithmetic to avoid overflow, giving one aggregate block-change figure. It excludes entity changes; commit and status code currently reads the individual list sizes instead.

### Parameters
None.

### Returns
`long` — total number of added, changed and removed block coordinates.

---

## totalEntityChanges

### Signature
```java
public long totalEntityChanges()
```

### Description
Sums entity additions and removals as a `long`. Because entity identity is full-value equality, a single moved or edited entity contributes two to this total.

### Parameters
None.

### Returns
`long` — total number of added and removed entity records.

---

## isEmpty

### Signature
```java
public boolean isEmpty()
```

### Description
Reports whether all block and entity change lists are empty; `unchanged` is ignored. This answers whether applying the diff would change the repository, and it is the check commit code uses to skip unchanged selections.

### Parameters
None.

### Returns
`boolean` — `true` when there are no added, changed, removed or entity changes.

---

## summary

### Signature
```java
public String summary()
```

### Description
Renders the block counts as `<added> added, <changed> changed, <removed> removed`. Entity counts are not included, so a diff with only entity changes reports `0 added, 0 changed, 0 removed` even though `totalEntityChanges()` is non-zero.

### Parameters
None.

### Returns
`String` — compact block-change summary such as `3 added, 1 changed, 0 removed`.

---

## See Also

- **Software Detailed Design:** [BuildDiff_SDD.md](./BuildDiff_SDD.md)
