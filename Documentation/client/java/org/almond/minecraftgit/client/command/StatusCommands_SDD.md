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

TODO: Describe how `StatusCommands` is instantiated and used.

---

## 1. Overview

{@code /git status} and {@code /git diff}.

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
| `StringArgumentType` | TODO: describe purpose |
| `LiteralArgumentBuilder` | TODO: describe purpose |
| `SuggestionProvider` | TODO: describe purpose |
| `ClientCommandManager` | TODO: describe purpose |
| `FabricClientCommandSource` | TODO: describe purpose |
| `Player` | TODO: describe purpose |
| [`GitServices`](../GitServices.md) | TODO: describe purpose |
| [`WorldContext`](../WorldContext.md) | TODO: describe purpose |
| [`WorldAccess`](../world/WorldAccess.md) | TODO: describe purpose |
| [`BlockSnapshot`](../../../../../../../main/java/org/almond/minecraftgit/model/BlockSnapshot.md) | TODO: describe purpose |
| [`BuildDiff`](../../../../../../../main/java/org/almond/minecraftgit/model/BuildDiff.md) | TODO: describe purpose |
| [`Selection`](../../../../../../../main/java/org/almond/minecraftgit/model/Selection.md) | TODO: describe purpose |
| [`Snapshot`](../../../../../../../main/java/org/almond/minecraftgit/model/Snapshot.md) | TODO: describe purpose |
| [`SelectionException`](../../../../../../../main/java/org/almond/minecraftgit/selection/SelectionException.md) | TODO: describe purpose |
| [`StatusService`](../../../../../../../main/java/org/almond/minecraftgit/status/StatusService.md) | TODO: describe purpose |
| [`CaptureResult`](../../../../../../../main/java/org/almond/minecraftgit/world/CaptureResult.md) | TODO: describe purpose |
| `Files` | TODO: describe purpose |
| `ArrayList` | TODO: describe purpose |
| `HashSet` | TODO: describe purpose |
| `LinkedHashMap` | TODO: describe purpose |
| `List` | TODO: describe purpose |
| `Map` | TODO: describe purpose |
| `Set` | TODO: describe purpose |
| `CompletableFuture` | TODO: describe purpose |

---

## 3. Class Declaration

```java
public final class StatusCommands {
```


---

## 4. Constructor

```java
private StatusCommands()
```

TODO: Provide detailed design explanation for `StatusCommands`.

---

## 5. `attach()`

```java
public static void attach(LiteralArgumentBuilder<FabricClientCommandSource> root, GitServices services)
```

TODO: Provide detailed design explanation for `attach`.

---

## 6. `status()`

```java
private static int status(FabricClientCommandSource source, GitServices services)
```

TODO: Provide detailed design explanation for `status`.

---

## 7. `showStatus()`

```java
private static void showStatus(FabricClientCommandSource source, GitServices services, WorldContext world,
                                   StatusService.Report report)
```

TODO: Provide detailed design explanation for `showStatus`.

---

## 8. `diff()`

```java
private static int diff(FabricClientCommandSource source, GitServices services, String requested)
```

TODO: Provide detailed design explanation for `diff`.

---

## 9. `showDiff()`

```java
private static void showDiff(FabricClientCommandSource source, GitServices services, Selection selection,
                                 StatusService.DiffReport report)
```

TODO: Provide detailed design explanation for `showDiff`.

---

## 10. `showSamples()`

```java
private static void showSamples(FabricClientCommandSource source, String label,
                                    List<BlockSnapshot> blocks)
```

TODO: Provide detailed design explanation for `showSamples`.

---

## 11. `resolve()`

```java
private static Selection resolve(GitServices services, WorldContext world, Player player, String requested)
```

TODO: Provide detailed design explanation for `resolve`.

---

## 12. `requirePlayer()`

```java
private static Player requirePlayer(FabricClientCommandSource source)
```

TODO: Provide detailed design explanation for `requirePlayer`.

---

## 13. `world()`

```java
private static WorldContext world(FabricClientCommandSource source)
```

TODO: Provide detailed design explanation for `world`.

---

## 14. `names()`

```java
private static SuggestionProvider<FabricClientCommandSource> names(GitServices services)
```

TODO: Provide detailed design explanation for `names`.

---
