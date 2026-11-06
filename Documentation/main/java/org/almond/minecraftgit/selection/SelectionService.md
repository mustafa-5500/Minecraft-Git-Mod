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

TODO: Add usage examples for `SelectionService`.

Loads, mutates, and persists named selections per world id + player. All
mutation methods write through immediately so a crash or disconnect cannot
lose a selection definition.

---

## Fields

- `config` ([`ModConfig`](../config/ModConfig.md)) — TODO: describe field
- `store` ([`SelectionMetadataStore`](SelectionMetadataStore.md)) — TODO: describe field
- `cache` (`Map<String, List<Selection>>`) — TODO: describe field

---

## SelectionService (Constructor)

### Signature
```java
public SelectionService(ModConfig config)
```

### Description
TODO: Describe what `SelectionService` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `config` | [`ModConfig`](../config/ModConfig.md) | TODO: describe |

### Returns
`SelectionService` instance.

---

## root

### Signature
```java
public Path root()
```

### Description
TODO: Describe what `root` does.

### Parameters
None.

### Returns
`Path` — TODO: describe return value.

---

## list

### Signature
```java
public List<Selection> list(String worldId, UUID owner)
```

### Description
TODO: Describe what `list` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | TODO: describe |
| `owner` | `UUID` | TODO: describe |

### Returns
`List<Selection>` — TODO: describe return value.

---

## listAll

### Signature
```java
public List<Selection> listAll(String worldId)
```

### Description
TODO: Describe what `listAll` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | TODO: describe |

### Returns
`List<Selection>` — TODO: describe return value.

---

## find

### Signature
```java
public Optional<Selection> find(String worldId, UUID owner, String name)
```

### Description
TODO: Describe what `find` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | TODO: describe |
| `owner` | `UUID` | TODO: describe |
| `name` | `String` | TODO: describe |

### Returns
`Optional<Selection>` — TODO: describe return value.

---

## get

### Signature
```java
public Selection get(String worldId, UUID owner, String name)
```

### Description
TODO: Describe what `get` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | TODO: describe |
| `owner` | `UUID` | TODO: describe |
| `name` | `String` | TODO: describe |

### Returns
[`Selection`](../model/Selection.md) — TODO: describe return value.

---

## create

### Signature
```java
public Selection create(String worldId, UUID owner, String ownerName, String name)
```

### Description
TODO: Describe what `create` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | TODO: describe |
| `owner` | `UUID` | TODO: describe |
| `ownerName` | `String` | TODO: describe |
| `name` | `String` | TODO: describe |

### Returns
[`Selection`](../model/Selection.md) — TODO: describe return value.

---

## createAt

### Signature
```java
public Selection createAt(String worldId, UUID owner, String ownerName, String name, BlockPos origin)
```

### Description
TODO: Describe what `createAt` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | TODO: describe |
| `owner` | `UUID` | TODO: describe |
| `ownerName` | `String` | TODO: describe |
| `name` | `String` | TODO: describe |
| `origin` | [`BlockPos`](../model/BlockPos.md) | TODO: describe |

### Returns
[`Selection`](../model/Selection.md) — TODO: describe return value.

---

## addCuboid

### Signature
```java
public Selection addCuboid(String worldId, UUID owner, String name, Cuboid cuboid)
```

### Description
TODO: Describe what `addCuboid` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | TODO: describe |
| `owner` | `UUID` | TODO: describe |
| `name` | `String` | TODO: describe |
| `cuboid` | [`Cuboid`](../model/Cuboid.md) | TODO: describe |

### Returns
[`Selection`](../model/Selection.md) — TODO: describe return value.

---

## removeCuboid

### Signature
```java
public Cuboid removeCuboid(String worldId, UUID owner, String name, int oneBasedIndex)
```

### Description
TODO: Describe what `removeCuboid` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | TODO: describe |
| `owner` | `UUID` | TODO: describe |
| `name` | `String` | TODO: describe |
| `oneBasedIndex` | `int` | TODO: describe |

### Returns
[`Cuboid`](../model/Cuboid.md) — TODO: describe return value.

---

## reorigin

### Signature
```java
public BlockPos reorigin(String worldId, UUID owner, String name)
```

### Description
TODO: Describe what `reorigin` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | TODO: describe |
| `owner` | `UUID` | TODO: describe |
| `name` | `String` | TODO: describe |

### Returns
[`BlockPos`](../model/BlockPos.md) — the previous origin

---

## delete

### Signature
```java
public void delete(String worldId, UUID owner, String name)
```

### Description
TODO: Describe what `delete` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | TODO: describe |
| `owner` | `UUID` | TODO: describe |
| `name` | `String` | TODO: describe |

### Returns
`void`

---

## setTracked

### Signature
```java
public void setTracked(String worldId, UUID owner, String name, boolean tracked)
```

### Description
TODO: Describe what `setTracked` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | TODO: describe |
| `owner` | `UUID` | TODO: describe |
| `name` | `String` | TODO: describe |
| `tracked` | `boolean` | TODO: describe |

### Returns
`void`

---

## save

### Signature
```java
public void save(String worldId, UUID owner)
```

### Description
TODO: Describe what `save` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | TODO: describe |
| `owner` | `UUID` | TODO: describe |

### Returns
`void`

---

## store

### Signature
```java
public SelectionMetadataStore store()
```

### Description
TODO: Describe what `store` does.

### Parameters
None.

### Returns
[`SelectionMetadataStore`](SelectionMetadataStore.md) — TODO: describe return value.

---

## load

### Signature
```java
private List<Selection> load(String worldId, UUID owner)
```

### Description
TODO: Describe what `load` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | TODO: describe |
| `owner` | `UUID` | TODO: describe |

### Returns
`List<Selection>` — TODO: describe return value.

---

## persist

### Signature
```java
private void persist(String worldId, UUID owner, String ownerName, List<Selection> selections)
```

### Description
TODO: Describe what `persist` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | TODO: describe |
| `owner` | `UUID` | TODO: describe |
| `ownerName` | `String` | TODO: describe |
| `selections` | `List<Selection>` | TODO: describe |

### Returns
`void`

---

## key

### Signature
```java
private static String key(String worldId, UUID owner)
```

### Description
TODO: Describe what `key` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | TODO: describe |
| `owner` | `UUID` | TODO: describe |

### Returns
`String` — TODO: describe return value.

---

## requireName

### Signature
```java
private static void requireName(String name)
```

### Description
TODO: Describe what `requireName` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `name` | `String` | TODO: describe |

### Returns
`void`

---

## saturatedAdd

### Signature
```java
private static long saturatedAdd(long a, long b)
```

### Description
TODO: Describe what `saturatedAdd` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `a` | `long` | TODO: describe |
| `b` | `long` | TODO: describe |

### Returns
`long` — TODO: describe return value.

---

## invalidate

### Signature
```java
public void invalidate(String worldId, UUID owner)
```

### Description
TODO: Describe what `invalidate` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | TODO: describe |
| `owner` | `UUID` | TODO: describe |

### Returns
`void`

---

## tracked

### Signature
```java
public List<Selection> tracked(String worldId, UUID owner)
```

### Description
TODO: Describe what `tracked` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | TODO: describe |
| `owner` | `UUID` | TODO: describe |

### Returns
`List<Selection>` — TODO: describe return value.

---

## See Also

- **Software Detailed Design:** [SelectionService_SDD.md](./SelectionService_SDD.md)
