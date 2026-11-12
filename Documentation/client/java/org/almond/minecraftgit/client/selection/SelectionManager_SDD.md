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

Constructed once by `GitServices` as `new SelectionManager(selections, repos)` and reached through `services.selectionManager()`; `WandEvents` writes corners and pending state, `SelectionRenderer` reads them from the render tick, and selection and staging commands consume the pending cuboid. Sessions live only in memory keyed by player UUID, while the active name is persisted in repo metadata through `RepoManager`, which is why it survives a restart.

---

## 1. Overview

Per-player wand session state: the two pending corners, the last completed
cuboid, and the active selection name. The durable selection list lives in
`SelectionService`; the remembered active name is stored in local repo
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
| [`WorldContext`](../WorldContext.md) | Supplies the world id used to scope the remembered active selection and its metadata. |
| [`BlockPos`](../../../../../../../main/java/org/almond/minecraftgit/model/BlockPos.md) | Type of the two corners and of the coordinates stored per session. |
| [`Cuboid`](../../../../../../../main/java/org/almond/minecraftgit/model/Cuboid.md) | Built from two corners and handed to the command layer as the pending cuboid. |
| [`RepoManager`](../../../../../../../main/java/org/almond/minecraftgit/repo/RepoManager.md) | Reads and writes the remembered active selection name in per-world local metadata. |
| [`SelectionService`](../../../../../../../main/java/org/almond/minecraftgit/selection/SelectionService.md) | Verifies that a remembered selection name still exists for the player. |
| `Player` | Supplies the UUID used as the session-map key. |
| `Map` | Field type of the session map. |
| `Optional` | Return type of `setCorner`, making "not yet complete" explicit. |
| `UUID` | Player identity used as the map key. |
| `ConcurrentHashMap` | Thread-safe map enabling lazy session creation through `computeIfAbsent`. |

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
| `selections` | [`SelectionService`](../../../../../../../main/java/org/almond/minecraftgit/selection/SelectionService.md) | Durable store used only to validate the active name; this manager never mutates it. |
| `repos` | [`RepoManager`](../../../../../../../main/java/org/almond/minecraftgit/repo/RepoManager.md) | Metadata source for the remembered name (`lastSelection` and `setLastSelection`), keyed by world id. |
| `sessions` | `Map<UUID, Session>` | Per-player in-memory state; entries are created on demand and never persisted, so a restart loses corners and pending cuboids but keeps the active name. |

---

## 5. Constructor

```java
public SelectionManager(SelectionService selections, RepoManager repos)
```

Captures both collaborators and allocates an empty `ConcurrentHashMap`; there is no IO and no eager session creation. The manager lives inside one `GitServices`, so its sessions are scoped to the client session.

---

## 6. `session()`

```java
public Session session(Player player)
```

Uses `computeIfAbsent(player.getUUID(), ...)` to get or create the `Session`, so a player always resolves to one object even if two callbacks race. The returned object is mutable state and is used on the client thread only.

---

## 7. `active()`

```java
public String active(WorldContext world, Player player)
```

Active selection name, or `null` when none is remembered/valid.

---

## 8. `setActive()`

```java
public void setActive(WorldContext world, Player player, String name)
```

Delegates to `repos.setLastSelection(world.id(), name)`, which persists the name in local repo metadata; commands pass an empty string to clear it. Validation is deliberately skipped, so `active` filters out names that no longer exist when they are read back.

---

## 9. `setCorner()`

```java
public Optional<Cuboid> setCorner(Player player, boolean first, BlockPos pos)
```

Writes the clicked position into `cornerA` or `cornerB` and, once both exist, builds a `Cuboid` with `Cuboid.of` (which normalizes the corners) and nulls both fields so the next pair starts clean. Returning `Optional.empty()` on the first click is what lets `WandEvents` show the corner confirmation without extra state.

---

## 10. `takePending()`

```java
public Cuboid takePending(Player player)
```

Clears the session's pending field and returns the old value, so the command flow consumes it exactly once. A caller that fails to add the cuboid must call `setPending` to restore it, which `/git selection add` does on a `SelectionException`.

---

## 11. `setPending()`

```java
public void setPending(Player player, Cuboid cuboid)
```

Assigns the pending field directly; used by `WandEvents.completeCuboid` when no selection is active or an add fails, and by commands restoring a failed add.

---

## 12. `peekPending()`

```java
public Cuboid peekPending(Player player)
```

Returns the field without clearing it; the renderer calls it once per drawn tick to outline the pending cuboid.

---

## 13. `clear()`

```java
public void clear(Player player)
```

Removes the UUID entry from the map, dropping corners and pending state; the next access through `session` creates a blank session.

---

## 14. `cornerA()`

```java
public BlockPos cornerA()
```

Reads the private session field for diagnostics and tests; the value is null between cuboid completions and after `clear`.

---

## 15. `cornerB()`

```java
public BlockPos cornerB()
```

Reads corner B without clearing it; it is null until set and again after a cuboid completes.

---

## 16. `pendingCuboid()`

```java
public Cuboid pendingCuboid()
```

Reads the pending field without consuming it, kept separate from `peekPending` so callers already holding a `Session` can inspect it.

---
