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

TODO: Describe how `RepoMetadata` is instantiated and used.

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
| `Instant` | TODO: describe purpose |

---

## 3. Class Declaration

```java
public final class RepoMetadata {
```


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
| `world` | `String` | TODO: describe |
| `branch` | `String` | TODO: describe |
| `remoteName` | `String` | TODO: describe |
| `remoteUrl` | `String` | TODO: describe |
| `authorName` | `String` | TODO: describe |
| `authorEmail` | `String` | TODO: describe |
| `lastSelection` | `String` | TODO: describe |
| `createdAt` | `String` | TODO: describe |

---

## 5. `defaults()`

```java
public static RepoMetadata defaults(String world, RepoOptions options)
```

TODO: Provide detailed design explanation for `defaults`.

---

## 6. `normalize()`

```java
public RepoMetadata normalize(String world, RepoOptions options)
```

Fill in blanks introduced by older or hand-edited metadata files.

---

## 7. `world()`

```java
public String world()
```

TODO: Provide detailed design explanation for `world`.

---

## 8. `setWorld()`

```java
public void setWorld(String world)
```

TODO: Provide detailed design explanation for `setWorld`.

---

## 9. `branch()`

```java
public String branch()
```

TODO: Provide detailed design explanation for `branch`.

---

## 10. `setBranch()`

```java
public void setBranch(String branch)
```

TODO: Provide detailed design explanation for `setBranch`.

---

## 11. `remoteName()`

```java
public String remoteName()
```

TODO: Provide detailed design explanation for `remoteName`.

---

## 12. `setRemoteName()`

```java
public void setRemoteName(String remoteName)
```

TODO: Provide detailed design explanation for `setRemoteName`.

---

## 13. `remoteUrl()`

```java
public String remoteUrl()
```

TODO: Provide detailed design explanation for `remoteUrl`.

---

## 14. `setRemoteUrl()`

```java
public void setRemoteUrl(String remoteUrl)
```

TODO: Provide detailed design explanation for `setRemoteUrl`.

---

## 15. `authorName()`

```java
public String authorName()
```

TODO: Provide detailed design explanation for `authorName`.

---

## 16. `setAuthorName()`

```java
public void setAuthorName(String authorName)
```

TODO: Provide detailed design explanation for `setAuthorName`.

---

## 17. `authorEmail()`

```java
public String authorEmail()
```

TODO: Provide detailed design explanation for `authorEmail`.

---

## 18. `setAuthorEmail()`

```java
public void setAuthorEmail(String authorEmail)
```

TODO: Provide detailed design explanation for `setAuthorEmail`.

---

## 19. `lastSelection()`

```java
public String lastSelection()
```

TODO: Provide detailed design explanation for `lastSelection`.

---

## 20. `setLastSelection()`

```java
public void setLastSelection(String lastSelection)
```

TODO: Provide detailed design explanation for `setLastSelection`.

---

## 21. `createdAt()`

```java
public String createdAt()
```

TODO: Provide detailed design explanation for `createdAt`.

---
