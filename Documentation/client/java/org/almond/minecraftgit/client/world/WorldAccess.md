# WorldAccess

> **Software Detailed Documentation:** [WorldAccess_SDD.md](./WorldAccess_SDD.md)  
> **Source File:** [WorldAccess.java](../../../../../../../../src/client/java/org/almond/minecraftgit/client/world/WorldAccess.java)

## Table of Contents

- [Usage](#usage)
- [WorldAccess (Constructor)](#worldaccess-constructor)
- [capture](#capture)
- [capturePositions](#capturepositions)
- [captureOnClientThread](#captureonclientthread)
- [See Also](#see-also)

## Usage

TODO: Add usage examples for `WorldAccess`.

Chooses where a capture runs. In single-player the integrated server owns the
authoritative world and can load chunks; in multiplayer only client-loaded
chunks are available.

---

## WorldAccess (Constructor)

### Signature
```java
private WorldAccess()
```

### Description
TODO: Describe what `WorldAccess` does.

### Parameters
None.

### Returns
`WorldAccess` instance.

---

## capture

### Signature
```java
public static CompletableFuture<CaptureResult> capture(GitServices services, WorldContext world,
                                                           Selection selection)
```

### Description
TODO: Describe what `capture` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `services` | [`GitServices`](../GitServices.md) | TODO: describe |
| `world` | [`WorldContext`](../WorldContext.md) | TODO: describe |
| `selection` | [`Selection`](../../../../../../../main/java/org/almond/minecraftgit/model/Selection.md) | TODO: describe |

### Returns
`CompletableFuture<CaptureResult>` — TODO: describe return value.

---

## capturePositions

### Signature
```java
public static CompletableFuture<Snapshot> capturePositions(GitServices services, WorldContext world,
                                                               String selectionName, BlockPos origin,
                                                               Collection<BlockPos> positions)
```

### Description
TODO: Describe what `capturePositions` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `services` | [`GitServices`](../GitServices.md) | TODO: describe |
| `world` | [`WorldContext`](../WorldContext.md) | TODO: describe |
| `selectionName` | `String` | TODO: describe |
| `origin` | [`BlockPos`](../../../../../../../main/java/org/almond/minecraftgit/model/BlockPos.md) | TODO: describe |
| `positions` | `Collection<BlockPos>` | TODO: describe |

### Returns
`CompletableFuture<Snapshot>` — TODO: describe return value.

---

## captureOnClientThread

### Signature
```java
private static <T> CompletableFuture<T> captureOnClientThread(Minecraft client,
                                                                  java.util.function.Supplier<T> task)
```

### Description
TODO: Describe what `captureOnClientThread` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `client` | `Minecraft` | TODO: describe |
| `task` | `java.util.function.Supplier<T>` | TODO: describe |

### Returns
`<T> CompletableFuture<T>` — TODO: describe return value.

---

## See Also

- **Software Detailed Design:** [WorldAccess_SDD.md](./WorldAccess_SDD.md)
