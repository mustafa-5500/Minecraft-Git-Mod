# CommitInfo

> **Source File:** [CommitInfo.java](../../../../../../../src/main/java/org/almond/minecraftgit/repo/CommitInfo.java)

## Usage

One row of `/git log`, produced by [`RepoService.log`](RepoService.md).

## Fields

| Name | Type | Description |
|------|------|-------------|
| `id` | `String` | Full commit object id. |
| `shortId` | `String` | Abbreviated id (8 characters). |
| `author` | `String` | Author name, or `"unknown"` when JGit has no ident. |
| `email` | `String` | Author email, or empty. |
| `when` | `Instant` | Author time (`Instant.EPOCH` when absent). |
| `message` | `String` | First line of the commit message. |

## See Also

- [RepoService](RepoService.md) — produces the records.
- [Implementation class reference](../../../../../../Implementation/06-Class-Reference.md)
