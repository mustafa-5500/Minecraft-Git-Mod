# RemoteInfo

> **Source File:** [RemoteInfo.java](../../../../../../../src/main/java/org/almond/minecraftgit/repo/RemoteInfo.java)

## Usage

Remote name/URL pair, returned by [`RepoService.remoteList`](RepoService.md) and
shown by `/git remote list`.

## Fields

| Name | Type | Description |
|------|------|-------------|
| `name` | `String` | Remote name (validated by `RepoService`). |
| `url` | `String` | First URI configured for the remote, or empty. |

## See Also

- [RepoService](RepoService.md) — produces the records.
- [Implementation class reference](../../../../../../Implementation/06-Class-Reference.md)
