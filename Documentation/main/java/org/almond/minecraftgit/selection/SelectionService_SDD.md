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

Constructed once by `GitServices` as `new SelectionService(config)` and reached through `services.selections()`; client commands, the wand handler and the renderer pass the current world id and player UUID. Because every mutation writes through, the cache is a session-level convenience rather than the source of truth. These selections are local-only state and are never committed; only their captured block snapshots reach the repository.

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
| [`ModConfig`](../config/ModConfig.md) | Supplies the data root (`selectionsRoot()`) and the cuboid-count and volume limits used by `addCuboid`. |
| [`BlockPos`](../model/BlockPos.md) | Carries the absolute origin values passed to `createAt` and returned by `reorigin`. |
| [`Cuboid`](../model/Cuboid.md) | The cuboid aggregate added to and removed from a selection, and the source of volume for cap checks. |
| [`Selection`](../model/Selection.md) | The persisted aggregate this service loads, mutates and saves. |
| [`Names`](../util/Names.md) | Validates a new selection name before it becomes a repository or metadata path. |
| `Path` | Return type of `root()`. |
| `ArrayList` | Builds a fresh result list in `tracked()`. |
| `Comparator` | Sorts selections before persistence by their lower-cased name. |
| `List` | Collection type exposed by the list and cache accessors. |
| `Locale` | Supplies `Locale.ROOT` so name ordering is independent of the player's locale. |
| `Map` | Field type of the world/owner cache. |
| `Optional` | Makes absence explicit in `find()`. |
| `UUID` | Player identity in method signatures and in the cache key. |
| `ConcurrentHashMap` | Thread-safe map backing the per-world and per-player cache. |

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
| `config` | [`ModConfig`](../config/ModConfig.md) | Captured configuration used for the store root and for the capacity limits consulted on each add. |
| `store` | [`SelectionMetadataStore`](SelectionMetadataStore.md) | Metadata store rooted at `config.selectionsRoot()`; the only component that touches JSON files. |
| `cache` | `Map<String, List<Selection>>` | `ConcurrentHashMap` keyed by world id plus owner UUID, holding the live mutable lists so repeat reads avoid disk; `persist` replaces entries after a successful write. |

---

## 5. Constructor

```java
public SelectionService(ModConfig config)
```

Stores the config, derives the metadata store from `config.selectionsRoot()`, and leaves the cache empty; no file IO happens until the first list, find or get call. Each `GitServices` instance owns exactly one service, so caches are scoped to the client session.

---

## 6. `root()`

```java
public Path root()
```

Delegates to `config.selectionsRoot()` and is exposed so diagnostics can locate the metadata tree without reaching into the store. No IO occurs.

---

## 7. `list()`

```java
public List<Selection> list(String worldId, UUID owner)
```

Resolves the cache entry through `load` and wraps it with `List.copyOf`, returning an immutable snapshot so command code cannot mutate the cached list accidentally. Order is the persisted name order.

---

## 8. `listAll()`

```java
public List<Selection> listAll(String worldId)
```

Bypasses the cache and asks the store to scan every JSON file in the world directory, aggregating and sorting all players' selections; used by console and status paths that have no player UUID.

---

## 9. `find()`

```java
public Optional<Selection> find(String worldId, UUID owner, String name)
```

Null-safe, case-insensitive linear scan over the player's cached list; deliberately returns an empty `Optional` instead of throwing so the renderer can probe for an active selection cheaply. It uses `equalsIgnoreCase`, matching the duplicate rule.

---

## 10. `get()`

```java
public Selection get(String worldId, UUID owner, String name)
```

Thin wrapper over `find` that raises `SelectionException` with the missing name; all mutating methods funnel through it so a missing selection produces a consistent, chat-friendly error.

---

## 11. `create()`

```java
public Selection create(String worldId, UUID owner, String ownerName, String name)
```

Delegates to `createAt` with a null origin, so the origin is seeded by the first cuboid added later.

---

## 12. `createAt()`

```java
public Selection createAt(String worldId, UUID owner, String ownerName, String name, BlockPos origin)
```

