# SnapshotCell — Software Detailed Design

> **API Documentation:** [SnapshotCell.md](./SnapshotCell.md)  
> **Source File:** [SnapshotCell.java](../../../../../../../src/main/java/org/almond/minecraftgit/serialization/SnapshotCell.java)

## Table of Contents

- [Usage](#usage)
- [1. Overview](#1-overview)
- [2. Package Declaration & Imports](#2-package-declaration-imports)
- [3. Class Declaration](#3-class-declaration)
- [4. Instance Fields](#4-instance-fields)
- [5. Constructor](#5-constructor)
- [6. `encode()`](#6-encode)
- [7. `decode()`](#7-decode)
- [8. `Cell()`](#8-cell)

## Usage

`SnapshotCell` is a static codec, never instantiated: the writer calls `encode` for every tracked cell and the reader calls `decode` for every non-empty cell. The surrounding CSV layer never sees the payload's commas or braces because they are Base64-encoded before `CsvLine.format` runs, and the block-state string stays readable in the file.

---

## 1. Overview

A CSV cell is either empty (coordinate not tracked), a block data string
alone, or block data followed by `|nbt|` and Base64-encoded block-entity
JSON. Base64 keeps commas, quotes, and newlines out of the CSV layer.

---

## 2. Package Declaration & Imports

```java
package org.almond.minecraftgit.serialization;
```

```java
import java.nio.charset.StandardCharsets;
import java.util.Base64;
```

| Import | Purpose |
|--------|---------|
| `StandardCharsets` | Encodes the payload as UTF-8 before Base64 and decodes the bytes back to a string, keeping non-ASCII NBT stable across platforms. |
| `Base64` | Standard encoder/decoder that maps the payload to a comma-, quote- and newline-free alphabet so the cell never needs CSV quoting. |

---

## 3. Class Declaration

```java
public final class SnapshotCell {
```


---

## 4. Instance Fields

```java
public static final String ENTITY_SEPARATOR;
```

| Field | Type | Description |
|-------|------|-------------|
| `ENTITY_SEPARATOR` | `String` | Constant separator token made of `nbt` between two pipe characters; used both to build and to split a cell, since a block-state string never contains a pipe and the first occurrence unambiguously starts the payload. |

---

## 5. Constructor

```java
private SnapshotCell()
```

Private no-argument constructor; the class only exposes static methods and the nested `Cell` record, so no state is carried.

---

## 6. `encode()`

```java
public static String encode(String blockData, String blockEntityJson)
```

The null/blank check is what preserves the tracked-air versus untracked distinction: a tracked air block has block data `minecraft:air` and no payload, so it is written literally, whereas an untracked coordinate is written as `""` by the writer without calling this method. When a payload exists, the method appends the separator and `Base64.getEncoder().encodeToString(payload.getBytes(UTF_8))`; the standard encoder without line breaks is used, so the result is a single cell token. The parameter name `blockEntityJson` is historical — the current producer is `BlockEntityCodec`, whose native payload is `nbt:`-prefixed SNBT, and a legacy plugin JSON document is passed through unchanged.

---

## 7. `decode()`

```java
public static Cell decode(String token)
```

`indexOf(ENTITY_SEPARATOR)` locates the boundary; if absent, the whole token is returned as block data with a `null` payload, which is the common path for plain states and tracked air. Otherwise the prefix is kept as block data and the suffix is decoded with `Base64.getDecoder().decode`, which throws `IllegalArgumentException` for invalid input; that exception is wrapped in `SerializationException` so the reader can report the offending file. A decoded payload that is blank is mapped to `null`, matching the encoder's treatment of blank payloads.

---

## 8. `Cell()`

```java
public record Cell(String blockData, String blockEntity)
```

The record is the reader's intermediate value: `QuadrantCsvReader` passes `cell.blockData()` and `cell.blockEntity()` straight into `BlockSnapshot.at`, so the block state can be applied even when the payload is later rejected as non-native. Being a record gives value equality and accessors without boilerplate, and `null` is meaningful for `blockEntity` rather than an error.

---
