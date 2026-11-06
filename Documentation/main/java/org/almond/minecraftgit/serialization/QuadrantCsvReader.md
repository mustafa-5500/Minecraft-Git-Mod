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

TODO: Add usage examples for `QuadrantCsvReader`.

Reads the repository CSV format written by {@link QuadrantCsvWriter}.

---

## Fields

- `BLOCK_HEADER` (`Pattern`) — TODO: describe field
- `ENTITY_HEADER` (`Pattern`) — TODO: describe field

---

## QuadrantCsvReader (Constructor)

### Signature
```java
private QuadrantCsvReader()
```

### Description
TODO: Describe what `QuadrantCsvReader` does.

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
TODO: Describe what `readSelection` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `selectionDir` | `Path` | TODO: describe |
| `selectionName` | `String` | TODO: describe |
| `fallbackOrigin` | [`BlockPos`](../model/BlockPos.md) | TODO: describe |

### Returns
[`Snapshot`](../model/Snapshot.md) — TODO: describe return value.

---

## readBlockFile

### Signature
```java
private static void readBlockFile(Path file, List<BlockSnapshot> blocks, BlockPos[] origin)
```

### Description
TODO: Describe what `readBlockFile` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `file` | `Path` | TODO: describe |
| `blocks` | `List<BlockSnapshot>` | TODO: describe |
| `origin` | [`BlockPos[]`](../model/BlockPos.md) | TODO: describe |

### Returns
`void`

---

## readEntityFile

### Signature
```java
private static void readEntityFile(Path file, List<EntitySnapshot> entities, BlockPos[] origin)
```

### Description
TODO: Describe what `readEntityFile` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `file` | `Path` | TODO: describe |
| `entities` | `List<EntitySnapshot>` | TODO: describe |
| `origin` | [`BlockPos[]`](../model/BlockPos.md) | TODO: describe |

### Returns
`void`

---

## mergeOrigin

### Signature
```java
private static void mergeOrigin(BlockPos[] origin, BlockPos fileOrigin, Path file)
```

### Description
TODO: Describe what `mergeOrigin` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `origin` | [`BlockPos[]`](../model/BlockPos.md) | TODO: describe |
| `fileOrigin` | [`BlockPos`](../model/BlockPos.md) | TODO: describe |
| `file` | `Path` | TODO: describe |

### Returns
`void`

---

## See Also

- **Software Detailed Design:** [QuadrantCsvReader_SDD.md](./QuadrantCsvReader_SDD.md)
