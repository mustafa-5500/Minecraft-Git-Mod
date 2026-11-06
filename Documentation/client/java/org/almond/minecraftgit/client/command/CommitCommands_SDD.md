# CommitCommands — Software Detailed Design

> **API Documentation:** [CommitCommands.md](./CommitCommands.md)  
> **Source File:** [CommitCommands.java](../../../../../../../../src/client/java/org/almond/minecraftgit/client/command/CommitCommands.java)

## Table of Contents

- [Usage](#usage)
- [1. Overview](#1-overview)
- [2. Package Declaration & Imports](#2-package-declaration-imports)
- [3. Class Declaration](#3-class-declaration)
- [4. Constructor](#4-constructor)
- [5. `attach()`](#5-attach)
- [6. `commit()`](#6-commit)
- [7. `showResult()`](#7-showresult)
- [8. `requirePlayer()`](#8-requireplayer)
- [9. `world()`](#9-world)

## Usage

TODO: Describe how `CommitCommands` is instantiated and used.

---

## 1. Overview

{@code /git commit [message]}: write all staged selections to the repository.

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
import net.minecraft.world.entity.player.Player;
import org.almond.minecraftgit.client.GitServices;
import org.almond.minecraftgit.client.WorldContext;
import org.almond.minecraftgit.commit.CommitRequest;
import org.almond.minecraftgit.commit.CommitResult;
import org.almond.minecraftgit.model.Selection;
import org.almond.minecraftgit.selection.SelectionException;
import java.util.ArrayList;
import java.util.List;
```

| Import | Purpose |
|--------|---------|
| `StringArgumentType` | TODO: describe purpose |
| `LiteralArgumentBuilder` | TODO: describe purpose |
| `ClientCommandManager` | TODO: describe purpose |
| `FabricClientCommandSource` | TODO: describe purpose |
| `Player` | TODO: describe purpose |
| [`GitServices`](../GitServices.md) | TODO: describe purpose |
| [`WorldContext`](../WorldContext.md) | TODO: describe purpose |
| [`CommitRequest`](../../../../../../../main/java/org/almond/minecraftgit/commit/CommitRequest.md) | TODO: describe purpose |
| [`CommitResult`](../../../../../../../main/java/org/almond/minecraftgit/commit/CommitResult.md) | TODO: describe purpose |
| [`Selection`](../../../../../../../main/java/org/almond/minecraftgit/model/Selection.md) | TODO: describe purpose |
| [`SelectionException`](../../../../../../../main/java/org/almond/minecraftgit/selection/SelectionException.md) | TODO: describe purpose |
| `ArrayList` | TODO: describe purpose |
| `List` | TODO: describe purpose |

---

## 3. Class Declaration

```java
public final class CommitCommands {
```


---

## 4. Constructor

```java
private CommitCommands()
```

TODO: Provide detailed design explanation for `CommitCommands`.

---

## 5. `attach()`

```java
public static void attach(LiteralArgumentBuilder<FabricClientCommandSource> root, GitServices services)
```

TODO: Provide detailed design explanation for `attach`.

---

## 6. `commit()`

```java
private static int commit(FabricClientCommandSource source, GitServices services,
                              String message, boolean allowEmpty)
```

TODO: Provide detailed design explanation for `commit`.

---

## 7. `showResult()`

```java
private static void showResult(FabricClientCommandSource source, GitServices services, CommitResult result)
```

TODO: Provide detailed design explanation for `showResult`.

---

## 8. `requirePlayer()`

```java
private static Player requirePlayer(FabricClientCommandSource source)
```

TODO: Provide detailed design explanation for `requirePlayer`.

---

## 9. `world()`

```java
private static WorldContext world(FabricClientCommandSource source)
```

TODO: Provide detailed design explanation for `world`.

---
