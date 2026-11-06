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

TODO: Add usage examples for `RepoLocator`.

Decides where repositories and their local-only metadata live.
<p>Repositories live in the mod data folder
({@code config/minecraftgit/repos/<world>}), not inside the world folder.
World backups therefore never capture a hot {@code .git} directory, and a
repository is not accidentally copied with the map.</p>

---

## Fields

- `repoRoot` (`Path`) — TODO: describe field
- `metadataRoot` (`Path`) — TODO: describe field

---

## RepoLocator (Constructor)

### Signature
```java
public RepoLocator(Path repoRoot, Path metadataRoot)
```

### Description
TODO: Describe what `RepoLocator` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `repoRoot` | `Path` | TODO: describe |
| `metadataRoot` | `Path` | TODO: describe |

### Returns
`RepoLocator` instance.

---

## repoRoot

### Signature
```java
public Path repoRoot()
```

### Description
TODO: Describe what `repoRoot` does.

### Parameters
None.

### Returns
`Path` — TODO: describe return value.

---

## metadataRoot

### Signature
```java
public Path metadataRoot()
```

### Description
TODO: Describe what `metadataRoot` does.

### Parameters
None.

### Returns
`Path` — TODO: describe return value.

---

## repoPath

### Signature
```java
public Path repoPath(String worldName)
```

### Description
TODO: Describe what `repoPath` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldName` | `String` | TODO: describe |

### Returns
`Path` — TODO: describe return value.

---

## metadataPath

### Signature
```java
public Path metadataPath(String worldName)
```

### Description
TODO: Describe what `metadataPath` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldName` | `String` | TODO: describe |

### Returns
`Path` — TODO: describe return value.

---

## See Also

- **Software Detailed Design:** [RepoLocator_SDD.md](./RepoLocator_SDD.md)
