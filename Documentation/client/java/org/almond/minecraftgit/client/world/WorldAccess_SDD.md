# WorldAccess — Software Detailed Design

> **API Documentation:** [WorldAccess.md](./WorldAccess.md)  
> **Source File:** [WorldAccess.java](../../../../../../../../src/client/java/org/almond/minecraftgit/client/world/WorldAccess.java)

## Table of Contents

- [Usage](#usage)
- [1. Overview](#1-overview)
- [2. Package Declaration & Imports](#2-package-declaration-imports)
- [3. Class Declaration](#3-class-declaration)
- [4. Constructor](#4-constructor)
- [5. `capture()`](#5-capture)
- [6. `capturePositions()`](#6-capturepositions)
- [7. `captureOnClientThread()`](#7-captureonclientthread)

## Usage

TODO: Describe how `WorldAccess` is instantiated and used.

---

## 1. Overview

Chooses where a capture runs. In single-player the integrated server owns the
authoritative world and can load chunks; in multiplayer only client-loaded
chunks are available.

---

## 2. Package Declaration & Imports

```java
package org.almond.minecraftgit.client.world;
```

```java
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import org.almond.minecraftgit.client.GitServices;
import org.almond.minecraftgit.client.WorldContext;
import org.almond.minecraftgit.model.BlockPos;
import org.almond.minecraftgit.model.Selection;
import org.almond.minecraftgit.model.Snapshot;
import org.almond.minecraftgit.util.UserFacingException;
import org.almond.minecraftgit.world.CaptureResult;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
```

| Import | Purpose |
|--------|---------|
| `Minecraft` | TODO: describe purpose |
| `MinecraftServer` | TODO: describe purpose |
| `ServerLevel` | TODO: describe purpose |
| [`GitServices`](../GitServices.md) | TODO: describe purpose |
| [`WorldContext`](../WorldContext.md) | TODO: describe purpose |
| [`BlockPos`](../../../../../../../main/java/org/almond/minecraftgit/model/BlockPos.md) | TODO: describe purpose |
| [`Selection`](../../../../../../../main/java/org/almond/minecraftgit/model/Selection.md) | TODO: describe purpose |
| [`Snapshot`](../../../../../../../main/java/org/almond/minecraftgit/model/Snapshot.md) | TODO: describe purpose |
| [`UserFacingException`](../../../../../../../main/java/org/almond/minecraftgit/util/UserFacingException.md) | TODO: describe purpose |
| [`CaptureResult`](../../../../../../../main/java/org/almond/minecraftgit/world/CaptureResult.md) | TODO: describe purpose |
| `Collection` | TODO: describe purpose |
| `CompletableFuture` | TODO: describe purpose |

---

## 3. Class Declaration

```java
public final class WorldAccess {
```


---

## 4. Constructor

```java
private WorldAccess()
```

TODO: Provide detailed design explanation for `WorldAccess`.

---

## 5. `capture()`

```java
public static CompletableFuture<CaptureResult> capture(GitServices services, WorldContext world,
                                                           Selection selection)
```

TODO: Provide detailed design explanation for `capture`.

---

## 6. `capturePositions()`

```java
public static CompletableFuture<Snapshot> capturePositions(GitServices services, WorldContext world,
                                                               String selectionName, BlockPos origin,
                                                               Collection<BlockPos> positions)
```

TODO: Provide detailed design explanation for `capturePositions`.

---

## 7. `captureOnClientThread()`

```java
private static <T> CompletableFuture<T> captureOnClientThread(Minecraft client,
                                                                  java.util.function.Supplier<T> task)
```

TODO: Provide detailed design explanation for `captureOnClientThread`.

---
