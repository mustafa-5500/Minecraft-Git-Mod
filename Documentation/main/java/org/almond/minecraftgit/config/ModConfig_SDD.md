# ModConfig — Software Detailed Design

> **API Documentation:** [ModConfig.md](./ModConfig.md)  
> **Source File:** [ModConfig.java](../../../../../../../src/main/java/org/almond/minecraftgit/config/ModConfig.java)

## Table of Contents

- [Usage](#usage)
- [1. Overview](#1-overview)
- [2. Package Declaration & Imports](#2-package-declaration-imports)
- [3. Class Declaration](#3-class-declaration)
- [4. Instance Fields](#4-instance-fields)
- [5. Constructor](#5-constructor)
- [6. `load()`](#6-load)
- [7. `root()`](#7-root)
- [8. `file()`](#8-file)
- [9. `reposRoot()`](#9-reposroot)
- [10. `metaRoot()`](#10-metaroot)
- [11. `selectionsRoot()`](#11-selectionsroot)
- [12. `stagingRoot()`](#12-stagingroot)
- [13. `backupsRoot()`](#13-backupsroot)
- [14. `tmpRoot()`](#14-tmproot)
- [15. `logsRoot()`](#15-logsroot)
- [16. `normalize()`](#16-normalize)
- [17. `save()`](#17-save)
- [18. `nonBlank()`](#18-nonblank)
- [19. `positive()`](#19-positive)
- [20. `positive()`](#20-positive)

## Usage

`MinecraftGitClient.onInitializeClient` calls `ModConfig.load` with `FabricLoader.getInstance().getConfigDir().resolve(MinecraftGit.MOD_ID)` before building `GitServices`, and that single instance is passed to every service; commands mutate fields and call `save()`. On a successful load it rewrites `config.json` with normalized values, so the file also documents the schema.

---

## 1. Overview

Typed view over `config/minecraftgit/config.json`.
The fields are the persisted schema. Defaults are deliberately conservative
for client memory/tick budgets; the plugin's much larger caps assume a server
thread that can spend seconds on a capture.

---

## 2. Package Declaration & Imports

```java
package org.almond.minecraftgit.config;
```

```java
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.almond.minecraftgit.MinecraftGit;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
```

| Import | Purpose |
|--------|---------|
| `Gson` | Serializes and deserializes the config object to JSON. |
| `GsonBuilder` | Creates the pretty-printing, non-HTML-escaping Gson instance. |
| `JsonSyntaxException` | Detected on load so corrupt JSON falls back to defaults. |
| [`MinecraftGit`](../MinecraftGit.md) | Provides `MOD_ID` and the shared `LOGGER` for warnings. |
| `IOException` | Caught around directory and file read-write. |
| `StandardCharsets` | UTF-8 for reading and writing config JSON. |
| `AtomicMoveNotSupportedException` | Triggers fallback to a non-atomic `Files.move`. |
| `Files` | Creates dirs, reads the file, writes the temp file and moves it. |
| `Path` | Config root and file locations. |
| `StandardCopyOption` | `REPLACE_EXISTING` and `ATOMIC_MOVE` options for the save move. |

---

## 3. Class Declaration

```java
public final class ModConfig {
```


---

## 4. Instance Fields

```java
public boolean debug;
public boolean debugBlocks;
public String branch;
public String defaultRemote;
public String wandItem;
public String prefix;
public String committerName;
public String committerEmail;
public int maxCuboidsPerSelection;
public long maxVolumePerSelection;
public long maxBlocksPerCommit;
public int maxEntitiesPerSelection;
public int maxBlockEntityBytes;
public long statusMaxVolume;
public int blocksPerTick;
public int maxBackups;
public boolean renderSelections;
public int renderBudget;
private transient Path root;
private transient Gson gson;
```

| Field | Type | Description |
|-------|------|-------------|
| `debug` | `boolean` | Persisted flag (`false` default) read when `DebugLogger` is constructed. |
| `debugBlocks` | `boolean` | Persisted per-block flag (`false`), toggled separately from `debug`. |
| `branch` | `String` | Persisted branch default `main`; non-blank after normalization. |
| `defaultRemote` | `String` | Persisted remote name default `origin`; non-blank after normalization. |
| `wandItem` | `String` | Persisted wand registry id default `minecraft:wooden_shovel`. |
| `prefix` | `String` | Persisted chat prefix default `[Git] `; null becomes empty. |
| `committerName` | `String` | Persisted Git identity name default `MinecraftGit`. |
| `committerEmail` | `String` | Persisted Git identity email default `minecraftgit@localhost`. |
| `maxCuboidsPerSelection` | `int` | Persisted capture guard default 64; positive after normalization. |
| `maxVolumePerSelection` | `long` | Persisted per-selection volume guard default 1,000,000 blocks. |
| `maxBlocksPerCommit` | `long` | Persisted per-capture block guard default 2,000,000. |
| `maxEntitiesPerSelection` | `int` | Persisted entity cap default 2000. |
| `maxBlockEntityBytes` | `int` | Persisted block-entity payload cap default 262,144 bytes. |
| `statusMaxVolume` | `long` | Persisted `/git status` capture threshold default 250,000 blocks. |
| `blocksPerTick` | `int` | Persisted apply throttle default 4096 block writes per tick. |
| `maxBackups` | `int` | Persisted per-selection backup retention default 10. |
| `renderSelections` | `boolean` | Persisted outline-rendering toggle default true. |
| `renderBudget` | `int` | Persisted cap on outline particle draws per render pass, default 256. |
| `root` | `transient Path` | Transient absolute data root; Gson skips it and `load` sets it. |
| `gson` | `transient Gson` | Transient shared Gson; skipped by serialization and set by `load`. |

---

## 5. Constructor

```java
private ModConfig()
```

Private no-arg constructor for Gson reflection and `load`; defaults live in field initializers so a JSON file may omit any key, and the transient `root`/`gson` stay null until `load` assigns them.

---

## 6. `load()`

```java
public static ModConfig load(Path root)
```

Load the config, creating it with defaults when it does not exist.
A corrupt file is reported once and defaults are used; the old file is
left untouched so a player can fix it.

---

## 7. `root()`

```java
public Path root()
```

Returns the transient root installed by `load`; path accessors resolve against it and `save` silently no-ops while it is null.

---

## 8. `file()`

```java
public Path file()
```

Returns `<root>/config.json`, the exact file read by `load` and written atomically by `save`.

---

## 9. `reposRoot()`

```java
public Path reposRoot()
```

Returns `<root>/repos`; `RepoManager` uses it as the repository parent, keeping repositories outside the save folder.

---

## 10. `metaRoot()`

```java
public Path metaRoot()
```

Returns `<root>/meta`, where `RepoManager` stores per-world repository settings and the active selection.

---

## 11. `selectionsRoot()`

```java
public Path selectionsRoot()
```

Returns `<root>/selections`, the per-world/player selection metadata root.

---

## 12. `stagingRoot()`

```java
public Path stagingRoot()
```

Returns `<root>/staging`, passed to `StagingService` as the root for gzipped staged snapshots.

---

## 13. `backupsRoot()`

```java
public Path backupsRoot()
```

Returns `<root>/backups`, passed to `BackupService` as the pre-apply backup root.

---

## 14. `tmpRoot()`

```java
public Path tmpRoot()
```

Returns `<root>/tmp`, used for transient commit and pull trees that are deleted after use.

---

## 15. `logsRoot()`

```java
public Path logsRoot()
```

Returns `<root>/logs`, passed to `DebugLogger` for optional log files.

---

## 16. `normalize()`

```java
public void normalize()
```

Fill in blanks and reject non-positive sizes.

---

## 17. `save()`

```java
public void save()
```

`save` is synchronized, creates the root, writes the Gson JSON to `config.json.tmp` in UTF-8 and promotes it with `REPLACE_EXISTING` plus `ATOMIC_MOVE`; if the filesystem rejects an atomic move it retries a plain replace. IO failures are logged through `MinecraftGit.LOGGER` and swallowed, and the method returns without writing when `root`/`gson` are unset.

---

## 18. `nonBlank()`

```java
private static String nonBlank(String value, String fallback)
```

Guard used by `normalize` for string fields: returns `fallback` for null or blank input, otherwise the original value (not trimmed).

---

## 19. `positive()`

```java
private static int positive(int value, int fallback)
```

Guard used by `normalize` for int limits: returns `fallback` unless `value > 0`.

---

## 20. `positive()`

```java
private static long positive(long value, long fallback)
```

Long overload of the same guard, used for volume and block limits; returns `fallback` unless `value > 0`.

---
