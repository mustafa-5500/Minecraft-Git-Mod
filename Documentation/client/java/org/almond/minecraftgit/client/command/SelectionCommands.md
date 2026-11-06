# SelectionCommands

> **Software Detailed Documentation:** [SelectionCommands_SDD.md](./SelectionCommands_SDD.md)  
> **Source File:** [SelectionCommands.java](../../../../../../../../src/client/java/org/almond/minecraftgit/client/command/SelectionCommands.java)

## Table of Contents

- [Usage](#usage)
- [SelectionCommands (Constructor)](#selectioncommands-constructor)
- [attach](#attach)
- [usage](#usage)
- [create](#create)
- [list](#list)
- [select](#select)
- [show](#show)
- [add](#add)
- [remove](#remove)
- [reorigin](#reorigin)
- [delete](#delete)
- [info](#info)
- [wand](#wand)
- [position](#position)
- [resolve](#resolve)
- [requirePlayer](#requireplayer)
- [world](#world)
- [names](#names)
- [See Also](#see-also)

## Usage

TODO: Add usage examples for `SelectionCommands`.

{@code /git selection ...}: create and manage named multi-cuboid selections.

---

## SelectionCommands (Constructor)

### Signature
```java
private SelectionCommands()
```

### Description
TODO: Describe what `SelectionCommands` does.

### Parameters
None.

### Returns
`SelectionCommands` instance.

---

## attach

### Signature
```java
public static void attach(LiteralArgumentBuilder<FabricClientCommandSource> root, GitServices services)
```

### Description
TODO: Describe what `attach` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `root` | `LiteralArgumentBuilder<FabricClientCommandSource>` | TODO: describe |
| `services` | [`GitServices`](../GitServices.md) | TODO: describe |

### Returns
`void`

---

## usage

### Signature
```java
private static int usage(FabricClientCommandSource source, GitServices services)
```

### Description
TODO: Describe what `usage` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | TODO: describe |
| `services` | [`GitServices`](../GitServices.md) | TODO: describe |

### Returns
`int` — TODO: describe return value.

---

## create

### Signature
```java
private static int create(FabricClientCommandSource source, GitServices services, String name)
```

### Description
TODO: Describe what `create` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | TODO: describe |
| `services` | [`GitServices`](../GitServices.md) | TODO: describe |
| `name` | `String` | TODO: describe |

### Returns
`int` — TODO: describe return value.

---

## list

### Signature
```java
private static int list(FabricClientCommandSource source, GitServices services)
```

### Description
TODO: Describe what `list` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | TODO: describe |
| `services` | [`GitServices`](../GitServices.md) | TODO: describe |

### Returns
`int` — TODO: describe return value.

---

## select

### Signature
```java
private static int select(FabricClientCommandSource source, GitServices services, String name)
```

### Description
TODO: Describe what `select` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | TODO: describe |
| `services` | [`GitServices`](../GitServices.md) | TODO: describe |
| `name` | `String` | TODO: describe |

### Returns
`int` — TODO: describe return value.

---

## show

### Signature
```java
private static int show(FabricClientCommandSource source, GitServices services, String name)
```

### Description
TODO: Describe what `show` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | TODO: describe |
| `services` | [`GitServices`](../GitServices.md) | TODO: describe |
| `name` | `String` | TODO: describe |

### Returns
`int` — TODO: describe return value.

---

## add

### Signature
```java
private static int add(FabricClientCommandSource source, GitServices services, String name)
```

### Description
TODO: Describe what `add` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | TODO: describe |
| `services` | [`GitServices`](../GitServices.md) | TODO: describe |
| `name` | `String` | TODO: describe |

### Returns
`int` — TODO: describe return value.

---

## remove

### Signature
```java
private static int remove(FabricClientCommandSource source, GitServices services, int index, String name)
```

### Description
TODO: Describe what `remove` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | TODO: describe |
| `services` | [`GitServices`](../GitServices.md) | TODO: describe |
| `index` | `int` | TODO: describe |
| `name` | `String` | TODO: describe |

### Returns
`int` — TODO: describe return value.

---

## reorigin

### Signature
```java
private static int reorigin(FabricClientCommandSource source, GitServices services, String name)
```

### Description
TODO: Describe what `reorigin` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | TODO: describe |
| `services` | [`GitServices`](../GitServices.md) | TODO: describe |
| `name` | `String` | TODO: describe |

### Returns
`int` — TODO: describe return value.

---

## delete

### Signature
```java
private static int delete(FabricClientCommandSource source, GitServices services, String name)
```

### Description
TODO: Describe what `delete` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | TODO: describe |
| `services` | [`GitServices`](../GitServices.md) | TODO: describe |
| `name` | `String` | TODO: describe |

### Returns
`int` — TODO: describe return value.

---

## info

### Signature
```java
private static int info(FabricClientCommandSource source, GitServices services, String name)
```

### Description
TODO: Describe what `info` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | TODO: describe |
| `services` | [`GitServices`](../GitServices.md) | TODO: describe |
| `name` | `String` | TODO: describe |

### Returns
`int` — TODO: describe return value.

---

## wand

### Signature
```java
private static int wand(FabricClientCommandSource source, GitServices services)
```

### Description
TODO: Describe what `wand` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | TODO: describe |
| `services` | [`GitServices`](../GitServices.md) | TODO: describe |

### Returns
`int` — TODO: describe return value.

---

## position

### Signature
```java
private static int position(FabricClientCommandSource source, GitServices services, boolean first)
```

### Description
TODO: Describe what `position` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | TODO: describe |
| `services` | [`GitServices`](../GitServices.md) | TODO: describe |
| `first` | `boolean` | TODO: describe |

### Returns
`int` — TODO: describe return value.

---

## resolve

### Signature
```java
private static Selection resolve(GitServices services, WorldContext world, Player player, String requested)
```

### Description
TODO: Describe what `resolve` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `services` | [`GitServices`](../GitServices.md) | TODO: describe |
| `world` | [`WorldContext`](../WorldContext.md) | TODO: describe |
| `player` | `Player` | TODO: describe |
| `requested` | `String` | TODO: describe |

### Returns
[`Selection`](../../../../../../../main/java/org/almond/minecraftgit/model/Selection.md) — TODO: describe return value.

---

## requirePlayer

### Signature
```java
private static Player requirePlayer(FabricClientCommandSource source)
```

### Description
TODO: Describe what `requirePlayer` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | TODO: describe |

### Returns
`Player` — TODO: describe return value.

---

## world

### Signature
```java
private static WorldContext world(FabricClientCommandSource source)
```

### Description
TODO: Describe what `world` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | TODO: describe |

### Returns
[`WorldContext`](../WorldContext.md) — TODO: describe return value.

---

## names

### Signature
```java
private static SuggestionProvider<FabricClientCommandSource> names(GitServices services)
```

### Description
TODO: Describe what `names` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `services` | [`GitServices`](../GitServices.md) | TODO: describe |

### Returns
`SuggestionProvider<FabricClientCommandSource>` — TODO: describe return value.

---

## See Also

- **Software Detailed Design:** [SelectionCommands_SDD.md](./SelectionCommands_SDD.md)
