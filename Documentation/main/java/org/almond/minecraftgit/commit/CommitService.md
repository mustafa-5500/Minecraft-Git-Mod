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

Instantiate it once in the client service graph (`GitServices`) and call `commit` with a request assembled on the client thread; for example, `/git commit` collects the player's staged selections into a `CommitRequest` and invokes `services.commits().commit(request)` on the IO executor. The call performs blocking file and JGit work, so it must not run on the render thread.

Turns staged snapshots into repository commits: writes the CSV tree, writes
per-selection metadata, produces a readable commit message, and verifies that
something actually changed before touching Git.

---

## Fields

- `repos` ([`RepoManager`](../repo/RepoManager.md)) — Resolves the per-world `RepoService` that owns the on-disk Git repository.
- `staging` ([`StagingService`](../staging/StagingService.md)) — Loads and deletes staged snapshots keyed by world id, player UUID and selection name.
- `config` ([`ModConfig`](../config/ModConfig.md)) — Supplies the temporary directory (`tmpRoot()`) used to assemble commit trees.
- `debug` ([`DebugLogger`](../debug/DebugLogger.md)) — Receives the diagnostic summary line for successful commits.
- `modVersion` (`String`) — Friendly mod version recorded in each `meta.json` as `pluginVersion`.
- `commitLock` (`ReentrantLock`) — Reentrant lock that serializes commit operations so two commits cannot interleave.

---

## CommitService (Constructor)

### Signature
```java
public CommitService(RepoManager repos, StagingService staging, ModConfig config,
                         DebugLogger debug, String modVersion)
```

### Description
Creates a commit service bound to the supplied repository manager, staging service, config, logger and mod version; all dependencies are retained for the service lifetime and no IO happens at construction.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `repos` | [`RepoManager`](../repo/RepoManager.md) | Per-world repository manager used to resolve and commit to the Git repository. |
| `staging` | [`StagingService`](../staging/StagingService.md) | Durable staging store holding snapshots produced by `/git add`. |
| `config` | [`ModConfig`](../config/ModConfig.md) | Mod config supplying `tmpRoot()` for temporary commit trees. |
| `debug` | [`DebugLogger`](../debug/DebugLogger.md) | Debug logger for commit summaries. |
| `modVersion` | `String` | Mod version string persisted in generated commit metadata. |

### Returns
`CommitService` instance.

---

## commit

### Signature
```java
public CommitResult commit(CommitRequest request)
```

### Description
Validates that the world repository is initialized and that something is staged, then for each selection loads its staged snapshot, reads the previous HEAD tree into an old `Snapshot`, diffs the two and, when changed or re-origined, writes the new CSV/meta tree into the repository. Unchanged selections are skipped; after `RepoService.commitWorkspace` succeeds the staged snapshots are deleted. Throws `CommitException` for a missing repository or staging entry, nothing to commit, an unchanged repository, or IO failure while building the tree, and the whole method is serialized by `commitLock`.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `request` | [`CommitRequest`](CommitRequest.md) | Commit request carrying player/world identity, the selections to commit, an optional message and the `allowEmpty` flag. |

### Returns
[`CommitResult`](CommitResult.md) — Identifier, short id, committed selection names, added/changed/removed block counts, entity change count and the generated message of the new commit.

---

## buildMeta

### Signature
```java
private CommitMeta buildMeta(CommitRequest request, Selection selection, Snapshot snapshot,
                                 BlockPos previousOrigin)
```

### Description
Builds the `CommitMeta` committed beside one selection: cuboids rebased onto the snapshot origin, dimensions and counts, current and previous origin, and whether the origin moved.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `request` | [`CommitRequest`](CommitRequest.md) | Commit request supplying world, player identity and game mode. |
| `selection` | [`Selection`](../model/Selection.md) | Selection whose cuboids, bounds and cuboid count describe the build. |
| `snapshot` | [`Snapshot`](../model/Snapshot.md) | Staged snapshot supplying the origin, block/entity counts and fallback dimensions. |
| `previousOrigin` | [`BlockPos`](../model/BlockPos.md) | Origin read from the old committed metadata, or `null` when the selection did not exist before. |

### Returns
[`CommitMeta`](../repo/CommitMeta.md) — Metadata record written as `meta.json`, with cuboids relative to the snapshot origin and `reorigined` set when the origin moved.

---

## commitMessage

### Signature
```java
private String commitMessage(CommitRequest request, List<String> committedNames,
                                 long added, long changed, long removed, long entityChanges)
```

### Description
Builds the commit message: the trimmed user-supplied message when present, otherwise a generated header naming the player and selection count/block totals, followed by the `MinecraftGit-Commit: v1` trailer body with selection names, block counts and entity changes.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `request` | [`CommitRequest`](CommitRequest.md) | Commit request providing the optional user message and player name. |
| `committedNames` | `List<String>` | Names of selections that produced changes in this commit. |
| `added` | `long` | Number of block coordinates added relative to HEAD. |
| `changed` | `long` | Number of tracked block coordinates whose state changed. |
| `removed` | `long` | Number of block coordinates removed relative to HEAD. |
| `entityChanges` | `long` | Total entity additions plus removals. |

### Returns
`String` — Full multi-line commit message used for both the Git commit and the returned `CommitResult`.

---

## collectFiles

### Signature
```java
private static void collectFiles(Path root, String selection, Map<String, byte[]> files)
```

### Description
Recursively walks a temporary selection tree and copies every regular file into `files` under `<selection>/<relative path>` with `/` separators, ready for `RepoService.commitWorkspace`.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `root` | `Path` | Root of the temporary directory holding one selection's CSV/meta tree. |
| `selection` | `String` | Selection name used as the repository-relative directory prefix. |
| `` | `Map<String` | Key type of the map: repository-relative path strings. |
| `files` | `byte[]>` | Value type of the map: raw bytes of each collected file. |

### Returns
`void`

---

## deleteRecursively

### Signature
```java
private static void deleteRecursively(Path path)
```

### Description
Deletes a temporary commit tree best-effort by walking depth-first in reverse order; a missing path and `IOException`s are ignored so cleanup cannot mask the commit result.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `path` | `Path` | Temporary directory or file to remove; it is left in place if deletion fails. |

### Returns
`void`

---

## See Also

- **Software Detailed Design:** [CommitService_SDD.md](./CommitService_SDD.md)
