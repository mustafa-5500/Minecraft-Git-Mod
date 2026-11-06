# SelectionService — Software Detailed Design

> **API Documentation:** [SelectionService.md](./SelectionService.md)  
> **Source File:** [SelectionService.java](../../../../../../../src/main/java/org/almond/minecraftgit/selection/SelectionService.java)

## Table of Contents

- [Usage](#usage)
- [1. Overview](#1-overview)
- [2. Package Declaration & Imports](#2-package-declaration-imports)
- [3. Class Declaration](#3-class-declaration)
- [4. Instance Fields](#4-instance-fields)
- [5. Constructor](#5-constructor)
- [6. `root()`](#6-root)
- [7. `list()`](#7-list)
- [8. `listAll()`](#8-listall)
- [9. `find()`](#9-find)
- [10. `get()`](#10-get)
- [11. `create()`](#11-create)
- [12. `createAt()`](#12-createat)
- [13. `addCuboid()`](#13-addcuboid)
- [14. `removeCuboid()`](#14-removecuboid)
- [15. `reorigin()`](#15-reorigin)
- [16. `delete()`](#16-delete)
- [17. `setTracked()`](#17-settracked)
- [18. `save()`](#18-save)
- [19. `store()`](#19-store)
- [20. `load()`](#20-load)
- [21. `persist()`](#21-persist)
- [22. `key()`](#22-key)
- [23. `requireName()`](#23-requirename)
- [24. `saturatedAdd()`](#24-saturatedadd)
- [25. `invalidate()`](#25-invalidate)
- [26. `tracked()`](#26-tracked)

## Usage

TODO: Describe how `SelectionService` is instantiated and used.

---

## 1. Overview

Loads, mutates, and persists named selections per world id + player. All
mutation methods write through immediately so a crash or disconnect cannot
lose a selection definition.

---

## 2. Package Declaration & Imports

```java
package org.almond.minecraftgit.selection;
```

```java
import org.almond.minecraftgit.config.ModConfig;
import org.almond.minecraftgit.model.BlockPos;
import org.almond.minecraftgit.model.Cuboid;
import org.almond.minecraftgit.model.Selection;
import org.almond.minecraftgit.util.Names;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
```

| Import | Purpose |
|--------|---------|
| [`ModConfig`](../config/ModConfig.md) | TODO: describe purpose |
| [`BlockPos`](../model/BlockPos.md) | TODO: describe purpose |
| [`Cuboid`](../model/Cuboid.md) | TODO: describe purpose |
| [`Selection`](../model/Selection.md) | TODO: describe purpose |
| [`Names`](../util/Names.md) | TODO: describe purpose |
| `Path` | TODO: describe purpose |
| `ArrayList` | TODO: describe purpose |
| `Comparator` | TODO: describe purpose |
| `List` | TODO: describe purpose |
| `Locale` | TODO: describe purpose |
| `Map` | TODO: describe purpose |
| `Optional` | TODO: describe purpose |
| `UUID` | TODO: describe purpose |
| `ConcurrentHashMap` | TODO: describe purpose |

---

## 3. Class Declaration

```java
public final class SelectionService {
```


---

## 4. Instance Fields

```java
private final ModConfig config;
private final SelectionMetadataStore store;
private final Map<String, List<Selection>> cache;
```

| Field | Type | Description |
|-------|------|-------------|
| `config` | [`ModConfig`](../config/ModConfig.md) | TODO: describe |
| `store` | [`SelectionMetadataStore`](SelectionMetadataStore.md) | TODO: describe |
| `cache` | `Map<String, List<Selection>>` | TODO: describe |

---

## 5. Constructor

```java
public SelectionService(ModConfig config)
```

TODO: Provide detailed design explanation for `SelectionService`.

---

## 6. `root()`

```java
public Path root()
```

TODO: Provide detailed design explanation for `root`.

---

## 7. `list()`

```java
public List<Selection> list(String worldId, UUID owner)
```

TODO: Provide detailed design explanation for `list`.

---

## 8. `listAll()`

```java
public List<Selection> listAll(String worldId)
```

TODO: Provide detailed design explanation for `listAll`.

---

## 9. `find()`

```java
public Optional<Selection> find(String worldId, UUID owner, String name)
```

TODO: Provide detailed design explanation for `find`.

---

## 10. `get()`

```java
public Selection get(String worldId, UUID owner, String name)
```

TODO: Provide detailed design explanation for `get`.

---

## 11. `create()`

```java
public Selection create(String worldId, UUID owner, String ownerName, String name)
```

TODO: Provide detailed design explanation for `create`.

---

## 12. `createAt()`

```java
public Selection createAt(String worldId, UUID owner, String ownerName, String name, BlockPos origin)
```

TODO: Provide detailed design explanation for `createAt`.

---

## 13. `addCuboid()`

```java
public Selection addCuboid(String worldId, UUID owner, String name, Cuboid cuboid)
```

TODO: Provide detailed design explanation for `addCuboid`.

---

## 14. `removeCuboid()`

```java
public Cuboid removeCuboid(String worldId, UUID owner, String name, int oneBasedIndex)
```

TODO: Provide detailed design explanation for `removeCuboid`.

---

## 15. `reorigin()`

```java
public BlockPos reorigin(String worldId, UUID owner, String name)
```

TODO: Provide detailed design explanation for `reorigin`.

---

## 16. `delete()`

```java
public void delete(String worldId, UUID owner, String name)
```

TODO: Provide detailed design explanation for `delete`.

---

## 17. `setTracked()`

```java
public void setTracked(String worldId, UUID owner, String name, boolean tracked)
```

TODO: Provide detailed design explanation for `setTracked`.

---

## 18. `save()`

```java
public void save(String worldId, UUID owner)
```

TODO: Provide detailed design explanation for `save`.

---

## 19. `store()`

```java
public SelectionMetadataStore store()
```

TODO: Provide detailed design explanation for `store`.

---

## 20. `load()`

```java
private List<Selection> load(String worldId, UUID owner)
```

TODO: Provide detailed design explanation for `load`.

---

## 21. `persist()`

```java
private void persist(String worldId, UUID owner, String ownerName, List<Selection> selections)
```

TODO: Provide detailed design explanation for `persist`.

---

## 22. `key()`

```java
private static String key(String worldId, UUID owner)
```

TODO: Provide detailed design explanation for `key`.

---

## 23. `requireName()`

```java
private static void requireName(String name)
```

TODO: Provide detailed design explanation for `requireName`.

---

## 24. `saturatedAdd()`

```java
private static long saturatedAdd(long a, long b)
```

TODO: Provide detailed design explanation for `saturatedAdd`.

---

## 25. `invalidate()`

```java
public void invalidate(String worldId, UUID owner)
```

TODO: Provide detailed design explanation for `invalidate`.

---

## 26. `tracked()`

```java
public List<Selection> tracked(String worldId, UUID owner)
```

TODO: Provide detailed design explanation for `tracked`.

---
