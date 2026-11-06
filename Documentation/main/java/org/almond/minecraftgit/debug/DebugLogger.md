# DebugLogger

> **Software Detailed Documentation:** [DebugLogger_SDD.md](./DebugLogger_SDD.md)  
> **Source File:** [DebugLogger.java](../../../../../../../src/main/java/org/almond/minecraftgit/debug/DebugLogger.java)

## Table of Contents

- [Usage](#usage)
- [Fields](#fields)
- [DebugLogger (Constructor)](#debuglogger-constructor)
- [file](#file)
- [enabled](#enabled)
- [setEnabled](#setenabled)
- [setBlocks](#setblocks)
- [log](#log)
- [blockRead](#blockread)
- [blockWrite](#blockwrite)
- [close](#close)
- [See Also](#see-also)

## Usage

TODO: Add usage examples for `DebugLogger`.

Optional, per-session debug log. Block-level logging is opt-in because a
single large capture would otherwise write millions of lines.

---

## Fields

- `STAMP` (`DateTimeFormatter`) — TODO: describe field
- `logsRoot` (`Path`) — TODO: describe field
- `file` (`Path`) — TODO: describe field
- `enabled` (`volatile boolean`) — TODO: describe field
- `blocks` (`volatile boolean`) — TODO: describe field
- `writer` (`BufferedWriter`) — TODO: describe field
- `buffered` (`int`) — TODO: describe field
- `failed` (`boolean`) — TODO: describe field

---

## DebugLogger (Constructor)

### Signature
```java
public DebugLogger(Path logsRoot, boolean enabled, boolean blocks)
```

### Description
TODO: Describe what `DebugLogger` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `logsRoot` | `Path` | TODO: describe |
| `enabled` | `boolean` | TODO: describe |
| `blocks` | `boolean` | TODO: describe |

### Returns
`DebugLogger` instance.

---

## file

### Signature
```java
public Path file()
```

### Description
TODO: Describe what `file` does.

### Parameters
None.

### Returns
`Path` — TODO: describe return value.

---

## enabled

### Signature
```java
public boolean enabled()
```

### Description
TODO: Describe what `enabled` does.

### Parameters
None.

### Returns
`boolean` — TODO: describe return value.

---

## setEnabled

### Signature
```java
public void setEnabled(boolean enabled)
```

### Description
TODO: Describe what `setEnabled` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `enabled` | `boolean` | TODO: describe |

### Returns
`void`

---

## setBlocks

### Signature
```java
public void setBlocks(boolean blocks)
```

### Description
TODO: Describe what `setBlocks` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `blocks` | `boolean` | TODO: describe |

### Returns
`void`

---

## log

### Signature
```java
public void log(String category, String format, Object... args)
```

### Description
TODO: Describe what `log` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `category` | `String` | TODO: describe |
| `format` | `String` | TODO: describe |
| `args` | `Object...` | TODO: describe |

### Returns
`void`

---

## blockRead

### Signature
```java
public void blockRead(String world, int x, int y, int z, String state)
```

### Description
TODO: Describe what `blockRead` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `world` | `String` | TODO: describe |
| `x` | `int` | TODO: describe |
| `y` | `int` | TODO: describe |
| `z` | `int` | TODO: describe |
| `state` | `String` | TODO: describe |

### Returns
`void`

---

## blockWrite

### Signature
```java
public void blockWrite(String world, int x, int y, int z, String state)
```

### Description
TODO: Describe what `blockWrite` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `world` | `String` | TODO: describe |
| `x` | `int` | TODO: describe |
| `y` | `int` | TODO: describe |
| `z` | `int` | TODO: describe |
| `state` | `String` | TODO: describe |

### Returns
`void`

---

## close

### Signature
```java
@Override
public void close()
```

### Description
TODO: Describe what `close` does.

### Parameters
None.

### Returns
`void`

---

## See Also

- **Software Detailed Design:** [DebugLogger_SDD.md](./DebugLogger_SDD.md)
