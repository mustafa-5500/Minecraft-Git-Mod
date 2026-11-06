# RepoLocator — Software Detailed Design

> **API Documentation:** [RepoLocator.md](./RepoLocator.md)  
> **Source File:** [RepoLocator.java](../../../../../../../src/main/java/org/almond/minecraftgit/repo/RepoLocator.java)

## Table of Contents

- [Usage](#usage)
- [1. Overview](#1-overview)
- [2. Package Declaration & Imports](#2-package-declaration-imports)
- [3. Class Declaration](#3-class-declaration)
- [4. Instance Fields](#4-instance-fields)
- [5. Constructor](#5-constructor)
- [6. `repoRoot()`](#6-reporoot)
- [7. `metadataRoot()`](#7-metadataroot)
- [8. `repoPath()`](#8-repopath)
- [9. `metadataPath()`](#9-metadatapath)

## Usage

TODO: Describe how `RepoLocator` is instantiated and used.

---

## 1. Overview

Decides where repositories and their local-only metadata live.
<p>Repositories live in the mod data folder
({@code config/minecraftgit/repos/<world>}), not inside the world folder.
World backups therefore never capture a hot {@code .git} directory, and a
repository is not accidentally copied with the map.</p>

---

## 2. Package Declaration & Imports

```java
package org.almond.minecraftgit.repo;
```

```java
import org.almond.minecraftgit.util.Names;
import java.nio.file.Path;
```

| Import | Purpose |
|--------|---------|
| [`Names`](../util/Names.md) | TODO: describe purpose |
| `Path` | TODO: describe purpose |

---

## 3. Class Declaration

```java
public final class RepoLocator {
```


---

## 4. Instance Fields

```java
private final Path repoRoot;
private final Path metadataRoot;
```

| Field | Type | Description |
|-------|------|-------------|
| `repoRoot` | `Path` | TODO: describe |
| `metadataRoot` | `Path` | TODO: describe |

---

## 5. Constructor

```java
public RepoLocator(Path repoRoot, Path metadataRoot)
```

TODO: Provide detailed design explanation for `RepoLocator`.

---

## 6. `repoRoot()`

```java
public Path repoRoot()
```

TODO: Provide detailed design explanation for `repoRoot`.

---

## 7. `metadataRoot()`

```java
public Path metadataRoot()
```

TODO: Provide detailed design explanation for `metadataRoot`.

---

## 8. `repoPath()`

```java
public Path repoPath(String worldName)
```

TODO: Provide detailed design explanation for `repoPath`.

---

## 9. `metadataPath()`

```java
public Path metadataPath(String worldName)
```

TODO: Provide detailed design explanation for `metadataPath`.

---
