# CsvLine — Software Detailed Design

> **API Documentation:** [CsvLine.md](./CsvLine.md)  
> **Source File:** [CsvLine.java](../../../../../../../src/main/java/org/almond/minecraftgit/serialization/CsvLine.java)

## Table of Contents

- [Usage](#usage)
- [1. Overview](#1-overview)
- [2. Package Declaration & Imports](#2-package-declaration-imports)
- [3. Class Declaration](#3-class-declaration)
- [4. Constructor](#4-constructor)
- [5. `format()`](#5-format)
- [6. `escape()`](#6-escape)
- [7. `parse()`](#7-parse)

## Usage

`CsvLine` has only static methods, so it is never instantiated; the writer funnels every emitted row through `format`, and the reader funnels every file line through `parse`. Keeping both directions in one class means the quoting rules are applied and undone by the same code, so a block-state string containing a comma or a Base64 payload is quoted exactly once.

---

## 1. Overview

Minimal RFC-4180 CSV handling. Block data strings contain commas, so every
cell is quoted when needed rather than using a fragile split(',').

---

## 2. Package Declaration & Imports

```java
package org.almond.minecraftgit.serialization;
```

```java
import java.util.ArrayList;
import java.util.List;
```

| Import | Purpose |
|--------|---------|
| `ArrayList` | Backs the growable field list that `parse` fills while scanning a line. |
| `List` | Input type of `format` and return type of `parse`, keeping the API independent of the concrete list implementation. |

---

## 3. Class Declaration

```java
public final class CsvLine {
```


---

## 4. Constructor

```java
private CsvLine()
```

Private no-argument constructor that suppresses the implicit public one, making the utility class non-instantiable; it has no body and no state to initialise.

---

## 5. `format()`

```java
public static String format(List<String> fields)
```

Iterates the input list, appends a comma before every field after the first, and delegates quoting to `escape`. No line terminator is produced: `QuadrantCsvWriter.writeLine` appends the LF so that every generated file uses the repository's LF convention.

---

## 6. `escape()`

```java
public static String escape(String value)
```

First scans for `,`, `"`, `\n` or `\r`; when none is present the value is returned untouched. Otherwise it wraps the value in double quotes and replaces each `"` with `""`, which is the RFC-4180 escape. `null` and the empty string both produce an empty field, preserving the empty-means-untracked cell convention.

---

## 7. `parse()`

```java
public static List<String> parse(String line)
```

Single left-to-right scan with a `quoted` flag. Outside quotes, `,` terminates the current cell and the cell text is added to the list, while `"` starts quoting only when the current cell is still empty (start of field); any other character is appended literally. Inside quotes, `""` appends one quote and skips the second, and a lone `"` closes the field. A final cell is always appended, so `a,b,` has three fields and an empty line has one empty field, and malformed input is tolerated rather than raising an exception.

---
