# ApplyPlan

> **Source File:** [ApplyPlan.java](../../../../../../../src/main/java/org/almond/minecraftgit/world/ApplyPlan.java)

## Usage

Absolute world writes needed to turn one snapshot into another. Relative
coordinates are the canonical Git representation:

- placements land at `targetOrigin + relative`,
- removals happen at `oldOrigin + relative`,
- if the origin moved, the whole build is removed from the old location and
  placed at the new one (E4).

Built by `PullCommands` for pull/checkout and by `PullCommands.abort` through
`restore`.

```java
ApplyPlan plan = ApplyPlan.between(selection, localHead, localOrigin,
        targetSnapshot, localOrigin, true);
ApplyResult result = applyService.apply(world, plan).join();
```

## Fields

| Name | Type | Description |
|------|------|-------------|
| `selectionName` | `String` | Selection this plan belongs to. |
| `owner` | `UUID` | Player UUID used for backup paths. |
| `targetOrigin` | [`BlockPos`](../model/BlockPos.md) | Origin the target snapshot is placed at. |
| `oldEntityOrigin` | [`BlockPos`](../model/BlockPos.md) | Origin the old entities were measured from (removal only), or `null`. |
| `placements` | `Map<BlockPos, BlockSnapshot>` | Absolute position -> block to place (including block-entity payload). |
| `removals` | `Set<BlockPos>` | Absolute positions to set to air. |
| `entityRemovals` | `List<EntitySnapshot>` | Old entities to remove, matched by type/position. |
| `entityPlacements` | `List<EntityPlacement>` | New entities with absolute position/rotation. |
| `createBackup` | `boolean` | Whether `ApplyService` should capture a pre-apply backup. |

## Methods

| Method | Description |
|--------|-------------|
| `between(Selection, Snapshot old, BlockPos oldOrigin, Snapshot target, BlockPos targetOrigin, boolean createBackup)` | Diff-style plan at the block/entity level; a null old snapshot or a moved origin means "place everything, remove everything old". |
| `restore(Selection, Snapshot backup)` | Plan that restores a backup: block snapshots are already absolute; entities are placed from their backup positions. |
| `allPositions()` | Union of placement and removal positions, used for the backup capture. |

## See Also

- [`ApplyService`](../../../../../../client/java/org/almond/minecraftgit/client/world/ApplyService.md) — executes the plan.
- [Snapshot](../model/Snapshot.md) — the source/target data.
- [Implementation class reference](../../../../../../Implementation/06-Class-Reference.md)
