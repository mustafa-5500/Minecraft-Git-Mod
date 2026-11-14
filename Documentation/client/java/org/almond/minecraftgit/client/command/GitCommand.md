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

`GitCommand.register(dispatcher, services)` is called once during client command registration and installs the complete `/git` tree; the class is never instantiated. Typical uses are `/git` or `/git help` for the command list, `/git version` for mod and Minecraft versions, `/git config show` for effective settings, `/git config debug` and `/git config blocks` to toggle logging, and `/git config author <name> <email>` (added by `RepoCommands`) to set the committer.

Root `/git` command. Phases add subcommands here; the root and help
command stay stable so players always have a discovery path.

---

## GitCommand (Constructor)

### Signature
```java
private GitCommand()
```

### Description
Private constructor; `GitCommand` is a static registration facade for the root command.

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
Creates and registers the root `/git` literal. It wires the default root action and `help` to the help screen, adds `version`, a `config` subtree (`show`, `debug`, `blocks`, plus `RepoCommands.attachConfig`), and a root-level `debug` toggle, then lets `RepoCommands`, `SelectionCommands`, `StagingCommands`, `CommitCommands`, `StatusCommands` and `PullCommands` attach their subtrees before `dispatcher.register(root)`.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `dispatcher` | `CommandDispatcher<FabricClientCommandSource>` | Brigadier dispatcher supplied by Fabric's client command callback |
| `services` | [`GitServices`](../GitServices.md) | service graph passed to every subcommand |

### Returns
`void`

---

## help

### Signature
```java
private static int help(CommandContext<FabricClientCommandSource> context, GitServices services)
```

### Description
Sends the full help screen: an aqua title, the list of currently available commands (selection, staging, commit, status, pull and checkout lines), a note that applying builds needs single-player, and the data folder (`services.root()`). All lines but the title are sent with `Messages.raw`, so no prefix is added.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `context` | `CommandContext<FabricClientCommandSource>` | Brigadier context of `/git` or `/git help` |
| `services` | [`GitServices`](../GitServices.md) | provides the message sender and data root |

### Returns
`int` — `1` when the help was sent, `0` if sending feedback threw.

---

## version

### Signature
```java
private static int version(FabricClientCommandSource source, GitServices services)
```

### Description
Resolves the `minecraftgit` and `minecraft` mod containers through `FabricLoader` and sends one line with both friendly versions. Missing containers fall back to `development` for the mod and `unknown` for Minecraft.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | destination for the version line |
| `services` | [`GitServices`](../GitServices.md) | message sender used for formatted output |

### Returns
`int` — `1` when the line was sent, `0` if it threw.

---

## showConfig

### Signature
```java
private static int showConfig(FabricClientCommandSource source, GitServices services)
```

### Description
Prints the effective `ModConfig` in grouped lines: data root, branch and default remote, wand item and chat prefix, committer name/email, selection and commit limits, apply settings (blocks per tick and max backups), and the debug flags. It only reads the config; nothing is saved.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | destination for the settings report |
| `services` | [`GitServices`](../GitServices.md) | supplies the config and data root |

### Returns
`int` — `1` on success, `0` if reporting threw.

---

## toggleDebug

### Signature
```java
private static int toggleDebug(FabricClientCommandSource source, GitServices services)
```

### Description
Flips `config.debug`, mirrors it into `services.debug()`, saves the config, and reports the new state. When enabling, the message includes the debug log file path.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | destination for the toggle confirmation |
| `services` | [`GitServices`](../GitServices.md) | provides config, debug logger and messages |

### Returns
`int` — `1` on success, `0` if the toggle or save threw.

---

## toggleBlocks

### Signature
```java
private static int toggleBlocks(FabricClientCommandSource source, GitServices services)
```

### Description
Flips `config.debugBlocks`, updates the debug logger's per-block flag, saves the config, and reports the new state. If block logging is enabled while general `debug` is off, the message appends a hint to run `/git config debug`.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | destination for the toggle confirmation |
| `services` | [`GitServices`](../GitServices.md) | provides config, debug logger and messages |

### Returns
`int` — `1` on success, `0` if the toggle or save threw.

---

## See Also

- **Software Detailed Design:** [GitCommand_SDD.md](./GitCommand_SDD.md)
