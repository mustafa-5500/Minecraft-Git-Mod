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

TODO: Add usage examples for `SelectionRenderer`.

Budgeted particle outline for the active selection, a pending cuboid, and a
temporary {@code /git selection show} highlight. Particles are local to this
client and never affect the server.

---

## Fields

- `tickCounter` (`int`) — TODO: describe field
- `highlightWorld` (`String`) — TODO: describe field
- `highlightSelection` (`String`) — TODO: describe field
- `highlightUntil` (`long`) — TODO: describe field

---

## SelectionRenderer (Constructor)

### Signature
```java
private SelectionRenderer()
```

### Description
TODO: Describe what `SelectionRenderer` does.

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
TODO: Describe what `register` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `services` | [`GitServices`](../GitServices.md) | TODO: describe |

### Returns
`void`

---

## highlight

### Signature
```java
public static void highlight(String worldId, String selection, int seconds)
```

### Description
TODO: Describe what `highlight` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | TODO: describe |
| `selection` | `String` | TODO: describe |
| `seconds` | `int` | TODO: describe |

### Returns
`void`

---

## tick

### Signature
```java
private static void tick(GitServices services, Minecraft client)
```

### Description
TODO: Describe what `tick` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `services` | [`GitServices`](../GitServices.md) | TODO: describe |
| `client` | `Minecraft` | TODO: describe |

### Returns
`void`

---

## drawCuboid

### Signature
```java
private static void drawCuboid(ClientLevel level, Cuboid cuboid, int[] used, int budget)
```

### Description
TODO: Describe what `drawCuboid` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `level` | `ClientLevel` | TODO: describe |
| `cuboid` | [`Cuboid`](../../../../../../../main/java/org/almond/minecraftgit/model/Cuboid.md) | TODO: describe |
| `used` | `int[]` | TODO: describe |
| `budget` | `int` | TODO: describe |

### Returns
`void`

---

## draw

### Signature
```java
private static void draw(ClientLevel level, int x, int y, int z, int[] used, int budget)
```

### Description
TODO: Describe what `draw` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `level` | `ClientLevel` | TODO: describe |
| `x` | `int` | TODO: describe |
| `y` | `int` | TODO: describe |
| `z` | `int` | TODO: describe |
| `used` | `int[]` | TODO: describe |
| `budget` | `int` | TODO: describe |

### Returns
`void`

---

## See Also

- **Software Detailed Design:** [SelectionRenderer_SDD.md](./SelectionRenderer_SDD.md)
