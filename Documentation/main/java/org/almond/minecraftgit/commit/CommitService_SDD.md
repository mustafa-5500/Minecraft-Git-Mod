# CommitService — Software Detailed Design

> **API Documentation:** [CommitService.md](./CommitService.md)  
> **Source File:** [CommitService.java](../../../../../../../src/main/java/org/almond/minecraftgit/commit/CommitService.java)

## Table of Contents

- [Usage](#usage)
- [1. Overview](#1-overview)
- [2. Package Declaration & Imports](#2-package-declaration-imports)
- [3. Class Declaration](#3-class-declaration)
- [4. Instance Fields](#4-instance-fields)
- [5. Constructor](#5-constructor)
- [6. `commit()`](#6-commit)
- [7. `buildMeta()`](#7-buildmeta)
- [8. `commitMessage()`](#8-commitmessage)
- [9. `collectFiles()`](#9-collectfiles)
- [10. `deleteRecursively()`](#10-deleterecursively)

## Usage

TODO: Describe how `CommitService` is instantiated and used.

---

## 1. Overview

Turns staged snapshots into repository commits: writes the CSV tree, writes
per-selection metadata, produces a readable commit message, and verifies that
something actually changed before touching Git.

---

## 2. Package Declaration & Imports

```java
package org.almond.minecraftgit.commit;
```

```java
import org.almond.minecraftgit.config.ModConfig;
import org.almond.minecraftgit.debug.DebugLogger;
import org.almond.minecraftgit.model.BlockPos;
import org.almond.minecraftgit.model.BuildDiff;
import org.almond.minecraftgit.model.Cuboid;
import org.almond.minecraftgit.model.Selection;
import org.almond.minecraftgit.model.Snapshot;
import org.almond.minecraftgit.repo.CommitMeta;
import org.almond.minecraftgit.repo.RepoManager;
import org.almond.minecraftgit.repo.RepoService;
import org.almond.minecraftgit.serialization.QuadrantCsvReader;
import org.almond.minecraftgit.serialization.QuadrantCsvWriter;
import org.almond.minecraftgit.staging.StagedSelection;
import org.almond.minecraftgit.staging.StagingService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;
```

| Import | Purpose |
|--------|---------|
| [`ModConfig`](../config/ModConfig.md) | TODO: describe purpose |
| [`DebugLogger`](../debug/DebugLogger.md) | TODO: describe purpose |
| [`BlockPos`](../model/BlockPos.md) | TODO: describe purpose |
| [`BuildDiff`](../model/BuildDiff.md) | TODO: describe purpose |
| [`Cuboid`](../model/Cuboid.md) | TODO: describe purpose |
| [`Selection`](../model/Selection.md) | TODO: describe purpose |
| [`Snapshot`](../model/Snapshot.md) | TODO: describe purpose |
| [`CommitMeta`](../repo/CommitMeta.md) | TODO: describe purpose |
| [`RepoManager`](../repo/RepoManager.md) | TODO: describe purpose |
| [`RepoService`](../repo/RepoService.md) | TODO: describe purpose |
| [`QuadrantCsvReader`](../serialization/QuadrantCsvReader.md) | TODO: describe purpose |
| [`QuadrantCsvWriter`](../serialization/QuadrantCsvWriter.md) | TODO: describe purpose |
| [`StagedSelection`](../staging/StagedSelection.md) | TODO: describe purpose |
| [`StagingService`](../staging/StagingService.md) | TODO: describe purpose |
| `IOException` | TODO: describe purpose |
| `Files` | TODO: describe purpose |
| `Path` | TODO: describe purpose |
| `Instant` | TODO: describe purpose |
| `ArrayList` | TODO: describe purpose |
| `LinkedHashMap` | TODO: describe purpose |
| `LinkedHashSet` | TODO: describe purpose |
| `List` | TODO: describe purpose |
| `Map` | TODO: describe purpose |
| `Optional` | TODO: describe purpose |
| `Set` | TODO: describe purpose |
| `ReentrantLock` | TODO: describe purpose |
| `Stream` | TODO: describe purpose |

---

## 3. Class Declaration

```java
public final class CommitService {
```


---

## 4. Instance Fields

```java
private final RepoManager repos;
private final StagingService staging;
private final ModConfig config;
private final DebugLogger debug;
private final String modVersion;
private final ReentrantLock commitLock;
```

| Field | Type | Description |
|-------|------|-------------|
| `repos` | [`RepoManager`](../repo/RepoManager.md) | TODO: describe |
| `staging` | [`StagingService`](../staging/StagingService.md) | TODO: describe |
| `config` | [`ModConfig`](../config/ModConfig.md) | TODO: describe |
| `debug` | [`DebugLogger`](../debug/DebugLogger.md) | TODO: describe |
| `modVersion` | `String` | TODO: describe |
| `commitLock` | `ReentrantLock` | TODO: describe |

---

## 5. Constructor

```java
public CommitService(RepoManager repos, StagingService staging, ModConfig config,
                         DebugLogger debug, String modVersion)
```

TODO: Provide detailed design explanation for `CommitService`.

---

## 6. `commit()`

```java
public CommitResult commit(CommitRequest request)
```

TODO: Provide detailed design explanation for `commit`.

---

## 7. `buildMeta()`

```java
private CommitMeta buildMeta(CommitRequest request, Selection selection, Snapshot snapshot,
                                 BlockPos previousOrigin)
```

TODO: Provide detailed design explanation for `buildMeta`.

---

## 8. `commitMessage()`

```java
private String commitMessage(CommitRequest request, List<String> committedNames,
                                 long added, long changed, long removed, long entityChanges)
```

TODO: Provide detailed design explanation for `commitMessage`.

---

## 9. `collectFiles()`

```java
private static void collectFiles(Path root, String selection, Map<String, byte[]> files)
```

TODO: Provide detailed design explanation for `collectFiles`.

---

## 10. `deleteRecursively()`

```java
private static void deleteRecursively(Path path)
```

TODO: Provide detailed design explanation for `deleteRecursively`.

---
