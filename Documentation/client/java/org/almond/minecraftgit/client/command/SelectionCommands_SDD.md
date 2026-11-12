# SelectionCommands — Software Detailed Design

> **API Documentation:** [SelectionCommands.md](./SelectionCommands.md)  
> **Source File:** [SelectionCommands.java](../../../../../../../../src/client/java/org/almond/minecraftgit/client/command/SelectionCommands.java)

## Table of Contents

- [Usage](#usage)
- [1. Overview](#1-overview)
- [2. Package Declaration & Imports](#2-package-declaration-imports)
- [3. Class Declaration](#3-class-declaration)
- [4. Constructor](#4-constructor)
- [5. `attach()`](#5-attach)
- [6. `usage()`](#6-usage)
- [7. `create()`](#7-create)
- [8. `list()`](#8-list)
- [9. `select()`](#9-select)
- [10. `show()`](#10-show)
- [11. `add()`](#11-add)
- [12. `remove()`](#12-remove)
- [13. `reorigin()`](#13-reorigin)
- [14. `delete()`](#14-delete)
- [15. `info()`](#15-info)
- [16. `wand()`](#16-wand)
- [17. `position()`](#17-position)
- [18. `resolve()`](#18-resolve)
- [19. `requirePlayer()`](#19-requireplayer)
- [20. `world()`](#20-world)
- [21. `names()`](#21-names)

## Usage

`GitCommand.register` calls `attach` with the shared `/git` literal and the session's `GitServices`; the class is never instantiated and keeps no state between commands. Each subcommand validates the player and world, delegates state changes to `SelectionService`/`SelectionManager`/`StagingService`, and reports through the message helper, with slow staging work delivered by `CommandSupport.complete` on the IO executor and results applied back on the client thread.

---

## 1. Overview

`/git selection ...`: create and manage named multi-cuboid selections.

The class is a thin orchestration layer over three kinds of state: durable selection definitions in `SelectionService`, ephemeral wand corners and the pending cuboid in `SelectionManager`, and the remembered active selection in the world's local repo metadata. `create`, `add`, `remove`, `reorigin` and `delete` mutate selection state; `show` and `info` are read-only; `wand` and `position` feed the same corner pipeline used by in-world wand clicks.

---

## 2. Package Declaration & Imports

```java
package org.almond.minecraftgit.client.command;
```

```java
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import org.almond.minecraftgit.client.GitServices;
import org.almond.minecraftgit.client.WorldContext;
import org.almond.minecraftgit.client.selection.SelectionManager;
import org.almond.minecraftgit.client.selection.SelectionRenderer;
import org.almond.minecraftgit.client.selection.WandEvents;
import org.almond.minecraftgit.client.selection.WandItem;
import org.almond.minecraftgit.model.BlockPos;
import org.almond.minecraftgit.model.Cuboid;
import org.almond.minecraftgit.model.Selection;
import org.almond.minecraftgit.selection.SelectionException;
import java.util.List;
import java.util.Locale;
```

| Import | Purpose |
|--------|---------|
| `IntegerArgumentType` | Parses the `index` argument of `remove` as an integer of at least 1. |
| `StringArgumentType` | Declares quoted-string `name` arguments and reads their values with `getString`. |
| `LiteralArgumentBuilder` | Type of the `/git` root builder that the command subtree is attached to. |
| `SuggestionProvider` | Return type of `names`, installed on `name` arguments through `suggests`. |
| `ClientCommandManager` | Fabric factory that builds client-side literals and arguments so commands never touch the server dispatcher. |
| `FabricClientCommandSource` | Command source carrying the client, player and chat output used by every branch. |
| `Minecraft` | Client handle whose `hitResult` is inspected by `position`. |
| `Component` | Declared by the generator's import block but not referenced by this file; all chat goes through `GitServices.messages()`. |
| `Player` | The issuing player, supplying UUID, name and the wand corner session. |
| `BlockHitResult` | Pattern-matched in `position` before the targeted block position is read. |
| [`GitServices`](../GitServices.md) | Service locator that supplies selections, staging, selection state and messaging to every callback. |
| [`WorldContext`](../WorldContext.md) | Resolves the current `world__dimension` key used for all selection lookups. |
| [`SelectionManager`](../selection/SelectionManager.md) | Holds the two wand corners, the pending cuboid and the active selection name. |
| [`SelectionRenderer`](../selection/SelectionRenderer.md) | Draws the temporary 8-second highlight used by `show`. |
| [`WandEvents`](../selection/WandEvents.md) | Shared corner handler invoked by `pos1`/`pos2` so command and wand paths behave identically. |
| [`WandItem`](../selection/WandItem.md) | Grants the wand in `wand` and knows the configured item identity. |
| [`BlockPos`](../../../../../../../main/java/org/almond/minecraftgit/model/BlockPos.md) | Immutable model position built from the vanilla block position before corners are recorded. |
| [`Cuboid`](../../../../../../../main/java/org/almond/minecraftgit/model/Cuboid.md) | Pending cuboid type moved between `SelectionManager` and the selection, and printed by `info`. |
| [`Selection`](../../../../../../../main/java/org/almond/minecraftgit/model/Selection.md) | The named unit resolved and mutated by most commands. |
| [`SelectionException`](../../../../../../../main/java/org/almond/minecraftgit/selection/SelectionException.md) | Thrown for user-correctable failures (no player, no active selection, unknown name) and rendered as chat errors. |
| `List` | Iterates selections in `list` and cuboids in `info`. |
| `Locale` | Declared by the generator's import block but not referenced; name comparison uses case-insensitive equality on the service side. |

---

## 3. Class Declaration

```java
public final class SelectionCommands {
```

`public final` with a private constructor marks a stateless utility module: there is no instance state, and `attach` installs every callback as a static method reference bound to the supplied `GitServices`. All persistence is delegated to the selection metadata store and the repo metadata store, so the command class can be reloaded or re-attached without migrating anything.

---

## 4. Constructor

```java
private SelectionCommands()
```

Never invoked. The private no-argument constructor exists only to suppress the implicit public constructor; all command behaviour is registered statically through `attach`, and the instance `Returns` value in the API documentation is a formality.

---

## 5. `attach()`

```java
public static void attach(LiteralArgumentBuilder<FabricClientCommandSource> root, GitServices services)
```

Builds the whole `/git selection` tree in one fluent Brigadier expression and appends it to `root`, plus a second top-level `wand` literal so `/git wand` works without the `selection` prefix. The tree is:

- `/git selection` → `usage`
- `/git selection create <name>` → `create`
- `/git selection list` → `list`
- `/git selection select <name>` → `select` (with `names` suggestions)
- `/git selection show [name]` → `show`
- `/git selection add [name]` → `add`
- `/git selection remove <index> [name]` → `remove`
- `/git selection reorigin [name]` → `reorigin`
- `/git selection delete <name>` → `delete`
- `/git selection info [name]` → `info`
- `/git selection wand` → `wand`
- `/git selection pos1` / `pos2` → `position(first = true|false)`
- `/git wand` → `wand`

Every optional `name` argument carries `names(services)` as its suggestion provider. Registration captures `services` in each lambda but performs no I/O, world lookup or repository access; failures therefore happen only when a command executes and are handled by `CommandSupport`.

---

## 6. `usage()`

```java
private static int usage(FabricClientCommandSource source, GitServices services)
```

Fallback executor for `/git selection`. It emits seven fixed help lines (header, five usage lines and the wand hint) covering create/list/select/info/add/remove/reorigin/delete/show/wand/pos1/pos2, ending with `left-click = corner A, right-click = corner B`. It runs inside `CommandSupport.run`, so a messaging failure is reported rather than propagating, and returns 1 on the normal path.

---

## 7. `create()`

```java
private static int create(FabricClientCommandSource source, GitServices services, String name)
```

Resolve player and world, then take the pending cuboid from `SelectionManager` before creating the selection. The ordering is deliberate: `SelectionService.create` writes the new selection to disk and can fail on a duplicate name, and the pending cuboid must survive such a failure. After a successful create, a non-null pending cuboid is added as the first cuboid; if that add exceeds the cuboid or volume caps the selection is deleted again, `selection` is reset to null and the exception is rethrown so the outer catch can restore the cuboid with `setPending`. Finally `manager.setActive` records the new name as the active selection, and the success message mentions the pending cuboid and the origin (seeded from the first cuboid's smallest corner).

Data flow: `SelectionManager.takePending` → `SelectionService.create` → `SelectionService.addCuboid` (optional) → `SelectionManager.setActive` → `Messages.success`.

---

## 8. `list()`

```java
private static int list(FabricClientCommandSource source, GitServices services)
```

Read-only. Fetches `SelectionService.list(world.id(), player.getUUID())` (an immutable copy loaded from the per-player JSON on first access), prints an empty-state hint pointing at `/git wand` when nothing exists, and otherwise prefixes the header with the world id. The active name is read once from `SelectionManager.active` and compared case-insensitively to decide the `*` marker; each row shows cuboid count, volume, `origin` or `unset`, and `[tracked]` when the selection metadata marks it tracked.

---

## 9. `select()`

```java
private static int select(FabricClientCommandSource source, GitServices services, String name)
```

Looks the selection up with `SelectionService.get` (case-insensitive, throwing `SelectionException` when absent) and then calls `SelectionManager.setActive`. That call persists only `name` through `RepoManager.setLastSelection`, which rewrites the world's local `RepoMetadata` JSON rather than the selection file. Persistence is what makes the active selection survive a restart; `SelectionManager.active` revalidates the remembered name against the selection list on every read, so a deleted or renamed selection silently degrades to "no active selection" instead of failing later commands.

---

## 10. `show()`

```java
private static int show(FabricClientCommandSource source, GitServices services, String name)
```

Resolves the target through `resolve` (argument or active selection) and, when it has at least one cuboid, calls `SelectionRenderer.highlight(world.id(), selection.name(), 8)`. The renderer owns a timed highlight keyed by world and selection name, so no state is stored here; a selection with no cuboids is reported instead of highlighted, avoiding a highlight for an empty box. The success message reports the cuboid count.

---

## 11. `add()`

```java
private static int add(FabricClientCommandSource source, GitServices services, String name)
```

Wand-side completion normally adds cuboids directly once an active selection exists; this command handles the complementary case where the cuboid was left pending (no active selection, or a rejected add). It takes the pending cuboid first, returns early with an instruction when there is none, then resolves the target selection and calls `SelectionService.addCuboid`. Because consumption happens before resolution, a resolve failure (no name given and no active selection) is reported after the cuboid was taken; only the `addCuboid` failure path restores it with `setPending` before rethrowing. On success the selection becomes active so the next wand click continues appending there, and the message reports `pending.dimensions()` and the updated cuboid count.

---

## 12. `remove()`

```java
private static int remove(FabricClientCommandSource source, GitServices services, int index, String name)
```

Removes a cuboid synchronously (the `Selection` object is mutated in place and persisted by `SelectionService.removeCuboid`) and then schedules staging cleanup through `CommandSupport.complete`. The blocking part, `StagingService.restrictStaged(world.id(), owner, selection)`, runs on `GitServices.io()`: it reads the staged snapshot, keeps only blocks inside the remaining cuboids (`Snapshot.retain`) and rebases it to the unchanged origin, then rewrites the file. Because `removeCuboid` keeps the origin fixed (E2), the rebase target is still `selection.origin()`. The success consumer runs back on the client thread and reports the removed dimensions and the preserved origin; if the player left the world meanwhile the result is dropped by `CommandSupport`.

---

## 13. `reorigin()`

```java
private static int reorigin(FabricClientCommandSource source, GitServices services, String name)
```

`SelectionService.reorigin` recomputes the origin from the smallest corner of the current cuboids and returns the previous origin, failing when the selection is empty. The command then rebases the staged snapshot asynchronously with `StagingService.rebaseStaged(..., selection.name(), selection.origin())`, which returns immediately when no staged file exists. This keeps previously staged block coordinates consistent with the new origin so a later commit is not offset. The completion message distinguishes a previous origin of `null` as `unset`.

---

## 14. `delete()`

```java
private static int delete(FabricClientCommandSource source, GitServices services, String name)
```

Fetches the selection first (so an unknown name fails before any side effect), deletes it through `SelectionService.delete`, and clears the active pointer when `SelectionManager.active` matches the deleted name (writing an empty last-selection value). It then schedules `StagingService.unstage` on the IO executor to delete the selection's staged snapshot, so a recreate with the same name starts clean. The success message is sent from the client-thread consumer.

---

## 15. `info()`

```java
private static int info(FabricClientCommandSource source, GitServices services, String name)
```

Purely diagnostic output for the resolved selection: header with tracked/untracked state, then world id, origin, cuboid count and volume, and finally overall bounds plus one numbered line per cuboid (1-based index, `Cuboid.toString`, per-cuboid volume). Bounds are skipped for an empty selection because `boundsMin`/`boundsMax` are only meaningful with at least one cuboid. No state is modified.

---

## 16. `wand()`

```java
private static int wand(FabricClientCommandSource source, GitServices services)
```

Delegates to `WandItem.give(player, services.config(), source.getClient())`, which succeeds only when the client can safely add the item (single-player inventory); the boolean result selects between a success hint and a multiplayer warning. In multiplayer the server owns the inventory, so the warning tells the player to hold the configured `config().wandItem` while sneaking (the sneaking check is in `WandItem.isWand`) or to use `pos1`/`pos2`. The command itself never mutates selection state.

---

## 17. `position()`

```java
private static int position(FabricClientCommandSource source, GitServices services, boolean first)
```

Command-line equivalent of a wand click. It inspects `client.hitResult`, requires it to be a `BlockHitResult` (otherwise one hint message and return), converts the vanilla `BlockPos` into the model `BlockPos`, and calls the same `WandEvents.handleCorner(services, player, pos, first)` entry point used by the attack/use callbacks. That shared path sets corner A or B on the player's `SelectionManager.Session`, shows an action-bar confirmation for the first corner, and on the second corner either adds the cuboid to the active selection or stores it as pending. `first` is `true` for `pos1` and `false` for `pos2`.

---

## 18. `resolve()`

```java
private static Selection resolve(GitServices services, WorldContext world, Player player, String requested)
```

Central name resolution for commands with an optional argument: `requested` wins when non-blank, otherwise `SelectionManager.active(world, player)` supplies the persisted active name. When both are blank it throws `SelectionException` telling the player to name a selection or run `/git selection select <name>`; otherwise it defers to `SelectionService.get`, which performs case-insensitive lookup and throws the canonical "No selection named ..." error. The method never returns null and has no side effects.

---

## 19. `requirePlayer()`

```java
private static Player requirePlayer(FabricClientCommandSource source)
```

Guard for commands that need an owner UUID or an inventory. It reads `source.getPlayer()` and throws `SelectionException` when it is null; `CommandSupport.runInt` catches the exception and prints it as a chat error, so callers do not need their own check. This is separate from `CommandSupport.requirePlayer` (which returns the source) because selection commands need the `Player` object itself.

---

## 20. `world()`

```java
private static WorldContext world(FabricClientCommandSource source)
```

One-line adapter over `WorldContext.current(source.getClient())`. The resulting context keys selections as `<world>__<dimension>`, so the same build in the Overworld and Nether never shares coordinates; the call throws `UserFacingException` when no level is open, which the error handler turns into the standard "Open a world before using /git." message.

---

## 21. `names()`

```java
private static SuggestionProvider<FabricClientCommandSource> names(GitServices services)
```

Returns a lambda suggestion provider installed on every optional/required `name` argument. On each keystroke it re-reads the client, player and world, lists the player's selections for that world and suggests each name; the whole body is wrapped in `try/catch (RuntimeException)` because Brigadier must always get a future back and suggestion code runs during parsing, where an exception would abort command entry. It always returns `builder.buildFuture()`, even when nothing was suggested.

---
