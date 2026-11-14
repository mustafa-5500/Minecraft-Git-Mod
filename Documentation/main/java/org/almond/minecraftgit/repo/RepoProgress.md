# RepoProgress

> **Software Detailed Documentation:** [RepoProgress_SDD.md](./RepoProgress_SDD.md)  
> **Source File:** [RepoProgress.java](../../../../../../../src/main/java/org/almond/minecraftgit/repo/RepoProgress.java)

## Table of Contents

- [Usage](#usage)
- [See Also](#see-also)

## Usage

A functional callback passed to `RepoService.fetch`, `RepoService.push` and `RepoService.planPull` (through `JGitProgressMonitor`) to report transport progress. Implementations receive `update(task, completed, total)` from a worker thread and must marshal any UI work to the client thread; `done()` may be overridden to clear an indicator, and `RepoProgress.NOOP` is the shared do-nothing instance used by commands that do not show progress.

```java
RepoProgress progress = (task, completed, total) ->
        client.execute(() -> MinecraftGit.LOGGER.info("{} {}/{}", task, completed, total));
repo.fetch("origin", progress);
```

Transport progress sink, called from a worker thread.

---

## See Also

- **Software Detailed Design:** [RepoProgress_SDD.md](./RepoProgress_SDD.md)
