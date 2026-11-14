# BlockSnapshot

> **Source File:** [BlockSnapshot.java](../../../../../../../src/main/java/org/almond/minecraftgit/model/BlockSnapshot.java)

## Usage

One captured block. Both coordinate systems are kept: the repository writer uses
the relative `(rx, ry, rz)` coordinates, while apply/debug tooling uses the
absolute `(x, y, z)` position. A snapshot origin is the world position the
relative coordinates are measured from.

```java
BlockSnapshot block = BlockSnapshot.at(
        new BlockPos(120, 64, -30),   // absolute
        new BlockPos(100, 60, -40),   // selection origin
        "minecraft:oak_planks",       // block state
        null);                        // optional block-entity payload
```

## Fields

| Name | Type | Description |
|------|------|-------------|
| `x`, `y`, `z` | `int` | Absolute world block position. |
| `rx`, `ry`, `rz` | `int` | Relative position (`absolute - origin`). |
| `blockData` | `String` | Canonical block-state string; blank becomes `minecraft:air`. |
| `blockEntity` | `String` | Optional `nbt:<SNBT>` payload; blank becomes `null`. |

## Methods

| Method | Description |
|--------|-------------|
| `at(BlockPos absolute, BlockPos origin, String blockData, String blockEntity)` | Builds a snapshot and computes relative coordinates. |
| `absolute()` / `relative()` | `BlockPos` views of the two coordinate sets. |
| `hasBlockEntity()` | True when a payload is present. |
| `isAir()` | True for `minecraft:air`/`air`. |
| `ORDER` | Sorts by `(y, x, z)`. |

## See Also

- [Snapshot](Snapshot.md) — owns the sorted list of block snapshots.
- [Implementation class reference](../../../../../../Implementation/06-Class-Reference.md)
