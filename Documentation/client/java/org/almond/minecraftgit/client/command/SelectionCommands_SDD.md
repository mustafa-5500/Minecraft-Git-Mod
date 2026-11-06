# SelectionCommands — Software Detailed Design

> **API Documentation:** [SelectionCommands.md](./SelectionCommands.md)  
> **Source File:** [SelectionCommands.java](../../../../../../../../src/client/java/org/almond/minecraftgit/client/command/SelectionCommands.java)

## Table of Contents

- [Usage](#usage)
- [1. Overview](#1-overview)
- [2. Package Declaration & Imports](#2-package-declaration-imports)
- [3. Class Declaration](#3-class-declaration)
- [4. Constructor](#4-constructor)
- [5. `attach()`](#5-attach)
- [6. `usage()`](#6-usage)
- [7. `create()`](#7-create)
- [8. `list()`](#8-list)
- [9. `select()`](#9-select)
- [10. `show()`](#10-show)
- [11. `add()`](#11-add)
- [12. `remove()`](#12-remove)
- [13. `reorigin()`](#13-reorigin)
- [14. `delete()`](#14-delete)
- [15. `info()`](#15-info)
- [16. `wand()`](#16-wand)
- [17. `position()`](#17-position)
- [18. `resolve()`](#18-resolve)
- [19. `requirePlayer()`](#19-requireplayer)
- [20. `world()`](#20-world)
- [21. `names()`](#21-names)

## Usage

TODO: Describe how `SelectionCommands` is instantiated and used.

---

## 1. Overview

{@code /git selection ...}: create and manage named multi-cuboid selections.

---

## 2. Package Declaration & Imports

```java
package org.almond.minecraftgit.client.command;
```

```java
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import org.almond.minecraftgit.client.GitServices;
import org.almond.minecraftgit.client.WorldContext;
import org.almond.minecraftgit.client.selection.SelectionManager;
import org.almond.minecraftgit.client.selection.SelectionRenderer;
import org.almond.minecraftgit.client.selection.WandEvents;
import org.almond.minecraftgit.client.selection.WandItem;
import org.almond.minecraftgit.model.BlockPos;
import org.almond.minecraftgit.model.Cuboid;
import org.almond.minecraftgit.model.Selection;
import org.almond.minecraftgit.selection.SelectionException;
import java.util.List;
import java.util.Locale;
```

| Import | Purpose |
|--------|---------|
| `IntegerArgumentType` | TODO: describe purpose |
| `StringArgumentType` | TODO: describe purpose |
| `LiteralArgumentBuilder` | TODO: describe purpose |
| `SuggestionProvider` | TODO: describe purpose |
| `ClientCommandManager` | TODO: describe purpose |
| `FabricClientCommandSource` | TODO: describe purpose |
| `Minecraft` | TODO: describe purpose |
| `Component` | TODO: describe purpose |
| `Player` | TODO: describe purpose |
| `BlockHitResult` | TODO: describe purpose |
| [`GitServices`](../GitServices.md) | TODO: describe purpose |
| [`WorldContext`](../WorldContext.md) | TODO: describe purpose |
| [`SelectionManager`](../selection/SelectionManager.md) | TODO: describe purpose |
| [`SelectionRenderer`](../selection/SelectionRenderer.md) | TODO: describe purpose |
| [`WandEvents`](../selection/WandEvents.md) | TODO: describe purpose |
| [`WandItem`](../selection/WandItem.md) | TODO: describe purpose |
| [`BlockPos`](../../../../../../../main/java/org/almond/minecraftgit/model/BlockPos.md) | TODO: describe purpose |
| [`Cuboid`](../../../../../../../main/java/org/almond/minecraftgit/model/Cuboid.md) | TODO: describe purpose |
| [`Selection`](../../../../../../../main/java/org/almond/minecraftgit/model/Selection.md) | TODO: describe purpose |
| [`SelectionException`](../../../../../../../main/java/org/almond/minecraftgit/selection/SelectionException.md) | TODO: describe purpose |
| `List` | TODO: describe purpose |
| `Locale` | TODO: describe purpose |

---

## 3. Class Declaration

```java
public final class SelectionCommands {
```


---

## 4. Constructor

```java
private SelectionCommands()
```

TODO: Provide detailed design explanation for `SelectionCommands`.

---

## 5. `attach()`

```java
public static void attach(LiteralArgumentBuilder<FabricClientCommandSource> root, GitServices services)
```

TODO: Provide detailed design explanation for `attach`.

---

## 6. `usage()`

```java
private static int usage(FabricClientCommandSource source, GitServices services)
```

TODO: Provide detailed design explanation for `usage`.

---

## 7. `create()`

```java
private static int create(FabricClientCommandSource source, GitServices services, String name)
```

TODO: Provide detailed design explanation for `create`.

---

## 8. `list()`

```java
private static int list(FabricClientCommandSource source, GitServices services)
```

TODO: Provide detailed design explanation for `list`.

---

## 9. `select()`

```java
private static int select(FabricClientCommandSource source, GitServices services, String name)
```

TODO: Provide detailed design explanation for `select`.

---

## 10. `show()`

```java
private static int show(FabricClientCommandSource source, GitServices services, String name)
```

TODO: Provide detailed design explanation for `show`.

---

## 11. `add()`

```java
private static int add(FabricClientCommandSource source, GitServices services, String name)
```

TODO: Provide detailed design explanation for `add`.

---

## 12. `remove()`

```java
private static int remove(FabricClientCommandSource source, GitServices services, int index, String name)
```

TODO: Provide detailed design explanation for `remove`.

---

## 13. `reorigin()`

```java
private static int reorigin(FabricClientCommandSource source, GitServices services, String name)
```

TODO: Provide detailed design explanation for `reorigin`.

---

## 14. `delete()`

```java
private static int delete(FabricClientCommandSource source, GitServices services, String name)
```

TODO: Provide detailed design explanation for `delete`.

---

## 15. `info()`

```java
private static int info(FabricClientCommandSource source, GitServices services, String name)
```

TODO: Provide detailed design explanation for `info`.

---

## 16. `wand()`

```java
private static int wand(FabricClientCommandSource source, GitServices services)
```

TODO: Provide detailed design explanation for `wand`.

---

## 17. `position()`

```java
private static int position(FabricClientCommandSource source, GitServices services, boolean first)
```

TODO: Provide detailed design explanation for `position`.

---

## 18. `resolve()`

```java
private static Selection resolve(GitServices services, WorldContext world, Player player, String requested)
```

TODO: Provide detailed design explanation for `resolve`.

---

## 19. `requirePlayer()`

```java
private static Player requirePlayer(FabricClientCommandSource source)
```

TODO: Provide detailed design explanation for `requirePlayer`.

---

## 20. `world()`

```java
private static WorldContext world(FabricClientCommandSource source)
```

TODO: Provide detailed design explanation for `world`.

---

## 21. `names()`

```java
private static SuggestionProvider<FabricClientCommandSource> names(GitServices services)
```

TODO: Provide detailed design explanation for `names`.

---
