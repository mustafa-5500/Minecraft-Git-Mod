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

TODO: Describe how `DebugLogger` is instantiated and used.

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
| [`MinecraftGit`](../MinecraftGit.md) | TODO: describe purpose |
| `BufferedWriter` | TODO: describe purpose |
| `IOException` | TODO: describe purpose |
| `StandardCharsets` | TODO: describe purpose |
| `Files` | TODO: describe purpose |
| `Path` | TODO: describe purpose |
| `StandardOpenOption` | TODO: describe purpose |
| `Instant` | TODO: describe purpose |
| `ZoneId` | TODO: describe purpose |
| `DateTimeFormatter` | TODO: describe purpose |
| `Locale` | TODO: describe purpose |

---

## 3. Class Declaration

```java
public final class DebugLogger implements AutoCloseable {
```

- **`implements AutoCloseable`** — TODO: explain interfaces.

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
| `STAMP` | `DateTimeFormatter` | TODO: describe |
| `logsRoot` | `Path` | TODO: describe |
| `file` | `Path` | TODO: describe |
| `enabled` | `volatile boolean` | TODO: describe |
| `blocks` | `volatile boolean` | TODO: describe |
| `writer` | `BufferedWriter` | TODO: describe |
| `buffered` | `int` | TODO: describe |
| `failed` | `boolean` | TODO: describe |

---

## 5. Constructor

```java
public DebugLogger(Path logsRoot, boolean enabled, boolean blocks)
```

TODO: Provide detailed design explanation for `DebugLogger`.

---

## 6. `file()`

```java
public Path file()
```

TODO: Provide detailed design explanation for `file`.

---

## 7. `enabled()`

```java
public boolean enabled()
```

TODO: Provide detailed design explanation for `enabled`.

---

## 8. `setEnabled()`

```java
public void setEnabled(boolean enabled)
```

TODO: Provide detailed design explanation for `setEnabled`.

---

## 9. `setBlocks()`

```java
public void setBlocks(boolean blocks)
```

TODO: Provide detailed design explanation for `setBlocks`.

---

## 10. `log()`

```java
public void log(String category, String format, Object... args)
```

TODO: Provide detailed design explanation for `log`.

---

## 11. `blockRead()`

```java
public void blockRead(String world, int x, int y, int z, String state)
```

TODO: Provide detailed design explanation for `blockRead`.

---

## 12. `blockWrite()`

```java
public void blockWrite(String world, int x, int y, int z, String state)
```

TODO: Provide detailed design explanation for `blockWrite`.

---

## 13. `close()`

```java
@Override
public void close()
```

TODO: Provide detailed design explanation for `close`.

---
