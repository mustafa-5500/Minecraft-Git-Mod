# RepoMetadata — Software Detailed Design

> **API Documentation:** [RepoMetadata.md](./RepoMetadata.md)  
> **Source File:** [RepoMetadata.java](../../../../../../../src/main/java/org/almond/minecraftgit/repo/RepoMetadata.java)

## Table of Contents

- [Usage](#usage)
- [1. Overview](#1-overview)
- [2. Package Declaration & Imports](#2-package-declaration-imports)
- [3. Class Declaration](#3-class-declaration)
- [4. Instance Fields](#4-instance-fields)
- [5. `defaults()`](#5-defaults)
- [6. `normalize()`](#6-normalize)
- [7. `world()`](#7-world)
- [8. `setWorld()`](#8-setworld)
- [9. `branch()`](#9-branch)
- [10. `setBranch()`](#10-setbranch)
- [11. `remoteName()`](#11-remotename)
- [12. `setRemoteName()`](#12-setremotename)
- [13. `remoteUrl()`](#13-remoteurl)
- [14. `setRemoteUrl()`](#14-setremoteurl)
- [15. `authorName()`](#15-authorname)
- [16. `setAuthorName()`](#16-setauthorname)
- [17. `authorEmail()`](#17-authoremail)
- [18. `setAuthorEmail()`](#18-setauthoremail)
- [19. `lastSelection()`](#19-lastselection)
- [20. `setLastSelection()`](#20-setlastselection)
- [21. `createdAt()`](#21-createdat)

## Usage

Instantiated only through `RepoMetadata.defaults` (new world) or Gson inside `RepoMetadataStore.load` (existing world), then cached by `RepoManager`. Callers mutate the cached instance's setters and persist it with `RepoManager.saveMetadata`; the class itself performs no IO and has no knowledge of paths.

```java
RepoMetadata metadata = RepoMetadata.defaults(worldId,
        new RepoOptions("main", "origin", "Alex", "alex@example.com"));
metadata.setLastSelection("castle");
```

---

## 1. Overview

Per-world, local-only repository state. This file is never committed: it maps
mod concepts (world id, default remote, author) onto Git concepts and may
differ between machines that share the same repository.

---

## 2. Package Declaration & Imports

```java
package org.almond.minecraftgit.repo;
```

```java
import java.time.Instant;
```

| Import | Purpose |
|--------|---------|
| `Instant` | Stamps `createdAt` with a UTC ISO-8601 timestamp in `defaults` and in `normalize` when legacy metadata lacks one. |

---

## 3. Class Declaration

```java
public final class RepoMetadata {
```

A deliberately plain mutable POJO: private fields with public getters/setters, no interfaces, no validation in the accessors. It is `final` because Gson only needs to instantiate the concrete type, and the JSON schema is exactly these field names. Repair of partial or hand-edited JSON is centralized in `normalize` instead of being spread across setters.

---

## 4. Instance Fields

```java
private String world;
private String branch;
private String remoteName;
private String remoteUrl;
private String authorName;
private String authorEmail;
private String lastSelection;
private String createdAt;
```

| Field | Type | Description |
|-------|------|-------------|
| `world` | `String` | World key; has no field initializer, so a freshly constructed instance is `null` until `defaults` or `normalize` assigns the load argument. |
| `branch` | `String` | Initialized to `main`; the branch JGit creates and the local branch compared during pull. |
| `remoteName` | `String` | Initialized to `origin`; the default remote for commands that do not name one. |
| `remoteUrl` | `String` | Initialized to `""`; filled from `RepoInfo.remoteUrl` after init when a remote exists. |
| `authorName` | `String` | Initialized to `MinecraftGit`; sentinel value that lets `RepoManager.isDefaultIdentity` detect an unpersonalized world. |
| `authorEmail` | `String` | Initialized to `minecraftgit@localhost`; replaced together with the name by first init. |
| `lastSelection` | `String` | Initialized to `""`; the wand's remembered active selection name. |
| `createdAt` | `String` | Initialized with `Instant.now().toString()`; records first creation for diagnostics. |

---

## 5. `defaults()`

```java
public static RepoMetadata defaults(String world, RepoOptions options)
```

A static factory rather than a constructor, so the no-arg constructor Gson uses stays untouched. It builds a new object and copies the four values that `RepoOptions` owns (`branch`, `defaultRemote` → `remoteName`, `authorName`, `authorEmail`), leaving `remoteUrl`/`lastSelection` at their empty initializers and re-stamping `createdAt`. Because `RepoOptions` has already canonicalized blanks, the result is always usable without calling `normalize`.

---

## 6. `normalize()`

```java
public RepoMetadata normalize(String world, RepoOptions options)
```

Fill in blanks introduced by older or hand-edited metadata files.

It calls `defaults(world, options)` once to obtain fallback values, then walks each field:

- `world`, `branch`, `remoteName`, `authorName`, `authorEmail`, `createdAt`: replaced when `null` or blank.
- `remoteUrl`, `lastSelection`: only `null` is converted to `""`, because an empty remote URL is a meaningful state (no remote yet).
- Non-blank custom values are never overwritten, so a player's author or branch survives an upgrade.

It mutates the receiver in place and returns `this`, which is how `RepoMetadataStore.load` can `return metadata.normalize(world, defaults)`.

---

## 7. `world()`

```java
public String world()
```

Trivial getter for the world key. `RepoMetadataStore.file` relies on it indirectly through `save`, and `RepoManager.service` uses it only as the cache key of the surrounding map.

---

## 8. `setWorld()`

```java
public void setWorld(String world)
```

Plain setter with no validation. It is not called by the manager (the load path passes the world to `normalize`), so changing it in code would make `save` write a different world's file.

---

## 9. `branch()`

```java
public String branch()
```

Getter consumed when building `RepoOptions` for a new `RepoService` and when `init` records the branch; `normalize` guarantees it is non-blank for loaded metadata.

---

## 10. `setBranch()`

```java
public void setBranch(String branch)
```

Plain setter; does not rename a Git branch and does not reconfigure already-created services, because `RepoOptions` was captured at service creation.

---

## 11. `remoteName()`

```java
public String remoteName()
```

Getter for the default remote name, read by commands (`push`, `fetch`, `pull`) and by `RepoOptions` construction.

---

## 12. `setRemoteName()`

```java
public void setRemoteName(String remoteName)
```

Plain setter for the default remote name. It neither adds nor renames remotes in the repository; that is `RepoService.remoteAdd`/`remoteRemove`.

---

## 13. `remoteUrl()`

```java
public String remoteUrl()
```

Getter for the last recorded remote URL. It is informational only: transport code resolves the real URL from the JGit remote configuration.

---

## 14. `setRemoteUrl()`

```java
public void setRemoteUrl(String remoteUrl)
```

Plain setter used by `RepoManager.init` when `RepoInfo` reports a non-blank URL. A `null` value is tolerated until the next `normalize`, which converts it to `""`.

---

## 15. `authorName()`

```java
public String authorName()
```

Getter that feeds `RepoOptions.authorName`, `RepoService.setAuthor` and the default-identity check in `RepoManager.init`.

---

## 16. `setAuthorName()`

```java
public void setAuthorName(String authorName)
```

Plain setter; persistence plus propagation to the live service is orchestrated by `RepoManager.setAuthor`. Blank values would be rejected only later by `RepoService.setAuthor`.

---

## 17. `authorEmail()`

```java
public String authorEmail()
```

Getter for the author email used as the committer identity in JGit config and in the bootstrap commit.

---

## 18. `setAuthorEmail()`

```java
public void setAuthorEmail(String authorEmail)
```

Plain setter paired with `setAuthorName`; there is no validation here, by design, so the metadata schema stays a faithful view of the JSON file.

---

## 19. `lastSelection()`

```java
public String lastSelection()
```

Getter used by `RepoManager.lastSelection` and `SelectionManager.active`; an empty string means "nothing remembered", never `null` for freshly defaulted metadata.

---

## 20. `setLastSelection()`

```java
public void setLastSelection(String lastSelection)
```

Plain setter for the remembered selection name; `RepoManager.setLastSelection` normalizes `null` to `""` before calling it, and saving happens separately.

---

## 21. `createdAt()`

```java
public String createdAt()
```

Getter with no matching setter, so the timestamp is read-only once Gson or `defaults` has populated it. It is generated with `Instant.toString()` (UTC, ISO-8601) and is informational; no code currently reads it back for ordering or comparisons.

---
