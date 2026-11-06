# StagingService

> **Software Detailed Documentation:** [StagingService_SDD.md](./StagingService_SDD.md)  
> **Source File:** [StagingService.java](../../../../../../../src/main/java/org/almond/minecraftgit/staging/StagingService.java)

## Table of Contents

- [Usage](#usage)
- [Fields](#fields)
- [StagingService (Constructor)](#stagingservice-constructor)
- [root](#root)
- [file](#file)
- [stage](#stage)
- [load](#load)
- [hasStaged](#hasstaged)
- [stagedNames](#stagednames)
- [unstage](#unstage)
- [rebaseStaged](#rebasestaged)
- [restrictStaged](#restrictstaged)
- [deleteAll](#deleteall)
- [store](#store)
- [See Also](#see-also)

## Usage

TODO: Add usage examples for `StagingService`.

Durable staging area. {@code /git add} writes a captured snapshot here;
{@code /git commit} reads it later. Files survive restarts and are keyed by
world id + player + selection, never by repository.

---

## Fields

- `root` (`Path`) — TODO: describe field
- `store` ([`SnapshotStore`](SnapshotStore.md)) — TODO: describe field
- `debug` ([`DebugLogger`](../debug/DebugLogger.md)) — TODO: describe field

---

## StagingService (Constructor)

### Signature
```java
public StagingService(Path root, SnapshotStore store, DebugLogger debug)
```

### Description
TODO: Describe what `StagingService` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `root` | `Path` | TODO: describe |
| `store` | [`SnapshotStore`](SnapshotStore.md) | TODO: describe |
| `debug` | [`DebugLogger`](../debug/DebugLogger.md) | TODO: describe |

### Returns
`StagingService` instance.

---

## root

### Signature
```java
public Path root()
```

### Description
TODO: Describe what `root` does.

### Parameters
None.

### Returns
`Path` — TODO: describe return value.

---

## file

### Signature
```java
public Path file(String worldId, UUID owner, String selection)
```

### Description
TODO: Describe what `file` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | TODO: describe |
| `owner` | `UUID` | TODO: describe |
| `selection` | `String` | TODO: describe |

### Returns
`Path` — TODO: describe return value.

---

## stage

### Signature
```java
public StagedSelection stage(Selection selection, Snapshot snapshot)
```

### Description
Write a capture to disk and return the staged handle once it is durable.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `selection` | [`Selection`](../model/Selection.md) | TODO: describe |
| `snapshot` | [`Snapshot`](../model/Snapshot.md) | TODO: describe |

### Returns
[`StagedSelection`](StagedSelection.md) — TODO: describe return value.

---

## load

### Signature
```java
public Optional<StagedSelection> load(String worldId, UUID owner, String selection)
```

### Description
TODO: Describe what `load` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | TODO: describe |
| `owner` | `UUID` | TODO: describe |
| `selection` | `String` | TODO: describe |

### Returns
`Optional<StagedSelection>` — TODO: describe return value.

---

## hasStaged

### Signature
```java
public boolean hasStaged(String worldId, UUID owner, String selection)
```

### Description
TODO: Describe what `hasStaged` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | TODO: describe |
| `owner` | `UUID` | TODO: describe |
| `selection` | `String` | TODO: describe |

### Returns
`boolean` — TODO: describe return value.

---

## stagedNames

### Signature
```java
public List<String> stagedNames(String worldId, UUID owner)
```

### Description
TODO: Describe what `stagedNames` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | TODO: describe |
| `owner` | `UUID` | TODO: describe |

### Returns
`List<String>` — TODO: describe return value.

---

## unstage

### Signature
```java
public void unstage(String worldId, UUID owner, String selection)
```

### Description
TODO: Describe what `unstage` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | TODO: describe |
| `owner` | `UUID` | TODO: describe |
| `selection` | `String` | TODO: describe |

### Returns
`void`

---

## rebaseStaged

### Signature
```java
public void rebaseStaged(String worldId, UUID owner, String selection, BlockPos newOrigin)
```

### Description
Rebase a staged snapshot after {@code /git selection reorigin}.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | TODO: describe |
| `owner` | `UUID` | TODO: describe |
| `selection` | `String` | TODO: describe |
| `newOrigin` | [`BlockPos`](../model/BlockPos.md) | TODO: describe |

### Returns
`void`

---

## restrictStaged

### Signature
```java
public void restrictStaged(String worldId, UUID owner, Selection selection)
```

### Description
Trim a staged snapshot after a cuboid is removed: coordinates outside the
remaining cuboids are untracked and must disappear from the next commit.
The origin itself does not move here (E2/E6).

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | TODO: describe |
| `owner` | `UUID` | TODO: describe |
| `selection` | [`Selection`](../model/Selection.md) | TODO: describe |

### Returns
`void`

---

## deleteAll

### Signature
```java
public void deleteAll(String worldId, UUID owner, String selection)
```

### Description
TODO: Describe what `deleteAll` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | TODO: describe |
| `owner` | `UUID` | TODO: describe |
| `selection` | `String` | TODO: describe |

### Returns
`void`

---

## store

### Signature
```java
public SnapshotStore store()
```

### Description
TODO: Describe what `store` does.

### Parameters
None.

### Returns
[`SnapshotStore`](SnapshotStore.md) — TODO: describe return value.

---

## See Also

- **Software Detailed Design:** [StagingService_SDD.md](./StagingService_SDD.md)
