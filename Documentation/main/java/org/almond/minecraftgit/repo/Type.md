# Type

> **Software Detailed Documentation:** [Type_SDD.md](./Type_SDD.md)  
> **Source File:** [PullPlan.java](../../../../../../../src/main/java/org/almond/minecraftgit/repo/PullPlan.java)

## Table of Contents

- [Usage](#usage)
- [PullPlan](#pullplan)
- [canFastForward](#canfastforward)
- [See Also](#see-also)

## Usage

`Type` is the nested enum `PullPlan.Type`, produced by `RepoService.planPull` after a fetch to classify how the local branch relates to the remote branch. Callers branch on it (usually via `PullPlan.canFastForward()`) to decide whether the remote snapshot can be applied to the world, while `DIVERGED` tells `/git pull` to stop and ask the player to resolve manually.

```java
PullPlan plan = repo.planPull("origin", RepoProgress.NOOP);
switch (plan.type()) {
    case UP_TO_DATE -> { /* nothing to apply */ }
    case FAST_FORWARD -> { /* apply remote snapshots, then repo.fastForward(plan) */ }
    case DIVERGED -> throw new RepoException("Local and remote histories have diverged.");
}
```

The classification produced by `planPull`: `UP_TO_DATE` means the remote adds nothing the local branch lacks (equal heads, or the remote head is an ancestor of local), `FAST_FORWARD` means the local branch is an ancestor of the remote (or the local branch is unborn/pristine), and `DIVERGED` means the two histories have split and a merge would be required.

---

## PullPlan

### Signature
```java
public record PullPlan(String remote, String branch, ObjectId localHead, ObjectId remoteHead,
                       ObjectId mergeBase, Type type, int fetchedRefs)
```

### Description
A fetch plus the relationship between local and remote branches. The caller
may apply the remote snapshots to the world before `RepoService.fastForward`
moves the local branch, so a failed world write never leaves Git ahead of the
world.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `remote` | `String` | Remote name that was fetched, e.g. `origin`. |
| `branch` | `String` | Local branch that was compared; also the remote-tracking branch name (`refs/remotes/<remote>/<branch>`). |
| `localHead` | `ObjectId` | Local HEAD commit at planning time, or `null` when the branch has no commits yet. |
| `remoteHead` | `ObjectId` | Commit the remote-tracking branch pointed at after the fetch. |
| `mergeBase` | `ObjectId` | Best common ancestor of the two heads, or `null` when the histories are unrelated or the local branch is unborn. |
| `type` | `Type` | Relationship classification deciding whether the plan may be fast-forwarded. |
| `fetchedRefs` | `int` | Number of remote-tracking refs updated by the fetch that produced this plan. |

### Returns
`record` — A `PullPlan` instance carrying the fetch result and its classification.

---

## canFastForward

### Signature
```java
public boolean canFastForward()
```

### Description
Convenience predicate equal to `type == Type.FAST_FORWARD`. It is the check `RepoService.fastForward` performs before moving the branch, and callers use it to gate applying remote snapshots to the world.

### Parameters
None.

### Returns
`boolean` — `true` only for `Type.FAST_FORWARD`.

---

## See Also

- **Software Detailed Design:** [Type_SDD.md](./Type_SDD.md)
