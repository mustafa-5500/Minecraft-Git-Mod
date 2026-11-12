# StagingService — Software Detailed Design

> **API Documentation:** [StagingService.md](./StagingService.md)  
> **Source File:** [StagingService.java](../../../../../../../src/main/java/org/almond/minecraftgit/staging/StagingService.java)

## Table of Contents

- [Usage](#usage)
- [1. Overview](#1-overview)
- [2. Package Declaration & Imports](#2-package-declaration-imports)
- [3. Class Declaration](#3-class-declaration)
- [4. Instance Fields](#4-instance-fields)
- [5. Constructor](#5-constructor)
- [6. `root()`](#6-root)
- [7. `file()`](#7-file)
- [8. `stage()`](#8-stage)
- [9. `load()`](#9-load)
- [10. `hasStaged()`](#10-hasstaged)
- [11. `stagedNames()`](#11-stagednames)
- [12. `unstage()`](#12-unstage)
- [13. `rebaseStaged()`](#13-rebasestaged)
- [14. `restrictStaged()`](#14-restrictstaged)
- [15. `deleteAll()`](#15-deleteall)
- [16. `store()`](#16-store)

## Usage

`StagingService` is a long-lived collaborator created once at mod startup with the mod's staging root and shared by the add/rm, commit and status command paths; command handlers call `stage` after a capture and `load`, `hasStaged` or `stagedNames` before commit and status. It owns only the local staging area and never reads or writes Git objects, so staged data survives restarts and is deliberately not part of the repository.

---

## 1. Overview

Durable staging area. `/git add` writes a captured snapshot here;
`/git commit` reads it later. Files survive restarts and are keyed by
world id + player + selection, never by repository.

---

## 2. Package Declaration & Imports

```java
package org.almond.minecraftgit.staging;
```

```java
import org.almond.minecraftgit.debug.DebugLogger;
import org.almond.minecraftgit.model.BlockPos;
import org.almond.minecraftgit.model.Selection;
import org.almond.minecraftgit.model.Snapshot;
import org.almond.minecraftgit.util.Names;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
```

| Import | Purpose |
|--------|---------|
| [`DebugLogger`](../debug/DebugLogger.md) | Records staging, rebase, restrict and removal events for diagnostics. |
| [`BlockPos`](../model/BlockPos.md) | Carries the new origin for `rebaseStaged` and compares snapshot origins. |
| [`Selection`](../model/Selection.md) | Provides the name, world, owner, origin and cuboids used to validate and trim captures. |
| [`Snapshot`](../model/Snapshot.md) | The payload read from and written to staging files. |
| [`Names`](../util/Names.md) | Validates selection names and sanitizes world ids before building paths. |
| `IOException` | Caught and wrapped as `StagingException` around filesystem operations. |
| `Files` | Checks existence, reads timestamps, lists directories and deletes staged files. |
| `Path` | Represents the staging root and resolved snapshot files. |
| `FileTime` | Reads the staged file's last-modified time for `load`. |
| `Instant` | Records the staging time and provides the fallback `stagedAt`. |
| `ArrayList` | Collects selection names in `stagedNames`. |
| `List` | Returns staged names, including the empty-list case. |
| `Optional` | Represents an absent staged snapshot in `load`. |
| `UUID` | Identifies the owning player in the path layout. |
| `Stream` | Enumerates the staging directory in `stagedNames`. |

---

## 3. Class Declaration

```java
public final class StagingService {
```

The class is final and keeps only the normalized root, the shared store and the logger; durability is delegated entirely to `SnapshotStore`, and there is no per-request mutable state.

---

## 4. Instance Fields

```java
private final Path root;
private final SnapshotStore store;
private final DebugLogger debug;
```

| Field | Type | Description |
|-------|------|-------------|
| `root` | `Path` | Normalized absolute root of the staging tree; immutable after construction. |
| `store` | [`SnapshotStore`](SnapshotStore.md) | Serializer for `.snap` files, shared with the rest of the mod. |
| `debug` | [`DebugLogger`](../debug/DebugLogger.md) | Diagnostic logger. |

---

## 5. Constructor

```java
public StagingService(Path root, SnapshotStore store, DebugLogger debug)
```

Normalizes `root` with `toAbsolutePath().normalize()` and stores the injected `SnapshotStore` and `DebugLogger`; the constructor performs no I/O, so the staging tree is created lazily by `SnapshotStore.write`. This makes the service cheap to construct and safe to create before a world is loaded.

---

## 6. `root()`

```java
public Path root()
```

Exposes the normalized root for diagnostics and for callers that need to place related local data beside the staging tree; it returns the stored instance directly, so callers cannot change the path.

---

## 7. `file()`

```java
public Path file(String worldId, UUID owner, String selection)
```

Validates the selection with `Names.requireSelectionName` (converting `IllegalArgumentException` into a user-facing `StagingException`), sanitizes the world id, and resolves `root/world/owner/name.snap`. The owner is the full UUID string, so different players and worlds never collide, and the repository is deliberately absent from the key. The method is pure path arithmetic and does not create directories.

---

## 8. `stage()`

```java
public StagedSelection stage(Selection selection, Snapshot snapshot)
```

Rejects a selection without an origin or owner, resolves the target with `file`, rebases the capture onto the selection origin when it differs, and writes it through `SnapshotStore.write`; the returned `StagedSelection` is stamped `Instant.now()` only after the atomic write succeeds. The selection's tracked flag and cuboids are not modified here, so `stage` is safe to call repeatedly.

---

## 9. `load()`

```java
public Optional<StagedSelection> load(String worldId, UUID owner, String selection)
```

If the resolved path is not a regular file, returns `Optional.empty()`; otherwise it reads through `SnapshotStore.read` and uses `Files.getLastModifiedTime` as `stagedAt`, falling back to `Instant.now()` if the attribute read fails. The returned `StagedSelection` retains the resolved `Path` so callers can report or delete the exact file.

---

## 10. `hasStaged()`

```java
public boolean hasStaged(String worldId, UUID owner, String selection)
```

Pure existence check on `file(...)`; it does not validate that the file is decodable, so a corrupt `.snap` still reports as present until `load` fails.

---

## 11. `stagedNames()`

```java
public List<String> stagedNames(String worldId, UUID owner)
```

Resolves the world/player directory directly (without requiring a selection name), returns `List.of()` if it is absent, and otherwise lists directory entries whose names end in `.snap`, strips the suffix, sorts and returns them. Only the immediate directory is scanned, so orphaned `.tmp` files are ignored; an `IOException` becomes a `StagingException`.

---

## 12. `unstage()`

```java
public void unstage(String worldId, UUID owner, String selection)
```

Resolves the file and calls `Files.deleteIfExists`, logging the path even when nothing was deleted; this is the user-facing removal used when a selection is dropped. I/O errors become `StagingException` with the path in the message.

---

## 13. `rebaseStaged()`

```java
public void rebaseStaged(String worldId, UUID owner, String selection, BlockPos newOrigin)
```

If no staged file exists it returns silently; otherwise it reads the snapshot, calls `snapshot.rebase(newOrigin)` and writes the result back through the atomic store. Only relative coordinates change — absolute block and entity positions are preserved — and the rewrite is a no-op for a missing file, so a re-origin can be applied before or after staging.

---

## 14. `restrictStaged()`

```java
public void restrictStaged(String worldId, UUID owner, Selection selection)
```

Used after a cuboid is removed: it reads the staged snapshot, keeps only blocks and entities whose absolute position is inside the selection's remaining cuboids (`snapshot.retain`), rebases the survivors onto the unchanged selection origin, and writes the trimmed snapshot back. It is a no-op when no staged file exists or the selection has no origin, and the origin never moves here (E2/E6), matching the selection model's rule that only an explicit re-origin changes it.

---

## 15. `deleteAll()`

```java
public void deleteAll(String worldId, UUID owner, String selection)
```

Delegates to the same `Files.deleteIfExists` removal as `unstage` but performs no logging; it is kept for callers that clean up without wanting a debug entry. A missing file is still success, and I/O failures are wrapped in `StagingException`.

---

## 16. `store()`

```java
public SnapshotStore store()
```

Returns the injected store directly so callers can reuse the same serializer for other staging-like files; no lookup or I/O is performed.

---
