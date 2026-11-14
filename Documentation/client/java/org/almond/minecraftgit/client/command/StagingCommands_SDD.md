# StagingCommands — Software Detailed Design

> **API Documentation:** [StagingCommands.md](./StagingCommands.md)  
> **Source File:** [StagingCommands.java](../../../../../../../../src/client/java/org/almond/minecraftgit/client/command/StagingCommands.java)

## Table of Contents

- [Usage](#usage)
- [1. Overview](#1-overview)
- [2. Package Declaration & Imports](#2-package-declaration-imports)
- [3. Class Declaration](#3-class-declaration)
- [4. Constructor](#4-constructor)
- [5. `attach()`](#5-attach)
- [6. `add()`](#6-add)
- [7. `onStaged()`](#7-onstaged)
- [8. `rm()`](#8-rm)
- [9. `resolve()`](#9-resolve)
- [10. `requirePlayer()`](#10-requireplayer)
- [11. `world()`](#11-world)
- [12. `names()`](#12-names)

## Usage

`StagingCommands` is a static command module wired by `GitCommand.register` through `attach(root, services)`; it keeps no state. `/git add` executes the asynchronous capture -> stage -> track -> set-active pipeline and `/git rm` deletes the staged snapshot on the IO pool and clears the tracked flag.

---

## 1. Overview

`/git add` and `/git rm`: stage and untrack a selection.

---

## 2. Package Declaration & Imports

```java
package org.almond.minecraftgit.client.command;
```

```java
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.world.entity.player.Player;
import org.almond.minecraftgit.client.GitServices;
import org.almond.minecraftgit.client.WorldContext;
import org.almond.minecraftgit.client.world.WorldAccess;
import org.almond.minecraftgit.model.Selection;
import org.almond.minecraftgit.selection.SelectionException;
import org.almond.minecraftgit.staging.StagedSelection;
import java.util.List;
```

| Import | Purpose |
|--------|---------|
| `StringArgumentType` | declares the optional `name` argument for both commands |
| `LiteralArgumentBuilder` | root builder the `add`/`rm` literals attach to |
| `SuggestionProvider` | tab-completes existing selection names |
| `ClientCommandManager` | Fabric factory for the `add`/`rm` literals and arguments |
| `FabricClientCommandSource` | handler source for player, client and feedback |
| `Player` | supplies the UUID and name used as the selection/staging owner |
| [`GitServices`](../GitServices.md) | access to repositories, selections, staging, selection manager and messages |
| [`WorldContext`](../WorldContext.md) | world id and single-player flag that route the capture |
| [`WorldAccess`](../world/WorldAccess.md) | captures blocks from the integrated server or client chunks |
| [`Selection`](../../../../../../../main/java/org/almond/minecraftgit/model/Selection.md) | target selection with its cuboids and origin |
| [`SelectionException`](../../../../../../../main/java/org/almond/minecraftgit/selection/SelectionException.md) | player-readable validation failures |
| [`StagedSelection`](../../../../../../../main/java/org/almond/minecraftgit/staging/StagedSelection.md) | staged handle carrying block/entity counts and snapshot origin |
| `List` | iteration type when building name suggestions |

---

## 3. Class Declaration

```java
public final class StagingCommands {
```

- **`final` with a private constructor** — a static command module; per-request values are passed as method parameters, while durable state lives in `SelectionService` and `StagingService`.

---

## 4. Constructor

```java
private StagingCommands()
```

Private constructor keeps the module static; every collaborator arrives through the `services` graph, so there is no per-instance lifecycle or mutable command state.

---

## 5. `attach()`

```java
public static void attach(LiteralArgumentBuilder<FabricClientCommandSource> root, GitServices services)
```

Registers `add` and `rm`, each with a no-argument form (passes `requested == null`) and a `name` form with suggestions from `names(services)`. Both forms call the same private handler, so selection resolution and validation live in one place.

---

## 6. `add()`

```java
private static int add(FabricClientCommandSource source, GitServices services, String requested)
```

Pipeline design: validate player/repository, resolve the selection, reject `cuboidCount() == 0`, then start `WorldAccess.capture`. `WorldAccess` completes on the integrated-server thread in single-player or on the client thread in multiplayer; the callback is re-dispatched with `client.execute` and silently aborts if the player left the world. The capture summary plus at most five warnings are printed, then `CommandSupport.complete` runs `StagingService.stage` (origin rebase and durable snapshot write) off the render thread and calls `onStaged` on the client thread. The handler returns `1` before either async step finishes; callback failures go through `CommandSupport.handleError`.

---

## 7. `onStaged()`

```java
private static void onStaged(FabricClientCommandSource source, GitServices services, WorldContext world,
                                 Player player, Selection selection, StagedSelection staged)
```

Final pipeline step: `SelectionService.setTracked(..., true)` records tracking, `SelectionManager.setActive` remembers the selection (persisted in local repo metadata), and chat reports name, block/entity counts and snapshot origin. If the snapshot has no blocks it notes that the commit will record an empty selection; the next-step hint is always `/git commit`.

---

## 8. `rm()`

```java
private static int rm(FabricClientCommandSource source, GitServices services, String requested)
```

Inverse pipeline: validate, resolve, then `CommandSupport.complete` calls `StagingService.unstage` on the IO pool, which deletes `<stagingRoot>/<world>/<uuid>/<name>.snap` if present. On the client thread it calls `setTracked(..., false)` and reports that the selection was untracked and its staged snapshot removed. Returns `1` immediately; staging errors surface through `handleError`.

---

## 9. `resolve()`

```java
private static Selection resolve(GitServices services, WorldContext world, Player player, String requested)
```

Name resolution shared by add/rm: a non-blank `requested` wins, otherwise `SelectionManager.active` returns the remembered selection or `null` if none is valid. A blank result raises `SelectionException` telling the player to pass a name or use `/git selection select`; `SelectionService.get` then loads the durable selection or raises when it does not exist for this world/player.

---

## 10. `requirePlayer()`

```java
private static Player requirePlayer(FabricClientCommandSource source)
```

Guard for player-only commands: throws `SelectionException` when `source.getPlayer()` is null, otherwise returns the player whose UUID keys selections and staging.

---

## 11. `world()`

```java
private static WorldContext world(FabricClientCommandSource source)
```

Derives the current `WorldContext` (world id, dimension, single-player flag) from the client; `WorldContext.current` throws `UserFacingException` when no level is loaded.

---

## 12. `names()`

```java
private static SuggestionProvider<FabricClientCommandSource> names(GitServices services)
```

Suggestion-provider design: resolves player and world, lists `SelectionService.list(world, player)` and offers each name; any `RuntimeException` is swallowed so completion can never break parsing. Returns `builder.buildFuture()`, already complete because the names are read synchronously.

---
