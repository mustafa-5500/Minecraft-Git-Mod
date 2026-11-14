# CommitRequest

> **Source File:** [CommitRequest.java](../../../../../../../src/main/java/org/almond/minecraftgit/commit/CommitRequest.java)

## Usage

Immutable inputs for one commit, built on the client thread by
`CommitCommands` before the IO work starts. Passed to
[`CommitService.commit`](CommitService.md).

## Fields

| Name | Type | Description |
|------|------|-------------|
| `playerName` | `String` | Display name recorded in `meta.json` and the commit message. |
| `playerUuid` | `String` | Player UUID string recorded in `meta.json`. |
| `worldName` | `String` | World id used to locate the repository and staging. |
| `gameMode` | `String` | Game mode at commit time. |
| `selections` | `List<Selection>` | Staged selections to commit. |
| `message` | `String` | Optional user message; `null`/blank uses the generated message. |
| `allowEmpty` | `boolean` | `--empty`/explicit empty checkpoint flag. |

## See Also

- [CommitService](CommitService.md) — consumes the request.
- [CommitResult](CommitResult.md) — the result type.
- [Implementation class reference](../../../../../../Implementation/06-Class-Reference.md)
