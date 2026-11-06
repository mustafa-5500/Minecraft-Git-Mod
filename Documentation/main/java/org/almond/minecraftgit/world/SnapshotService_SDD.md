# SnapshotService — Software Detailed Design

> **API Documentation:** [SnapshotService.md](./SnapshotService.md)  
> **Source File:** [SnapshotService.java](../../../../../../../src/main/java/org/almond/minecraftgit/world/SnapshotService.java)

## Table of Contents

- [Usage](#usage)
- [1. Overview](#1-overview)
- [2. Package Declaration & Imports](#2-package-declaration-imports)
- [3. Class Declaration](#3-class-declaration)
- [4. Instance Fields](#4-instance-fields)
- [5. Constructor](#5-constructor)
- [6. `capture()`](#6-capture)
- [7. `capture()`](#7-capture)
- [8. `capturePositions()`](#8-capturepositions)
- [9. `captureEntities()`](#9-captureentities)
- [10. `ensureLoaded()`](#10-ensureloaded)
- [11. `chunkKey()`](#11-chunkkey)
- [12. `warn()`](#12-warn)

## Usage

TODO: Describe how `SnapshotService` is instantiated and used.

---

## 1. Overview

Reads blocks, block entities and entities from a {@link Level}. The caller
must run this on the owning thread: the client thread for a
{@code ClientLevel}, or the server thread for an integrated-server level.
<p>Unloaded chunks are skipped and counted. When {@code loadChunks} is true
and the level is a {@link ServerLevel}, chunks are loaded on demand (used by
single-player staging/status/backups so a commit does not silently truncate).</p>

---

## 2. Package Declaration & Imports

```java
package org.almond.minecraftgit.world;
```

```java
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import org.almond.minecraftgit.config.ModConfig;
import org.almond.minecraftgit.debug.DebugLogger;
import org.almond.minecraftgit.model.BlockPos;
import org.almond.minecraftgit.model.BlockSnapshot;
import org.almond.minecraftgit.model.Cuboid;
import org.almond.minecraftgit.model.EntitySnapshot;
import org.almond.minecraftgit.model.Selection;
import org.almond.minecraftgit.model.Snapshot;
import org.almond.minecraftgit.selection.SelectionException;
import org.almond.minecraftgit.serialization.BlockEntityCodec;
import org.almond.minecraftgit.serialization.BlockStateCodec;
import org.almond.minecraftgit.serialization.EntityCodec;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
```

| Import | Purpose |
|--------|---------|
| `ServerLevel` | TODO: describe purpose |
| `Entity` | TODO: describe purpose |
| `EntityType` | TODO: describe purpose |
| `Player` | TODO: describe purpose |
| `Level` | TODO: describe purpose |
| `BlockEntity` | TODO: describe purpose |
| `AABB` | TODO: describe purpose |
| [`ModConfig`](../config/ModConfig.md) | TODO: describe purpose |
| [`DebugLogger`](../debug/DebugLogger.md) | TODO: describe purpose |
| [`BlockPos`](../model/BlockPos.md) | TODO: describe purpose |
| [`BlockSnapshot`](../model/BlockSnapshot.md) | TODO: describe purpose |
| [`Cuboid`](../model/Cuboid.md) | TODO: describe purpose |
| [`EntitySnapshot`](../model/EntitySnapshot.md) | TODO: describe purpose |
| [`Selection`](../model/Selection.md) | TODO: describe purpose |
| [`Snapshot`](../model/Snapshot.md) | TODO: describe purpose |
| [`SelectionException`](../selection/SelectionException.md) | TODO: describe purpose |
| [`BlockEntityCodec`](../serialization/BlockEntityCodec.md) | TODO: describe purpose |
| [`BlockStateCodec`](../serialization/BlockStateCodec.md) | TODO: describe purpose |
| [`EntityCodec`](../serialization/EntityCodec.md) | TODO: describe purpose |
| `Instant` | TODO: describe purpose |
| `ArrayList` | TODO: describe purpose |
| `Collection` | TODO: describe purpose |
| `HashSet` | TODO: describe purpose |
| `LinkedHashMap` | TODO: describe purpose |
| `List` | TODO: describe purpose |
| `Map` | TODO: describe purpose |
| `Set` | TODO: describe purpose |
| `TreeSet` | TODO: describe purpose |
| `UUID` | TODO: describe purpose |

---

## 3. Class Declaration

```java
public final class SnapshotService {
```


---

## 4. Instance Fields

```java
private static final int MAX_WARNINGS;
private final ModConfig config;
private final DebugLogger debug;
```

| Field | Type | Description |
|-------|------|-------------|
| `MAX_WARNINGS` | `int` | TODO: describe |
| `config` | [`ModConfig`](../config/ModConfig.md) | TODO: describe |
| `debug` | [`DebugLogger`](../debug/DebugLogger.md) | TODO: describe |

---

## 5. Constructor

```java
public SnapshotService(ModConfig config, DebugLogger debug)
```

TODO: Provide detailed design explanation for `SnapshotService`.

---

## 6. `capture()`

```java
public CaptureResult capture(Selection selection, Level level)
```

TODO: Provide detailed design explanation for `capture`.

---

## 7. `capture()`

```java
public CaptureResult capture(Selection selection, Level level, boolean loadChunks)
```

TODO: Provide detailed design explanation for `capture`.

---

## 8. `capturePositions()`

```java
public Snapshot capturePositions(Level level, String selectionName, BlockPos origin,
                                     Collection<BlockPos> positions, boolean loadChunks)
```

Capture exactly the given positions, for a pre-apply backup. Entities are
not captured here; apply restores blocks and block entities.

---

## 9. `captureEntities()`

```java
private List<EntitySnapshot> captureEntities(Level level, Selection selection, BlockPos origin,
                                                 List<String> warnings)
```

TODO: Provide detailed design explanation for `captureEntities`.

---

## 10. `ensureLoaded()`

```java
private static boolean ensureLoaded(Level level, int x, int z, boolean loadChunks, Set<Long> loadedChunks)
```

TODO: Provide detailed design explanation for `ensureLoaded`.

---

## 11. `chunkKey()`

```java
private static long chunkKey(int chunkX, int chunkZ)
```

TODO: Provide detailed design explanation for `chunkKey`.

---

## 12. `warn()`

```java
private static void warn(List<String> warnings, String warning)
```

TODO: Provide detailed design explanation for `warn`.

---
