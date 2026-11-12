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

Create it once with a temporary root directory (for example the mod's `tmp` folder) and call `prepare` after a fetch when `PullPlan.canFastForward()` is true; feed the returned `PreparedPull`s to the apply service, then call `RepoService.fastForward` and `cleanup(prepared)`. For example, `List<PreparedPull> prepared = pull.prepare(worldId, selections, playerOrigin, repo, plan, null);` keeps the exported snapshots alive only until the world writes finish.

Reads remote commit trees and local HEAD state into snapshots that the apply
service can turn into world writes. All filesystem/repository work here runs
off the client thread; only selection metadata and world writes happen later.

---

## Fields

- `tmpRoot` (`Path`) — Normalized absolute directory under which each `pull-*` or `checkout-*` export directory is created.

---

## PullService (Constructor)

### Signature
```java
public PullService(Path tmpRoot)
```

### Description
Normalizes the supplied temporary root to an absolute path; no directories are created until `prepare` or `prepareCheckout` runs.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `tmpRoot` | `Path` | Directory for temporary selection exports; created on demand. |

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
Prepares a fast-forward pull: it lists the selections in `plan.remoteHead()` sorted by name, optionally filters and validates `onlySelection`, exports every remote selection into a fresh `pull-*` directory, and reads each exported `meta.json` plus CSVs into a target snapshot. For each name it also resolves the local selection (case-insensitive), the local origin fallback chain (selection origin, else `playerOrigin`), and the local HEAD snapshot and origin when `<repo>/<name>` exists, producing a `PreparedPull`. It throws [`PullException`](PullException.md) when `onlySelection` names a selection absent from the remote commit, when a partial pull is attempted on a remote with more than one selection, or when export/read I/O fails (in which case the temp directory is cleaned up).

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | World being pulled; the repository is already resolved by `repo`, so no path is built from it here. |
| `localSelections` | `List<Selection>` | Local selections used to find a same-named selection and its configured origin; unmatched names fall back to `playerOrigin`. |
| `playerOrigin` | [`BlockPos`](../model/BlockPos.md) | Origin used when neither a local selection nor its metadata supplies one. |
| `repo` | [`RepoService`](../repo/RepoService.md) | Provides remote selection listing/export and the local repository path for HEAD reads. |
| `plan` | [`PullPlan`](../repo/PullPlan.md) | Fetch result containing `remoteHead`; only that head is read here. |
| `onlySelection` | `String` | Optional case-insensitive selection name to prepare; blank or null means all selections. |

### Returns
`List<PreparedPull>` — One prepared pull per exported selection in remote-name order; empty when the remote commit has no selections and no `onlySelection` was requested.

---

## prepareCheckout

### Signature
```java
public PreparedCheckout prepareCheckout(Selection selection, RepoService repo, ObjectId commit)
```

### Description
Prepares a direct checkout of one selection at a specific commit: it creates a `checkout-*` temp directory, exports `<commit>/<selection.name()>` into it, and reads the target snapshot using the commit's origin (falling back to the selection origin). It also reads the current local HEAD snapshot for the same selection from `<repo>/<name>` so the caller can show or apply differences, and throws [`PullException`](PullException.md) when the commit does not contain the selection or export/read fails, cleaning up the temp directory.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `selection` | [`Selection`](../model/Selection.md) | Selection to export; its name must be a valid selection name. |
| `repo` | [`RepoService`](../repo/RepoService.md) | Repository service used to export from the commit and locate local HEAD. |
| `commit` | `ObjectId` | Commit whose selection tree is checked out. |

### Returns
`PreparedCheckout` — Target and local-HEAD snapshots plus the temporary export directory.

---

## cleanup

### Signature
```java
public void cleanup(Path root)
```

### Description
Recursively deletes `root` and everything beneath it, walking deepest-first and ignoring I/O errors; a null or already-missing root is a no-op. It is best-effort cleanup for partially built export directories, not a durability guarantee.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `root` | `Path` | Temporary directory to remove; may be null. |

### Returns
`void`

---

## cleanup

### Signature
```java
public void cleanup(List<PreparedPull> prepared)
```

### Description
Deletes the shared parent of the prepared pulls' temp directories (all `PreparedPull`s from one `prepare` call share a `pull-*` root), doing nothing for an empty list. It is intended to be called after apply succeeds or fails.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `prepared` | `List<PreparedPull>` | Result of a previous `prepare` call; only the first element's parent directory is used. |

### Returns
`void`

---

## shortId

### Signature
```java
private static String shortId(ObjectId id)
```

### Description
Abbreviates a JGit `ObjectId` to eight hex characters for messages, returning `?` when the id is null so error text never fails on a missing head.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `id` | `ObjectId` | Commit id to abbreviate, possibly null. |

### Returns
`String` — Eight-character id, or `?` when `id` is null.

---

## PreparedPull

### Signature
```java
public record PreparedPull(String name, Snapshot target, CommitMeta meta, Selection localSelection,
                               BlockPos localOrigin, Snapshot localHead, BlockPos localHeadOrigin,
                               Path tempDirectory)
```

### Description
One remote selection exported into a temporary directory, together with the target snapshot and the local HEAD/origin data needed to apply and rebase it. The record is immutable and owns no resources; the caller must invoke cleanup after use.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `name` | `String` | Selection name as it appears in the remote commit. |
| `target` | [`Snapshot`](../model/Snapshot.md) | Snapshot decoded from the remote commit for that selection. |
| `meta` | [`CommitMeta`](../repo/CommitMeta.md) | Parsed `meta.json` from the remote commit, or null when absent. |
| `localSelection` | [`Selection`](../model/Selection.md) | Matching local selection (case-insensitive), or null when none exists. |
| `localOrigin` | [`BlockPos`](../model/BlockPos.md) | Origin to apply against: local selection origin, else `playerOrigin`. |
| `localHead` | [`Snapshot`](../model/Snapshot.md) | Local HEAD snapshot for the same selection, or null when not checked out. |
| `localHeadOrigin` | [`BlockPos`](../model/BlockPos.md) | Committed origin of `localHead`, or null when there is no local HEAD. |
| `tempDirectory` | `Path` | Export directory holding the remote CSV and metadata files for this selection. |

### Returns
`record` — Immutable prepared-pull value.

---

## PreparedCheckout

### Signature
```java
public record PreparedCheckout(Snapshot target, CommitMeta meta, Snapshot localHead,
                                   BlockPos localHeadOrigin, Path tempDirectory)
```

### Description
One selection exported from a specific commit, with the target snapshot and the local HEAD/origin for comparison. Like `PreparedPull` it is a passive value whose temporary directory must be cleaned up by the caller.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `target` | [`Snapshot`](../model/Snapshot.md) | Snapshot decoded from the requested commit. |
| `meta` | [`CommitMeta`](../repo/CommitMeta.md) | Commit metadata for the selection in that commit, or null when `meta.json` is missing. |
| `localHead` | [`Snapshot`](../model/Snapshot.md) | Current local HEAD snapshot for the selection, or null when absent. |
| `localHeadOrigin` | [`BlockPos`](../model/BlockPos.md) | Committed origin of `localHead`, or null when absent. |
| `tempDirectory` | `Path` | Temporary `checkout-*` root containing the exported selection. |

### Returns
`record` — Immutable prepared-checkout value.

---

## See Also

- **Software Detailed Design:** [PullService_SDD.md](./PullService_SDD.md)
