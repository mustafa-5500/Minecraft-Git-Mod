# SelectionManager

> **Software Detailed Documentation:** [SelectionManager_SDD.md](./SelectionManager_SDD.md)  
> **Source File:** [SelectionManager.java](../../../../../../../../src/client/java/org/almond/minecraftgit/client/selection/SelectionManager.java)

## Table of Contents

- [Usage](#usage)
- [Fields](#fields)
- [SelectionManager (Constructor)](#selectionmanager-constructor)
- [session](#session)
- [active](#active)
- [setActive](#setactive)
- [setCorner](#setcorner)
- [takePending](#takepending)
- [setPending](#setpending)
- [peekPending](#peekpending)
- [clear](#clear)
- [cornerA](#cornera)
- [cornerB](#cornerb)
- [pendingCuboid](#pendingcuboid)
- [See Also](#see-also)

## Usage

Obtained with `services.selectionManager()`; `WandEvents` calls `setCorner` on each wand click and `setPending` or `takePending` around `/git selection add`, while `SelectionRenderer` only reads `active` and `peekPending`. For example, `Optional<Cuboid> done = services.selectionManager().setCorner(player, true, pos)` stores corner A and returns empty until both corners exist. Durable selection data stays in `SelectionService`; this class holds only per-player client session state.

Per-player wand session state: the two pending corners, the last completed
cuboid, and the active selection name. The durable selection list lives in
`SelectionService`; the remembered active name is stored in local repo
metadata so it survives a restart.

---

## Fields

- `selections` ([`SelectionService`](../../../../../../../main/java/org/almond/minecraftgit/selection/SelectionService.md)) — Durable selection store consulted by `active` to confirm that the remembered name still exists for this player and world.
- `repos` ([`RepoManager`](../../../../../../../main/java/org/almond/minecraftgit/repo/RepoManager.md)) — Source of the remembered active name: `lastSelection` and `setLastSelection` persist it in local repo metadata per world id.
- `sessions` (`Map<UUID, Session>`) — `ConcurrentHashMap` from player UUID to session, entries created lazily by `session` and removed by `clear`; session fields are plain and meant to be touched from the client thread.

---

## SelectionManager (Constructor)

### Signature
```java
public SelectionManager(SelectionService selections, RepoManager repos)
```

### Description
Stores both collaborators and leaves the session map empty; no disk IO occurs until `active` or `setActive` touches repo metadata. One manager exists per `GitServices`, so sessions last for the client session.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `selections` | [`SelectionService`](../../../../../../../main/java/org/almond/minecraftgit/selection/SelectionService.md) | Durable selection service used to validate the remembered active name. |
| `repos` | [`RepoManager`](../../../../../../../main/java/org/almond/minecraftgit/repo/RepoManager.md) | Repository manager holding the per-world metadata that remembers the active name. |

### Returns
`SelectionManager` instance.

---

## session

### Signature
```java
public Session session(Player player)
```

### Description
Returns the player's live `Session`, creating an empty one with `computeIfAbsent` on first use. The returned object is the map's own mutable state, so callers change it through the manager methods rather than copying it.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `player` | `Player` | Player whose UUID keys the session. |

### Returns
`Session` — the live session, whose `cornerA`, `cornerB` and pending cuboid may still be null.

---

## active

### Signature
```java
public String active(WorldContext world, Player player)
```

### Description
Active selection name, or `null` when none is remembered/valid.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `world` | [`WorldContext`](../WorldContext.md) | World context whose id scopes the remembered name. |
| `player` | `Player` | Player whose UUID scopes the selection lookup. |

### Returns
`String` — the remembered name when it still exists for this player and world, otherwise `null`.

---

## setActive

### Signature
```java
public void setActive(WorldContext world, Player player, String name)
```

### Description
Records the active selection name for the world through `repos.setLastSelection`; an empty string clears it. The name is not validated here, so `active` re-checks existence on the next read.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `world` | [`WorldContext`](../WorldContext.md) | World context whose id keys the metadata entry. |
| `player` | `Player` | Player on whose behalf the active name is set; kept for symmetry with `active`. |
| `name` | `String` | Selection name to remember; `null` or blank clears the remembered selection. |

### Returns
`void`

---

## setCorner

### Signature
```java
public Optional<Cuboid> setCorner(Player player, boolean first, BlockPos pos)
```

### Description
Stores `pos` as corner A (`first == true`) or corner B and, once both are present, builds a normalized `Cuboid` via `Cuboid.of`, clears both corners and returns it. With only one corner it returns `Optional.empty()`, which callers use to show the action-bar confirmation. No selection is modified.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `player` | `Player` | Player who clicked, selecting the session. |
| `first` | `boolean` | `true` for corner A (attack), `false` for corner B (use). |
| `pos` | [`BlockPos`](../../../../../../../main/java/org/almond/minecraftgit/model/BlockPos.md) | Clicked block position in absolute world coordinates. |

### Returns
`Optional<Cuboid>` — the completed cuboid when this click supplied the second corner, otherwise empty.

---

## takePending

### Signature
```java
public Cuboid takePending(Player player)
```

### Description
Returns the stored pending cuboid and clears it in the same call, so `/git selection add` consumes it exactly once; a caller that fails to add it must restore it with `setPending`. Returns `null` when nothing is pending.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `player` | `Player` | Player whose pending cuboid is consumed. |

### Returns
[`Cuboid`](../../../../../../../main/java/org/almond/minecraftgit/model/Cuboid.md) — the pending cuboid, or `null` when none.

---

## setPending

### Signature
```java
public void setPending(Player player, Cuboid cuboid)
```

### Description
Replaces the player's pending cuboid, used for a completed pair of corners that could not be added yet. It overwrites any previous value and performs no validation.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `player` | `Player` | Player whose session receives the cuboid. |
| `cuboid` | [`Cuboid`](../../../../../../../main/java/org/almond/minecraftgit/model/Cuboid.md) | Cuboid to keep pending; `null` clears it. |

### Returns
`void`

---

## peekPending

### Signature
```java
public Cuboid peekPending(Player player)
```

### Description
Reads the pending cuboid without consuming it, which is how the renderer outlines it every tenth tick. Returns `null` when nothing is pending.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `player` | `Player` | Player whose pending cuboid is read. |

### Returns
[`Cuboid`](../../../../../../../main/java/org/almond/minecraftgit/model/Cuboid.md) — the pending cuboid, or `null`.

---

## clear

### Signature
```java
public void clear(Player player)
```

### Description
Drops the player's whole session, discarding both corners and any pending cuboid; the next `session` call creates a fresh one.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `player` | `Player` | Player whose session is removed. |

### Returns
`void`

---

## cornerA

### Signature
```java
public BlockPos cornerA()
```

### Description
Returns corner A of the in-progress session without clearing it; `setCorner` nulls both corners as soon as a cuboid completes.

### Parameters
None.

### Returns
[`BlockPos`](../../../../../../../main/java/org/almond/minecraftgit/model/BlockPos.md) — corner A, or `null` when it has not been set.

---

## cornerB

### Signature
```java
public BlockPos cornerB()
```

### Description
Returns corner B of the in-progress session without clearing it; like corner A it is nulled when the pair completes.

### Parameters
None.

### Returns
[`BlockPos`](../../../../../../../main/java/org/almond/minecraftgit/model/BlockPos.md) — corner B, or `null` when it has not been set.

---

## pendingCuboid

### Signature
```java
public Cuboid pendingCuboid()
```

### Description
Returns the session's pending cuboid, equivalent to `peekPending` but reached through the session object.

### Parameters
None.

### Returns
[`Cuboid`](../../../../../../../main/java/org/almond/minecraftgit/model/Cuboid.md) — the pending cuboid, or `null` when none.

---

## See Also

- **Software Detailed Design:** [SelectionManager_SDD.md](./SelectionManager_SDD.md)
