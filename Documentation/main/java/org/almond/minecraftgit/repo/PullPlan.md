# PullPlan

> **Source File:** [PullPlan.java](../../../../../../../src/main/java/org/almond/minecraftgit/repo/PullPlan.java)

## Usage

The result of a fetch plus the classification of the local/remote branch
relationship, produced by [`RepoService.planPull`](RepoService.md). The caller
applies the remote snapshots to the world **before** calling
[`RepoService.fastForward`](RepoService.md), so a failed world write never moves
Git.

```java
PullPlan plan = repo.planPull("origin", RepoProgress.NOOP);
if (plan.canFastForward()) {
    // ... apply prepared pulls ...
    repo.fastForward(plan);
}
```

## Fields

| Name | Type | Description |
|------|------|-------------|
| `remote` | `String` | Remote name that was fetched. |
| `branch` | `String` | Current branch name (attached HEAD). |
| `localHead` | `ObjectId` | Local HEAD before the pull, or `null` for an unborn branch. |
| `remoteHead` | `ObjectId` | Fetched remote branch head. |
| `mergeBase` | `ObjectId` | Merge base when histories are related, else `null`. |
| `type` | [`Type`](Type.md) | `UP_TO_DATE`, `FAST_FORWARD` or `DIVERGED`. |
| `fetchedRefs` | `int` | Number of tracking refs updated by the fetch. |

## Methods

| Method | Description |
|--------|-------------|
| `canFastForward()` | True only for `Type.FAST_FORWARD`. |

## See Also

- [Type](Type.md) — the nested relationship enum.
- [RepoService](RepoService.md) — produces the plan and performs `fastForward`.
- [Implementation class reference](../../../../../../Implementation/06-Class-Reference.md)
