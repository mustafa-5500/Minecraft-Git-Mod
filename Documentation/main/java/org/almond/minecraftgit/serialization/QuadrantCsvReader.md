# QuadrantCsvReader

> **Software Detailed Documentation:** [QuadrantCsvReader_SDD.md](./QuadrantCsvReader_SDD.md)  
> **Source File:** [QuadrantCsvReader.java](../../../../../../../src/main/java/org/almond/minecraftgit/serialization/QuadrantCsvReader.java)

## Table of Contents

- [Usage](#usage)
- [Fields](#fields)
- [QuadrantCsvReader (Constructor)](#quadrantcsvreader-constructor)
- [readSelection](#readselection)
- [readBlockFile](#readblockfile)
- [readEntityFile](#readentityfile)
- [mergeOrigin](#mergeorigin)
- [See Also](#see-also)

## Usage

`QuadrantCsvReader` is a static entry point: call `readSelection(selectionDir, name, fallbackOrigin)` on a selection directory such as `<repo>/castle` to get a `Snapshot` of absolute block and entity positions; it is the counterpart of `QuadrantCsvWriter` and parses the v1 format described in [Git-Format.md](../../../../../../Git-Format.md). For example, `Snapshot snapshot = QuadrantCsvReader.readSelection(dir, "castle", origin)` loads every `y_<n>/*.csv` plus `entities.csv`.

Reads the repository CSV format written by `QuadrantCsvWriter`.

---

## Fields

- `BLOCK_HEADER` (`Pattern`) — Compiled regex for the v1 block header `# minecraftgit-csv v1 origin=x,y,z y=<relativeY> quadrant=<pp|pn|np|nn>`; its five groups are origin X, Y, Z, relative Y and the quadrant code.
- `ENTITY_HEADER` (`Pattern`) — Compiled regex for the v1 entity header `# minecraftgit-entities v1 origin=x,y,z`; its three groups are the origin coordinates.

---

## QuadrantCsvReader (Constructor)

### Signature
```java
private QuadrantCsvReader()
```

### Description
Private and empty: every operation is static, so the class only holds the two header patterns and the file-walking logic.

### Parameters
None.

### Returns
`QuadrantCsvReader` instance.

---

## readSelection

### Signature
```java
public static Snapshot readSelection(Path selectionDir, String selectionName, BlockPos fallbackOrigin)
```

### Description
Walks the selection directory up to two levels deep, reads every `*.csv` except `entities.csv` as a quadrant file, then reads `entities.csv` from the selection root when present. All headers must agree on one origin; the returned snapshot uses the header origin, else `fallbackOrigin`, else `BlockPos.ORIGIN`, and is timestamped with `Instant.now()`. A missing directory returns `Snapshot.empty`, while I/O and format failures are wrapped in `SerializationException`.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `selectionDir` | `Path` | Selection directory containing `y_<n>/*.csv` and an optional `entities.csv`; it does not have to exist. |
| `selectionName` | `String` | Name recorded on the returned snapshot, independent of the directory name. |
| `fallbackOrigin` | [`BlockPos`](../model/BlockPos.md) | Origin used when no CSV header supplies one; `null` falls back to `BlockPos.ORIGIN`. |

### Returns
[`Snapshot`](../model/Snapshot.md) — The decoded selection with blocks and entities in absolute world coordinates and a capture timestamp of now.

---

## readBlockFile

### Signature
```java
private static void readBlockFile(Path file, List<BlockSnapshot> blocks, BlockPos[] origin)
```

### Description
Reads one quadrant file: validates and parses the header for origin, relative Y and quadrant, merges that origin with the other files, then walks the data rows. Each row index becomes X through `quadrant.rowToX(row)` and each non-empty cell's column becomes Z through `quadrant.columnToZ(column)`; the block is stored at `origin + (rx, y, rz)` with the file origin retained for later re-origining. Blank lines still advance the row counter, and empty cells are skipped as untracked; an empty file, an unrecognised header or a non-numeric coordinate raises `SerializationException`.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `file` | `Path` | Quadrant CSV file to parse. |
| `blocks` | `List<BlockSnapshot>` | Accumulator that decoded blocks are appended to. |
| `origin` | [`BlockPos[]`](../model/BlockPos.md) | Single-element holder for the selection origin, shared across all files so disagreements can be detected. |

### Returns
`void`

---

## readEntityFile

### Signature
```java
private static void readEntityFile(Path file, List<EntitySnapshot> entities, BlockPos[] origin)
```

### Description
Reads `entities.csv`: validates the header and origin, then parses each row's seven columns as relative X/Y/Z doubles, yaw and pitch floats, the entity type id, and a Base64 SNBT payload decoded as UTF-8. The absolute position is the header origin plus the relative offsets, and the relative offsets are kept in the snapshot so entities can be re-based. An unrecognised header, fewer than seven columns, invalid Base64 or a non-numeric value raises `SerializationException`.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `file` | `Path` | `entities.csv` file to parse. |
| `entities` | `List<EntitySnapshot>` | Accumulator that decoded entities are appended to. |
| `origin` | [`BlockPos[]`](../model/BlockPos.md) | Single-element holder for the selection origin, shared with the block files. |

### Returns
`void`

---

## mergeOrigin

### Signature
```java
private static void mergeOrigin(BlockPos[] origin, BlockPos fileOrigin, Path file)
```

### Description
Records the first file origin in the one-element array and rejects any later file whose origin differs, which catches a selection that was only partially re-origined. Both the block reader and the entity reader call it, so every file in one selection directory must have been written from the same origin.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `origin` | [`BlockPos[]`](../model/BlockPos.md) | One-element holder carrying the origin between file reads; element 0 is `null` until the first header is seen. |
| `fileOrigin` | [`BlockPos`](../model/BlockPos.md) | Origin parsed from the current file's header. |
| `file` | `Path` | Current file, used in the mismatch message. |

### Returns
`void`

---

## See Also

- **Software Detailed Design:** [QuadrantCsvReader_SDD.md](./QuadrantCsvReader_SDD.md)
