# ApplyService

> **Software Detailed Documentation:** [ApplyService_SDD.md](./ApplyService_SDD.md)  
> **Source File:** [ApplyService.java](../../../../../../../../src/client/java/org/almond/minecraftgit/client/world/ApplyService.java)

## Table of Contents

- [Usage](#usage)
- [Fields](#fields)
- [ApplyService (Constructor)](#applyservice-constructor)
- [apply](#apply)
- [begin](#begin)
- [beginWrites](#beginwrites)
- [process](#process)
- [finish](#finish)
- [removeEntities](#removeentities)
- [placeEntities](#placeentities)
- [outOfRange](#outofrange)
- [warn](#warn)
- [BlockWrite](#blockwrite)
- [See Also](#see-also)

## Usage

TODO: Add usage examples for `ApplyService`.

Applies an {@link ApplyPlan} through the integrated server in tick batches.
This is deliberately single-player only: in multiplayer the server is
authoritative and a client-only mod cannot safely write blocks or entities.

---

## Fields

- `MAX_WARNINGS` (`int`) — TODO: describe field
- `snapshots` ([`SnapshotService`](../../../../../../../main/java/org/almond/minecraftgit/world/SnapshotService.md)) — TODO: describe field
- `backups` ([`BackupService`](../../../../../../../main/java/org/almond/minecraftgit/backup/BackupService.md)) — TODO: describe field
- `config` ([`ModConfig`](../../../../../../../main/java/org/almond/minecraftgit/config/ModConfig.md)) — TODO: describe field
- `debug` ([`DebugLogger`](../../../../../../../main/java/org/almond/minecraftgit/debug/DebugLogger.md)) — TODO: describe field
- `io` (`ExecutorService`) — TODO: describe field

---

## ApplyService (Constructor)

### Signature
```java
public ApplyService(SnapshotService snapshots, BackupService backups, ModConfig config,
                        DebugLogger debug, ExecutorService io)
```

### Description
TODO: Describe what `ApplyService` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `snapshots` | [`SnapshotService`](../../../../../../../main/java/org/almond/minecraftgit/world/SnapshotService.md) | TODO: describe |
| `backups` | [`BackupService`](../../../../../../../main/java/org/almond/minecraftgit/backup/BackupService.md) | TODO: describe |
| `config` | [`ModConfig`](../../../../../../../main/java/org/almond/minecraftgit/config/ModConfig.md) | TODO: describe |
| `debug` | [`DebugLogger`](../../../../../../../main/java/org/almond/minecraftgit/debug/DebugLogger.md) | TODO: describe |
| `io` | `ExecutorService` | TODO: describe |

### Returns
`ApplyService` instance.

---

## apply

### Signature
```java
public CompletableFuture<ApplyResult> apply(WorldContext world, ApplyPlan plan)
```

### Description
TODO: Describe what `apply` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `world` | [`WorldContext`](../WorldContext.md) | TODO: describe |
| `plan` | [`ApplyPlan`](../../../../../../../main/java/org/almond/minecraftgit/world/ApplyPlan.md) | TODO: describe |

### Returns
`CompletableFuture<ApplyResult>` — TODO: describe return value.

---

## begin

### Signature
```java
private void begin(MinecraftServer server, ServerLevel level, WorldContext world, ApplyPlan plan,
                       CompletableFuture<ApplyResult> future)
```

### Description
TODO: Describe what `begin` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `server` | `MinecraftServer` | TODO: describe |
| `level` | `ServerLevel` | TODO: describe |
| `world` | [`WorldContext`](../WorldContext.md) | TODO: describe |
| `plan` | [`ApplyPlan`](../../../../../../../main/java/org/almond/minecraftgit/world/ApplyPlan.md) | TODO: describe |
| `future` | `CompletableFuture<ApplyResult>` | TODO: describe |

### Returns
`void`

---

## beginWrites

### Signature
```java
private void beginWrites(MinecraftServer server, ServerLevel level, ApplyPlan plan, Path backup,
                             CompletableFuture<ApplyResult> future)
```

### Description
TODO: Describe what `beginWrites` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `server` | `MinecraftServer` | TODO: describe |
| `level` | `ServerLevel` | TODO: describe |
| `plan` | [`ApplyPlan`](../../../../../../../main/java/org/almond/minecraftgit/world/ApplyPlan.md) | TODO: describe |
| `backup` | `Path` | TODO: describe |
| `future` | `CompletableFuture<ApplyResult>` | TODO: describe |

### Returns
`void`

---

## process

### Signature
```java
private void process()
```

### Description
TODO: Describe what `process` does.

### Parameters
None.

### Returns
`void`

---

## finish

### Signature
```java
private void finish()
```

### Description
TODO: Describe what `finish` does.

### Parameters
None.

### Returns
`void`

---

## removeEntities

### Signature
```java
private int removeEntities()
```

### Description
TODO: Describe what `removeEntities` does.

### Parameters
None.

### Returns
`int` — TODO: describe return value.

---

## placeEntities

### Signature
```java
private int placeEntities()
```

### Description
TODO: Describe what `placeEntities` does.

### Parameters
None.

### Returns
`int` — TODO: describe return value.

---

## outOfRange

### Signature
```java
private static boolean outOfRange(ServerLevel level, BlockPos position)
```

### Description
TODO: Describe what `outOfRange` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `level` | `ServerLevel` | TODO: describe |
| `position` | [`BlockPos`](../../../../../../../main/java/org/almond/minecraftgit/model/BlockPos.md) | TODO: describe |

### Returns
`boolean` — TODO: describe return value.

---

## warn

### Signature
```java
private static void warn(List<String> warnings, String warning)
```

### Description
TODO: Describe what `warn` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `warnings` | `List<String>` | TODO: describe |
| `warning` | `String` | TODO: describe |

### Returns
`void`

---

## BlockWrite

### Signature
```java
private record BlockWrite(BlockPos position, BlockState state, String blockEntity, boolean removal)
```

### Description
TODO: Describe what `BlockWrite` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `position` | [`BlockPos`](../../../../../../../main/java/org/almond/minecraftgit/model/BlockPos.md) | TODO: describe |
| `state` | `BlockState` | TODO: describe |
| `blockEntity` | `String` | TODO: describe |
| `removal` | `boolean` | TODO: describe |

### Returns
`record` — TODO: describe return value.

---

## See Also

- **Software Detailed Design:** [ApplyService_SDD.md](./ApplyService_SDD.md)
