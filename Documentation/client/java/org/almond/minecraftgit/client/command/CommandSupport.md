# CommandSupport

> **Software Detailed Documentation:** [CommandSupport_SDD.md](./CommandSupport_SDD.md)  
> **Source File:** [CommandSupport.java](../../../../../../../../src/client/java/org/almond/minecraftgit/client/command/CommandSupport.java)

## Table of Contents

- [Usage](#usage)
- [CommandSupport (Constructor)](#commandsupport-constructor)
- [requirePlayer](#requireplayer)
- [run](#run)
- [runInt](#runint)
- [complete](#complete)
- [completeFuture](#completefuture)
- [handleError](#handleerror)
- [unwrap](#unwrap)
- [join](#join)
- [lower](#lower)
- [See Also](#see-also)

## Usage

TODO: Add usage examples for `CommandSupport`.

Shared plumbing for client commands: player checks, one-line error handling,
and returning async results to the client thread.

---

## CommandSupport (Constructor)

### Signature
```java
private CommandSupport()
```

### Description
TODO: Describe what `CommandSupport` does.

### Parameters
None.

### Returns
`CommandSupport` instance.

---

## requirePlayer

### Signature
```java
public static FabricClientCommandSource requirePlayer(FabricClientCommandSource source)
```

### Description
TODO: Describe what `requirePlayer` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | TODO: describe |

### Returns
`FabricClientCommandSource` — TODO: describe return value.

---

## run

### Signature
```java
public static int run(FabricClientCommandSource source, Runnable action)
```

### Description
TODO: Describe what `run` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | TODO: describe |
| `action` | `Runnable` | TODO: describe |

### Returns
`int` — TODO: describe return value.

---

## runInt

### Signature
```java
public static int runInt(FabricClientCommandSource source, Supplier<Integer> action)
```

### Description
TODO: Describe what `runInt` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | TODO: describe |
| `action` | `Supplier<Integer>` | TODO: describe |

### Returns
`int` — TODO: describe return value.

---

## complete

### Signature
```java
public static <T> void complete(GitServices services, FabricClientCommandSource source,
                                    Supplier<T> task, Consumer<T> success)
```

### Description
Run a blocking task on the IO executor, then invoke {@code success} on the
client thread. If the player left the world while the task was running,
the result is silently dropped.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `services` | [`GitServices`](../GitServices.md) | TODO: describe |
| `source` | `FabricClientCommandSource` | TODO: describe |
| `task` | `Supplier<T>` | TODO: describe |
| `success` | `Consumer<T>` | TODO: describe |

### Returns
`<T> void` — TODO: describe return value.

---

## completeFuture

### Signature
```java
public static <T> void completeFuture(FabricClientCommandSource source, CompletableFuture<T> future,
                                          Consumer<T> success)
```

### Description
Deliver an already-running future's result on the client thread.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | TODO: describe |
| `future` | `CompletableFuture<T>` | TODO: describe |
| `success` | `Consumer<T>` | TODO: describe |

### Returns
`<T> void` — TODO: describe return value.

---

## handleError

### Signature
```java
public static void handleError(FabricClientCommandSource source, Throwable error)
```

### Description
TODO: Describe what `handleError` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | TODO: describe |
| `error` | `Throwable` | TODO: describe |

### Returns
`void`

---

## unwrap

### Signature
```java
public static Throwable unwrap(Throwable error)
```

### Description
TODO: Describe what `unwrap` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `error` | `Throwable` | TODO: describe |

### Returns
`Throwable` — TODO: describe return value.

---

## join

### Signature
```java
public static String join(String[] args, int from)
```

### Description
TODO: Describe what `join` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `args` | `String[]` | TODO: describe |
| `from` | `int` | TODO: describe |

### Returns
`String` — TODO: describe return value.

---

## lower

### Signature
```java
public static String lower(String value)
```

### Description
TODO: Describe what `lower` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `value` | `String` | TODO: describe |

### Returns
`String` — TODO: describe return value.

---

## See Also

- **Software Detailed Design:** [CommandSupport_SDD.md](./CommandSupport_SDD.md)
