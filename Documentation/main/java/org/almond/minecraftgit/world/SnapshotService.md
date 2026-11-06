# SnapshotService

> **Software Detailed Documentation:** [SnapshotService_SDD.md](./SnapshotService_SDD.md)  
> **Source File:** [SnapshotService.java](../../../../../../../src/main/java/org/almond/minecraftgit/world/SnapshotService.java)

## Table of Contents

- [Usage](#usage)
- [Fields](#fields)
- [SnapshotService (Constructor)](#snapshotservice-constructor)
- [capture](#capture)
- [capturePositions](#capturepositions)
- [captureEntities](#captureentities)
- [ensureLoaded](#ensureloaded)
- [chunkKey](#chunkkey)
- [warn](#warn)
- [See Also](#see-also)

## Usage

TODO: Add usage examples for `SnapshotService`.

Reads blocks, block entities and entities from a {@link Level}. The caller
must run this on the owning thread: the client thread for a
{@code ClientLevel}, or the server thread for an integrated-server level.
<p>Unloaded chunks are skipped and counted. When {@code loadChunks} is true
and the level is a {@link ServerLevel}, chunks are loaded on demand (used by
single-player staging/status/backups so a commit does not silently truncate).</p>

---

## Fields

- `MAX_WARNINGS` (`int`) — TODO: describe field
- `config` ([`ModConfig`](../config/ModConfig.md)) — TODO: describe field
- `debug` ([`DebugLogger`](../debug/DebugLogger.md)) — TODO: describe field

---

## SnapshotService (Constructor)

### Signature
```java
public SnapshotService(ModConfig config, DebugLogger debug)
```

### Description
TODO: Describe what `SnapshotService` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `config` | [`ModConfig`](../config/ModConfig.md) | TODO: describe |
| `debug` | [`DebugLogger`](../debug/DebugLogger.md) | TODO: describe |

### Returns
`SnapshotService` instance.

---

## capture

### Signature
```java
public CaptureResult capture(Selection selection, Level level)
```

### Description
TODO: Describe what `capture` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `selection` | [`Selection`](../model/Selection.md) | TODO: describe |
| `level` | `Level` | TODO: describe |

### Returns
[`CaptureResult`](CaptureResult.md) — TODO: describe return value.

---

## capture

### Signature
```java
public CaptureResult capture(Selection selection, Level level, boolean loadChunks)
```

### Description
TODO: Describe what `capture` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `selection` | [`Selection`](../model/Selection.md) | TODO: describe |
| `level` | `Level` | TODO: describe |
| `loadChunks` | `boolean` | TODO: describe |

### Returns
[`CaptureResult`](CaptureResult.md) — TODO: describe return value.

---

## capturePositions

### Signature
```java
public Snapshot capturePositions(Level level, String selectionName, BlockPos origin,
                                     Collection<BlockPos> positions, boolean loadChunks)
```

### Description
Capture exactly the given positions, for a pre-apply backup. Entities are
not captured here; apply restores blocks and block entities.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `level` | `Level` | TODO: describe |
| `selectionName` | `String` | TODO: describe |
| `origin` | [`BlockPos`](../model/BlockPos.md) | TODO: describe |
| `positions` | `Collection<BlockPos>` | TODO: describe |
| `loadChunks` | `boolean` | TODO: describe |

### Returns
[`Snapshot`](../model/Snapshot.md) — TODO: describe return value.

---

## captureEntities

### Signature
```java
private List<EntitySnapshot> captureEntities(Level level, Selection selection, BlockPos origin,
                                                 List<String> warnings)
```

### Description
TODO: Describe what `captureEntities` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `level` | `Level` | TODO: describe |
| `selection` | [`Selection`](../model/Selection.md) | TODO: describe |
| `origin` | [`BlockPos`](../model/BlockPos.md) | TODO: describe |
| `warnings` | `List<String>` | TODO: describe |

### Returns
`List<EntitySnapshot>` — TODO: describe return value.

---

## ensureLoaded

### Signature
```java
private static boolean ensureLoaded(Level level, int x, int z, boolean loadChunks, Set<Long> loadedChunks)
```

### Description
TODO: Describe what `ensureLoaded` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `level` | `Level` | TODO: describe |
| `x` | `int` | TODO: describe |
| `z` | `int` | TODO: describe |
| `loadChunks` | `boolean` | TODO: describe |
| `loadedChunks` | `Set<Long>` | TODO: describe |

### Returns
`boolean` — TODO: describe return value.

---

## chunkKey

### Signature
```java
private static long chunkKey(int chunkX, int chunkZ)
```

### Description
TODO: Describe what `chunkKey` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `chunkX` | `int` | TODO: describe |
| `chunkZ` | `int` | TODO: describe |

### Returns
`long` — TODO: describe return value.

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

## See Also

- **Software Detailed Design:** [SnapshotService_SDD.md](./SnapshotService_SDD.md)
