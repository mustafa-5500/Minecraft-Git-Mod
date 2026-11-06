# PullCommands

> **Software Detailed Documentation:** [PullCommands_SDD.md](./PullCommands_SDD.md)  
> **Source File:** [PullCommands.java](../../../../../../../../src/client/java/org/almond/minecraftgit/client/command/PullCommands.java)

## Table of Contents

- [Usage](#usage)
- [PullCommands (Constructor)](#pullcommands-constructor)
- [attach](#attach)
- [pull](#pull)
- [onPlan](#onplan)
- [applyNext](#applynext)
- [ensureSelection](#ensureselection)
- [addCuboids](#addcuboids)
- [finishPull](#finishpull)
- [fail](#fail)
- [abort](#abort)
- [checkout](#checkout)
- [sendApplyResult](#sendapplyresult)
- [resolve](#resolve)
- [remoteFor](#remotefor)
- [requirePlayer](#requireplayer)
- [world](#world)
- [shortId](#shortid)
- [message](#message)
- [See Also](#see-also)

## Usage

TODO: Add usage examples for `PullCommands`.

{@code /git pull} and {@code /git checkout}.
<p>Pull is fast-forward-only and single-player-only for the apply step. The
local branch is moved only after every selected build has been written, so a
failed world write never leaves Git ahead of the world. A pre-apply backup is
written for each selection and can be restored with {@code --abort}.</p>

---

## PullCommands (Constructor)

### Signature
```java
private PullCommands()
```

### Description
TODO: Describe what `PullCommands` does.

### Parameters
None.

### Returns
`PullCommands` instance.

---

## attach

### Signature
```java
public static void attach(LiteralArgumentBuilder<FabricClientCommandSource> root, GitServices services)
```

### Description
TODO: Describe what `attach` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `root` | `LiteralArgumentBuilder<FabricClientCommandSource>` | TODO: describe |
| `services` | [`GitServices`](../GitServices.md) | TODO: describe |

### Returns
`void`

---

## pull

### Signature
```java
private static int pull(FabricClientCommandSource source, GitServices services,
                            String onlySelection, String requestedRemote)
```

### Description
TODO: Describe what `pull` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | TODO: describe |
| `services` | [`GitServices`](../GitServices.md) | TODO: describe |
| `onlySelection` | `String` | TODO: describe |
| `requestedRemote` | `String` | TODO: describe |

### Returns
`int` — TODO: describe return value.

---

## onPlan

### Signature
```java
private static void onPlan(FabricClientCommandSource source, GitServices services, WorldContext world,
                               Player player, List<Selection> localSelections, BlockPos playerOrigin,
                               RepoService repo, PullPlan plan, String onlySelection)
```

### Description
TODO: Describe what `onPlan` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | TODO: describe |
| `services` | [`GitServices`](../GitServices.md) | TODO: describe |
| `world` | [`WorldContext`](../WorldContext.md) | TODO: describe |
| `player` | `Player` | TODO: describe |
| `localSelections` | `List<Selection>` | TODO: describe |
| `playerOrigin` | [`BlockPos`](../../../../../../../main/java/org/almond/minecraftgit/model/BlockPos.md) | TODO: describe |
| `repo` | [`RepoService`](../../../../../../../main/java/org/almond/minecraftgit/repo/RepoService.md) | TODO: describe |
| `plan` | [`PullPlan`](../../../../../../../main/java/org/almond/minecraftgit/repo/PullPlan.md) | TODO: describe |
| `onlySelection` | `String` | TODO: describe |

### Returns
`void`

---

## applyNext

### Signature
```java
private static void applyNext(FabricClientCommandSource source, GitServices services, WorldContext world,
                                  Player player, RepoService repo, PullPlan plan,
                                  List<PullService.PreparedPull> prepared, int index)
```

### Description
TODO: Describe what `applyNext` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | TODO: describe |
| `services` | [`GitServices`](../GitServices.md) | TODO: describe |
| `world` | [`WorldContext`](../WorldContext.md) | TODO: describe |
| `player` | `Player` | TODO: describe |
| `repo` | [`RepoService`](../../../../../../../main/java/org/almond/minecraftgit/repo/RepoService.md) | TODO: describe |
| `plan` | [`PullPlan`](../../../../../../../main/java/org/almond/minecraftgit/repo/PullPlan.md) | TODO: describe |
| `prepared` | `List<PullService.PreparedPull>` | TODO: describe |
| `index` | `int` | TODO: describe |

### Returns
`void`

---

## ensureSelection

### Signature
```java
private static Selection ensureSelection(GitServices services, WorldContext world, Player player,
                                             PullService.PreparedPull prepared)
```

### Description
TODO: Describe what `ensureSelection` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `services` | [`GitServices`](../GitServices.md) | TODO: describe |
| `world` | [`WorldContext`](../WorldContext.md) | TODO: describe |
| `player` | `Player` | TODO: describe |
| `prepared` | `PullService.PreparedPull` | TODO: describe |

### Returns
[`Selection`](../../../../../../../main/java/org/almond/minecraftgit/model/Selection.md) — TODO: describe return value.

---

## addCuboids

### Signature
```java
private static void addCuboids(GitServices services, WorldContext world, Player player,
                                   Selection selection, PullService.PreparedPull prepared, boolean created)
```

### Description
TODO: Describe what `addCuboids` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `services` | [`GitServices`](../GitServices.md) | TODO: describe |
| `world` | [`WorldContext`](../WorldContext.md) | TODO: describe |
| `player` | `Player` | TODO: describe |
| `selection` | [`Selection`](../../../../../../../main/java/org/almond/minecraftgit/model/Selection.md) | TODO: describe |
| `prepared` | `PullService.PreparedPull` | TODO: describe |
| `created` | `boolean` | TODO: describe |

### Returns
`void`

---

## finishPull

### Signature
```java
private static void finishPull(FabricClientCommandSource source, GitServices services, WorldContext world,
                                   RepoService repo, PullPlan plan, List<PullService.PreparedPull> prepared)
```

### Description
TODO: Describe what `finishPull` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | TODO: describe |
| `services` | [`GitServices`](../GitServices.md) | TODO: describe |
| `world` | [`WorldContext`](../WorldContext.md) | TODO: describe |
| `repo` | [`RepoService`](../../../../../../../main/java/org/almond/minecraftgit/repo/RepoService.md) | TODO: describe |
| `plan` | [`PullPlan`](../../../../../../../main/java/org/almond/minecraftgit/repo/PullPlan.md) | TODO: describe |
| `prepared` | `List<PullService.PreparedPull>` | TODO: describe |

### Returns
`void`

---

## fail

### Signature
```java
private static void fail(FabricClientCommandSource source, GitServices services,
                             List<PullService.PreparedPull> prepared, Throwable error)
```

### Description
TODO: Describe what `fail` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | TODO: describe |
| `services` | [`GitServices`](../GitServices.md) | TODO: describe |
| `prepared` | `List<PullService.PreparedPull>` | TODO: describe |
| `error` | `Throwable` | TODO: describe |

### Returns
`void`

---

## abort

### Signature
```java
private static int abort(FabricClientCommandSource source, GitServices services, String requested)
```

### Description
TODO: Describe what `abort` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | TODO: describe |
| `services` | [`GitServices`](../GitServices.md) | TODO: describe |
| `requested` | `String` | TODO: describe |

### Returns
`int` — TODO: describe return value.

---

## checkout

### Signature
```java
private static int checkout(FabricClientCommandSource source, GitServices services,
                                String revision, String requested)
```

### Description
TODO: Describe what `checkout` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | TODO: describe |
| `services` | [`GitServices`](../GitServices.md) | TODO: describe |
| `revision` | `String` | TODO: describe |
| `requested` | `String` | TODO: describe |

### Returns
`int` — TODO: describe return value.

---

## sendApplyResult

### Signature
```java
private static void sendApplyResult(FabricClientCommandSource source, GitServices services,
                                        ApplyResult result)
```

### Description
TODO: Describe what `sendApplyResult` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | TODO: describe |
| `services` | [`GitServices`](../GitServices.md) | TODO: describe |
| `result` | [`ApplyResult`](../../../../../../../main/java/org/almond/minecraftgit/world/ApplyResult.md) | TODO: describe |

### Returns
`void`

---

## resolve

### Signature
```java
private static Selection resolve(GitServices services, WorldContext world, Player player, String requested)
```

### Description
TODO: Describe what `resolve` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `services` | [`GitServices`](../GitServices.md) | TODO: describe |
| `world` | [`WorldContext`](../WorldContext.md) | TODO: describe |
| `player` | `Player` | TODO: describe |
| `requested` | `String` | TODO: describe |

### Returns
[`Selection`](../../../../../../../main/java/org/almond/minecraftgit/model/Selection.md) — TODO: describe return value.

---

## remoteFor

### Signature
```java
private static String remoteFor(GitServices services, WorldContext world, String requested)
```

### Description
TODO: Describe what `remoteFor` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `services` | [`GitServices`](../GitServices.md) | TODO: describe |
| `world` | [`WorldContext`](../WorldContext.md) | TODO: describe |
| `requested` | `String` | TODO: describe |

### Returns
`String` — TODO: describe return value.

---

## requirePlayer

### Signature
```java
private static Player requirePlayer(FabricClientCommandSource source)
```

### Description
TODO: Describe what `requirePlayer` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | TODO: describe |

### Returns
`Player` — TODO: describe return value.

---

## world

### Signature
```java
private static WorldContext world(FabricClientCommandSource source)
```

### Description
TODO: Describe what `world` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | TODO: describe |

### Returns
[`WorldContext`](../WorldContext.md) — TODO: describe return value.

---

## shortId

### Signature
```java
private static String shortId(org.eclipse.jgit.lib.ObjectId id)
```

### Description
TODO: Describe what `shortId` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `id` | `org.eclipse.jgit.lib.ObjectId` | TODO: describe |

### Returns
`String` — TODO: describe return value.

---

## message

### Signature
```java
private static String message(Throwable error)
```

### Description
TODO: Describe what `message` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `error` | `Throwable` | TODO: describe |

### Returns
`String` — TODO: describe return value.

---

## See Also

- **Software Detailed Design:** [PullCommands_SDD.md](./PullCommands_SDD.md)
