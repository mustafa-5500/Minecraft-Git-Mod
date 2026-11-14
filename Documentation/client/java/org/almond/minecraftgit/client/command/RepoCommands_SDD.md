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

`GitCommand.register` calls `attach` once with the `/git` root and `attachConfig` once with the pre-built `config` literal; the class is never instantiated and mutates nothing at attach time. Every executing branch resolves the current world, optionally checks that the repository is initialized, runs the blocking JGit call through `CommandSupport.complete` on `GitServices.io()`, and formats the result back on the client thread with `Messages`.

---

## 1. Overview

Repository lifecycle and transport commands.

The class splits cleanly into command wiring (`attach`, `attachConfig`, `attachRemote`), asynchronous operations (`init`, `repo`, `setAuthor`, the remote commands, `log`, `push`, `fetch`) and formatting helpers (`showRepo`, `showLog`, `shortId`, `currentWorld`). Repository identity is always the `WorldContext.id()` key, and remote/branch defaults come from local `RepoMetadata` first and `ModConfig` second. Blocking JGit work never runs on the render thread: `CommandSupport.complete` submits it to the IO executor and marshals the result back through `Minecraft.execute`.

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
| `IntegerArgumentType` | Declares the `count` argument of `log` with the 1–100 range. |
| `StringArgumentType` | Declares `name`, `url`, `remote` and author arguments; `greedyString()` keeps the rest of the line intact for URLs. |
| `LiteralArgumentBuilder` | Type of the `/git` root and `config` builders the subtrees are appended to. |
| `ClientCommandManager` | Fabric factory for client-side literals and arguments. |
| `FabricClientCommandSource` | Command source providing the client, the optional player and chat output. |
| `ServerPlayer` | Declared by the generator's import block but not referenced; `init` derives the author name from the client `Player` instead. |
| [`GitServices`](../GitServices.md) | Service locator supplying the repo manager, config defaults, async executor and messaging. |
| [`WorldContext`](../WorldContext.md) | Resolves the `<world>__<dimension>` repository key for the current world. |
| [`CommitInfo`](../../../../../../../main/java/org/almond/minecraftgit/repo/CommitInfo.md) | Row type returned by `RepoService.log` and formatted by `showLog`. |
| [`PullPlan`](../../../../../../../main/java/org/almond/minecraftgit/repo/PullPlan.md) | Declared by the generator's import block but not referenced; pull/plan commands live in `PullCommands`. |
| [`RemoteInfo`](../../../../../../../main/java/org/almond/minecraftgit/repo/RemoteInfo.md) | Name/URL pair listed by `remoteList` and suggested for `fetch <remote>`. |
| [`RepoInfo`](../../../../../../../main/java/org/almond/minecraftgit/repo/RepoInfo.md) | Snapshot produced by `init`/`info` and rendered by `showRepo`. |
| [`RepoMetadata`](../../../../../../../main/java/org/almond/minecraftgit/repo/RepoMetadata.md) | Local per-world state read by `push`/`fetch` to pick the configured remote name. |
| [`RepoProgress`](../../../../../../../main/java/org/almond/minecraftgit/repo/RepoProgress.md) | `RepoProgress.NOOP` is passed to push/fetch so JGit progress callbacks do nothing while the command is async. |
| [`RepoService`](../../../../../../../main/java/org/almond/minecraftgit/repo/RepoService.md) | Declared by the generator's import block but not named explicitly; repository calls go through `services.repos().service(worldId)`. |
| [`Names`](../../../../../../../main/java/org/almond/minecraftgit/util/Names.md) | Declared by the generator's import block but not referenced here; name sanitising happens inside `WorldContext`. |
| `ZoneId` | Supplies the system default time zone for the `DATE` formatter. |
| `DateTimeFormatter` | Types the `DATE` field used to render commit timestamps. |
| `List` | Types the commit list parameter of `showLog`. |

---

## 3. Class Declaration

```java
public final class RepoCommands {
```

`public final` with a private constructor marks a static-only command module. Its only state is the immutable `DATE` formatter; the `GitServices` instance is captured by the registered lambdas rather than stored in a field, so re-registering the command tree cannot retain a stale service set. Repository handles are never cached here — each command asks `RepoManager` for the current world's `RepoService`.

---

## 4. Instance Fields

```java
private static final DateTimeFormatter DATE;
```

