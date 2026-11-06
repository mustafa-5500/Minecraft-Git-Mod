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

TODO: Add usage examples for `BuildDiff`.

Coordinate-level difference between two snapshots, using relative
coordinates. The final state of a coordinate wins; there is no line-based
three-way merge at this layer.

---

## Fields

- `added` (`List<BlockSnapshot>`) — TODO: describe field
- `changed` (`List<BlockSnapshot>`) — TODO: describe field
- `removed` (`List<BlockSnapshot>`) — TODO: describe field
- `entitiesAdded` (`List<EntitySnapshot>`) — TODO: describe field
- `entitiesRemoved` (`List<EntitySnapshot>`) — TODO: describe field
- `unchanged` (`int`) — TODO: describe field

---

## BuildDiff (Constructor)

### Signature
```java
private BuildDiff(List<BlockSnapshot> added, List<BlockSnapshot> changed, List<BlockSnapshot> removed,
                      List<EntitySnapshot> entitiesAdded, List<EntitySnapshot> entitiesRemoved, int unchanged)
```

### Description
TODO: Describe what `BuildDiff` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `added` | `List<BlockSnapshot>` | TODO: describe |
| `changed` | `List<BlockSnapshot>` | TODO: describe |
| `removed` | `List<BlockSnapshot>` | TODO: describe |
| `entitiesAdded` | `List<EntitySnapshot>` | TODO: describe |
| `entitiesRemoved` | `List<EntitySnapshot>` | TODO: describe |
| `unchanged` | `int` | TODO: describe |

### Returns
`BuildDiff` instance.

---

## between

### Signature
```java
public static BuildDiff between(Snapshot before, Snapshot after)
```

### Description
TODO: Describe what `between` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `before` | [`Snapshot`](Snapshot.md) | TODO: describe |
| `after` | [`Snapshot`](Snapshot.md) | TODO: describe |

### Returns
`BuildDiff` — TODO: describe return value.

---

## sameBlock

### Signature
```java
private static boolean sameBlock(BlockSnapshot a, BlockSnapshot b)
```

### Description
TODO: Describe what `sameBlock` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `a` | [`BlockSnapshot`](BlockSnapshot.md) | TODO: describe |
| `b` | [`BlockSnapshot`](BlockSnapshot.md) | TODO: describe |

### Returns
`boolean` — TODO: describe return value.

---

## mergeBlocks

### Signature
```java
public static Map<BlockPos, BlockSnapshot> mergeBlocks(Map<BlockPos, BlockSnapshot>... maps)
```

### Description
Merge maps keyed by relative coordinate, final state wins.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `` | `Map<BlockPos` | TODO: describe |
| `maps` | `BlockSnapshot>...` | TODO: describe |

### Returns
`Map<BlockPos, BlockSnapshot>` — TODO: describe return value.

---

## added

### Signature
```java
public List<BlockSnapshot> added()
```

### Description
TODO: Describe what `added` does.

### Parameters
None.

### Returns
`List<BlockSnapshot>` — TODO: describe return value.

---

## changed

### Signature
```java
public List<BlockSnapshot> changed()
```

### Description
TODO: Describe what `changed` does.

### Parameters
None.

### Returns
`List<BlockSnapshot>` — TODO: describe return value.

---

## removed

### Signature
```java
public List<BlockSnapshot> removed()
```

### Description
TODO: Describe what `removed` does.

### Parameters
None.

### Returns
`List<BlockSnapshot>` — TODO: describe return value.

---

## entitiesAdded

### Signature
```java
public List<EntitySnapshot> entitiesAdded()
```

### Description
TODO: Describe what `entitiesAdded` does.

### Parameters
None.

### Returns
`List<EntitySnapshot>` — TODO: describe return value.

---

## entitiesRemoved

### Signature
```java
public List<EntitySnapshot> entitiesRemoved()
```

### Description
TODO: Describe what `entitiesRemoved` does.

### Parameters
None.

### Returns
`List<EntitySnapshot>` — TODO: describe return value.

---

## unchanged

### Signature
```java
public int unchanged()
```

### Description
TODO: Describe what `unchanged` does.

### Parameters
None.

### Returns
`int` — TODO: describe return value.

---

## totalBlockChanges

### Signature
```java
public long totalBlockChanges()
```

### Description
TODO: Describe what `totalBlockChanges` does.

### Parameters
None.

### Returns
`long` — TODO: describe return value.

---

## totalEntityChanges

### Signature
```java
public long totalEntityChanges()
```

### Description
TODO: Describe what `totalEntityChanges` does.

### Parameters
None.

### Returns
`long` — TODO: describe return value.

---

## isEmpty

### Signature
```java
public boolean isEmpty()
```

### Description
TODO: Describe what `isEmpty` does.

### Parameters
None.

### Returns
`boolean` — TODO: describe return value.

---

## summary

### Signature
```java
public String summary()
```

### Description
TODO: Describe what `summary` does.

### Parameters
None.

### Returns
`String` — TODO: describe return value.

---

## See Also

- **Software Detailed Design:** [BuildDiff_SDD.md](./BuildDiff_SDD.md)
