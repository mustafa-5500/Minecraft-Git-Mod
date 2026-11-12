# PullService — Software Detailed Design

> **API Documentation:** [PullService.md](./PullService.md)  
> **Source File:** [PullService.java](../../../../../../../src/main/java/org/almond/minecraftgit/sync/PullService.java)

## Table of Contents

- [Usage](#usage)
- [1. Overview](#1-overview)
- [2. Package Declaration & Imports](#2-package-declaration-imports)
- [3. Class Declaration](#3-class-declaration)
- [4. Instance Fields](#4-instance-fields)
- [5. Constructor](#5-constructor)
- [6. `prepare()`](#6-prepare)
- [7. `prepareCheckout()`](#7-preparecheckout)
- [8. `cleanup()`](#8-cleanup)
- [9. `cleanup()`](#9-cleanup)
- [10. `shortId()`](#10-shortid)
- [11. `PreparedPull()`](#11-preparedpull)
- [12. `PreparedCheckout()`](#12-preparedcheckout)

## Usage

Instantiated once with a writable temporary root and used by the pull/checkout command path after `RepoService.fetch` has produced a `PullPlan`; `prepare` is only called when `canFastForward()` is true, because the service prepares snapshots for a fast-forward and never merges diverged histories. `prepare` runs off the client thread and returns snapshots; the caller applies them to the world, advances the branch with `fastForward`, and then calls `cleanup`, so no exception thrown here moves the branch and Git is never ahead of the world.

---

## 1. Overview

Reads remote commit trees and local HEAD state into snapshots that the apply
service can turn into world writes. All filesystem/repository work here runs
off the client thread; only selection metadata and world writes happen later.

---

## 2. Package Declaration & Imports

```java
package org.almond.minecraftgit.sync;
```

```java
import org.almond.minecraftgit.model.BlockPos;
import org.almond.minecraftgit.model.Selection;
import org.almond.minecraftgit.model.Snapshot;
import org.almond.minecraftgit.repo.CommitMeta;
import org.almond.minecraftgit.repo.PullPlan;
import org.almond.minecraftgit.repo.RepoService;
import org.almond.minecraftgit.serialization.QuadrantCsvReader;
import org.eclipse.jgit.lib.ObjectId;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
```

| Import | Purpose |
|--------|---------|
| [`BlockPos`](../model/BlockPos.md) | Origins for target/local snapshots and fallback positions. |
| [`Selection`](../model/Selection.md) | Supplies the requested name and configured origin for filtering and fallbacks. |
| [`Snapshot`](../model/Snapshot.md) | Decoded target and local HEAD snapshots carried to the apply step. |
| [`CommitMeta`](../repo/CommitMeta.md) | Reads committed origin metadata from exported `meta.json`. |
| [`PullPlan`](../repo/PullPlan.md) | Provides the remote head and fetch classification for the pull. |
| [`RepoService`](../repo/RepoService.md) | Lists and exports selection trees and exposes the local repository path. |
| [`QuadrantCsvReader`](../serialization/QuadrantCsvReader.md) | Decodes exported CSV quadrants and entities into snapshots. |
| `ObjectId` | Identifies the remote or local commit being prepared. |
| `IOException` | Raised by directory/export operations and wrapped as `PullException`. |
| `Files` | Creates temp directories, checks local HEAD directories and walks paths in cleanup. |
| `Path` | Represents temp roots, export targets and local HEAD directories. |
| `ArrayList` | Collects remote names and prepared results. |
| `Comparator` | Sorts remote names and orders cleanup deletion deepest-first. |
| `List` | Returns prepared pulls and empty-list cases. |

---

## 3. Class Declaration

```java
public final class PullService {
```

The class is final and holds only the normalized temporary root; every call creates and owns its own `pull-*` or `checkout-*` directory, so separate preparations cannot share state through the service.

---

## 4. Instance Fields

```java
private final Path tmpRoot;
```

| Field | Type | Description |
|-------|------|-------------|
| `tmpRoot` | `Path` | Normalized absolute base directory for per-call `pull-*`/`checkout-*` temp directories. |

---

## 5. Constructor

```java
public PullService(Path tmpRoot)
```

Normalizes `tmpRoot` with `toAbsolutePath().normalize()`; it does not create the directory, so construction cannot fail on a missing path and the first `prepare` creates it lazily.

---

## 6. `prepare()`

```java
public List<PreparedPull> prepare(String worldId, List<Selection> localSelections, BlockPos playerOrigin,
                                      RepoService repo, PullPlan plan, String onlySelection)
```

The method first snapshots the remote selection names and sorts them for deterministic output. `onlySelection` is validated case-insensitively against that list; if the remote contains more than one selection, a single-selection pull is refused with a `PullException` because `fastForward` advances the whole branch and applying only one tree would desynchronise the branch from the world. It does not re-check `PullPlan.type()`: the caller refuses `DIVERGED` plans before invoking it, so the export always targets a fast-forwardable remote head. An empty remote with no `onlySelection` returns `List.of()`; otherwise it creates one `pull-*` temp root and, for each name, exports the remote tree (`RepoService.exportSelectionAt`), reads `meta.json` for the origin fallback, decodes the target CSVs, resolves the local selection and origin (selection origin else `playerOrigin`), and reads local HEAD from `<repo>/<name>` when present. I/O failures clean the temp root and throw `PullException`; any other runtime failure cleans up and rethrows.

---

## 7. `prepareCheckout()`

```java
public PreparedCheckout prepareCheckout(Selection selection, RepoService repo, ObjectId commit)
```

Exports a single selection at an arbitrary commit into a new `checkout-*` directory. Unlike `prepare`, it fails with `PullException` if `exportSelectionAt` reports the selection absent, and it reads the commit's `meta.json` origin with the selection's own origin as fallback. It then decodes the local HEAD snapshot from the checked-out `<repo>/<name>` directory (if any) so the caller can diff or back up before applying; all error paths delete the temp root.

---

## 8. `cleanup()`

```java
public void cleanup(Path root)
```

Best-effort recursive delete: `Files.walk` the root, sort paths in reverse order so children go first, and delete each; `IOException` is swallowed because a leftover temp directory must not fail a pull that already applied. A null or missing root returns immediately.

---

## 9. `cleanup()`

```java
public void cleanup(List<PreparedPull> prepared)
```

`prepare` gives every `PreparedPull` a directory under one shared `pull-*` root, so deleting the parent of the first entry removes all exports at once; the empty list is a no-op. This avoids relying on record equality to find a common ancestor.

---

## 10. `shortId()`

```java
private static String shortId(ObjectId id)
```

Uses JGit's `ObjectId.abbreviate(8)` for consistent short hashes in error messages, with `?` as the null fallback; it is private because it only serves diagnostics.

---

## 11. `PreparedPull()`

```java
public record PreparedPull(String name, Snapshot target, CommitMeta meta, Selection localSelection,
                               BlockPos localOrigin, Snapshot localHead, BlockPos localHeadOrigin,
                               Path tempDirectory)
```

Carries everything the apply path needs without re-reading the repository: the decoded target, its commit metadata, the matching local selection/origin, the local HEAD snapshot/origin for a three-way apply or backup, and the temp directory to clean. It is a plain immutable carrier, and its lifetime is bounded by `cleanup`.

---

## 12. `PreparedCheckout()`

```java
public record PreparedCheckout(Snapshot target, CommitMeta meta, Snapshot localHead,
                                   BlockPos localHeadOrigin, Path tempDirectory)
```

Value carrier for a single-commit checkout: target snapshot, metadata, optional local HEAD/origin and the temp root. Having the local HEAD inline lets the checkout command compute a backup or diff before writing; the record itself performs no I/O.

---
