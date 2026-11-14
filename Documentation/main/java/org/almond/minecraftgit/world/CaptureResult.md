# CaptureResult

> **Source File:** [CaptureResult.java](../../../../../../../src/main/java/org/almond/minecraftgit/world/CaptureResult.java)

## Usage

Result of [`SnapshotService.capture`](SnapshotService.md) and the value passed to
`StagingCommands` after a capture. It records everything that could not be read,
so a partial capture is visible rather than silent.

## Fields

| Name | Type | Description |
|------|------|-------------|
| `snapshot` | [`Snapshot`](../model/Snapshot.md) | Captured blocks and entities with the selection origin. |
| `skippedChunks` | `int` | Number of distinct unloaded chunks encountered (positions in them were skipped). |
| `skippedBlocks` | `long` | Positions skipped for height range or unloaded chunks. |
| `skippedEntities` | `int` | Entities dropped by the entity cap. |
| `warnings` | `List<String>` | Capped (20) human-readable warnings. |

## Methods

| Method | Description |
|--------|-------------|
| `partial()` | True when any chunk/block/entity was skipped. |
| `summary()` | Human-readable capture summary including skipped counts. |

## See Also

- [SnapshotService](SnapshotService.md) — produces the result.
- [Implementation class reference](../../../../../../Implementation/06-Class-Reference.md)
