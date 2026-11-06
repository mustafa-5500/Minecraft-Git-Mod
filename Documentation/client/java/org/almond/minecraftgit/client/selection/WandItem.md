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

TODO: Add usage examples for `WandItem`.

The wand is a vanilla item carrying a custom-data tag. A client-only mod
cannot register a server item, so the item is created on the integrated
server when one exists. In multiplayer the same behaviour is available by
sneaking with the configured item, or by using the pos1/pos2 commands.

---

## Fields

- `TAG` (`String`) — TODO: describe field

---

## WandItem (Constructor)

### Signature
```java
private WandItem()
```

### Description
TODO: Describe what `WandItem` does.

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
TODO: Describe what `configuredItem` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `config` | [`ModConfig`](../../../../../../../main/java/org/almond/minecraftgit/config/ModConfig.md) | TODO: describe |

### Returns
`Item` — TODO: describe return value.

---

## create

### Signature
```java
public static ItemStack create(ModConfig config)
```

### Description
TODO: Describe what `create` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `config` | [`ModConfig`](../../../../../../../main/java/org/almond/minecraftgit/config/ModConfig.md) | TODO: describe |

### Returns
`ItemStack` — TODO: describe return value.

---

## isWand

### Signature
```java
public static boolean isWand(ItemStack stack, ModConfig config, Player player)
```

### Description
TODO: Describe what `isWand` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `stack` | `ItemStack` | TODO: describe |
| `config` | [`ModConfig`](../../../../../../../main/java/org/almond/minecraftgit/config/ModConfig.md) | TODO: describe |
| `player` | `Player` | TODO: describe |

### Returns
`boolean` — TODO: describe return value.

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
| `player` | `Player` | TODO: describe |
| `config` | [`ModConfig`](../../../../../../../main/java/org/almond/minecraftgit/config/ModConfig.md) | TODO: describe |
| `client` | `Minecraft` | TODO: describe |

### Returns
`boolean` — TODO: describe return value.

---

## See Also

- **Software Detailed Design:** [WandItem_SDD.md](./WandItem_SDD.md)
