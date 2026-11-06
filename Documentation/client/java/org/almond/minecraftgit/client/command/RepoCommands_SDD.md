# RepoCommands — Software Detailed Design

> **API Documentation:** [RepoCommands.md](./RepoCommands.md)  
> **Source File:** [RepoCommands.java](../../../../../../../../src/client/java/org/almond/minecraftgit/client/command/RepoCommands.java)

## Table of Contents

- [Usage](#usage)
- [1. Overview](#1-overview)
- [2. Package Declaration & Imports](#2-package-declaration-imports)
- [3. Class Declaration](#3-class-declaration)
- [4. Instance Fields](#4-instance-fields)
- [5. Constructor](#5-constructor)
- [6. `attach()`](#6-attach)
- [7. `attachConfig()`](#7-attachconfig)
- [8. `attachRemote()`](#8-attachremote)
- [9. `init()`](#9-init)
- [10. `repo()`](#10-repo)
- [11. `showRepo()`](#11-showrepo)
- [12. `setAuthor()`](#12-setauthor)
- [13. `remoteAdd()`](#13-remoteadd)
- [14. `remoteRemove()`](#14-remoteremove)
- [15. `remoteList()`](#15-remotelist)
- [16. `log()`](#16-log)
- [17. `showLog()`](#17-showlog)
- [18. `push()`](#18-push)
- [19. `fetch()`](#19-fetch)
- [20. `currentWorld()`](#20-currentworld)
- [21. `shortId()`](#21-shortid)

## Usage

TODO: Describe how `RepoCommands` is instantiated and used.

---

## 1. Overview

Repository lifecycle and transport commands.

---

## 2. Package Declaration & Imports

```java
package org.almond.minecraftgit.client.command;
```

```java
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.server.level.ServerPlayer;
import org.almond.minecraftgit.client.GitServices;
import org.almond.minecraftgit.client.WorldContext;
import org.almond.minecraftgit.repo.CommitInfo;
import org.almond.minecraftgit.repo.PullPlan;
import org.almond.minecraftgit.repo.RemoteInfo;
import org.almond.minecraftgit.repo.RepoInfo;
import org.almond.minecraftgit.repo.RepoMetadata;
import org.almond.minecraftgit.repo.RepoProgress;
import org.almond.minecraftgit.repo.RepoService;
import org.almond.minecraftgit.util.Names;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
```

| Import | Purpose |
|--------|---------|
| `IntegerArgumentType` | TODO: describe purpose |
| `StringArgumentType` | TODO: describe purpose |
| `LiteralArgumentBuilder` | TODO: describe purpose |
| `ClientCommandManager` | TODO: describe purpose |
| `FabricClientCommandSource` | TODO: describe purpose |
| `ServerPlayer` | TODO: describe purpose |
| [`GitServices`](../GitServices.md) | TODO: describe purpose |
| [`WorldContext`](../WorldContext.md) | TODO: describe purpose |
| [`CommitInfo`](../../../../../../../main/java/org/almond/minecraftgit/repo/CommitInfo.md) | TODO: describe purpose |
| [`PullPlan`](../../../../../../../main/java/org/almond/minecraftgit/repo/PullPlan.md) | TODO: describe purpose |
| [`RemoteInfo`](../../../../../../../main/java/org/almond/minecraftgit/repo/RemoteInfo.md) | TODO: describe purpose |
| [`RepoInfo`](../../../../../../../main/java/org/almond/minecraftgit/repo/RepoInfo.md) | TODO: describe purpose |
| [`RepoMetadata`](../../../../../../../main/java/org/almond/minecraftgit/repo/RepoMetadata.md) | TODO: describe purpose |
| [`RepoProgress`](../../../../../../../main/java/org/almond/minecraftgit/repo/RepoProgress.md) | TODO: describe purpose |
| [`RepoService`](../../../../../../../main/java/org/almond/minecraftgit/repo/RepoService.md) | TODO: describe purpose |
| [`Names`](../../../../../../../main/java/org/almond/minecraftgit/util/Names.md) | TODO: describe purpose |
| `ZoneId` | TODO: describe purpose |
| `DateTimeFormatter` | TODO: describe purpose |
| `List` | TODO: describe purpose |

---

## 3. Class Declaration

```java
public final class RepoCommands {
```


---

## 4. Instance Fields

```java
private static final DateTimeFormatter DATE;
```

| Field | Type | Description |
|-------|------|-------------|
| `DATE` | `DateTimeFormatter` | TODO: describe |

---

## 5. Constructor

```java
private RepoCommands()
```

TODO: Provide detailed design explanation for `RepoCommands`.

---

## 6. `attach()`

```java
public static void attach(LiteralArgumentBuilder<FabricClientCommandSource> root, GitServices services)
```

TODO: Provide detailed design explanation for `attach`.

---

## 7. `attachConfig()`

```java
public static void attachConfig(LiteralArgumentBuilder<FabricClientCommandSource> config,
                                    GitServices services)
```

Adds the author subcommand to the config literal built by {@link GitCommand}.

---

## 8. `attachRemote()`

```java
private static void attachRemote(LiteralArgumentBuilder<FabricClientCommandSource> root,
                                     GitServices services)
```

TODO: Provide detailed design explanation for `attachRemote`.

---

## 9. `init()`

```java
private static int init(FabricClientCommandSource source, GitServices services)
```

TODO: Provide detailed design explanation for `init`.

---

## 10. `repo()`

```java
private static int repo(FabricClientCommandSource source, GitServices services)
```

TODO: Provide detailed design explanation for `repo`.

---

## 11. `showRepo()`

```java
private static void showRepo(FabricClientCommandSource source, GitServices services,
                                 WorldContext world, RepoInfo info)
```

TODO: Provide detailed design explanation for `showRepo`.

---

## 12. `setAuthor()`

```java
private static int setAuthor(FabricClientCommandSource source, GitServices services, String name, String email)
```

TODO: Provide detailed design explanation for `setAuthor`.

---

## 13. `remoteAdd()`

```java
private static int remoteAdd(FabricClientCommandSource source, GitServices services,
                                 String name, String url)
```

TODO: Provide detailed design explanation for `remoteAdd`.

---

## 14. `remoteRemove()`

```java
private static int remoteRemove(FabricClientCommandSource source, GitServices services, String name)
```

TODO: Provide detailed design explanation for `remoteRemove`.

---

## 15. `remoteList()`

```java
private static int remoteList(FabricClientCommandSource source, GitServices services)
```

TODO: Provide detailed design explanation for `remoteList`.

---

## 16. `log()`

```java
private static int log(FabricClientCommandSource source, GitServices services, int count)
```

TODO: Provide detailed design explanation for `log`.

---

## 17. `showLog()`

```java
private static void showLog(FabricClientCommandSource source, GitServices services, List<CommitInfo> commits)
```

TODO: Provide detailed design explanation for `showLog`.

---

## 18. `push()`

```java
private static int push(FabricClientCommandSource source, GitServices services, boolean force)
```

TODO: Provide detailed design explanation for `push`.

---

## 19. `fetch()`

```java
private static int fetch(FabricClientCommandSource source, GitServices services, String requestedRemote)
```

TODO: Provide detailed design explanation for `fetch`.

---

## 20. `currentWorld()`

```java
private static WorldContext currentWorld(FabricClientCommandSource source)
```

TODO: Provide detailed design explanation for `currentWorld`.

---

## 21. `shortId()`

```java
private static String shortId(String id)
```

TODO: Provide detailed design explanation for `shortId`.

---
