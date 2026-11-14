# SelectionService

> **Software Detailed Documentation:** [SelectionService_SDD.md](./SelectionService_SDD.md)  
> **Source File:** [SelectionService.java](../../../../../../../src/main/java/org/almond/minecraftgit/selection/SelectionService.java)

## Table of Contents

- [Usage](#usage)
- [Fields](#fields)
- [SelectionService (Constructor)](#selectionservice-constructor)
- [root](#root)
- [list](#list)
- [listAll](#listall)
- [find](#find)
- [get](#get)
- [create](#create)
- [createAt](#createat)
- [addCuboid](#addcuboid)
- [removeCuboid](#removecuboid)
- [reorigin](#reorigin)
- [delete](#delete)
- [setTracked](#settracked)
- [save](#save)
- [store](#store)
- [load](#load)
- [persist](#persist)
- [key](#key)
- [requireName](#requirename)
- [saturatedAdd](#saturatedadd)
- [invalidate](#invalidate)
- [tracked](#tracked)
- [See Also](#see-also)

## Usage

Obtain the shared instance from `GitServices.selections()` and pass the current `world.id()` plus the player UUID to every call; mutations validate, update the cached list, and persist the player's JSON before returning. For example, `services.selections().create(world.id(), player.getUUID(), player.getGameProfile().getName(), name)` followed by `services.selections().addCuboid(world.id(), player.getUUID(), name, cuboid)` records a new selection and its first cuboid immediately. Selections are local-only metadata under `config.selectionsRoot()` and are never committed; commits contain block snapshots, not selection definitions.

Loads, mutates, and persists named selections per world id + player. All
mutation methods write through immediately so a crash or disconnect cannot
lose a selection definition.

---

## Fields

- `config` ([`ModConfig`](../config/ModConfig.md)) — Supplies `selectionsRoot()` for the store and the `maxCuboidsPerSelection`/`maxVolumePerSelection` caps read on each add; captured at construction.
- `store` ([`SelectionMetadataStore`](SelectionMetadataStore.md)) — Metadata store rooted at `config.selectionsRoot()`; the only object that reads or atomically writes selection JSON files.
- `cache` (`Map<String, List<Selection>>`) — Thread-safe cache keyed by world id plus owner UUID, holding live mutable lists; `load` fills entries lazily and `persist` replaces them after a successful write.

---

## SelectionService (Constructor)

### Signature
```java
public SelectionService(ModConfig config)
```

### Description
Creates the service and its `SelectionMetadataStore` under `config.selectionsRoot()` without touching disk; the first `load` for a world/player performs the read. The supplied config also provides the cuboid-count and volume caps.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `config` | [`ModConfig`](../config/ModConfig.md) | Mod configuration providing the data root and the capacity limits; must not be null. |

### Returns
`SelectionService` instance.

---

## root

### Signature
```java
public Path root()
```

### Description
Returns `config.selectionsRoot()`, the directory under which per-world and per-player selection metadata is stored.

### Parameters
None.

### Returns
`Path` — absolute root directory of the selection metadata tree.

---

## list

### Signature
```java
public List<Selection> list(String worldId, UUID owner)
```

### Description
Returns an immutable copy of this player's selections in the world, loading the cache entry from disk on first use. Later mutations are not reflected in the returned list, so callers should re-read or use the live APIs.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | World id the selections belong to, typically `WorldContext.id()`. |
| `owner` | `UUID` | UUID of the player who owns the selections, and therefore the cache entry and file. |

### Returns
`List<Selection>` — immutable copy in persisted name order, which is normally name-sorted.

---

## listAll

### Signature
```java
public List<Selection> listAll(String worldId)
```

### Description
Reads every player's selections for one world directly through the store, bypassing the cache, for console/status and pull contexts. Results are sorted by name and a missing world directory yields an empty list.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | World id whose per-player files should be aggregated. |

### Returns
`List<Selection>` — all owners' selections in the world, sorted by name.

---

## find

### Signature
```java
public Optional<Selection> find(String worldId, UUID owner, String name)
```

### Description
Case-insensitive lookup of one selection in the player's cached list; returns empty for a null name or an unknown selection. It never throws and performs no disk write.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | World id that scopes the lookup. |
| `owner` | `UUID` | Owner whose cached list is searched. |
| `name` | `String` | Selection name to match, case-insensitively. |

### Returns
`Optional<Selection>` — the matching cached selection, or empty when absent.

---

## get

### Signature
```java
public Selection get(String worldId, UUID owner, String name)
```

### Description
Like `find`, but throws `SelectionException` when no selection matches, including a null name, so mutating commands can rely on a non-null result.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | World id that scopes the lookup. |
| `owner` | `UUID` | Owner whose cached list is searched. |
| `name` | `String` | Case-insensitive name of the required selection. |

### Returns
[`Selection`](../model/Selection.md) — the matching selection; never null.

---

## create

### Signature
```java
public Selection create(String worldId, UUID owner, String ownerName, String name)
```

### Description
Convenience for `createAt(..., null)` that creates a cuboid-less selection with no origin yet; the first added cuboid later seeds the origin.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | World id stored on the selection and used as the metadata directory. |
| `owner` | `UUID` | UUID of the owning player; selects the metadata file and cache entry. |
| `ownerName` | `String` | Last-known player name persisted for display. |
| `name` | `String` | New selection name; validated and compared case-insensitively. |

### Returns
[`Selection`](../model/Selection.md) — the created, already-persisted selection.

---

## createAt

### Signature
```java
public Selection createAt(String worldId, UUID owner, String ownerName, String name, BlockPos origin)
```

### Description
Validates the name, rejects a case-insensitive duplicate, appends a selection with the given owner name, world and origin, sorts by name, and persists it immediately. A null origin is allowed and is seeded by the first cuboid added later.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | World id stored on the selection and used as the metadata directory. |
| `owner` | `UUID` | UUID of the owning player; selects the metadata file and cache entry. |
| `ownerName` | `String` | Last-known player name persisted for display. |
| `name` | `String` | 1-32 character name matching `[A-Za-z0-9][A-Za-z0-9_-]{0,31}`, compared case-insensitively. |
| `origin` | [`BlockPos`](../model/BlockPos.md) | Absolute world position used as the origin for relative coordinates, or null when not yet known. |

### Returns
[`Selection`](../model/Selection.md) — the created selection, already persisted and cached.

---

## addCuboid

### Signature
```java
public Selection addCuboid(String worldId, UUID owner, String name, Cuboid cuboid)
```

### Description
Appends a cuboid after checking `maxCuboidsPerSelection` and the saturated `maxVolumePerSelection` cap, then persists the player's list. Both limits are checked before mutation, so a rejected cuboid leaves the selection unchanged.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | World id that scopes the selection. |
| `owner` | `UUID` | Owner of the selection. |
| `name` | `String` | Case-insensitive selection name. |
| `cuboid` | [`Cuboid`](../model/Cuboid.md) | Cuboid in absolute world coordinates; its volume is added to the running total and overlaps are counted twice. |

### Returns
[`Selection`](../model/Selection.md) — the updated selection.

---

## removeCuboid

### Signature
```java
public Cuboid removeCuboid(String worldId, UUID owner, String name, int oneBasedIndex)
```

### Description
Removes the cuboid at the given one-based position from the sorted list and persists; the origin is deliberately retained. Throws `SelectionException` when the index is outside `1..cuboidCount()`.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | World id that scopes the selection. |
| `owner` | `UUID` | Owner of the selection. |
| `name` | `String` | Case-insensitive selection name. |
| `oneBasedIndex` | `int` | One-based position in the cuboid list as displayed to the player. |

### Returns
[`Cuboid`](../model/Cuboid.md) — the removed cuboid.

---

## reorigin

### Signature
```java
public BlockPos reorigin(String worldId, UUID owner, String name)
```

### Description
Rebases the origin onto the smallest corner of the first remaining cuboid and persists, returning the previous origin. Throws `SelectionException` for an unknown selection or one with no cuboids.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | World id that scopes the selection. |
| `owner` | `UUID` | Owner of the selection. |
| `name` | `String` | Case-insensitive selection name. |

### Returns
[`BlockPos`](../model/BlockPos.md) — the previous origin

---

## delete

### Signature
```java
public void delete(String worldId, UUID owner, String name)
```

### Description
Finds the selection case-insensitively, removes it from the player's list, and persists immediately; throws `SelectionException` if no selection has that name.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | World id that scopes the selection. |
| `owner` | `UUID` | Owner of the selection. |
| `name` | `String` | Case-insensitive name of the selection to delete. |

### Returns
`void`

---

## setTracked

### Signature
```java
public void setTracked(String worldId, UUID owner, String name, boolean tracked)
```

### Description
Sets the selection's tracked flag, which controls whether staging and status include it, and persists the player's whole list immediately.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | World id that scopes the selection. |
| `owner` | `UUID` | Owner of the selection. |
| `name` | `String` | Case-insensitive selection name. |
| `tracked` | `boolean` | `true` to track the selection for staging and status, `false` to exclude it. |

### Returns
`void`

---

## save

### Signature
```java
public void save(String worldId, UUID owner)
```

### Description
Re-sorts and rewrites the player's cached selections even without a new mutation, deriving `ownerName` from the first entry and using an empty string when the list is empty. Useful after editing cached `Selection` objects in place.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | World whose cached selections should be written. |
| `owner` | `UUID` | Owner whose cache entry and file should be written. |

### Returns
`void`

---

## store

### Signature
```java
public SelectionMetadataStore store()
```

### Description
Exposes the underlying metadata store for raw access such as `loadAll` and `loadFile`, bypassing the per-player cache.

### Parameters
None.

### Returns
[`SelectionMetadataStore`](SelectionMetadataStore.md) — the store created in the constructor.

---

## load

### Signature
```java
private List<Selection> load(String worldId, UUID owner)
```

### Description
Returns the cached list for the world/owner key, reading that player's JSON through the store on a cache miss. The returned list is the live mutable cache entry, not a copy.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | World id of the cache key. |
| `owner` | `UUID` | Owner UUID of the cache key. |

### Returns
`List<Selection>` — the live cached list.

---

## persist

### Signature
```java
private void persist(String worldId, UUID owner, String ownerName, List<Selection> selections)
```

### Description
Sorts the list by lower-cased name, writes it through `store.save`, then installs the same list as the cache entry. A failed write throws before the cache is touched, keeping memory and disk consistent.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | World id of the cache key and file directory. |
| `owner` | `UUID` | Owner UUID of the cache key and file name. |
| `ownerName` | `String` | Player name persisted in the file wrapper. |
| `selections` | `List<Selection>` | Selections to sort, persist and install; mutated in place by the sort. |

### Returns
`void`

---

## key

### Signature
```java
private static String key(String worldId, UUID owner)
```

### Description
Builds the cache key by concatenating the world id, a `|` separator, and the owner UUID. A UUID never contains `|`, so the pair is unambiguous, and no composite key object is allocated.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | World id part of the key. |
| `owner` | `UUID` | Owner UUID part of the key. |

### Returns
`String` — cache key of the form `worldId|uuid`.

---

## requireName

### Signature
```java
private static void requireName(String name)
```

### Description
Validates a new selection name via `Names.requireSelectionName`, translating its `IllegalArgumentException` into a user-facing `SelectionException`; returns silently on success.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `name` | `String` | Candidate name to validate. |

### Returns
`void`

---

## saturatedAdd

### Signature
```java
private static long saturatedAdd(long a, long b)
```

### Description
Adds two non-negative volumes and clamps at `Long.MAX_VALUE` instead of overflowing, so the volume-cap comparison stays correct for very large selections.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `a` | `long` | Current accumulated volume, non-negative. |
| `b` | `long` | Volume to add, non-negative. |

### Returns
`long` — the sum, or `Long.MAX_VALUE` on overflow.

---

## invalidate

### Signature
```java
public void invalidate(String worldId, UUID owner)
```

### Description
Evicts the player's cache entry so the next access re-reads the JSON file, picking up external or console-side changes. In-memory edits that were not persisted are discarded.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | World id of the cache key. |
| `owner` | `UUID` | Owner UUID of the cache key. |

### Returns
`void`

---

## tracked

### Signature
```java
public List<Selection> tracked(String worldId, UUID owner)
```

### Description
Returns a fresh mutable list containing only tracked selections in persisted name order, loading the player's cache entry if needed. Callers use it to restrict work to selections the player has explicitly tracked.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | World id that scopes the selection list. |
| `owner` | `UUID` | Owner whose selections are filtered. |

### Returns
`List<Selection>` — new list of tracked selections.

---

## See Also

- **Software Detailed Design:** [SelectionService_SDD.md](./SelectionService_SDD.md)
