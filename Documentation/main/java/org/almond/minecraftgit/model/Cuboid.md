# Cuboid

> **Source File:** [Cuboid.java](../../../../../../../src/main/java/org/almond/minecraftgit/model/Cuboid.java)

## Usage

Inclusive rectangular region, normalised on construction so callers do not need
to know which corner the player clicked first. Cuboids are combined into a
[`Selection`](Selection.md); their smallest corner is the ordering key and may
seed the selection origin.

```java
Cuboid box = Cuboid.of(new BlockPos(10, 64, 10), new BlockPos(0, 60, 0));
// min = (0,60,0), max = (10,64,10)
long volume = box.volume(); // 11 * 5 * 11
```

## Fields

| Name | Type | Description |
|------|------|-------------|
| `minX`, `minY`, `minZ` | `int` | Inclusive minimum corner, normalised from the constructor arguments. |
| `maxX`, `maxY`, `maxZ` | `int` | Inclusive maximum corner, normalised from the constructor arguments. |
| `SMALLEST_CORNER_ORDER` | `Comparator<Cuboid>` | Sorts by `(minX, minY, minZ, maxX, maxY, maxZ)`. |

## Methods

| Method | Description |
|--------|-------------|
| `of(BlockPos, BlockPos)` / `of(int...)` | Factory for a cuboid. |
| `smallestCorner()` / `largestCorner()` | Corner `BlockPos` values. |
| `sizeX()` / `sizeY()` / `sizeZ()` | Inclusive dimension on each axis. |
| `volume()` | `sizeX * sizeY * sizeZ`, saturated at `Long.MAX_VALUE` on overflow. |
| `contains(BlockPos)` / `contains(int, int, int)` | Inclusive containment test. |
| `intersects(Cuboid)` | Inclusive overlap test. |
| `union(Cuboid)` / `expand(int)` | Bounding union / grow by an amount on every side. |
| `corners()` | The 8 corner positions. |
| `dimensions()` / `toString()` | `"WxHxD"` / `"[minX..maxX, ...]"`. |

## See Also

- [Implementation class reference](../../../../../../Implementation/06-Class-Reference.md)
- [Selection](Selection.md) — owns a sorted list of cuboids.
