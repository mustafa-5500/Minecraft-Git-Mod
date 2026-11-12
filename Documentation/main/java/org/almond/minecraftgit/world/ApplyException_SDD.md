# ApplyException — Software Detailed Design

> **API Documentation:** [ApplyException.md](./ApplyException.md)  
> **Source File:** [ApplyException.java](../../../../../../../src/main/java/org/almond/minecraftgit/world/ApplyException.java)

## Table of Contents

- [Usage](#usage)
- [1. Overview](#1-overview)
- [2. Package Declaration & Imports](#2-package-declaration-imports)
- [3. Class Declaration](#3-class-declaration)
- [4. Constructor](#4-constructor)
- [5. Constructor](#5-constructor)

## Usage

`ApplyException` is created only inside `client.world.ApplyService`: once eagerly in `apply` for a missing single-player server or level, and four times as `completeExceptionally` calls in `begin` and `Job.process` for a stopping server, a failed backup capture, a failed backup write, or a server that stopped mid-apply. It is never caught inside the apply job; command handlers unwrap the future with `CommandSupport.unwrap` and display the message through `Messages`.

```java
future.completeExceptionally(new ApplyException(
        "Could not write a pre-apply backup: " + cause.getMessage(), cause));
```

The type carries no flags or retry policy: its message is the entire user-facing contract, and the cause is preserved only for diagnostics.

---

## 1. Overview

A world apply could not proceed safely.

---

## 2. Package Declaration & Imports

```java
package org.almond.minecraftgit.world;
```

```java
import org.almond.minecraftgit.util.UserFacingException;
```

| Import | Purpose |
|--------|---------|
| [`UserFacingException`](../util/UserFacingException.md) | Supplies the unchecked, chat-safe superclass so command handlers can treat apply failures like other expected errors. |

---

## 3. Class Declaration

```java
public final class ApplyException extends UserFacingException {
```

- **`extends UserFacingException`** — Inherits `RuntimeException` through `UserFacingException`, so apply failures are unchecked and do not force `throws` clauses across the futures-based service; the superclass marks the message as safe to display to the player rather than as an internal bug. Being `final` prevents handlers from depending on subtypes.

---

## 4. Constructor

```java
public ApplyException(String message)
```

Stores the message and leaves the cause unset. This is used for conditions the player can act on (not single-player, server stopping) where there is no useful underlying exception to retain.

---

## 5. Constructor

```java
public ApplyException(String message, Throwable cause)
```

Stores the message and cause; used by `begin` when a backup capture or backup write fails. The message remains the short chat-facing summary while the cause preserves the original stack for logs. The single-message constructor covers the server-stopped-mid-apply case.

---
