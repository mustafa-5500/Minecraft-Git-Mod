# CommitResult

> **Source File:** [CommitResult.java](../../../../../../../src/main/java/org/almond/minecraftgit/commit/CommitResult.java)

## Usage

Result of a successful [`CommitService.commit`](CommitService.md), consumed by
`CommitCommands` for chat feedback.

## Fields

| Name | Type | Description |
|------|------|-------------|
| `commitId` | `String` | Full commit object id. |
| `shortId` | `String` | Abbreviated id (8 characters). |
| `selections` | `List<String>` | Selection names actually committed (unchanged ones omitted). |
| `added` | `long` | Added block coordinates. |
| `changed` | `long` | Changed block coordinates. |
| `removed` | `long` | Removed block coordinates. |
| `entityChanges` | `long` | Entity additions + removals. |
| `message` | `String` | Full generated/user commit message. |

## See Also

- [CommitService](CommitService.md) — produces the result.
- [CommitRequest](CommitRequest.md) — the input type.
- [Implementation class reference](../../../../../../Implementation/06-Class-Reference.md)
