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

TODO: Add usage examples for `SelectionMetadataStore`.

Machine-local selection state. This is intentionally stored outside the
repository: it records absolute world coordinates, owners, and the current
origin, none of which belong in a shared commit.

---

## Fields

- `FORMAT_VERSION` (`int`) — TODO: describe field
- `root` (`Path`) — TODO: describe field
- `gson` (`Gson`) — TODO: describe field

---

## SelectionMetadataStore (Constructor)

### Signature
```java
public SelectionMetadataStore(Path root)
```

### Description
TODO: Describe what `SelectionMetadataStore` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `root` | `Path` | TODO: describe |

### Returns
`SelectionMetadataStore` instance.

---

## file

### Signature
```java
public Path file(String world, UUID owner)
```

### Description
TODO: Describe what `file` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `world` | `String` | TODO: describe |
| `owner` | `UUID` | TODO: describe |

### Returns
`Path` — TODO: describe return value.

---

## load

### Signature
```java
public List<Selection> load(String world, UUID owner)
```

### Description
TODO: Describe what `load` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `world` | `String` | TODO: describe |
| `owner` | `UUID` | TODO: describe |

### Returns
`List<Selection>` — TODO: describe return value.

---

## loadFile

### Signature
```java
public List<Selection> loadFile(Path file, String world, UUID fallbackOwner, String fallbackOwnerName)
```

### Description
TODO: Describe what `loadFile` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `file` | `Path` | TODO: describe |
| `world` | `String` | TODO: describe |
| `fallbackOwner` | `UUID` | TODO: describe |
| `fallbackOwnerName` | `String` | TODO: describe |

### Returns
`List<Selection>` — TODO: describe return value.

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
| `world` | `String` | TODO: describe |

### Returns
`List<Selection>` — TODO: describe return value.

---

## save

### Signature
```java
public void save(String world, UUID owner, String ownerName, List<Selection> selections)
```

### Description
TODO: Describe what `save` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `world` | `String` | TODO: describe |
| `owner` | `UUID` | TODO: describe |
| `ownerName` | `String` | TODO: describe |
| `selections` | `List<Selection>` | TODO: describe |

### Returns
`void`

---

## ownerFromFileName

### Signature
```java
private static UUID ownerFromFileName(Path file)
```

### Description
TODO: Describe what `ownerFromFileName` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `file` | `Path` | TODO: describe |

### Returns
`UUID` — TODO: describe return value.

---

## See Also

- **Software Detailed Design:** [SelectionMetadataStore_SDD.md](./SelectionMetadataStore_SDD.md)
