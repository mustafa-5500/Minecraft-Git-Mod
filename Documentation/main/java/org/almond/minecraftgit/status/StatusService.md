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

TODO: Add usage examples for `StatusService`.

Computes status and diffs by comparing three snapshots per selection: HEAD
(committed), staged (local index), and a current capture (the world).

---

## Fields

- `repos` ([`RepoManager`](../repo/RepoManager.md)) — TODO: describe field
- `staging` ([`StagingService`](../staging/StagingService.md)) — TODO: describe field
- `debug` ([`DebugLogger`](../debug/DebugLogger.md)) — TODO: describe field

---

## StatusService (Constructor)

### Signature
```java
public StatusService(RepoManager repos, StagingService staging, DebugLogger debug)
```

### Description
TODO: Describe what `StatusService` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `repos` | [`RepoManager`](../repo/RepoManager.md) | TODO: describe |
| `staging` | [`StagingService`](../staging/StagingService.md) | TODO: describe |
| `debug` | [`DebugLogger`](../debug/DebugLogger.md) | TODO: describe |

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
TODO: Describe what `report` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | TODO: describe |
| `owner` | `UUID` | TODO: describe |
| `selections` | `List<Selection>` | TODO: describe |
| `` | `Map<String` | TODO: describe |
| `current` | `Snapshot>` | TODO: describe |
| `tooLarge` | `Set<String>` | TODO: describe |
| `incomplete` | `Set<String>` | TODO: describe |

### Returns
`Report` — TODO: describe return value.

---

## diff

### Signature
```java
public DiffReport diff(String worldId, UUID owner, Selection selection, Snapshot current,
                           boolean tooLarge, boolean incomplete)
```

### Description
TODO: Describe what `diff` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | TODO: describe |
| `owner` | `UUID` | TODO: describe |
| `selection` | [`Selection`](../model/Selection.md) | TODO: describe |
| `current` | [`Snapshot`](../model/Snapshot.md) | TODO: describe |
| `tooLarge` | `boolean` | TODO: describe |
| `incomplete` | `boolean` | TODO: describe |

### Returns
`DiffReport` — TODO: describe return value.

---

## statusOf

### Signature
```java
private SelectionStatus statusOf(boolean initialized, RepoService repo, Selection selection,
                                     Snapshot current, boolean tooLarge, boolean incomplete)
```

### Description
TODO: Describe what `statusOf` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `initialized` | `boolean` | TODO: describe |
| `repo` | [`RepoService`](../repo/RepoService.md) | TODO: describe |
| `selection` | [`Selection`](../model/Selection.md) | TODO: describe |
| `current` | [`Snapshot`](../model/Snapshot.md) | TODO: describe |
| `tooLarge` | `boolean` | TODO: describe |
| `incomplete` | `boolean` | TODO: describe |

### Returns
`SelectionStatus` — TODO: describe return value.

---

## loadStaged

### Signature
```java
private Optional<StagedSelection> loadStaged(String worldId, UUID owner, Selection selection)
```

### Description
TODO: Describe what `loadStaged` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | TODO: describe |
| `owner` | `UUID` | TODO: describe |
| `selection` | [`Selection`](../model/Selection.md) | TODO: describe |

### Returns
`Optional<StagedSelection>` — TODO: describe return value.

---

## readHead

### Signature
```java
private Snapshot readHead(RepoService repo, Selection selection)
```

### Description
TODO: Describe what `readHead` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `repo` | [`RepoService`](../repo/RepoService.md) | TODO: describe |
| `selection` | [`Selection`](../model/Selection.md) | TODO: describe |

### Returns
[`Snapshot`](../model/Snapshot.md) — TODO: describe return value.

---

## DiffCounts

### Signature
```java
public record DiffCounts(int added, int changed, int removed, int entityChanges)
```

### Description
TODO: Describe what `DiffCounts` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `added` | `int` | TODO: describe |
| `changed` | `int` | TODO: describe |
| `removed` | `int` | TODO: describe |
| `entityChanges` | `int` | TODO: describe |

### Returns
`record` — TODO: describe return value.

---

## zero

### Signature
```java
public static DiffCounts zero()
```

### Description
TODO: Describe what `zero` does.

### Parameters
None.