Validates the name, rejects case-insensitive duplicates, constructs the `Selection`, appends and sorts it, then calls `persist`, so memory and disk are updated together. Name validation is translated to `SelectionException` by `requireName`.

---

## 13. `addCuboid()`

```java
public Selection addCuboid(String worldId, UUID owner, String name, Cuboid cuboid)
```

Fetches the selection, checks the cuboid-count cap and the saturated total-volume cap before mutating, then appends via `Selection.addCuboid`, which re-sorts cuboids and seeds an unset origin, and persists the player's list. Using `saturatedAdd` keeps overflow from slipping past the cap.

---

## 14. `removeCuboid()`

```java
public Cuboid removeCuboid(String worldId, UUID owner, String name, int oneBasedIndex)
```

Resolves the selection, validates the one-based index against `cuboidCount()`, removes the zero-based cuboid, and persists. `Selection.removeCuboidAt` intentionally retains the origin (E2), so existing staged blocks keep their reference point.

---

## 15. `reorigin()`

```java
public BlockPos reorigin(String worldId, UUID owner, String name)
```

Requires at least one cuboid, calls `Selection.reorigin()` to move the origin onto the smallest corner of the first sorted cuboid, persists, and returns the previous origin for the command's feedback message.

---

## 16. `delete()`

```java
public void delete(String worldId, UUID owner, String name)
```

Scans the cached list case-insensitively, removes the match, and persists; unlike `get` it does its own lookup so the removed object's `ownerName` can be written. Throws when nothing matches.

---

## 17. `setTracked()`

```java
public void setTracked(String worldId, UUID owner, String name, boolean tracked)
```

Flips the tracked flag on the resolved selection and persists the whole list; tracking is the per-selection flag that decides staging and status participation.

---

## 18. `save()`

```java
public void save(String worldId, UUID owner)
```

Re-sorts and rewrites the cached list even without a mutation, deriving `ownerName` from the first entry; intended for cached `Selection` instances edited in place by callers.

---

## 19. `store()`

```java
public SelectionMetadataStore store()
```

Accessor for the metadata store so pull and status code can call `loadAll` or `loadFile` without duplicating the root; it does not bypass the cache automatically.

---

## 20. `load()`

```java
private List<Selection> load(String worldId, UUID owner)
```

Lazy cache loader using `computeIfAbsent`: the store reads the player's JSON the first time and later calls return the same mutable list. No extra synchronisation is used beyond the map's per-key atomicity, so returned lists should be treated as session-internal.

---

## 21. `persist()`

```java
private void persist(String worldId, UUID owner, String ownerName, List<Selection> selections)
```

Sorts in place with a locale-independent lower-case comparator, writes through the store, which performs the atomic temp-file move, then puts the list into the cache. If the store throws, the cache keeps its old entry and memory does not diverge from disk.

---

## 22. `key()`

```java
private static String key(String worldId, UUID owner)
```

Concatenates the world id and owner UUID with a `|`, giving a stable map key without allocating a composite record. UUID text cannot contain `|`, so the pair is unambiguous.

---

## 23. `requireName()`

```java
private static void requireName(String name)
```

Bridges the validation layer to the UI layer: `Names.requireSelectionName` throws `IllegalArgumentException`, which is rethrown as `SelectionException` so commands print it verbatim. Returns nothing on success.

---

## 24. `saturatedAdd()`

```java
private static long saturatedAdd(long a, long b)
```

Guards `a > Long.MAX_VALUE - b` and clamps to `Long.MAX_VALUE`; volumes are non-negative, so this is a safe saturating add used only for cap comparisons.

---

## 25. `invalidate()`

```java
public void invalidate(String worldId, UUID owner)
```

Removes the cache entry, forcing a re-read on the next access; useful after external or console writes to one player's file. Unpersisted in-memory edits are discarded.

---

## 26. `tracked()`

```java
public List<Selection> tracked(String worldId, UUID owner)
```

Filters the cached list into a fresh `ArrayList` of tracked selections, preserving persisted name order; callers can use it instead of filtering `list()` themselves.

---
