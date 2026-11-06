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

TODO: Describe how `BuildDiff` is instantiated and used.

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
| `ArrayList` | TODO: describe purpose |
| `LinkedHashMap` | TODO: describe purpose |
| `List` | TODO: describe purpose |
| `Map` | TODO: describe purpose |

---

## 3. Class Declaration

```java
public final class BuildDiff {
```


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
| `added` | `List<BlockSnapshot>` | TODO: describe |
| `changed` | `List<BlockSnapshot>` | TODO: describe |
| `removed` | `List<BlockSnapshot>` | TODO: describe |
| `entitiesAdded` | `List<EntitySnapshot>` | TODO: describe |
| `entitiesRemoved` | `List<EntitySnapshot>` | TODO: describe |
| `unchanged` | `int` | TODO: describe |

---

## 5. Constructor

```java
private BuildDiff(List<BlockSnapshot> added, List<BlockSnapshot> changed, List<BlockSnapshot> removed,
                      List<EntitySnapshot> entitiesAdded, List<EntitySnapshot> entitiesRemoved, int unchanged)
```

TODO: Provide detailed design explanation for `BuildDiff`.

---

## 6. `between()`

```java
public static BuildDiff between(Snapshot before, Snapshot after)
```

TODO: Provide detailed design explanation for `between`.

---

## 7. `sameBlock()`

```java
private static boolean sameBlock(BlockSnapshot a, BlockSnapshot b)
```

TODO: Provide detailed design explanation for `sameBlock`.

---

## 8. `mergeBlocks()`

```java
public static Map<BlockPos, BlockSnapshot> mergeBlocks(Map<BlockPos, BlockSnapshot>... maps)
```

Merge maps keyed by relative coordinate, final state wins.

---

## 9. `added()`

```java
public List<BlockSnapshot> added()
```

TODO: Provide detailed design explanation for `added`.

---

## 10. `changed()`

```java
public List<BlockSnapshot> changed()
```

TODO: Provide detailed design explanation for `changed`.

---

## 11. `removed()`

```java
public List<BlockSnapshot> removed()
```

TODO: Provide detailed design explanation for `removed`.

---

## 12. `entitiesAdded()`

```java
public List<EntitySnapshot> entitiesAdded()
```

TODO: Provide detailed design explanation for `entitiesAdded`.

---

## 13. `entitiesRemoved()`

```java
public List<EntitySnapshot> entitiesRemoved()
```

TODO: Provide detailed design explanation for `entitiesRemoved`.

---

## 14. `unchanged()`

```java
public int unchanged()
```

TODO: Provide detailed design explanation for `unchanged`.

---

## 15. `totalBlockChanges()`

```java
public long totalBlockChanges()
```

TODO: Provide detailed design explanation for `totalBlockChanges`.

---

## 16. `totalEntityChanges()`

```java
public long totalEntityChanges()
```

TODO: Provide detailed design explanation for `totalEntityChanges`.

---

## 17. `isEmpty()`

```java
public boolean isEmpty()
```

TODO: Provide detailed design explanation for `isEmpty`.

---

## 18. `summary()`

```java
public String summary()
```

TODO: Provide detailed design explanation for `summary`.

---
