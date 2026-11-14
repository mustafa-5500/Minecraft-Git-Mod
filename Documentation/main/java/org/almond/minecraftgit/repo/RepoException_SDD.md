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

Raised inside `RepoService` (via its `error(...)` helper and direct validation), `RepoMetadataStore` and `RepoManager` checks; commands do not catch it explicitly — `CommandSupport.handleError` unwraps completion exceptions and prints any `UserFacingException` message in chat.

```java
throw new RepoException("Could not read repository metadata at " + file + ": " + e.getMessage(), e);
```

---

## 1. Overview

A repository operation failed in a way the user can act on. Commands show
`.getMessage()` directly; the JGit cause is kept for debug logs.

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
| [`UserFacingException`](../util/UserFacingException.md) | Parent type that marks the message as safe to show in chat; `CommandSupport` checks for it when deciding between a clean error and a logged crash. |

---

## 3. Class Declaration

```java
public class RepoException extends UserFacingException {
```

- **`extends UserFacingException`** — Inherits the unchecked `RuntimeException` nature, so repository methods need no `throws` clauses and lambdas passed to `CommandSupport.complete` can propagate failures; the distinction from a generic exception is entirely the user-facing message contract.

---

## 4. Constructor

```java
public RepoException(String message)
```

Delegates to `UserFacingException(message)`. Used when the failure is a known precondition (no repository, blank author, detached HEAD, empty metadata), so no stack trace is needed and the message alone tells the player what to do. The exception is not logged by the repository layer; the command layer decides.

---

## 5. Constructor

```java
public RepoException(String message, Throwable cause)
```

Delegates to `UserFacingException(message, cause)`. Used when wrapping `IOException`/`GitAPIException`: the message names the file or action in plain language while `getCause()` keeps the original exception chain for `MinecraftGit.LOGGER`/`DebugLogger` diagnostics. Note that command output shows only `getMessage()`, so callers must not put the only useful detail in the cause.

---
