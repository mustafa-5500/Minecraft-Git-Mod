# WandItem — Software Detailed Design

> **API Documentation:** [WandItem.md](./WandItem.md)  
> **Source File:** [WandItem.java](../../../../../../../../src/client/java/org/almond/minecraftgit/client/selection/WandItem.java)

## Table of Contents

- [Usage](#usage)
- [1. Overview](#1-overview)
- [2. Package Declaration & Imports](#2-package-declaration-imports)
- [3. Class Declaration](#3-class-declaration)
- [4. Instance Fields](#4-instance-fields)
- [5. Constructor](#5-constructor)
- [6. `configuredItem()`](#6-configureditem)
- [7. `create()`](#7-create)
- [8. `isWand()`](#8-iswand)
- [9. `give()`](#9-give)

## Usage

Used statically by `/git wand` (`WandItem.give`), by `WandEvents` (`WandItem.isWand`), and by the multiplayer message that points players at the sneak fallback. There is no instance and no registry entry: the wand is a vanilla item plus custom data, which is why it works in single-player and degrades gracefully in multiplayer.

---

## 1. Overview

The wand is a vanilla item carrying a custom-data tag. A client-only mod
cannot register a server item, so the item is created on the integrated
server when one exists. In multiplayer the same behaviour is available by
sneaking with the configured item, or by using the pos1/pos2 commands.

---

## 2. Package Declaration & Imports

```java
package org.almond.minecraftgit.client.selection;
```

```java
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import org.almond.minecraftgit.config.ModConfig;
```

| Import | Purpose |
|--------|---------|
| `ChatFormatting` | Styles the `Git Wand` custom name aqua. |
| `Minecraft` | Provides `getSingleplayerServer()` for the give path. |
| `BuiltInRegistries` | Resolves the configured `wandItem` resource location to an `Item`. |
| `CompoundTag` | Holds the `TAG` boolean written into `CUSTOM_DATA`. |
| `Component` | The literal custom name component. |
| `ResourceLocation` | Parses the configured item id. |
| `MinecraftServer` | The integrated server used to reach the authoritative inventory. |
| `ServerPlayer` | Server-side player whose inventory receives the stack. |
| `Player` | Client player passed in by commands and click handlers. |
| `Item` | Return type of `configuredItem`. |
| `ItemStack` | The wand stack created and tested. |
| `Items` | Supplies the `AIR` and `WOODEN_SHOVEL` fallbacks. |
| `CustomData` | Component wrapper for the custom-data tag. |
| [`ModConfig`](../../../../../../../main/java/org/almond/minecraftgit/config/ModConfig.md) | Supplies the `wandItem` id used to choose the base item. |

---

## 3. Class Declaration

```java
public final class WandItem {
```


---

## 4. Instance Fields

```java
public static final String TAG;
```

| Field | Type | Description |
|-------|------|-------------|
| `TAG` | `String` | Public constant holding the custom-data key `minecraftgit_wand`; both `create` and `isWand` use it, so other code stamping a stack can reuse it. |

---

## 5. Constructor

```java
private WandItem()
```

Private and empty: everything is static because the wand is identified by stack data rather than by a class instance or registry entry.

---

## 6. `configuredItem()`

```java
public static Item configuredItem(ModConfig config)
```

Parses `config.wandItem` with `ResourceLocation.tryParse`, returns `Items.WOODEN_SHOVEL` for a null id, looks the item up in `BuiltInRegistries.ITEM` and falls back again for a null or `Items.AIR` result. The registry lookup lets a modded item id work as the wand base, and a typo degrades to the shovel instead of failing.

---

## 7. `create()`

```java
public static ItemStack create(ModConfig config)
```

Creates the stack, sets `CUSTOM_NAME` to `Git Wand` styled aqua, builds a `CompoundTag` with the `TAG` boolean set to true and wraps it in `CUSTOM_DATA`. Nothing is registered or persisted: identity lives entirely in item components, which is why copied or command-created stacks still count as wands.

---

## 8. `isWand()`

```java
public static boolean isWand(ItemStack stack, ModConfig config, Player player)
```

First checks the `CUSTOM_DATA` component for the `TAG` boolean, so a renamed or re-itemised wand still works. The fallback compares the stack item to `configuredItem(config)` and requires `player.isShiftKeyDown()`, giving multiplayer players a way to set corners without a server-side item; null or empty stacks short-circuit to false.

---

## 9. `give()`

```java
public static boolean give(Player player, ModConfig config, Minecraft client)
```

Give the wand through the integrated server's player inventory. Returns
false in multiplayer, where the server would reject a client-only item and
desynchronize the inventory.

---
