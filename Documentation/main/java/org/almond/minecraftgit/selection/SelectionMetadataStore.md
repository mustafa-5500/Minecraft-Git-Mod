# SelectionMetadataStore

> **Software Detailed Documentation:** [SelectionMetadataStore_SDD.md](./SelectionMetadataStore_SDD.md)  
> **Source File:** [SelectionMetadataStore.java](../../../../../../../src/main/java/org/almond/minecraftgit/selection/SelectionMetadataStore.java)

## Table of Contents

- [Usage](#usage)
- [Fields](#fields)
- [SelectionMetadataStore (Constructor)](#selectionmetadatastore-constructor)
- [file](#file)
- [load](#load)
- [loadFile](#loadfile)
- [loadAll](#loadall)
- [save](#save)
- [ownerFromFileName](#ownerfromfilename)
- [See Also](#see-also)

## Usage

Created by `SelectionService` with `config.selectionsRoot()`; most code should use the service, but pull/status and diagnostic code can call `store.loadAll(worldId)` or `store.file(world, owner)` directly. For example, `new SelectionMetadataStore(config.selectionsRoot()).loadAll(world.id())` returns every player's selections without needing a player UUID.

Machine-local selection state. This is intentionally stored outside the
repository: it records absolute world coordinates, owners, and the current
origin, none of which belong in a shared commit.

---

## Fields

- `FORMAT_VERSION` (`int`) — Current on-disk schema marker, written as the `format` property of every file (currently `1`); readers do not branch on it yet, but it reserves a migration hook.
- `root` (`Path`) — Absolute, normalized root directory; per-world subdirectories are created beneath it on first save.
- `gson` (`Gson`) — Pretty-printing Gson with HTML escaping disabled, used for both the wrapper object and the selections array.

---

## SelectionMetadataStore (Constructor)

### Signature
```java
public SelectionMetadataStore(Path root)
```

### Description
Normalizes `root` to an absolute path and creates the JSON codec; it performs no IO and does not create directories until `save` is called.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `root` | `Path` | Directory that holds one subdirectory per sanitized world id; must not be null. |

### Returns
`SelectionMetadataStore` instance.

---

## file

### Signature
```java
public Path file(String world, UUID owner)
```

### Description
Maps a world and owner to `root/<safeWorldName(world)>/<owner>.json`, applying `Names.safeWorldName` to keep filesystem-hostile ids out of the path. The parent directory is not created here.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `world` | `String` | World id, sanitized with `Names.safeWorldName`. |
| `owner` | `UUID` | Player UUID, used verbatim as the JSON file name. |

### Returns
`Path` — target JSON file; it may not exist yet.

---

## load

### Signature
```java
public List<Selection> load(String world, UUID owner)
```

### Description
Reads the player's file through `loadFile`, using the world id as the fallback world and an empty owner-name fallback; a missing file yields an empty list.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `world` | `String` | World id applied to selections that lack a world. |
| `owner` | `UUID` | Owner UUID applied to selections that lack an owner. |

### Returns
`List<Selection>` — normalized selections, or an empty list when the file is absent.

---

## loadFile

### Signature
```java
public List<Selection> loadFile(Path file, String world, UUID fallbackOwner, String fallbackOwnerName)
```

### Description
Parses the wrapper object, treats a missing `selections` array as empty, normalizes each non-null entry, and returns a mutable `ArrayList`. Invalid JSON raises a `SelectionException` telling the player to delete the file, while IO errors report the path and cause.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `file` | `Path` | JSON file to parse, usually obtained from `file()`. |
| `world` | `String` | World id applied to selections that lack a world. |
| `fallbackOwner` | `UUID` | Owner UUID applied to selections that lack an owner. |
| `fallbackOwnerName` | `String` | Display name applied to selections that lack an owner name. |

### Returns
`List<Selection>` — mutable list of normalized selections; empty when the file is absent.

---

## loadAll

### Signature
```java
public List<Selection> loadAll(String world)
```

### Description
All players' selections in a world. Used by status/pull for console contexts.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `world` | `String` | World id whose per-player files should be aggregated. |

### Returns
`List<Selection>` — every owner's selections sorted by name; empty when the world directory is absent.

---

## save

### Signature
```java
public void save(String world, UUID owner, String ownerName, List<Selection> selections)
```

### Description
Creates the world directory, builds the wrapper with `format`, `world`, `owner`, `ownerName` and `selections`, writes it to a `.tmp` sibling, then atomically moves it over the target. Any IO failure is wrapped in `SelectionException`, and the previous file survives a failure before the move.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `world` | `String` | World id recorded in the wrapper and used for the directory. |
| `owner` | `UUID` | Owner UUID recorded in the wrapper and used for the file name. |
| `ownerName` | `String` | Player name recorded for display; null becomes an empty string. |
| `selections` | `List<Selection>` | Selections serialized into the `selections` array in the given order. |

### Returns
`void`

---

## ownerFromFileName

### Signature
```java
private static UUID ownerFromFileName(Path file)
```

### Description
Extracts the UUID from a `<uuid>.json` file name, returning the all-zero UUID when the stem is not a valid UUID so a stray file cannot abort a `loadAll` scan.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `file` | `Path` | File whose name supplies the owner UUID. |

### Returns
`UUID` — the parsed owner, or the all-zero UUID when the name is malformed.

---

## See Also

- **Software Detailed Design:** [SelectionMetadataStore_SDD.md](./SelectionMetadataStore_SDD.md)
