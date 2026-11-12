# Quadrant — Software Detailed Design

> **API Documentation:** [Quadrant.md](./Quadrant.md)  
> **Source File:** [Quadrant.java](../../../../../../../src/main/java/org/almond/minecraftgit/serialization/Quadrant.java)

## Table of Contents

- [Usage](#usage)
- [1. Overview](#1-overview)
- [2. Package Declaration & Imports](#2-package-declaration-imports)
- [3. Class Declaration](#3-class-declaration)
- [4. Instance Fields](#4-instance-fields)
- [5. `code()`](#5-code)
- [6. `positiveX()`](#6-positivex)
- [7. `positiveZ()`](#7-positivez)
- [8. `rowToX()`](#8-rowtox)
- [9. `columnToZ()`](#9-columntoz)
- [10. `rowForX()`](#10-rowforx)
- [11. `columnForZ()`](#11-columnforz)
- [12. `fromOffsets()`](#12-fromoffsets)
- [13. `fromCode()`](#13-fromcode)

## Usage

`Quadrant` is an enum and is never instantiated directly: `QuadrantCsvWriter` calls `fromOffsets` to bucket each block into one of the four files per Y level, and `QuadrantCsvReader` calls `fromCode` on the header then uses `rowToX`/`columnToZ` to turn a row and column back into relative offsets. For a block at relative `(-3, 5)`, `Quadrant.fromOffsets(-3, 5)` returns `NP`, whose row `2` and column `5` map back to `-3` and `5`.

---

## 1. Overview

Quadrant of the relative XZ plane. The four files per Y level are named by
the signs of X and Z, in that order.

---

## 2. Package Declaration & Imports

```java
package org.almond.minecraftgit.serialization;
```

---

## 3. Class Declaration

```java
public enum Quadrant {
```


---

## 4. Instance Fields

```java
private final String code;
private final boolean positiveX;
private final boolean positiveZ;
```

| Field | Type | Description |
|-------|------|-------------|
| `code` | `String` | Two-letter code supplied per constant and returned by `code()`; used as the file stem and the `quadrant=` header value. |
| `positiveX` | `boolean` | Sign flag for X, set to `true` for `PP` and `PN`; it selects the branch taken by `rowToX`/`rowForX`. |
| `positiveZ` | `boolean` | Sign flag for Z, set to `true` for `PP` and `NP`; it selects the branch taken by `columnToZ`/`columnForZ`. |

---

## 5. `code()`

```java
public String code()
```

Trivial accessor for the `code` field; the writer concatenates it with `.csv` and the reader matches it against the `quadrant=` group of the header regex, so the value must stay in sync with both.

---

## 6. `positiveX()`

```java
public boolean positiveX()
```

Trivial accessor for the X sign flag; it exists for symmetry and for callers that need the sign without performing a mapping, while the mapping methods read the field directly.

---

## 7. `positiveZ()`

```java
public boolean positiveZ()
```

Trivial accessor for the Z sign flag; like `positiveX()`, it exposes the constant's side of the origin without converting an index.

---

## 8. `rowToX()`

```java
public int rowToX(int row)
```

Row 0 is the origin-adjacent coordinate; rows grow away from the origin.

---

## 9. `columnToZ()`

```java
public int columnToZ(int column)
```

Read-side half of the column mapping, called once per parsed cell with the zero-based column index. Positive-Z quadrants return the index unchanged; negative-Z quadrants return `-column - 1`, so column 0 is always the cell next to the origin and the values grow away from it (`-1`, `-2`, …).

---

## 10. `rowForX()`

```java
public int rowForX(int rx)
```

Write-side inverse of `rowToX`. The writer calls it for every block in a quadrant both to find the maximum row (which fixes the number of data rows) and to look a block up while filling a row, so it must stay exactly inverse to `rowToX` or cells would be written to the wrong coordinate.

---

## 11. `columnForZ()`

```java
public int columnForZ(int rz)
```

Write-side inverse of `columnToZ`. It fixes the width of every row in the quadrant: the writer takes the maximum column over all cells and emits that many fields per row, padding unused trailing cells with empty (untracked) values.

---

## 12. `fromOffsets()`

```java
public static Quadrant fromOffsets(int rx, int rz)
```

Bucketing factory used by the writer to split blocks into the four files. The test is `rx >= 0` and `rz >= 0`, so coordinate 0 is deliberately assigned to the positive quadrant; the four combinations produce `PP`, `PN`, `NP` and `NN`. It performs no validation and cannot fail.

---

## 13. `fromCode()`

```java
public static Quadrant fromCode(String code)
```

Parses the `quadrant=<pp|pn|np|nn>` field of a block CSV header. It scans `values()` and compares with `equalsIgnoreCase`, so a hand-edited header in uppercase still loads; an unmatched code throws `SerializationException` rather than returning a default, which lets the reader report the offending file instead of silently misplacing blocks.

---
