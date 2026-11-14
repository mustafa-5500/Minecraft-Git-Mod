# StagingService

> **Software Detailed Documentation:** [StagingService_SDD.md](./StagingService_SDD.md)  
> **Source File:** [StagingService.java](../../../../../../../src/main/java/org/almond/minecraftgit/staging/StagingService.java)

## Table of Contents

- [Usage](#usage)
- [Fields](#fields)
- [StagingService (Constructor)](#stagingservice-constructor)
- [root](#root)
- [file](#file)
- [stage](#stage)
- [load](#load)
- [hasStaged](#hasstaged)
- [stagedNames](#stagednames)
- [unstage](#unstage)
- [rebaseStaged](#rebasestaged)
- [restrictStaged](#restrictstaged)
- [deleteAll](#deleteall)
- [store](#store)
- [See Also](#see-also)

## Usage

Create it during mod initialisation with the mod data root, a [`SnapshotStore`](SnapshotStore.md) and the logger, then call `stage` from `/git add` and `load` from `/git commit` or `/git status`; `file` exposes the resolved path for diagnostics. A typical sequence is `StagingService staging = new StagingService(root, new SnapshotStore(), debug); staging.stage(selection, snapshot);` followed later by `staging.load(worldId, owner, name)`.

Durable staging area. `/git add` writes a captured snapshot here;
`/git commit` reads it later. Files survive restarts and are keyed by
world id + player + selection, never by repository.

---

## Fields

- `root` (`Path`) — Normalized absolute directory that contains every per-world/per-player staging directory.
- `store` ([`SnapshotStore`](SnapshotStore.md)) — Serializer used for all staged snapshot reads and writes.
- `debug` ([`DebugLogger`](../debug/DebugLogger.md)) — Logger for stage, rebase, restrict and unstage operations.

---

## StagingService (Constructor)

### Signature
```java
public StagingService(Path root, SnapshotStore store, DebugLogger debug)
```

### Description
Stores the normalized absolute staging root and the injected store/logger; no directories are created until the first `stage` call writes a snapshot.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `root` | `Path` | Base directory for staged `.snap` files; normalized to an absolute path. |
| `store` | [`SnapshotStore`](SnapshotStore.md) | Snapshot serializer; `StagingService` only chooses paths and lifecycle. |
| `debug` | [`DebugLogger`](../debug/DebugLogger.md) | Logger used for informational staging events. |

### Returns
`StagingService` instance.

---

## root

### Signature
```java
public Path root()
```

### Description
Returns the normalized absolute staging root supplied to the constructor; the value is stable for the lifetime of the service.

### Parameters
None.

### Returns
`Path` — Absolute staging root directory.

---

## file

### Signature
```java
public Path file(String worldId, UUID owner, String selection)
```

### Description
Resolves the staged file for a world/player/selection triple to `root/<safe world>/<owner UUID>/<selection>.snap`, validating the selection name with `Names.requireSelectionName` and sanitizing the world id with `Names.safeWorldName`. An invalid selection name is reported as a [`StagingException`](StagingException.md); the method does not touch the filesystem.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | World identifier, sanitized so it is safe as a directory name. |
| `owner` | `UUID` | Owning player UUID, stringified into the path. |
| `selection` | `String` | Selection name; 1-32 letters, digits, underscores or hyphens, starting with a letter or digit. |

### Returns
`Path` — Resolved path of the staged snapshot file, which may not exist yet.

---

## stage

### Signature
```java
public StagedSelection stage(Selection selection, Snapshot snapshot)
```

### Description
Writes a capture to disk and returns the staged handle once it is durable. It requires a selection with an origin and owner, rebases the snapshot onto the selection origin when needed, and delegates the atomic write to [`SnapshotStore`](SnapshotStore.md); failures surface as [`StagingException`](StagingException.md).

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `selection` | [`Selection`](../model/Selection.md) | Selection owning the capture; must have an origin and an owner. |
| `snapshot` | [`Snapshot`](../model/Snapshot.md) | Captured snapshot; rebased onto the selection origin when the origins differ. |

### Returns
[`StagedSelection`](StagedSelection.md) — Durable handle containing the stored snapshot, its file and the staging timestamp.

---

## load

### Signature
```java
public Optional<StagedSelection> load(String worldId, UUID owner, String selection)
```

### Description
Returns the staged snapshot for the triple if the `.snap` file exists, using the file's last-modified time as `stagedAt`; if that attribute cannot be read it falls back to `Instant.now()`. It returns `Optional.empty()` when no regular file exists and lets [`StagingException`](StagingException.md) propagate when a file exists but cannot be decoded.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | World identifier used to build the staging path. |
| `owner` | `UUID` | Owner UUID used to build the staging path. |
| `selection` | `String` | Selection name used to build the staging path. |

### Returns
`Optional<StagedSelection>` — The staged snapshot when present, otherwise `Optional.empty()`.

---

## hasStaged

### Signature
```java
public boolean hasStaged(String worldId, UUID owner, String selection)
```

### Description
Reports whether a regular staged file exists for the world/player/selection triple, without reading or modifying it.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | World identifier used to build the staging path. |
| `owner` | `UUID` | Owner UUID used to build the staging path. |
| `selection` | `String` | Selection name used to build the staging path. |

### Returns
`boolean` — `true` when the staging path is a regular file.

---

## stagedNames

### Signature
```java
public List<String> stagedNames(String worldId, UUID owner)
```

### Description
Lists the selection names that have `.snap` files in the world/player staging directory, stripping the extension and sorting the names alphabetically. It returns an empty list when the directory does not exist and wraps directory-listing I/O failures in [`StagingException`](StagingException.md).

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | World identifier whose staging directory is listed. |
| `owner` | `UUID` | Owner UUID whose staging directory is listed. |

### Returns
`List<String>` — Sorted selection names with staged snapshots; empty when none exist.

---

## unstage

### Signature
```java
public void unstage(String worldId, UUID owner, String selection)
```

### Description
Deletes the staged file for the triple if it exists and logs the removal; a missing file is not an error. I/O failures are wrapped in [`StagingException`](StagingException.md).

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | World identifier of the staged file. |
| `owner` | `UUID` | Owner UUID of the staged file. |
| `selection` | `String` | Selection name whose staged snapshot is removed. |

### Returns
`void`

---

## rebaseStaged

### Signature
```java
public void rebaseStaged(String worldId, UUID owner, String selection, BlockPos newOrigin)
```

### Description
Rebases the staged snapshot after `/git selection reorigin`: it reads the file, calls `Snapshot.rebase(newOrigin)` and atomically rewrites it through [`SnapshotStore`](SnapshotStore.md). It is a no-op when no staged file exists, and read/write failures surface as [`StagingException`](StagingException.md).

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | World identifier of the staged file. |
| `owner` | `UUID` | Owner UUID of the staged file. |
| `selection` | `String` | Selection name whose staged snapshot is rebased. |
| `newOrigin` | [`BlockPos`](../model/BlockPos.md) | New absolute origin; relative coordinates are recomputed against it. |

### Returns
`void`

---

## restrictStaged

### Signature
```java
public void restrictStaged(String worldId, UUID owner, Selection selection)
```

### Description
Trim a staged snapshot after a cuboid is removed: blocks and entities outside the remaining cuboids are dropped and the survivors are rebased onto the unchanged selection origin before the file is rewritten. It is a no-op when no staged file exists or the selection has no origin (E2/E6); read/write failures surface as [`StagingException`](StagingException.md).

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | World identifier of the staged file. |
| `owner` | `UUID` | Owner UUID of the staged file. |
| `selection` | [`Selection`](../model/Selection.md) | Selection whose current cuboids define what is retained; its origin does not move. |

### Returns
`void`

---

## deleteAll

### Signature
```java
public void deleteAll(String worldId, UUID owner, String selection)
```

### Description
Deletes the staged file for the triple if present, silently treating a missing file as success; I/O failures raise [`StagingException`](StagingException.md). It is the non-logging variant of `unstage`.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | World identifier of the staged file. |
| `owner` | `UUID` | Owner UUID of the staged file. |
| `selection` | `String` | Selection name whose staged snapshot is deleted. |

### Returns
`void`

---

## store

### Signature
```java
public SnapshotStore store()
```

### Description
Returns the injected [`SnapshotStore`](SnapshotStore.md), allowing callers that already hold a `StagingService` to reuse the same serializer for other snapshot files.

### Parameters
None.

### Returns
[`SnapshotStore`](SnapshotStore.md) — The store used for staged snapshots.

---

## See Also

- **Software Detailed Design:** [StagingService_SDD.md](./StagingService_SDD.md)
