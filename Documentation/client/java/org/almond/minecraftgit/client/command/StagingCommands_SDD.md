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

TODO: Describe how `StagingCommands` is instantiated and used.

---

## 1. Overview

{@code /git add} and {@code /git rm}: stage and untrack a selection.

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
| `StringArgumentType` | TODO: describe purpose |
| `LiteralArgumentBuilder` | TODO: describe purpose |
| `SuggestionProvider` | TODO: describe purpose |
| `ClientCommandManager` | TODO: describe purpose |
| `FabricClientCommandSource` | TODO: describe purpose |
| `Player` | TODO: describe purpose |
| [`GitServices`](../GitServices.md) | TODO: describe purpose |
| [`WorldContext`](../WorldContext.md) | TODO: describe purpose |
| [`WorldAccess`](../world/WorldAccess.md) | TODO: describe purpose |
| [`Selection`](../../../../../../../main/java/org/almond/minecraftgit/model/Selection.md) | TODO: describe purpose |
| [`SelectionException`](../../../../../../../main/java/org/almond/minecraftgit/selection/SelectionException.md) | TODO: describe purpose |
| [`StagedSelection`](../../../../../../../main/java/org/almond/minecraftgit/staging/StagedSelection.md) | TODO: describe purpose |
| `List` | TODO: describe purpose |

---

## 3. Class Declaration

```java
public final class StagingCommands {
```


---

## 4. Constructor

```java
private StagingCommands()
```

TODO: Provide detailed design explanation for `StagingCommands`.

---

## 5. `attach()`

```java
public static void attach(LiteralArgumentBuilder<FabricClientCommandSource> root, GitServices services)
```

TODO: Provide detailed design explanation for `attach`.

---

## 6. `add()`

```java
private static int add(FabricClientCommandSource source, GitServices services, String requested)
```

TODO: Provide detailed design explanation for `add`.

---

## 7. `onStaged()`

```java
private static void onStaged(FabricClientCommandSource source, GitServices services, WorldContext world,
                                 Player player, Selection selection, StagedSelection staged)
```

TODO: Provide detailed design explanation for `onStaged`.

---

## 8. `rm()`

```java
private static int rm(FabricClientCommandSource source, GitServices services, String requested)
```

TODO: Provide detailed design explanation for `rm`.

---

## 9. `resolve()`

```java
private static Selection resolve(GitServices services, WorldContext world, Player player, String requested)
```

TODO: Provide detailed design explanation for `resolve`.

---

## 10. `requirePlayer()`

```java
private static Player requirePlayer(FabricClientCommandSource source)
```

TODO: Provide detailed design explanation for `requirePlayer`.

---

## 11. `world()`

```java
private static WorldContext world(FabricClientCommandSource source)
```

TODO: Provide detailed design explanation for `world`.

---

## 12. `names()`

```java
private static SuggestionProvider<FabricClientCommandSource> names(GitServices services)
```

TODO: Provide detailed design explanation for `names`.

---
