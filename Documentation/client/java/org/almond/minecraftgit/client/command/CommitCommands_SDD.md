# CommitCommands — Software Detailed Design

> **API Documentation:** [CommitCommands.md](./CommitCommands.md)  
> **Source File:** [CommitCommands.java](../../../../../../../../src/client/java/org/almond/minecraftgit/client/command/CommitCommands.java)

## Table of Contents

- [Usage](#usage)
- [1. Overview](#1-overview)
- [2. Package Declaration & Imports](#2-package-declaration-imports)
- [3. Class Declaration](#3-class-declaration)
- [4. Constructor](#4-constructor)
- [5. `attach()`](#5-attach)
- [6. `commit()`](#6-commit)
- [7. `showResult()`](#7-showresult)
- [8. `requirePlayer()`](#8-requireplayer)
- [9. `world()`](#9-world)

## Usage

`CommitCommands` is a static command module: `GitCommand.register` calls `attach(root, services)` once, and no instance state is kept. The handler validates client state, builds a `CommitRequest`, and hands the actual repository work to `CommitService` on the IO pool, with `CommandSupport.complete` returning the result to the client thread.

---

## 1. Overview

`/git commit [message]`: write all staged selections to the repository.

---

## 2. Package Declaration & Imports

```java
package org.almond.minecraftgit.client.command;
```

```java
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.world.entity.player.Player;
import org.almond.minecraftgit.client.GitServices;
import org.almond.minecraftgit.client.WorldContext;
import org.almond.minecraftgit.commit.CommitRequest;
import org.almond.minecraftgit.commit.CommitResult;
import org.almond.minecraftgit.model.Selection;
import org.almond.minecraftgit.selection.SelectionException;
import java.util.ArrayList;
import java.util.List;
```

| Import | Purpose |
|--------|---------|
| `StringArgumentType` | declares the greedy `message` argument |
| `LiteralArgumentBuilder` | root builder the `commit` literal attaches to |
| `ClientCommandManager` | Fabric factory for the `commit`/`--empty` literals and the `message` argument |
| `FabricClientCommandSource` | handler source for player, client and feedback |
| `Player` | supplies the UUID and name used for selection/staging keys and commit metadata |
| [`GitServices`](../GitServices.md) | access to repositories, selections, staging, commits and messages |
| [`WorldContext`](../WorldContext.md) | world id and game mode recorded in the commit |
| [`CommitRequest`](../../../../../../../main/java/org/almond/minecraftgit/commit/CommitRequest.md) | immutable input passed to `CommitService` |
| [`CommitResult`](../../../../../../../main/java/org/almond/minecraftgit/commit/CommitResult.md) | async result rendered by `showResult` |
| [`Selection`](../../../../../../../main/java/org/almond/minecraftgit/model/Selection.md) | staged selections collected before the async commit |
| [`SelectionException`](../../../../../../../main/java/org/almond/minecraftgit/selection/SelectionException.md) | player-readable validation failures |
| `ArrayList` | mutable list used to gather staged selections |
| `List` | type of the frozen selection list in `CommitRequest` |

---

## 3. Class Declaration

```java
public final class CommitCommands {
```

- **`final` with a private constructor** — a static command module; it owns no state because per-commit data travels in the immutable `CommitRequest` and repository state lives in `CommitService`.

---

## 4. Constructor

```java
private CommitCommands()
```

Private constructor enforces the static-module pattern. Because no state is stored, concurrent command executions share only the services reachable through `services`; `CommitService` serializes repository writes with its own commit lock.

---

## 5. `attach()`

```java
public static void attach(LiteralArgumentBuilder<FabricClientCommandSource> root, GitServices services)
```

Builds a single `commit` literal with three forms: no arguments (`message=null`, `allowEmpty=false`), `--empty` (allowEmpty=true) and a greedy `message` (allowEmpty=false), plus `--empty <message>`. Brigadier treats `--empty` as a literal, so it must come before a free-form message; `greedyString()` keeps the message verbatim, including spaces.

---

## 6. `commit()`

```java
private static int commit(FabricClientCommandSource source, GitServices services,
                              String message, boolean allowEmpty)
```

Data flow: validate player and repo, compute `WorldContext`, list the player's selections and keep those whose staging file exists, then fail fast if the result is empty and `--empty` was not given. All client-thread state is captured in a `CommitRequest` (including `List.copyOf` so the async task cannot observe later mutations). `CommandSupport.complete` schedules `services.commits().commit(request)` on the IO executor and calls `showResult` on the client thread; returning `1` means scheduled, not completed. `CommitService` independently re-checks initialization/emptiness and raises `CommitException` for races such as a staging file disappearing.

---

## 7. `showResult()`

```java
private static void showResult(FabricClientCommandSource source, GitServices services, CommitResult result)
```

Renders a success line with short id, selection count, `+added ~changed -removed` totals and entity changes, then the first line of the commit message (split on the first line break so multi-line messages stay compact). If `result.selections()` is empty it warns that the commit recorded no file changes.

---

## 8. `requirePlayer()`

```java
private static Player requirePlayer(FabricClientCommandSource source)
```

Local guard used instead of `CommandSupport.requirePlayer`; a null player raises `SelectionException` with the standard message, otherwise the player is returned for its UUID and name.

---

## 9. `world()`

```java
private static WorldContext world(FabricClientCommandSource source)
```

Delegates to `WorldContext.current`, which throws `UserFacingException` when no level is loaded and otherwise derives the `<world>__<dimension>` id used as the repository key.

---
