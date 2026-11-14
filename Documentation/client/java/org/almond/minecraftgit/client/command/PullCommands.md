# PullCommands

> **Software Detailed Documentation:** [PullCommands_SDD.md](./PullCommands_SDD.md)  
> **Source File:** [PullCommands.java](../../../../../../../../src/client/java/org/almond/minecraftgit/client/command/PullCommands.java)

## Table of Contents

- [Usage](#usage)
- [PullCommands (Constructor)](#pullcommands-constructor)
- [attach](#attach)
- [pull](#pull)
- [onPlan](#onplan)
- [applyNext](#applynext)
- [ensureSelection](#ensureselection)
- [addCuboids](#addcuboids)
- [finishPull](#finishpull)
- [fail](#fail)
- [abort](#abort)
- [checkout](#checkout)
- [sendApplyResult](#sendapplyresult)
- [resolve](#resolve)
- [remoteFor](#remotefor)
- [requirePlayer](#requireplayer)
- [world](#world)
- [shortId](#shortid)
- [message](#message)
- [See Also](#see-also)

## Usage

Used through chat: `/git pull` fetches and fast-forwards the current branch after writing every remote selection into the world, `/git pull <name>` narrows the pull to one remote selection, `/git pull --remote <remote> [name]` overrides the remote, `/git pull --abort [name]` restores the latest pre-apply backup, and `/git checkout <commit> [name]` applies a revision into an existing selection. Every command requires a player and a single-player world.

`/git pull` and `/git checkout`.
Pull is fast-forward-only and single-player-only for the apply step. The
local branch is moved only after every selected build has been written, so a
failed world write never leaves Git ahead of the world. A pre-apply backup is
written for each selection and can be restored with `--abort`.

---

## PullCommands (Constructor)

### Signature
```java
private PullCommands()
```

### Description
Prevents instantiation; the class is a static command handler with no per-instance state.

### Parameters
None.

### Returns
`PullCommands` instance.

---

## attach

### Signature
```java
public static void attach(LiteralArgumentBuilder<FabricClientCommandSource> root, GitServices services)
```

### Description
Registers `/git pull`, `/git pull --abort [name]`, `/git pull --remote <remote> [name]` and `/git checkout <commit> [name]` on the root builder, wiring each leaf to the matching static handler. Selection-name arguments use the internal suggestion provider built from `SelectionService.list`.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `root` | `LiteralArgumentBuilder<FabricClientCommandSource>` | The `/git` root literal builder that the subcommands are attached to. |
| `services` | [`GitServices`](../GitServices.md) | Service graph supplying repository, pull/apply, selection, staging, status and messaging services. |

### Returns
`void`

---

## pull

### Signature
```java
private static int pull(FabricClientCommandSource source, GitServices services,
                            String onlySelection, String requestedRemote)
```

### Description
Entry point for `/git pull`: it requires a player and a single-player world, checks that the repository is initialized, resolves the remote, records the player's origin and current selections, then runs `RepoService.planPull` off the client thread and passes the plan to `onPlan`. Failures are reported by `CommandSupport.runInt`, which returns `0`.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | Command source used to resolve the player and to send chat feedback and errors. |
| `services` | [`GitServices`](../GitServices.md) | Service graph supplying repository, pull/apply, selection, staging, status and messaging services. |
| `onlySelection` | `String` | Optional selection filter from the command line; blank or `null` means every remote selection. |
| `requestedRemote` | `String` | Remote name given as `--remote`; `null` or blank falls back to repository metadata and then the configured default. |

### Returns
`int` — `1` on success; `0` when `CommandSupport.runInt` caught and reported an error.

---

## onPlan

### Signature
```java
private static void onPlan(FabricClientCommandSource source, GitServices services, WorldContext world,
                               Player player, List<Selection> localSelections, BlockPos playerOrigin,
                               RepoService repo, PullPlan plan, String onlySelection)
```

### Description
Handles the fetched plan: reports an up-to-date branch, refuses diverged histories with a `PullException`, then prepares every remote selection asynchronously and starts applying them one by one. When the remote head contains no selections it goes straight to the fast-forward.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | Command source used to resolve the player and to send chat feedback and errors. |
| `services` | [`GitServices`](../GitServices.md) | Service graph supplying repository, pull/apply, selection, staging, status and messaging services. |
| `world` | [`WorldContext`](../WorldContext.md) | World context identifying the world and dimension and whether it is single-player. |
| `player` | `Player` | The issuing player; their UUID scopes selections. |
| `localSelections` | `List<Selection>` | The player's selections before the pull, used to match names and reuse existing origins. |
| `playerOrigin` | [`BlockPos`](../../../../../../../main/java/org/almond/minecraftgit/model/BlockPos.md) | The player's current block position, used as the origin for new or origin-less selections. |
| `repo` | [`RepoService`](../../../../../../../main/java/org/almond/minecraftgit/repo/RepoService.md) | Repository service for the current world; used to plan, resolve and fast-forward. |
| `plan` | [`PullPlan`](../../../../../../../main/java/org/almond/minecraftgit/repo/PullPlan.md) | The planner's fetch result: local/remote heads, merge base, branch and the up-to-date/fast-forward/diverged classification. |
| `onlySelection` | `String` | Optional selection filter from the command line; blank or `null` means every remote selection. |

### Returns
`void`

---

## applyNext

### Signature
```java
private static void applyNext(FabricClientCommandSource source, GitServices services, WorldContext world,
                                  Player player, RepoService repo, PullPlan plan,
                                  List<PullService.PreparedPull> prepared, int index)
```

### Description
Applies the prepared pull at `index`: ensures a local selection, builds an `ApplyPlan` from the local HEAD snapshot to the remote target and submits it to `ApplyService.apply`, handling the completion on the client thread. It then recurses to the next pull, or calls `finishPull` after the last one; any failure aborts the whole pull via `fail`.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | Command source used to resolve the player and to send chat feedback and errors. |
| `services` | [`GitServices`](../GitServices.md) | Service graph supplying repository, pull/apply, selection, staging, status and messaging services. |
| `world` | [`WorldContext`](../WorldContext.md) | World context identifying the world and dimension and whether it is single-player. |
| `player` | `Player` | The issuing player; their UUID scopes selections. |
| `repo` | [`RepoService`](../../../../../../../main/java/org/almond/minecraftgit/repo/RepoService.md) | Repository service for the current world; used to plan, resolve and fast-forward. |
| `plan` | [`PullPlan`](../../../../../../../main/java/org/almond/minecraftgit/repo/PullPlan.md) | The planner's fetch result: local/remote heads, merge base, branch and the up-to-date/fast-forward/diverged classification. |
| `prepared` | `List<PullService.PreparedPull>` | Prepared remote selections, applied in order from `index`. |
| `index` | `int` | Zero-based index of the prepared pull currently being applied. |

### Returns
`void`

---

## ensureSelection

### Signature
```java
private static Selection ensureSelection(GitServices services, WorldContext world, Player player,
                                             PullService.PreparedPull prepared)
```

### Description
Returns the local selection that receives the remote build, creating it at the prepared local origin when missing or filling in a missing origin, adding cuboids when it has none, and marking it tracked. The persisted selection is re-read before it is returned.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `services` | [`GitServices`](../GitServices.md) | Service graph supplying repository, pull/apply, selection, staging, status and messaging services. |
| `world` | [`WorldContext`](../WorldContext.md) | World context identifying the world and dimension and whether it is single-player. |
| `player` | `Player` | The issuing player; their UUID scopes selections. |
| `prepared` | `PullService.PreparedPull` | Prepared remote selection describing the name, origin and target snapshot. |

### Returns
[`Selection`](../../../../../../../main/java/org/almond/minecraftgit/model/Selection.md) — The persisted, tracked selection with an origin and at least one cuboid.

---

## addCuboids

### Signature
```java
private static void addCuboids(GitServices services, WorldContext world, Player player,
                                   Selection selection, PullService.PreparedPull prepared, boolean created)
```

### Description
Adds the remote build's cuboids to a newly created or empty selection, translating relative cuboids from the commit metadata (or the target snapshot's bounding box) by the local origin. If an add fails for a selection created by this pull, the selection is deleted and the exception rethrown.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `services` | [`GitServices`](../GitServices.md) | Service graph supplying repository, pull/apply, selection, staging, status and messaging services. |
| `world` | [`WorldContext`](../WorldContext.md) | World context identifying the world and dimension and whether it is single-player. |
| `player` | `Player` | The issuing player; their UUID scopes selections. |
| `selection` | [`Selection`](../../../../../../../main/java/org/almond/minecraftgit/model/Selection.md) | The selection receiving the pulled build; its cuboids are appended. |
| `prepared` | `PullService.PreparedPull` | Prepared remote selection providing the relative cuboids, origin and target snapshot. |
| `created` | `boolean` | Whether this call created the selection, so it can be deleted if adding cuboids fails. |

### Returns
`void`

---

## finishPull

### Signature
```java
private static void finishPull(FabricClientCommandSource source, GitServices services, WorldContext world,
                                   RepoService repo, PullPlan plan, List<PullService.PreparedPull> prepared)
```

### Description
Performs the final fast-forward of the local branch on the IO executor after every world write succeeded, cleans up the prepared temporary directory, and reports success with the new remote head. If the branch cannot be moved, the already-applied build is reported as not reflected in HEAD.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | Command source used to resolve the player and to send chat feedback and errors. |
| `services` | [`GitServices`](../GitServices.md) | Service graph supplying repository, pull/apply, selection, staging, status and messaging services. |
| `world` | [`WorldContext`](../WorldContext.md) | World context identifying the world and dimension and whether it is single-player. |
| `repo` | [`RepoService`](../../../../../../../main/java/org/almond/minecraftgit/repo/RepoService.md) | Repository service for the current world; used to plan, resolve and fast-forward. |
| `plan` | [`PullPlan`](../../../../../../../main/java/org/almond/minecraftgit/repo/PullPlan.md) | The planner's fetch result: local/remote heads, merge base, branch and the up-to-date/fast-forward/diverged classification. |
| `prepared` | `List<PullService.PreparedPull>` | Prepared remote selections whose temporary files are cleaned up. |

### Returns
`void`

---

## fail

### Signature
```java
private static void fail(FabricClientCommandSource source, GitServices services,
                             List<PullService.PreparedPull> prepared, Throwable error)
```

### Description
Cleans up the prepared temporary directory asynchronously and reports that the pull failed without moving the local branch.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | Command source used to resolve the player and to send chat feedback and errors. |
| `services` | [`GitServices`](../GitServices.md) | Service graph supplying repository, pull/apply, selection, staging, status and messaging services. |
| `prepared` | `List<PullService.PreparedPull>` | Prepared remote selections whose temporary files are cleaned up. |
| `error` | `Throwable` | The failure whose message is reported; the local branch is not moved. |

### Returns
`void`

---

## abort

### Signature
```java
private static int abort(FabricClientCommandSource source, GitServices services, String requested)
```

### Description
Restores the latest pre-apply backup for a selection through an `ApplyPlan.restore` plan, scheduling the apply and reporting its result. It requires single-player and fails when no backup exists for the selection.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | Command source used to resolve the player and to send chat feedback and errors. |
| `services` | [`GitServices`](../GitServices.md) | Service graph supplying repository, pull/apply, selection, staging, status and messaging services. |
| `requested` | `String` | Optional selection name; when blank the active selection is used. |

### Returns
`int` — `1` on success; `0` when `CommandSupport.runInt` caught and reported an error.

---

## checkout

### Signature
```java
private static int checkout(FabricClientCommandSource source, GitServices services,
                                String revision, String requested)
```

### Description
Applies a specific revision's build into an existing selection, anchored at the selection's own origin so the local build is not relocated. The revision is resolved, the commit snapshot is prepared off the client thread, and the temporary directory is deleted after the apply completes.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | Command source used to resolve the player and to send chat feedback and errors. |
| `services` | [`GitServices`](../GitServices.md) | Service graph supplying repository, pull/apply, selection, staging, status and messaging services. |
| `revision` | `String` | Commit-ish resolved through `RepoService.resolve`, e.g. a commit hash or branch name. |
| `requested` | `String` | Optional selection name; when blank the active selection is used. |

### Returns
`int` — `1` on success; `0` when `CommandSupport.runInt` caught and reported an error.

---

## sendApplyResult

### Signature
```java
private static void sendApplyResult(FabricClientCommandSource source, GitServices services,
                                        ApplyResult result)
```

### Description
Reports the apply summary (appending the backup file name when one was written) and up to five warnings through the message service.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | Command source used to resolve the player and to send chat feedback and errors. |
| `services` | [`GitServices`](../GitServices.md) | Service graph supplying repository, pull/apply, selection, staging, status and messaging services. |
| `result` | [`ApplyResult`](../../../../../../../main/java/org/almond/minecraftgit/world/ApplyResult.md) | Apply result carrying placed/removed counts, the backup file and warnings. |

### Returns
`void`

---

## resolve

### Signature
```java
private static Selection resolve(GitServices services, WorldContext world, Player player, String requested)
```

### Description
Resolves the selection from the explicit argument or the active selection, throwing `SelectionException` when neither is available.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `services` | [`GitServices`](../GitServices.md) | Service graph supplying repository, pull/apply, selection, staging, status and messaging services. |
| `world` | [`WorldContext`](../WorldContext.md) | World context identifying the world and dimension and whether it is single-player. |
| `player` | `Player` | The issuing player; their UUID scopes selections. |
| `requested` | `String` | Optional selection name; when blank the active selection is used. |

### Returns
[`Selection`](../../../../../../../main/java/org/almond/minecraftgit/model/Selection.md) — The named or active selection read from `SelectionService`.

---

## remoteFor

### Signature
```java
private static String remoteFor(GitServices services, WorldContext world, String requested)
```

### Description
Chooses the remote: the explicit `--remote` value, otherwise the repository metadata's remote name, otherwise the configured default remote.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `services` | [`GitServices`](../GitServices.md) | Service graph supplying repository, pull/apply, selection, staging, status and messaging services. |
| `world` | [`WorldContext`](../WorldContext.md) | World context identifying the world and dimension and whether it is single-player. |
| `requested` | `String` | Optional selection name; when blank the active selection is used. |

### Returns
`String` — The chosen remote name.

---

## requirePlayer

### Signature
```java
private static Player requirePlayer(FabricClientCommandSource source)
```

### Description
Returns the issuing player, or throws `SelectionException` when the command source has none.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | Command source used to resolve the player and to send chat feedback and errors. |

### Returns
`Player` — The issuing player.

---

## world

### Signature
```java
private static WorldContext world(FabricClientCommandSource source)
```

### Description
Derives the current `WorldContext` from the command source's client.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | Command source used to resolve the player and to send chat feedback and errors. |

### Returns
[`WorldContext`](../WorldContext.md) — The current `WorldContext`.

---

## shortId

### Signature
```java
private static String shortId(org.eclipse.jgit.lib.ObjectId id)
```

### Description
Abbreviates a JGit object id to eight hex characters for chat messages.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `id` | `org.eclipse.jgit.lib.ObjectId` | JGit object id to abbreviate; `null` is rendered as `?`. |

### Returns
`String` — The eight-character abbreviation, or `?` when `id` is `null`.

---

## message

### Signature
```java
private static String message(Throwable error)
```

### Description
Returns the throwable's message, falling back to the simple class name when the message is `null`.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `error` | `Throwable` | The failure whose message is reported; the local branch is not moved. |

### Returns
`String` — The error's message, or its simple class name when the message is `null`.

---

## See Also

- **Software Detailed Design:** [PullCommands_SDD.md](./PullCommands_SDD.md)
