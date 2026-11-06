# Snapshot

> **Software Detailed Documentation:** [Snapshot_SDD.md](./Snapshot_SDD.md)  
> **Source File:** [Snapshot.java](../../../../../../../src/main/java/org/almond/minecraftgit/model/Snapshot.java)

## Table of Contents

- [Usage](#usage)
- [Fields](#fields)
- [Snapshot (Constructor)](#snapshot-constructor)
- [empty](#empty)
- [selection](#selection)
- [origin](#origin)
- [blocks](#blocks)
- [entities](#entities)
- [capturedAt](#capturedat)
- [blockCount](#blockcount)
- [entityCount](#entitycount)
- [isEmpty](#isempty)
- [rebase](#rebase)
- [retain](#retain)
- [byRelative](#byrelative)
- [byAbsolute](#byabsolute)
- [volume](#volume)
- [dimensions](#dimensions)
- [toString](#tostring)
- [See Also](#see-also)

## Usage

TODO: Add usage examples for `Snapshot`.

Immutable capture of one selection at one moment. Blocks and entities carry
coordinates relative to {@link #origin()} so the repository representation is
portable between worlds.

---

## Fields

- `selection` (`String`) — TODO: describe field
- `origin` ([`BlockPos`](BlockPos.md)) — TODO: describe field
- `blocks` (`List<BlockSnapshot>`) — TODO: describe field
- `entities` (`List<EntitySnapshot>`) — TODO: describe field
- `capturedAt` (`Instant`) — TODO: describe field

---

## Snapshot (Constructor)

### Signature
```java
public Snapshot(String selection, BlockPos origin, List<BlockSnapshot> blocks,
                    List<EntitySnapshot> entities, Instant capturedAt)
```

### Description
TODO: Describe what `Snapshot` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `selection` | `String` | TODO: describe |
| `origin` | [`BlockPos`](BlockPos.md) | TODO: describe |
| `blocks` | `List<BlockSnapshot>` | TODO: describe |
| `entities` | `List<EntitySnapshot>` | TODO: describe |
| `capturedAt` | `Instant` | TODO: describe |

### Returns
`Snapshot` instance.

---

## empty

### Signature
```java
public static Snapshot empty(String selection, BlockPos origin)
```

### Description
TODO: Describe what `empty` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `selection` | `String` | TODO: describe |
| `origin` | [`BlockPos`](BlockPos.md) | TODO: describe |

### Returns
`Snapshot` — TODO: describe return value.

---

## selection

### Signature
```java
public String selection()
```

### Description
TODO: Describe what `selection` does.

### Parameters
None.

### Returns
`String` — TODO: describe return value.

---

## origin

### Signature
```java
public BlockPos origin()
```

### Description
TODO: Describe what `origin` does.

### Parameters
None.

### Returns
[`BlockPos`](BlockPos.md) — TODO: describe return value.

---

## blocks

### Signature
```java
public List<BlockSnapshot> blocks()
```

### Description
TODO: Describe what `blocks` does.

### Parameters
None.

### Returns
`List<BlockSnapshot>` — TODO: describe return value.

---

## entities

### Signature
```java
public List<EntitySnapshot> entities()
```

### Description
TODO: Describe what `entities` does.

### Parameters
None.

### Returns
`List<EntitySnapshot>` — TODO: describe return value.

---

## capturedAt

### Signature
```java
public Instant capturedAt()
```

### Description
TODO: Describe what `capturedAt` does.

### Parameters
None.

### Returns
`Instant` — TODO: describe return value.

---

## blockCount

### Signature
```java
public int blockCount()
```

### Description
TODO: Describe what `blockCount` does.

### Parameters
None.

### Returns
`int` — TODO: describe return value.

---

## entityCount

### Signature
```java
public int entityCount()
```

### Description
TODO: Describe what `entityCount` does.

### Parameters
None.

### Returns
`int` — TODO: describe return value.

---

## isEmpty

### Signature
```java
public boolean isEmpty()
```

### Description
TODO: Describe what `isEmpty` does.

### Parameters
None.

### Returns
`boolean` — TODO: describe return value.

---

## rebase

### Signature
```java
public Snapshot rebase(BlockPos newOrigin)
```

### Description
Return an equivalent snapshot measured from a new origin. Absolute world
positions do not change; only the relative coordinates do.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `newOrigin` | [`BlockPos`](BlockPos.md) | TODO: describe |

### Returns
`Snapshot` — TODO: describe return value.

---

## retain

### Signature
```java
public Snapshot retain(Selection selection)
```

### Description
Keep only blocks and entities inside the selection's current cuboids.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `selection` | [`Selection`](Selection.md) | TODO: describe |

### Returns
`Snapshot` — TODO: describe return value.

---

## byRelative

### Signature
```java
public Map<BlockPos, BlockSnapshot> byRelative()
```

### Description
TODO: Describe what `byRelative` does.

### Parameters
None.

### Returns
`Map<BlockPos, BlockSnapshot>` — TODO: describe return value.

---

## byAbsolute

### Signature
```java
public Map<BlockPos, BlockSnapshot> byAbsolute()
```

### Description
TODO: Describe what `byAbsolute` does.

### Parameters
None.

### Returns
`Map<BlockPos, BlockSnapshot>` — TODO: describe return value.

---

## volume

### Signature
```java
public long volume()
```

### Description
TODO: Describe what `volume` does.

### Parameters
None.

### Returns
`long` — TODO: describe return value.

---

## dimensions

### Signature
```java
public String dimensions()
```

### Description
TODO: Describe what `dimensions` does.

### Parameters
None.

### Returns
`String` — TODO: describe return value.

---

## toString

### Signature
```java
@Override
public String toString()
```

### Description
TODO: Describe what `toString` does.

### Parameters
None.

### Returns
`String` — TODO: describe return value.

---

## See Also

- **Software Detailed Design:** [Snapshot_SDD.md](./Snapshot_SDD.md)
