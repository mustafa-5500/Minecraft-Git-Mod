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

`QuadrantCsvWriter.writeSelection(selectionDir, snapshot)` is the only public entry point: it rewrites a selection's generated CSV tree from a `Snapshot`. For example, a snapshot containing blocks at relative `(0,0,0)` and `(-1,0,0)` on Y 0 under origin `0,0,0` writes `y_0/pp.csv` and `y_0/np.csv`, and a non-empty `snapshot.entities()` adds `entities.csv`.

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
a cell containing `minecraft:air` (tracked and currently air).

---

## QuadrantCsvWriter (Constructor)

### Signature
```java
private QuadrantCsvWriter()
```

### Description
Private and empty: the class is a static namespace, so no writer instance or open file handle is kept between calls.

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
Creates the selection directory, deletes previously generated CSVs, and returns without writing anything when the snapshot has neither blocks nor entities. Blocks are grouped by relative Y (`TreeMap`, ascending), then by `Quadrant` (`EnumMap`), then by `CellKey`, and each non-empty quadrant is written to `y_<ry>/<code>.csv`; entities are written last to `entities.csv`. `IOException` is wrapped in `SerializationException` naming the selection and path.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `selectionDir` | `Path` | Target directory for the selection tree; created if missing and stripped of previously generated CSV files. |
| `snapshot` | [`Snapshot`](../model/Snapshot.md) | Snapshot to serialise; its origin is written into every header and its blocks supply the relative coordinates. |

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
Writes one quadrant file for one Y level. It first scans the cells for the maximum row and column so every row has the same width, then emits the header `# minecraftgit-csv v1 origin=x,y,z y=<y> quadrant=<code>` followed by rows `0..maxRow` with columns `0..maxColumn`. Each cell is looked up by `quadrant.rowToX`/`columnToZ`, encoded with `SnapshotCell.encode`, and a missing cell becomes an empty (untracked) field, while a tracked air block is written as `minecraft:air`; every row is joined with `CsvLine.format`.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `file` | `Path` | Destination `y_<n>/<code>.csv` file. |
| `origin` | [`BlockPos`](../model/BlockPos.md) | Selection origin written into the header. |
| `y` | `int` | Relative Y level written into the header and shared by every block in the map. |
| `quadrant` | [`Quadrant`](Quadrant.md) | Quadrant that fixes the row/column mapping and the file code. |
| `` | `Map<CellKey` | Cell map for this Y level and quadrant, keyed by relative X/Z. |
| `cells` | `BlockSnapshot>` | Remaining type of that map: the block snapshots that supply each cell's data. |

### Returns
`void`

---

## writeEntities

### Signature
```java
private static void writeEntities(Path file, BlockPos origin, List<EntitySnapshot> entities)
```

### Description
Writes `entities.csv` with the header `# minecraftgit-entities v1 origin=x,y,z`, then one row per entity giving relative X/Y/Z as doubles, yaw and pitch as floats, the registry type id and the entity SNBT Base64-encoded as UTF-8. Rows go through `CsvLine.format`, so a payload requiring quoting is handled by the shared CSV rules.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `file` | `Path` | Destination `entities.csv`. |
| `origin` | [`BlockPos`](../model/BlockPos.md) | Selection origin written into the header; entity rows store offsets from it. |
| `entities` | `List<EntitySnapshot>` | Entities to write, in snapshot order. |

### Returns
`void`

---

## writeLine

### Signature
```java
private static void writeLine(BufferedWriter writer, String line)
```

### Description
Writes the already-formatted line followed by a single `\n`; no platform separator is used, matching the repository's LF-only CSV convention.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `writer` | `BufferedWriter` | Open writer for the target file. |
| `line` | `String` | Line content without a terminator. |

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
| `selectionDir` | `Path` | Selection directory whose `entities.csv` and `y_<n>` directories are removed, leaving `meta.json` and other files untouched. |

### Returns
`void`

---

## deleteRecursively

### Signature
```java
private static void deleteRecursively(Path path)
```

### Description
Deletes a file, or a directory with all descendants, walking the tree in reverse order so directories are empty before removal; a path that does not exist is ignored, which makes the cleanup idempotent.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `path` | `Path` | File or directory to delete; may be absent. |

### Returns
`void`

---

## CellKey

### Signature
```java
private record CellKey(int rx, int rz)
```

### Description
Private record used as the map key that identifies a cell within one quadrant; its generated `equals`/`hashCode` allow a block to be found by relative coordinates without scanning a list. Because the map is already partitioned by Y and quadrant, only `rx` and `rz` take part in the key.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `rx` | `int` | Relative X offset from the selection origin. |
| `rz` | `int` | Relative Z offset from the selection origin. |

### Returns
`record` — A `CellKey` instance whose `rx()` and `rz()` accessors expose the coordinates.

---

## See Also

- **Software Detailed Design:** [QuadrantCsvWriter_SDD.md](./QuadrantCsvWriter_SDD.md)
