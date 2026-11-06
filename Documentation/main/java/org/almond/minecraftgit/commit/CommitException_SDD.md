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

TODO: Describe how `CommitException` is instantiated and used.

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
| [`UserFacingException`](../util/UserFacingException.md) | TODO: describe purpose |

---

## 3. Class Declaration

```java
public final class CommitException extends UserFacingException {
```

- **`extends UserFacingException`** — TODO: explain inheritance.

---

## 4. Constructor

```java
public CommitException(String message)
```

TODO: Provide detailed design explanation for `CommitException`.

---

## 5. Constructor

```java
public CommitException(String message, Throwable cause)
```

TODO: Provide detailed design explanation for `CommitException`.

---
