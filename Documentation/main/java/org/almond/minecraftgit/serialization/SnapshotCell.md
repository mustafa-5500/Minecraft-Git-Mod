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

`SnapshotCell.encode` and `decode` convert between a block's data string and the single CSV cell that stores it; `QuadrantCsvWriter` calls `encode` while filling a grid row and `QuadrantCsvReader` calls `decode` on each non-empty cell. For example, `SnapshotCell.encode("minecraft:chest[facing=north]", payload)` returns `minecraft:chest[facing=north]|nbt|<base64>` when `payload` is the `nbt:`-prefixed SNBT produced by `BlockEntityCodec`.

A CSV cell is either empty (coordinate not tracked), a block data string
alone, or block data followed by `|nbt|` and Base64-encoded block-entity
JSON. Base64 keeps commas, quotes, and newlines out of the CSV layer.

---

## Fields

- `ENTITY_SEPARATOR` (`String`) — Literal `|nbt|` inserted between the block-state string and the Base64 block-entity payload; its presence marks a cell as carrying NBT.

---

## SnapshotCell (Constructor)

### Signature
```java
private SnapshotCell()
```

### Description
Private and empty: both operations are static, so no `SnapshotCell` instance is ever needed.

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
Returns `blockData` unchanged when the payload is `null` or blank, since an empty block entity means there is nothing to store; otherwise it concatenates `blockData`, `|nbt|` and the standard Base64 encoding of the payload's UTF-8 bytes. `blockData` itself is not validated and may be empty.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `blockData` | `String` | Block-state string as produced by `BlockStateCodec.encode`, or empty. |
| `blockEntityJson` | `String` | Block-entity payload — native SNBT with an `nbt:` prefix, or a legacy plugin JSON document; `null`/blank means no payload. |

### Returns
`String` — The cell text: the block data alone, or block data plus `|nbt|` and Base64; never `null`.

---

## decode

### Signature
```java
public static Cell decode(String token)
```

### Description
Finds the first `|nbt|`; without one the whole token is block data and the payload is `null`. With one, the text before it is the block data, the remainder is Base64-decoded as UTF-8, and a blank result is normalised to `null`. A decoder failure is rethrown as `SerializationException` with the message `Invalid block-entity payload in CSV cell.`

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `token` | `String` | Non-empty cell text from a quadrant CSV row. |

### Returns
`Cell` — The block-state string and the optional block-entity payload.

---

## Cell

### Signature
```java
public record Cell(String blockData, String blockEntity)
```

### Description
Value type returned by `decode`; it keeps the two parts of a cell separate so callers can still apply the block state when the payload is unsupported, for example a plugin JSON document. The record performs no validation: `blockEntity` is `null` when the cell had no `|nbt|` part.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `blockData` | `String` | Block-state string for the block itself. |
| `blockEntity` | `String` | Decoded SNBT or legacy JSON payload, or `null` when absent. |

### Returns
`record` — A `Cell` instance exposing `blockData()` and `blockEntity()`.

---

## See Also

- **Software Detailed Design:** [SnapshotCell_SDD.md](./SnapshotCell_SDD.md)
