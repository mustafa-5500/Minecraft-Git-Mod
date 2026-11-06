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

TODO: Describe how `QuadrantCsvWriter` is instantiated and used.

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
a cell containing {@code minecraft:air} (tracked and currently air).

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
| [`BlockPos`](../model/BlockPos.md) | TODO: describe purpose |
| [`BlockSnapshot`](../model/BlockSnapshot.md) | TODO: describe purpose |
| [`EntitySnapshot`](../model/EntitySnapshot.md) | TODO: describe purpose |
| [`Snapshot`](../model/Snapshot.md) | TODO: describe purpose |
| `BufferedWriter` | TODO: describe purpose |
| `IOException` | TODO: describe purpose |
| `StandardCharsets` | TODO: describe purpose |
| `Files` | TODO: describe purpose |
| `Path` | TODO: describe purpose |
| `ArrayList` | TODO: describe purpose |
| `Base64` | TODO: describe purpose |
| `Comparator` | TODO: describe purpose |
| `EnumMap` | TODO: describe purpose |
| `HashMap` | TODO: describe purpose |
| `List` | TODO: describe purpose |
| `Map` | TODO: describe purpose |
| `TreeMap` | TODO: describe purpose |
| `Stream` | TODO: describe purpose |

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

TODO: Provide detailed design explanation for `QuadrantCsvWriter`.

---

## 5. `writeSelection()`

```java
public static void writeSelection(Path selectionDir, Snapshot snapshot)
```

TODO: Provide detailed design explanation for `writeSelection`.

---

## 6. `writeQuadrant()`

```java
private static void writeQuadrant(Path file, BlockPos origin, int y, Quadrant quadrant,
                                      Map<CellKey, BlockSnapshot> cells)
```

TODO: Provide detailed design explanation for `writeQuadrant`.

---

## 7. `writeEntities()`

```java
private static void writeEntities(Path file, BlockPos origin, List<EntitySnapshot> entities)
```

TODO: Provide detailed design explanation for `writeEntities`.

---

## 8. `writeLine()`

```java
private static void writeLine(BufferedWriter writer, String line)
```

TODO: Provide detailed design explanation for `writeLine`.

---

## 9. `cleanPrevious()`

```java
private static void cleanPrevious(Path selectionDir)
```

Remove generated CSV files while preserving a later-written meta.json.

---

## 10. `deleteRecursively()`

```java
private static void deleteRecursively(Path path)
```

TODO: Provide detailed design explanation for `deleteRecursively`.

---

## 11. `CellKey()`

```java
private record CellKey(int rx, int rz)
```

TODO: Provide detailed design explanation for `CellKey`.

---
