# PullException — Software Detailed Design

> **API Documentation:** [PullException.md](./PullException.md)  
> **Source File:** [PullException.java](../../../../../../../src/main/java/org/almond/minecraftgit/sync/PullException.java)

## Table of Contents

- [Usage](#usage)
- [1. Overview](#1-overview)
- [2. Package Declaration & Imports](#2-package-declaration-imports)
- [3. Class Declaration](#3-class-declaration)
- [4. Constructor](#4-constructor)
- [5. Constructor](#5-constructor)

## Usage

Pull and checkout preparation throw it for a selection missing from the remote commit, a refused partial pull, or an export/read I/O failure; command handlers catch `UserFacingException` (or this specific type) and display `getMessage()` in chat. Because the class is final, callers can rely on catching the exact type when they need pull-specific handling.

---

## 1. Overview

A pull/checkout preparation failed in a user-correctable way.

---

## 2. Package Declaration & Imports

```java
package org.almond.minecraftgit.sync;
```

```java
import org.almond.minecraftgit.util.UserFacingException;
```

| Import | Purpose |
|--------|---------|
| [`UserFacingException`](../util/UserFacingException.md) | Base class that marks the failure as safe to present to the player. |

---

## 3. Class Declaration

```java
public final class PullException extends UserFacingException {
```

- **`extends UserFacingException`** — Inherits the unchecked, user-displayable contract, so a pull failure can be caught by the shared command-layer handler while remaining distinguishable by type.

---

## 4. Constructor

```java
public PullException(String message)
```

Delegates to `UserFacingException(String)`: it stores the message and performs no I/O or validation, so constructing the exception is cheap even on a hot error path.

---

## 5. Constructor

```java
public PullException(String message, Throwable cause)
```

Delegates to `UserFacingException(String, Throwable)`, preserving the cause chain for logs while exposing only the curated message to the player. `PullService` uses this form when wrapping `IOException`s from temp-directory or export operations.

---
