# SelectionCommands

> **Software Detailed Documentation:** [SelectionCommands_SDD.md](./SelectionCommands_SDD.md)  
> **Source File:** [SelectionCommands.java](../../../../../../../../src/client/java/org/almond/minecraftgit/client/command/SelectionCommands.java)

## Table of Contents

- [Usage](#usage)
- [SelectionCommands (Constructor)](#selectioncommands-constructor)
- [attach](#attach)
- [usage](#usage)
- [create](#create)
- [list](#list)
- [select](#select)
- [show](#show)
- [add](#add)
- [remove](#remove)
- [reorigin](#reorigin)
- [delete](#delete)
- [info](#info)
- [wand](#wand)
- [position](#position)
- [resolve](#resolve)
- [requirePlayer](#requireplayer)
- [world](#world)
- [names](#names)
- [See Also](#see-also)

## Usage

`SelectionCommands` is a static-only module: `GitCommand.register` calls `attach` once with the shared `/git` builder and the active `GitServices`, and every subcommand delegates to those services. The normal flow is to create or select a named selection, click two corners with the wand, and then append further cuboids to the active selection; most subcommands accept an optional name and fall back to the active selection. For example: `/git selection create house`, `/git wand`, `/git selection info house`, `/git selection remove 2 house`, `/git selection reorigin house`, `/git selection show`, `/git selection list`, `/git selection delete house`, and the `/git selection pos1` / `/git selection pos2` fallbacks for multiplayer.

`/git selection ...`: create and manage named multi-cuboid selections.

---

## SelectionCommands (Constructor)

### Signature
```java
private SelectionCommands()
```

### Description
Private no-argument constructor for a static-only command module: the class is never instantiated, and all behaviour is registered by `attach` on the shared `/git` command tree.

### Parameters
None.

### Returns
`SelectionCommands` instance.

---

## attach

### Signature
```java
public static void attach(LiteralArgumentBuilder<FabricClientCommandSource> root, GitServices services)
```

### Description
Appends the `selection` literal (with its `create`, `list`, `select`, `show`, `add`, `remove`, `reorigin`, `delete`, `info`, `wand`, `pos1` and `pos2` children) to the `/git` root, plus a top-level `wand` alias. Attaching only builds the Brigadier tree; no world, repository or selection state is touched until a command actually executes.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `root` | `LiteralArgumentBuilder<FabricClientCommandSource>` | The `/git` literal builder that the `selection` subtree and the top-level `wand` alias are appended to. |
| `services` | [`GitServices`](../GitServices.md) | Shared services captured by the command callbacks for selections, staging, selection state, rendering, wand items and chat feedback. |

### Returns
`void`

---

## usage

### Signature
```java
private static int usage(FabricClientCommandSource source, GitServices services)
```

### Description
Prints the short `selection` command summary and the wand hint (left-click = corner A, right-click = corner B) as raw chat lines; it is the fallback when `/git selection` runs without a subcommand. It runs through `CommandSupport.run`, so a failure is reported as a chat error rather than escaping into the command dispatcher.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | Command source whose chat receives the summary; no player is required. |
| `services` | [`GitServices`](../GitServices.md) | Supplies the message helper used for raw feedback. |

### Returns
`int` — always 1 on success, or 0 if `CommandSupport.run` caught an exception while sending the lines.

---

## create

### Signature
```java
private static int create(FabricClientCommandSource source, GitServices services, String name)
```

### Description
Creates a selection owned by the issuing player in the current world, consumes any pending cuboid left by earlier wand clicks and adds it as the first cuboid, then makes the selection active. If the cuboid add fails, the just-created selection is deleted again and the cuboid is restored as pending so the player can retry; the success message reports the resulting origin.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | Command source; must belong to a player inside an open world. |
| `services` | [`GitServices`](../GitServices.md) | Provides the selection service, the per-player selection manager and chat feedback. |
| `name` | `String` | Name of the new selection; must be unique for this world and player, compared case-insensitively by `SelectionService.create`. |

### Returns
`int` — 1 on success; `CommandSupport.runInt` reports failures (unknown player, duplicate name, cuboid limits) as a chat error and returns 0.

---

## list

### Signature
```java
private static int list(FabricClientCommandSource source, GitServices services)
```

### Description
Prints every selection owned by the player in this world, one line each, with cuboid count, total volume, origin (`unset` when absent), a `[tracked]` marker and a leading `*` for the active selection. When the list is empty it explains how to create one and where to click instead.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | Command source; must belong to a player inside an open world. |
| `services` | [`GitServices`](../GitServices.md) | Provides the selection service, the active-selection lookup and chat feedback. |

### Returns
`int` — 1 even when no selections exist; 0 if listing or messaging fails.

---

## select

### Signature
```java
private static int select(FabricClientCommandSource source, GitServices services, String name)
```

### Description
Looks up the named selection and persists it as the active selection for this world and player via `SelectionManager.setActive`, which writes the name into the world's local repo metadata. Later commands that omit a name, and wand clicks that complete a cuboid, then target this selection.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | Command source; must belong to a player inside an open world. |
| `services` | [`GitServices`](../GitServices.md) | Provides the selection lookup, the selection manager and chat feedback. |
| `name` | `String` | Name of an existing selection owned by this player in this world; `SelectionException` is thrown when it does not exist. |

### Returns
`int` — 1 on success, 0 after an error is reported in chat.

---

## show

### Signature
```java
private static int show(FabricClientCommandSource source, GitServices services, String name)
```

### Description
Resolves the requested or active selection and asks `SelectionRenderer` to highlight it in the world for 8 seconds. A selection without cuboids is not highlighted; the player is told it has none yet, and the success message otherwise reports how many cuboids are shown.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | Command source; must belong to a player inside an open world. |
| `services` | [`GitServices`](../GitServices.md) | Provides selection lookup, the active selection and chat feedback; the renderer is static. |
| `name` | `String` | Optional selection name; when `null` the active selection is used. |

### Returns
`int` — 1 on success (including the "no cuboids" case), 0 after an error is reported in chat.

---

## add

### Signature
```java
private static int add(FabricClientCommandSource source, GitServices services, String name)
```

### Description
Consumes the pending cuboid produced by the last pair of wand corners and appends it to the resolved selection, then marks that selection active. With no pending cuboid it only explains that two corners must be clicked first; if `SelectionService.addCuboid` rejects the cuboid (cuboid or volume cap), the cuboid is put back as pending instead of being lost.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | Command source; must belong to a player inside an open world. |
| `services` | [`GitServices`](../GitServices.md) | Provides the pending-cuboid store, selection service, selection manager and chat feedback. |
| `name` | `String` | Optional target selection name; when `null` the active selection is used. |

### Returns
`int` — 1 on success or when no cuboid is pending, 0 after an error is reported in chat.

---

## remove

### Signature
```java
private static int remove(FabricClientCommandSource source, GitServices services, int index, String name)
```

### Description
Removes the cuboid at the given 1-based index from the resolved selection, then (through `CommandSupport.complete`) trims that selection's staged snapshot on the IO executor with `StagingService.restrictStaged` so blocks outside the remaining cuboids disappear from the next commit. The origin deliberately does not move; the success message reports the removed dimensions and the unchanged origin.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | Command source; must belong to a player inside an open world. |
| `services` | [`GitServices`](../GitServices.md) | Provides selection mutation, staging and chat feedback. |
| `index` | `int` | 1-based cuboid position accepted by the literal as `IntegerArgumentType.integer(1)`; `SelectionService` rejects values beyond the current count. |
| `name` | `String` | Optional selection name; when `null` the active selection is used. |

### Returns
`int` — 1 once the cuboid is removed and the async staging trim is scheduled, 0 after an error is reported in chat.

---

## reorigin

### Signature
```java
private static int reorigin(FabricClientCommandSource source, GitServices services, String name)
```

### Description
Recomputes the selection's origin from its current cuboids (failing when there are none) and rebases the staged snapshot to that origin on the IO executor via `StagingService.rebaseStaged`. The success message reports the previous origin, shown as `unset` when the selection had none, and the new one.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | Command source; must belong to a player inside an open world. |
| `services` | [`GitServices`](../GitServices.md) | Provides selection mutation, staging and chat feedback. |
| `name` | `String` | Optional selection name; when `null` the active selection is used. |

### Returns
`int` — 1 when the origin is updated and the staging rebase is scheduled, 0 after an error is reported in chat.

---

## delete

### Signature
```java
private static int delete(FabricClientCommandSource source, GitServices services, String name)
```

### Description
Deletes the named selection from local metadata, clears the active selection when it points at that name, and removes its staged snapshot asynchronously with `StagingService.unstage`. The lookup happens first, so an unknown name fails before anything is deleted or unstaged.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | Command source; must belong to a player inside an open world. |
| `services` | [`GitServices`](../GitServices.md) | Provides the selection store, active-selection state, staging and chat feedback. |
| `name` | `String` | Name of the existing selection to delete; required, there is no active-selection fallback. |

### Returns
`int` — 1 when the deletion and unstage are scheduled, 0 after an error is reported in chat.

---

## info

### Signature
```java
private static int info(FabricClientCommandSource source, GitServices services, String name)
```

### Description
Prints detailed, read-only information about the resolved selection: name with tracked/untracked flag, world id, origin, cuboid count and total volume, overall bounds when at least one cuboid exists, and one line per cuboid with its 1-based index and volume.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | Command source; must belong to a player inside an open world. |
| `services` | [`GitServices`](../GitServices.md) | Provides selection lookup, the active selection and chat feedback. |
| `name` | `String` | Optional selection name; when `null` the active selection is used. |

### Returns
`int` — 1 after printing, 0 after an error is reported in chat.

---

## wand

### Signature
```java
private static int wand(FabricClientCommandSource source, GitServices services)
```

### Description
Gives the player the configured wand item via `WandItem.give`. In single-player the item is placed in the inventory and a hint explains the two click actions; in multiplayer the server owns the inventory, so the command only warns the player to hold the configured `wandItem` while sneaking or to use `/git selection pos1` and `/git selection pos2`.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | Command source; must belong to a player so the inventory or warning target exists. |
| `services` | [`GitServices`](../GitServices.md) | Provides the mod config (`wandItem`), the wand grant path and chat feedback. |

### Returns
`int` — 1 whether the wand was granted or the multiplayer warning was sent; 0 if messaging fails.

---

## position

### Signature
```java
private static int position(FabricClientCommandSource source, GitServices services, boolean first)
```

### Description
Reads the client's crosshair `hitResult`; it must be a `BlockHitResult`, otherwise the player is told to look at a block within reach. The hit block is converted to a model `BlockPos` and passed to `WandEvents.handleCorner`, so `pos1` and `pos2` set exactly the same corners as a left or right wand click, including completing a cuboid once both corners exist.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | Command source; must belong to a player and provides the client used for the ray trace. |
| `services` | [`GitServices`](../GitServices.md) | Forwarded to `WandEvents.handleCorner` for selection state and feedback. |
| `first` | `boolean` | `true` for `pos1` (corner A), `false` for `pos2` (corner B). |

### Returns
`int` — 1 when a block was hit (the corner is recorded), 1 with a hint when nothing is in reach, 0 after an unexpected error.

---

## resolve

### Signature
```java
private static Selection resolve(GitServices services, WorldContext world, Player player, String requested)
```

### Description
Chooses the selection a command operates on: the non-blank `requested` name, otherwise `SelectionManager.active` for this world and player. When neither yields a name it throws `SelectionException` with a hint to pass a name or run `/git selection select`; `SelectionService.get` then throws for an unknown name.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `services` | [`GitServices`](../GitServices.md) | Provides the active-selection lookup and selection service used for resolution. |
| `world` | [`WorldContext`](../WorldContext.md) | Current world context supplying the repository key `world.id()`. |
| `player` | `Player` | Issuing player, used for the owner UUID and active-selection state. |
| `requested` | `String` | Explicit name from the command, or `null`/blank to fall back to the active selection. |

### Returns
[`Selection`](../../../../../../../main/java/org/almond/minecraftgit/model/Selection.md) — the existing selection matching the name, never null.

---

## requirePlayer

### Signature
```java
private static Player requirePlayer(FabricClientCommandSource source)
```

### Description
Returns `source.getPlayer()`, or throws `SelectionException("That command can only be run by a player.")` when the source has no player, which `CommandSupport.runInt` turns into a chat error.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | Command source to unwrap; sources such as server console or command blocks yield no player. |

### Returns
`Player` — the issuing client player, never null.

---

## world

### Signature
```java
private static WorldContext world(FabricClientCommandSource source)
```

### Description
Returns `WorldContext.current(source.getClient())`, the `world__dimension` identity used to key repositories and selection metadata. It throws a `UserFacingException` when no world is open, so every caller effectively requires being in-game.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | Command source providing the Minecraft client whose current level is described. |

### Returns
[`WorldContext`](../WorldContext.md) — id, display name, dimension key and single-player flag of the open world.

---

## names

### Signature
```java
private static SuggestionProvider<FabricClientCommandSource> names(GitServices services)
```

### Description
Builds a Brigadier suggestion provider that offers this player's selection names for the current world, feeding tab completion for every `name` argument. Any runtime failure during suggestion (no player, no world, unreadable metadata) is swallowed so command parsing never breaks, and the builder's completed future is returned.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `services` | [`GitServices`](../GitServices.md) | Provides the selection list consulted while building suggestions. |

### Returns
`SuggestionProvider<FabricClientCommandSource>` — provider that suggests each selection name, or nothing when the player/world is unavailable.

---

## See Also

- **Software Detailed Design:** [SelectionCommands_SDD.md](./SelectionCommands_SDD.md)
