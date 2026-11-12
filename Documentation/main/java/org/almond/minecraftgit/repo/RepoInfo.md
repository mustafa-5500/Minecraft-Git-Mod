# RepoInfo

> **Source File:** [RepoInfo.java](../../../../../../../src/main/java/org/almond/minecraftgit/repo/RepoInfo.java)

## Usage

Read-only snapshot of a repository's state, returned by
[`RepoService.info`](RepoService.md) and shown by `/git repo` and `/git init`.

## Fields

| Name | Type | Description |
|------|------|-------------|
| `path` | `Path` | Repository root on disk. |
| `branch` | `String` | Current branch, or empty for a detached HEAD. |
| `headId` | `String` | Full HEAD commit id, or empty when the branch has no commits. |
| `headSummary` | `String` | Short message of the HEAD commit. |
| `remoteName` | `String` | Configured upstream remote for the branch (or the default). |
| `remoteUrl` | `String` | URL of that remote, or empty. |
| `authorName` / `authorEmail` | `String` | Effective commit identity. |
| `hasCommits` | `boolean` | Whether HEAD resolves to a commit. |
| `clean` | `boolean` | Whether the JGit working tree has no changes. |

## See Also

- [RepoService](RepoService.md) — produces the record.
- [Implementation class reference](../../../../../../Implementation/06-Class-Reference.md)
