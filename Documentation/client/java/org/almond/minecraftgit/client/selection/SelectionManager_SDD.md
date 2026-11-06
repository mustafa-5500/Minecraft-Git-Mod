# SelectionManager — Software Detailed Design

> **API Documentation:** [SelectionManager.md](./SelectionManager.md)  
> **Source File:** [SelectionManager.java](../../../../../../../../src/client/java/org/almond/minecraftgit/client/selection/SelectionManager.java)

## Table of Contents

- [Usage](#usage)
- [1. Overview](#1-overview)
- [2. Package Declaration & Imports](#2-package-declaration-imports)
- [3. Class Declaration](#3-class-declaration)
- [4. Instance Fields](#4-instance-fields)
- [5. Constructor](#5-constructor)
- [6. `session()`](#6-session)
- [7. `active()`](#7-active)
- [8. `setActive()`](#8-setactive)
- [9. `setCorner()`](#9-setcorner)
- [10. `takePending()`](#10-takepending)
- [11. `setPending()`](#11-setpending)
- [12. `peekPending()`](#12-peekpending)
- [13. `clear()`](#13-clear)
- [14. `cornerA()`](#14-cornera)
- [15. `cornerB()`](#15-cornerb)
- [16. `pendingCuboid()`](#16-pendingcuboid)

## Usage

TODO: Describe how `SelectionManager` is instantiated and used.

---

## 1. Overview

Per-player wand session state: the two pending corners, the last completed
cuboid, and the active selection name. The durable selection list lives in
{@link SelectionService}; the remembered active name is stored in local repo
metadata so it survives a restart.

---

## 2. Package Declaration & Imports

```java
package org.almond.minecraftgit.client.selection;
```

```java
import org.almond.minecraftgit.client.WorldContext;
import org.almond.minecraftgit.model.BlockPos;
import org.almond.minecraftgit.model.Cuboid;
import org.almond.minecraftgit.repo.RepoManager;
import org.almond.minecraftgit.selection.SelectionService;
import net.minecraft.world.entity.player.Player;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
```

| Import | Purpose |
|--------|---------|
| [`WorldContext`](../WorldContext.md) | TODO: describe purpose |
| [`BlockPos`](../../../../../../../main/java/org/almond/minecraftgit/model/BlockPos.md) | TODO: describe purpose |
| [`Cuboid`](../../../../../../../main/java/org/almond/minecraftgit/model/Cuboid.md) | TODO: describe purpose |
| [`RepoManager`](../../../../../../../main/java/org/almond/minecraftgit/repo/RepoManager.md) | TODO: describe purpose |
| [`SelectionService`](../../../../../../../main/java/org/almond/minecraftgit/selection/SelectionService.md) | TODO: describe purpose |
| `Player` | TODO: describe purpose |
| `Map` | TODO: describe purpose |
| `Optional` | TODO: describe purpose |
| `UUID` | TODO: describe purpose |
| `ConcurrentHashMap` | TODO: describe purpose |

---

## 3. Class Declaration

```java
public final class SelectionManager {
```


---

## 4. Instance Fields

```java
private final SelectionService selections;
private final RepoManager repos;
private final Map<UUID, Session> sessions;
```

| Field | Type | Description |
|-------|------|-------------|
| `selections` | [`SelectionService`](../../../../../../../main/java/org/almond/minecraftgit/selection/SelectionService.md) | TODO: describe |
| `repos` | [`RepoManager`](../../../../../../../main/java/org/almond/minecraftgit/repo/RepoManager.md) | TODO: describe |
| `sessions` | `Map<UUID, Session>` | TODO: describe |

---

## 5. Constructor

```java
public SelectionManager(SelectionService selections, RepoManager repos)
```

TODO: Provide detailed design explanation for `SelectionManager`.

---

## 6. `session()`

```java
public Session session(Player player)
```

TODO: Provide detailed design explanation for `session`.

---

## 7. `active()`

```java
public String active(WorldContext world, Player player)
```

Active selection name, or {@code null} when none is remembered/valid.

---

## 8. `setActive()`

```java
public void setActive(WorldContext world, Player player, String name)
```

TODO: Provide detailed design explanation for `setActive`.

---

## 9. `setCorner()`

```java
public Optional<Cuboid> setCorner(Player player, boolean first, BlockPos pos)
```

TODO: Provide detailed design explanation for `setCorner`.

---

## 10. `takePending()`

```java
public Cuboid takePending(Player player)
```

TODO: Provide detailed design explanation for `takePending`.

---

## 11. `setPending()`

```java
public void setPending(Player player, Cuboid cuboid)
```

TODO: Provide detailed design explanation for `setPending`.

---

## 12. `peekPending()`

```java
public Cuboid peekPending(Player player)
```

TODO: Provide detailed design explanation for `peekPending`.

---

## 13. `clear()`

```java
public void clear(Player player)
```

TODO: Provide detailed design explanation for `clear`.

---

## 14. `cornerA()`

```java
public BlockPos cornerA()
```

TODO: Provide detailed design explanation for `cornerA`.

---

## 15. `cornerB()`

```java
public BlockPos cornerB()
```

TODO: Provide detailed design explanation for `cornerB`.

---

## 16. `pendingCuboid()`

```java
public Cuboid pendingCuboid()
```

TODO: Provide detailed design explanation for `pendingCuboid`.

---
