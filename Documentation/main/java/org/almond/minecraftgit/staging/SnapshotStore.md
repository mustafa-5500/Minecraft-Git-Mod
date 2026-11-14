# SnapshotStore

> **Software Detailed Documentation:** [SnapshotStore_SDD.md](./SnapshotStore_SDD.md)  
> **Source File:** [SnapshotStore.java](../../../../../../../src/main/java/org/almond/minecraftgit/staging/SnapshotStore.java)

## Table of Contents

- [Usage](#usage)
- [Fields](#fields)
- [write](#write)
- [read](#read)
- [readCount](#readcount)
- [writeString](#writestring)
- [readString](#readstring)
- [See Also](#see-also)

## Usage

Create a single stateless instance and use it through [`StagingService`](StagingService.md): `store.write(file, snapshot)` persists a capture at a staging path, and `Snapshot restored = store.read(file)` loads it again during commit or status. The store only serializes snapshots; the staging service owns the per-world/per-player path layout and decides when files are written or deleted.

Compact binary persistence for staged snapshots and pre-pull backups. This is
deliberately not the commit format: staging files are machine-local, may be
large, and are never shared, so a gzipped binary stream is a better fit than
CSV. The magic header and version make corruption detectable.

---

## Fields

- `MAGIC` (`byte[]`) — Eight-byte header (`MCGITSNP`) written at the start of every file and checked on read to reject non-staging or corrupt data.
- `VERSION` (`int`) — Binary format version (currently `1`); `read` refuses files written with a different version.
- `MAX_BLOCKS` (`int`) — Upper bound of 10,000,000 on the declared block count, enforced before allocating or looping.
- `MAX_ENTITIES` (`int`) — Upper bound of 1,000,000 on the declared entity count.
- `MAX_BLOCK_DATA_BYTES` (`int`) — Per-block limit of 1,048,576 UTF-8 bytes for the encoded block-state string.
- `MAX_ENTITY_BYTES` (`int`) — Per-entity limit of 8,388,608 UTF-8 bytes for block-entity NBT and entity data strings.
- `MAX_SELECTION_BYTES` (`int`) — 128-byte limit for the selection name stored in the header.

---

## write

### Signature
```java
public void write(Path file, Snapshot snapshot)
```

### Description
Serializes `snapshot` as a gzipped binary stream in a fixed order: magic, version, selection name, absolute origin, each block's absolute and relative coordinates plus block data/block-entity NBT, and each entity's absolute/relative position, yaw/pitch, type and data. It creates the parent directory, writes to a sibling `.tmp` file, then replaces `file` only after that stream is complete (using `ATOMIC_MOVE` where the filesystem supports it), so the target is not touched while it is being written. Any `IOException` is wrapped in [`StagingException`](StagingException.md); a crash can leave the `.tmp` file behind.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `file` | `Path` | Destination staging file; its parent directory is created if missing. |
| `snapshot` | [`Snapshot`](../model/Snapshot.md) | Snapshot to persist; its origin and both absolute and relative coordinates are stored. |

### Returns
`void`

---

## read

### Signature
```java
public Snapshot read(Path file)
```

### Description
Opens `file`, verifies the eight-byte magic and format version, then decodes the selection name, origin, block list and entity list, applying the `MAX_*` bounds through `readCount` and `readString`. It returns a new [`Snapshot`](../model/Snapshot.md) whose `capturedAt` is the current time, and throws [`StagingException`](StagingException.md) when the file is missing, has the wrong magic/version, declares an unreasonable count or string length, or is truncated (`EOFException`).

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `file` | `Path` | Staging file to decode; must be a regular file. |

### Returns
[`Snapshot`](../model/Snapshot.md) — Decoded snapshot with the stored origin and coordinates, stamped with `Instant.now()`.

---

## readCount

### Signature
```java
private static int readCount(DataInputStream in, int max, String label)
```

### Description
Reads one big-endian `int` count from `in` and validates that it lies in `0..max`, so corrupt headers are rejected before any allocation or loop. It throws [`StagingException`](StagingException.md) naming the offending `label` when the count is negative or above the limit.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `in` | `DataInputStream` | Data input positioned at the count field. |
| `max` | `int` | Largest accepted count for the labelled collection. |
| `label` | `String` | Human-readable collection name (`block` or `entity`) used in the error message. |

### Returns
`int` — Validated, non-negative count within the supplied limit.

---

## writeString

### Signature
```java
private static void writeString(DataOutputStream out, String value, int maxBytes)
```

### Description
Writes a length-prefixed UTF-8 string, mapping `null` to an empty string and rejecting values whose encoded form exceeds `maxBytes` before writing. It emits the byte length as an `int` followed by the raw bytes, so `readString` can enforce the same bound; an over-limit value throws [`StagingException`](StagingException.md).

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `out` | `DataOutputStream` | Data output for the current snapshot file. |
| `value` | `String` | String to encode; `null` is stored as an empty string. |
| `maxBytes` | `int` | Maximum permitted UTF-8 byte length. |

### Returns
`void`

---

## readString

### Signature
```java
private static String readString(DataInputStream in, int maxBytes)
```

### Description
Reads an `int` byte length, rejects negative or over-limit lengths, then reads exactly that many bytes with `readFully` and decodes them as UTF-8. A truncated stream propagates `EOFException` (reported as a truncated snapshot by `read`), and an unreasonable length throws [`StagingException`](StagingException.md).

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `in` | `DataInputStream` | Data input positioned at the length field. |
| `maxBytes` | `int` | Maximum accepted UTF-8 byte length. |

### Returns
`String` — Decoded string, or an empty string when the stored length is zero.

---

## See Also

- **Software Detailed Design:** [SnapshotStore_SDD.md](./SnapshotStore_SDD.md)
