# RepoException — Software Detailed Design

> **API Documentation:** [RepoException.md](./RepoException.md)  
> **Source File:** [RepoException.java](../../../../../../../src/main/java/org/almond/minecraftgit/repo/RepoException.java)

## Table of Contents

- [Usage](#usage)
- [1. Overview](#1-overview)
- [2. Package Declaration & Imports](#2-package-declaration-imports)
- [3. Class Declaration](#3-class-declaration)
- [4. Constructor](#4-constructor)
- [5. Constructor](#5-constructor)

## Usage

TODO: Describe how `RepoException` is instantiated and used.

---

## 1. Overview

A repository operation failed in a way the user can act on. Commands show
{@link #getMessage()} directly; the JGit cause is kept for debug logs.

---

## 2. Package Declaration & Imports

```java
package org.almond.minecraftgit.repo;
```

```java
import org.almond.minecraftgit.util.UserFacingException;
```

| Import | Purpose |
|--------|---------|
| [`UserFacingException`](../util/UserFacingException.md) | TODO: describe purpose |

---

## 3. Class Declaration

```java
public class RepoException extends UserFacingException {
```

- **`extends UserFacingException`** — TODO: explain inheritance.

---

## 4. Constructor

```java
public RepoException(String message)
```

TODO: Provide detailed design explanation for `RepoException`.

---

## 5. Constructor

```java
public RepoException(String message, Throwable cause)
```

TODO: Provide detailed design explanation for `RepoException`.

---
