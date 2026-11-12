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

`SnapshotStore` is stateless, so one instance is created at mod startup and injected into [`StagingService`](StagingService.md); service code calls `write` on every `/git add` (and on rebase/restrict) and `read` whenever staged data is needed for `/git commit` or `/git status`. The store deliberately has no path policy: callers pass the fully resolved staging file, which keeps the binary format independent of world/player layout.

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
| [`BlockPos`](../model/BlockPos.md) | Reconstructs the snapshot origin and reads/writes block coordinates. |
| [`BlockSnapshot`](../model/BlockSnapshot.md) | Represents each persisted block row (position, relative coordinates, block data, block entity). |
| [`EntitySnapshot`](../model/EntitySnapshot.md) | Represents each persisted entity row (position, relative coordinates, rotation, type, data). |
| [`Snapshot`](../model/Snapshot.md) | Aggregate written and returned by the store. |
| `BufferedInputStream` | Wraps the gzip input to reduce small reads while decoding. |
| `BufferedOutputStream` | Wraps the gzip output to batch writes to the temp file. |
| `DataInputStream` | Provides big-endian primitive reads such as `readInt`, `readDouble`, `readFloat` and `readFully`. |
| `DataOutputStream` | Provides the matching primitive writes for the binary format. |
| `EOFException` | Detects truncation in `read` and maps it to a clear staging error. |
| `IOException` | Caught and wrapped as `StagingException` for read/write failures. |
| `StandardCharsets` | Specifies UTF-8 for every string field. |
| `AtomicMoveNotSupportedException` | Triggers the plain-replace fallback when atomic rename is unavailable. |
| `Files` | Creates directories, opens streams, checks existence and moves the temp file. |
| `Path` | Identifies the staging file and its temp sibling. |
| `StandardCopyOption` | Requests replacement and, where possible, an atomic move. |
| `Instant` | Stamps the returned snapshot with the read time. |
| `ArrayList` | Accumulates decoded blocks and entities. |
| `List` | Holds the decoded collections passed to `Snapshot`. |
| `GZIPInputStream` | Decompresses the staging stream on read. |
| `GZIPOutputStream` | Compresses the staging stream on write. |

---

## 3. Class Declaration

```java
public final class SnapshotStore {
```

The class is final and stateless — every field is a static format constant — so one instance can be shared freely; each method is a pure serialization operation over caller-supplied paths and streams.

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
| `MAGIC` | `byte[]` | Constant eight-byte signature `MCGITSNP`, checked first on read. |
| `VERSION` | `int` | Format version `1`, written after the magic and required to match on read. |
| `MAX_BLOCKS` | `int` | 10,000,000-block cap applied to the declared count. |
| `MAX_ENTITIES` | `int` | 1,000,000-entity cap applied to the declared count. |
| `MAX_BLOCK_DATA_BYTES` | `int` | 1 MiB cap per block-data string. |
| `MAX_ENTITY_BYTES` | `int` | 8 MiB cap per block-entity or entity-data string. |
| `MAX_SELECTION_BYTES` | `int` | 128-byte cap for the stored selection name. |

---

## 5. `write()`

```java
public void write(Path file, Snapshot snapshot)
```

`write` builds the file through a temp file. It creates the parent directory, opens a `GZIPOutputStream` over `file + ".tmp"` through a `DataOutputStream`, and emits a fixed sequence: magic, version, selection name, origin, block count and block rows, then entity count and entity rows. Every string is length-prefixed and capped by `writeString`, making the format self-delimiting and every later read bounds-checkable. The temp file is then moved over `file` with `REPLACE_EXISTING | ATOMIC_MOVE`; on `AtomicMoveNotSupportedException` the same move is retried without `ATOMIC_MOVE`, after the complete stream has been written. `IOException` becomes `StagingException`; the old file is left in place until the move, and a failure may leave a `.tmp` file behind.

---

## 6. `read()`

```java
public Snapshot read(Path file)
```

`read` rejects a non-regular path immediately, opens a `GZIPInputStream`/`DataInputStream`, and validates the magic and `VERSION` before parsing anything else. It decodes the origin, then uses `readCount` and `readString` for every collection and string, capping counts and lengths before allocation; blocks are six ints plus block data and an optional block-entity string, entities are six doubles plus yaw, pitch, type and data. Wrong magic/version, malformed gzip or an out-of-range count/length raises `StagingException`; `EOFException` is reported as a truncated snapshot, and the returned `Snapshot` is stamped with the current time because no timestamp is persisted.

---

## 7. `readCount()`

```java
private static int readCount(DataInputStream in, int max, String label)
```

Reads the next big-endian `int` and validates it against `max`, throwing a `StagingException` that includes `label` and the bad value when it is negative or too large. Centralising the check keeps both block and entity counts bounded before any `ArrayList` is sized or loop entered.

---

## 8. `writeString()`

```java
private static void writeString(DataOutputStream out, String value, int maxBytes)
```

Encodes the value as UTF-8 (mapping `null` to an empty string), checks the encoded length against `maxBytes`, then writes the length as an `int` followed by the bytes. It mirrors `readString`; a length over the limit raises `StagingException`, while a later I/O failure leaves only the temp file to clean up.

---

## 9. `readString()`

```java
private static String readString(DataInputStream in, int maxBytes)
```

Reads an `int` length, rejects negative or over-`maxBytes` values, then `readFully` reads exactly that many bytes and decodes them as UTF-8. It deliberately lets `EOFException` propagate so `read` can report truncation, and raises `StagingException` for an impossible length.

---
