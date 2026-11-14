# EntitySnapshot

> **Source File:** [EntitySnapshot.java](../../../../../../../src/main/java/org/almond/minecraftgit/model/EntitySnapshot.java)

## Usage

One captured non-player entity. Position and rotation are kept outside the
serialized payload so a build can be shifted to a new origin without rewriting
NBT. `data` is the mod's serialized entity string (`nbt:<SNBT>`) produced by
`EntityCodec`.

## Fields

| Name | Type | Description |
|------|------|-------------|
| `x`, `y`, `z` | `double` | Absolute world position (entities are not grid-aligned). |
| `rx`, `ry`, `rz` | `double` | Relative position (`absolute - origin`). |
| `yaw`, `pitch` | `float` | Entity rotation. |
| `type` | `String` | Entity registry id, e.g. `minecraft:item_frame`. |
| `data` | `String` | Serialized entity payload (`nbt:<SNBT>`). |

## Methods

| Method | Description |
|--------|-------------|
| `relativeBlock()` | Floors the relative position to a `BlockPos`, used by `Snapshot.retain`. |
| `ORDER` | Sorts by `(y, x, z)`. |
| `compact constructor` | Blank type -> `"unknown"`; null data -> `""`. |

## Identity

`BuildDiff` uses the whole record for entity equality: type, position, rotation
and payload. Moving an entity is therefore represented as one removal plus one
addition, not as a changed entity.

## See Also

- [Snapshot](Snapshot.md) — owns the sorted list of entity snapshots.
- [Implementation class reference](../../../../../../Implementation/06-Class-Reference.md)
