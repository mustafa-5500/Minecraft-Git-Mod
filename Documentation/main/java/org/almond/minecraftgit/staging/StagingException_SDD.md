# StagingException — Software Detailed Design

> **API Documentation:** [StagingException.md](./StagingException.md)  
> **Source File:** [StagingException.java](../../../../../../../src/main/java/org/almond/minecraftgit/staging/StagingException.java)

## Table of Contents

- [Usage](#usage)
- [1. Overview](#1-overview)
- [2. Package Declaration & Imports](#2-package-declaration-imports)
- [3. Class Declaration](#3-class-declaration)
- [4. Constructor](#4-constructor)
- [5. Constructor](#5-constructor)

## Usage

Staging services and `SnapshotStore` throw it for missing or invalid selections, absent origins, and unreadable or corrupt staged files; command handlers catch `UserFacingException` (or this specific type) and display `getMessage()` in chat. Because the class is final, callers can rely on catching the exact type when they need staging-specific handling.

---

## 1. Overview

A staging operation failed in a way the user can fix.

---

## 2. Package Declaration & Imports

```java
package org.almond.minecraftgit.staging;
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
public final class StagingException extends UserFacingException {
```

- **`extends UserFacingException`** — Inherits the unchecked, user-displayable contract, so a staging failure can be caught by the shared command-layer handler while remaining distinguishable by type.

---

## 4. Constructor

```java
public StagingException(String message)
```

Delegates to `UserFacingException(String)`: it stores the message and performs no I/O or validation, so constructing the exception is cheap even on a hot error path.

---

## 5. Constructor

```java
public StagingException(String message, Throwable cause)
```

Delegates to `UserFacingException(String, Throwable)`, preserving the cause chain for logs while exposing only the curated message to the player. `StagingService` and `SnapshotStore` use this form when wrapping `IOException`s.

---
