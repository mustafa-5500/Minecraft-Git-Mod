# PullCommands — Software Detailed Design

> **API Documentation:** [PullCommands.md](./PullCommands.md)  
> **Source File:** [PullCommands.java](../../../../../../../../src/client/java/org/almond/minecraftgit/client/command/PullCommands.java)

## Table of Contents

- [Usage](#usage)
- [1. Overview](#1-overview)
- [2. Package Declaration & Imports](#2-package-declaration-imports)
- [3. Class Declaration](#3-class-declaration)
- [4. Constructor](#4-constructor)
- [5. `attach()`](#5-attach)
- [6. `pull()`](#6-pull)
- [7. `onPlan()`](#7-onplan)
- [8. `applyNext()`](#8-applynext)
- [9. `ensureSelection()`](#9-ensureselection)
- [10. `addCuboids()`](#10-addcuboids)
- [11. `finishPull()`](#11-finishpull)
- [12. `fail()`](#12-fail)
- [13. `abort()`](#13-abort)
- [14. `checkout()`](#14-checkout)
- [15. `sendApplyResult()`](#15-sendapplyresult)
- [16. `resolve()`](#16-resolve)
- [17. `remoteFor()`](#17-remotefor)
- [18. `requirePlayer()`](#18-requireplayer)
- [19. `world()`](#19-world)
- [20. `shortId()`](#20-shortid)
- [21. `message()`](#21-message)

## Usage

`PullCommands` is never instantiated: `GitCommand` calls the static `attach(root, services)` once when client commands are registered, and each handler then receives the shared `GitServices`. Usage is through `/git pull [name]`, `/git pull --remote <remote> [name]`, `/git pull --abort [name]` and `/git checkout <commit> [name]`.

---

## 1. Overview

`/git pull` and `/git checkout`.
Pull is fast-forward-only and single-player-only for the apply step. The
local branch is moved only after every selected build has been written, so a
failed world write never leaves Git ahead of the world. A pre-apply backup is
written for each selection and can be restored with `--abort`.

---

## 2. Package Declaration & Imports

```java
package org.almond.minecraftgit.client.command;
```

```java
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.almond.minecraftgit.backup.BackupService;
import org.almond.minecraftgit.client.GitServices;
import org.almond.minecraftgit.client.WorldContext;
import org.almond.minecraftgit.model.BlockPos;
import org.almond.minecraftgit.model.Cuboid;
import org.almond.minecraftgit.model.Selection;
import org.almond.minecraftgit.model.Snapshot;
import org.almond.minecraftgit.repo.CommitMeta;
import org.almond.minecraftgit.repo.PullPlan;
import org.almond.minecraftgit.repo.RepoMetadata;
import org.almond.minecraftgit.repo.RepoProgress;
import org.almond.minecraftgit.repo.RepoService;
import org.almond.minecraftgit.selection.SelectionException;
import org.almond.minecraftgit.sync.PullException;
import org.almond.minecraftgit.sync.PullService;
import org.almond.minecraftgit.util.UserFacingException;
import org.almond.minecraftgit.world.ApplyPlan;
import org.almond.minecraftgit.world.ApplyResult;
import java.util.ArrayList;
import java.util.List;
```

| Import | Purpose |
|--------|---------|
| `StringArgumentType` | Brigadier string argument type for the selection-name, remote and revision arguments. |
| `LiteralArgumentBuilder` | Builder type for the `/git` command tree the subcommands are attached to. |
| `ClientCommandManager` | Fabric client command factory (`literal`, `argument`) used to build the tree. |
| `FabricClientCommandSource` | Client command source carrying the player, client and feedback/error channels. |
| `ChatFormatting` | Chat colour constants; not referenced here because all output goes through `Messages`. |
| `Minecraft` | Client instance used to schedule apply completion back onto the render thread. |
| `Component` | Chat component type; not referenced directly because `Messages` builds the components. |
| `Player` | The issuing player, supplying UUID, name and current block position. |
| [`BackupService`](../../../../../../../main/java/org/almond/minecraftgit/backup/BackupService.md) | Provides the `Backup` record (snapshot plus file) restored by `--abort`. |
| [`GitServices`](../GitServices.md) | Service graph: repositories, pull, apply, selections, backups, config, messages and the IO executor. |
| [`WorldContext`](../WorldContext.md) | World id and dimension plus the single-player check that gates apply/backup. |
| [`BlockPos`](../../../../../../../main/java/org/almond/minecraftgit/model/BlockPos.md) | Mod block coordinate type used for selection origins and the player's position. |
| [`Cuboid`](../../../../../../../main/java/org/almond/minecraftgit/model/Cuboid.md) | Build region used when adding cuboids to a pulled or checked-out selection. |
| [`Selection`](../../../../../../../main/java/org/almond/minecraftgit/model/Selection.md) | Local selection model supplying name, origin, cuboids and tracked state. |
| [`Snapshot`](../../../../../../../main/java/org/almond/minecraftgit/model/Snapshot.md) | Block/entity snapshot read from the remote commit and local HEAD, and used to derive cuboid bounds. |
| [`CommitMeta`](../../../../../../../main/java/org/almond/minecraftgit/repo/CommitMeta.md) | Commit metadata (origin, relative cuboids); read by `PullService`, so this import is unused here. |
| [`PullPlan`](../../../../../../../main/java/org/almond/minecraftgit/repo/PullPlan.md) | Fetch result with local/remote heads, merge base, branch and up-to-date/fast-forward/diverged type. |
| [`RepoMetadata`](../../../../../../../main/java/org/almond/minecraftgit/repo/RepoMetadata.md) | Per-world metadata providing the default remote name. |
| [`RepoProgress`](../../../../../../../main/java/org/almond/minecraftgit/repo/RepoProgress.md) | Fetch progress callback; `RepoProgress.NOOP` is passed to `planPull`. |
| [`RepoService`](../../../../../../../main/java/org/almond/minecraftgit/repo/RepoService.md) | Per-world repository: plans the pull, resolves revisions, exports selections and fast-forwards. |
| [`SelectionException`](../../../../../../../main/java/org/almond/minecraftgit/selection/SelectionException.md) | User-facing error for a missing player or selection, reported by `CommandSupport`. |
| [`PullException`](../../../../../../../main/java/org/almond/minecraftgit/sync/PullException.md) | User-facing pull/checkout error (multiplayer, divergence, unknown revision). |
| [`PullService`](../../../../../../../main/java/org/almond/minecraftgit/sync/PullService.md) | Prepares remote and local-HEAD snapshots in a temporary directory and cleans them up. |
| [`UserFacingException`](../../../../../../../main/java/org/almond/minecraftgit/util/UserFacingException.md) | Base type for errors reported verbatim by `CommandSupport`; not referenced directly here. |
| [`ApplyPlan`](../../../../../../../main/java/org/almond/minecraftgit/world/ApplyPlan.md) | Absolute placements and removals computed between two snapshots, including backup restores. |
| [`ApplyResult`](../../../../../../../main/java/org/almond/minecraftgit/world/ApplyResult.md) | Apply outcome (counts, backup file, warnings) reported after a world write. |
| `ArrayList` | Mutable list of relative cuboids in `addCuboids`. |
| `List` | Selection, prepared-pull and remote-name lists. |

---

## 3. Class Declaration

```java
public final class PullCommands {
```

The class is `final` and holds only static members: it owns no repository or world state, receives `GitServices` and the command source per invocation, and keeps the pull state machine explicit by passing the plan, prepared pulls and index through the handler chain.

---

## 4. Constructor

```java
private PullCommands()
```

The private constructor makes the class a pure static utility: there is no per-command state, and every dependency arrives through the parameters of `attach` and is passed down the handler chain. This keeps registration cheap and avoids holding a `Minecraft` or service reference in a static field.

---

## 5. `attach()`

```java
public static void attach(LiteralArgumentBuilder<FabricClientCommandSource> root, GitServices services)
```

`attach` adds four pull leaves (bare `pull`, `--abort` with optional `name`, `--remote` with required `remote` and optional `name`, and `name` alone) plus `checkout <commit> [name]` to the `/git` root. Argument order is fixed so `--remote` precedes the selection name, and every selection-name argument uses a suggestion provider built from `SelectionService.list` for the current player and world.

---

## 6. `pull()`

```java
private static int pull(FabricClientCommandSource source, GitServices services,
                            String onlySelection, String requestedRemote)
```

`pull` is the pipeline entry: it validates the player, single-player world and initialized repository, resolves the remote via `remoteFor`, snapshots the player's `BlockPos` origin and selection list, and sends `Fetching from ...`. It then runs `repo.planPull(remote, RepoProgress.NOOP)` on the IO executor through `CommandSupport.complete` and forwards the resulting `PullPlan` to `onPlan` on the client thread; validation failures are reported by `runInt`, which returns `0`.

---

## 7. `onPlan()`

```java
private static void onPlan(FabricClientCommandSource source, GitServices services, WorldContext world,
                               Player player, List<Selection> localSelections, BlockPos playerOrigin,
                               RepoService repo, PullPlan plan, String onlySelection)
```

`onPlan` branches on the plan type: `UP_TO_DATE` prints the remote/branch and stops, while `!canFastForward()` (a `DIVERGED` history) throws `PullException` because version 1 never merges — the message tells the user to push or clone into a fresh world. For `FAST_FORWARD` it announces the plan and calls `PullService.prepare` asynchronously; an empty prepared list (remote head with no selections) jumps to `finishPull`, otherwise `applyNext(..., 0)` starts the writes. `prepare` refuses a selection filter when the remote head contains more than one selection, because fast-forwarding the branch would otherwise leave the unapplied selections in HEAD but not in the world; partial pulls are therefore rejected in version 1.

---

## 8. `applyNext()`

```java
private static void applyNext(FabricClientCommandSource source, GitServices services, WorldContext world,
                                  Player player, RepoService repo, PullPlan plan,
                                  List<PullService.PreparedPull> prepared, int index)
```

`applyNext` drives the apply phase recursively. For the pull at `index` it obtains the selection through `ensureSelection`, builds `ApplyPlan.between(selection, localHead, localOrigin, target, localOrigin, true)` and submits it to `ApplyService.apply`, whose completion is rescheduled with `client.execute` so the result is handled on the render thread. A callback after the player left the world is dropped, an error routes to `fail` (aborting the whole pull and leaving the branch untouched), and success prints the result and recurses with `index + 1`; reaching `prepared.size()` calls `finishPull`. Synchronous `RuntimeException`s also go to `fail`, which is what makes a partial pull impossible.

---

## 9. `ensureSelection()`

```java
private static Selection ensureSelection(GitServices services, WorldContext world, Player player,
                                             PullService.PreparedPull prepared)
```

`ensureSelection` reuses `prepared.localSelection()` only if it still exists in `SelectionService`, otherwise it creates a selection at `prepared.localOrigin()` with the player's name. An existing selection without an origin receives the prepared local origin and is saved. When the selection has no cuboids, `addCuboids` reconstructs them from the commit metadata or target bounds, after which the selection is marked tracked and re-read so the caller sees the persisted state.

---

## 10. `addCuboids()`

```java
private static void addCuboids(GitServices services, WorldContext world, Player player,
                                   Selection selection, PullService.PreparedPull prepared, boolean created)
```

Relative cuboids come from `CommitMeta.relativeCuboids()`; when the commit carries none, the bounding box of the target snapshot's relative block coordinates is used as a single cuboid. Each cuboid is translated by `prepared.localOrigin()` and added through `SelectionService.addCuboid`. If an add fails and the selection was created by this pull, the selection is deleted before the exception is rethrown, so a failed pull leaves no half-built selection.

---

## 11. `finishPull()`

```java
private static void finishPull(FabricClientCommandSource source, GitServices services, WorldContext world,
                                   RepoService repo, PullPlan plan, List<PullService.PreparedPull> prepared)
```

`finishPull` runs only after every write succeeded: `repo.fastForward(plan)` and `PullService.cleanup(prepared)` execute together on the IO executor, and the client-thread callback reports the abbreviated remote head and number of selections. A `false` fast-forward means the build is in the world but the branch did not move, which is reported as an explicit error rather than success; an empty prepared list produces the 'remote has no selections' wording.

---

## 12. `fail()`

```java
private static void fail(FabricClientCommandSource source, GitServices services,
                             List<PullService.PreparedPull> prepared, Throwable error)
```

`fail` schedules `PullService.cleanup(prepared)` on the IO executor for best-effort temp-directory removal and immediately reports `Pull failed; the local branch was not moved.` with the error message. Because `fastForward` has not run, Git stays behind the world and the pull can be retried after the cause is fixed.

---

## 13. `abort()`

```java
private static int abort(FabricClientCommandSource source, GitServices services, String requested)
```

`abort` restores the newest pre-apply backup of one selection: it requires single-player, resolves the selection (argument or active), reads `BackupService.latest`, and builds `ApplyPlan.restore(selection, backup.snapshot())`, whose block snapshots already carry absolute coordinates. The apply future is delivered through `CommandSupport.completeFuture`, so `sendApplyResult` runs on the client thread; a missing backup raises `PullException`.

---

## 14. `checkout()`

```java
private static int checkout(FabricClientCommandSource source, GitServices services,
                                String revision, String requested)
```

`checkout` applies an arbitrary revision into an existing selection. It requires single-player and an initialized repository, resolves the selection (rejecting one without an origin) and the revision through `RepoService.resolve`, then prepares the commit snapshot with `PullService.prepareCheckout`. The plan uses `selection.origin()` as both the old and new origin so the local build keeps its position instead of jumping to the absolute origin recorded in the commit meta; the checkout temp directory is cleaned asynchronously after the result is delivered.

---

## 15. `sendApplyResult()`

```java
private static void sendApplyResult(FabricClientCommandSource source, GitServices services,
                                        ApplyResult result)
```

Formats the success line from `ApplyResult.summary()`, appending the backup file name when `result.backup()` is non-null, then prints at most the first five warnings at warn level. It assumes it already runs on the client thread, as guaranteed by the apply callbacks.

---

## 16. `resolve()`

```java
private static Selection resolve(GitServices services, WorldContext world, Player player, String requested)
```

Shared selection lookup for `--abort` and `checkout`: a non-blank explicit argument wins, otherwise `SelectionManager.active(world, player)` is used. A still-blank name raises `SelectionException` telling the user to specify or select one; the actual read is `SelectionService.get`, which throws for unknown names.

---

## 17. `remoteFor()`

```java
private static String remoteFor(GitServices services, WorldContext world, String requested)
```

Reads `RepoManager.metadata(world.id())` and uses its `remoteName` when set, falling back to `ModConfig.defaultRemote`. An explicit `--remote` value short-circuits the metadata lookup entirely.

---

## 18. `requirePlayer()`

```java
private static Player requirePlayer(FabricClientCommandSource source)
```

Rejects sources without a player (console, command blocks) with a `SelectionException`; because that is a `UserFacingException`, `CommandSupport.handleError` turns it into a red chat error instead of a stack-trace message.

---

## 19. `world()`

```java
private static WorldContext world(FabricClientCommandSource source)
```

Thin wrapper over `WorldContext.current(source.getClient())`; the returned context supplies the world id used for repository and selection lookups, the dimension's `ServerLevel` and the single-player flag that gates apply and backup.

---

## 20. `shortId()`

```java
private static String shortId(org.eclipse.jgit.lib.ObjectId id)
```

Abbreviates an `ObjectId` to eight hex characters (`abbreviate(8)`), or `?` for a null head, keeping chat lines short.

---

## 21. `message()`

```java
private static String message(Throwable error)
```

Extracts `error.getMessage()`, substituting the simple class name when the message is null so the failure line is never empty.

---
