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

TODO: Describe how `CommandSupport` is instantiated and used.

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
| `FabricClientCommandSource` | TODO: describe purpose |
| `Minecraft` | TODO: describe purpose |
| [`MinecraftGit`](../../../../../../../main/java/org/almond/minecraftgit/MinecraftGit.md) | TODO: describe purpose |
| [`GitServices`](../GitServices.md) | TODO: describe purpose |
| [`UserFacingException`](../../../../../../../main/java/org/almond/minecraftgit/util/UserFacingException.md) | TODO: describe purpose |
| `Locale` | TODO: describe purpose |
| `CompletableFuture` | TODO: describe purpose |
| `CompletionException` | TODO: describe purpose |
| `ExecutionException` | TODO: describe purpose |
| `Consumer` | TODO: describe purpose |
| `Supplier` | TODO: describe purpose |

---

## 3. Class Declaration

```java
public final class CommandSupport {
```


---

## 4. Constructor

```java
private CommandSupport()
```

TODO: Provide detailed design explanation for `CommandSupport`.

---

## 5. `requirePlayer()`

```java
public static FabricClientCommandSource requirePlayer(FabricClientCommandSource source)
```

TODO: Provide detailed design explanation for `requirePlayer`.

---

## 6. `run()`

```java
public static int run(FabricClientCommandSource source, Runnable action)
```

TODO: Provide detailed design explanation for `run`.

---

## 7. `runInt()`

```java
public static int runInt(FabricClientCommandSource source, Supplier<Integer> action)
```

TODO: Provide detailed design explanation for `runInt`.

---

## 8. `complete()`

```java
public static <T> void complete(GitServices services, FabricClientCommandSource source,
                                    Supplier<T> task, Consumer<T> success)
```

Run a blocking task on the IO executor, then invoke {@code success} on the
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

TODO: Provide detailed design explanation for `handleError`.

---

## 11. `unwrap()`

```java
public static Throwable unwrap(Throwable error)
```

TODO: Provide detailed design explanation for `unwrap`.

---

## 12. `join()`

```java
public static String join(String[] args, int from)
```

TODO: Provide detailed design explanation for `join`.

---

## 13. `lower()`

```java
public static String lower(String value)
```

TODO: Provide detailed design explanation for `lower`.

---
