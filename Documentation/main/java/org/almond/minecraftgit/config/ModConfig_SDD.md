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

TODO: Describe how `ModConfig` is instantiated and used.

---

## 1. Overview

Typed view over {@code config/minecraftgit/config.json}.
<p>The fields are the persisted schema. Defaults are deliberately conservative
for client memory/tick budgets; the plugin's much larger caps assume a server
thread that can spend seconds on a capture.</p>

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
| `Gson` | TODO: describe purpose |
| `GsonBuilder` | TODO: describe purpose |
| `JsonSyntaxException` | TODO: describe purpose |
| [`MinecraftGit`](../MinecraftGit.md) | TODO: describe purpose |
| `IOException` | TODO: describe purpose |
| `StandardCharsets` | TODO: describe purpose |
| `AtomicMoveNotSupportedException` | TODO: describe purpose |
| `Files` | TODO: describe purpose |
| `Path` | TODO: describe purpose |
| `StandardCopyOption` | TODO: describe purpose |

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
| `debug` | `boolean` | TODO: describe |
| `debugBlocks` | `boolean` | TODO: describe |
| `branch` | `String` | TODO: describe |
| `defaultRemote` | `String` | TODO: describe |
| `wandItem` | `String` | TODO: describe |
| `prefix` | `String` | TODO: describe |
| `committerName` | `String` | TODO: describe |
| `committerEmail` | `String` | TODO: describe |
| `maxCuboidsPerSelection` | `int` | TODO: describe |
| `maxVolumePerSelection` | `long` | TODO: describe |
| `maxBlocksPerCommit` | `long` | TODO: describe |
| `maxEntitiesPerSelection` | `int` | TODO: describe |
| `maxBlockEntityBytes` | `int` | TODO: describe |
| `statusMaxVolume` | `long` | TODO: describe |
| `blocksPerTick` | `int` | TODO: describe |
| `maxBackups` | `int` | TODO: describe |
| `renderSelections` | `boolean` | TODO: describe |
| `renderBudget` | `int` | TODO: describe |
| `root` | `transient Path` | TODO: describe |
| `gson` | `transient Gson` | TODO: describe |

---

## 5. Constructor

```java
private ModConfig()
```

TODO: Provide detailed design explanation for `ModConfig`.

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

TODO: Provide detailed design explanation for `root`.

---

## 8. `file()`

```java
public Path file()
```

TODO: Provide detailed design explanation for `file`.

---

## 9. `reposRoot()`

```java
public Path reposRoot()
```

TODO: Provide detailed design explanation for `reposRoot`.

---

## 10. `metaRoot()`

```java
public Path metaRoot()
```

TODO: Provide detailed design explanation for `metaRoot`.

---

## 11. `selectionsRoot()`

```java
public Path selectionsRoot()
```

TODO: Provide detailed design explanation for `selectionsRoot`.

---

## 12. `stagingRoot()`

```java
public Path stagingRoot()
```

TODO: Provide detailed design explanation for `stagingRoot`.

---

## 13. `backupsRoot()`

```java
public Path backupsRoot()
```

TODO: Provide detailed design explanation for `backupsRoot`.

---

## 14. `tmpRoot()`

```java
public Path tmpRoot()
```

TODO: Provide detailed design explanation for `tmpRoot`.

---

## 15. `logsRoot()`

```java
public Path logsRoot()
```

TODO: Provide detailed design explanation for `logsRoot`.

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

TODO: Provide detailed design explanation for `save`.

---

## 18. `nonBlank()`

```java
private static String nonBlank(String value, String fallback)
```

TODO: Provide detailed design explanation for `nonBlank`.

---

## 19. `positive()`

```java
private static int positive(int value, int fallback)
```

TODO: Provide detailed design explanation for `positive`.

---

## 20. `positive()`

```java
private static long positive(long value, long fallback)
```

TODO: Provide detailed design explanation for `positive`.

---
