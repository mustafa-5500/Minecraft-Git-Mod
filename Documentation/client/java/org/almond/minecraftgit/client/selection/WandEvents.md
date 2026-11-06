# WandEvents

> **Software Detailed Documentation:** [WandEvents_SDD.md](./WandEvents_SDD.md)  
> **Source File:** [WandEvents.java](../../../../../../../../src/client/java/org/almond/minecraftgit/client/selection/WandEvents.java)

## Table of Contents

- [Usage](#usage)
- [WandEvents (Constructor)](#wandevents-constructor)
- [register](#register)
- [handleCorner](#handlecorner)
- [completeCuboid](#completecuboid)
- [See Also](#see-also)

## Usage

TODO: Add usage examples for `WandEvents`.

Turns wand clicks into selection corners. Returning {@link InteractionResult#FAIL}
on the client cancels further processing without sending a break/use packet,
so a wand click never edits the world on a remote server.

---

## WandEvents (Constructor)

### Signature
```java
private WandEvents()
```

### Description
TODO: Describe what `WandEvents` does.

### Parameters
None.

### Returns
`WandEvents` instance.

---

## register

### Signature
```java
public static void register(GitServices services)
```

### Description
TODO: Describe what `register` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `services` | [`GitServices`](../GitServices.md) | TODO: describe |

### Returns
`void`

---

## handleCorner

### Signature
```java
public static void handleCorner(GitServices services, Player player, BlockPos pos, boolean first)
```

### Description
TODO: Describe what `handleCorner` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `services` | [`GitServices`](../GitServices.md) | TODO: describe |
| `player` | `Player` | TODO: describe |
| `pos` | [`BlockPos`](../../../../../../../main/java/org/almond/minecraftgit/model/BlockPos.md) | TODO: describe |
| `first` | `boolean` | TODO: describe |

### Returns
`void`

---

## completeCuboid

### Signature
```java
private static void completeCuboid(GitServices services, WorldContext world, Player player, Cuboid cuboid)
```

### Description
TODO: Describe what `completeCuboid` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `services` | [`GitServices`](../GitServices.md) | TODO: describe |
| `world` | [`WorldContext`](../WorldContext.md) | TODO: describe |
| `player` | `Player` | TODO: describe |
| `cuboid` | [`Cuboid`](../../../../../../../main/java/org/almond/minecraftgit/model/Cuboid.md) | TODO: describe |

### Returns
`void`

---

## See Also

- **Software Detailed Design:** [WandEvents_SDD.md](./WandEvents_SDD.md)
