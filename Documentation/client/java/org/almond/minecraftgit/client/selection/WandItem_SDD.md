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

TODO: Describe how `WandItem` is instantiated and used.

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
| `ChatFormatting` | TODO: describe purpose |
| `Minecraft` | TODO: describe purpose |
| `BuiltInRegistries` | TODO: describe purpose |
| `CompoundTag` | TODO: describe purpose |
| `Component` | TODO: describe purpose |
| `ResourceLocation` | TODO: describe purpose |
| `MinecraftServer` | TODO: describe purpose |
| `ServerPlayer` | TODO: describe purpose |
| `Player` | TODO: describe purpose |
| `Item` | TODO: describe purpose |
| `ItemStack` | TODO: describe purpose |
| `Items` | TODO: describe purpose |
| `CustomData` | TODO: describe purpose |
| [`ModConfig`](../../../../../../../main/java/org/almond/minecraftgit/config/ModConfig.md) | TODO: describe purpose |

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
| `TAG` | `String` | TODO: describe |

---

## 5. Constructor

```java
private WandItem()
```

TODO: Provide detailed design explanation for `WandItem`.

---

## 6. `configuredItem()`

```java
public static Item configuredItem(ModConfig config)
```

TODO: Provide detailed design explanation for `configuredItem`.

---

## 7. `create()`

```java
public static ItemStack create(ModConfig config)
```

TODO: Provide detailed design explanation for `create`.

---

## 8. `isWand()`

```java
public static boolean isWand(ItemStack stack, ModConfig config, Player player)
```

TODO: Provide detailed design explanation for `isWand`.

---

## 9. `give()`

```java
public static boolean give(Player player, ModConfig config, Minecraft client)
```

Give the wand through the integrated server's player inventory. Returns
false in multiplayer, where the server would reject a client-only item and
desynchronize the inventory.

---
