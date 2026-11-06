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

TODO: Add usage examples for `RepoMetadataStore`.

Reads and writes local repository metadata, one JSON file per world.

---

## Fields

- `metadataRoot` (`Path`) — TODO: describe field
- `debug` ([`DebugLogger`](../debug/DebugLogger.md)) — TODO: describe field
- `gson` (`Gson`) — TODO: describe field

---

## RepoMetadataStore (Constructor)

### Signature
```java
public RepoMetadataStore(Path metadataRoot, DebugLogger debug)
```

### Description
TODO: Describe what `RepoMetadataStore` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `metadataRoot` | `Path` | TODO: describe |
| `debug` | [`DebugLogger`](../debug/DebugLogger.md) | TODO: describe |

### Returns
`RepoMetadataStore` instance.

---

## load

### Signature
```java
public RepoMetadata load(String world, RepoOptions defaults)
```

### Description
TODO: Describe what `load` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `world` | `String` | TODO: describe |
| `defaults` | [`RepoOptions`](RepoOptions.md) | TODO: describe |

### Returns
[`RepoMetadata`](RepoMetadata.md) — TODO: describe return value.

---

## save

### Signature
```java
public void save(RepoMetadata metadata)
```

### Description
TODO: Describe what `save` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `metadata` | [`RepoMetadata`](RepoMetadata.md) | TODO: describe |

### Returns
`void`

---

## file

### Signature
```java
public Path file(String world)
```

### Description
TODO: Describe what `file` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `world` | `String` | TODO: describe |

### Returns
`Path` — TODO: describe return value.

---

## See Also

- **Software Detailed Design:** [RepoMetadataStore_SDD.md](./RepoMetadataStore_SDD.md)
