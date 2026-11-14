# DebugLogger — Software Detailed Design

> **API Documentation:** [DebugLogger.md](./DebugLogger.md)  
> **Source File:** [DebugLogger.java](../../../../../../../src/main/java/org/almond/minecraftgit/debug/DebugLogger.java)

## Table of Contents

- [Usage](#usage)
- [1. Overview](#1-overview)
- [2. Package Declaration & Imports](#2-package-declaration-imports)
- [3. Class Declaration](#3-class-declaration)
- [4. Instance Fields](#4-instance-fields)
- [5. Constructor](#5-constructor)
- [6. `file()`](#6-file)
- [7. `enabled()`](#7-enabled)
- [8. `setEnabled()`](#8-setenabled)
- [9. `setBlocks()`](#9-setblocks)
- [10. `log()`](#10-log)
- [11. `blockRead()`](#11-blockread)
- [12. `blockWrite()`](#12-blockwrite)
- [13. `close()`](#13-close)

## Usage

`MinecraftGitClient.onInitializeClient()` creates exactly one logger with `new DebugLogger(config.logsRoot(), config.debug, config.debugBlocks)` and passes it into `GitServices`, which shares it with `RepoManager`, `SnapshotService`, `StagingService`, `CommitService`, `StatusService` and `ApplyService`. Commands never create their own logger: `/git config debug` toggles it through `setEnabled` and `/git config blocks` through `setBlocks`, and `GitServices.close()` calls `close()` when the client stops.

```java
DebugLogger debug = new DebugLogger(config.logsRoot(), config.debug, config.debugBlocks);
GitServices services = new GitServices(root, config, debug);
services.debug().log("status", "Repository %s opened", worldId);
// CLIENT_STOPPING -> GitServices.close() -> debug.close()
```

---

## 1. Overview

Optional, per-session debug log. Block-level logging is opt-in because a
single large capture would otherwise write millions of lines.

---

## 2. Package Declaration & Imports

```java
package org.almond.minecraftgit.debug;
```

```java
import org.almond.minecraftgit.MinecraftGit;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
```

| Import | Purpose |
|--------|---------|
| [`MinecraftGit`](../MinecraftGit.md) | Supplies the `LOGGER` used for the single warning emitted when the debug file cannot be written. |
| `BufferedWriter` | Buffers log lines in memory so a capture does not pay one syscall per line. |
| `IOException` | The checked failure type caught around directory creation, writing, flushing and closing. |
| `StandardCharsets` | Selects UTF-8 explicitly for the log file. |
| `Files` | Creates the log directory and opens the append-mode writer. |
| `Path` | Type of the log root directory and the resolved session file path. |
| `StandardOpenOption` | Supplies `CREATE` and `APPEND` when opening the file so restarts never truncate an existing log. |
| `Instant` | Timestamps the `# MinecraftGit debug log` header and every line. |
| `ZoneId` | Binds the timestamp formatter to the system-default time zone. |
| `DateTimeFormatter` | Holds the `HH:mm:ss.SSS` pattern used for the line prefix. |
| `Locale` | Forces `Locale.ROOT` for the formatter and for `String.format` so output is period-decimal and locale-independent. |

---

## 3. Class Declaration

```java
public final class DebugLogger implements AutoCloseable {
```

`DebugLogger` is a `final`, stateful utility owned by the service graph rather than a static singleton, so tests can construct a disabled logger and each client session gets a distinct file. Implementing `AutoCloseable` gives `GitServices` a uniform shutdown path for the writer. The class owns no thread: flags are `volatile` so service threads can read them cheaply, while the lazily opened writer and its flush counter are guarded by `synchronized` methods because logging can arrive from the client, integrated-server and IO threads.

---

## 4. Instance Fields

```java
private static final DateTimeFormatter STAMP;
private final Path logsRoot;
private final Path file;
private volatile boolean enabled;
private volatile boolean blocks;
private BufferedWriter writer;
private int buffered;
private boolean failed;
```

| Field | Type | Description |
|-------|------|-------------|
| `STAMP` | `DateTimeFormatter` | Shared immutable formatter for `HH:mm:ss.SSS` in the system-default zone with `Locale.ROOT`; used for every line. |
| `logsRoot` | `Path` | Caller-supplied directory, normally `config.logsRoot()`; created by `Files.createDirectories` on first write. |
| `file` | `Path` | Resolved once in the constructor as `debug-<System.currentTimeMillis()>.log`, giving each session a unique file. |
| `enabled` | `volatile boolean` | Master gate checked first in `log`; `setEnabled` writes it, other threads read it without locking. |
| `blocks` | `volatile boolean` | Per-block gate checked by `blockRead`/`blockWrite`; independent of `enabled` but only visible when `log` proceeds. |
| `writer` | `BufferedWriter` | Lazily created append writer; `null` means "not opened yet or closed" and triggers reopen on the next line. |
| `buffered` | `int` | Counts lines since the last flush; at 32 the writer is flushed and the count reset, limiting loss on an abrupt exit. |
| `failed` | `boolean` | One-shot guard: after the first `IOException` it is set so later failures do not repeat the `MinecraftGit.LOGGER` warning. |

---

## 5. Constructor

```java
public DebugLogger(Path logsRoot, boolean enabled, boolean blocks)
```

The constructor is deliberately trivial: it stores `logsRoot`, copies the two switches and resolves `file` from `System.currentTimeMillis()`, so no directories are touched and no writer is allocated until logging is actually enabled and used. The timestamp-based name also means a disabled logger still reports a stable `file()` path that `/git config debug` can show if the player enables logging mid-session. Because the fields are final except the mutable flags, a logger cannot be retargeted to another directory.

---

## 6. `file()`

```java
public Path file()
```

Returns the already-resolved session path; it performs no file-system access and does not reveal whether the file exists. `GitCommand.toggleDebug` uses it to print the destination when debug logging is toggled on.

---

## 7. `enabled()`

```java
public boolean enabled()
```

A volatile read of the master switch, used by callers that want to avoid building expensive arguments before calling `log`. It does not imply that the writer is open.

---

## 8. `setEnabled()`

```java
public void setEnabled(boolean enabled)
```

Writes the volatile master switch only; it does not open the file on enable, flush on disable, or close the writer. This lets `/git config debug` change the setting and persist the config after the command returns, while already-open files remain valid for a later re-enable.

---

## 9. `setBlocks()`

```java
public void setBlocks(boolean blocks)
```

Writes the volatile per-block switch. `blockRead`/`blockWrite` check it before calling `log`, so enabling it while `enabled` is false produces no output; the command layer warns the player about that combination.

---

## 10. `log()`

```java
public void log(String category, String format, Object... args)
```

The method is `synchronized` because the writer, `buffered` and `failed` are not thread-safe and apply/capture logging happens on the integrated-server thread while repository and backup logging can happen on a `minecraftgit-io` thread. It returns immediately when `enabled` is false. On the first enabled call it creates `logsRoot`, opens `file` with `CREATE` + `APPEND` and UTF-8, and writes the session header; the writer is kept for the lifetime of the logger unless `close` is called. Lines are `STAMP.format(Instant.now()) + " [" + category + "] " + message`; `args.length == 0` avoids `String.format` and uses the template literally. Flushing every 32 lines trades a small loss window for throughput. Any `IOException` is caught, `failed` suppresses duplicates, and the first failure is reported through the mod logger so a broken file does not crash a capture or apply.

---

## 11. `blockRead()`

```java
public void blockRead(String world, int x, int y, int z, String state)
```

This is a thin guard over `log`: it checks the `blocks` switch and, when set, writes a `block-read` line with the selection/world name, absolute coordinates and encoded block state. `SnapshotService.capture` and `capturePositions` call it once per decoded block, so it is intentionally cheap when disabled and can produce millions of lines when enabled.

---

## 12. `blockWrite()`

```java
public void blockWrite(String world, int x, int y, int z, String state)
```

Same guard and message shape as `blockRead`, with category `block-write`. `ApplyService.Job.process` calls it after a successful `setBlock`, passing `minecraft:air` for removals and `BlockState.toString()` for placements, which makes a block-level audit of an apply possible.

---

## 13. `close()`

```java
@Override
public void close()
```

Synchronized so it cannot race an in-flight `log`. It flushes and closes the writer, ignoring `IOException` because there is nothing useful to do during shutdown, then sets `writer` back to `null` and leaves the flags untouched; calling it twice is harmless. `GitServices.close()` invokes it before shutting down the IO executor.

---
