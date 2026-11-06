# SnapshotCell

> **Software Detailed Documentation:** [SnapshotCell_SDD.md](./SnapshotCell_SDD.md)  
> **Source File:** [SnapshotCell.java](../../../../../../../src/main/java/org/almond/minecraftgit/serialization/SnapshotCell.java)

## Table of Contents

- [Usage](#usage)
- [Fields](#fields)
- [SnapshotCell (Constructor)](#snapshotcell-constructor)
- [encode](#encode)
- [decode](#decode)
- [Cell](#cell)
- [See Also](#see-also)

## Usage

TODO: Add usage examples for `SnapshotCell`.

A CSV cell is either empty (coordinate not tracked), a block data string
alone, or block data followed by {@code |nbt|} and Base64-encoded block-entity
JSON. Base64 keeps commas, quotes, and newlines out of the CSV layer.

---

## Fields

- `ENTITY_SEPARATOR` (`String`) — TODO: describe field

---

## SnapshotCell (Constructor)

### Signature
```java
private SnapshotCell()
```

### Description
TODO: Describe what `SnapshotCell` does.

### Parameters
None.

### Returns
`SnapshotCell` instance.

---

## encode

### Signature
```java
public static String encode(String blockData, String blockEntityJson)
```

### Description
TODO: Describe what `encode` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `blockData` | `String` | TODO: describe |
| `blockEntityJson` | `String` | TODO: describe |

### Returns
`String` — TODO: describe return value.

---

## decode

### Signature
```java
public static Cell decode(String token)
```

### Description
TODO: Describe what `decode` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `token` | `String` | TODO: describe |

### Returns
`Cell` — TODO: describe return value.

---

## Cell

### Signature
```java
public record Cell(String blockData, String blockEntity)
```

### Description
TODO: Describe what `Cell` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `blockData` | `String` | TODO: describe |
| `blockEntity` | `String` | TODO: describe |

### Returns
`record` — TODO: describe return value.

---

## See Also

- **Software Detailed Design:** [SnapshotCell_SDD.md](./SnapshotCell_SDD.md)
