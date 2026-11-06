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

TODO: Describe how `RepoManager` is instantiated and used.

---

## 1. Overview

Owns the per-world-id {@link RepoService} instances and their local metadata.
A "world" here is the client's {@code <world>__<dimension>} key, not a
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
| [`ModConfig`](../config/ModConfig.md) | TODO: describe purpose |
| [`DebugLogger`](../debug/DebugLogger.md) | TODO: describe purpose |
| `Files` | TODO: describe purpose |
| `Path` | TODO: describe purpose |
| `Map` | TODO: describe purpose |
| `ConcurrentHashMap` | TODO: describe purpose |

---

## 3. Class Declaration

```java
public final class RepoManager implements AutoCloseable {
```

- **`implements AutoCloseable`** — TODO: explain interfaces.

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
| `config` | [`ModConfig`](../config/ModConfig.md) | TODO: describe |
| `debug` | [`DebugLogger`](../debug/DebugLogger.md) | TODO: describe |
| `locator` | [`RepoLocator`](RepoLocator.md) | TODO: describe |
| `metadataStore` | [`RepoMetadataStore`](RepoMetadataStore.md) | TODO: describe |
| `services` | `Map<String, RepoService>` | TODO: describe |
| `metadataCache` | `Map<String, RepoMetadata>` | TODO: describe |

---

## 5. Constructor

```java
public RepoManager(ModConfig config, DebugLogger debug)
```

TODO: Provide detailed design explanation for `RepoManager`.

---

## 6. `locator()`

```java
public RepoLocator locator()
```

TODO: Provide detailed design explanation for `locator`.

---

## 7. `repoPath()`

```java
public Path repoPath(String worldId)
```

TODO: Provide detailed design explanation for `repoPath`.

---

## 8. `isInitialized()`

```java
public boolean isInitialized(String worldId)
```

TODO: Provide detailed design explanation for `isInitialized`.

---

## 9. `metadata()`

```java
public RepoMetadata metadata(String worldId)
```

TODO: Provide detailed design explanation for `metadata`.

---

## 10. `saveMetadata()`

```java
public void saveMetadata(RepoMetadata metadata)
```

TODO: Provide detailed design explanation for `saveMetadata`.

---

## 11. `service()`

```java
public RepoService service(String worldId)
```

TODO: Provide detailed design explanation for `service`.

---

## 12. `init()`

```java
public RepoInfo init(String worldId, String playerName, String playerEmail)
```

Create or open the repository. When the repository still uses the default
identity, adopt the local player's name so commits are attributable without
an extra setup step.

---

## 13. `setAuthor()`

```java
public void setAuthor(String worldId, String name, String email)
```

TODO: Provide detailed design explanation for `setAuthor`.

---

## 14. `setLastSelection()`

```java
public void setLastSelection(String worldId, String selection)
```

TODO: Provide detailed design explanation for `setLastSelection`.

---

## 15. `lastSelection()`

```java
public String lastSelection(String worldId)
```

TODO: Provide detailed design explanation for `lastSelection`.

---

## 16. `reload()`

```java
public void reload()
```

TODO: Provide detailed design explanation for `reload`.

---

## 17. `defaults()`

```java
private RepoOptions defaults()
```

TODO: Provide detailed design explanation for `defaults`.

---

## 18. `isDefaultIdentity()`

```java
private static boolean isDefaultIdentity(String authorName)
```

TODO: Provide detailed design explanation for `isDefaultIdentity`.

---

## 19. `close()`

```java
@Override
public void close()
```

TODO: Provide detailed design explanation for `close`.

---
