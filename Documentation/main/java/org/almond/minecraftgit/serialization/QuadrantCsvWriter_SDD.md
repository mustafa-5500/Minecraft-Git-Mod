# QuadrantCsvWriter — Software Detailed Design

> **API Documentation:** [QuadrantCsvWriter.md](./QuadrantCsvWriter.md)  
> **Source File:** [QuadrantCsvWriter.java](../../../../../../../src/main/java/org/almond/minecraftgit/serialization/QuadrantCsvWriter.java)

## Table of Contents

- [Usage](#usage)
- [1. Overview](#1-overview)
- [2. Package Declaration & Imports](#2-package-declaration-imports)
- [3. Class Declaration](#3-class-declaration)
- [4. Constructor](#4-constructor)
- [5. `writeSelection()`](#5-writeselection)
- [6. `writeQuadrant()`](#6-writequadrant)
- [7. `writeEntities()`](#7-writeentities)
- [8. `writeLine()`](#8-writeline)
- [9. `cleanPrevious()`](#9-cleanprevious)
- [10. `deleteRecursively()`](#10-deleterecursively)
- [11. `CellKey()`](#11-cellkey)

## Usage

`QuadrantCsvWriter` is never instantiated; a single static call to `writeSelection(selectionDir, snapshot)` materialises the whole directory tree for one selection, and every CSV file it creates is rewritten from scratch on each call. The snapshot supplies relative block coordinates (`rx`, `ry`, `rz`) and entities; the snapshot origin is copied into every header so the reader can rebuild absolute positions.

---

## 1. Overview

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

## 2. Package Declaration & Imports

```java
package org.almond.minecraftgit.serialization;
```

```java
import org.almond.minecraftgit.model.BlockPos;
import org.almond.minecraftgit.model.BlockSnapshot;
import org.almond.minecraftgit.model.EntitySnapshot;
import org.almond.minecraftgit.model.Snapshot;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;
```

| Import | Purpose |
|--------|---------|
| [`BlockPos`](../model/BlockPos.md) | Carries the selection origin into the header and supplies it to each quadrant writer. |
| [`BlockSnapshot`](../model/BlockSnapshot.md) | Source of each block's relative coordinates and cell payload. |
| [`EntitySnapshot`](../model/EntitySnapshot.md) | Source of the entity rows written to `entities.csv`. |
| [`Snapshot`](../model/Snapshot.md) | The aggregate being serialised; supplies origin, blocks and entities. |
| `BufferedWriter` | Buffered per-file writer so each CSV is emitted with few system calls. |
| `IOException` | Checked file failure, wrapped into `SerializationException` with the selection name. |
| `StandardCharsets` | UTF-8 for file output and for the bytes fed to Base64. |
| `Files` | Creates directories, opens writers, lists children and walks trees for deletion. |
| `Path` | Identifies the selection directory, Y directories and individual CSV files. |
| `ArrayList` | Reused row buffer plus the single-row field list for entities. |
| `Base64` | Encodes the entity SNBT payload into one CSV field. |
| `Comparator` | `reverseOrder` for deepest-first deletion in `deleteRecursively`. |
| `EnumMap` | Per-Y map keyed by `Quadrant`, giving constant-time bucket lookup for the four files. |
| `HashMap` | Holds a quadrant's cells keyed by `CellKey`. |
| `List` | Row and entity field lists passed to `CsvLine.format`. |
| `Map` | Declares the Y→quadrant→cell grouping and the cell maps. |
| `TreeMap` | Sorts the Y levels ascending so generated directory order and file content are deterministic. |
| `Stream` | Backs `Files.list` for cleanup and `Files.walk` for recursive deletion. |

---

## 3. Class Declaration

```java
public final class QuadrantCsvWriter {
```


---

## 4. Constructor

```java
private QuadrantCsvWriter()
```

Private no-argument constructor for a stateless utility class; grouping maps and writers are locals of `writeSelection`/`writeQuadrant`, so nothing survives a call.

---

## 5. `writeSelection()`

```java
public static void writeSelection(Path selectionDir, Snapshot snapshot)
```

The method is the orchestrator: `Files.createDirectories` ensures the selection path exists, `cleanPrevious` removes only generated artefacts, and an empty snapshot (no blocks and no entities) returns immediately, leaving an empty selection directory. Blocks are then bucketed into `TreeMap<Integer, Map<Quadrant, Map<CellKey, BlockSnapshot>>>`: the outer `TreeMap` sorts relative Y, the `EnumMap` gives one bucket per constant in declaration order, and the inner `HashMap` is keyed by `CellKey(rx, rz)`. For each Y the method creates `y_<ry>` and, for every quadrant with a non-empty bucket, calls `writeQuadrant`; empty quadrants produce no file at all. Entities are written last, only when non-empty. Any `IOException` is rethrown as `SerializationException` mentioning the selection name, the directory and the lower-level message, so the command layer can report it.

---

## 6. `writeQuadrant()`

```java
private static void writeQuadrant(Path file, BlockPos origin, int y, Quadrant quadrant,
                                      Map<CellKey, BlockSnapshot> cells)
```

First pass computes `maxRow`/`maxColumn` with `quadrant.rowForX`/`columnForZ`, which fixes a rectangular grid that covers every tracked cell; there is no trimming of trailing empty columns or rows. The writer then emits the header `# minecraftgit-csv v1 origin=<x>,<y>,<z> y=<y> quadrant=<code>` and loops rows `0..maxRow`, filling a reused `ArrayList<String>` with one entry per column. For each coordinate it looks the block up by the inverse mapping (`rowToX`, `columnToZ`); a `null` lookup adds `""` (untracked), while a present block goes through `SnapshotCell.encode`, so a tracked air block is the literal `minecraft:air` and a block with NBT gets the `|nbt|<base64>` suffix. Each row is serialised with `CsvLine.format` and terminated by `writeLine`.

---

## 7. `writeEntities()`

```java
private static void writeEntities(Path file, BlockPos origin, List<EntitySnapshot> entities)
```

Writes the entity header `# minecraftgit-entities v1 origin=<x>,<y>,<z>` and then one row per snapshot. Numeric columns use `Double.toString`/`Float.toString` for relative position and rotation, followed by the registry type id and `Base64.getEncoder().encodeToString` of the payload's UTF-8 bytes, which is what keeps SNBT braces, quotes and commas out of the CSV grammar. The whole row is passed through `CsvLine.format` even though Base64 never needs quoting, so the writer has a single formatting path. The list order is preserved as supplied by the snapshot.

---

## 8. `writeLine()`

```java
private static void writeLine(BufferedWriter writer, String line)
```

Two explicit writes — the content and a single `'\n'` — instead of `newLine()`, because `newLine()` would emit the platform separator and the repository forces LF for CSV/JSON via `.gitattributes`.

---

## 9. `cleanPrevious()`

```java
private static void cleanPrevious(Path selectionDir)
```

Remove generated CSV files while preserving a later-written meta.json.

Lists the direct children and deletes exactly two kinds of entry: a file named `entities.csv` and any directory whose name matches `y_-?\d+`. `meta.json`, the README and any unrelated file are preserved, so metadata written before or after the CSV pass is never lost. This also removes stale `y_<n>` directories from a previous, taller capture, preventing leftover Y levels from surviving a rewrite.

---

## 10. `deleteRecursively()`

```java
private static void deleteRecursively(Path path)
```

Returns immediately for a non-existent path, then walks the tree, sorts the entries with `Comparator.reverseOrder()` and calls `Files.deleteIfExists` on each. Reverse lexicographic order guarantees descendants (longer paths under the same prefix) are removed before their parent directory, so no non-empty-directory error occurs.

---

## 11. `CellKey()`

```java
private record CellKey(int rx, int rz)
```

A private nested record generated by the compiler with `equals`, `hashCode`, `toString` and the `rx()`/`rz()` accessors. It exists solely as the `HashMap` key inside one quadrant bucket: because the map is already scoped to one Y level and one quadrant, the key needs only the two relative offsets, and value equality on the record makes lookup independent of object identity.

---
