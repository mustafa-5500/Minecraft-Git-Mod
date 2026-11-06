# Selection — Software Detailed Design

> **API Documentation:** [Selection.md](./Selection.md)  
> **Source File:** [Selection.java](../../../../../../../src/main/java/org/almond/minecraftgit/model/Selection.java)

## Table of Contents

- [Usage](#usage)
- [1. Overview](#1-overview)
- [2. Package Declaration & Imports](#2-package-declaration-imports)
- [3. Class Declaration](#3-class-declaration)
- [4. Instance Fields](#4-instance-fields)
- [5. Constructor](#5-constructor)
- [6. Constructor](#6-constructor)
- [7. `normalize()`](#7-normalize)
- [8. `name()`](#8-name)
- [9. `setName()`](#9-setname)
- [10. `owner()`](#10-owner)
- [11. `setOwner()`](#11-setowner)
- [12. `ownerName()`](#12-ownername)
- [13. `setOwnerName()`](#13-setownername)
- [14. `world()`](#14-world)
- [15. `setWorld()`](#15-setworld)
- [16. `origin()`](#16-origin)
- [17. `setOrigin()`](#17-setorigin)
- [18. `hasOrigin()`](#18-hasorigin)
- [19. `cuboids()`](#19-cuboids)
- [20. `setCuboids()`](#20-setcuboids)
- [21. `addCuboid()`](#21-addcuboid)
- [22. `removeCuboidAt()`](#22-removecuboidat)
- [23. `reorigin()`](#23-reorigin)
- [24. `isTracked()`](#24-istracked)
- [25. `setTracked()`](#25-settracked)
- [26. `createdAt()`](#26-createdat)
- [27. `updatedAt()`](#27-updatedat)
- [28. `touch()`](#28-touch)
- [29. `cuboidCount()`](#29-cuboidcount)
- [30. `volume()`](#30-volume)
- [31. `boundsMin()`](#31-boundsmin)
- [32. `boundsMax()`](#32-boundsmax)
- [33. `contains()`](#33-contains)
- [34. `relative()`](#34-relative)
- [35. `absolute()`](#35-absolute)
- [36. `byName()`](#36-byname)
- [37. `toString()`](#37-tostring)

## Usage

TODO: Describe how `Selection` is instantiated and used.

---

## 1. Overview

A named, multi-cuboid build selection owned by one player in one world.
<p>Selections are persisted per world id + player in the mod data folder and
are never committed. The origin is the absolute world position that relative
repository coordinates are measured from. It is set by the first cuboid and
deliberately does <em>not</em> move when that cuboid is removed (E2); only an
explicit re-origin or a new selection changes it.</p>

---

## 2. Package Declaration & Imports

```java
package org.almond.minecraftgit.model;
```

```java
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
```

| Import | Purpose |
|--------|---------|
| `Instant` | TODO: describe purpose |
| `ArrayList` | TODO: describe purpose |
| `Collections` | TODO: describe purpose |
| `Comparator` | TODO: describe purpose |
| `List` | TODO: describe purpose |
| `UUID` | TODO: describe purpose |

---

## 3. Class Declaration

```java
public final class Selection {
```


---

## 4. Instance Fields

```java
private String name;
private UUID owner;
private String ownerName;
private String world;
private BlockPos origin;
private List<Cuboid> cuboids;
private boolean tracked;
private String createdAt;
private String updatedAt;
```

| Field | Type | Description |
|-------|------|-------------|
| `name` | `String` | TODO: describe |
| `owner` | `UUID` | TODO: describe |
| `ownerName` | `String` | TODO: describe |
| `world` | `String` | TODO: describe |
| `origin` | [`BlockPos`](BlockPos.md) | TODO: describe |
| `cuboids` | `List<Cuboid>` | TODO: describe |
| `tracked` | `boolean` | TODO: describe |
| `createdAt` | `String` | TODO: describe |
| `updatedAt` | `String` | TODO: describe |

---

## 5. Constructor

```java
public Selection()
```

TODO: Provide detailed design explanation for `Selection`.

---

## 6. Constructor

```java
public Selection(String name, UUID owner, String ownerName, String world, BlockPos origin)
```

TODO: Provide detailed design explanation for `Selection`.

---

## 7. `normalize()`

```java
public Selection normalize(String fallbackWorld, UUID fallbackOwner, String fallbackOwnerName)
```

TODO: Provide detailed design explanation for `normalize`.

---

## 8. `name()`

```java
public String name()
```

TODO: Provide detailed design explanation for `name`.

---

## 9. `setName()`

```java
public void setName(String name)
```

TODO: Provide detailed design explanation for `setName`.

---

## 10. `owner()`

```java
public UUID owner()
```

TODO: Provide detailed design explanation for `owner`.

---

## 11. `setOwner()`

```java
public void setOwner(UUID owner)
```

TODO: Provide detailed design explanation for `setOwner`.

---

## 12. `ownerName()`

```java
public String ownerName()
```

TODO: Provide detailed design explanation for `ownerName`.

---

## 13. `setOwnerName()`

```java
public void setOwnerName(String ownerName)
```

TODO: Provide detailed design explanation for `setOwnerName`.

---

## 14. `world()`

```java
public String world()
```

TODO: Provide detailed design explanation for `world`.

---

## 15. `setWorld()`

```java
public void setWorld(String world)
```

TODO: Provide detailed design explanation for `setWorld`.

---

## 16. `origin()`

```java
public BlockPos origin()
```

TODO: Provide detailed design explanation for `origin`.

---

## 17. `setOrigin()`

```java
public void setOrigin(BlockPos origin)
```

TODO: Provide detailed design explanation for `setOrigin`.

---

## 18. `hasOrigin()`

```java
public boolean hasOrigin()
```

TODO: Provide detailed design explanation for `hasOrigin`.

---

## 19. `cuboids()`

```java
public List<Cuboid> cuboids()
```

TODO: Provide detailed design explanation for `cuboids`.

---

## 20. `setCuboids()`

```java
public void setCuboids(List<Cuboid> cuboids)
```

TODO: Provide detailed design explanation for `setCuboids`.

---

## 21. `addCuboid()`

```java
public void addCuboid(Cuboid cuboid)
```

Append a cuboid, re-sort by smallest corner, and seed the origin.

---

## 22. `removeCuboidAt()`

```java
public Cuboid removeCuboidAt(int index)
```

Remove a cuboid by zero-based position in the sorted list.

---

## 23. `reorigin()`

```java
public BlockPos reorigin()
```

Rebase the origin onto the smallest remaining cuboid corner (E3).

---

## 24. `isTracked()`

```java
public boolean isTracked()
```

TODO: Provide detailed design explanation for `isTracked`.

---

## 25. `setTracked()`

```java
public void setTracked(boolean tracked)
```

TODO: Provide detailed design explanation for `setTracked`.

---

## 26. `createdAt()`

```java
public String createdAt()
```

TODO: Provide detailed design explanation for `createdAt`.

---

## 27. `updatedAt()`

```java
public String updatedAt()
```

TODO: Provide detailed design explanation for `updatedAt`.

---

## 28. `touch()`

```java
public void touch()
```

TODO: Provide detailed design explanation for `touch`.

---

## 29. `cuboidCount()`

```java
public int cuboidCount()
```

TODO: Provide detailed design explanation for `cuboidCount`.

---

## 30. `volume()`

```java
public long volume()
```

Sum of cuboid volumes, saturated. Overlaps are counted twice.

---

## 31. `boundsMin()`

```java
public BlockPos boundsMin()
```

TODO: Provide detailed design explanation for `boundsMin`.

---

## 32. `boundsMax()`

```java
public BlockPos boundsMax()
```

TODO: Provide detailed design explanation for `boundsMax`.

---

## 33. `contains()`

```java
public boolean contains(BlockPos pos)
```

TODO: Provide detailed design explanation for `contains`.

---

## 34. `relative()`

```java
public BlockPos relative(BlockPos absolute)
```

TODO: Provide detailed design explanation for `relative`.

---

## 35. `absolute()`

```java
public BlockPos absolute(BlockPos relative)
```

TODO: Provide detailed design explanation for `absolute`.

---

## 36. `byName()`

```java
public static Comparator<Selection> byName()
```

TODO: Provide detailed design explanation for `byName`.

---

## 37. `toString()`

```java
@Override
public String toString()
```

TODO: Provide detailed design explanation for `toString`.

---
