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

TODO: Describe how `SnapshotCell` is instantiated and used.

---

## 1. Overview

A CSV cell is either empty (coordinate not tracked), a block data string
alone, or block data followed by {@code |nbt|} and Base64-encoded block-entity
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
| `StandardCharsets` | TODO: describe purpose |
| `Base64` | TODO: describe purpose |

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
| `ENTITY_SEPARATOR` | `String` | TODO: describe |

---

## 5. Constructor

```java
private SnapshotCell()
```

TODO: Provide detailed design explanation for `SnapshotCell`.

---

## 6. `encode()`

```java
public static String encode(String blockData, String blockEntityJson)
```

TODO: Provide detailed design explanation for `encode`.

---

## 7. `decode()`

```java
public static Cell decode(String token)
```

TODO: Provide detailed design explanation for `decode`.

---

## 8. `Cell()`

```java
public record Cell(String blockData, String blockEntity)
```

TODO: Provide detailed design explanation for `Cell`.

---
