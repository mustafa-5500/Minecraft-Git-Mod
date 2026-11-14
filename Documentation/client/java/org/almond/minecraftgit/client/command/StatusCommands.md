# StatusCommands

> **Software Detailed Documentation:** [StatusCommands_SDD.md](./StatusCommands_SDD.md)  
> **Source File:** [StatusCommands.java](../../../../../../../../src/client/java/org/almond/minecraftgit/client/command/StatusCommands.java)

## Table of Contents

- [Usage](#usage)
- [StatusCommands (Constructor)](#statuscommands-constructor)
- [attach](#attach)
- [status](#status)
- [showStatus](#showstatus)
- [diff](#diff)
- [showDiff](#showdiff)
- [showSamples](#showsamples)
- [resolve](#resolve)
- [requirePlayer](#requireplayer)
- [world](#world)
- [names](#names)
- [See Also](#see-also)

## Usage

Used through chat: `/git status` prints one line per selection with an `[untracked]`, `[clean]` or `[dirty]` tag, cuboid and volume counts, staged and working diff summaries, and a final dirty/untracked/clean total. `/git diff [name]` shows the staged-vs-HEAD and current-vs-base diffs for one selection, with up to six sample blocks apiece for added, changed and removed entries; `name` falls back to the active selection.

`/git status` and `/git diff`.

---

## StatusCommands (Constructor)

### Signature
```java
private StatusCommands()
```

### Description
Prevents instantiation; the class is a static command handler with no per-instance state.

### Parameters
None.

### Returns
`StatusCommands` instance.

---

## attach

### Signature
```java
public static void attach(LiteralArgumentBuilder<FabricClientCommandSource> root, GitServices services)
```

### Description
Registers `/git status` and `/git diff [name]` on the root builder, with selection-name suggestions for the optional diff argument.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `root` | `LiteralArgumentBuilder<FabricClientCommandSource>` | The `/git` root literal builder that the subcommands are attached to. |
| `services` | [`GitServices`](../GitServices.md) | Service graph supplying repository, pull/apply, selection, staging, status and messaging services. |

### Returns
`void`

---

## status

### Signature
```java
private static int status(FabricClientCommandSource source, GitServices services)
```

### Description
Entry point for `/git status`: it filters the player's selections, captures a current snapshot for each candidate sequentially, then asks `StatusService.report` for the status on the IO executor and renders it. Selections with no cuboids or with no tracking, HEAD or staged presence are skipped for capture, and selections over `statusMaxVolume` are flagged instead of captured.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | Command source used to resolve the player and to send chat feedback and errors. |
| `services` | [`GitServices`](../GitServices.md) | Service graph supplying repository, pull/apply, selection, staging, status and messaging services. |

### Returns
`int` — `1` on success; `0` when `CommandSupport.runInt` caught and reported an error.

---

## showStatus

### Signature
```java
private static void showStatus(FabricClientCommandSource source, GitServices services, WorldContext world,
                                   StatusService.Report report)
```

### Description
Renders the report: a header with the world id and branch/HEAD (or a no-repository note), one raw line per selection with its tag, cuboid and volume counts, indented staged and working diff summaries, any warning note, and the closing dirty/untracked/clean summary.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | Command source used to resolve the player and to send chat feedback and errors. |
| `services` | [`GitServices`](../GitServices.md) | Service graph supplying repository, pull/apply, selection, staging, status and messaging services. |
| `world` | [`WorldContext`](../WorldContext.md) | World context identifying the world and dimension and whether it is single-player. |
| `report` | `StatusService.Report` | The computed status report (branch, HEAD, per-selection statuses and counts) to render. |

### Returns
`void`

---

## diff

### Signature
```java
private static int diff(FabricClientCommandSource source, GitServices services, String requested)
```

### Description
Entry point for `/git diff [name]`: it resolves the selection, captures its current snapshot unless its volume exceeds `statusMaxVolume`, computes the diff through `StatusService.diff` on the IO executor and renders the result.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | Command source used to resolve the player and to send chat feedback and errors. |
| `services` | [`GitServices`](../GitServices.md) | Service graph supplying repository, pull/apply, selection, staging, status and messaging services. |
| `requested` | `String` | Optional selection name; blank falls back to the active selection. |

### Returns
`int` — `1` on success; `0` when `CommandSupport.runInt` caught and reported an error.

---

## showDiff

### Signature
```java
private static void showDiff(FabricClientCommandSource source, GitServices services, Selection selection,
                                 StatusService.DiffReport report)
```

### Description
Prints the selection header with HEAD/staged/current flags, any note, the staged-vs-HEAD and current-vs-base summaries, and sample blocks for added, changed and removed entries from the working diff.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | Command source used to resolve the player and to send chat feedback and errors. |
| `services` | [`GitServices`](../GitServices.md) | Service graph supplying repository, pull/apply, selection, staging, status and messaging services. |
| `selection` | [`Selection`](../../../../../../../main/java/org/almond/minecraftgit/model/Selection.md) | The selection being displayed; its name and world are printed in the header. |
| `report` | `StatusService.DiffReport` | Diff report containing the selection status, staged diff and working diff. |

### Returns
`void`

---

## showSamples

### Signature
```java
private static void showSamples(FabricClientCommandSource source, String label,
                                    List<BlockSnapshot> blocks)
```

### Description
Prints up to six sample blocks of one diff category with their relative coordinates and block data, followed by a line counting the remaining blocks; an empty list prints nothing.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | Command source used to resolve the player and to send chat feedback and errors. |
| `label` | `String` | Category label (`added`, `changed` or `removed`). |
| `blocks` | `List<BlockSnapshot>` | The diff sample list; at most six entries are printed. |

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

## names

### Signature
```java
private static SuggestionProvider<FabricClientCommandSource> names(GitServices services)
```

### Description
Builds a suggestion provider that lists the player's selection names; runtime failures are swallowed so autocomplete never breaks argument parsing.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `services` | [`GitServices`](../GitServices.md) | Service graph supplying repository, pull/apply, selection, staging, status and messaging services. |

### Returns
`SuggestionProvider<FabricClientCommandSource>` — A `SuggestionProvider` that suggests the player's selection names.

---

## See Also

- **Software Detailed Design:** [StatusCommands_SDD.md](./StatusCommands_SDD.md)
