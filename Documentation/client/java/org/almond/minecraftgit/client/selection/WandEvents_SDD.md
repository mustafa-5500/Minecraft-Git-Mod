# WandEvents — Software Detailed Design

> **API Documentation:** [WandEvents.md](./WandEvents.md)  
> **Source File:** [WandEvents.java](../../../../../../../../src/client/java/org/almond/minecraftgit/client/selection/WandEvents.java)

## Table of Contents

- [Usage](#usage)
- [1. Overview](#1-overview)
- [2. Package Declaration & Imports](#2-package-declaration-imports)
- [3. Class Declaration](#3-class-declaration)
- [4. Constructor](#4-constructor)
- [5. `register()`](#5-register)
- [6. `handleCorner()`](#6-handlecorner)
- [7. `completeCuboid()`](#7-completecuboid)

## Usage

TODO: Describe how `WandEvents` is instantiated and used.

---

## 1. Overview

Turns wand clicks into selection corners. Returning {@link InteractionResult#FAIL}
on the client cancels further processing without sending a break/use packet,
so a wand click never edits the world on a remote server.

---

## 2. Package Declaration & Imports

```java
package org.almond.minecraftgit.client.selection;
```

```java
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import org.almond.minecraftgit.client.GitServices;
import org.almond.minecraftgit.client.WorldContext;
import org.almond.minecraftgit.model.BlockPos;
import org.almond.minecraftgit.model.Cuboid;
import org.almond.minecraftgit.model.Selection;
import org.almond.minecraftgit.selection.SelectionException;
import java.util.Optional;
```

| Import | Purpose |
|--------|---------|
| `AttackBlockCallback` | TODO: describe purpose |
| `UseBlockCallback` | TODO: describe purpose |
| `ChatFormatting` | TODO: describe purpose |
| `Minecraft` | TODO: describe purpose |
| `ClientLevel` | TODO: describe purpose |
| `Component` | TODO: describe purpose |
| `InteractionHand` | TODO: describe purpose |
| `InteractionResult` | TODO: describe purpose |
| `Player` | TODO: describe purpose |
| [`GitServices`](../GitServices.md) | TODO: describe purpose |
| [`WorldContext`](../WorldContext.md) | TODO: describe purpose |
| [`BlockPos`](../../../../../../../main/java/org/almond/minecraftgit/model/BlockPos.md) | TODO: describe purpose |
| [`Cuboid`](../../../../../../../main/java/org/almond/minecraftgit/model/Cuboid.md) | TODO: describe purpose |
| [`Selection`](../../../../../../../main/java/org/almond/minecraftgit/model/Selection.md) | TODO: describe purpose |
| [`SelectionException`](../../../../../../../main/java/org/almond/minecraftgit/selection/SelectionException.md) | TODO: describe purpose |
| `Optional` | TODO: describe purpose |

---

## 3. Class Declaration

```java
public final class WandEvents {
```


---

## 4. Constructor

```java
private WandEvents()
```

TODO: Provide detailed design explanation for `WandEvents`.

---

## 5. `register()`

```java
public static void register(GitServices services)
```

TODO: Provide detailed design explanation for `register`.

---

## 6. `handleCorner()`

```java
public static void handleCorner(GitServices services, Player player, BlockPos pos, boolean first)
```

TODO: Provide detailed design explanation for `handleCorner`.

---

## 7. `completeCuboid()`

```java
private static void completeCuboid(GitServices services, WorldContext world, Player player, Cuboid cuboid)
```

TODO: Provide detailed design explanation for `completeCuboid`.

---
