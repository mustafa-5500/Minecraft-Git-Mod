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

`GitCommand` is a static registration class, called once by the client entrypoint when Fabric's client command callback fires; no instance is retained. `register(dispatcher, services)` builds the root literal and config subtree, then delegates to each feature class's `attach(...)` so command ownership stays with the class that implements it.

---

## 1. Overview

Root `/git` command. Phases add subcommands here; the root and help
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
| `CommandDispatcher` | registers the assembled `/git` literal tree with Brigadier |
| `LiteralArgumentBuilder` | builds the root and `config` literals that subcommands are attached to |
| `CommandContext` | lets `help` read the source of `/git` and `/git help` |
| `ClientCommandManager` | Fabric factory for client literals and arguments |
| `FabricClientCommandSource` | command source type used by registration and every handler |
| `FabricLoader` | reads mod metadata for `/git version` |
| [`MinecraftGit`](../../../../../../../main/java/org/almond/minecraftgit/MinecraftGit.md) | supplies `MOD_ID` for the mod container lookup |
| [`GitServices`](../GitServices.md) | service graph threaded through registration and handlers |
| [`ModConfig`](../../../../../../../main/java/org/almond/minecraftgit/config/ModConfig.md) | typed settings read and toggled by the config commands |

---

## 3. Class Declaration

```java
public final class GitCommand {
```

- **`final` with a private constructor** — a static command-registration facade; it holds no state and exists only to build the `/git` tree once per client session.

---

## 4. Constructor

```java
private GitCommand()
```

Private constructor marks a pure static facade: the class holds no state, so the command tree is built entirely from the arguments of `register`.

---

## 5. `register()`

```java
public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, GitServices services)
```

Builds bottom-up: first the `config` literal with a default `show` action plus `show`, `debug` and `blocks`, then `RepoCommands.attachConfig(config, services)` for settings such as `author`; then the `git` root with default help, `help`, `version`, `config` and a root-level `debug` alias. Feature modules attach their literals to the same root in sequence (`RepoCommands`, `SelectionCommands`, `StagingCommands`, `CommitCommands`, `StatusCommands`, `PullCommands`), and `dispatcher.register(root)` publishes the tree. Keeping the root/help nodes stable guarantees discovery even as phases add commands.

---

## 6. `help()`

```java
private static int help(CommandContext<FabricClientCommandSource> context, GitServices services)
```

Uses `CommandSupport.run` so any feedback failure is reported rather than escaping. Sends the fixed catalogue with `Messages.raw` (title styled aqua), including all currently attached subcommands, the single-player note and `services.root()`. The handler does not inspect arguments; both `/git` and `/git help` execute the same code.

---

## 7. `version()`

```java
private static int version(FabricClientCommandSource source, GitServices services)
```

Looks up the `minecraftgit` and `minecraft` containers with `FabricLoader`; missing containers degrade to `development`/`unknown` instead of failing. Output is one `Messages.send` line so it carries the configured prefix.

---

## 8. `showConfig()`

```java
private static int showConfig(FabricClientCommandSource source, GitServices services)
```

Read-only reporting path: obtains `services.config()` and prints data root, branch/remote, wand/prefix, committer, limits, apply settings and debug flags via `Messages.raw`. No config mutation or save occurs, so it is safe to run at any time.

---

## 9. `toggleDebug()`

```java
private static int toggleDebug(FabricClientCommandSource source, GitServices services)
```

Mutates `config.debug`, applies it to `DebugLogger.setEnabled`, then calls `config.save()` (an atomic write in `ModConfig`). The confirmation includes the debug log path only when enabling. Because this runs on the client thread, the logger flag changes before any later command logs.

---

## 10. `toggleBlocks()`

```java
private static int toggleBlocks(FabricClientCommandSource source, GitServices services)
```

Mutates `config.debugBlocks` and mirrors it with `DebugLogger.setBlocks`, then saves. The confirmation reminds the player that block logging also needs `/git config debug` when general debug is off.

---
