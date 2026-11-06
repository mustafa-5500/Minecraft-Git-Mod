# RepoMetadataStore — Software Detailed Design

> **API Documentation:** [RepoMetadataStore.md](./RepoMetadataStore.md)  
> **Source File:** [RepoMetadataStore.java](../../../../../../../src/main/java/org/almond/minecraftgit/repo/RepoMetadataStore.java)

## Table of Contents

- [Usage](#usage)
- [1. Overview](#1-overview)
- [2. Package Declaration & Imports](#2-package-declaration-imports)
- [3. Class Declaration](#3-class-declaration)
- [4. Instance Fields](#4-instance-fields)
- [5. Constructor](#5-constructor)
- [6. `load()`](#6-load)
- [7. `save()`](#7-save)
- [8. `file()`](#8-file)

## Usage

TODO: Describe how `RepoMetadataStore` is instantiated and used.

---

## 1. Overview

Reads and writes local repository metadata, one JSON file per world.

---

## 2. Package Declaration & Imports

```java
package org.almond.minecraftgit.repo;
```

```java
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.almond.minecraftgit.debug.DebugLogger;
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
| [`DebugLogger`](../debug/DebugLogger.md) | TODO: describe purpose |
| `IOException` | TODO: describe purpose |
| `StandardCharsets` | TODO: describe purpose |
| `AtomicMoveNotSupportedException` | TODO: describe purpose |
| `Files` | TODO: describe purpose |
| `Path` | TODO: describe purpose |
| `StandardCopyOption` | TODO: describe purpose |

---

## 3. Class Declaration

```java
public final class RepoMetadataStore {
```


---

## 4. Instance Fields

```java
private final Path metadataRoot;
private final DebugLogger debug;
private final Gson gson;
```

| Field | Type | Description |
|-------|------|-------------|
| `metadataRoot` | `Path` | TODO: describe |
| `debug` | [`DebugLogger`](../debug/DebugLogger.md) | TODO: describe |
| `gson` | `Gson` | TODO: describe |

---

## 5. Constructor

```java
public RepoMetadataStore(Path metadataRoot, DebugLogger debug)
```

TODO: Provide detailed design explanation for `RepoMetadataStore`.

---

## 6. `load()`

```java
public RepoMetadata load(String world, RepoOptions defaults)
```

TODO: Provide detailed design explanation for `load`.

---

## 7. `save()`

```java
public void save(RepoMetadata metadata)
```

TODO: Provide detailed design explanation for `save`.

---

## 8. `file()`

```java
public Path file(String world)
```

TODO: Provide detailed design explanation for `file`.

---
