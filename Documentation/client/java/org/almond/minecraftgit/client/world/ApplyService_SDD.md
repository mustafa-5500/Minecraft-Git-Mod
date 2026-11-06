# ApplyService — Software Detailed Design

> **API Documentation:** [ApplyService.md](./ApplyService.md)  
> **Source File:** [ApplyService.java](../../../../../../../../src/client/java/org/almond/minecraftgit/client/world/ApplyService.java)

## Table of Contents

- [Usage](#usage)
- [1. Overview](#1-overview)
- [2. Package Declaration & Imports](#2-package-declaration-imports)
- [3. Class Declaration](#3-class-declaration)
- [4. Instance Fields](#4-instance-fields)
- [5. Constructor](#5-constructor)
- [6. `apply()`](#6-apply)
- [7. `begin()`](#7-begin)
- [8. `beginWrites()`](#8-beginwrites)
- [9. `process()`](#9-process)
- [10. `finish()`](#10-finish)
- [11. `removeEntities()`](#11-removeentities)
- [12. `placeEntities()`](#12-placeentities)
- [13. `outOfRange()`](#13-outofrange)
- [14. `warn()`](#14-warn)
- [15. `BlockWrite()`](#15-blockwrite)

## Usage

TODO: Describe how `ApplyService` is instantiated and used.

---

## 1. Overview

Applies an {@link ApplyPlan} through the integrated server in tick batches.
This is deliberately single-player only: in multiplayer the server is
authoritative and a client-only mod cannot safely write blocks or entities.

---

## 2. Package Declaration & Imports

```java
package org.almond.minecraftgit.client.world;
```

```java
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.almond.minecraftgit.backup.BackupService;
import org.almond.minecraftgit.client.WorldContext;
import org.almond.minecraftgit.config.ModConfig;
import org.almond.minecraftgit.debug.DebugLogger;
import org.almond.minecraftgit.model.BlockPos;
import org.almond.minecraftgit.model.BlockSnapshot;
import org.almond.minecraftgit.model.EntitySnapshot;
import org.almond.minecraftgit.model.Snapshot;
import org.almond.minecraftgit.serialization.BlockEntityCodec;
import org.almond.minecraftgit.serialization.BlockStateCodec;
import org.almond.minecraftgit.serialization.EntityCodec;
import org.almond.minecraftgit.world.ApplyException;
import org.almond.minecraftgit.world.ApplyPlan;
import org.almond.minecraftgit.world.ApplyResult;
import org.almond.minecraftgit.world.SnapshotService;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
```

| Import | Purpose |
|--------|---------|
| `Minecraft` | TODO: describe purpose |
| `MinecraftServer` | TODO: describe purpose |
| `ServerLevel` | TODO: describe purpose |
| `Entity` | TODO: describe purpose |
| `EntityType` | TODO: describe purpose |
| `Player` | TODO: describe purpose |
| `Block` | TODO: describe purpose |
| `BlockEntity` | TODO: describe purpose |
| `BlockState` | TODO: describe purpose |
| `AABB` | TODO: describe purpose |
| [`BackupService`](../../../../../../../main/java/org/almond/minecraftgit/backup/BackupService.md) | TODO: describe purpose |
| [`WorldContext`](../WorldContext.md) | TODO: describe purpose |
| [`ModConfig`](../../../../../../../main/java/org/almond/minecraftgit/config/ModConfig.md) | TODO: describe purpose |
| [`DebugLogger`](../../../../../../../main/java/org/almond/minecraftgit/debug/DebugLogger.md) | TODO: describe purpose |
| [`BlockPos`](../../../../../../../main/java/org/almond/minecraftgit/model/BlockPos.md) | TODO: describe purpose |
| [`BlockSnapshot`](../../../../../../../main/java/org/almond/minecraftgit/model/BlockSnapshot.md) | TODO: describe purpose |
| [`EntitySnapshot`](../../../../../../../main/java/org/almond/minecraftgit/model/EntitySnapshot.md) | TODO: describe purpose |
| [`Snapshot`](../../../../../../../main/java/org/almond/minecraftgit/model/Snapshot.md) | TODO: describe purpose |
| [`BlockEntityCodec`](../../../../../../../main/java/org/almond/minecraftgit/serialization/BlockEntityCodec.md) | TODO: describe purpose |
| [`BlockStateCodec`](../../../../../../../main/java/org/almond/minecraftgit/serialization/BlockStateCodec.md) | TODO: describe purpose |
| [`EntityCodec`](../../../../../../../main/java/org/almond/minecraftgit/serialization/EntityCodec.md) | TODO: describe purpose |
| [`ApplyException`](../../../../../../../main/java/org/almond/minecraftgit/world/ApplyException.md) | TODO: describe purpose |
| [`ApplyPlan`](../../../../../../../main/java/org/almond/minecraftgit/world/ApplyPlan.md) | TODO: describe purpose |
| [`ApplyResult`](../../../../../../../main/java/org/almond/minecraftgit/world/ApplyResult.md) | TODO: describe purpose |
| [`SnapshotService`](../../../../../../../main/java/org/almond/minecraftgit/world/SnapshotService.md) | TODO: describe purpose |
| `Path` | TODO: describe purpose |
| `ArrayList` | TODO: describe purpose |
| `Comparator` | TODO: describe purpose |
| `List` | TODO: describe purpose |
| `CompletableFuture` | TODO: describe purpose |
| `ExecutorService` | TODO: describe purpose |

---

## 3. Class Declaration

```java
public final class ApplyService {
```


---

## 4. Instance Fields

```java
private static final int MAX_WARNINGS;
private final SnapshotService snapshots;
private final BackupService backups;
private final ModConfig config;
private final DebugLogger debug;
private final ExecutorService io;
```

| Field | Type | Description |
|-------|------|-------------|
| `MAX_WARNINGS` | `int` | TODO: describe |
| `snapshots` | [`SnapshotService`](../../../../../../../main/java/org/almond/minecraftgit/world/SnapshotService.md) | TODO: describe |
| `backups` | [`BackupService`](../../../../../../../main/java/org/almond/minecraftgit/backup/BackupService.md) | TODO: describe |
| `config` | [`ModConfig`](../../../../../../../main/java/org/almond/minecraftgit/config/ModConfig.md) | TODO: describe |
| `debug` | [`DebugLogger`](../../../../../../../main/java/org/almond/minecraftgit/debug/DebugLogger.md) | TODO: describe |
| `io` | `ExecutorService` | TODO: describe |

---

## 5. Constructor

```java
public ApplyService(SnapshotService snapshots, BackupService backups, ModConfig config,
                        DebugLogger debug, ExecutorService io)
```

TODO: Provide detailed design explanation for `ApplyService`.

---

## 6. `apply()`

```java
public CompletableFuture<ApplyResult> apply(WorldContext world, ApplyPlan plan)
```

TODO: Provide detailed design explanation for `apply`.

---

## 7. `begin()`

```java
private void begin(MinecraftServer server, ServerLevel level, WorldContext world, ApplyPlan plan,
                       CompletableFuture<ApplyResult> future)
```

TODO: Provide detailed design explanation for `begin`.

---

## 8. `beginWrites()`

```java
private void beginWrites(MinecraftServer server, ServerLevel level, ApplyPlan plan, Path backup,
                             CompletableFuture<ApplyResult> future)
```

TODO: Provide detailed design explanation for `beginWrites`.

---

## 9. `process()`

```java
private void process()
```

TODO: Provide detailed design explanation for `process`.

---

## 10. `finish()`

```java
private void finish()
```

TODO: Provide detailed design explanation for `finish`.

---

## 11. `removeEntities()`

```java
private int removeEntities()
```

TODO: Provide detailed design explanation for `removeEntities`.

---

## 12. `placeEntities()`

```java
private int placeEntities()
```

TODO: Provide detailed design explanation for `placeEntities`.

---

## 13. `outOfRange()`

```java
private static boolean outOfRange(ServerLevel level, BlockPos position)
```

TODO: Provide detailed design explanation for `outOfRange`.

---

## 14. `warn()`

```java
private static void warn(List<String> warnings, String warning)
```

TODO: Provide detailed design explanation for `warn`.

---

## 15. `BlockWrite()`

```java
private record BlockWrite(BlockPos position, BlockState state, String blockEntity, boolean removal)
```

TODO: Provide detailed design explanation for `BlockWrite`.

---
