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

Create a selection through `SelectionService.createAt` (which enforces unique names and persists it) or directly with the parameterized constructor, then append one or more inclusive cuboids; the first `addCuboid` call seeds the origin that repository coordinates are measured from.

```java
Selection castle = new Selection("castle", playerId, "Almond", worldId, null);
castle.addCuboid(Cuboid.of(new BlockPos(0, 64, 0), new BlockPos(9, 73, 9)));
castle.addCuboid(Cuboid.of(new BlockPos(20, 64, 0), new BlockPos(29, 73, 9)));
assert castle.origin().equals(new BlockPos(0, 64, 0)); // seeded by the first cuboid
```

A named, multi-cuboid build selection owned by one player in one world.
Selections are persisted per world id + player in the mod data folder and
are never committed. The origin is the absolute world position that relative
repository coordinates are measured from. It is set by the first cuboid and
deliberately does *not* move when that cuboid is removed (E2); only an
explicit re-origin or a new selection changes it.

---

## Fields

- `name` (`String`) — Display name of the selection, unique per owner within a world (uniqueness is enforced by `SelectionService`) and used for command lookup and `byName()` ordering.
- `owner` (`UUID`) — UUID of the owning player, used together with the world id as the persistence key; null until set or filled by `normalize`.
- `ownerName` (`String`) — Last-known player name kept for display and diagnostics; it is not validated and may be blank.
- `world` (`String`) — World id string (for example `world__overworld`) that the selection and its metadata file belong to.
- `origin` ([`BlockPos`](BlockPos.md)) — Absolute world position that relative repository coordinates are measured from; null until seeded by the first cuboid, by `setCuboids`, or by `normalize`, and left unchanged when that cuboid is removed (E2).
- `cuboids` (`List<Cuboid>`) — Mutable backing list of inclusive cuboids, kept sorted by smallest corner (E1); may be empty, and the public `cuboids()` accessor exposes it read-only.
- `tracked` (`boolean`) — Staging flag indicating whether the selection is currently tracked/staged; it starts false, is toggled by stage/unstage flows, and is reported by status.
- `createdAt` (`String`) — ISO-8601 instant string captured when the object is created and preserved across normalization.
- `updatedAt` (`String`) — ISO-8601 instant string refreshed by `touch()` on mutating operations.

---

## Selection (Constructor)

### Signature
```java
public Selection()
```

### Description
Creates an empty selection for deserialization: `cuboids` is initialized to an empty mutable list and `createdAt`/`updatedAt` are set to the current time, while `name`, `owner`, `ownerName`, `world` and `origin` remain null. Call `normalize` with world/owner fallbacks after loading to make the instance usable.

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
Creates an identified selection with the given name, owner, owner name and world, optionally seeded with `origin` (null is allowed). The values are stored as-is: the constructor does not validate the name, add cuboids, sort anything, or call `touch()`.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `name` | `String` | Display name of the selection; it should be non-null because `byName()` and `toString()` assume a value. |
| `owner` | `UUID` | UUID of the owning player; null is permitted for a not-yet-normalized instance. |
| `ownerName` | `String` | Last-known player name for display; may be blank. |
| `world` | `String` | World id this selection belongs to, used for per-world metadata storage. |
| `origin` | [`BlockPos`](BlockPos.md) | Absolute world origin, or null to let the first added cuboid seed it. |

### Returns
`Selection` instance.

---

## normalize

### Signature
```java
public Selection normalize(String fallbackWorld, UUID fallbackOwner, String fallbackOwnerName)
```

### Description
Repairs a deserialized selection in place: blank or null `world`/`ownerName` and null `owner` are replaced from the fallbacks, a null cuboid list is replaced with an empty list, null cuboids are dropped, the remainder is sorted by smallest corner (E1), and the origin is seeded from the first cuboid when still null. Blank timestamps are backfilled (`updatedAt` falls back to `createdAt`) without refreshing them to the current time, so stored values survive a round trip; the method returns `this` for chaining.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `fallbackWorld` | `String` | World id used when the stored world is null or blank. |
| `fallbackOwner` | `UUID` | Owner UUID used when the stored owner is null (typically derived from the metadata file name). |
| `fallbackOwnerName` | `String` | Owner name used when the stored owner name is null or blank. |

