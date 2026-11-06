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

TODO: Describe how `PullCommands` is instantiated and used.

---

## 1. Overview

{@code /git pull} and {@code /git checkout}.
<p>Pull is fast-forward-only and single-player-only for the apply step. The
local branch is moved only after every selected build has been written, so a
failed world write never leaves Git ahead of the world. A pre-apply backup is
written for each selection and can be restored with {@code --abort}.</p>

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
| `StringArgumentType` | TODO: describe purpose |
| `LiteralArgumentBuilder` | TODO: describe purpose |
| `ClientCommandManager` | TODO: describe purpose |
| `FabricClientCommandSource` | TODO: describe purpose |
| `ChatFormatting` | TODO: describe purpose |
| `Minecraft` | TODO: describe purpose |
| `Component` | TODO: describe purpose |
| `Player` | TODO: describe purpose |
| [`BackupService`](../../../../../../../main/java/org/almond/minecraftgit/backup/BackupService.md) | TODO: describe purpose |
| [`GitServices`](../GitServices.md) | TODO: describe purpose |
| [`WorldContext`](../WorldContext.md) | TODO: describe purpose |
| [`BlockPos`](../../../../../../../main/java/org/almond/minecraftgit/model/BlockPos.md) | TODO: describe purpose |
| [`Cuboid`](../../../../../../../main/java/org/almond/minecraftgit/model/Cuboid.md) | TODO: describe purpose |
| [`Selection`](../../../../../../../main/java/org/almond/minecraftgit/model/Selection.md) | TODO: describe purpose |
| [`Snapshot`](../../../../../../../main/java/org/almond/minecraftgit/model/Snapshot.md) | TODO: describe purpose |
| [`CommitMeta`](../../../../../../../main/java/org/almond/minecraftgit/repo/CommitMeta.md) | TODO: describe purpose |
| [`PullPlan`](../../../../../../../main/java/org/almond/minecraftgit/repo/PullPlan.md) | TODO: describe purpose |
| [`RepoMetadata`](../../../../../../../main/java/org/almond/minecraftgit/repo/RepoMetadata.md) | TODO: describe purpose |
| [`RepoProgress`](../../../../../../../main/java/org/almond/minecraftgit/repo/RepoProgress.md) | TODO: describe purpose |
| [`RepoService`](../../../../../../../main/java/org/almond/minecraftgit/repo/RepoService.md) | TODO: describe purpose |
| [`SelectionException`](../../../../../../../main/java/org/almond/minecraftgit/selection/SelectionException.md) | TODO: describe purpose |
| [`PullException`](../../../../../../../main/java/org/almond/minecraftgit/sync/PullException.md) | TODO: describe purpose |
| [`PullService`](../../../../../../../main/java/org/almond/minecraftgit/sync/PullService.md) | TODO: describe purpose |
| [`UserFacingException`](../../../../../../../main/java/org/almond/minecraftgit/util/UserFacingException.md) | TODO: describe purpose |
| [`ApplyPlan`](../../../../../../../main/java/org/almond/minecraftgit/world/ApplyPlan.md) | TODO: describe purpose |
| [`ApplyResult`](../../../../../../../main/java/org/almond/minecraftgit/world/ApplyResult.md) | TODO: describe purpose |
| `ArrayList` | TODO: describe purpose |
| `List` | TODO: describe purpose |

---

## 3. Class Declaration

```java
public final class PullCommands {
```


---

## 4. Constructor

```java
private PullCommands()
```

TODO: Provide detailed design explanation for `PullCommands`.

---

## 5. `attach()`

```java
public static void attach(LiteralArgumentBuilder<FabricClientCommandSource> root, GitServices services)
```

TODO: Provide detailed design explanation for `attach`.

---

## 6. `pull()`

```java
private static int pull(FabricClientCommandSource source, GitServices services,
                            String onlySelection, String requestedRemote)
```

TODO: Provide detailed design explanation for `pull`.

---

## 7. `onPlan()`

```java
private static void onPlan(FabricClientCommandSource source, GitServices services, WorldContext world,
                               Player player, List<Selection> localSelections, BlockPos playerOrigin,
                               RepoService repo, PullPlan plan, String onlySelection)
```

TODO: Provide detailed design explanation for `onPlan`.

---

## 8. `applyNext()`

```java
private static void applyNext(FabricClientCommandSource source, GitServices services, WorldContext world,
                                  Player player, RepoService repo, PullPlan plan,
                                  List<PullService.PreparedPull> prepared, int index)
```

TODO: Provide detailed design explanation for `applyNext`.

---

## 9. `ensureSelection()`

```java
private static Selection ensureSelection(GitServices services, WorldContext world, Player player,
                                             PullService.PreparedPull prepared)
```

TODO: Provide detailed design explanation for `ensureSelection`.

---

## 10. `addCuboids()`

```java
private static void addCuboids(GitServices services, WorldContext world, Player player,
                                   Selection selection, PullService.PreparedPull prepared, boolean created)
```

TODO: Provide detailed design explanation for `addCuboids`.

---

## 11. `finishPull()`

```java
private static void finishPull(FabricClientCommandSource source, GitServices services, WorldContext world,
                                   RepoService repo, PullPlan plan, List<PullService.PreparedPull> prepared)
```

TODO: Provide detailed design explanation for `finishPull`.

---

## 12. `fail()`

```java
private static void fail(FabricClientCommandSource source, GitServices services,
                             List<PullService.PreparedPull> prepared, Throwable error)
```

TODO: Provide detailed design explanation for `fail`.

---

## 13. `abort()`

```java
private static int abort(FabricClientCommandSource source, GitServices services, String requested)
```

TODO: Provide detailed design explanation for `abort`.

---

## 14. `checkout()`

```java
private static int checkout(FabricClientCommandSource source, GitServices services,
                                String revision, String requested)
```

TODO: Provide detailed design explanation for `checkout`.

---

## 15. `sendApplyResult()`

```java
private static void sendApplyResult(FabricClientCommandSource source, GitServices services,
                                        ApplyResult result)
```

TODO: Provide detailed design explanation for `sendApplyResult`.

---

## 16. `resolve()`

```java
private static Selection resolve(GitServices services, WorldContext world, Player player, String requested)
```

TODO: Provide detailed design explanation for `resolve`.

---

## 17. `remoteFor()`

```java
private static String remoteFor(GitServices services, WorldContext world, String requested)
```

TODO: Provide detailed design explanation for `remoteFor`.

---

## 18. `requirePlayer()`

```java
private static Player requirePlayer(FabricClientCommandSource source)
```

TODO: Provide detailed design explanation for `requirePlayer`.

---

## 19. `world()`

```java
private static WorldContext world(FabricClientCommandSource source)
```

TODO: Provide detailed design explanation for `world`.

---

## 20. `shortId()`

```java
private static String shortId(org.eclipse.jgit.lib.ObjectId id)
```

TODO: Provide detailed design explanation for `shortId`.

---

## 21. `message()`

```java
private static String message(Throwable error)
```

TODO: Provide detailed design explanation for `message`.

---
