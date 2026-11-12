# StagingCommands

> **Software Detailed Documentation:** [StagingCommands_SDD.md](./StagingCommands_SDD.md)  
> **Source File:** [StagingCommands.java](../../../../../../../../src/client/java/org/almond/minecraftgit/client/command/StagingCommands.java)

## Table of Contents

- [Usage](#usage)
- [StagingCommands (Constructor)](#stagingcommands-constructor)
- [attach](#attach)
- [add](#add)
- [onStaged](#onstaged)
- [rm](#rm)
- [resolve](#resolve)
- [requirePlayer](#requireplayer)
- [world](#world)
- [names](#names)
- [See Also](#see-also)

## Usage

`StagingCommands.attach` is called by `GitCommand.register` and adds `/git add` and `/git rm`; the class is never instantiated. Examples: `/git add` or `/git add tower` to capture and stage a selection, and `/git rm` or `/git rm tower` to untrack it and delete its staged snapshot.

`/git add` and `/git rm`: stage and untrack a selection.

---

## StagingCommands (Constructor)

### Signature
```java
private StagingCommands()
```

### Description
Private constructor; the class is a static command module attached to the shared `/git` root.

### Parameters
None.

### Returns
`StagingCommands` instance.

---

## attach

### Signature
```java
public static void attach(LiteralArgumentBuilder<FabricClientCommandSource> root, GitServices services)
```

### Description
Adds `add` and `rm` literals, each with a no-argument form (targets the active selection) and an optional `name` string argument whose tab completion comes from `names(services)`.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `root` | `LiteralArgumentBuilder<FabricClientCommandSource>` | shared `/git` root the `add`/`rm` nodes attach to |
| `services` | [`GitServices`](../GitServices.md) | service graph used by handlers and suggestions |

### Returns
`void`

---

## add

### Signature
```java
private static int add(FabricClientCommandSource source, GitServices services, String requested)
```

### Description
Runs the capture -> stage pipeline for one selection. It requires a player and an initialized repository, resolves the target (`requested` name or the active selection), rejects selections with no cuboids, and calls `WorldAccess.capture` (integrated server when single-player, loaded client chunks otherwise). On the client thread it prints the capture summary and at most five warnings, stages the snapshot with `services.staging().stage`, and finishes in `onStaged`. Returns `1` when the pipeline starts; failures inside callbacks are reported by `CommandSupport.handleError`.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | command source for player lookup and feedback |
| `services` | [`GitServices`](../GitServices.md) | repository, selection, staging and capture services |
| `requested` | `String` | selection name, or `null` to use the active selection |

### Returns
`int` — `1` when the async capture was started, `0` if validation failed.

---

## onStaged

### Signature
```java
private static void onStaged(FabricClientCommandSource source, GitServices services, WorldContext world,
                                 Player player, Selection selection, StagedSelection staged)
```

### Description
Completes the stage pipeline: marks the selection tracked, makes it the active selection so later `/git add`, `/git rm` and `/git commit` can omit the name, and reports block/entity counts plus the snapshot origin. It adds a note when the capture was empty and always suggests `/git commit` as the next step.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | destination for the success and next-step messages |
| `services` | [`GitServices`](../GitServices.md) | selection tracking and message services |
| `world` | [`WorldContext`](../WorldContext.md) | world id used for tracking and staging keys |
| `player` | `Player` | owner of the selection; provides the UUID |
| `selection` | [`Selection`](../../../../../../../main/java/org/almond/minecraftgit/model/Selection.md) | selection that was just staged |
| `staged` | [`StagedSelection`](../../../../../../../main/java/org/almond/minecraftgit/staging/StagedSelection.md) | durable staged snapshot with counts and origin |

### Returns
`void`

---

## rm

### Signature
```java
private static int rm(FabricClientCommandSource source, GitServices services, String requested)
```

### Description
Untracks a selection and removes its staged snapshot: after validating player and repository it resolves the target, deletes the staging file on the IO executor (`StagingService.unstage`), then clears the tracked flag on the client thread and reports success. Returns `1` when the removal was scheduled; unstage errors are reported through `CommandSupport.handleError`.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | command source for player lookup and feedback |
| `services` | [`GitServices`](../GitServices.md) | repository, selection and staging services |
| `requested` | `String` | selection name, or `null` to use the active selection |

### Returns
`int` — `1` when the async unstage was started, `0` if validation failed.

---

## resolve

### Signature
```java
private static Selection resolve(GitServices services, WorldContext world, Player player, String requested)
```

### Description
Resolves the selection a command should act on: a non-blank `requested` name wins, otherwise `SelectionManager.active` supplies the remembered selection. If neither exists it throws `SelectionException` asking for a name or `/git selection select`; the name is then loaded through `SelectionService.get`, which also throws when unknown for this world and player.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `services` | [`GitServices`](../GitServices.md) | selection and selection-manager services |
| `world` | [`WorldContext`](../WorldContext.md) | world id used to look up the selection |
| `player` | `Player` | owner whose selection list is searched |
| `requested` | `String` | explicit name, possibly empty or `null` |

### Returns
[`Selection`](../../../../../../../main/java/org/almond/minecraftgit/model/Selection.md) — the existing selection for this world and player.

---

## requirePlayer

### Signature
```java
private static Player requirePlayer(FabricClientCommandSource source)
```

### Description
Returns the source's player or throws `SelectionException` with the `only be run by a player` message when the source has no player.

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
Returns `WorldContext.current(source.getClient())`, the world id/dimension/single-player context used by every staging operation; it throws `UserFacingException` when no world is open.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | source whose client holds the current level |

### Returns
[`WorldContext`](../WorldContext.md) — the open world's id, dimension and single-player flag.

---

## names

### Signature
```java
private static SuggestionProvider<FabricClientCommandSource> names(GitServices services)
```

### Description
Builds the tab-completion provider for the `name` argument: it reads the player and world, suggests every selection name from `SelectionService.list`, and ignores any `RuntimeException` so suggestion failures never break command parsing. The future is already complete because names are read synchronously from the in-memory selection service.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `services` | [`GitServices`](../GitServices.md) | selection service used to enumerate names |

### Returns
`SuggestionProvider<FabricClientCommandSource>` — a completed suggestion future listing the player's selection names.

---

## See Also

- **Software Detailed Design:** [StagingCommands_SDD.md](./StagingCommands_SDD.md)
