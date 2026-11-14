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

`CommandSupport` is a stateless utility used by every `/git` handler; it is never instantiated. Synchronous command bodies are wrapped in `CommandSupport.run(source, () -> ...)` or `CommandSupport.runInt(source, () -> ...)`, while asynchronous work such as `/git add` or `/git commit` is started with `CommandSupport.complete(services, source, task, success)` so its result is delivered back on the client thread. Failures thrown inside these wrappers become one chat line via `handleError`, which is why handlers can simply throw `UserFacingException` or `SelectionException`.

Shared plumbing for client commands: player checks, one-line error handling,
and returning async results to the client thread.

---

## CommandSupport (Constructor)

### Signature
```java
private CommandSupport()
```

### Description
Private no-argument constructor; `CommandSupport` is a static-only plumbing class and cannot be instantiated.

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
Checks that the command source belongs to a player and returns it unchanged. If `source.getPlayer()` is `null` (a console or other non-player context), it throws a `UserFacingException` with the message `That command can only be run by a player.`

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | client command source to validate |

### Returns
`FabricClientCommandSource` — the same source, guaranteed to have a non-null player.

---

## run

### Signature
```java
public static int run(FabricClientCommandSource source, Runnable action)
```

### Description
Runs a synchronous command action and converts any thrown error into chat feedback. Returns `1` on success and `0` after a failure, following the Brigadier integer convention; the exception is not rethrown.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | source used to report errors |
| `action` | `Runnable` | command body executed on the calling (client) thread |

### Returns
`int` — `1` if the action completed, `0` if it threw.

---

## runInt

### Signature
```java
public static int runInt(FabricClientCommandSource source, Supplier<Integer> action)
```

### Description
Runs a synchronous command action that supplies a Brigadier return code, reporting any thrown error through `handleError`. The supplier's value is returned unchanged on success; otherwise the result is `0`.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | source used to report errors |
| `action` | `Supplier<Integer>` | command body producing a Brigadier result code |

### Returns
`int` — the supplier's return code, or `0` if it threw.

---

## complete

### Signature
```java
public static <T> void complete(GitServices services, FabricClientCommandSource source,
                                    Supplier<T> task, Consumer<T> success)
```

### Description
Run a blocking task on the IO executor, then invoke `success` on the
client thread. If the player left the world while the task was running,
the result is silently dropped.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `services` | [`GitServices`](../GitServices.md) | service graph whose IO executor runs the task |
| `source` | `FabricClientCommandSource` | source for chat feedback/errors; its client schedules the callback |
| `task` | `Supplier<T>` | blocking operation executed on the IO pool; may throw |
| `success` | `Consumer<T>` | result handler invoked on the client thread |

### Returns
`<T> void` — returns immediately; the result is delivered asynchronously. If the player left the world before the task finished, nothing is shown.

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
| `source` | `FabricClientCommandSource` | source for chat feedback/errors; its client schedules the callback |
| `future` | `CompletableFuture<T>` | already-running operation whose result should be shown |
| `success` | `Consumer<T>` | result handler invoked on the client thread |

### Returns
`<T> void` — returns immediately; the result or error is delivered later on the client thread and is dropped if the player has left the world.

---

## handleError

### Signature
```java
public static void handleError(FabricClientCommandSource source, Throwable error)
```

### Description
Reports a command failure as a single chat error instead of letting it escape. `UserFacingException` and `IllegalArgumentException` messages are echoed verbatim; any other throwable is logged to `MinecraftGit.LOGGER` and shown as `Git operation failed: <message> (see the game log for details)`.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | source that receives the error message |
| `error` | `Throwable` | failure to report, possibly wrapped in a completion exception |

### Returns
`void`

---

## unwrap

### Signature
```java
public static Throwable unwrap(Throwable error)
```

### Description
Removes `CompletionException` and `ExecutionException` wrappers by repeatedly walking to the cause. This exposes the original service exception so `handleError` can decide whether it is user-facing; an already-unwrapped throwable is returned as-is.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `error` | `Throwable` | throwable captured from a future or command body |

### Returns
`Throwable` — the first non-wrapper cause, or `error` itself when it is not wrapped.

---

## join

### Signature
```java
public static String join(String[] args, int from)
```

### Description
Rebuilds a space-separated string from command arguments starting at index `from`; available to handlers that need to reconstruct message text from an argument array. Returns the empty string when `from` is at or past `args.length`.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `args` | `String[]` | raw command arguments |
| `from` | `int` | zero-based index of the first argument to include |

### Returns
`String` — the joined arguments, or the empty string when the range is empty.

---

## lower

### Signature
```java
public static String lower(String value)
```

### Description
Null-safe lower-case helper for normalizing names and ids, using `Locale.ROOT` so locale-specific rules cannot alter matching. A `null` value becomes the empty string.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `value` | `String` | text to normalize; may be `null` |

### Returns
`String` — lower-cased text, or the empty string when `value` is `null`.

---

## See Also

- **Software Detailed Design:** [CommandSupport_SDD.md](./CommandSupport_SDD.md)