### Returns
`Selection` — the same instance after normalization; never null.

---

## name

### Signature
```java
public String name()
```

### Description
Returns the stored display name.

### Parameters
None.

### Returns
`String` — the selection name, which is null if it was never set and `normalize` was not run.

---

## setName

### Signature
```java
public void setName(String name)
```

### Description
Replaces the display name without validation or a timestamp update; callers that need a persisted or unique name go through `SelectionService`.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `name` | `String` | New display name; null is stored as-is. |

### Returns
`void`

---

## owner

### Signature
```java
public UUID owner()
```

### Description
Returns the owning player's UUID.

### Parameters
None.

### Returns
`UUID` — the owner UUID, or null when it has not been set or normalized.

---

## setOwner

### Signature
```java
public void setOwner(UUID owner)
```

### Description
Replaces the owner UUID without validating it or calling `touch()`; persistence happens when the owning service saves the list.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `owner` | `UUID` | New owner UUID; null is stored as-is. |

### Returns
`void`

---

## ownerName

### Signature
```java
public String ownerName()
```

### Description
Returns the last-known player name stored for display.

### Parameters
None.

### Returns
`String` — the owner name, possibly blank or null on an un-normalized instance.

---

## setOwnerName

### Signature
```java
public void setOwnerName(String ownerName)
```

### Description
Replaces the stored owner name without validation or a timestamp update.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `ownerName` | `String` | New display name for the owner; may be blank or null. |

### Returns
`void`

---

## world

### Signature
```java
public String world()
```

### Description
Returns the world id string this selection belongs to.

### Parameters
None.

### Returns
`String` — the world id, or null if never set and not normalized.

---

## setWorld

### Signature
```java
public void setWorld(String world)
```

### Description
Replaces the world id without migrating any stored metadata or calling `touch()`.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `world` | `String` | New world id string, such as `world__overworld`. |

### Returns
`void`

---

## origin

### Signature
```java
public BlockPos origin()
```

### Description
Returns the absolute world origin used to convert between absolute world positions and relative repository coordinates; it may be null before any cuboid is added.

### Parameters
None.

### Returns
[`BlockPos`](BlockPos.md) — the current origin, or null when the selection has no origin yet.

---

## setOrigin

### Signature
```java
public void setOrigin(BlockPos origin)
```

### Description
Explicitly overwrites the origin and refreshes `updatedAt` via `touch()`. The new origin is not checked against the cuboids, so callers can rebase to any world position.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `origin` | [`BlockPos`](BlockPos.md) | New absolute origin; null clears it. |

### Returns
`void`

---

## hasOrigin

### Signature
```java
public boolean hasOrigin()
```

### Description
Reports whether an origin has been seeded or explicitly set.

### Parameters
None.

### Returns
`boolean` — true when `origin` is non-null, false otherwise.

---

## cuboids

### Signature
```java
public List<Cuboid> cuboids()
```

### Description
Returns an unmodifiable live view of the internal cuboid list, which is maintained in smallest-corner order (E1).

### Parameters
None.

### Returns
`List<Cuboid>` — read-only view of the sorted cuboids; attempts to modify it throw `UnsupportedOperationException`.

---

## setCuboids

### Signature
```java
public void setCuboids(List<Cuboid> cuboids)
```

### Description
Replaces the cuboid contents with a defensive copy (null becomes an empty list), sorts it by smallest corner (E1), seeds the origin from the first cuboid only if no origin exists yet, and calls `touch()`. Null elements are not filtered here (unlike `normalize`), so the supplied list must contain only non-null cuboids.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `cuboids` | `List<Cuboid>` | New cuboid collection; null is treated as empty and the list is copied. |

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
| `cuboid` | [`Cuboid`](Cuboid.md) | Non-null inclusive cuboid to append; null throws `IllegalArgumentException`. |

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
| `index` | `int` | Zero-based position in the current sorted list; out-of-range values throw `IndexOutOfBoundsException`. |

