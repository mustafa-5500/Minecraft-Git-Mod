# StagingCommands

> **Software Detailed Documentation:** [StagingCommands_SDD.md](./StagingCommands_SDD.md)  
> **Source File:** [StagingCommands.java](../../../../../../../../src/client/java/org/almond/minecraftgit/client/command/StagingCommands.java)

## Table of Contents

- [Usage](#usage)
- [StagingCommands (Constructor)](#stagingcommands-constructor)
- [attach](#attach)
- [add](#add)
- [onStaged](#onstaged)
- [rm](#rm)
- [resolve](#resolve)
- [requirePlayer](#requireplayer)
- [world](#world)
- [names](#names)
- [See Also](#see-also)

## Usage

TODO: Add usage examples for `StagingCommands`.

{@code /git add} and {@code /git rm}: stage and untrack a selection.

---

## StagingCommands (Constructor)

### Signature
```java
private StagingCommands()
```

### Description
TODO: Describe what `StagingCommands` does.

### Parameters
None.

### Returns
`StagingCommands` instance.

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

## add

### Signature
```java
private static int add(FabricClientCommandSource source, GitServices services, String requested)
```

### Description
TODO: Describe what `add` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | TODO: describe |
| `services` | [`GitServices`](../GitServices.md) | TODO: describe |
| `requested` | `String` | TODO: describe |

### Returns
`int` — TODO: describe return value.

---

## onStaged

### Signature
```java
private static void onStaged(FabricClientCommandSource source, GitServices services, WorldContext world,
                                 Player player, Selection selection, StagedSelection staged)
```

### Description
TODO: Describe what `onStaged` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | TODO: describe |
| `services` | [`GitServices`](../GitServices.md) | TODO: describe |
| `world` | [`WorldContext`](../WorldContext.md) | TODO: describe |
| `player` | `Player` | TODO: describe |
| `selection` | [`Selection`](../../../../../../../main/java/org/almond/minecraftgit/model/Selection.md) | TODO: describe |
| `staged` | [`StagedSelection`](../../../../../../../main/java/org/almond/minecraftgit/staging/StagedSelection.md) | TODO: describe |

### Returns
`void`

---

## rm

### Signature
```java
private static int rm(FabricClientCommandSource source, GitServices services, String requested)
```

### Description
TODO: Describe what `rm` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | TODO: describe |
| `services` | [`GitServices`](../GitServices.md) | TODO: describe |
| `requested` | `String` | TODO: describe |

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

- **Software Detailed Design:** [StagingCommands_SDD.md](./StagingCommands_SDD.md)
