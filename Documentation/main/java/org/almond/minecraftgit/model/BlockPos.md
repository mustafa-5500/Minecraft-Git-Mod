# BlockPos

> **Source File:** [BlockPos.java](../../../../../../../src/main/java/org/almond/minecraftgit/model/BlockPos.java)

## Usage

Immutable integer world position used for both absolute world coordinates and
selection-relative repository coordinates. The type does not know which space it
holds; callers subtract a selection `origin` to obtain relative coordinates.
`ORIGIN` is `(0, 0, 0)`.

```java
BlockPos absolute = new BlockPos(120, 64, -30);
BlockPos origin = new BlockPos(100, 60, -40);
BlockPos relative = absolute.subtract(origin); // (20, 4, 10)
```

## Fields

| Name | Type | Description |
|------|------|-------------|
| `x` | `int` | Block X coordinate. |
| `y` | `int` | Block Y coordinate. |
| `z` | `int` | Block Z coordinate. |
| `ORIGIN` | `BlockPos` | The `(0, 0, 0)` constant. |

## Methods

| Method | Description |
|--------|-------------|
| `add(BlockPos)` / `add(int, int, int)` | Returns a new position offset by the argument(s). |
| `subtract(BlockPos)` | Returns a new position with the argument subtracted. |
| `relativeTo(BlockPos origin)` | Alias for `subtract(origin)`: absolute -> relative. |
| `absoluteFrom(BlockPos origin)` | Alias for `add(origin)`: relative -> absolute. |
| `compareTo(BlockPos)` | Lexicographic `(x, y, z)` order; used by sorted maps/sets. |
| `toString()` | Returns `"x, y, z"`. |

## See Also

- [Implementation class reference](../../../../../../Implementation/06-Class-Reference.md)
- [Selection](Selection.md) — owns the origin that converts between spaces.
