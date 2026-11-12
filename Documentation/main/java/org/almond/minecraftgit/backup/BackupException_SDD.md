# BackupException — Software Detailed Design

> **API Documentation:** [BackupException.md](./BackupException.md)  
> **Source File:** [BackupException.java](../../../../../../../src/main/java/org/almond/minecraftgit/backup/BackupException.java)

## Table of Contents

- [Usage](#usage)
- [1. Overview](#1-overview)
- [2. Package Declaration & Imports](#2-package-declaration-imports)
- [3. Class Declaration](#3-class-declaration)
- [4. Constructor](#4-constructor)
- [5. Constructor](#5-constructor)

## Usage

It is thrown by `BackupService.store`, `list` and `requireName`, and bubbles through `ApplyService` and `PullCommands` as a `UserFacingException` so the player sees a chat error. It is unchecked and marks recoverable local-storage or name-validation failures rather than programming errors.

---

## 1. Overview

A backup could not be created or read.

---

## 2. Package Declaration & Imports

```java
package org.almond.minecraftgit.backup;
```

```java
import org.almond.minecraftgit.util.UserFacingException;
```

| Import | Purpose |
|--------|---------|
| [`UserFacingException`](../util/UserFacingException.md) | Base runtime exception whose messages are safe to display in chat. |

---

## 3. Class Declaration

```java
public final class BackupException extends UserFacingException {
```

- **`extends UserFacingException`** — Extends the unchecked `UserFacingException`, so callers are not forced to declare it and the command layer can render it like any other user-facing failure.

---

## 4. Constructor

```java
public BackupException(String message)
```

Delegates to `UserFacingException(String)`; used for validation-style failures such as an invalid selection name.

---

## 5. Constructor

```java
public BackupException(String message, Throwable cause)
```

Delegates to `UserFacingException(String, Throwable)`; used when wrapping `IOException` from directory creation, snapshot writing or listing.

---
