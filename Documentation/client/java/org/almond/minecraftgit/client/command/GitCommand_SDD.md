# GitCommand — Software Detailed Design

> **API Documentation:** [GitCommand.md](./GitCommand.md)  
> **Source File:** [GitCommand.java](../../../../../../../../src/client/java/org/almond/minecraftgit/client/command/GitCommand.java)

## Table of Contents

- [Usage](#usage)
- [1. Overview](#1-overview)
- [2. Package Declaration & Imports](#2-package-declaration-imports)
- [3. Class Declaration](#3-class-declaration)
- [4. Constructor](#4-constructor)
- [5. `register()`](#5-register)
- [6. `help()`](#6-help)
- [7. `version()`](#7-version)
- [8. `showConfig()`](#8-showconfig)
- [9. `toggleDebug()`](#9-toggledebug)
- [10. `toggleBlocks()`](#10-toggleblocks)

## Usage

TODO: Describe how `GitCommand` is instantiated and used.

---

## 1. Overview

Root {@code /git} command. Phases add subcommands here; the root and help
command stay stable so players always have a discovery path.

---

## 2. Package Declaration & Imports

```java
package org.almond.minecraftgit.client.command;
```

```java
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.loader.api.FabricLoader;
import org.almond.minecraftgit.MinecraftGit;
import org.almond.minecraftgit.client.GitServices;
import org.almond.minecraftgit.config.ModConfig;
```

| Import | Purpose |
|--------|---------|
| `CommandDispatcher` | TODO: describe purpose |
| `LiteralArgumentBuilder` | TODO: describe purpose |
| `CommandContext` | TODO: describe purpose |
| `ClientCommandManager` | TODO: describe purpose |
| `FabricClientCommandSource` | TODO: describe purpose |
| `FabricLoader` | TODO: describe purpose |
| [`MinecraftGit`](../../../../../../../main/java/org/almond/minecraftgit/MinecraftGit.md) | TODO: describe purpose |
| [`GitServices`](../GitServices.md) | TODO: describe purpose |
| [`ModConfig`](../../../../../../../main/java/org/almond/minecraftgit/config/ModConfig.md) | TODO: describe purpose |

---

## 3. Class Declaration

```java
public final class GitCommand {
```


---

## 4. Constructor

```java
private GitCommand()
```

TODO: Provide detailed design explanation for `GitCommand`.

---

## 5. `register()`

```java
public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, GitServices services)
```

TODO: Provide detailed design explanation for `register`.

---

## 6. `help()`

```java
private static int help(CommandContext<FabricClientCommandSource> context, GitServices services)
```

TODO: Provide detailed design explanation for `help`.

---

## 7. `version()`

```java
private static int version(FabricClientCommandSource source, GitServices services)
```

TODO: Provide detailed design explanation for `version`.

---

## 8. `showConfig()`

```java
private static int showConfig(FabricClientCommandSource source, GitServices services)
```

TODO: Provide detailed design explanation for `showConfig`.

---

## 9. `toggleDebug()`

```java
private static int toggleDebug(FabricClientCommandSource source, GitServices services)
```

TODO: Provide detailed design explanation for `toggleDebug`.

---

## 10. `toggleBlocks()`

```java
private static int toggleBlocks(FabricClientCommandSource source, GitServices services)
```

TODO: Provide detailed design explanation for `toggleBlocks`.

---