| Field | Type | Description |
|-------|------|-------------|
| `DATE` | `DateTimeFormatter` | Immutable `yyyy-MM-dd HH:mm` formatter bound to `ZoneId.systemDefault()`, used only by `showLog`. `DateTimeFormatter` is thread-safe, so the static instance is safe even though formatting happens on the client thread. |

---

## 5. Constructor

```java
private RepoCommands()
```

Never invoked; the private constructor documents that the class is a static utility. All commands are registered through `attach`/`attachConfig`, and no instance fields or resources need initialization.

---

## 6. `attach()`

```java
public static void attach(LiteralArgumentBuilder<FabricClientCommandSource> root, GitServices services)
```

Registers the repository-facing command tree on the `/git` root:

- `/git init` → `init`
- `/git repo` → `repo`
- `/git remote` and `/git remote list` → `remoteList`
- `/git remote add <name> <url>` → `remoteAdd`
- `/git remote remove <name>` → `remoteRemove`
- `/git log [count]` → `log` (default 10, range 1..100)
- `/git push [--force]` → `push`
- `/git fetch [remote]` → `fetch`

`log` exposes a bare executor and an integer argument whose range is enforced by Brigadier before the callback runs. `push` exposes a `--force` literal that only flips the boolean passed to `push`; it is not an argument, so it must follow the literal exactly. `fetch`'s remote argument has an inline suggestion provider that lists `services.repos().service(world.id()).remoteList()` only when `isInitialized(world.id())` is true, and catches `RuntimeException` so a broken repository or missing world cannot break command parsing. The tree is built without touching disk.

---

## 7. `attachConfig()`

```java
public static void attachConfig(LiteralArgumentBuilder<FabricClientCommandSource> config,
                                    GitServices services)
```

Called by `GitCommand.register` before the root is registered, because the `/git config` literal is created there and needs its `author <name> <email>` child before `config` is attached to `/git`. The name and email are both `StringArgumentType.string()` arguments and are passed verbatim to `setAuthor`, which owns validation. Keeping the author command in this class means every repository-metadata mutation lives beside the rest of the repo commands, while `/git config show|debug|blocks` stays in `GitCommand`.

---

## 8. `attachRemote()`

```java
private static void attachRemote(LiteralArgumentBuilder<FabricClientCommandSource> root,
                                     GitServices services)
```

Builds the `remote` literal with four paths: bare `remote` and `remote list` both execute `remoteList`; `remote add` requires a `name` and a `greedyString` `url`, so a URL may contain spaces and consumes the remainder of the line without quoting; `remote remove` requires just the name. It is factored out of `attach` only to keep that method readable — it captures the same `services`.

---

## 9. `init()`

```java
private static int init(FabricClientCommandSource source, GitServices services)
```

Resolves the world, chooses the initial author name (`source.getPlayer().getName()` when a player is present, otherwise `config.committerName`), sends an "Initializing repository for ..." line and submits `services.repos().init(world.id(), playerName, null)` to the IO executor. `RepoManager.init` adopts the player's name if the metadata still has the default identity, derives `<sanitized>@minecraftgit.local` when no email is given, opens/creates the JGit repository and persists the effective branch and identity. The success consumer prints the repo path and the branch with either `<shortId> (<summary>)` or `empty` HEAD, and adds a multiplayer warning that applying builds requires single-player. The executor callback returns 1 immediately, because the work is asynchronous.

---

## 10. `repo()`

```java
private static int repo(FabricClientCommandSource source, GitServices services)
```

Guard-then-delegate: if `services.repos().isInitialized(world.id())` is false it prints the "Run /git init" hint and returns without scheduling work; otherwise it submits `service(world.id()).info()` and passes the snapshot to `showRepo` on the client thread. `RepoInfo` is a pure record, so nothing is retained between commands.

---

## 11. `showRepo()`

```java
private static void showRepo(FabricClientCommandSource source, GitServices services,
                                 WorldContext world, RepoInfo info)
```

Formatting-only helper for `/git repo`: prints the world id header and the path, branch, HEAD (short id plus summary, or `(no commits)`), author with email, remote (`(none)` or `name -> url`) and working-tree state from the snapshot. `shortId` is applied to the full HEAD id so chat lines stay narrow.

---

## 12. `setAuthor()`

```java
private static int setAuthor(FabricClientCommandSource source, GitServices services, String name, String email)
```

Validates both arguments before scheduling anything: either blank value throws `UserFacingException("Author name and email must not be blank.")`, which `runInt` turns into a chat error. It then submits `services.repos().setAuthor(world.id(), name, email)`, which rewrites the local `RepoMetadata` and, when a `RepoService` is already open for the world, re-applies the JGit author identity so the next commit uses it. The result consumer reports the identity and the world id.

