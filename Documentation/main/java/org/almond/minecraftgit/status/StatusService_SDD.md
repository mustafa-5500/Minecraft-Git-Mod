# StatusService — Software Detailed Design

> **API Documentation:** [StatusService.md](./StatusService.md)  
> **Source File:** [StatusService.java](../../../../../../../src/main/java/org/almond/minecraftgit/status/StatusService.java)

## Table of Contents

- [Usage](#usage)
- [1. Overview](#1-overview)
- [2. Package Declaration & Imports](#2-package-declaration-imports)
- [3. Class Declaration](#3-class-declaration)
- [4. Instance Fields](#4-instance-fields)
- [5. Constructor](#5-constructor)
- [6. `report()`](#6-report)
- [7. `diff()`](#7-diff)
- [8. `statusOf()`](#8-statusof)
- [9. `loadStaged()`](#9-loadstaged)
- [10. `readHead()`](#10-readhead)
- [11. `DiffCounts()`](#11-diffcounts)
- [12. `zero()`](#12-zero)
- [13. `of()`](#13-of)
- [14. `clean()`](#14-clean)
- [15. `total()`](#15-total)
- [16. `summary()`](#16-summary)
- [17. `SelectionStatus()`](#17-selectionstatus)
- [18. `clean()`](#18-clean)
- [19. `untracked()`](#19-untracked)
- [20. `dirty()`](#20-dirty)
- [21. `Report()`](#21-report)
- [22. `dirtyCount()`](#22-dirtycount)
- [23. `untrackedCount()`](#23-untrackedcount)
- [24. `cleanCount()`](#24-cleancount)
- [25. `DiffReport()`](#25-diffreport)

## Usage

Created once with the [`RepoManager`](../repo/RepoManager.md), [`StagingService`](../staging/StagingService.md) and logger, then shared by the `/git status` and `/git diff` command paths. The command layer captures the selected regions from the world and passes those snapshots plus the too-large/incomplete flags into `report` or `diff`; the service only reads repository and staged state and never mutates the world.

---

## 1. Overview

Computes status and diffs by comparing three snapshots per selection: HEAD
(committed), staged (local index), and a current capture (the world).

---

## 2. Package Declaration & Imports

```java
package org.almond.minecraftgit.status;
```

```java
import org.almond.minecraftgit.debug.DebugLogger;
import org.almond.minecraftgit.model.BlockPos;
import org.almond.minecraftgit.model.BuildDiff;
import org.almond.minecraftgit.model.Selection;
import org.almond.minecraftgit.model.Snapshot;
import org.almond.minecraftgit.repo.CommitMeta;
import org.almond.minecraftgit.repo.RepoManager;
import org.almond.minecraftgit.repo.RepoService;
import org.almond.minecraftgit.serialization.QuadrantCsvReader;
import org.almond.minecraftgit.staging.StagedSelection;
import org.almond.minecraftgit.staging.StagingService;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
```

| Import | Purpose |
|--------|---------|
| [`DebugLogger`](../debug/DebugLogger.md) | Logs repository and staged-snapshot read failures so status still renders. |
| [`BlockPos`](../model/BlockPos.md) | Fallback origin used when a selection or commit metadata has no origin. |
| [`BuildDiff`](../model/BuildDiff.md) | Computes coordinate-level block and entity differences between two snapshots. |
| [`Selection`](../model/Selection.md) | Supplies selection name, owner/world, tracked flag, origin, cuboids and volume. |
| [`Snapshot`](../model/Snapshot.md) | HEAD, staged and current capture payloads that are compared. |
| [`CommitMeta`](../repo/CommitMeta.md) | Reads the committed origin from `meta.json` when loading HEAD. |
| [`RepoManager`](../repo/RepoManager.md) | Resolves the `RepoService` for the requested world. |
| [`RepoService`](../repo/RepoService.md) | Provides repository path, initialization state, branch and HEAD commit. |
| [`QuadrantCsvReader`](../serialization/QuadrantCsvReader.md) | Decodes the committed CSV files into a HEAD snapshot. |
| [`StagedSelection`](../staging/StagedSelection.md) | Carries the staged snapshot and its timestamp out of `StagingService`. |
| [`StagingService`](../staging/StagingService.md) | Loads the local staged (index) snapshot for a selection. |
| `Files` | Checks whether the HEAD selection directory exists. |
| `Path` | Builds the `<repo>/<selection>` path for HEAD reads. |
| `ArrayList` | Builds the per-selection status list in `report`. |
| `Comparator` | Sorts statuses case-insensitively by name. |
| `List` | Holds the request's selections and the report's statuses. |
| `Map` | Looks up current captures by selection name. |
| `Optional` | Represents missing staged snapshots and commit metadata. |
| `Set` | Looks up too-large and incomplete selection names. |
| `UUID` | Identifies the staging owner. |

---

## 3. Class Declaration

```java
public final class StatusService {
```

The class is final and holds only immutable collaborator references; every `report`/`diff` call is a fresh read-only computation, so the same instance can safely serve repeated status and diff commands.

---

## 4. Instance Fields

```java
private final RepoManager repos;
private final StagingService staging;
private final DebugLogger debug;
```

| Field | Type | Description |
|-------|------|-------------|
| `repos` | [`RepoManager`](../repo/RepoManager.md) | Resolves per-world repository services; no repository is opened at construction. |
| `staging` | [`StagingService`](../staging/StagingService.md) | Source of the staged (index) snapshot and its existence. |
| `debug` | [`DebugLogger`](../debug/DebugLogger.md) | Logger for read failures downgraded to empty/unknown status. |

---

## 5. Constructor

```java
public StatusService(RepoManager repos, StagingService staging, DebugLogger debug)
```

Injects the three collaborators and performs no I/O; all repository and staging access happens per call, keyed by the `worldId`/`owner` arguments. This keeps `StatusService` usable during startup and in tests with stub collaborators.

---

## 6. `report()`

```java
public Report report(String worldId, UUID owner, List<Selection> selections,
                         Map<String, Snapshot> current, Set<String> tooLarge, Set<String> incomplete)
```

Resolves `repos.service(worldId)`, reads branch/HEAD only when the repository is initialized and inside a try/catch so a broken repository yields empty branch/HEAD, then maps each selection through `statusOf` using `current.get(name)`, `tooLarge.contains(name)` and `incomplete.contains(name)`. The resulting list is sorted by lower-cased name for stable chat output. The method is read-only and captures nothing from the world.

---

## 7. `diff()`

```java
public DiffReport diff(String worldId, UUID owner, Selection selection, Snapshot current,
                           boolean tooLarge, boolean incomplete)
```

Resolves the repository and reads HEAD only when initialized, loads the staged snapshot through the defensive `loadStaged`, and picks the base as staged, else HEAD, else `Snapshot.empty` at the selection origin (or world origin). `stagedDiff` compares HEAD/empty to staged, and `workingDiff` compares that base to `current`; both use `BuildDiff` on relative coordinates. A null or incomplete `current` yields an empty working diff rather than a misleading one.

---

## 8. `statusOf()`

```java
private SelectionStatus statusOf(boolean initialized, RepoService repo, Selection selection,
                                     Snapshot current, boolean tooLarge, boolean incomplete)
```

Central per-selection algorithm: load staged and HEAD, compute `stagedDiff` from the head/empty base to the staged snapshot, then choose the working base (staged, else HEAD, else empty at the current origin) and compute `workingDiff` against a complete current capture. If no complete capture exists it records zero counts and sets a note in priority order: too-large volume, incomplete chunks, or missing capture for a selection that is tracked, in HEAD or staged. All reads are defensive, and the record also carries selection volume/cuboid metadata and the `currentChecked` flag.

---

## 9. `loadStaged()`

```java
private Optional<StagedSelection> loadStaged(String worldId, UUID owner, Selection selection)
```

Wraps `staging.load` in a try/catch for `RuntimeException` (for example a corrupt or unreadable `.snap`) and returns `Optional.empty()` after logging; status therefore treats an unreadable index entry as "no staged data" instead of failing the whole command.

---

## 10. `readHead()`

```java
private Snapshot readHead(RepoService repo, Selection selection)
```

Looks for `<repo.path()>/<selection.name()>` on disk, returns null when it is not a directory, then reads `meta.json` for the committed origin (falling back to the selection origin or `BlockPos.ORIGIN`) and delegates CSV parsing to `QuadrantCsvReader.readSelection`. Any exception is logged and converted to null, which maps to `inHead = false`; this reads the checked-out working tree, not a JGit tree object.

---

## 11. `DiffCounts()`

```java
public record DiffCounts(int added, int changed, int removed, int entityChanges)
```

Value record used by both the API and internal logic to keep a diff's four counts together without retaining the full block/entity lists. The compact representation is what `SelectionStatus` stores and what `Report` aggregates.

---

## 12. `zero()`

```java
public static DiffCounts zero()
```

Factory for the neutral element used when a diff is not computed; it avoids null checks at the call sites and guarantees `clean()`/`total()` behave consistently.

---

## 13. `of()`

```java
public static DiffCounts of(BuildDiff diff)
```

Maps a `BuildDiff` to counters; the block lists contribute their sizes, and entity changes use `totalEntityChanges()` (additions plus removals) cast to int, matching the record's int contract.

---

## 14. `clean()`

```java
public boolean clean()
```

Returns true only when all four counters are zero; it is the primitive behind `SelectionStatus.clean()` and therefore also drives the report's clean/dirty counts.

---

## 15. `total()`

```java
public int total()
```

Sums the counters into one int; entity changes are already counted individually, so this is a magnitude rather than a unique-coordinate count.

---

## 16. `summary()`

```java
public String summary()
```

Builds the short chat form `+a ~c -r` and appends ` eN` only when `entityChanges > 0`, matching the wording used by the status renderer.

---

## 17. `SelectionStatus()`

```java
public record SelectionStatus(String name, boolean tracked, boolean inHead, boolean staged,
                                  int cuboidCount, long volume, boolean currentChecked, boolean tooLarge,
                                  DiffCounts stagedDiff, DiffCounts workingDiff, String note)
```

Composes selection metadata and the three-way comparison into one immutable row. `tracked` comes from the selection file, `inHead`/`staged` from the presence checks, `currentChecked` from `current != null && !incomplete`, and the two `DiffCounts` from the staged and working comparisons; `note` explains why a working diff was skipped. The helper predicates below derive the untracked/clean/dirty classification used by `Report`.

---

## 18. `clean()`

```java
public boolean clean()
```

Returns true when both diff summaries are clean and no note is present; an untracked selection with no data satisfies this too, so the report pairs it with `untracked()`.

---

## 19. `untracked()`

```java
public boolean untracked()
```

True when the selection is not tracked, not in HEAD and has no staged snapshot; these are rows the repository has never seen.

---

## 20. `dirty()`

```java
public boolean dirty()
```

True for any known selection that is not clean, i.e. it has staged or working changes or an explanatory note; untracked rows are excluded by design.

---

## 21. `Report()`

```java
public record Report(boolean repoInitialized, String branch, String head, List<SelectionStatus> selections)
```

Top-level immutable result of `report`: repository identity plus the sorted per-selection rows. The count helpers stream over `selections`; because `clean()` also returns true for untracked rows, `cleanCount` explicitly excludes them.

---

## 22. `dirtyCount()`

```java
public long dirtyCount()
```

Counts rows whose `dirty()` predicate is true; it streams the status list on each call, which is negligible for the number of selections.

---

## 23. `untrackedCount()`

```java
public long untrackedCount()
```

Counts rows whose `untracked()` predicate is true.

---

## 24. `cleanCount()`

```java
public long cleanCount()
```

Counts rows that are both not untracked and clean; this is the complement of dirty plus untracked and is used by the status header summary.

---

## 25. `DiffReport()`

```java
public record DiffReport(SelectionStatus status, BuildDiff stagedDiff, BuildDiff workingDiff,
                             Snapshot current)
```

Bundles the per-selection `SelectionStatus` with the actual `BuildDiff` objects and the current snapshot so `/git diff` can render detail without recomputing anything. `stagedDiff` is HEAD/empty vs staged and `workingDiff` is staged/HEAD/empty vs current; `current` may be null when no capture was taken.

---
