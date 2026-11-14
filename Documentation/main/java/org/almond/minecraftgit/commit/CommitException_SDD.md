# CommitException — Software Detailed Design

> **API Documentation:** [CommitException.md](./CommitException.md)  
> **Source File:** [CommitException.java](../../../../../../../src/main/java/org/almond/minecraftgit/commit/CommitException.java)

## Table of Contents

- [Usage](#usage)
- [1. Overview](#1-overview)
- [2. Package Declaration & Imports](#2-package-declaration-imports)
- [3. Class Declaration](#3-class-declaration)
- [4. Constructor](#4-constructor)
- [5. Constructor](#5-constructor)

## Usage

It is thrown by `CommitService.commit` for validation and IO wrapping, and surfaces through `CommitCommands` as a `UserFacingException` whose message is shown in chat. It is an unchecked control-flow signal, not a data carrier, and the commit service never catches it itself.

---

## 1. Overview

A commit could not be created for a user-correctable reason.

---

## 2. Package Declaration & Imports

```java
package org.almond.minecraftgit.commit;
```

```java
import org.almond.minecraftgit.util.UserFacingException;
```

| Import | Purpose |
|--------|---------|
| [`UserFacingException`](../util/UserFacingException.md) | Base runtime exception marking messages that are safe to display to the player. |

---

## 3. Class Declaration

```java
public final class CommitException extends UserFacingException {
```

- **`extends UserFacingException`** — `UserFacingException` extends `RuntimeException`, so the commit failure is unchecked; the command layer catches this base type and prints `getMessage()` without a stack trace.

---

## 4. Constructor

```java
public CommitException(String message)
```

Delegates to `UserFacingException(String)`, which stores the message; with no cause, it represents a validation failure such as missing staged data.

---

## 5. Constructor

```java
public CommitException(String message, Throwable cause)
```

Delegates to `UserFacingException(String, Throwable)` so the original cause is available for logs while chat shows only the message; used when wrapping `IOException` from temporary-tree construction.

---
