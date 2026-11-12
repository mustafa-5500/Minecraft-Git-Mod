# BackupService — Software Detailed Design

> **API Documentation:** [BackupService.md](./BackupService.md)  
> **Source File:** [BackupService.java](../../../../../../../src/main/java/org/almond/minecraftgit/backup/BackupService.java)

## Table of Contents

- [Usage](#usage)
- [1. Overview](#1-overview)
- [2. Package Declaration & Imports](#2-package-declaration-imports)
- [3. Class Declaration](#3-class-declaration)
- [4. Instance Fields](#4-instance-fields)
- [5. Constructor](#5-constructor)
- [6. `store()`](#6-store)
- [7. `latest()`](#7-latest)
- [8. `list()`](#8-list)
- [9. `directory()`](#9-directory)
- [10. `prune()`](#10-prune)
- [11. `requireName()`](#11-requirename)
- [12. `Backup()`](#12-backup)

## Usage

`GitServices` creates the service once with `config.backupsRoot()` and `config.maxBackups`, then hands it to `ApplyService` (write path) and `PullCommands` (restore path); all calls are blocking filesystem work, so the write happens on the IO executor. There is no instance lock: name uniqueness comes from the static sequence and per-directory operations are independent.

---

## 1. Overview

Local pre-apply snapshots so a pull or checkout can be undone. Backups are
gzipped binary snapshots under the mod data folder and are pruned to the
configured maximum.

---

## 2. Package Declaration & Imports

```java
package org.almond.minecraftgit.backup;
```

```java
import org.almond.minecraftgit.model.Snapshot;
import org.almond.minecraftgit.staging.SnapshotStore;
import org.almond.minecraftgit.util.Names;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;
```

| Import | Purpose |
|--------|---------|
| [`Snapshot`](../model/Snapshot.md) | Snapshot payload written to and read from each backup file. |
| [`SnapshotStore`](../staging/SnapshotStore.md) | Gzip/binary codec used for backup files. |
| [`Names`](../util/Names.md) | Validates selection names and sanitizes world ids for directory paths. |
| `IOException` | Caught and wrapped in `BackupException`. |
| `Files` | Creates directories, lists files and deletes pruned backups. |
| `Path` | Represents backup directories and files. |
| `ArrayList` | Mutable copies of directory listings for inspection and pruning. |
| `Comparator` | Sorts backup file names so the last entry is newest. |
| `List` | Return type of `list` and the pruning work list. |
| `Optional` | Represents the no-backup-exists case. |
| `UUID` | Owner key in the directory layout. |
| `AtomicLong` | Generates a monotonic suffix for same-millisecond backups. |
| `Stream` | Lists directory entries in try-with-resources. |

---

## 3. Class Declaration

```java
public final class BackupService {
```


---

## 4. Instance Fields

```java
private static final AtomicLong SEQUENCE;
private final Path root;
private final int maxBackups;
private final SnapshotStore store;
```

| Field | Type | Description |
|-------|------|-------------|
| `SEQUENCE` | `AtomicLong` | Static monotonic counter shared by all instances so names sort consistently. |
| `root` | `Path` | Absolute normalized backup root. |
| `maxBackups` | `int` | Per-selection retention cap, minimum 1. |
| `store` | [`SnapshotStore`](../staging/SnapshotStore.md) | Stateless codec instance used for every write and read. |

---

## 5. Constructor

```java
public BackupService(Path root, int maxBackups)
```

Normalizes `root` to an absolute path and stores it; `maxBackups` is clamped with `Math.max(1, maxBackups)` so retention never deletes the backup just written. No filesystem access occurs in the constructor.

---

## 6. `store()`

```java
public Path store(String world, UUID owner, String selection, Snapshot snapshot)
```

`store` derives and creates the directory, then names the file `System.currentTimeMillis() + "-" + SEQUENCE.incrementAndGet() + ".snap"`, which keeps names lexicographically increasing even for backups created in the same millisecond. It writes through `SnapshotStore.write` (gzip, temp file then atomic move), prunes the directory, and wraps any `IOException` as `BackupException`.

---

## 7. `latest()`

```java
public Optional<Backup> latest(String world, UUID owner, String selection)
```

`latest` delegates to `list`, returns `Optional.empty()` for an empty list, and otherwise reads the last path (the greatest file name) with `SnapshotStore.read`; it relies on the timestamp-sequence naming rather than file modification times.

---

## 8. `list()`

```java
public List<Path> list(String world, UUID owner, String selection)
```

`list` resolves the directory, returns `List.of()` when it is absent, otherwise streams the directory, keeps `.snap` files and sorts them by file name so callers get deterministic oldest-to-newest order; an `IOException` becomes a `BackupException`.

---

## 9. `directory()`

```java
public Path directory(String world, UUID owner, String selection)
```

`directory` composes `<root>/<Names.safeWorldName(world)>/<owner or "console">/<validated selection>` and returns it without creating it; a null owner maps to the literal `console` for non-player callers. `requireName` runs first, so an invalid selection name fails before a path is built.

---

## 10. `prune()`

```java
private void prune(Path directory)
```

`prune` re-lists `.snap` files sorted by name and deletes from the front (oldest) while more than `maxBackups` remain; because it runs inside `store` after the new file is durable, the directory never retains more than the cap.

---

## 11. `requireName()`

```java
private static String requireName(String selection)
```

Wraps `Names.requireSelectionName`, translating its `IllegalArgumentException` (bad length or characters) into a `BackupException` so path-building code has a single failure type.

---

## 12. `Backup()`

```java
public record Backup(Snapshot snapshot, Path file)
```

`Backup` is a nested record pairing the decoded `Snapshot` with the `.snap` file that produced it; it is the value returned by `latest` and consumed by `/git pull --abort`.

---
