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

Create the logger once during client initialisation from `config.debug` and `config.debugBlocks`, and share it through `GitServices`; `/git config debug` and `/git config blocks` flip the flags at runtime. The file is only created and opened on the first enabled `log` call, and `GitServices.close()` closes it when the client stops.

```java
DebugLogger debug = new DebugLogger(config.logsRoot(), config.debug, config.debugBlocks);
debug.log("capture", "Capturing '%s'", selection.name());
debug.blockRead("world__overworld", 0, 64, 0, "minecraft:stone");
// SnapshotService and ApplyService also emit "capture" and "block-write" lines.
debug.close(); // GitServices.close() does this on CLIENT_STOPPING
```

Optional, per-session debug log. Block-level logging is opt-in because a
single large capture would otherwise write millions of lines.

---

## Fields

- `STAMP` (`DateTimeFormatter`) — Immutable `HH:mm:ss.SSS` formatter built with `Locale.ROOT` and the system-default zone; it prefixes every log line.
- `logsRoot` (`Path`) — Directory supplied to the constructor that receives the log file; it is created lazily by the first write and may not exist until then.
- `file` (`Path`) — Fixed per-session path `debug-<epochMillis>.log` resolved against `logsRoot` at construction; it does not change while the logger lives.
- `enabled` (`volatile boolean`) — Master switch, initialised from `config.debug`; when false, `log` and therefore both block helpers return immediately. Volatile so `/git config debug` can toggle it from the client thread.
- `blocks` (`volatile boolean`) — Opt-in per-block switch, initialised from `config.debugBlocks`; it gates only `blockRead` and `blockWrite` and is effective only when `enabled` is also true.
- `writer` (`BufferedWriter`) — Lazily opened UTF-8 appender, `null` until the first enabled line and again after `close()`; all access is guarded by the instance lock.
- `buffered` (`int`) — Number of lines written since the last flush; the writer is flushed when it reaches 32, bounding data loss on a crash.
- `failed` (`boolean`) — Set once after an `IOException` so a broken log destination produces a single warning instead of one per line.

---

## DebugLogger (Constructor)

### Signature
```java
public DebugLogger(Path logsRoot, boolean enabled, boolean blocks)
```

### Description
Stores the log directory and the two initial switches and precomputes the session file name from `System.currentTimeMillis()`. It performs no IO: directories and the file are not created until the first enabled `log` call, so constructing a disabled logger is free.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `logsRoot` | `Path` | Directory under the mod data root that receives `debug-<epochMillis>.log`; created on first write. |
| `enabled` | `boolean` | Initial value of the master switch, normally `config.debug`. |
| `blocks` | `boolean` | Initial value of the per-block switch, normally `config.debugBlocks`; only matters while `enabled` is true. |

### Returns
`DebugLogger` instance.

---

## file

### Signature
```java
public Path file()
```

### Description
Returns the precomputed session log path so commands can show the player where output will go. The accessor does no IO and returns a path whether or not the file has been created yet.

### Parameters
None.

### Returns
`Path` — the log file path `logsRoot/debug-<epochMillis>.log`.

---

## enabled

### Signature
```java
public boolean enabled()
```

### Description
Returns the current master switch. It is a volatile read and does not touch the writer or the file system.

### Parameters
None.

### Returns
`boolean` — `true` when normal log lines are currently written.

---

## setEnabled

### Signature
```java
public void setEnabled(boolean enabled)
```

### Description
Updates the master switch, which `/git config debug` calls before saving the config. It neither opens nor closes the writer and does not flush, so disabling logging stops new lines while an already open writer stays open until `close()`.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `enabled` | `boolean` | New master switch value; `false` suppresses every subsequent log line, `true` re-enables them. |

### Returns
`void`

---

## setBlocks

### Signature
```java
public void setBlocks(boolean blocks)
```

### Description
Updates the per-block switch used by `blockRead` and `blockWrite`. It does not flush or write anything itself, and per-block lines still require `enabled` to be true.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `blocks` | `boolean` | `true` to emit per-block `block-read`/`block-write` lines, `false` to suppress them. |

### Returns
`void`

---

## log

### Signature
```java
public void log(String category, String format, Object... args)
```

### Description
Writes one synchronised line in the form `HH:mm:ss.SSS [category] message`, opening the UTF-8 file in append mode and writing a `# MinecraftGit debug log <Instant>` header on first use. Arguments are formatted with `String.format(Locale.ROOT, ...)` only when at least one is supplied, the writer is flushed every 32 lines, and an `IOException` is swallowed after one warning through `MinecraftGit.LOGGER`. It is safe to call from the client thread, the integrated-server thread or an IO thread, but it performs synchronous buffered file IO.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `category` | `String` | Short tag placed in brackets, for example `capture`, `block-read` or `block-write`. |
| `format` | `String` | Message template, or the literal message when no arguments are given. |
| `args` | `Object...` | Optional `String.format` arguments passed with `Locale.ROOT`. |

### Returns
`void`

---

## blockRead

### Signature
```java
public void blockRead(String world, int x, int y, int z, String state)
```

### Description
Emits a `block-read` line containing `world x y z state` when the `blocks` switch is on; otherwise it returns immediately. `SnapshotService` calls it for every decoded block during capture, which is why the switch is separate from normal debug logging and off by default.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `world` | `String` | Selection or world name used as the first token of the message. |
| `x` | `int` | Absolute block X coordinate. |
| `y` | `int` | Absolute block Y coordinate. |
| `z` | `int` | Absolute block Z coordinate. |
| `state` | `String` | Canonical block-state string that was read, for example `minecraft:stone`. |

### Returns
`void`

---

## blockWrite

### Signature
```java
public void blockWrite(String world, int x, int y, int z, String state)
```

### Description
Emits a `block-write` line containing `world x y z state` when the `blocks` switch is on; otherwise it returns immediately. `ApplyService` calls it after each successful block write, using `minecraft:air` for removals.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `world` | `String` | Selection name that identifies the applied build. |
| `x` | `int` | Absolute block X coordinate. |
| `y` | `int` | Absolute block Y coordinate. |
| `z` | `int` | Absolute block Z coordinate. |
| `state` | `String` | Block state written, or `minecraft:air` for a removal. |

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
Flushes and closes the writer under the instance lock, swallowing any `IOException` because shutdown is best-effort, then clears the writer so a later enabled line reopens the file in append mode. It is idempotent and is called by `GitServices.close()` on `CLIENT_STOPPING`.

### Parameters
None.

### Returns
`void`

---

## See Also

- **Software Detailed Design:** [DebugLogger_SDD.md](./DebugLogger_SDD.md)
