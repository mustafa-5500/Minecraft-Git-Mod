# SelectionRenderer

> **Software Detailed Documentation:** [SelectionRenderer_SDD.md](./SelectionRenderer_SDD.md)  
> **Source File:** [SelectionRenderer.java](../../../../../../../../src/client/java/org/almond/minecraftgit/client/selection/SelectionRenderer.java)

## Table of Contents

- [Usage](#usage)
- [Fields](#fields)
- [SelectionRenderer (Constructor)](#selectionrenderer-constructor)
- [register](#register)
- [highlight](#highlight)
- [tick](#tick)
- [drawCuboid](#drawcuboid)
- [draw](#draw)
- [See Also](#see-also)

## Usage

Registered once by `MinecraftGitClient` with `SelectionRenderer.register(services)`; it then runs from the client tick and needs no further calls. `/git selection show` calls the static `SelectionRenderer.highlight(world.id(), selection.name(), 8)` to add a temporary outline, and wand clicks feed the pending cuboid that the renderer reads. For example, `SelectionRenderer.highlight(world.id(), "castle", 8)` outlines the named selection for eight seconds even when it is not active.

Budgeted particle outline for the active selection, a pending cuboid, and a
temporary `/git selection show` highlight. Particles are local to this
client and never affect the server.

---

## Fields

- `tickCounter` (`int`) — Free-running tick counter; drawing happens only when `tickCounter % 10 == 0`, roughly twice per second.
- `highlightWorld` (`String`) — World id captured by `highlight`; a highlight is ignored in any other world.
- `highlightSelection` (`String`) — Name of the temporarily highlighted selection, or `null` when no highlight is active.
- `highlightUntil` (`long`) — Epoch-millisecond deadline computed by `highlight`; drawing stops once `System.currentTimeMillis()` passes it.

---

## SelectionRenderer (Constructor)

### Signature
```java
private SelectionRenderer()
```

### Description
Private and empty: the class is a static utility holding only tick and highlight state, so no instance is ever created.

### Parameters
None.

### Returns
`SelectionRenderer` instance.

---

## register

### Signature
```java
public static void register(GitServices services)
```

### Description
Subscribes `tick` to `ClientTickEvents.END_CLIENT_TICK` for the given service graph, activating the renderer for the rest of the client session. Called once from `MinecraftGitClient.onInitializeClient()`; registering twice would double the particle work.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `services` | [`GitServices`](../GitServices.md) | Service graph providing config, selection data and the per-player selection manager. |

### Returns
`void`

---

## highlight

### Signature
```java
public static void highlight(String worldId, String selection, int seconds)
```

### Description
Stores the world id, the selection name and an absolute deadline (`now + seconds * 1000`) consumed by the tick loop; it does not validate the selection, so an unknown name simply draws nothing. Successive calls replace the previous highlight rather than stacking.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | World id the highlight is valid for, compared with `WorldContext.id()` each tick. |
| `selection` | `String` | Name of the selection to outline, resolved per tick through `SelectionService.find`. |
| `seconds` | `int` | How long the highlight lasts, in seconds. |

### Returns
`void`

---

## tick

### Signature
```java
private static void tick(GitServices services, Minecraft client)
```

### Description
Gatekeeping entry point: returns immediately when `config.renderSelections` is off or no level or player exists, then draws only every tenth tick. It collects the active selection's cuboids, the pending cuboid and any unexpired highlight for the current world, then draws them against one shared particle budget. A runtime failure from `WorldContext.current` is swallowed, so no open world means no outline.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `services` | [`GitServices`](../GitServices.md) | Graph supplying config, the selection service and the selection manager. |
| `client` | `Minecraft` | Client instance providing the level, player and particle sink. |

### Returns
`void`

---

## drawCuboid

### Signature
```java
private static void drawCuboid(ClientLevel level, Cuboid cuboid, int[] used, int budget)
```

### Description
Walks the twelve edges of the cuboid with a stride of two blocks (`step = 2`) on each axis and calls `draw` for every sample. It checks no budget itself: `draw` stops adding once the shared counter is exhausted and the caller breaks out between cuboids.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `level` | `ClientLevel` | Client level used as the particle sink. |
| `cuboid` | [`Cuboid`](../../../../../../../main/java/org/almond/minecraftgit/model/Cuboid.md) | Inclusive region whose edges are sampled. |
| `used` | `int[]` | Single-element counter shared across all cuboids, mutated in place. |
| `budget` | `int` | Maximum particles for this tick. |

### Returns
`void`

---

## draw

### Signature
```java
private static void draw(ClientLevel level, int x, int y, int z, int[] used, int budget)
```

### Description
Adds one `END_ROD` particle at the block centre (`x+0.5, y+0.5, z+0.5`) with zero velocity and increments the shared counter, provided the budget has not been reached. Particles are client-local and are never sent to a server.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `level` | `ClientLevel` | Client level that receives the particle. |
| `x` | `int` | Block x coordinate of the sampled edge point. |
| `y` | `int` | Block y coordinate of the sampled edge point. |
| `z` | `int` | Block z coordinate of the sampled edge point. |
| `used` | `int[]` | Single-element counter shared across the tick, mutated in place. |
| `budget` | `int` | Maximum particles for this tick. |

### Returns
`void`

---

## See Also

- **Software Detailed Design:** [SelectionRenderer_SDD.md](./SelectionRenderer_SDD.md)
