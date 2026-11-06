# PullService

> **Software Detailed Documentation:** [PullService_SDD.md](./PullService_SDD.md)  
> **Source File:** [PullService.java](../../../../../../../src/main/java/org/almond/minecraftgit/sync/PullService.java)

## Table of Contents

- [Usage](#usage)
- [Fields](#fields)
- [PullService (Constructor)](#pullservice-constructor)
- [prepare](#prepare)
- [prepareCheckout](#preparecheckout)
- [cleanup](#cleanup)
- [shortId](#shortid)
- [PreparedPull](#preparedpull)
- [PreparedCheckout](#preparedcheckout)
- [See Also](#see-also)

## Usage

TODO: Add usage examples for `PullService`.

Reads remote commit trees and local HEAD state into snapshots that the apply
service can turn into world writes. All filesystem/repository work here runs
off the client thread; only selection metadata and world writes happen later.

---

## Fields

- `tmpRoot` (`Path`) — TODO: describe field

---

## PullService (Constructor)

### Signature
```java
public PullService(Path tmpRoot)
```

### Description
TODO: Describe what `PullService` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `tmpRoot` | `Path` | TODO: describe |

### Returns
`PullService` instance.

---

## prepare

### Signature
```java
public List<PreparedPull> prepare(String worldId, List<Selection> localSelections, BlockPos playerOrigin,
                                      RepoService repo, PullPlan plan, String onlySelection)
```

### Description
TODO: Describe what `prepare` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | TODO: describe |
| `localSelections` | `List<Selection>` | TODO: describe |
| `playerOrigin` | [`BlockPos`](../model/BlockPos.md) | TODO: describe |
| `repo` | [`RepoService`](../repo/RepoService.md) | TODO: describe |
| `plan` | [`PullPlan`](../repo/PullPlan.md) | TODO: describe |
| `onlySelection` | `String` | TODO: describe |

### Returns
`List<PreparedPull>` — TODO: describe return value.

---

## prepareCheckout

### Signature
```java
public PreparedCheckout prepareCheckout(Selection selection, RepoService repo, ObjectId commit)
```

### Description
TODO: Describe what `prepareCheckout` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `selection` | [`Selection`](../model/Selection.md) | TODO: describe |
| `repo` | [`RepoService`](../repo/RepoService.md) | TODO: describe |
| `commit` | `ObjectId` | TODO: describe |

### Returns
`PreparedCheckout` — TODO: describe return value.

---

## cleanup

### Signature
```java
public void cleanup(Path root)
```

### Description
TODO: Describe what `cleanup` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `root` | `Path` | TODO: describe |

### Returns
`void`

---

## cleanup

### Signature
```java
public void cleanup(List<PreparedPull> prepared)
```

### Description
TODO: Describe what `cleanup` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `prepared` | `List<PreparedPull>` | TODO: describe |

### Returns
`void`

---

## shortId

### Signature
```java
private static String shortId(ObjectId id)
```

### Description
TODO: Describe what `shortId` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `id` | `ObjectId` | TODO: describe |

### Returns
`String` — TODO: describe return value.

---

## PreparedPull

### Signature
```java
public record PreparedPull(String name, Snapshot target, CommitMeta meta, Selection localSelection,
                               BlockPos localOrigin, Snapshot localHead, BlockPos localHeadOrigin,
                               Path tempDirectory)
```

### Description
TODO: Describe what `PreparedPull` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `name` | `String` | TODO: describe |
| `target` | [`Snapshot`](../model/Snapshot.md) | TODO: describe |
| `meta` | [`CommitMeta`](../repo/CommitMeta.md) | TODO: describe |
| `localSelection` | [`Selection`](../model/Selection.md) | TODO: describe |
| `localOrigin` | [`BlockPos`](../model/BlockPos.md) | TODO: describe |
| `localHead` | [`Snapshot`](../model/Snapshot.md) | TODO: describe |
| `localHeadOrigin` | [`BlockPos`](../model/BlockPos.md) | TODO: describe |
| `tempDirectory` | `Path` | TODO: describe |

### Returns
`record` — TODO: describe return value.

---

## PreparedCheckout

### Signature
```java
public record PreparedCheckout(Snapshot target, CommitMeta meta, Snapshot localHead,
                                   BlockPos localHeadOrigin, Path tempDirectory)
```

### Description
TODO: Describe what `PreparedCheckout` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `target` | [`Snapshot`](../model/Snapshot.md) | TODO: describe |
| `meta` | [`CommitMeta`](../repo/CommitMeta.md) | TODO: describe |
| `localHead` | [`Snapshot`](../model/Snapshot.md) | TODO: describe |
| `localHeadOrigin` | [`BlockPos`](../model/BlockPos.md) | TODO: describe |
| `tempDirectory` | `Path` | TODO: describe |

### Returns
`record` — TODO: describe return value.

---

## See Also

- **Software Detailed Design:** [PullService_SDD.md](./PullService_SDD.md)
