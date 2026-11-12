# StatusService

> **Software Detailed Documentation:** [StatusService_SDD.md](./StatusService_SDD.md)  
> **Source File:** [StatusService.java](../../../../../../../src/main/java/org/almond/minecraftgit/status/StatusService.java)

## Table of Contents

- [Usage](#usage)
- [Fields](#fields)
- [StatusService (Constructor)](#statusservice-constructor)
- [report](#report)
- [diff](#diff)
- [statusOf](#statusof)
- [loadStaged](#loadstaged)
- [readHead](#readhead)
- [DiffCounts](#diffcounts)
- [zero](#zero)
- [of](#of)
- [clean](#clean)
- [total](#total)
- [summary](#summary)
- [SelectionStatus](#selectionstatus)
- [untracked](#untracked)
- [dirty](#dirty)
- [dirtyCount](#dirtycount)
- [untrackedCount](#untrackedcount)
- [cleanCount](#cleancount)
- [DiffReport](#diffreport)
- [See Also](#see-also)

## Usage

Construct one instance with the repository manager, staging service and logger, then call `report` for `/git status` (passing a freshly captured world snapshot per selection) and `diff` for `/git diff`. For example, `status.report(worldId, owner, selections, captures, tooLarge, incomplete)` returns a `Report` whose per-selection records distinguish untracked, staged and working-tree changes.

Computes status and diffs by comparing three snapshots per selection: HEAD
(committed), staged (local index), and a current capture (the world).

---

## Fields

- `repos` ([`RepoManager`](../repo/RepoManager.md)) — Resolves the per-world `RepoService` used to read the branch, HEAD id and HEAD selection data.
- `staging` ([`StagingService`](../staging/StagingService.md)) — Reads the staged snapshot for each selection.
- `debug` ([`DebugLogger`](../debug/DebugLogger.md)) — Logs repository and staged-snapshot read failures that status degrades past.

---

## StatusService (Constructor)

### Signature
```java
public StatusService(RepoManager repos, StagingService staging, DebugLogger debug)
```

### Description
Stores the three collaborators; it does not open a repository or perform I/O, because repository access is resolved per world id on each call.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `repos` | [`RepoManager`](../repo/RepoManager.md) | Resolves the repository belonging to the world. |
| `staging` | [`StagingService`](../staging/StagingService.md) | Staging area queried for the local index snapshot. |
| `debug` | [`DebugLogger`](../debug/DebugLogger.md) | Logger for non-fatal status read failures. |

### Returns
`StatusService` instance.

---

## report

### Signature
```java
public Report report(String worldId, UUID owner, List<Selection> selections,
                         Map<String, Snapshot> current, Set<String> tooLarge, Set<String> incomplete)
```

### Description
Builds a whole-world status report: it resolves the world's repository, reads the current branch and short HEAD id when the repository is initialized, and creates a `SelectionStatus` for every supplied selection using the matching entry in `current`, `tooLarge` and `incomplete`. Statuses are sorted case-insensitively by name; a repository read failure is logged and leaves branch/HEAD empty rather than failing the report, and the method never captures the world itself.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | World whose repository supplies branch and HEAD information. |
| `owner` | `UUID` | Player UUID used when loading staged snapshots. |
| `selections` | `List<Selection>` | Selections to report on; the result is sorted by name. |
| `current` | `Map<String, Snapshot>` | World captures keyed by selection name; a missing key means no fresh capture for that selection. |
| `tooLarge` | `Set<String>` | Selection names whose volume exceeded the capture limit; their working diff is omitted with a note. |
| `incomplete` | `Set<String>` | Selection names whose capture skipped unloaded chunks; their working diff is omitted. |

### Returns
`Report` — Repository identity plus one `SelectionStatus` per requested selection, sorted by name.

---

## diff

### Signature
```java
public DiffReport diff(String worldId, UUID owner, Selection selection, Snapshot current,
                           boolean tooLarge, boolean incomplete)
```

### Description
Builds a detailed diff for one selection: it reads HEAD (when the repository is initialized) and the staged snapshot, chooses staged else HEAD else an empty snapshot as the working base, and computes `stagedDiff` (HEAD/empty to staged) and `workingDiff` (base to current). `workingDiff` is an empty diff when `current` is null or `incomplete`; the returned `DiffReport` also contains the computed `SelectionStatus` and the supplied current snapshot. The method is read-only and logs rather than throws when HEAD or staged data cannot be read.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | World whose repository and HEAD are read. |
| `owner` | `UUID` | Player UUID used when loading the staged snapshot. |
| `selection` | [`Selection`](../model/Selection.md) | Selection being diffed; supplies its name, origin and current cuboids. |
| `current` | [`Snapshot`](../model/Snapshot.md) | Fresh world capture to compare against the staged/HEAD base; may be null. |
| `tooLarge` | `boolean` | Whether the capture was skipped for exceeding the volume limit. |
| `incomplete` | `boolean` | Whether the capture skipped unloaded chunks and must not be treated as complete. |

### Returns
`DiffReport` — Per-selection status, staged diff, working diff and the current snapshot used.

---

## statusOf

### Signature
```java
private SelectionStatus statusOf(boolean initialized, RepoService repo, Selection selection,
                                     Snapshot current, boolean tooLarge, boolean incomplete)
```

### Description
Computes one `SelectionStatus`: it loads the staged snapshot and HEAD, derives `stagedDiff` from HEAD (or an empty snapshot) to staged and `workingDiff` from staged/HEAD/empty to the current capture, and records the tracked/inHead/staged flags plus cuboid count and selection volume. When no usable capture exists it leaves the working counts at zero and sets a `note` explaining why (volume limit, unloaded chunks, or missing capture); `currentChecked` is true only when `current` is non-null and the capture is complete.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `initialized` | `boolean` | Whether the repository has a `.git` directory and HEAD can be read. |
| `repo` | [`RepoService`](../repo/RepoService.md) | Repository service for the selection's world. |
| `selection` | [`Selection`](../model/Selection.md) | Selection whose metadata and staged/HEAD state are reported. |
| `current` | [`Snapshot`](../model/Snapshot.md) | Fresh capture for the selection, or null when none was taken. |
| `tooLarge` | `boolean` | Whether the capture was skipped for size. |
| `incomplete` | `boolean` | Whether the capture skipped unloaded chunks. |

### Returns
`SelectionStatus` — Computed per-selection status record.

---

## loadStaged

### Signature
```java
private Optional<StagedSelection> loadStaged(String worldId, UUID owner, Selection selection)
```

### Description
Loads the staged snapshot through `StagingService.load`, converting any `RuntimeException` (for example a corrupt `.snap`) into `Optional.empty()` and logging the reason, so status output degrades instead of failing.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | World identifier used to build the staging path. |
| `owner` | `UUID` | Owner UUID used to build the staging path. |
| `selection` | [`Selection`](../model/Selection.md) | Selection whose staged snapshot is loaded. |

### Returns
`Optional<StagedSelection>` — The staged selection when readable, otherwise empty.

---

## readHead

### Signature
```java
private Snapshot readHead(RepoService repo, Selection selection)
```

### Description
Reads the committed selection directly from the repository's working tree under `<repo>/<selection>`: it takes the origin from `meta.json` (falling back to the selection origin or `BlockPos.ORIGIN`) and decodes the CSVs with `QuadrantCsvReader`. It returns null when the directory does not exist or parsing fails, logging the failure; status treats null as "not in HEAD".

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `repo` | [`RepoService`](../repo/RepoService.md) | Repository whose checked-out selection directory is read. |
| `selection` | [`Selection`](../model/Selection.md) | Selection name and origin used to locate and decode the committed snapshot. |

### Returns
[`Snapshot`](../model/Snapshot.md) — HEAD snapshot, or null when it is absent or unreadable.

---

## DiffCounts

### Signature
```java
public record DiffCounts(int added, int changed, int removed, int entityChanges)
```

### Description
Immutable count summary of a [`BuildDiff`](../model/BuildDiff.md): added, changed and removed blocks plus added and removed entities. It is the compact form embedded in `SelectionStatus` for display and clean/dirty checks.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `added` | `int` | Number of block coordinates present after but not before. |
| `changed` | `int` | Number of coordinates whose block data or block entity differs. |
| `removed` | `int` | Number of block coordinates present before but not after. |
| `entityChanges` | `int` | Total entity additions and removals. |

### Returns
`record` — Immutable value object holding the four counts.

---

## zero

### Signature
```java
public static DiffCounts zero()
```

### Description
Returns a `DiffCounts` with all four counters at zero, used when a diff was not computed.

### Parameters
None.

### Returns
`DiffCounts` — All-zero counts.

---

## of

### Signature
```java
public static DiffCounts of(BuildDiff diff)
```

### Description
Converts a `BuildDiff` into counts by taking the added/changed/removed list sizes and the total entity changes; entity additions and removals are summed.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `diff` | [`BuildDiff`](../model/BuildDiff.md) | Build diff to summarise. |

### Returns
`DiffCounts` — Counts derived from the diff.

---

## clean

### Signature
```java
public boolean clean()
```

### Description
Returns true when no blocks were added, changed or removed and no entity change occurred.

### Parameters
None.

### Returns
`boolean` — `true` for an empty diff.

---

## total

### Signature
```java
public int total()
```

### Description
Returns the sum of all four counters, giving a single magnitude for the diff (entities count individually).

### Parameters
None.

### Returns
`int` — `added + changed + removed + entityChanges`.

---

## summary

### Signature
```java
public String summary()
```

### Description
Formats the counts as a compact human-readable string, for example `+3 ~1 -2` plus ` e4` when entities changed; the entity suffix is omitted when zero.

### Parameters
None.

### Returns
`String` — Compact `+a ~c -r` summary, with ` eN` appended when entities changed.

---

## SelectionStatus

### Signature
```java
public record SelectionStatus(String name, boolean tracked, boolean inHead, boolean staged,
                                  int cuboidCount, long volume, boolean currentChecked, boolean tooLarge,
                                  DiffCounts stagedDiff, DiffCounts workingDiff, String note)
```

### Description
Per-selection status record combining selection metadata (`name`, `tracked`, `cuboidCount`, `volume`) with the three-way state: `inHead`, `staged`, the staged diff, the working diff, capture flags and an optional explanatory `note`.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `name` | `String` | Selection name. |
| `tracked` | `boolean` | Whether the selection is marked tracked on disk (`Selection.isTracked`). |
| `inHead` | `boolean` | Whether a committed snapshot exists for it in HEAD. |
| `staged` | `boolean` | Whether a staged snapshot currently exists. |
| `cuboidCount` | `int` | Number of cuboids in the selection. |
| `volume` | `long` | Saturated sum of cuboid volumes; overlaps are counted twice. |
| `currentChecked` | `boolean` | True when a complete current capture was available (`current != null && !incomplete`). |
| `tooLarge` | `boolean` | True when the capture was skipped because the volume exceeded the limit. |
| `stagedDiff` | `DiffCounts` | HEAD (or empty) to staged counts. |
| `workingDiff` | `DiffCounts` | Staged/HEAD/empty base to current counts; zero when the capture was omitted. |
| `note` | `String` | Explanation when the working diff is unavailable, otherwise null. |

### Returns
`record` — Immutable per-selection status snapshot.

---

## clean

### Signature
```java
public boolean clean()
```

### Description
True when both the staged and working diffs are empty and there is no note; an untracked selection with no data also satisfies this, so callers combine it with `untracked()`.

### Parameters
None.

### Returns
`boolean` — `true` when there is nothing to commit for this selection.

---

## untracked

### Signature
```java
public boolean untracked()
```

### Description
True when the selection is not tracked, not present in HEAD and has no staged snapshot, i.e. the repository does not know about it yet.

### Parameters
None.

### Returns
`boolean` — `true` for a selection unknown to the repository.

---

## dirty

### Signature
```java
public boolean dirty()
```

### Description
True when the selection is known to the repository and not clean, i.e. it has staged or working changes or a note; untracked selections are excluded.

### Parameters
None.

### Returns
`boolean` — `true` when tracked or committed/staged data has changes.

---

## Report

### Signature
```java
public record Report(boolean repoInitialized, String branch, String head, List<SelectionStatus> selections)
```

### Description
Whole-world status report: repository initialization, branch and HEAD id plus the sorted per-selection statuses and aggregate counters.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `repoInitialized` | `boolean` | Whether the world's repository exists and is initialized. |
| `branch` | `String` | Current branch name, or empty when unavailable. |
| `head` | `String` | Short (8-character) HEAD commit id, or empty when unavailable. |
| `selections` | `List<SelectionStatus>` | Statuses sorted case-insensitively by selection name. |

### Returns
`record` — Immutable report object.

---

## dirtyCount

### Signature
```java
public long dirtyCount()
```

### Description
Counts selections where `dirty()` is true, i.e. known to the repository but not clean.

### Parameters
None.

### Returns
`long` — Number of dirty selections.

---

## untrackedCount

### Signature
```java
public long untrackedCount()
```

### Description
Counts selections where `untracked()` is true.

### Parameters
None.

### Returns
`long` — Number of untracked selections.

---

## cleanCount

### Signature
```java
public long cleanCount()
```

### Description
Counts selections that are not untracked and are clean, i.e. known to the repository with no staged or working changes and no note.

### Parameters
None.

### Returns
`long` — Number of clean, non-untracked selections.

---

## DiffReport

### Signature
```java
public record DiffReport(SelectionStatus status, BuildDiff stagedDiff, BuildDiff workingDiff,
                             Snapshot current)
```

### Description
Detailed diff payload for one selection: its `SelectionStatus`, the staged and working `BuildDiff`s, and the current capture used for the comparison.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `status` | `SelectionStatus` | Per-selection status derived alongside the diffs. |
| `stagedDiff` | [`BuildDiff`](../model/BuildDiff.md) | HEAD/empty to staged block and entity differences. |
| `workingDiff` | [`BuildDiff`](../model/BuildDiff.md) | Staged/HEAD/empty base to current differences; empty when no complete capture exists. |
| `current` | [`Snapshot`](../model/Snapshot.md) | Current snapshot supplied for the comparison, possibly null. |

### Returns
`record` — Immutable diff report.

---

## See Also

- **Software Detailed Design:** [StatusService_SDD.md](./StatusService_SDD.md)
