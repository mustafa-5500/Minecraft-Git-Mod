# QuadrantCsvWriter

> **Software Detailed Documentation:** [QuadrantCsvWriter_SDD.md](./QuadrantCsvWriter_SDD.md)  
> **Source File:** [QuadrantCsvWriter.java](../../../../../../../src/main/java/org/almond/minecraftgit/serialization/QuadrantCsvWriter.java)

## Table of Contents

- [Usage](#usage)
- [QuadrantCsvWriter (Constructor)](#quadrantcsvwriter-constructor)
- [writeSelection](#writeselection)
- [writeQuadrant](#writequadrant)
- [writeEntities](#writeentities)
- [writeLine](#writeline)
- [cleanPrevious](#cleanprevious)
- [deleteRecursively](#deleterecursively)
- [CellKey](#cellkey)
- [See Also](#see-also)

## Usage

TODO: Add usage examples for `QuadrantCsvWriter`.

Serializes a snapshot into the repository format:
<pre>
&lt;selection&gt;/y_&lt;y&gt;/pp.csv
&lt;selection&gt;/y_&lt;y&gt;/pn.csv
&lt;selection&gt;/y_&lt;y&gt;/np.csv
&lt;selection&gt;/y_&lt;y&gt;/nn.csv
&lt;selection&gt;/entities.csv
</pre>
Each block CSV has one header line and then one row per relative X, with one
column per relative Z. Empty cells mean "not tracked", which is distinct from
a cell containing {@code minecraft:air} (tracked and currently air).

---

## QuadrantCsvWriter (Constructor)

### Signature
```java
private QuadrantCsvWriter()
```

### Description
TODO: Describe what `QuadrantCsvWriter` does.

### Parameters
None.

### Returns
`QuadrantCsvWriter` instance.

---

## writeSelection

### Signature
```java
public static void writeSelection(Path selectionDir, Snapshot snapshot)
```

### Description
TODO: Describe what `writeSelection` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `selectionDir` | `Path` | TODO: describe |
| `snapshot` | [`Snapshot`](../model/Snapshot.md) | TODO: describe |

### Returns
`void`

---

## writeQuadrant

### Signature
```java
private static void writeQuadrant(Path file, BlockPos origin, int y, Quadrant quadrant,
                                      Map<CellKey, BlockSnapshot> cells)
```

### Description
TODO: Describe what `writeQuadrant` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `file` | `Path` | TODO: describe |
| `origin` | [`BlockPos`](../model/BlockPos.md) | TODO: describe |
| `y` | `int` | TODO: describe |
| `quadrant` | [`Quadrant`](Quadrant.md) | TODO: describe |
| `` | `Map<CellKey` | TODO: describe |
| `cells` | `BlockSnapshot>` | TODO: describe |

### Returns
`void`

---

## writeEntities

### Signature
```java
private static void writeEntities(Path file, BlockPos origin, List<EntitySnapshot> entities)
```

### Description
TODO: Describe what `writeEntities` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `file` | `Path` | TODO: describe |
| `origin` | [`BlockPos`](../model/BlockPos.md) | TODO: describe |
| `entities` | `List<EntitySnapshot>` | TODO: describe |

### Returns
`void`

---

## writeLine

### Signature
```java
private static void writeLine(BufferedWriter writer, String line)
```

### Description
TODO: Describe what `writeLine` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `writer` | `BufferedWriter` | TODO: describe |
| `line` | `String` | TODO: describe |

### Returns
`void`

---

## cleanPrevious

### Signature
```java
private static void cleanPrevious(Path selectionDir)
```

### Description
Remove generated CSV files while preserving a later-written meta.json.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `selectionDir` | `Path` | TODO: describe |

### Returns
`void`

---

## deleteRecursively

### Signature
```java
private static void deleteRecursively(Path path)
```

### Description
TODO: Describe what `deleteRecursively` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `path` | `Path` | TODO: describe |

### Returns
`void`

---

## CellKey

### Signature
```java
private record CellKey(int rx, int rz)
```

### Description
TODO: Describe what `CellKey` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `rx` | `int` | TODO: describe |
| `rz` | `int` | TODO: describe |

### Returns
`record` — TODO: describe return value.

---

## See Also

- **Software Detailed Design:** [QuadrantCsvWriter_SDD.md](./QuadrantCsvWriter_SDD.md)
