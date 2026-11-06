# CommitService

> **Software Detailed Documentation:** [CommitService_SDD.md](./CommitService_SDD.md)  
> **Source File:** [CommitService.java](../../../../../../../src/main/java/org/almond/minecraftgit/commit/CommitService.java)

## Table of Contents

- [Usage](#usage)
- [Fields](#fields)
- [CommitService (Constructor)](#commitservice-constructor)
- [commit](#commit)
- [buildMeta](#buildmeta)
- [commitMessage](#commitmessage)
- [collectFiles](#collectfiles)
- [deleteRecursively](#deleterecursively)
- [See Also](#see-also)

## Usage

TODO: Add usage examples for `CommitService`.

Turns staged snapshots into repository commits: writes the CSV tree, writes
per-selection metadata, produces a readable commit message, and verifies that
something actually changed before touching Git.

---

## Fields

- `repos` ([`RepoManager`](../repo/RepoManager.md)) — TODO: describe field
- `staging` ([`StagingService`](../staging/StagingService.md)) — TODO: describe field
- `config` ([`ModConfig`](../config/ModConfig.md)) — TODO: describe field
- `debug` ([`DebugLogger`](../debug/DebugLogger.md)) — TODO: describe field
- `modVersion` (`String`) — TODO: describe field
- `commitLock` (`ReentrantLock`) — TODO: describe field

---

## CommitService (Constructor)

### Signature
```java
public CommitService(RepoManager repos, StagingService staging, ModConfig config,
                         DebugLogger debug, String modVersion)
```

### Description
TODO: Describe what `CommitService` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `repos` | [`RepoManager`](../repo/RepoManager.md) | TODO: describe |
| `staging` | [`StagingService`](../staging/StagingService.md) | TODO: describe |
| `config` | [`ModConfig`](../config/ModConfig.md) | TODO: describe |
| `debug` | [`DebugLogger`](../debug/DebugLogger.md) | TODO: describe |
| `modVersion` | `String` | TODO: describe |

### Returns
`CommitService` instance.

---

## commit

### Signature
```java
public CommitResult commit(CommitRequest request)
```

### Description
TODO: Describe what `commit` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `request` | [`CommitRequest`](CommitRequest.md) | TODO: describe |

### Returns
[`CommitResult`](CommitResult.md) — TODO: describe return value.

---

## buildMeta

### Signature
```java
private CommitMeta buildMeta(CommitRequest request, Selection selection, Snapshot snapshot,
                                 BlockPos previousOrigin)
```

### Description
TODO: Describe what `buildMeta` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `request` | [`CommitRequest`](CommitRequest.md) | TODO: describe |
| `selection` | [`Selection`](../model/Selection.md) | TODO: describe |
| `snapshot` | [`Snapshot`](../model/Snapshot.md) | TODO: describe |
| `previousOrigin` | [`BlockPos`](../model/BlockPos.md) | TODO: describe |

### Returns
[`CommitMeta`](../repo/CommitMeta.md) — TODO: describe return value.

---

## commitMessage

### Signature
```java
private String commitMessage(CommitRequest request, List<String> committedNames,
                                 long added, long changed, long removed, long entityChanges)
```

### Description
TODO: Describe what `commitMessage` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `request` | [`CommitRequest`](CommitRequest.md) | TODO: describe |
| `committedNames` | `List<String>` | TODO: describe |
| `added` | `long` | TODO: describe |
| `changed` | `long` | TODO: describe |
| `removed` | `long` | TODO: describe |
| `entityChanges` | `long` | TODO: describe |

### Returns
`String` — TODO: describe return value.

---

## collectFiles

### Signature
```java
private static void collectFiles(Path root, String selection, Map<String, byte[]> files)
```

### Description
TODO: Describe what `collectFiles` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `root` | `Path` | TODO: describe |
| `selection` | `String` | TODO: describe |
| `` | `Map<String` | TODO: describe |
| `files` | `byte[]>` | TODO: describe |

### Returns
`void`

---

## deleteRecursively

### Signature
```java
private static void deleteRecursively(Path path)
```

### Description
TODO: Describe what `deleteRecursively` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `path` | `Path` | TODO: describe |

### Returns
`void`

---

## See Also

- **Software Detailed Design:** [CommitService_SDD.md](./CommitService_SDD.md)