---

## 13. `remoteAdd()`

```java
private static int remoteAdd(FabricClientCommandSource source, GitServices services,
                                 String name, String url)
```

Trims the greedy URL string and submits `service(world.id()).remoteAdd(name, target)` to the IO executor. `RepoService` validates the remote name and URI and writes the JGit config; failures (invalid URL, reserved name) propagate through `CommandSupport` and appear as chat errors. Success echoes the trimmed `name -> target` pair.

---

## 14. `remoteRemove()`

```java
private static int remoteRemove(FabricClientCommandSource source, GitServices services, String name)
```

Submits `service(world.id()).remoteRemove(name)` and confirms with "Removed remote '<name>'." when it returns. Matching is delegated entirely to JGit/`RepoService`; an unknown remote raises a `RepoException` that becomes a chat error.

---

## 15. `remoteList()`

```java
private static int remoteList(FabricClientCommandSource source, GitServices services)
```

Submits `service(world.id()).remoteList()` and consumes the `List<RemoteInfo>` on the client thread. An empty list prints the actionable hint `/git remote add origin <url>`; otherwise each remote becomes a raw `name -> url` line (no header), so the output is easy to copy.

---

## 16. `log()`

```java
private static int log(FabricClientCommandSource source, GitServices services, int count)
```

Requires an initialized repository (otherwise the `/git init` hint), then submits `service(world.id()).log(count)` and hands the commits to `showLog`. The default of 10 is passed by the bare executor in `attach`; the integer argument is constrained to 1–100 by Brigadier, so `count` is never zero or negative. `RepoService.log` walks HEAD from newest to oldest, so the printed order is newest first.

---

## 17. `showLog()`

```java
private static void showLog(FabricClientCommandSource source, GitServices services, List<CommitInfo> commits)
```

Prints "No commits yet." for an empty list, otherwise a "Recent commits:" header followed by one line per commit as `shortId`, the `DATE`-formatted timestamp, author and the first line of the commit message (`CommitInfo.message` is already abbreviated by `RepoService.log`). The formatter converts each `CommitInfo.when()` `Instant` into the system time zone; no date/time is parsed back.

---

## 18. `push()`

```java
private static int push(FabricClientCommandSource source, GitServices services, boolean force)
```

Requires an initialized repository, then derives the remote from metadata: `metadata(world.id()).remoteName()` when non-blank, otherwise `config.defaultRemote`. It announces "Pushing to <remote>..." and submits `service(world.id()).push(remote, force, RepoProgress.NOOP)`; the NOOP progress monitor means no per-object UI updates are attempted while the command runs off-thread. The consumer distinguishes `summary.upToDate()` ("Remote is already up to date.") from a real push, reporting branch, remote and `updatedRefs()`. `RepoService` rejects a detached HEAD, a repository without commits and non-fast-forward ref updates; those errors are caught by `CommandSupport` and shown in chat (the rejection message suggests fetching/pulling or `--force`).

---

## 19. `fetch()`

```java
private static int fetch(FabricClientCommandSource source, GitServices services, String requestedRemote)
```

Requires an initialized repository and resolves the remote with a strict precedence: a non-blank `requestedRemote` argument wins; otherwise the metadata remote name; otherwise `config.defaultRemote`. That metadata-derived fallback is what lets a bare `/git fetch` track the remote configured for this world instead of always using the global default. It announces the chosen remote, submits `fetch(remote, RepoProgress.NOOP)` and reports `summary.remote()` plus the number of updated refs. The same remote names are offered by the argument suggestion provider in `attach`, but only for an initialized repository, so suggestions fail soft before `/git init`.

---

## 20. `currentWorld()`

```java
private static WorldContext currentWorld(FabricClientCommandSource source)
```

Shared adapter over `WorldContext.current(source.getClient())`. It runs synchronously before any async submission, so a missing world produces the standard `UserFacingException` ("Open a world before using /git.") and no background task is created. All repository commands key their state by the returned `id()`.

---

## 21. `shortId()`

```java
private static String shortId(String id)
```

Small display helper used for HEAD ids in `init` and `showRepo`: it returns `null` unchanged, leaves strings shorter than 8 characters untouched (so an unborn or truncated HEAD cannot overflow) and otherwise returns the first 8 characters. It makes no assumption that the input is a valid Git hash, which keeps the formatter safe for hand-edited or future metadata.

---
