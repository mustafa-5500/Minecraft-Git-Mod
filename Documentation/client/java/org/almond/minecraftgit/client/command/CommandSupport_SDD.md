# CommandSupport — Software Detailed Design

> **API Documentation:** [CommandSupport.md](./CommandSupport.md)  
> **Source File:** [CommandSupport.java](../../../../../../../../src/client/java/org/almond/minecraftgit/client/command/CommandSupport.java)

## Table of Contents

- [Usage](#usage)
- [1. Overview](#1-overview)
- [2. Package Declaration & Imports](#2-package-declaration-imports)
- [3. Class Declaration](#3-class-declaration)
- [4. Constructor](#4-constructor)
- [5. `requirePlayer()`](#5-requireplayer)
- [6. `run()`](#6-run)
- [7. `runInt()`](#7-runint)
- [8. `complete()`](#8-complete)
- [9. `completeFuture()`](#9-completefuture)
- [10. `handleError()`](#10-handleerror)
- [11. `unwrap()`](#11-unwrap)
- [12. `join()`](#12-join)
- [13. `lower()`](#13-lower)

## Usage

`CommandSupport` is never instantiated — its constructor is private and all helpers are static. Command classes such as `GitCommand`, `StagingCommands` and `CommitCommands` call it directly: `run`/`runInt` wrap synchronous handler bodies, `complete`/`completeFuture` move async results onto the client thread, and `unwrap`/`handleError` turn service failures into a single chat line.

---

## 1. Overview

Shared plumbing for client commands: player checks, one-line error handling,
and returning async results to the client thread.

---

## 2. Package Declaration & Imports

```java
package org.almond.minecraftgit.client.command;
```

```java
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import org.almond.minecraftgit.MinecraftGit;
import org.almond.minecraftgit.client.GitServices;
import org.almond.minecraftgit.util.UserFacingException;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Supplier;
```

| Import | Purpose |
|--------|---------|
| `FabricClientCommandSource` | command-source type read by the player checks and used to send feedback and errors |
| `Minecraft` | obtains the client instance used for `execute` and for the `player`/`level` liveness check |
| [`MinecraftGit`](../../../../../../../main/java/org/almond/minecraftgit/MinecraftGit.md) | provides the shared `LOGGER` for unexpected failures |
| [`GitServices`](../GitServices.md) | supplies the IO executor via `async` and the command services used by callbacks |
| [`UserFacingException`](../../../../../../../main/java/org/almond/minecraftgit/util/UserFacingException.md) | expected player-readable failure whose message is shown verbatim |
| `Locale` | provides `Locale.ROOT` for locale-independent lowercasing |
| `CompletableFuture` | result type accepted by `completeFuture` and produced by `GitServices.async` |
| `CompletionException` | wrapper type stripped by `unwrap` around async stage failures |
| `ExecutionException` | wrapper type stripped by `unwrap` when joining futures |
| `Consumer` | success callback invoked on the client thread |
| `Supplier` | blocking task supplied to `complete` |

---

## 3. Class Declaration

```java
public final class CommandSupport {
```

- **`final` with a private constructor** — a non-instantiable static helper collection; there is no instance state, so every command class can call the same helpers without any lifecycle or ownership concerns.

---

## 4. Constructor

```java
private CommandSupport()
```

Private constructor: the class is a static utility holder with no state, so there is no instance lifecycle to design around.

---

## 5. `requirePlayer()`

```java
public static FabricClientCommandSource requirePlayer(FabricClientCommandSource source)
```

Player-only guard. It inspects `source.getPlayer()`; when null (console, command block, or a source detached from a player) it throws `UserFacingException` with the fixed message, so handlers never dereference a missing player UUID. Otherwise it returns the same source, enabling the one-line chaining used by the command classes.

---

## 6. `run()`

```java
public static int run(FabricClientCommandSource source, Runnable action)
```

Synchronous wrapper around a `Runnable`. It returns `1` after a successful run and catches `Throwable` — not just `Exception` — so no failure escapes into Brigadier; the throwable is delegated to `handleError` and `0` marks failure for the dispatcher.

---

## 7. `runInt()`

```java
public static int runInt(FabricClientCommandSource source, Supplier<Integer> action)
```

Synchronous wrapper for handlers whose real work is asynchronous and therefore only report a Brigadier status (typically `1` means scheduled). The supplier's integer is returned as-is; a thrown `Throwable` is reported by `handleError` and replaced with `0`.

---

## 8. `complete()`

```java
public static <T> void complete(GitServices services, FabricClientCommandSource source,
                                    Supplier<T> task, Consumer<T> success)
```

Run a blocking task on the IO executor, then invoke `success` on the
client thread. If the player left the world while the task was running,
the result is silently dropped.

---

## 9. `completeFuture()`

```java
public static <T> void completeFuture(FabricClientCommandSource source, CompletableFuture<T> future,
                                          Consumer<T> success)
```

Deliver an already-running future's result on the client thread.

---

## 10. `handleError()`

```java
public static void handleError(FabricClientCommandSource source, Throwable error)
```

Central error policy shared by every command. It first calls `unwrap`; `UserFacingException` and `IllegalArgumentException` are treated as expected input problems, so only their message is sent to chat and nothing is logged. Any other throwable is logged at error level through `MinecraftGit.LOGGER`, then chat receives a generic `Git operation failed: ...` line naming the message (or simple class name) and pointing at the game log. This keeps stack traces out of chat while keeping them diagnosable.

---

## 11. `unwrap()`

```java
public static Throwable unwrap(Throwable error)
```

Walks the cause chain while the current throwable is a `CompletionException` or `ExecutionException` and has a cause. `CompletableFuture.whenComplete` and `join` wrap service failures this way; unwrapping lets `handleError` classify them and lets callers report the real message. A throwable that is already unwrapped is returned unchanged.

---

## 12. `join()`

```java
public static String join(String[] args, int from)
```

Reconstructs a greedy argument tail: arguments from index `from` onward are concatenated with single spaces. Intended for handlers that receive an argument array plus a start index; returns the empty string for an out-of-range start.

---

## 13. `lower()`

```java
public static String lower(String value)
```

Normalizes text for case-insensitive name comparisons. `null` maps to the empty string; otherwise `toLowerCase(Locale.ROOT)` is used so the result does not depend on the JVM default locale.

---
