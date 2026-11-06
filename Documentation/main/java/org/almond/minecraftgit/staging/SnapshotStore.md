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

TODO: Add usage examples for `SnapshotStore`.

Compact binary persistence for staged snapshots and pre-pull backups. This is
deliberately not the commit format: staging files are machine-local, may be
large, and are never shared, so a gzipped binary stream is a better fit than
CSV. The magic header and version make corruption detectable.

---

## Fields

- `MAGIC` (`byte[]`) — TODO: describe field
- `VERSION` (`int`) — TODO: describe field
- `MAX_BLOCKS` (`int`) — TODO: describe field
- `MAX_ENTITIES` (`int`) — TODO: describe field
- `MAX_BLOCK_DATA_BYTES` (`int`) — TODO: describe field
- `MAX_ENTITY_BYTES` (`int`) — TODO: describe field
- `MAX_SELECTION_BYTES` (`int`) — TODO: describe field

---

## write

### Signature
```java
public void write(Path file, Snapshot snapshot)
```

### Description
TODO: Describe what `write` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `file` | `Path` | TODO: describe |
| `snapshot` | [`Snapshot`](../model/Snapshot.md) | TODO: describe |

### Returns
`void`

---

## read

### Signature
```java
public Snapshot read(Path file)
```

### Description
TODO: Describe what `read` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `file` | `Path` | TODO: describe |

### Returns
[`Snapshot`](../model/Snapshot.md) — TODO: describe return value.

---

## readCount

### Signature
```java
private static int readCount(DataInputStream in, int max, String label)
```

### Description
TODO: Describe what `readCount` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `in` | `DataInputStream` | TODO: describe |
| `max` | `int` | TODO: describe |
| `label` | `String` | TODO: describe |

### Returns
`int` — TODO: describe return value.

---

## writeString

### Signature
```java
private static void writeString(DataOutputStream out, String value, int maxBytes)
```

### Description
TODO: Describe what `writeString` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `out` | `DataOutputStream` | TODO: describe |
| `value` | `String` | TODO: describe |
| `maxBytes` | `int` | TODO: describe |

### Returns
`void`

---

## readString

### Signature
```java
private static String readString(DataInputStream in, int maxBytes)
```

### Description
TODO: Describe what `readString` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `in` | `DataInputStream` | TODO: describe |
| `maxBytes` | `int` | TODO: describe |

### Returns
`String` — TODO: describe return value.

---

## See Also

- **Software Detailed Design:** [SnapshotStore_SDD.md](./SnapshotStore_SDD.md)
