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

TODO: Describe how `Snapshot` is instantiated and used.

---

## 1. Overview

Immutable capture of one selection at one moment. Blocks and entities carry
coordinates relative to {@link #origin()} so the repository representation is
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
| `Instant` | TODO: describe purpose |
| `ArrayList` | TODO: describe purpose |
| `Collections` | TODO: describe purpose |
| `LinkedHashMap` | TODO: describe purpose |
| `List` | TODO: describe purpose |
| `Map` | TODO: describe purpose |

---

## 3. Class Declaration

```java
public final class Snapshot {
```


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
| `selection` | `String` | TODO: describe |
| `origin` | [`BlockPos`](BlockPos.md) | TODO: describe |
| `blocks` | `List<BlockSnapshot>` | TODO: describe |
| `entities` | `List<EntitySnapshot>` | TODO: describe |
| `capturedAt` | `Instant` | TODO: describe |

---

## 5. Constructor

```java
public Snapshot(String selection, BlockPos origin, List<BlockSnapshot> blocks,
                    List<EntitySnapshot> entities, Instant capturedAt)
```

TODO: Provide detailed design explanation for `Snapshot`.

---

## 6. `empty()`

```java
public static Snapshot empty(String selection, BlockPos origin)
```

TODO: Provide detailed design explanation for `empty`.

---

## 7. `selection()`

```java
public String selection()
```

TODO: Provide detailed design explanation for `selection`.

---

## 8. `origin()`

```java
public BlockPos origin()
```

TODO: Provide detailed design explanation for `origin`.

---

## 9. `blocks()`

```java
public List<BlockSnapshot> blocks()
```

TODO: Provide detailed design explanation for `blocks`.

---

## 10. `entities()`

```java
public List<EntitySnapshot> entities()
```

TODO: Provide detailed design explanation for `entities`.

---

## 11. `capturedAt()`

```java
public Instant capturedAt()
```

TODO: Provide detailed design explanation for `capturedAt`.

---

## 12. `blockCount()`

```java
public int blockCount()
```

TODO: Provide detailed design explanation for `blockCount`.

---

## 13. `entityCount()`

```java
public int entityCount()
```

TODO: Provide detailed design explanation for `entityCount`.

---

## 14. `isEmpty()`

```java
public boolean isEmpty()
```

TODO: Provide detailed design explanation for `isEmpty`.

---

## 15. `rebase()`

```java
public Snapshot rebase(BlockPos newOrigin)
```

Return an equivalent snapshot measured from a new origin. Absolute world
positions do not change; only the relative coordinates do.

---

## 16. `retain()`

```java
public Snapshot retain(Selection selection)
```

Keep only blocks and entities inside the selection's current cuboids.

---

## 17. `byRelative()`

```java
public Map<BlockPos, BlockSnapshot> byRelative()
```

TODO: Provide detailed design explanation for `byRelative`.

---

## 18. `byAbsolute()`

```java
public Map<BlockPos, BlockSnapshot> byAbsolute()
```

TODO: Provide detailed design explanation for `byAbsolute`.

---

## 19. `volume()`

```java
public long volume()
```

TODO: Provide detailed design explanation for `volume`.

---

## 20. `dimensions()`

```java
public String dimensions()
```

TODO: Provide detailed design explanation for `dimensions`.

---

## 21. `toString()`

```java
@Override
public String toString()
```

TODO: Provide detailed design explanation for `toString`.

---