### Returns
`DiffCounts` — TODO: describe return value.

---

## of

### Signature
```java
public static DiffCounts of(BuildDiff diff)
```

### Description
TODO: Describe what `of` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `diff` | [`BuildDiff`](../model/BuildDiff.md) | TODO: describe |

### Returns
`DiffCounts` — TODO: describe return value.

---

## clean

### Signature
```java
public boolean clean()
```

### Description
TODO: Describe what `clean` does.

### Parameters
None.

### Returns
`boolean` — TODO: describe return value.

---

## total

### Signature
```java
public int total()
```

### Description
TODO: Describe what `total` does.

### Parameters
None.

### Returns
`int` — TODO: describe return value.

---

## summary

### Signature
```java
public String summary()
```

### Description
TODO: Describe what `summary` does.

### Parameters
None.

### Returns
`String` — TODO: describe return value.

---

## SelectionStatus

### Signature
```java
public record SelectionStatus(String name, boolean tracked, boolean inHead, boolean staged,
                                  int cuboidCount, long volume, boolean currentChecked, boolean tooLarge,
                                  DiffCounts stagedDiff, DiffCounts workingDiff, String note)
```

### Description
TODO: Describe what `SelectionStatus` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `name` | `String` | TODO: describe |
| `tracked` | `boolean` | TODO: describe |
| `inHead` | `boolean` | TODO: describe |
| `staged` | `boolean` | TODO: describe |
| `cuboidCount` | `int` | TODO: describe |
| `volume` | `long` | TODO: describe |
| `currentChecked` | `boolean` | TODO: describe |
| `tooLarge` | `boolean` | TODO: describe |
| `stagedDiff` | `DiffCounts` | TODO: describe |
| `workingDiff` | `DiffCounts` | TODO: describe |
| `note` | `String` | TODO: describe |

### Returns
`record` — TODO: describe return value.

---

## clean

### Signature
```java
public boolean clean()
```

### Description
TODO: Describe what `clean` does.

### Parameters
None.

### Returns
`boolean` — TODO: describe return value.

---

## untracked

### Signature
```java
public boolean untracked()
```

### Description
TODO: Describe what `untracked` does.

### Parameters
None.

### Returns
`boolean` — TODO: describe return value.

---

## dirty

### Signature
```java
public boolean dirty()
```

### Description
TODO: Describe what `dirty` does.

### Parameters
None.

### Returns
`boolean` — TODO: describe return value.

---

## Report

### Signature
```java
public record Report(boolean repoInitialized, String branch, String head, List<SelectionStatus> selections)
```

### Description
TODO: Describe what `Report` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `repoInitialized` | `boolean` | TODO: describe |
| `branch` | `String` | TODO: describe |
| `head` | `String` | TODO: describe |
| `selections` | `List<SelectionStatus>` | TODO: describe |

### Returns
`record` — TODO: describe return value.

---

## dirtyCount

### Signature
```java
public long dirtyCount()
```

### Description
TODO: Describe what `dirtyCount` does.

### Parameters
None.

### Returns
`long` — TODO: describe return value.

---

## untrackedCount

### Signature
```java
public long untrackedCount()
```

### Description
TODO: Describe what `untrackedCount` does.

### Parameters
None.

### Returns
`long` — TODO: describe return value.

---

## cleanCount

### Signature
```java
public long cleanCount()
```

### Description
TODO: Describe what `cleanCount` does.

### Parameters
None.

### Returns
`long` — TODO: describe return value.

---

## DiffReport

### Signature
```java
public record DiffReport(SelectionStatus status, BuildDiff stagedDiff, BuildDiff workingDiff,
                             Snapshot current)
```

### Description
TODO: Describe what `DiffReport` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `status` | `SelectionStatus` | TODO: describe |
| `stagedDiff` | [`BuildDiff`](../model/BuildDiff.md) | TODO: describe |
| `workingDiff` | [`BuildDiff`](../model/BuildDiff.md) | TODO: describe |
| `current` | [`Snapshot`](../model/Snapshot.md) | TODO: describe |

### Returns
`record` — TODO: describe return value.

---

## See Also

- **Software Detailed Design:** [StatusService_SDD.md](./StatusService_SDD.md)
