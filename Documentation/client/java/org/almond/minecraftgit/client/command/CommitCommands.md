# CommitCommands

> **Software Detailed Documentation:** [CommitCommands_SDD.md](./CommitCommands_SDD.md)  
> **Source File:** [CommitCommands.java](../../../../../../../../src/client/java/org/almond/minecraftgit/client/command/CommitCommands.java)

## Table of Contents

- [Usage](#usage)
- [CommitCommands (Constructor)](#commitcommands-constructor)
- [attach](#attach)
- [commit](#commit)
- [showResult](#showresult)
- [requirePlayer](#requireplayer)
- [world](#world)
- [See Also](#see-also)

## Usage

TODO: Add usage examples for `CommitCommands`.

{@code /git commit [message]}: write all staged selections to the repository.

---

## CommitCommands (Constructor)

### Signature
```java
private CommitCommands()
```

### Description
TODO: Describe what `CommitCommands` does.

### Parameters
None.

### Returns
`CommitCommands` instance.

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

## commit

### Signature
```java
private static int commit(FabricClientCommandSource source, GitServices services,
                              String message, boolean allowEmpty)
```

### Description
TODO: Describe what `commit` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | TODO: describe |
| `services` | [`GitServices`](../GitServices.md) | TODO: describe |
| `message` | `String` | TODO: describe |
| `allowEmpty` | `boolean` | TODO: describe |

### Returns
`int` — TODO: describe return value.

---

## showResult

### Signature
```java
private static void showResult(FabricClientCommandSource source, GitServices services, CommitResult result)
```

### Description
TODO: Describe what `showResult` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | TODO: describe |
| `services` | [`GitServices`](../GitServices.md) | TODO: describe |
| `result` | [`CommitResult`](../../../../../../../main/java/org/almond/minecraftgit/commit/CommitResult.md) | TODO: describe |

### Returns
`void`

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

## See Also

- **Software Detailed Design:** [CommitCommands_SDD.md](./CommitCommands_SDD.md)
