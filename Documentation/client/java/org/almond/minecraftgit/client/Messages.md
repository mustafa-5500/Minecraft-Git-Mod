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

TODO: Add usage examples for `Messages`.

Chat formatting shared by all client commands.

---

## Fields

- `config` ([`ModConfig`](../../../../../../main/java/org/almond/minecraftgit/config/ModConfig.md)) — TODO: describe field

---

## Messages (Constructor)

### Signature
```java
public Messages(ModConfig config)
```

### Description
TODO: Describe what `Messages` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `config` | [`ModConfig`](../../../../../../main/java/org/almond/minecraftgit/config/ModConfig.md) | TODO: describe |

### Returns
`Messages` instance.

---

## prefix

### Signature
```java
private Component prefix()
```

### Description
TODO: Describe what `prefix` does.

### Parameters
None.

### Returns
`Component` — TODO: describe return value.

---

## send

### Signature
```java
public void send(FabricClientCommandSource source, String message)
```

### Description
TODO: Describe what `send` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | TODO: describe |
| `message` | `String` | TODO: describe |

### Returns
`void`

---

## success

### Signature
```java
public void success(FabricClientCommandSource source, String message)
```

### Description
TODO: Describe what `success` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | TODO: describe |
| `message` | `String` | TODO: describe |

### Returns
`void`

---

## warn

### Signature
```java
public void warn(FabricClientCommandSource source, String message)
```

### Description
TODO: Describe what `warn` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | TODO: describe |
| `message` | `String` | TODO: describe |

### Returns
`void`

---

## error

### Signature
```java
public void error(FabricClientCommandSource source, String message)
```

### Description
TODO: Describe what `error` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | TODO: describe |
| `message` | `String` | TODO: describe |

### Returns
`void`

---

## raw

### Signature
```java
public void raw(FabricClientCommandSource source, String message)
```

### Description
TODO: Describe what `raw` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | TODO: describe |
| `message` | `String` | TODO: describe |

### Returns
`void`

---

## format

### Signature
```java
public void format(FabricClientCommandSource source, String format, Object... args)
```

### Description
TODO: Describe what `format` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | TODO: describe |
| `format` | `String` | TODO: describe |
| `args` | `Object...` | TODO: describe |

### Returns
`void`

---

## See Also

- **Software Detailed Design:** [Messages_SDD.md](./Messages_SDD.md)
