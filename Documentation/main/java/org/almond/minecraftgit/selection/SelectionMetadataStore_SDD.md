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

Instantiated by `SelectionService` with `config.selectionsRoot()`; `SelectionService` is the normal entry point, while sync and status code may call `loadAll` on the store for console-side reads.

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
| `Gson` | Serializes the selections array and the surrounding wrapper tree to and from JSON. |
| `GsonBuilder` | Builds a pretty-printing Gson instance with HTML escaping disabled. |
| `JsonArray` | Represents the wrapper's `selections` array while reading. |
| `JsonObject` | Represents the versioned wrapper object read and written around the array. |
| `JsonParser` | Parses the outer JSON string before Gson maps the array to `Selection[]`. |
| `JsonSyntaxException` | Caught together with `IllegalStateException` to report a corrupt metadata file. |
| [`Selection`](../model/Selection.md) | Element type serialized and deserialized, then normalized on load. |
| [`Names`](../util/Names.md) | Sanitizes world ids into filesystem-safe directory names. |
| `IOException` | Caught around every file operation and wrapped as a `SelectionException`. |
| `StandardCharsets` | UTF-8 charset used for both reads and writes. |
| `AtomicMoveNotSupportedException` | Signals that the filesystem rejects atomic moves, triggering the plain-replace fallback. |
| `Files` | Existence checks, directory creation, reads/writes and the final move. |
| `Path` | File and directory handles for the root and per-owner files. |
| `StandardCopyOption` | Provides `REPLACE_EXISTING` and `ATOMIC_MOVE` semantics for the save move. |
| `ArrayList` | Mutable result lists, including the empty-list defaults on missing files. |
| `List` | API result and parameter type for collections of selections. |
| `UUID` | Owner identity used in paths and in normalization fallbacks. |
| `Stream` | Lists the world directory's JSON files in `loadAll`. |

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
| `FORMAT_VERSION` | `int` | Schema marker (`1`) written as the `format` property; currently informational and reserved for migrations. |
| `root` | `Path` | Absolute normalized selections directory under which all paths resolve. |
| `gson` | `Gson` | Shared pretty-printing Gson with HTML escaping disabled, used for the wrapper and the array. |

---

## 5. Constructor

```java
public SelectionMetadataStore(Path root)
```

Stores `root.toAbsolutePath().normalize()` and constructs the Gson instance; no directories are created and no files are read, so construction is cheap and IO-free.

---

## 6. `file()`

```java
public Path file(String world, UUID owner)
```

Builds `<sanitized world>/<owner>.json`, delegating sanitization to `Names.safeWorldName`, which replaces anything outside `[A-Za-z0-9_.-]` with `_`. The UUID is used raw because its format is already filename-safe.

---

## 7. `load()`

```java
public List<Selection> load(String world, UUID owner)
```

Convenience wrapper that calls `loadFile` for the player's file, passing the world as the fallback world and an empty owner-name fallback; a missing file returns an empty mutable list rather than an error. All normalization fallbacks refer to the requested world and owner.

---

## 8. `loadFile()`

```java
public List<Selection> loadFile(Path file, String world, UUID fallbackOwner, String fallbackOwnerName)
```

Guards with `Files.isRegularFile`, reads UTF-8, parses the wrapper, and treats a missing `selections` array as empty. Each parsed entry is passed through `Selection.normalize` with the supplied world, owner and name fallbacks; parse/state failures produce a delete-the-file error while IO failures report the path and message.

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

Writes a versioned wrapper object containing `format`, `world`, `owner`, `ownerName` and `selections` to a `.tmp` sibling, then moves it onto the target with `ATOMIC_MOVE` and `REPLACE_EXISTING`, falling back to a plain replace when atomic moves are unsupported. This prevents a crash from leaving a half-written JSON file, and IO errors are surfaced as `SelectionException`.

---

## 11. `ownerFromFileName()`

```java
private static UUID ownerFromFileName(Path file)
```

Strips a trailing `.json` and parses the stem as a UUID, returning the all-zero UUID on `IllegalArgumentException` so an unexpected file cannot abort a `loadAll` scan.

---
