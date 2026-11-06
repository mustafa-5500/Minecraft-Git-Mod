# GitCommand

> **Software Detailed Documentation:** [GitCommand_SDD.md](./GitCommand_SDD.md)  
> **Source File:** [GitCommand.java](../../../../../../../../src/client/java/org/almond/minecraftgit/client/command/GitCommand.java)

## Table of Contents

- [Usage](#usage)
- [GitCommand (Constructor)](#gitcommand-constructor)
- [register](#register)
- [help](#help)
- [version](#version)
- [showConfig](#showconfig)
- [toggleDebug](#toggledebug)
- [toggleBlocks](#toggleblocks)
- [See Also](#see-also)

## Usage

TODO: Add usage examples for `GitCommand`.

Root {@code /git} command. Phases add subcommands here; the root and help
command stay stable so players always have a discovery path.

---

## GitCommand (Constructor)

### Signature
```java
private GitCommand()
```

### Description
TODO: Describe what `GitCommand` does.

### Parameters
None.

### Returns
`GitCommand` instance.

---

## register

### Signature
```java
public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, GitServices services)
```

### Description
TODO: Describe what `register` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `dispatcher` | `CommandDispatcher<FabricClientCommandSource>` | TODO: describe |
| `services` | [`GitServices`](../GitServices.md) | TODO: describe |

### Returns
`void`

---

## help

### Signature
```java
private static int help(CommandContext<FabricClientCommandSource> context, GitServices services)
```

### Description
TODO: Describe what `help` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `context` | `CommandContext<FabricClientCommandSource>` | TODO: describe |
| `services` | [`GitServices`](../GitServices.md) | TODO: describe |

### Returns
`int` — TODO: describe return value.

---

## version

### Signature
```java
private static int version(FabricClientCommandSource source, GitServices services)
```

### Description
TODO: Describe what `version` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | TODO: describe |
| `services` | [`GitServices`](../GitServices.md) | TODO: describe |

### Returns
`int` — TODO: describe return value.

---

## showConfig

### Signature
```java
private static int showConfig(FabricClientCommandSource source, GitServices services)
```

### Description
TODO: Describe what `showConfig` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | TODO: describe |
| `services` | [`GitServices`](../GitServices.md) | TODO: describe |

### Returns
`int` — TODO: describe return value.

---

## toggleDebug

### Signature
```java
private static int toggleDebug(FabricClientCommandSource source, GitServices services)
```

### Description
TODO: Describe what `toggleDebug` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | TODO: describe |
| `services` | [`GitServices`](../GitServices.md) | TODO: describe |

### Returns
`int` — TODO: describe return value.

---

## toggleBlocks

### Signature
```java
private static int toggleBlocks(FabricClientCommandSource source, GitServices services)
```

### Description
TODO: Describe what `toggleBlocks` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | TODO: describe |
| `services` | [`GitServices`](../GitServices.md) | TODO: describe |

### Returns
`int` — TODO: describe return value.

---

## See Also

- **Software Detailed Design:** [GitCommand_SDD.md](./GitCommand_SDD.md)