### Returns
[`Cuboid`](Cuboid.md) — the cuboid that was removed; the origin is left unchanged (E2).

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
Returns the staging flag that records whether this selection is currently tracked.

### Parameters
None.

### Returns
`boolean` — the tracked flag, false by default.

---

## setTracked

### Signature
```java
public void setTracked(boolean tracked)
```

### Description
Sets the tracked/staged flag and refreshes `updatedAt` via `touch()`.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `tracked` | `boolean` | New tracked state; true when the selection is staged, false when it is untracked. |

### Returns
`void`

---

## createdAt

### Signature
```java
public String createdAt()
```

### Description
Returns the creation timestamp string.

### Parameters
None.

### Returns
`String` — ISO-8601 instant text set when the object was constructed, or backfilled by `normalize`.

---

## updatedAt

### Signature
```java
public String updatedAt()
```

### Description
Returns the last-mutation timestamp string.

### Parameters
None.

### Returns
`String` — ISO-8601 instant text last written by `touch()`, or backfilled by `normalize`.

---

## touch

### Signature
```java
public void touch()
```

### Description
Sets `updatedAt` to the current instant; it is called by the mutating operations but does not itself persist anything to disk.

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
Returns the number of cuboids currently in the selection.

### Parameters
None.

### Returns
`int` — the size of the cuboid list; 0 for an empty selection.

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
`long` — total block count of all cuboids, 0 when empty, capped at `Long.MAX_VALUE` instead of overflowing.

---

## boundsMin

### Signature
```java
public BlockPos boundsMin()
```

### Description
Computes the smallest x, y and z across all cuboids without involving the origin.

### Parameters
None.

### Returns
[`BlockPos`](BlockPos.md) — the minimum corner of the selection's bounds, or the current origin (possibly null) when there are no cuboids.

---

## boundsMax

### Signature
```java
public BlockPos boundsMax()
```

### Description
Computes the largest x, y and z across all cuboids without involving the origin.

### Parameters
None.

### Returns
[`BlockPos`](BlockPos.md) — the maximum corner of the selection's bounds, or the current origin (possibly null) when there are no cuboids.

---

## contains

### Signature
```java
public boolean contains(BlockPos pos)
```

### Description
Tests whether any cuboid contains the given position, using inclusive bounds; a null position causes a `NullPointerException`.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `pos` | [`BlockPos`](BlockPos.md) | Absolute world position to test; must be non-null. |

### Returns
`boolean` — true when at least one cuboid contains `pos`, false otherwise (including for an empty selection).

---

## relative

### Signature
```java
public BlockPos relative(BlockPos absolute)
```

### Description
Converts an absolute world position into a repository-relative coordinate by subtracting the origin; integer arithmetic does not saturate.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `absolute` | [`BlockPos`](BlockPos.md) | Absolute world position to convert. |

### Returns
[`BlockPos`](BlockPos.md) — `absolute - origin`; throws `IllegalStateException` when the selection has no origin.

---

## absolute

### Signature
```java
public BlockPos absolute(BlockPos relative)
```

### Description
Converts a repository-relative coordinate back into an absolute world position by adding the origin; integer arithmetic does not saturate.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `relative` | [`BlockPos`](BlockPos.md) | Relative coordinate measured from this selection's origin. |

### Returns
[`BlockPos`](BlockPos.md) — `origin + relative`; throws `IllegalStateException` when the selection has no origin.

---

## byName

### Signature
```java
public static Comparator<Selection> byName()
```

### Description
Returns a comparator that orders selections alphabetically by name, case-insensitively and independent of the default locale.

### Parameters
None.

### Returns
`Comparator<Selection>` — comparator using the lowercased name in `Locale.ROOT`; comparing selections with a null name throws `NullPointerException`.

---

## toString

### Signature
```java
@Override
public String toString()
```

### Description
Renders a short diagnostic form containing the name, cuboid count and origin.

### Parameters
None.

### Returns
`String` — text of the form `name (N cuboids, origin x, y, z)`; a null origin is rendered as `null`.

---

## See Also

- **Software Detailed Design:** [Selection_SDD.md](./Selection_SDD.md)
