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

Registered from `MinecraftGitClient.onInitializeClient()` with the live service graph; Fabric then invokes the callbacks on the client thread for block interactions. The public `handleCorner` is also the path `/git selection pos1` and `pos2` reuse, so wand clicks and commands share one implementation.

---

## 1. Overview

Turns wand clicks into selection corners. Returning `InteractionResult.FAIL`
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
| `AttackBlockCallback` | Left-click and attack hook used to set corner A. |
| `UseBlockCallback` | Right-click and use hook used to set corner B. |
| `ChatFormatting` | AQUA action-bar corner feedback plus YELLOW, GREEN and RED result messages. |
| `Minecraft` | Provides the local player used to reject non-local clicks and to resolve the world. |
| `ClientLevel` | Type check that keeps the callbacks from firing on non-client levels. |
| `Component` | Builds the literal system messages and action-bar text. |
| `InteractionHand` | Restricts handling to `MAIN_HAND` so off-hand clicks are ignored. |
| `InteractionResult` | `PASS` for non-wand clicks and `FAIL` to cancel the click after handling it. |
| `Player` | The actor Fabric passes in and the target of session and message calls. |
| [`GitServices`](../GitServices.md) | Provides config, the selection manager and the selection service. |
| [`WorldContext`](../WorldContext.md) | Resolves the current `world.id()`; absence aborts handling quietly. |
| [`BlockPos`](../../../../../../../main/java/org/almond/minecraftgit/model/BlockPos.md) | Mod-format position sent to the selection manager. |
| [`Cuboid`](../../../../../../../main/java/org/almond/minecraftgit/model/Cuboid.md) | Completed region returned by `setCorner` and passed to `completeCuboid`. |
| [`Selection`](../../../../../../../main/java/org/almond/minecraftgit/model/Selection.md) | Result of `addCuboid`, used for the confirmation message. |
| [`SelectionException`](../../../../../../../main/java/org/almond/minecraftgit/selection/SelectionException.md) | Caught when a selection cap rejects the new cuboid, keeping it pending. |
| `Optional` | Distinguishes "first corner stored" from "cuboid completed". |

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

Private and empty; all behaviour lives in static registration and helper methods, so `WandEvents` never has an instance.

---

## 5. `register()`

```java
public static void register(GitServices services)
```

Attaches two lambdas. The attack callback requires a `ClientLevel`, the main hand, the local player and a wand (through `WandItem.isWand`) before calling `handleCorner(..., true)`; the use callback adds a null `hitResult` guard and calls `handleCorner(..., false)`. Both return `FAIL` on the wand path, which is what stops the client sending a break or use packet and therefore protects remote servers, while `PASS` leaves vanilla handling untouched.

---

## 6. `handleCorner()`

```java
public static void handleCorner(GitServices services, Player player, BlockPos pos, boolean first)
```

Resolves `WorldContext.current`, returning silently when that fails, then calls `SelectionManager.setCorner`. An empty result means only one corner is known, so it shows `Corner A/B set at x, y, z` on the action bar; a completed cuboid is forwarded to `completeCuboid` with the world context. Everything happens on the client thread in the click callback.

---

## 7. `completeCuboid()`

```java
private static void completeCuboid(GitServices services, WorldContext world, Player player, Cuboid cuboid)
```

Reads the active selection through `SelectionManager.active`, which validates the remembered name against `SelectionService`. With no active selection it stores the cuboid with `setPending` and tells the player to create or select a selection and run `/git selection add`; with one it calls `addCuboid`, re-sets the active name to the resolved name and reports dimensions, count and origin in green. A `SelectionException` keeps the cuboid pending and shows the reason in red.

---
