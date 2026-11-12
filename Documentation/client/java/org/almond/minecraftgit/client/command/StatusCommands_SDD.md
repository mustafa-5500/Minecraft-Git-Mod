# StatusCommands — Software Detailed Design

> **API Documentation:** [StatusCommands.md](./StatusCommands.md)  
> **Source File:** [StatusCommands.java](../../../../../../../../src/client/java/org/almond/minecraftgit/client/command/StatusCommands.java)

## Table of Contents

- [Usage](#usage)
- [1. Overview](#1-overview)
- [2. Package Declaration & Imports](#2-package-declaration-imports)
- [3. Class Declaration](#3-class-declaration)
- [4. Constructor](#4-constructor)
- [5. `attach()`](#5-attach)
- [6. `status()`](#6-status)
- [7. `showStatus()`](#7-showstatus)
- [8. `diff()`](#8-diff)
- [9. `showDiff()`](#9-showdiff)
- [10. `showSamples()`](#10-showsamples)
- [11. `resolve()`](#11-resolve)
- [12. `requirePlayer()`](#12-requireplayer)
- [13. `world()`](#13-world)
- [14. `names()`](#14-names)

## Usage

`StatusCommands` is a static utility: `GitCommand` calls `attach(root, services)` once at client command registration, and the handlers use the shared `GitServices`. Usage is `/git status` and `/git diff [name]`.

---

## 1. Overview

`/git status` and `/git diff`.

---

## 2. Package Declaration & Imports

```java
package org.almond.minecraftgit.client.command;
```

```java
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.world.entity.player.Player;
import org.almond.minecraftgit.client.GitServices;
import org.almond.minecraftgit.client.WorldContext;
import org.almond.minecraftgit.client.world.WorldAccess;
import org.almond.minecraftgit.model.BlockSnapshot;
import org.almond.minecraftgit.model.BuildDiff;
import org.almond.minecraftgit.model.Selection;
import org.almond.minecraftgit.model.Snapshot;
import org.almond.minecraftgit.selection.SelectionException;
import org.almond.minecraftgit.status.StatusService;
import org.almond.minecraftgit.world.CaptureResult;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
```

| Import | Purpose |
|--------|---------|
| `StringArgumentType` | String argument type for the optional selection name of `/git diff`. |
| `LiteralArgumentBuilder` | Builder type for the `/git` command tree the subcommands are attached to. |
| `SuggestionProvider` | Return type of `names`, the selection-name autocomplete factory. |
| `ClientCommandManager` | Fabric client command factory (`literal`, `argument`) used to build the tree. |
| `FabricClientCommandSource` | Client command source carrying the player, client and feedback/error channels. |
| `Player` | The issuing player, supplying UUID, name and current block position. |
| [`GitServices`](../GitServices.md) | Service graph: repository manager, selections, staging, status, messages and the IO executor. |
| [`WorldContext`](../WorldContext.md) | World id and dimension plus the single-player flag used for capture routing. |
| [`WorldAccess`](../world/WorldAccess.md) | Chooses integrated-server versus client-thread capture for a selection. |
| [`BlockSnapshot`](../../../../../../../main/java/org/almond/minecraftgit/model/BlockSnapshot.md) | Per-block record (relative coordinates and block data) printed by `showSamples`. |
| [`BuildDiff`](../../../../../../../main/java/org/almond/minecraftgit/model/BuildDiff.md) | Diff result whose summaries and counts are rendered; referenced through `StatusService`, so this import is unused here. |
| [`Selection`](../../../../../../../main/java/org/almond/minecraftgit/model/Selection.md) | Selection record supplying name, world, cuboids and volume for filtering and display. |
| [`Snapshot`](../../../../../../../main/java/org/almond/minecraftgit/model/Snapshot.md) | Current capture snapshot stored per selection and compared against HEAD/staged by the status service. |
| [`SelectionException`](../../../../../../../main/java/org/almond/minecraftgit/selection/SelectionException.md) | User-facing error for a missing player or selection, reported by `CommandSupport`. |
| [`StatusService`](../../../../../../../main/java/org/almond/minecraftgit/status/StatusService.md) | Computes `Report` and `DiffReport` from HEAD, staged and current snapshots. |
| [`CaptureResult`](../../../../../../../main/java/org/almond/minecraftgit/world/CaptureResult.md) | Capture outcome: snapshot, skipped chunks/blocks and the `partial()` flag. |
| `Files` | `Files.isDirectory` checks whether a selection folder exists in HEAD. |
| `ArrayList` | Mutable list of relative cuboids in `addCuboids`. |
| `HashSet` | Name sets for selections that are too large or only partially captured. |
| `LinkedHashMap` | Insertion-ordered name-to-snapshot map so the report order matches capture order. |
| `List` | Selection, prepared-pull and remote-name lists. |
| `Map` | Name-to-snapshot map of current captures. |
| `Set` | Too-large and incomplete selection-name sets. |
| `CompletableFuture` | Sequential capture chain and asynchronous report delivery. |

---

## 3. Class Declaration

```java
public final class StatusCommands {
```

The class is `final` and holds only static members: selection and repository state live in `GitServices`, while the handler-local state (candidate list, capture chain, name sets) is created per invocation so concurrent commands cannot interfere.

---

## 4. Constructor

```java
private StatusCommands()
```

The private constructor prevents instantiation; the class is a namespace for the two command handlers and their helpers, while all state lives in `GitServices` and in the world's selections.

---

## 5. `attach()`

```java
public static void attach(LiteralArgumentBuilder<FabricClientCommandSource> root, GitServices services)
```

`attach` adds the argument-less `status` literal and the `diff` literal with an optional `name` argument to the `/git` root. Only the diff argument uses the `names(services)` suggestion provider; `status` always reports every selection.

---

## 6. `status()`

```java
private static int status(FabricClientCommandSource source, GitServices services)
```

The status chain is select → filter → capture → report. After the player/world checks, an empty selection list short-circuits with a hint. The filter drops selections with no cuboids and selections that are neither tracked, nor present in HEAD (`Files.isDirectory(repoPath/<name>)`), nor staged (`StagingService.hasStaged`); those above `config.statusMaxVolume` go into `tooLarge` and are not captured. Candidate captures are chained with `thenCompose` so `WorldAccess.capture` runs one at a time, and a `partial()` capture is recorded in `incomplete` while its snapshot is still passed on. `services.async` then computes `StatusService.report` off the client thread and `CommandSupport.completeFuture` renders it on the client thread; the report itself is built from the full selection list, so filtering only limits capture, not what is shown.

---

## 7. `showStatus()`

```java
private static void showStatus(FabricClientCommandSource source, GitServices services, WorldContext world,
                                   StatusService.Report report)
```

Renders `StatusService.Report` with prefix-less raw lines: a header with the world id and branch/HEAD (or `(no repository yet)`), a `[untracked]`/`[clean]`/`[dirty]` tag plus `cuboids=` and `volume=` per selection, indented staged and working diff summaries, any note at warn level, and a closing dirty/untracked/clean summary. Output goes through `Messages.raw` and `Messages.warn`.

---

## 8. `diff()`

```java
private static int diff(FabricClientCommandSource source, GitServices services, String requested)
```

Resolves the selection and compares `selection.volume()` with `statusMaxVolume`: when too large the capture is skipped and only the flag is passed to the service; otherwise `WorldAccess.capture` produces the snapshot and `partial()` becomes `incomplete`. `services.async` runs `StatusService.diff` on the IO executor and `completeFuture` delivers the `DiffReport` to `showDiff` on the client thread. Inside the service the three snapshots are compared pairwise: the staged index against HEAD, and the current capture against a base that is the staged snapshot when present, else HEAD, else an empty snapshot, which keeps the staged-vs-HEAD and current-vs-base diffs independent.

---

## 9. `showDiff()`

```java
private static void showDiff(FabricClientCommandSource source, GitServices services, Selection selection,
                                 StatusService.DiffReport report)
```

Prints the selection name and world, HEAD present/absent, staged yes/no, current captured/not captured, any note, then the staged-vs-HEAD and current-vs-base summaries, followed by sample blocks for added, changed and removed blocks taken from the working diff.

---

## 10. `showSamples()`

```java
private static void showSamples(FabricClientCommandSource source, String label,
                                    List<BlockSnapshot> blocks)
```

Prints at most six blocks with their relative coordinates and block data using `source.sendFeedback`, then an ellipsis line with the remaining count; an empty list prints nothing.

---

## 11. `resolve()`

```java
private static Selection resolve(GitServices services, WorldContext world, Player player, String requested)
```

Argument-first selection lookup with a fallback to `SelectionManager.active`: a blank name raises `SelectionException` asking the user to specify or select a selection, and the read is `SelectionService.get(world, owner, name)`.

---

## 12. `requirePlayer()`

```java
private static Player requirePlayer(FabricClientCommandSource source)
```

Guards against non-player sources; the thrown `SelectionException` is a `UserFacingException`, so `CommandSupport` reports it as a plain chat error.

---

## 13. `world()`

```java
private static WorldContext world(FabricClientCommandSource source)
```

Delegates to `WorldContext.current(client)` for the world id, dimension and single-player flag.

---

## 14. `names()`

```java
private static SuggestionProvider<FabricClientCommandSource> names(GitServices services)
```

Returns a suggestion provider that lists `SelectionService.list(world.id(), player.getUUID())` names; any runtime failure is caught so autocomplete can never break argument parsing.

---
