# JGitProgressMonitor

> **Source File:** [JGitProgressMonitor.java](../../../../../../../src/main/java/org/almond/minecraftgit/repo/JGitProgressMonitor.java)

## Usage

Package-private adapter that forwards JGit's `ProgressMonitor` callbacks to the
mod's transport-agnostic [`RepoProgress`](RepoProgress.md) sink. `RepoService`
uses it for fetch and push; commands currently pass `RepoProgress.NOOP`, so the
adapter returns JGit's `NullProgressMonitor` and avoids per-object callback
overhead.

```java
FetchResult result = git.fetch()
        .setProgressMonitor(JGitProgressMonitor.of(progress))
        .call();
```

## Fields

| Name | Type | Description |
|------|------|-------------|
| `progress` | `RepoProgress` | Target sink; never null (`RepoProgress.NOOP` fallback). |
| `task` | `String` | Current JGit task title. |
| `total` | `int` | Total work units for the current task. |

## Methods

| Method | Description |
|--------|-------------|
| `static of(RepoProgress)` | Returns `NullProgressMonitor.INSTANCE` for a null/NOOP sink, otherwise a new adapter. |
| `start(int totalTasks)` | No-op; JGit's task count is not exposed to the command UI. |
| `beginTask(String title, int totalWork)` | Stores the title/total and calls `update(title, 0, total)`. |
| `update(int completed)` | Calls `update(task, completed, total)` on the sink. |
| `endTask()` | Calls `update(task, total, total)`. |
| `isCancelled()` | Always false; the mod has no cancel UI. |
| `showDuration(boolean)` | No-op. |

## See Also

- [RepoProgress](RepoProgress.md) — the sink interface and `NOOP`.
- [RepoService](RepoService.md) — passes the adapter to JGit fetch/push.
- [Implementation class reference](../../../../../../Implementation/06-Class-Reference.md)
