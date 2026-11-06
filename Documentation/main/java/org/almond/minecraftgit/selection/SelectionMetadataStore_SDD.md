# SelectionMetadataStore — Software Detailed Design

> **API Documentation:** [SelectionMetadataStore.md](./SelectionMetadataStore.md)  
> **Source File:** [SelectionMetadataStore.java](../../../../../../../src/main/java/org/almond/minecraftgit/selection/SelectionMetadataStore.java)

## Table of Contents

- [Usage](#usage)
- [1. Overview](#1-overview)
- [2. Package Declaration & Imports](#2-package-declaration-imports)
- [3. Class Declaration](#3-class-declaration)
- [4. Instance Fields](#4-instance-fields)
- [5. Constructor](#5-constructor)
- [6. `file()`](#6-file)
- [7. `load()`](#7-load)
- [8. `loadFile()`](#8-loadfile)
- [9. `loadAll()`](#9-loadall)
- [10. `save()`](#10-save)
- [11. `ownerFromFileName()`](#11-ownerfromfilename)

## Usage

TODO: Describe how `SelectionMetadataStore` is instantiated and used.

---

## 1. Overview

Machine-local selection state. This is intentionally stored outside the
repository: it records absolute world coordinates, owners, and the current
origin, none of which belong in a shared commit.

---

## 2. Package Declaration & Imports

```java
package org.almond.minecraftgit.selection;
```

```java
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.almond.minecraftgit.model.Selection;
import org.almond.minecraftgit.util.Names;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
```

| Import | Purpose |
|--------|---------|
| `Gson` | TODO: describe purpose |
| `GsonBuilder` | TODO: describe purpose |
| `JsonArray` | TODO: describe purpose |
| `JsonObject` | TODO: describe purpose |
| `JsonParser` | TODO: describe purpose |
| `JsonSyntaxException` | TODO: describe purpose |
| [`Selection`](../model/Selection.md) | TODO: describe purpose |
| [`Names`](../util/Names.md) | TODO: describe purpose |
| `IOException` | TODO: describe purpose |
| `StandardCharsets` | TODO: describe purpose |
| `AtomicMoveNotSupportedException` | TODO: describe purpose |
| `Files` | TODO: describe purpose |
| `Path` | TODO: describe purpose |
| `StandardCopyOption` | TODO: describe purpose |
| `ArrayList` | TODO: describe purpose |
| `List` | TODO: describe purpose |
| `UUID` | TODO: describe purpose |
| `Stream` | TODO: describe purpose |

---

## 3. Class Declaration

```java
public final class SelectionMetadataStore {
```


---

## 4. Instance Fields

```java
public static final int FORMAT_VERSION;
private final Path root;
private final Gson gson;
```

| Field | Type | Description |
|-------|------|-------------|
| `FORMAT_VERSION` | `int` | TODO: describe |
| `root` | `Path` | TODO: describe |
| `gson` | `Gson` | TODO: describe |

---

## 5. Constructor

```java
public SelectionMetadataStore(Path root)
```

TODO: Provide detailed design explanation for `SelectionMetadataStore`.

---

## 6. `file()`

```java
public Path file(String world, UUID owner)
```

TODO: Provide detailed design explanation for `file`.

---

## 7. `load()`

```java
public List<Selection> load(String world, UUID owner)
```

TODO: Provide detailed design explanation for `load`.

---

## 8. `loadFile()`

```java
public List<Selection> loadFile(Path file, String world, UUID fallbackOwner, String fallbackOwnerName)
```

TODO: Provide detailed design explanation for `loadFile`.

---

## 9. `loadAll()`

```java
public List<Selection> loadAll(String world)
```

All players' selections in a world. Used by status/pull for console contexts.

---

## 10. `save()`

```java
public void save(String world, UUID owner, String ownerName, List<Selection> selections)
```

TODO: Provide detailed design explanation for `save`.

---

## 11. `ownerFromFileName()`

```java
private static UUID ownerFromFileName(Path file)
```

TODO: Provide detailed design explanation for `ownerFromFileName`.

---
