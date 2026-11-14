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

`GitServices` constructs one instance after the staging service and passes it a `RepoManager`, `StagingService`, `ModConfig`, `DebugLogger` and the Fabric mod version; commands then call `commit` on the shared IO executor. The commit lock makes the instance safe to share between command and apply threads even though the underlying file and JGit work is blocking.

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
| [`ModConfig`](../config/ModConfig.md) | Supplies the temporary directory (`tmpRoot()`) where commit trees are assembled. |
| [`DebugLogger`](../debug/DebugLogger.md) | Emits the one-line commit summary. |
| [`BlockPos`](../model/BlockPos.md) | Carries the selection origin and previous origin in metadata. |
| [`BuildDiff`](../model/BuildDiff.md) | Computes coordinate-level added/changed/removed blocks and entity changes. |
| [`Cuboid`](../model/Cuboid.md) | Converted to origin-relative bounds for `meta.json`. |
| [`Selection`](../model/Selection.md) | Unit of staging and commit; supplies name, owner, cuboids and world. |
| [`Snapshot`](../model/Snapshot.md) | Staged and previously committed block/entity data compared and written as CSV. |
| [`CommitMeta`](../repo/CommitMeta.md) | Reads the old selection origin and writes new per-selection metadata. |
| [`RepoManager`](../repo/RepoManager.md) | Resolves the `RepoService` for the request's world id. |
| [`RepoService`](../repo/RepoService.md) | Provides the repository path and the atomic `commitWorkspace` write. |
| [`QuadrantCsvReader`](../serialization/QuadrantCsvReader.md) | Reads the previous HEAD selection tree into a `Snapshot` for diffing. |
| [`QuadrantCsvWriter`](../serialization/QuadrantCsvWriter.md) | Serializes the staged snapshot into the temporary CSV tree. |
| [`StagedSelection`](../staging/StagedSelection.md) | Handle returned by `StagingService.load`, carrying the snapshot. |
| [`StagingService`](../staging/StagingService.md) | Loads staged snapshots and deletes them after a successful commit. |
| `IOException` | Caught around temp-tree work and wrapped in `CommitException`. |
| `Files` | Creates temp dirs, walks/copies files and reads meta bytes. |
| `Path` | Locates repository selection directories and temporary trees. |
| `Instant` | Timestamps `CommitMeta.createdAt`. |
| `ArrayList` | Accumulates relative cuboids and selection-name lists. |
| `LinkedHashMap` | Preserves deterministic path order in the commit file map. |
| `LinkedHashSet` | Preserves deterministic order of repository directories to replace. |
| `List` | Holds request selections, committed names and metadata cuboid lists. |
| `Map` | Maps repository-relative path to file bytes for `commitWorkspace`. |
| `Optional` | Represents a missing old `meta.json` and an absent staged snapshot. |
| `Set` | Collects the selection directories replaced in the repository. |
| `ReentrantLock` | Serializes overlapping commit operations. |
| `Stream` | Walks temporary trees for file collection and cleanup. |

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
| `repos` | [`RepoManager`](../repo/RepoManager.md) | Resolves and caches the `RepoService` for the request world. |
| `staging` | [`StagingService`](../staging/StagingService.md) | Durable per-world/player staging area read at commit time. |
| `config` | [`ModConfig`](../config/ModConfig.md) | Supplies `tmpRoot()`; no other config values are used here. |
| `debug` | [`DebugLogger`](../debug/DebugLogger.md) | Logs a concise summary after a successful commit. |
| `modVersion` | `String` | Stamped into `meta.json` as `pluginVersion`. |
| `commitLock` | `ReentrantLock` | Held for the entire commit, including all file and JGit IO. |

---

## 5. Constructor

```java
public CommitService(RepoManager repos, StagingService staging, ModConfig config,
                         DebugLogger debug, String modVersion)
```

Stores the five collaborators; the lock is created eagerly and the constructor performs no IO. The instance is intended to be created once per client and shared, with `commitLock` providing serialization.

---

## 6. `commit()`

```java
public CommitResult commit(CommitRequest request)
```

Under `commitLock`, `commit` validates that the world's repository is initialized and, unless `allowEmpty`, that selections were supplied, then for each selection loads its `StagedSelection`, reads the committed CSV tree and `meta.json` from `repo.path()/selection` (or an empty snapshot) and computes `BuildDiff.between(oldSnapshot, stagedSnapshot)`, skipping the selection when the diff is empty and the origin did not move. For every changed selection it calls `buildMeta`, writes the staged snapshot's CSV tree and `meta.json` into a fresh `commit-*` directory under `config.tmpRoot()`, copies those files into a path-to-bytes map keyed `<selection>/...`, and records the selection directory in a replacement set, deleting each temp directory in a `finally` block. It then builds the message, calls `RepoService.commitWorkspace(files, directories, message, allowEmpty || committedNames.isEmpty())` — throwing `CommitException` if that returns `null` — deletes all staged snapshots of the requested selections, logs, and returns a `CommitResult`, while `IOException` becomes a `CommitException` and the lock is always released.

---

## 7. `buildMeta()`

```java
private CommitMeta buildMeta(CommitRequest request, Selection selection, Snapshot snapshot,
                                 BlockPos previousOrigin)
```

`buildMeta` translates each absolute cuboid into coordinates relative to `snapshot.origin()`, computes `dimensions` as `WxHxD` from the selection bounds (falling back to `snapshot.dimensions()` when bounds are unavailable), and sets `reorigined` when `previousOrigin` is non-null and differs from the current origin. It fills the remaining `CommitMeta` fields from the request (world, player identity, game mode), the format version and mod version, the snapshot counts and `Instant.now()`.

---

## 8. `commitMessage()`

```java
private String commitMessage(CommitRequest request, List<String> committedNames,
                                 long added, long changed, long removed, long entityChanges)
```

The first line is the trimmed user message when supplied, otherwise `player: empty commit`, `player: <selection> - N blocks changed` for one committed selection, or `player: N selections - N blocks changed` for several. It always appends the machine-readable body (`MinecraftGit-Commit: v1`, comma-separated selections, `Changes: +a ~c -r`, `Entities: n`), which is exactly what `CommitResult.message()` returns.

---

## 9. `collectFiles()`

```java
private static void collectFiles(Path root, String selection, Map<String, byte[]> files)
```

Walks `root` with `Files.walk`, filters regular files, normalizes Windows separators to `/`, and inserts each file's bytes into the output map as `<selection>/<relative>`; the stream is closed by try-with-resources and `IOException` propagates to `commit`, which wraps it.

---

## 10. `deleteRecursively()`

```java
private static void deleteRecursively(Path path)
```

Returns immediately when the path does not exist, otherwise walks the tree, sorts entries in reverse order (children before parents) and deletes each with `Files.deleteIfExists`. Failures are swallowed deliberately because temp cleanup is best-effort and must not fail a commit.

---
