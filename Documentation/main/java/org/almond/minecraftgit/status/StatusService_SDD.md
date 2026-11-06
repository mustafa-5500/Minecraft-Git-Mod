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

TODO: Describe how `StatusService` is instantiated and used.

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
| [`DebugLogger`](../debug/DebugLogger.md) | TODO: describe purpose |
| [`BlockPos`](../model/BlockPos.md) | TODO: describe purpose |
| [`BuildDiff`](../model/BuildDiff.md) | TODO: describe purpose |
| [`Selection`](../model/Selection.md) | TODO: describe purpose |
| [`Snapshot`](../model/Snapshot.md) | TODO: describe purpose |
| [`CommitMeta`](../repo/CommitMeta.md) | TODO: describe purpose |
| [`RepoManager`](../repo/RepoManager.md) | TODO: describe purpose |
| [`RepoService`](../repo/RepoService.md) | TODO: describe purpose |
| [`QuadrantCsvReader`](../serialization/QuadrantCsvReader.md) | TODO: describe purpose |
| [`StagedSelection`](../staging/StagedSelection.md) | TODO: describe purpose |
| [`StagingService`](../staging/StagingService.md) | TODO: describe purpose |
| `Files` | TODO: describe purpose |
| `Path` | TODO: describe purpose |
| `ArrayList` | TODO: describe purpose |
| `Comparator` | TODO: describe purpose |
| `List` | TODO: describe purpose |
| `Map` | TODO: describe purpose |
| `Optional` | TODO: describe purpose |
| `Set` | TODO: describe purpose |
| `UUID` | TODO: describe purpose |

---

## 3. Class Declaration

```java
public final class StatusService {
```


---

## 4. Instance Fields

```java
private final RepoManager repos;
private final StagingService staging;
private final DebugLogger debug;
```

| Field | Type | Description |
|-------|------|-------------|
| `repos` | [`RepoManager`](../repo/RepoManager.md) | TODO: describe |
| `staging` | [`StagingService`](../staging/StagingService.md) | TODO: describe |
| `debug` | [`DebugLogger`](../debug/DebugLogger.md) | TODO: describe |

---

## 5. Constructor

```java
public StatusService(RepoManager repos, StagingService staging, DebugLogger debug)
```

TODO: Provide detailed design explanation for `StatusService`.

---

## 6. `report()`

```java
public Report report(String worldId, UUID owner, List<Selection> selections,
                         Map<String, Snapshot> current, Set<String> tooLarge, Set<String> incomplete)
```

TODO: Provide detailed design explanation for `report`.

---

## 7. `diff()`

```java
public DiffReport diff(String worldId, UUID owner, Selection selection, Snapshot current,
                           boolean tooLarge, boolean incomplete)
```

TODO: Provide detailed design explanation for `diff`.

---

## 8. `statusOf()`

```java
private SelectionStatus statusOf(boolean initialized, RepoService repo, Selection selection,
                                     Snapshot current, boolean tooLarge, boolean incomplete)
```

TODO: Provide detailed design explanation for `statusOf`.

---

## 9. `loadStaged()`

```java
private Optional<StagedSelection> loadStaged(String worldId, UUID owner, Selection selection)
```

TODO: Provide detailed design explanation for `loadStaged`.

---

## 10. `readHead()`

```java
private Snapshot readHead(RepoService repo, Selection selection)
```

TODO: Provide detailed design explanation for `readHead`.

---

## 11. `DiffCounts()`

```java
public record DiffCounts(int added, int changed, int removed, int entityChanges)
```

TODO: Provide detailed design explanation for `DiffCounts`.

---

## 12. `zero()`

```java
public static DiffCounts zero()
```

TODO: Provide detailed design explanation for `zero`.

---

## 13. `of()`

```java
public static DiffCounts of(BuildDiff diff)
```

TODO: Provide detailed design explanation for `of`.

---

## 14. `clean()`

```java
public boolean clean()
```

TODO: Provide detailed design explanation for `clean`.

---

## 15. `total()`

```java
public int total()
```

TODO: Provide detailed design explanation for `total`.

---

## 16. `summary()`

```java
public String summary()
```

TODO: Provide detailed design explanation for `summary`.

---

## 17. `SelectionStatus()`

```java
public record SelectionStatus(String name, boolean tracked, boolean inHead, boolean staged,
                                  int cuboidCount, long volume, boolean currentChecked, boolean tooLarge,
                                  DiffCounts stagedDiff, DiffCounts workingDiff, String note)
```

TODO: Provide detailed design explanation for `SelectionStatus`.

---

## 18. `clean()`

```java
public boolean clean()
```

TODO: Provide detailed design explanation for `clean`.

---

## 19. `untracked()`

```java
public boolean untracked()
```

TODO: Provide detailed design explanation for `untracked`.

---

## 20. `dirty()`

```java
public boolean dirty()
```

TODO: Provide detailed design explanation for `dirty`.

---

## 21. `Report()`

```java
public record Report(boolean repoInitialized, String branch, String head, List<SelectionStatus> selections)
```

TODO: Provide detailed design explanation for `Report`.

---

## 22. `dirtyCount()`

```java
public long dirtyCount()
```

TODO: Provide detailed design explanation for `dirtyCount`.

---

## 23. `untrackedCount()`

```java
public long untrackedCount()
```

TODO: Provide detailed design explanation for `untrackedCount`.

---

## 24. `cleanCount()`

```java
public long cleanCount()
```

TODO: Provide detailed design explanation for `cleanCount`.

---

## 25. `DiffReport()`

```java
public record DiffReport(SelectionStatus status, BuildDiff stagedDiff, BuildDiff workingDiff,
                             Snapshot current)
```

TODO: Provide detailed design explanation for `DiffReport`.

---
