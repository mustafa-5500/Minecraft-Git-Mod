# CommitCommands

> **Software Detailed Documentation:** [CommitCommands_SDD.md](./CommitCommands_SDD.md)  
> **Source File:** [CommitCommands.java](../../../../../../../../src/client/java/org/almond/minecraftgit/client/command/CommitCommands.java)

## Table of Contents

- [Usage](#usage)
- [CommitCommands (Constructor)](#commitcommands-constructor)
- [attach](#attach)
- [commit](#commit)
- [showResult](#showresult)
- [requirePlayer](#requireplayer)
- [world](#world)
- [See Also](#see-also)

## Usage

`CommitCommands.attach` is called by `GitCommand.register` and adds the `/git commit` subtree; the class is never instantiated. Examples: `/git commit` (commit every staged selection with a generated message), `/git commit Added the tower`, `/git commit --empty`, and `/git commit --empty "checkpoint"`.

`/git commit [message]`: write all staged selections to the repository.

---

## CommitCommands (Constructor)

### Signature
```java
private CommitCommands()
```

### Description
Private constructor; the class is a static command module that only attaches nodes to the shared root.

### Parameters
None.

### Returns
`CommitCommands` instance.

---

## attach

### Signature
```java
public static void attach(LiteralArgumentBuilder<FabricClientCommandSource> root, GitServices services)
```

### Description
Adds the `commit` literal to the root: the plain form commits all staged selections, an optional greedy `message` argument overrides the generated message, and the `--empty` child permits a commit when nothing is staged or nothing changed, optionally with its own message.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `root` | `LiteralArgumentBuilder<FabricClientCommandSource>` | shared `/git` root the `commit` node is attached to |
| `services` | [`GitServices`](../GitServices.md) | service graph used by the handler |

### Returns
`void`

---

## commit

### Signature
```java
private static int commit(FabricClientCommandSource source, GitServices services,
                              String message, boolean allowEmpty)
```

### Description
Handler for all commit variants. It requires a player and an initialized repository, collects the player's selections that have a staged snapshot (`StagingService.hasStaged`), and fails when none are staged unless `allowEmpty` is set. It then builds an immutable `CommitRequest` (player name/UUID, world id, game mode, staged selections, message, allowEmpty), sends a progress line, and runs `CommitService.commit` on the IO executor through `CommandSupport.complete`, reporting the result with `showResult`. The method returns `1` once scheduling succeeds.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | command source providing the player and chat feedback |
| `services` | [`GitServices`](../GitServices.md) | repository, selection, staging and commit services |
| `message` | `String` | optional commit message; `null` or blank uses the generated summary |
| `allowEmpty` | `boolean` | whether `--empty` was passed, permitting no staged or changed selections |

### Returns
`int` — `1` when the commit was scheduled, `0` if validation failed and the error was reported.

---

## showResult

### Signature
```java
private static void showResult(FabricClientCommandSource source, GitServices services, CommitResult result)
```

### Description
Formats the completed commit in chat: short id, number of committed selections, `+added ~changed -removed` block totals and entity changes, followed by the first line of the commit message. When no selection files changed it adds a warning that this was an empty commit.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | destination for the result messages |
| `services` | [`GitServices`](../GitServices.md) | message sender used for success/warn output |
| `result` | [`CommitResult`](../../../../../../../main/java/org/almond/minecraftgit/commit/CommitResult.md) | outcome produced by `CommitService` |

### Returns
`void`

---

## requirePlayer

### Signature
```java
private static Player requirePlayer(FabricClientCommandSource source)
```

### Description
Returns the player from the source or throws `SelectionException` when the source is not a player, using the same `only be run by a player` wording as `CommandSupport`.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | source to read the player from |

### Returns
`Player` — the player running the command; never `null`.

---

## world

### Signature
```java
private static WorldContext world(FabricClientCommandSource source)
```

### Description
Derives the current `WorldContext` from the client, which supplies the world id used for repository and staging keys; throws `UserFacingException` if no world is open.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | source whose client holds the current level |

### Returns
[`WorldContext`](../WorldContext.md) — the open world's id, dimension and single-player flag.

---

## See Also

- **Software Detailed Design:** [CommitCommands_SDD.md](./CommitCommands_SDD.md)
