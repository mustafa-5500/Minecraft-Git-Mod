# QuadrantCsvReader — Software Detailed Design

> **API Documentation:** [QuadrantCsvReader.md](./QuadrantCsvReader.md)  
> **Source File:** [QuadrantCsvReader.java](../../../../../../../src/main/java/org/almond/minecraftgit/serialization/QuadrantCsvReader.java)

## Table of Contents

- [Usage](#usage)
- [1. Overview](#1-overview)
- [2. Package Declaration & Imports](#2-package-declaration-imports)
- [3. Class Declaration](#3-class-declaration)
- [4. Instance Fields](#4-instance-fields)
- [5. Constructor](#5-constructor)
- [6. `readSelection()`](#6-readselection)
- [7. `readBlockFile()`](#7-readblockfile)
- [8. `readEntityFile()`](#8-readentityfile)
- [9. `mergeOrigin()`](#9-mergeorigin)

## Usage

`QuadrantCsvReader` is never instantiated; the static `readSelection` is called with the directory of one selection (`<repo>/<selection>`) and a fallback origin, and returns a `Snapshot`. The reader is the read-side counterpart of `QuadrantCsvWriter`: it walks the `y_<n>/<code>.csv` files, validates every header, and merges the declared origins before decoding cells into absolute positions.

---

## 1. Overview

Reads the repository CSV format written by `QuadrantCsvWriter`.

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
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
```

| Import | Purpose |
|--------|---------|
| [`BlockPos`](../model/BlockPos.md) | Immutable XYZ value used for the header origin, the fallback origin and the absolute block position. |
| [`BlockSnapshot`](../model/BlockSnapshot.md) | One decoded block: absolute position, relative offsets and the cell payload. |
| [`EntitySnapshot`](../model/EntitySnapshot.md) | One decoded entity row, including relative offsets, rotation, type and SNBT payload. |
| [`Snapshot`](../model/Snapshot.md) | Aggregate returned by `readSelection` and by `Snapshot.empty` for a missing directory. |
| `BufferedReader` | Line-by-line reading of each CSV file via `Files.newBufferedReader`. |
| `IOException` | Checked failure of the file walk or a read, wrapped into `SerializationException`. |
| `StandardCharsets` | Forces UTF-8 for both reading files and decoding Base64 entity payloads. |
| `Files` | Detects directories and regular files, walks the tree and opens readers. |
| `Path` | Represents the selection directory and each CSV file. |
| `Instant` | Supplies the capture timestamp stored on the returned snapshot. |
| `ArrayList` | Accumulates blocks and entities while files are read. |
| `Base64` | Decodes the entity payload column; also turns decoder failures into `SerializationException`. |
| `List` | Holds blocks/entities and the parsed cells of one row. |
| `Matcher` | Applies the compiled header patterns and extracts their capture groups. |
| `Pattern` | Precompiles the block and entity header grammars once at class load. |
| `Stream` | Implements the depth-limited `Files.walk` used to find every CSV file. |

---

## 3. Class Declaration

```java
public final class QuadrantCsvReader {
```


---

## 4. Instance Fields

```java
private static final Pattern BLOCK_HEADER;
private static final Pattern ENTITY_HEADER;
```

| Field | Type | Description |
|-------|------|-------------|
| `BLOCK_HEADER` | `Pattern` | Anchored regex for the v1 block header (`minecraftgit-csv v1`, `origin=x,y,z`, `y=<ry>` and `quadrant=` followed by one of the four codes); groups 1–3 are origin X/Y/Z, group 4 is the relative Y and group 5 the quadrant code. |
| `ENTITY_HEADER` | `Pattern` | Anchored regex for `# minecraftgit-entities v1 origin=<x>,<y>,<z>`; groups 1–3 are the origin coordinates. |

---

## 5. Constructor

```java
private QuadrantCsvReader()
```

Private no-argument constructor; the class is a pure static facade, so there is no per-instance state and no configuration to pass.

---

## 6. `readSelection()`

```java
public static Snapshot readSelection(Path selectionDir, String selectionName, BlockPos fallbackOrigin)
```

If the directory is absent, returns `Snapshot.empty` with the selection name and the fallback origin (or `BlockPos.ORIGIN` when that is `null`) immediately, so a commit of a never-captured selection is not an error. Otherwise it opens a `Files.walk` limited to depth 2, keeps regular files whose name ends in `.csv` but is not `entities.csv`, sorts them by path (so the order is deterministic) and passes each to `readBlockFile`; it then reads `selectionDir/entities.csv` if it exists. The shared `BlockPos[] origin` is filled by the first header and validated by `mergeOrigin` for every later file. Finally the method resolves the origin (header, else fallback, else `BlockPos.ORIGIN`) and builds a `Snapshot` with `Instant.now()`, noting that this timestamp is read time rather than the original commit time. `IOException` from the walk is wrapped in `SerializationException` with the directory path.

---

## 7. `readBlockFile()`

```java
private static void readBlockFile(Path file, List<BlockSnapshot> blocks, BlockPos[] origin)
```

Reads the header line, rejects a `null` line as `Empty CSV file`, and applies `BLOCK_HEADER`; the regex requires the exact v1 token order, so a foreign header fails with `Unrecognized block CSV header`. It then parses origin and relative Y, resolves the quadrant through `Quadrant.fromCode`, and calls `mergeOrigin`. While reading rows it keeps `row` starting at 0, counts positions of blank lines (they still consume a row index but add nothing), parses each data line with `CsvLine.parse`, and for every non-empty cell calls `SnapshotCell.decode`. Row and column are converted with `rowToX`/`columnToZ`, `fileOrigin.add(rx, y, rz)` produces the absolute position, and `BlockSnapshot.at(absolute, fileOrigin, ...)` keeps both the absolute and relative forms. Number-format failures become `SerializationException("Invalid number in CSV file ...")`.

---

## 8. `readEntityFile()`

```java
private static void readEntityFile(Path file, List<EntitySnapshot> entities, BlockPos[] origin)
```

A missing or `null` header simply returns (an empty file is tolerated for entities, unlike block files). The header is validated with `ENTITY_HEADER` and its origin merged, then each non-blank row is split by `CsvLine.parse` and required to have at least seven columns. Columns are read as `double` relative X/Y/Z, `float` yaw/pitch, the type string, and the Base64 payload decoded to a UTF-8 string; `EntitySnapshot` is constructed with both the absolute position (`origin + relative`) and the relative offsets. An `IllegalArgumentException` from the decoder is rethrown as `Invalid entity payload`, and numeric failures as `Invalid number in entity CSV`.

---

## 9. `mergeOrigin()`

```java
private static void mergeOrigin(BlockPos[] origin, BlockPos fileOrigin, Path file)
```

Implements the origin contract shared by all files of a selection: the first call stores `fileOrigin` in element 0; every later call compares with `equals` and throws `SerializationException` on mismatch, naming the file and the expected origin. This is the guard against a selection whose Y directories were produced with different origins, which would otherwise place blocks at wrong absolute coordinates. Using a one-element array rather than a return value lets both the block loop and the entity reader share one mutable origin without extra state objects.

---
