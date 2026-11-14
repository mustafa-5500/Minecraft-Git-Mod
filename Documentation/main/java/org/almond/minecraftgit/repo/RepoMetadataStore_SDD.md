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

Built by `RepoManager` with `locator.metadataRoot()` and used only through `RepoManager.metadata`/`saveMetadata`, which add the in-memory cache. The store is stateless apart from its root and logger, so it can be shared and called from command threads without synchronization.

```java
Path file = store.file(worldId);
RepoMetadata metadata = store.load(worldId, options);
store.save(metadata); // atomically replaces file
```

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
| `Gson` | Serializes/deserializes `RepoMetadata` field-for-field; the field name is the JSON schema. |
| `GsonBuilder` | Builds the serializer once with pretty printing and `disableHtmlEscaping`, keeping the files diffable and URLs readable. |
| `JsonSyntaxException` | Caught in `load` and converted into a `RepoException` telling the player the file is not valid JSON. |
| [`DebugLogger`](../debug/DebugLogger.md) | Records each successful save under the `repo` category; useful when a world's metadata seems stale. |
| `IOException` | Common failure type of all file operations; caught in both `load` and `save` and wrapped as `RepoException`. |
| `StandardCharsets` | Forces UTF-8 for reads and writes so metadata is portable across platforms. |
| `AtomicMoveNotSupportedException` | Signals a filesystem without atomic rename; caught in `save` to retry with a plain replacing move. |
| `Files` | Existence check, directory creation, read/write and move operations. |
| `Path` | Metadata root and per-world file handle. |
| `StandardCopyOption` | `REPLACE_EXISTING` always, plus `ATOMIC_MOVE` on the first attempt so readers never see a partial file. |

---

## 3. Class Declaration

```java
public final class RepoMetadataStore {
```

A small, stateless IO adapter: no caches, no locks, no repository access. It is `final` because it is an internal collaborator of `RepoManager`. The one-file-per-world design keeps metadata independent of the Git repository, so it is never committed and can differ per machine.

---

## 4. Instance Fields

```java
private final Path metadataRoot;
private final DebugLogger debug;
private final Gson gson;
```

| Field | Type | Description |
|-------|------|-------------|
| `metadataRoot` | `Path` | Directory used by `file()` and created by `save()`; not normalized here because `RepoLocator` already did that. |
| `debug` | [`DebugLogger`](../debug/DebugLogger.md) | Success-only logger; failures are surfaced to the caller as `RepoException` instead of being logged here. |
| `gson` | `Gson` | Single thread-safe serializer with pretty printing and HTML escaping disabled. |

---

## 5. Constructor

```java
public RepoMetadataStore(Path metadataRoot, DebugLogger debug)
```

Assigns the two collaborators; the `Gson` instance is created by its field initializer. No directory is created and no probe is made, so constructing the store during client startup cannot fail; `Files.createDirectories` happens lazily in `save`, and `load` treats a missing directory as "no metadata yet".

---

## 6. `load()`

```java
public RepoMetadata load(String world, RepoOptions defaults)
```

Data flow:

1. `file(world)` resolves the sanitized path.
2. `Files.isRegularFile` short-circuits to `RepoMetadata.defaults(world, defaults)` when the file is absent — the normal first-run path.
3. The file is read as UTF-8 and parsed with Gson.
4. A `null` result (e.g. the literal `null` document) throws `RepoException("... is empty. Delete it to recreate it.")`.
5. Otherwise `metadata.normalize(world, defaults)` repairs blanks from older or hand-edited files and is returned.

Error mapping: `JsonSyntaxException` → "not valid JSON" message with the file path; `IOException` → "Could not read ..." with the OS message. Both are `RepoException`s (user-facing), so commands print them without a stack trace. The method performs blocking file IO and is called from command/IO threads, never the render thread.

---

## 7. `save()`

```java
public void save(RepoMetadata metadata)
```

Data flow and durability:

1. `file(metadata.world())` chooses the target, so the world field — not the caller's intention — decides where data lands.
2. `Files.createDirectories(metadataRoot)` makes the first save succeed on a fresh install.
3. The JSON is written to a sibling temp file (`<name>.json.tmp`) as UTF-8.
4. `Files.move(tmp, file, REPLACE_EXISTING, ATOMIC_MOVE)` publishes it atomically; on `AtomicMoveNotSupportedException` the code retries without `ATOMIC_MOVE`, accepting a small window where a crash could leave a partially copied file.
5. `debug.log("repo", "Saved metadata for world %s", ...)` records success.

Any `IOException` in these steps is wrapped in `RepoException` with the target path. A leftover `.tmp` from a crash is simply overwritten by the next save and ignored by `load`, which only looks for the exact `.json` name.

---

## 8. `file()`

```java
public Path file(String world)
```

Resolves `metadataRoot.resolve(Names.safeWorldName(world) + ".json")`. `Names.safeWorldName` replaces every character outside `[A-Za-z0-9_.-]` with `_` and maps `null` to `world`, which keeps world ids such as `My Server__overworld` or `host:25565` inside the metadata directory and prevents path traversal. Because the naming rule mirrors `RepoLocator.metadataPath`, both classes agree on the file location without one calling the other. The method is pure path arithmetic — it does not create or check the file.

---
