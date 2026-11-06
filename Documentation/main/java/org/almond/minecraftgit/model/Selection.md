# Selection

> **Software Detailed Documentation:** [Selection_SDD.md](./Selection_SDD.md)  
> **Source File:** [Selection.java](../../../../../../../src/main/java/org/almond/minecraftgit/model/Selection.java)

## Table of Contents

- [Usage](#usage)
- [Fields](#fields)
- [Selection (Constructor)](#selection-constructor)
- [normalize](#normalize)
- [name](#name)
- [setName](#setname)
- [owner](#owner)
- [setOwner](#setowner)
- [ownerName](#ownername)
- [setOwnerName](#setownername)
- [world](#world)
- [setWorld](#setworld)
- [origin](#origin)
- [setOrigin](#setorigin)
- [hasOrigin](#hasorigin)
- [cuboids](#cuboids)
- [setCuboids](#setcuboids)
- [addCuboid](#addcuboid)
- [removeCuboidAt](#removecuboidat)
- [reorigin](#reorigin)
- [isTracked](#istracked)
- [setTracked](#settracked)
- [createdAt](#createdat)
- [updatedAt](#updatedat)
- [touch](#touch)
- [cuboidCount](#cuboidcount)
- [volume](#volume)
- [boundsMin](#boundsmin)
- [boundsMax](#boundsmax)
- [contains](#contains)
- [relative](#relative)
- [absolute](#absolute)
- [byName](#byname)
- [toString](#tostring)
- [See Also](#see-also)

## Usage

TODO: Add usage examples for `Selection`.

A named, multi-cuboid build selection owned by one player in one world.
<p>Selections are persisted per world id + player in the mod data folder and
are never committed. The origin is the absolute world position that relative
repository coordinates are measured from. It is set by the first cuboid and
deliberately does <em>not</em> move when that cuboid is removed (E2); only an
explicit re-origin or a new selection changes it.</p>

---

## Fields

- `name` (`String`) — TODO: describe field
- `owner` (`UUID`) — TODO: describe field
- `ownerName` (`String`) — TODO: describe field
- `world` (`String`) — TODO: describe field
- `origin` ([`BlockPos`](BlockPos.md)) — TODO: describe field
- `cuboids` (`List<Cuboid>`) — TODO: describe field
- `tracked` (`boolean`) — TODO: describe field
- `createdAt` (`String`) — TODO: describe field
- `updatedAt` (`String`) — TODO: describe field

---

## Selection (Constructor)

### Signature
```java
public Selection()
```

### Description
TODO: Describe what `Selection` does.

### Parameters
None.

### Returns
`Selection` instance.

---

## Selection (Constructor)

### Signature
```java
public Selection(String name, UUID owner, String ownerName, String world, BlockPos origin)
```

### Description
TODO: Describe what `Selection` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `name` | `String` | TODO: describe |
| `owner` | `UUID` | TODO: describe |
| `ownerName` | `String` | TODO: describe |
| `world` | `String` | TODO: describe |
| `origin` | [`BlockPos`](BlockPos.md) | TODO: describe |

### Returns
`Selection` instance.

---

## normalize

### Signature
```java
public Selection normalize(String fallbackWorld, UUID fallbackOwner, String fallbackOwnerName)
```

### Description
TODO: Describe what `normalize` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `fallbackWorld` | `String` | TODO: describe |
| `fallbackOwner` | `UUID` | TODO: describe |
| `fallbackOwnerName` | `String` | TODO: describe |

### Returns
`Selection` — TODO: describe return value.

---

## name

### Signature
```java
public String name()
```

### Description
TODO: Describe what `name` does.

### Parameters
None.

### Returns
`String` — TODO: describe return value.

---

## setName

### Signature
```java
public void setName(String name)
```

### Description
TODO: Describe what `setName` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `name` | `String` | TODO: describe |

### Returns
`void`

---

## owner

### Signature
```java
public UUID owner()
```

### Description
TODO: Describe what `owner` does.

### Parameters
None.

### Returns
`UUID` — TODO: describe return value.

---

## setOwner

### Signature
```java
public void setOwner(UUID owner)
```

### Description
TODO: Describe what `setOwner` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `owner` | `UUID` | TODO: describe |

### Returns
`void`

---

## ownerName

### Signature
```java
public String ownerName()
```

### Description
TODO: Describe what `ownerName` does.

### Parameters
None.

### Returns
`String` — TODO: describe return value.

---

## setOwnerName

### Signature
```java
public void setOwnerName(String ownerName)
```

### Description
TODO: Describe what `setOwnerName` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `ownerName` | `String` | TODO: describe |

### Returns
`void`

---

## world

### Signature
```java
public String world()
```

### Description
TODO: Describe what `world` does.

### Parameters
None.

### Returns
`String` — TODO: describe return value.

---

## setWorld

### Signature
```java
public void setWorld(String world)
```

### Description
TODO: Describe what `setWorld` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `world` | `String` | TODO: describe |

### Returns
`void`

---

## origin

### Signature
```java
public BlockPos origin()
```

### Description
TODO: Describe what `origin` does.

### Parameters
None.

### Returns
[`BlockPos`](BlockPos.md) — TODO: describe return value.

---

## setOrigin

### Signature
```java
public void setOrigin(BlockPos origin)
```

### Description
TODO: Describe what `setOrigin` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `origin` | [`BlockPos`](BlockPos.md) | TODO: describe |

### Returns
`void`

---

## hasOrigin

### Signature
```java
public boolean hasOrigin()
```

### Description
TODO: Describe what `hasOrigin` does.

### Parameters
None.

### Returns
`boolean` — TODO: describe return value.

---

## cuboids

### Signature
```java
public List<Cuboid> cuboids()
```

### Description
TODO: Describe what `cuboids` does.

### Parameters
None.

### Returns
`List<Cuboid>` — TODO: describe return value.

---

## setCuboids

### Signature
```java
public void setCuboids(List<Cuboid> cuboids)
```

### Description
TODO: Describe what `setCuboids` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `cuboids` | `List<Cuboid>` | TODO: describe |

### Returns
`void`

---

## addCuboid

### Signature
```java
public void addCuboid(Cuboid cuboid)
```

### Description
Append a cuboid, re-sort by smallest corner, and seed the origin.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `cuboid` | [`Cuboid`](Cuboid.md) | TODO: describe |

### Returns
`void`

---

## removeCuboidAt

### Signature
```java
public Cuboid removeCuboidAt(int index)
```

### Description
Remove a cuboid by zero-based position in the sorted list.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `index` | `int` | TODO: describe |

### Returns
[`Cuboid`](Cuboid.md) — TODO: describe return value.

---

## reorigin

### Signature
```java
public BlockPos reorigin()
```

### Description
Rebase the origin onto the smallest remaining cuboid corner (E3).

### Parameters
None.

### Returns
[`BlockPos`](BlockPos.md) — the previous origin

---

## isTracked

### Signature
```java
public boolean isTracked()
```

### Description
TODO: Describe what `isTracked` does.

### Parameters
None.

### Returns
`boolean` — TODO: describe return value.

---

## setTracked

### Signature
```java
public void setTracked(boolean tracked)
```

### Description
TODO: Describe what `setTracked` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `tracked` | `boolean` | TODO: describe |

### Returns
`void`

---

## createdAt

### Signature
```java
public String createdAt()
```

### Description
TODO: Describe what `createdAt` does.

### Parameters
None.

### Returns
`String` — TODO: describe return value.

---

## updatedAt

### Signature
```java
public String updatedAt()
```

### Description
TODO: Describe what `updatedAt` does.

### Parameters
None.

### Returns
`String` — TODO: describe return value.

---

## touch

### Signature
```java
public void touch()
```

### Description
TODO: Describe what `touch` does.

### Parameters
None.

### Returns
`void`

---

## cuboidCount

### Signature
```java
public int cuboidCount()
```

### Description
TODO: Describe what `cuboidCount` does.

### Parameters
None.

### Returns
`int` — TODO: describe return value.

---

## volume

### Signature
```java
public long volume()
```

### Description
Sum of cuboid volumes, saturated. Overlaps are counted twice.

### Parameters
None.

### Returns
`long` — TODO: describe return value.

---

## boundsMin

### Signature
```java
public BlockPos boundsMin()
```

### Description
TODO: Describe what `boundsMin` does.

### Parameters
None.

### Returns
[`BlockPos`](BlockPos.md) — TODO: describe return value.

---

## boundsMax

### Signature
```java
public BlockPos boundsMax()
```

### Description
TODO: Describe what `boundsMax` does.

### Parameters
None.

### Returns
[`BlockPos`](BlockPos.md) — TODO: describe return value.

---

## contains

### Signature
```java
public boolean contains(BlockPos pos)
```

### Description
TODO: Describe what `contains` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `pos` | [`BlockPos`](BlockPos.md) | TODO: describe |

### Returns
`boolean` — TODO: describe return value.

---

## relative

### Signature
```java
public BlockPos relative(BlockPos absolute)
```

### Description
TODO: Describe what `relative` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `absolute` | [`BlockPos`](BlockPos.md) | TODO: describe |

### Returns
[`BlockPos`](BlockPos.md) — TODO: describe return value.

---

## absolute

### Signature
```java
public BlockPos absolute(BlockPos relative)
```

### Description
TODO: Describe what `absolute` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `relative` | [`BlockPos`](BlockPos.md) | TODO: describe |

### Returns
[`BlockPos`](BlockPos.md) — TODO: describe return value.

---

## byName

### Signature
```java
public static Comparator<Selection> byName()
```

### Description
TODO: Describe what `byName` does.

### Parameters
None.

### Returns
`Comparator<Selection>` — TODO: describe return value.

---

## toString

### Signature
```java
@Override
public String toString()
```

### Description
TODO: Describe what `toString` does.

### Parameters
None.

### Returns
`String` — TODO: describe return value.

---

## See Also

- **Software Detailed Design:** [Selection_SDD.md](./Selection_SDD.md)
