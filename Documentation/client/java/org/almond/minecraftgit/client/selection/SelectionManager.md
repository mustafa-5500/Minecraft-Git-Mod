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

TODO: Add usage examples for `SelectionManager`.

Per-player wand session state: the two pending corners, the last completed
cuboid, and the active selection name. The durable selection list lives in
{@link SelectionService}; the remembered active name is stored in local repo
metadata so it survives a restart.

---

## Fields

- `selections` ([`SelectionService`](../../../../../../../main/java/org/almond/minecraftgit/selection/SelectionService.md)) — TODO: describe field
- `repos` ([`RepoManager`](../../../../../../../main/java/org/almond/minecraftgit/repo/RepoManager.md)) — TODO: describe field
- `sessions` (`Map<UUID, Session>`) — TODO: describe field

---

## SelectionManager (Constructor)

### Signature
```java
public SelectionManager(SelectionService selections, RepoManager repos)
```

### Description
TODO: Describe what `SelectionManager` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `selections` | [`SelectionService`](../../../../../../../main/java/org/almond/minecraftgit/selection/SelectionService.md) | TODO: describe |
| `repos` | [`RepoManager`](../../../../../../../main/java/org/almond/minecraftgit/repo/RepoManager.md) | TODO: describe |

### Returns
`SelectionManager` instance.

---

## session

### Signature
```java
public Session session(Player player)
```

### Description
TODO: Describe what `session` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `player` | `Player` | TODO: describe |

### Returns
`Session` — TODO: describe return value.

---

## active

### Signature
```java
public String active(WorldContext world, Player player)
```

### Description
Active selection name, or {@code null} when none is remembered/valid.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `world` | [`WorldContext`](../WorldContext.md) | TODO: describe |
| `player` | `Player` | TODO: describe |

### Returns
`String` — TODO: describe return value.

---

## setActive

### Signature
```java
public void setActive(WorldContext world, Player player, String name)
```

### Description
TODO: Describe what `setActive` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `world` | [`WorldContext`](../WorldContext.md) | TODO: describe |
| `player` | `Player` | TODO: describe |
| `name` | `String` | TODO: describe |

### Returns
`void`

---

## setCorner

### Signature
```java
public Optional<Cuboid> setCorner(Player player, boolean first, BlockPos pos)
```

### Description
TODO: Describe what `setCorner` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `player` | `Player` | TODO: describe |
| `first` | `boolean` | TODO: describe |
| `pos` | [`BlockPos`](../../../../../../../main/java/org/almond/minecraftgit/model/BlockPos.md) | TODO: describe |

### Returns
`Optional<Cuboid>` — TODO: describe return value.

---

## takePending

### Signature
```java
public Cuboid takePending(Player player)
```

### Description
TODO: Describe what `takePending` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `player` | `Player` | TODO: describe |

### Returns
[`Cuboid`](../../../../../../../main/java/org/almond/minecraftgit/model/Cuboid.md) — TODO: describe return value.

---

## setPending

### Signature
```java
public void setPending(Player player, Cuboid cuboid)
```

### Description
TODO: Describe what `setPending` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `player` | `Player` | TODO: describe |
| `cuboid` | [`Cuboid`](../../../../../../../main/java/org/almond/minecraftgit/model/Cuboid.md) | TODO: describe |

### Returns
`void`

---

## peekPending

### Signature
```java
public Cuboid peekPending(Player player)
```

### Description
TODO: Describe what `peekPending` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `player` | `Player` | TODO: describe |

### Returns
[`Cuboid`](../../../../../../../main/java/org/almond/minecraftgit/model/Cuboid.md) — TODO: describe return value.

---

## clear

### Signature
```java
public void clear(Player player)
```

### Description
TODO: Describe what `clear` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `player` | `Player` | TODO: describe |

### Returns
`void`

---

## cornerA

### Signature
```java
public BlockPos cornerA()
```

### Description
TODO: Describe what `cornerA` does.

### Parameters
None.

### Returns
[`BlockPos`](../../../../../../../main/java/org/almond/minecraftgit/model/BlockPos.md) — TODO: describe return value.

---

## cornerB

### Signature
```java
public BlockPos cornerB()
```

### Description
TODO: Describe what `cornerB` does.

### Parameters
None.

### Returns
[`BlockPos`](../../../../../../../main/java/org/almond/minecraftgit/model/BlockPos.md) — TODO: describe return value.

---

## pendingCuboid

### Signature
```java
public Cuboid pendingCuboid()
```

### Description
TODO: Describe what `pendingCuboid` does.

### Parameters
None.

### Returns
[`Cuboid`](../../../../../../../main/java/org/almond/minecraftgit/model/Cuboid.md) — TODO: describe return value.

---

## See Also

- **Software Detailed Design:** [SelectionManager_SDD.md](./SelectionManager_SDD.md)
