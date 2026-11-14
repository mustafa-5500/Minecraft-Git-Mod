# RepoLocator

> **Software Detailed Documentation:** [RepoLocator_SDD.md](./RepoLocator_SDD.md)  
> **Source File:** [RepoLocator.java](../../../../../../../src/main/java/org/almond/minecraftgit/repo/RepoLocator.java)

## Table of Contents

- [Usage](#usage)
- [Fields](#fields)
- [RepoLocator (Constructor)](#repolocator-constructor)
- [repoRoot](#reporoot)
- [metadataRoot](#metadataroot)
- [repoPath](#repopath)
- [metadataPath](#metadatapath)
- [See Also](#see-also)

## Usage

`RepoManager` creates one instance from `config.reposRoot()` and `config.metaRoot()`; the rest of the codebase asks it for paths instead of building them by hand. Both methods are pure path arithmetic — no directories are created and no existence checks are made.

```java
RepoLocator locator = new RepoLocator(config.reposRoot(), config.metaRoot());
Path repo = locator.repoPath(worldId);        // config/minecraftgit/repos/<world>
Path meta = locator.metadataPath(worldId);    // config/minecraftgit/meta/<world>.json
```

Decides where repositories and their local-only metadata live.
Repositories live in the mod data folder
(`config/minecraftgit/repos/<world>`), not inside the world folder.
World backups therefore never capture a hot `.git` directory, and a
repository is not accidentally copied with the map.

---

## Fields

- `repoRoot` (`Path`) — Absolute, normalized directory under which each world gets its own subdirectory containing the Git repository.
- `metadataRoot` (`Path`) — Absolute, normalized directory holding the per-world `<world>.json` metadata files.

---

## RepoLocator (Constructor)

### Signature
```java
public RepoLocator(Path repoRoot, Path metadataRoot)
```

### Description
Stores both roots after converting them with `toAbsolutePath().normalize()`, so every path derived later is absolute and free of `.`/`..` segments regardless of the process working directory.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `repoRoot` | `Path` | Directory that holds one repository subdirectory per world (normally `config/minecraftgit/repos`). |
| `metadataRoot` | `Path` | Directory that holds the per-world metadata JSON files (normally `config/minecraftgit/meta`). |

### Returns
`RepoLocator` instance.

---

## repoRoot

### Signature
```java
public Path repoRoot()
```

### Description
Returns the normalized repository root. `RepoManager` passes the derived paths to `RepoService`, and diagnostics may print the root to show where world data lives.

### Parameters
None.

### Returns
`Path` — Absolute, normalized repository root directory.

---

## metadataRoot

### Signature
```java
public Path metadataRoot()
```

### Description
Returns the normalized metadata root. `RepoManager` uses it to construct the `RepoMetadataStore`.

### Parameters
None.

### Returns
`Path` — Absolute, normalized metadata root directory.

---

## repoPath

### Signature
```java
public Path repoPath(String worldName)
```

### Description
Resolves `repoRoot/<Names.safeWorldName(worldName)>`. The sanitizer replaces characters outside `[A-Za-z0-9_.-]` with `_`, so a world id containing `__`, spaces, colons or slashes still maps to one directory level. The directory is not created here; `RepoService.init()` does that.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldName` | `String` | World key to map; `null` is sanitized to `world`. |

### Returns
`Path` — Absolute path of the world's repository directory, whether or not it exists.

---

## metadataPath

### Signature
```java
public Path metadataPath(String worldName)
```

### Description
Resolves `metadataRoot/<Names.safeWorldName(worldName)>.json`, mirroring the naming rule used by `RepoMetadataStore.file` so the metadata file for a world is always the same path. No file is read or created.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldName` | `String` | World key to map to a metadata file. |

### Returns
`Path` — Absolute path of the world's metadata JSON file, which may not exist.

---

## See Also

- **Software Detailed Design:** [RepoLocator_SDD.md](./RepoLocator_SDD.md)
