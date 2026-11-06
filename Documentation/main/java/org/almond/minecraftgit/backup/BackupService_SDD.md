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

TODO: Describe how `BackupService` is instantiated and used.

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
| [`Snapshot`](../model/Snapshot.md) | TODO: describe purpose |
| [`SnapshotStore`](../staging/SnapshotStore.md) | TODO: describe purpose |
| [`Names`](../util/Names.md) | TODO: describe purpose |
| `IOException` | TODO: describe purpose |
| `Files` | TODO: describe purpose |
| `Path` | TODO: describe purpose |
| `ArrayList` | TODO: describe purpose |
| `Comparator` | TODO: describe purpose |
| `List` | TODO: describe purpose |
| `Optional` | TODO: describe purpose |
| `UUID` | TODO: describe purpose |
| `AtomicLong` | TODO: describe purpose |
| `Stream` | TODO: describe purpose |

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
| `SEQUENCE` | `AtomicLong` | TODO: describe |
| `root` | `Path` | TODO: describe |
| `maxBackups` | `int` | TODO: describe |
| `store` | [`SnapshotStore`](../staging/SnapshotStore.md) | TODO: describe |

---

## 5. Constructor

```java
public BackupService(Path root, int maxBackups)
```

TODO: Provide detailed design explanation for `BackupService`.

---

## 6. `store()`

```java
public Path store(String world, UUID owner, String selection, Snapshot snapshot)
```

TODO: Provide detailed design explanation for `store`.

---

## 7. `latest()`

```java
public Optional<Backup> latest(String world, UUID owner, String selection)
```

TODO: Provide detailed design explanation for `latest`.

---

## 8. `list()`

```java
public List<Path> list(String world, UUID owner, String selection)
```

TODO: Provide detailed design explanation for `list`.

---

## 9. `directory()`

```java
public Path directory(String world, UUID owner, String selection)
```

TODO: Provide detailed design explanation for `directory`.

---

## 10. `prune()`

```java
private void prune(Path directory)
```

TODO: Provide detailed design explanation for `prune`.

---

## 11. `requireName()`

```java
private static String requireName(String selection)
```

TODO: Provide detailed design explanation for `requireName`.

---

## 12. `Backup()`

```java
public record Backup(Snapshot snapshot, Path file)
```

TODO: Provide detailed design explanation for `Backup`.

---
