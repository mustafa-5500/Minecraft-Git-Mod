# SnapshotStore — Software Detailed Design

> **API Documentation:** [SnapshotStore.md](./SnapshotStore.md)  
> **Source File:** [SnapshotStore.java](../../../../../../../src/main/java/org/almond/minecraftgit/staging/SnapshotStore.java)

## Table of Contents

- [Usage](#usage)
- [1. Overview](#1-overview)
- [2. Package Declaration & Imports](#2-package-declaration-imports)
- [3. Class Declaration](#3-class-declaration)
- [4. Instance Fields](#4-instance-fields)
- [5. `write()`](#5-write)
- [6. `read()`](#6-read)
- [7. `readCount()`](#7-readcount)
- [8. `writeString()`](#8-writestring)
- [9. `readString()`](#9-readstring)

## Usage

TODO: Describe how `SnapshotStore` is instantiated and used.

---

## 1. Overview

Compact binary persistence for staged snapshots and pre-pull backups. This is
deliberately not the commit format: staging files are machine-local, may be
large, and are never shared, so a gzipped binary stream is a better fit than
CSV. The magic header and version make corruption detectable.

---

## 2. Package Declaration & Imports

```java
package org.almond.minecraftgit.staging;
```

```java
import org.almond.minecraftgit.model.BlockPos;
import org.almond.minecraftgit.model.BlockSnapshot;
import org.almond.minecraftgit.model.EntitySnapshot;
import org.almond.minecraftgit.model.Snapshot;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
```

| Import | Purpose |
|--------|---------|
| [`BlockPos`](../model/BlockPos.md) | TODO: describe purpose |
| [`BlockSnapshot`](../model/BlockSnapshot.md) | TODO: describe purpose |
| [`EntitySnapshot`](../model/EntitySnapshot.md) | TODO: describe purpose |
| [`Snapshot`](../model/Snapshot.md) | TODO: describe purpose |
| `BufferedInputStream` | TODO: describe purpose |
| `BufferedOutputStream` | TODO: describe purpose |
| `DataInputStream` | TODO: describe purpose |
| `DataOutputStream` | TODO: describe purpose |
| `EOFException` | TODO: describe purpose |
| `IOException` | TODO: describe purpose |
| `StandardCharsets` | TODO: describe purpose |
| `AtomicMoveNotSupportedException` | TODO: describe purpose |
| `Files` | TODO: describe purpose |
| `Path` | TODO: describe purpose |
| `StandardCopyOption` | TODO: describe purpose |
| `Instant` | TODO: describe purpose |
| `ArrayList` | TODO: describe purpose |
| `List` | TODO: describe purpose |
| `GZIPInputStream` | TODO: describe purpose |
| `GZIPOutputStream` | TODO: describe purpose |

---

## 3. Class Declaration

```java
public final class SnapshotStore {
```


---

## 4. Instance Fields

```java
private static final byte[] MAGIC;
private static final int VERSION;
private static final int MAX_BLOCKS;
private static final int MAX_ENTITIES;
private static final int MAX_BLOCK_DATA_BYTES;
private static final int MAX_ENTITY_BYTES;
private static final int MAX_SELECTION_BYTES;
```

| Field | Type | Description |
|-------|------|-------------|
| `MAGIC` | `byte[]` | TODO: describe |
| `VERSION` | `int` | TODO: describe |
| `MAX_BLOCKS` | `int` | TODO: describe |
| `MAX_ENTITIES` | `int` | TODO: describe |
| `MAX_BLOCK_DATA_BYTES` | `int` | TODO: describe |
| `MAX_ENTITY_BYTES` | `int` | TODO: describe |
| `MAX_SELECTION_BYTES` | `int` | TODO: describe |

---

## 5. `write()`

```java
public void write(Path file, Snapshot snapshot)
```

TODO: Provide detailed design explanation for `write`.

---

## 6. `read()`

```java
public Snapshot read(Path file)
```

TODO: Provide detailed design explanation for `read`.

---

## 7. `readCount()`

```java
private static int readCount(DataInputStream in, int max, String label)
```

TODO: Provide detailed design explanation for `readCount`.

---

## 8. `writeString()`

```java
private static void writeString(DataOutputStream out, String value, int maxBytes)
```

TODO: Provide detailed design explanation for `writeString`.

---

## 9. `readString()`

```java
private static String readString(DataInputStream in, int maxBytes)
```

TODO: Provide detailed design explanation for `readString`.

---
