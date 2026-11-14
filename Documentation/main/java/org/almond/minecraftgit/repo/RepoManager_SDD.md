# RepoManager — Software Detailed Design

> **API Documentation:** [RepoManager.md](./RepoManager.md)  
> **Source File:** [RepoManager.java](../../../../../../../src/main/java/org/almond/minecraftgit/repo/RepoManager.java)

## Table of Contents

- [Usage](#usage)
- [1. Overview](#1-overview)
- [2. Package Declaration & Imports](#2-package-declaration-imports)
- [3. Class Declaration](#3-class-declaration)
- [4. Instance Fields](#4-instance-fields)
- [5. Constructor](#5-constructor)
- [6. `locator()`](#6-locator)
- [7. `repoPath()`](#7-repopath)
- [8. `isInitialized()`](#8-isinitialized)
- [9. `metadata()`](#9-metadata)
- [10. `saveMetadata()`](#10-savemetadata)
- [11. `service()`](#11-service)
- [12. `init()`](#12-init)
- [13. `setAuthor()`](#13-setauthor)
- [14. `setLastSelection()`](#14-setlastselection)
- [15. `lastSelection()`](#15-lastselection)
- [16. `reload()`](#16-reload)
- [17. `defaults()`](#17-defaults)
- [18. `isDefaultIdentity()`](#18-isdefaultidentity)
- [19. `close()`](#19-close)

## Usage

Created once by `GitServices` and exposed through `services.repos()`; commands and services call it instead of constructing `RepoService` themselves. `GitServices.close()` calls `RepoManager.close()` so JGit handles are released when the client shuts down.

```java
RepoManager repos = services.repos();
if (!repos.isInitialized(world.id())) {
    repos.init(world.id(), player.getName().getString(), null);
}
repos.service(world.id()).info();
```

---

## 1. Overview

Owns the per-world-id `RepoService` instances and their local metadata.
A "world" here is the client's `<world>__<dimension>` key, not a
server world object.

---

## 2. Package Declaration & Imports

```java
package org.almond.minecraftgit.repo;
```

```java
import org.almond.minecraftgit.config.ModConfig;
import org.almond.minecraftgit.debug.DebugLogger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
```

| Import | Purpose |
|--------|---------|
| [`ModConfig`](../config/ModConfig.md) | Source of the repository/metadata roots, default branch and remote, and committer identity used to build defaults. |
| [`DebugLogger`](../debug/DebugLogger.md) | Diagnostic log shared with the metadata store and every `RepoService` created by this manager. |
| `Files` | Used by `isInitialized` to test `repoPath(worldId)/.git` without opening the repository. |
| `Path` | Return type of `repoPath` and the handle passed to each `RepoService`. |
| `Map` | Declared type of the two caches so their lookup semantics are obvious. |
| `ConcurrentHashMap` | Backing implementation of both caches, allowing command threads to create services/metadata without external locking. |

---

## 3. Class Declaration

```java
public final class RepoManager implements AutoCloseable {
```

- **`implements AutoCloseable`** — Lifecycle belongs to the caller: `close()` closes every cached `RepoService` so JGit file handles are released, and the manager may then be discarded.

---

## 4. Instance Fields

```java
private final ModConfig config;
private final DebugLogger debug;
private final RepoLocator locator;
private final RepoMetadataStore metadataStore;
private final Map<String, RepoService> services;
private final Map<String, RepoMetadata> metadataCache;
```

| Field | Type | Description |
|-------|------|-------------|
| `config` | [`ModConfig`](../config/ModConfig.md) | Read for `reposRoot()`, `metaRoot()` and the default branch/remote/committer values; never mutated here. |
| `debug` | [`DebugLogger`](../debug/DebugLogger.md) | Passed to the metadata store and to each new `RepoService`; all repository logging goes through it. |
| `locator` | [`RepoLocator`](RepoLocator.md) | Resolves `<world>` directory and metadata file names; its roots are absolute and normalized once. |
| `metadataStore` | [`RepoMetadataStore`](RepoMetadataStore.md) | Performs the JSON load/save behind `metadata()` and `saveMetadata()`; owns no cache itself. |
| `services` | `Map<String, RepoService>` | World id → open repository facade. Entries are created by `service()` and dropped by `reload()`/`close()`. |
| `metadataCache` | `Map<String, RepoMetadata>` | World id → normalized metadata object; guarantees one load per world and a single shared mutable instance. |

---

## 5. Constructor

```java
public RepoManager(ModConfig config, DebugLogger debug)
```

Stores the configuration and logger and derives the two collaborators: `new RepoLocator(config.reposRoot(), config.metaRoot())` and `new RepoMetadataStore(locator.metadataRoot(), debug)`. The constructor performs no IO, creates no directories and opens no repository, so constructing the manager is cheap and safe during client startup; the caches start empty.

---

## 6. `locator()`

```java
public RepoLocator locator()
```

Exposes the path policy used internally to construct the metadata store and resolve `repoPath`. It is an accessor rather than direct field use so callers needing `metadataPath` or the roots get the same normalized instance; the repository commands themselves go through `RepoManager.repoPath`.

---

## 7. `repoPath()`

```java
public Path repoPath(String worldId)
```

One-line delegation to `RepoLocator.repoPath`, which sanitizes the world id with `Names.safeWorldName` and resolves it against the configured `repos` root. The locator normalized the root in its constructor, so the result is absolute. No directory is created; `init` is the only method that asks JGit to create it.

---

## 8. `isInitialized()`

```java
public boolean isInitialized(String worldId)
```

A cheap guard used by every repository command before doing work. If a `RepoService` is already cached, the manager trusts its `isInitialized()` (the same `.git` directory check); otherwise it checks `Files.isDirectory(repoPath(worldId).resolve(".git"))`. This avoids opening JGit merely to answer the question and never creates a repository.

---

## 9. `metadata()`

```java
public RepoMetadata metadata(String worldId)
```

The cache is the single in-memory copy for a world. `ConcurrentHashMap.computeIfAbsent` guarantees one load per id even when command threads race, and the mapping function delegates to `RepoMetadataStore.load(id, defaults())`. If the file is missing the store returns `RepoMetadata.defaults`; if it is partial the store normalizes it before caching. A `RepoException` thrown by the loader propagates out of `computeIfAbsent` and nothing is cached, so a later call retries the read.

---

## 10. `saveMetadata()`

```java
public void saveMetadata(RepoMetadata metadata)
```

Write-through design: the store performs the atomic JSON write first, then `metadataCache.put(metadata.world(), metadata)` replaces the cached instance. Ordering matters — a failed write leaves the cache untouched and the exception visible to the command. Because `metadata()` returns the cached instance and callers mutate it in place before saving (`init`, `setAuthor`, `setLastSelection`), the cache already contains the values being serialized.

---

## 11. `service()`

```java
public RepoService service(String worldId)
```

Lazily creates and caches one `RepoService` per world, built from `metadata(worldId)` and `locator.repoPath(id)`:

- `RepoOptions` is constructed from `metadata.branch()`, `metadata.remoteName()`, `metadata.authorName()` and `metadata.authorEmail()`.
- The options are fixed at creation time; later metadata edits affect the service only through explicit calls such as `setAuthor`.
- The constructor itself does no JGit work; `RepoService` opens the repository on first use and throws `RepoException` if `init` has not run.

`computeIfAbsent` makes concurrent first access safe, but the returned service serializes its own public methods with an internal lock.

---

## 12. `init()`

```java
public RepoInfo init(String worldId, String playerName, String playerEmail)
```

Create or open the repository. When the repository still uses the default
identity, adopt the local player's name so commits are attributable without
an extra setup step.

The orchestration is deliberately ordered so the bootstrap commit has an author:

1. Load metadata and, while `isDefaultIdentity` is true, adopt `playerName` (falling back to an email derived by replacing characters outside `[A-Za-z0-9_.-]` and appending `@minecraftgit.local`) and save.
2. Get/create the `RepoService` and call `setAuthor` with the metadata identity.
3. Call `RepoService.init()`, which creates the repository with a README/`.gitattributes` bootstrap commit or opens an existing one.
4. Copy the resulting branch (using `config.branch` when the service reports blank), author name/email and non-blank `remoteUrl` back into metadata, save, and return the `RepoInfo`.

Any failure surfaces as `RepoException` from the service layer; metadata may already have been saved with the adopted identity, which is harmless because it is local-only.

---

## 13. `setAuthor()`

```java
public void setAuthor(String worldId, String name, String email)
```

Persists the new identity to metadata first, then propagates it: the cached service is updated if present; otherwise a service is created only when `isInitialized(worldId)` is true, so configuring an author before `/git init` does not open an empty repository. `RepoService.setAuthor` rejects blank values with `RepoException` after the metadata write, so invalid values can still be stored locally but never reach Git.

---

## 14. `setLastSelection()`

```java
public void setLastSelection(String worldId, String selection)
```

Stores the wand's active selection name in local metadata. `null` is coerced to `""` because Gson round-trips `null` as JSON null and callers (`SelectionManager.active`) treat blank as "no remembered selection". The manager does not validate the name; `SelectionService.find` decides whether it still exists.

---

## 15. `lastSelection()`

```java
public String lastSelection(String worldId)
```

Reads through `metadata(worldId)`, so the first call may load the world's JSON file. It returns the raw stored string (`""` when none) and performs no name validation; selection existence is checked by the caller.

---

## 16. `reload()`

```java
public void reload()
```

Tears down all runtime state: `close()` closes cached services and clears both maps, and the explicit `metadataCache.clear()` is a defensive second clear. The next command re-reads metadata from disk and creates services from the current config, which is the intended hook after config values such as the default branch or identity change.

---

## 17. `defaults()`

```java
private RepoOptions defaults()
```

Adapts mutable public `ModConfig` fields to the immutable `RepoOptions` record. `RepoOptions` canonicalizes blanks to `main`/`origin`/`MinecraftGit`/`minecraftgit@localhost`, so the manager does not duplicate those fallbacks. The method is called only from the metadata loader.

---

## 18. `isDefaultIdentity()`

```java
private static boolean isDefaultIdentity(String authorName)
```

The identity-adoption predicate used by `init`: `null`, blank or the literal `"MinecraftGit"` means the world has not been personalized. It intentionally does not compare emails, because older metadata may hold a custom name with the default email.

---

## 19. `close()`

```java
@Override
public void close()
```

Iterates the service cache calling `RepoService.close()` (which closes the JGit `Git` handle and nulls its fields) and then clears both caches. Idempotent: a second call iterates empty maps. No exception is expected from `RepoService.close()`, so shutdown is not blocked by a failing repository.

---
