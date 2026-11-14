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

Registered once by `MinecraftGitClient` through `WandEvents.register(services)`; afterwards a left-click with the wand sets corner A and a right-click sets corner B. For example, two wand clicks complete a cuboid, which is added to the active selection when one exists and otherwise kept as the pending cuboid that `/git selection add` consumes.

Turns wand clicks into selection corners. Returning `InteractionResult.FAIL`
on the client cancels further processing without sending a break/use packet,
so a wand click never edits the world on a remote server.

---

## WandEvents (Constructor)

### Signature
```java
private WandEvents()
```

### Description
Private no-op constructor; the class holds only static registration and handler methods and is never instantiated.

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
Registers both Fabric block callbacks: `AttackBlockCallback` handles left-clicks as corner A and `UseBlockCallback` handles right-clicks as corner B. Each ignores non-client levels, off-hand clicks and players other than the local one, and returns `InteractionResult.FAIL` after handling so no break or use packet reaches the server; clicks with a non-wand item pass through unchanged.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `services` | [`GitServices`](../GitServices.md) | Service graph providing config, the selection manager and the selection store used by the handlers. |

### Returns
`void`

---

## handleCorner

### Signature
```java
public static void handleCorner(GitServices services, Player player, BlockPos pos, boolean first)
```

### Description
Resolves the current world (silently giving up when none is open), stores the position as corner A or B through `SelectionManager.setCorner`, then either shows an action-bar confirmation for the single corner or forwards the completed cuboid to `completeCuboid`. Runs on the client thread inside the click callback.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `services` | [`GitServices`](../GitServices.md) | Graph with the selection manager and selection service. |
| `player` | `Player` | Local player whose session receives the corner. |
| `pos` | [`BlockPos`](../../../../../../../main/java/org/almond/minecraftgit/model/BlockPos.md) | Clicked block position. |
| `first` | `boolean` | `true` for corner A (attack), `false` for corner B (use). |

### Returns
`void`

---

## completeCuboid

### Signature
```java
private static void completeCuboid(GitServices services, WorldContext world, Player player, Cuboid cuboid)
```

### Description
Looks up the active selection. With none it stores the cuboid as pending and warns the player to create or select one; otherwise it calls `SelectionService.addCuboid` and reports the dimensions, cuboid count and origin in green. A `SelectionException` from the cap checks is caught, the cuboid is kept pending and the reason is shown in red, so a failed add never loses the click.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `services` | [`GitServices`](../GitServices.md) | Graph providing the selection manager and selection service. |
| `world` | [`WorldContext`](../WorldContext.md) | Current world context whose id scopes the selection. |
| `player` | `Player` | Player who completed the cuboid and receives the feedback. |
| `cuboid` | [`Cuboid`](../../../../../../../main/java/org/almond/minecraftgit/model/Cuboid.md) | Normalized cuboid built from the two corners. |

### Returns
`void`

---

## See Also

- **Software Detailed Design:** [WandEvents_SDD.md](./WandEvents_SDD.md)
