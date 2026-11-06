# StagingService — Software Detailed Design

> **API Documentation:** [StagingService.md](./StagingService.md)  
> **Source File:** [StagingService.java](../../../../../../../src/main/java/org/almond/minecraftgit/staging/StagingService.java)

## Table of Contents

- [Usage](#usage)
- [1. Overview](#1-overview)
- [2. Package Declaration & Imports](#2-package-declaration-imports)
- [3. Class Declaration](#3-class-declaration)
- [4. Instance Fields](#4-instance-fields)
- [5. Constructor](#5-constructor)
- [6. `root()`](#6-root)
- [7. `file()`](#7-file)
- [8. `stage()`](#8-stage)
- [9. `load()`](#9-load)
- [10. `hasStaged()`](#10-hasstaged)
- [11. `stagedNames()`](#11-stagednames)
- [12. `unstage()`](#12-unstage)
- [13. `rebaseStaged()`](#13-rebasestaged)
- [14. `restrictStaged()`](#14-restrictstaged)
- [15. `deleteAll()`](#15-deleteall)
- [16. `store()`](#16-store)

## Usage

TODO: Describe how `StagingService` is instantiated and used.

---

## 1. Overview

Durable staging area. {@code /git add} writes a captured snapshot here;
{@code /git commit} reads it later. Files survive restarts and are keyed by
world id + player + selection, never by repository.

---

## 2. Package Declaration & Imports

```java
package org.almond.minecraftgit.staging;
```

```java
import org.almond.minecraftgit.debug.DebugLogger;
import org.almond.minecraftgit.model.BlockPos;
import org.almond.minecraftgit.model.Selection;
import org.almond.minecraftgit.model.Snapshot;
import org.almond.minecraftgit.util.Names;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
```

| Import | Purpose |
|--------|---------|
| [`DebugLogger`](../debug/DebugLogger.md) | TODO: describe purpose |
| [`BlockPos`](../model/BlockPos.md) | TODO: describe purpose |
| [`Selection`](../model/Selection.md) | TODO: describe purpose |
| [`Snapshot`](../model/Snapshot.md) | TODO: describe purpose |
| [`Names`](../util/Names.md) | TODO: describe purpose |
| `IOException` | TODO: describe purpose |
| `Files` | TODO: describe purpose |
| `Path` | TODO: describe purpose |
| `FileTime` | TODO: describe purpose |
| `Instant` | TODO: describe purpose |
| `ArrayList` | TODO: describe purpose |
| `List` | TODO: describe purpose |
| `Optional` | TODO: describe purpose |
| `UUID` | TODO: describe purpose |
| `Stream` | TODO: describe purpose |

---

## 3. Class Declaration

```java
public final class StagingService {
```


---

## 4. Instance Fields

```java
private final Path root;
private final SnapshotStore store;
private final DebugLogger debug;
```

| Field | Type | Description |
|-------|------|-------------|
| `root` | `Path` | TODO: describe |
| `store` | [`SnapshotStore`](SnapshotStore.md) | TODO: describe |
| `debug` | [`DebugLogger`](../debug/DebugLogger.md) | TODO: describe |

---

## 5. Constructor

```java
public StagingService(Path root, SnapshotStore store, DebugLogger debug)
```

TODO: Provide detailed design explanation for `StagingService`.

---

## 6. `root()`

```java
public Path root()
```

TODO: Provide detailed design explanation for `root`.

---

## 7. `file()`

```java
public Path file(String worldId, UUID owner, String selection)
```

TODO: Provide detailed design explanation for `file`.

---

## 8. `stage()`

```java
public StagedSelection stage(Selection selection, Snapshot snapshot)
```

Write a capture to disk and return the staged handle once it is durable.

---

## 9. `load()`

```java
public Optional<StagedSelection> load(String worldId, UUID owner, String selection)
```

TODO: Provide detailed design explanation for `load`.

---

## 10. `hasStaged()`

```java
public boolean hasStaged(String worldId, UUID owner, String selection)
```

TODO: Provide detailed design explanation for `hasStaged`.

---

## 11. `stagedNames()`

```java
public List<String> stagedNames(String worldId, UUID owner)
```

TODO: Provide detailed design explanation for `stagedNames`.

---

## 12. `unstage()`

```java
public void unstage(String worldId, UUID owner, String selection)
```

TODO: Provide detailed design explanation for `unstage`.

---

## 13. `rebaseStaged()`

```java
public void rebaseStaged(String worldId, UUID owner, String selection, BlockPos newOrigin)
```

Rebase a staged snapshot after {@code /git selection reorigin}.

---

## 14. `restrictStaged()`

```java
public void restrictStaged(String worldId, UUID owner, Selection selection)
```

Trim a staged snapshot after a cuboid is removed: coordinates outside the
remaining cuboids are untracked and must disappear from the next commit.
The origin itself does not move here (E2/E6).

---

## 15. `deleteAll()`

```java
public void deleteAll(String worldId, UUID owner, String selection)
```

TODO: Provide detailed design explanation for `deleteAll`.

---

## 16. `store()`

```java
public SnapshotStore store()
```

TODO: Provide detailed design explanation for `store`.

---
