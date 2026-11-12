# WandItem

> **Software Detailed Documentation:** [WandItem_SDD.md](./WandItem_SDD.md)  
> **Source File:** [WandItem.java](../../../../../../../../src/client/java/org/almond/minecraftgit/client/selection/WandItem.java)

## Table of Contents

- [Usage](#usage)
- [Fields](#fields)
- [WandItem (Constructor)](#wanditem-constructor)
- [configuredItem](#configureditem)
- [create](#create)
- [isWand](#iswand)
- [give](#give)
- [See Also](#see-also)

## Usage

All methods are static and take the caller's `ModConfig`; `/git wand` calls `WandItem.give(player, services.config(), source.getClient())`, while `WandEvents` calls `WandItem.isWand(player.getMainHandItem(), services.config(), player)` on every block click. For example, `ItemStack stack = WandItem.create(services.config())` builds the named, tagged wand. `TAG` is the custom-data key that marks a stack as a wand.

The wand is a vanilla item carrying a custom-data tag. A client-only mod
cannot register a server item, so the item is created on the integrated
server when one exists. In multiplayer the same behaviour is available by
sneaking with the configured item, or by using the pos1/pos2 commands.

---

## Fields

- `TAG` (`String`) — Custom-data boolean key (`minecraftgit_wand`): any stack whose tag has this key set to true is a wand, whatever the base item, and `create` writes it.

---

## WandItem (Constructor)

### Signature
```java
private WandItem()
```

### Description
Private and empty; the class is a static helper around item stacks and is never instantiated.

### Parameters
None.

### Returns
`WandItem` instance.

---

## configuredItem

### Signature
```java
public static Item configuredItem(ModConfig config)
```

### Description
Parses `config.wandItem` as a `ResourceLocation` and resolves it against `BuiltInRegistries.ITEM`; an unparsable id or an air result falls back to `Items.WOODEN_SHOVEL`. Because the lookup is by registry id, an item from another mod can be configured as the wand.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `config` | [`ModConfig`](../../../../../../../main/java/org/almond/minecraftgit/config/ModConfig.md) | Mod configuration whose `wandItem` string is resolved. |

### Returns
`Item` — the configured wand item, or `Items.WOODEN_SHOVEL` when the id is invalid or resolves to air.

---

## create

### Signature
```java
public static ItemStack create(ModConfig config)
```

### Description
Builds a stack of `configuredItem` with an aqua `Git Wand` custom name and a `CUSTOM_DATA` component containing a `minecraftgit_wand` boolean set to true. The stack is not added to any inventory here; `give` schedules that on the integrated server.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `config` | [`ModConfig`](../../../../../../../main/java/org/almond/minecraftgit/config/ModConfig.md) | Mod configuration selecting the base item. |

### Returns
`ItemStack` — a new named, tagged wand stack.

---

## isWand

### Signature
```java
public static boolean isWand(ItemStack stack, ModConfig config, Player player)
```

### Description
Returns true when the stack's `CUSTOM_DATA` carries the `TAG` boolean. Otherwise the multiplayer fallback applies: holding `configuredItem(config)` while sneaking counts as a wand, because a client-only mod cannot hand out a server item there. Null or empty stacks always return false.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `stack` | `ItemStack` | Main-hand stack to test; null or empty returns false. |
| `config` | [`ModConfig`](../../../../../../../main/java/org/almond/minecraftgit/config/ModConfig.md) | Configuration supplying the fallback item id. |
| `player` | `Player` | Player whose sneak state enables the fallback. |

### Returns
`boolean` — true for a tagged wand or the configured item held while sneaking.

---

## give

### Signature
```java
public static boolean give(Player player, ModConfig config, Minecraft client)
```

### Description
Give the wand through the integrated server's player inventory. Returns
false in multiplayer, where the server would reject a client-only item and
desynchronize the inventory.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `player` | `Player` | Player whose UUID resolves the server-side `ServerPlayer`. |
| `config` | [`ModConfig`](../../../../../../../main/java/org/almond/minecraftgit/config/ModConfig.md) | Configuration used to build the wand stack. |
| `client` | `Minecraft` | Client used to reach the integrated server. |

### Returns
`boolean` — false when there is no integrated server or no matching server player (multiplayer), otherwise true. The inventory add or drop is scheduled with `server.execute`, so true does not mean the item is already delivered.

---

## See Also

- **Software Detailed Design:** [WandItem_SDD.md](./WandItem_SDD.md)
