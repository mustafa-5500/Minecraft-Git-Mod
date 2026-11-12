# Messages

> **Software Detailed Documentation:** [Messages_SDD.md](./Messages_SDD.md)  
> **Source File:** [Messages.java](../../../../../../../src/client/java/org/almond/minecraftgit/client/Messages.java)

## Table of Contents

- [Usage](#usage)
- [Fields](#fields)
- [Messages (Constructor)](#messages-constructor)
- [prefix](#prefix)
- [send](#send)
- [success](#success)
- [warn](#warn)
- [error](#error)
- [raw](#raw)
- [format](#format)
- [See Also](#see-also)

## Usage

Reached through `GitServices.messages()`; commands pass the Fabric client command source and a plain string, and the helper prepends the configured prefix and applies the colour. For example `services.messages().format(source, "Added %s to '%s'", cuboid.dimensions(), selection.name())` sends a grey prefixed line, while `services.messages().error(source, ...)` reports through the command error channel. Instances are created by `GitServices`, not by commands.

Chat formatting shared by all client commands.

---

## Fields

- `config` ([`ModConfig`](../../../../../../main/java/org/almond/minecraftgit/config/ModConfig.md)) — The loaded `ModConfig`; only `prefix` is read, on every `prefix()` call, so a prefix change takes effect on the next message.

---

## Messages (Constructor)

### Signature
```java
public Messages(ModConfig config)
```

### Description
Stores the config reference only, so no component is cached and every message reflects the current `config.prefix`. `GitServices` constructs one instance and shares it with every command.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `config` | [`ModConfig`](../../../../../../main/java/org/almond/minecraftgit/config/ModConfig.md) | Loaded `ModConfig` whose `prefix` is prepended to every message. |

### Returns
`Messages` instance.

---

## prefix

### Signature
```java
private Component prefix()
```

### Description
Builds the prefix component from `config.prefix`, treating null as the empty string and styling it dark aqua. It is private because every public method copies the prefix before appending its own coloured body.

### Parameters
None.

### Returns
`Component` — a dark-aqua literal prefix (`[Git] ` by default), or an empty component when no prefix is configured.

---

## send

### Signature
```java
public void send(FabricClientCommandSource source, String message)
```

### Description
Sends the prefix followed by a grey literal body as normal command feedback.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | Client command source that decides where the feedback is displayed. |
| `message` | `String` | Body text, sent literally without interpreting formatting codes. |

### Returns
`void`

---

## success

### Signature
```java
public void success(FabricClientCommandSource source, String message)
```

### Description
Same as `send` but styles the body green, used to confirm that an operation completed.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | Client command source that receives the feedback. |
| `message` | `String` | Body text, sent literally and coloured green. |

### Returns
`void`

---

## warn

### Signature
```java
public void warn(FabricClientCommandSource source, String message)
```

### Description
Styles the body yellow for advisory output such as the multiplayer wand fallback message.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | Client command source that receives the feedback. |
| `message` | `String` | Body text, sent literally and coloured yellow. |

### Returns
`void`

---

## error

### Signature
```java
public void error(FabricClientCommandSource source, String message)
```

### Description
Sends a red prefixed message through `sendError` instead of `sendFeedback`, so the client treats it as an error result.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | Client command source that receives the error. |
| `message` | `String` | Body text, sent literally and coloured red. |

### Returns
`void`

---

## raw

### Signature
```java
public void raw(FabricClientCommandSource source, String message)
```

### Description
Sends the body in grey without the prefix, for headers and per-row output where indentation must be preserved.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | Client command source that receives the feedback. |
| `message` | `String` | Body text, sent literally without the prefix. |

### Returns
`void`

---

## format

### Signature
```java
public void format(FabricClientCommandSource source, String format, Object... args)
```

### Description
Formats with `String.format(Locale.ROOT, format, args)` and forwards to `send`, so the result inherits the prefix and grey styling.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | Client command source that receives the formatted feedback. |
| `format` | `String` | `String.format` pattern using `%s`-style specifiers. |
| `args` | `Object...` | Values substituted into the pattern. |

### Returns
`void`

---

## See Also

- **Software Detailed Design:** [Messages_SDD.md](./Messages_SDD.md)
