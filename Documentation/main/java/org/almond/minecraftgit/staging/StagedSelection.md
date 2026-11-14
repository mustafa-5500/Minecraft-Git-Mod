# StagedSelection

> **Source File:** [StagedSelection.java](../../../../../../../src/main/java/org/almond/minecraftgit/staging/StagedSelection.java)

## Usage

A snapshot persisted in the local staging area, ready for a commit. Returned by
[`StagingService.stage`](StagingService.md) and
[`StagingService.load`](StagingService.md).

## Fields

| Name | Type | Description |
|------|------|-------------|
| `snapshot` | [`Snapshot`](../model/Snapshot.md) | Captured blocks/entities with relative coordinates. |
| `file` | `Path` | The `.snap` file the snapshot was read from/written to. |
| `stagedAt` | `Instant` | Write time (from the file mtime when loaded). |

## Methods

| Method | Description |
|--------|-------------|
| `selectionName()` | `snapshot.selection()`. |
| `blockCount()` | Convenience for `snapshot.blockCount()`. |
| `entityCount()` | Convenience for `snapshot.entityCount()`. |

## See Also

- [StagingService](StagingService.md) — produces the record.
- [Implementation class reference](../../../../../../Implementation/06-Class-Reference.md)
