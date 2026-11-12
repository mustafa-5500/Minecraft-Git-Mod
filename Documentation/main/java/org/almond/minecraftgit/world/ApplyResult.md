# ApplyResult

> **Source File:** [ApplyResult.java](../../../../../../../src/main/java/org/almond/minecraftgit/world/ApplyResult.java)

## Usage

Result of [`ApplyService.apply`](../../../../../../client/java/org/almond/minecraftgit/client/world/ApplyService.md),
printed by `/git pull`, `/git pull --abort` and `/git checkout`.

## Fields

| Name | Type | Description |
|------|------|-------------|
| `placed` | `int` | Blocks written successfully (non-removal). |
| `removed` | `int` | Blocks set to air successfully. |
| `skipped` | `int` | Out-of-range or failed writes. |
| `entitiesPlaced` | `int` | Entities created. |
| `entitiesRemoved` | `int` | Entities discarded. |
| `backup` | `Path` | Pre-apply backup file, or `null` when no backup was requested. |
| `warnings` | `List<String>` | First `MAX_WARNINGS` (20) problems; unknown states, failed writes, unsupported payloads, entity failures. |

## Methods

| Method | Description |
|--------|-------------|
| `changed()` | `placed + removed`. |
| `summary()` | Human-readable `"X placed, Y removed[, Z skipped][, entities +A/-B]"`. |

## See Also

- [ApplyPlan](ApplyPlan.md) — the input.
- [Implementation class reference](../../../../../../Implementation/06-Class-Reference.md)
