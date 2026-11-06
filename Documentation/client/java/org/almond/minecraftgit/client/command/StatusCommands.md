# StatusCommands

> **Software Detailed Documentation:** [StatusCommands_SDD.md](./StatusCommands_SDD.md)  
> **Source File:** [StatusCommands.java](../../../../../../../../src/client/java/org/almond/minecraftgit/client/command/StatusCommands.java)

## Table of Contents

- [Usage](#usage)
- [StatusCommands (Constructor)](#statuscommands-constructor)
- [attach](#attach)
- [status](#status)
- [showStatus](#showstatus)
- [diff](#diff)
- [showDiff](#showdiff)
- [showSamples](#showsamples)
- [resolve](#resolve)
- [requirePlayer](#requireplayer)
- [world](#world)
- [names](#names)
- [See Also](#see-also)

## Usage

TODO: Add usage examples for `StatusCommands`.

{@code /git status} and {@code /git diff}.

---

## StatusCommands (Constructor)

### Signature
```java
private StatusCommands()
```

### Description
TODO: Describe what `StatusCommands` does.

### Parameters
None.

### Returns
`StatusCommands` instance.

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

## status

### Signature
```java
private static int status(FabricClientCommandSource source, GitServices services)
```

### Description
TODO: Describe what `status` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | TODO: describe |
| `services` | [`GitServices`](../GitServices.md) | TODO: describe |

### Returns
`int` — TODO: describe return value.

---

## showStatus

### Signature
```java
private static void showStatus(FabricClientCommandSource source, GitServices services, WorldContext world,
                                   StatusService.Report report)
```

### Description
TODO: Describe what `showStatus` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | TODO: describe |
| `services` | [`GitServices`](../GitServices.md) | TODO: describe |
| `world` | [`WorldContext`](../WorldContext.md) | TODO: describe |
| `report` | `StatusService.Report` | TODO: describe |

### Returns
`void`

---

## diff

### Signature
```java
private static int diff(FabricClientCommandSource source, GitServices services, String requested)
```

### Description
TODO: Describe what `diff` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | TODO: describe |
| `services` | [`GitServices`](../GitServices.md) | TODO: describe |
| `requested` | `String` | TODO: describe |

### Returns
`int` — TODO: describe return value.

---

## showDiff

### Signature
```java
private static void showDiff(FabricClientCommandSource source, GitServices services, Selection selection,
                                 StatusService.DiffReport report)
```

### Description
TODO: Describe what `showDiff` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | TODO: describe |
| `services` | [`GitServices`](../GitServices.md) | TODO: describe |
| `selection` | [`Selection`](../../../../../../../main/java/org/almond/minecraftgit/model/Selection.md) | TODO: describe |
| `report` | `StatusService.DiffReport` | TODO: describe |

### Returns
`void`

---

## showSamples

### Signature
```java
private static void showSamples(FabricClientCommandSource source, String label,
                                    List<BlockSnapshot> blocks)
```

### Description
TODO: Describe what `showSamples` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | TODO: describe |
| `label` | `String` | TODO: describe |
| `blocks` | `List<BlockSnapshot>` | TODO: describe |

### Returns
`void`

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

- **Software Detailed Design:** [StatusCommands_SDD.md](./StatusCommands_SDD.md)
