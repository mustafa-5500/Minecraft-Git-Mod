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

TODO: Describe how `QuadrantCsvReader` is instantiated and used.

---

## 1. Overview

Reads the repository CSV format written by {@link QuadrantCsvWriter}.

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
| [`BlockPos`](../model/BlockPos.md) | TODO: describe purpose |
| [`BlockSnapshot`](../model/BlockSnapshot.md) | TODO: describe purpose |
| [`EntitySnapshot`](../model/EntitySnapshot.md) | TODO: describe purpose |
| [`Snapshot`](../model/Snapshot.md) | TODO: describe purpose |
| `BufferedReader` | TODO: describe purpose |
| `IOException` | TODO: describe purpose |
| `StandardCharsets` | TODO: describe purpose |
| `Files` | TODO: describe purpose |
| `Path` | TODO: describe purpose |
| `Instant` | TODO: describe purpose |
| `ArrayList` | TODO: describe purpose |
| `Base64` | TODO: describe purpose |
| `List` | TODO: describe purpose |
| `Matcher` | TODO: describe purpose |
| `Pattern` | TODO: describe purpose |
| `Stream` | TODO: describe purpose |

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
| `BLOCK_HEADER` | `Pattern` | TODO: describe |
| `ENTITY_HEADER` | `Pattern` | TODO: describe |

---

## 5. Constructor

```java
private QuadrantCsvReader()
```

TODO: Provide detailed design explanation for `QuadrantCsvReader`.

---

## 6. `readSelection()`

```java
public static Snapshot readSelection(Path selectionDir, String selectionName, BlockPos fallbackOrigin)
```

TODO: Provide detailed design explanation for `readSelection`.

---

## 7. `readBlockFile()`

```java
private static void readBlockFile(Path file, List<BlockSnapshot> blocks, BlockPos[] origin)
```

TODO: Provide detailed design explanation for `readBlockFile`.

---

## 8. `readEntityFile()`

```java
private static void readEntityFile(Path file, List<EntitySnapshot> entities, BlockPos[] origin)
```

TODO: Provide detailed design explanation for `readEntityFile`.

---

## 9. `mergeOrigin()`

```java
private static void mergeOrigin(BlockPos[] origin, BlockPos fileOrigin, Path file)
```

TODO: Provide detailed design explanation for `mergeOrigin`.

---
