# RepoMetadataStore

> **Software Detailed Documentation:** [RepoMetadataStore_SDD.md](./RepoMetadataStore_SDD.md)  
> **Source File:** [RepoMetadataStore.java](../../../../../../../src/main/java/org/almond/minecraftgit/repo/RepoMetadataStore.java)

## Table of Contents

- [Usage](#usage)
- [Fields](#fields)
- [RepoMetadataStore (Constructor)](#repometadatastore-constructor)
- [load](#load)
- [save](#save)
- [file](#file)
- [See Also](#see-also)

## Usage

Normally created and owned by `RepoManager`, not by commands: the manager supplies `locator.metadataRoot()` and its debug logger. The store maps one world id to one readable, pretty-printed JSON file and converts every IO/parse failure into a `RepoException` whose message tells the player which file to fix or delete.

```java
RepoMetadataStore store = new RepoMetadataStore(locator.metadataRoot(), debug);
RepoMetadata metadata = store.load(worldId, new RepoOptions("main", "origin", "Alex", "alex@example.com"));
metadata.setLastSelection("castle");
store.save(metadata);
```

Reads and writes local repository metadata, one JSON file per world.

---

## Fields

- `metadataRoot` (`Path`) — Directory holding the per-world `<sanitized world id>.json` files; it is created on the first save and read on the first load.
- `debug` ([`DebugLogger`](../debug/DebugLogger.md)) — Logger called with category `repo` after each successful save; never used on the error path, which throws instead.
- `gson` (`Gson`) — Shared `Gson` configured with pretty printing and HTML escaping disabled, so `.json` files stay human-editable and URLs are not escaped; `Gson` instances are thread-safe.

---

## RepoMetadataStore (Constructor)

### Signature
```java
public RepoMetadataStore(Path metadataRoot, DebugLogger debug)
```

### Description
Stores the metadata directory and logger; no directory is created and no file is read until `load`/`save`/`file` is used. The `Gson` serializer is a field initializer and is reused for every call.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `metadataRoot` | `Path` | Directory where per-world metadata JSON files live (normally `config/minecraftgit/meta`). |
| `debug` | [`DebugLogger`](../debug/DebugLogger.md) | Debug logger used to record successful saves under the `repo` category. |

### Returns
`RepoMetadataStore` instance.

---

## load

### Signature
```java
public RepoMetadata load(String world, RepoOptions defaults)
```

### Description
Reads `file(world)` as UTF-8 JSON and normalizes the result against `defaults`. If the file does not exist it returns `RepoMetadata.defaults(world, defaults)` instead of failing. A `null` parse, invalid JSON or an unreadable file raises `RepoException` naming the path and suggesting the player delete or fix the file; a successful read returns a non-null, fully populated instance.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `world` | `String` | World key whose metadata file should be read; used both for the filename and for normalization. |
| `defaults` | [`RepoOptions`](RepoOptions.md) | Fallback branch, remote and author identity for missing fields or a missing file. |

### Returns
[`RepoMetadata`](RepoMetadata.md) — Normalized metadata (or freshly built defaults when no file exists); never `null`.

---

## save

### Signature
```java
public void save(RepoMetadata metadata)
```

### Description
Serializes `metadata` to pretty-printed JSON and writes it atomically: the JSON goes to `<file>.tmp` in the same directory, then is moved over `<file>` with `ATOMIC_MOVE`/`REPLACE_EXISTING`; if the filesystem does not support atomic moves the code falls back to a plain replacing move. It creates `metadataRoot` first and logs success; any `IOException` becomes a `RepoException` naming the target file.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `metadata` | [`RepoMetadata`](RepoMetadata.md) | Object to persist; `metadata.world()` selects the file name. |

### Returns
`void`

---

## file

### Signature
```java
public Path file(String world)
```

### Description
Computes the JSON path for a world as `metadataRoot/<Names.safeWorldName(world)>.json`, without touching the disk. Sanitization replaces every character outside `[A-Za-z0-9_.-]` with `_`, so a server address or `__` dimension key can never escape the metadata root.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `world` | `String` | World key to map to a file; `null` is treated as `world`. |

### Returns
`Path` — Path of the world's metadata file (the file may not exist).

---

## See Also

- **Software Detailed Design:** [RepoMetadataStore_SDD.md](./RepoMetadataStore_SDD.md)
