# Selection — Software Detailed Design

> **API Documentation:** [Selection.md](./Selection.md)  
> **Source File:** [Selection.java](../../../../../../../src/main/java/org/almond/minecraftgit/model/Selection.java)

## Table of Contents

- [Usage](#usage)
- [1. Overview](#1-overview)
- [2. Package Declaration & Imports](#2-package-declaration-imports)
- [3. Class Declaration](#3-class-declaration)
- [4. Instance Fields](#4-instance-fields)
- [5. Constructor](#5-constructor)
- [6. Constructor](#6-constructor)
- [7. `normalize()`](#7-normalize)
- [8. `name()`](#8-name)
- [9. `setName()`](#9-setname)
- [10. `owner()`](#10-owner)
- [11. `setOwner()`](#11-setowner)
- [12. `ownerName()`](#12-ownername)
- [13. `setOwnerName()`](#13-setownername)
- [14. `world()`](#14-world)
- [15. `setWorld()`](#15-setworld)
- [16. `origin()`](#16-origin)
- [17. `setOrigin()`](#17-setorigin)
- [18. `hasOrigin()`](#18-hasorigin)
- [19. `cuboids()`](#19-cuboids)
- [20. `setCuboids()`](#20-setcuboids)
- [21. `addCuboid()`](#21-addcuboid)
- [22. `removeCuboidAt()`](#22-removecuboidat)
- [23. `reorigin()`](#23-reorigin)
- [24. `isTracked()`](#24-istracked)
- [25. `setTracked()`](#25-settracked)
- [26. `createdAt()`](#26-createdat)
- [27. `updatedAt()`](#27-updatedat)
- [28. `touch()`](#28-touch)
- [29. `cuboidCount()`](#29-cuboidcount)
- [30. `volume()`](#30-volume)
- [31. `boundsMin()`](#31-boundsmin)
- [32. `boundsMax()`](#32-boundsmax)
- [33. `contains()`](#33-contains)
- [34. `relative()`](#34-relative)
- [35. `absolute()`](#35-absolute)
- [36. `byName()`](#36-byname)
- [37. `toString()`](#37-tostring)

## Usage

`SelectionService` creates selections with `new Selection(name, owner, ownerName, worldId, origin)` and appends cuboids through `addCuboid`; `SelectionMetadataStore` serializes the object to the per-world, per-owner JSON file and restores it through the no-arg constructor plus `normalize`. The type is a mutable, Gson-friendly aggregate whose identity fields (`name`, `owner`, `ownerName`, `world`) and geometry (`origin`, `cuboids`) are plain fields, with all conversion arithmetic delegated to `BlockPos` and all ordering delegated to `Cuboid.SMALLEST_CORNER_ORDER`.

---

## 1. Overview

A named, multi-cuboid build selection owned by one player in one world.
Selections are persisted per world id + player in the mod data folder and
are never committed. The origin is the absolute world position that relative
repository coordinates are measured from. It is set by the first cuboid and
deliberately does *not* move when that cuboid is removed (E2); only an
explicit re-origin or a new selection changes it.

---

## 2. Package Declaration & Imports

```java
package org.almond.minecraftgit.model;
```

```java
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
```

| Import | Purpose |
|--------|---------|
| `Instant` | Produces the ISO-8601 text for `createdAt`/`updatedAt` via `Instant.now().toString()`. |
| `ArrayList` | Creates the initial mutable cuboid list and the defensive or null-replacement copies used by `setCuboids` and `normalize`. |
| `Collections` | Wraps the backing list with `unmodifiableList` so `cuboids()` exposes a read-only view. |
| `Comparator` | Builds the case-insensitive `byName()` comparator with `Comparator.comparing`. |
| `List` | Declares the cuboid collection type and the list parameters/return types of the API. |
| `UUID` | Identifies the owning player, which forms half of the world + owner metadata persistence key. |

---

## 3. Class Declaration

```java
public final class Selection {
```

`Selection` is a `final`, mutable, POJO-style aggregate with no superclass: it deliberately exposes plain fields through accessors so Gson can serialize and deserialize it by field name, and it keeps all behaviour (sorting, origin seeding, saturation, coordinate conversion) in small methods rather than in a repository or service. Being `final` prevents subclassing from changing the persistence shape or the origin rules that the rest of the mod relies on.

---

## 4. Instance Fields

```java
private String name;
private UUID owner;
private String ownerName;
private String world;
private BlockPos origin;
private List<Cuboid> cuboids;
private boolean tracked;
private String createdAt;
private String updatedAt;
```

| Field | Type | Description |
|-------|------|-------------|
| `name` | `String` | Per-owner display name and lookup key; `SelectionService` enforces case-insensitive uniqueness on creation. |
| `owner` | `UUID` | Owning player identity; combined with `world` to select the metadata file and in-memory cache entry. |
| `ownerName` | `String` | Cached player name for display; advisory only and repairable by `normalize`. |
| `world` | `String` | World id string that scopes both the file location and the `SelectionService` cache. |
| `origin` | [`BlockPos`](BlockPos.md) | Absolute anchor for relative coordinates; null until seeded, and intentionally not moved by cuboid removal (E2). |
| `cuboids` | `List<Cuboid>` | Mutable backing list initialized to an empty `ArrayList`; readers see it read-only, and it is re-sorted on every mutation. |
| `tracked` | `boolean` | Staging flag, false by default; stage/unstage commands flip it and status surfaces it. |
| `createdAt` | `String` | Instant text captured by the field initializer when the object is created; not changed by later mutations. |
| `updatedAt` | `String` | Instant text rewritten by `touch()`; the field initializer supplies the initial value. |

---

## 5. Constructor

```java
public Selection()
```

The no-arg constructor is the Gson load path: because Gson bypasses field initializers, an object restored this way may have null `cuboids` and null timestamps, which is exactly why `normalize` repairs those fields after parsing. For a locally constructed instance the field initializers still run, so `cuboids` is an empty `ArrayList` and both timestamps hold the creation instant, while the identity fields and origin stay null until set.

---

## 6. Constructor

```java
public Selection(String name, UUID owner, String ownerName, String world, BlockPos origin)
```

Used by `SelectionService.createAt`, this constructor records identity and an optional origin in one step and leaves all policy to the caller: name validation and uniqueness happen in the service, cuboids start empty, and `origin` may be null so the first `addCuboid` (or `setCuboids`) can seed it. It performs no sorting and no `touch()`, because the timestamps were already initialized to the construction instant and no mutation has occurred yet.

---

## 7. `normalize()`

```java
public Selection normalize(String fallbackWorld, UUID fallbackOwner, String fallbackOwnerName)
```

This is the post-deserialization repair routine called by `SelectionMetadataStore.loadFile`: it fills a null/blank `world` and `ownerName` and a null `owner` from the supplied fallbacks, replaces a null cuboid list, removes null elements, and re-sorts by smallest corner (E1). It seeds `origin` from the first sorted cuboid only when the stored origin is null, and it backfills null/blank `createdAt`/`updatedAt` (`updatedAt` falls back to `createdAt`) without overwriting valid stored times. The method mutates `this` and returns it, making it safe to use as an inline load step; it does not call `touch()`, so a load does not look like a user edit.

---

## 8. `name()`

```java
public String name()
```

Returns the raw `name` field, which `SelectionService` uses as the per-owner semantic key and which `byName()`/`toString()` assume to be non-null; it may be null on a deserialized object that has not yet passed through `normalize`.

---

## 9. `setName()`

```java
public void setName(String name)
```

Assigns the name directly with no trimming, validation, uniqueness check or timestamp update; rename collisions are outside this model type, so callers that rename must enforce policy themselves.

---

## 10. `owner()`

```java
public UUID owner()
```

Returns the owning player's UUID, which the metadata store combines with the world id to choose the JSON file and the service cache key; null indicates an instance that has not been assigned or normalized.

---

## 11. `setOwner()`

```java
public void setOwner(UUID owner)
```

Stores the owner UUID verbatim without validating it or calling `touch()`; reassigning ownership does not move the selection to another metadata file, so that remains a service-level responsibility.

---

## 12. `ownerName()`

```java
public String ownerName()
```

Returns the cached player name, which is only a display/diagnostic convenience and may be null or blank on old or hand-edited metadata.

---

## 13. `setOwnerName()`

```java
public void setOwnerName(String ownerName)
```

Replaces the cached player name without any validation or timestamp refresh, since the name is informational and does not participate in identity.

---

## 14. `world()`

```java
public String world()
```

Returns the world id that scopes the selection; it is used for per-world file paths, status/commit selection resolution, and cache partitioning by `SelectionService`.

---

## 15. `setWorld()`

```java
public void setWorld(String world)
```

Assigns the world id directly with no migration or timestamp update; changing it on a live object only changes the field, because file placement and caching are keyed by the world id passed to `SelectionService`.

---

## 16. `origin()`

```java
public BlockPos origin()
```

Returns the absolute anchor for relative coordinates; it is null exactly when the selection has never had a cuboid and no explicit origin was set, which callers detect with `hasOrigin()` before calling `relative` or `absolute`.

---

## 17. `setOrigin()`

```java
public void setOrigin(BlockPos origin)
```

This is the explicit re-origin primitive: it overwrites `origin` with any position (including null) and calls `touch()`. Because it does not verify that the position lies within a cuboid, callers can rebase the coordinate system deliberately; the automatic alternatives are `reorigin()` and first-cuboid seeding.

---

## 18. `hasOrigin()`

```java
public boolean hasOrigin()
```

A null check on `origin`, used as the guard that decides whether relative/absolute conversions are currently legal.

---

## 19. `cuboids()`

```java
public List<Cuboid> cuboids()
```

Returns `Collections.unmodifiableList(cuboids)`, a live read-only view: callers observe later additions and removals, but any mutation attempt throws `UnsupportedOperationException`, so the E1 ordering invariant cannot be broken from outside.

---

## 20. `setCuboids()`

```java
public void setCuboids(List<Cuboid> cuboids)
```

Replaces the backing list with a defensive `ArrayList` copy (null becomes empty), sorts it with `Cuboid.SMALLEST_CORNER_ORDER` (E1), seeds `origin` from the first element only if no origin exists yet, and calls `touch()`. Unlike `normalize`, it does not strip null elements, so callers passing deserialized or externally built lists must clean them first.

---

## 21. `addCuboid()`

```java
public void addCuboid(Cuboid cuboid)
```

Append a cuboid, re-sort by smallest corner, and seed the origin. A null argument is rejected with `IllegalArgumentException`; when the origin is null it is taken from the cuboid being added, not from whatever ends up first after sorting, so re-sorting never moves an existing origin (E1/E2), and the method finishes with `touch()`.

---

## 22. `removeCuboidAt()`

```java
public Cuboid removeCuboidAt(int index)
```

Remove a cuboid by zero-based position in the sorted list. It re-sorts the remainder, calls `touch()`, and returns the removed cuboid; per E2 the origin is deliberately left where it is even when the removed cuboid supplied it, so remaining block references keep the same relative coordinates. An out-of-range index propagates `IndexOutOfBoundsException`.

---

## 23. `reorigin()`

```java
public BlockPos reorigin()
```

Rebase the origin onto the smallest remaining cuboid corner (E3). It throws `IllegalStateException` for an empty selection, calls `touch()`, and returns the previous origin (possibly null) so the caller can report or reason about the shift.

---

## 24. `isTracked()`

```java
public boolean isTracked()
```

Returns the staging flag; it is false on a fresh selection and is only changed by `setTracked`, which the stage/unstage flows drive.

---

## 25. `setTracked()`

```java
public void setTracked(boolean tracked)
```

Stores the requested tracked state and calls `touch()`; `SelectionService` persists the whole selection list immediately afterward, so the flag survives a restart.

---

## 26. `createdAt()`

```java
public String createdAt()
```

Returns the creation timestamp text, which is set by field initialization at construction and preserved by `normalize` when present, so it reflects when the selection was first created rather than when it was last loaded.

---

## 27. `updatedAt()`

```java
public String updatedAt()
```

Returns the last-mutation timestamp text, maintained by `touch()` and backfilled from `createdAt` by `normalize` when missing.

---

## 28. `touch()`

```java
public void touch()
```

Writes `Instant.now().toString()` into `updatedAt`; it is the single timestamp-refresh point called by `setOrigin`, `setCuboids`, `addCuboid`, `removeCuboidAt`, `reorigin` and `setTracked`, and it deliberately leaves `createdAt` untouched and performs no I/O.

---

## 29. `cuboidCount()`

```java
public int cuboidCount()
```

Returns `cuboids.size()`, the count used by limits, command output and status summaries.

---

## 30. `volume()`

```java
public long volume()
```

Sum of cuboid volumes, saturated. Overlaps are counted twice. Each cuboid's already-saturated volume is folded in with an overflow guard that returns `Long.MAX_VALUE` if the next addition would wrap, so an oversized selection reports a capped total; an empty selection reports 0.

---

## 31. `boundsMin()`

```java
public BlockPos boundsMin()
```

For a non-empty selection it scans the cuboids' minimum x/y/z and returns the combined minimum corner; for an empty selection it returns `origin` directly, which may be null. It is independent of the origin and does not touch timestamps.

---

## 32. `boundsMax()`

```java
public BlockPos boundsMax()
```

For a non-empty selection it scans the cuboids' maximum x/y/z and returns the combined maximum corner; for an empty selection it returns `origin` directly, which may be null. Since it works only on cuboid bounds, it never needs a seeded origin.

---

## 33. `contains()`

```java
public boolean contains(BlockPos pos)
```

Linear scan over the cuboid list using the inclusive `Cuboid.contains` test, short-circuiting on the first hit; it returns false for an empty selection and ignores the origin, and a null argument surfaces a `NullPointerException`.

---

## 34. `relative()`

```java
public BlockPos relative(BlockPos absolute)
```

Computes `absolute.subtract(origin)` to produce the relative coordinate used in snapshots; it throws `IllegalStateException` when no origin exists and relies on `BlockPos` integer arithmetic, so extreme coordinates can overflow silently.

---

## 35. `absolute()`

```java
public BlockPos absolute(BlockPos relative)
```

Computes `origin.add(relative)` to turn a repository coordinate back into a world position, throwing `IllegalStateException` when the origin is missing; it is the exact inverse of `relative` while no overflow occurs.

---

## 36. `byName()`

```java
public static Comparator<Selection> byName()
```

Builds a comparator over `name.toLowerCase(Locale.ROOT)`, giving locale-independent, case-insensitive alphabetical ordering; both `SelectionService` and `SelectionMetadataStore.loadAll` use it for stable list output, and a null name causes `NullPointerException` when compared.

---

## 37. `toString()`

```java
@Override
public String toString()
```

Produces the diagnostic string `name (N cuboids, origin x, y, z)` using the `BlockPos` text form; it is for logs and command output only, is not the persistence format, and prints `null` for a missing origin or name.

---
