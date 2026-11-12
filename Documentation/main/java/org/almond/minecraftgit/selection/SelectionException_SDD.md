# SelectionException — Software Detailed Design

> **API Documentation:** [SelectionException.md](./SelectionException.md)  
> **Source File:** [SelectionException.java](../../../../../../../src/main/java/org/almond/minecraftgit/selection/SelectionException.java)

## Table of Contents

- [Usage](#usage)
- [1. Overview](#1-overview)
- [2. Package Declaration & Imports](#2-package-declaration-imports)
- [3. Class Declaration](#3-class-declaration)
- [4. Constructor](#4-constructor)

## Usage

Constructed at the point of failure in `SelectionService`, `SnapshotService` and wand handling, then caught by command handling, which prints `getMessage()` instead of logging a stack trace.

---

## 1. Overview

A selection or wand operation the user can correct.

---

## 2. Package Declaration & Imports

```java
package org.almond.minecraftgit.selection;
```

```java
import org.almond.minecraftgit.util.UserFacingException;
```

| Import | Purpose |
|--------|---------|
| [`UserFacingException`](../util/UserFacingException.md) | Provides the user-facing marker base class plus the message-only constructor this exception uses. |

---

## 3. Class Declaration

```java
public final class SelectionException extends UserFacingException {
```

Marks the failure as safe to display; `CommandSupport.handleError` checks for `UserFacingException` and sends its message without logging a stack trace.

---

## 4. Constructor

```java
public SelectionException(String message)
```

Only forwards to `super(message)`, so every selection error is a user-facing runtime exception with a precise, actionable message; the `final` modifier prevents accidental subclass growth.

---
