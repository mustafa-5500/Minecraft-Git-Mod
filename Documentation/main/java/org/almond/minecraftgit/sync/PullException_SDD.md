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

TODO: Describe how `PullException` is instantiated and used.

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
| [`UserFacingException`](../util/UserFacingException.md) | TODO: describe purpose |

---

## 3. Class Declaration

```java
public final class PullException extends UserFacingException {
```

- **`extends UserFacingException`** — TODO: explain inheritance.

---

## 4. Constructor

```java
public PullException(String message)
```

TODO: Provide detailed design explanation for `PullException`.

---

## 5. Constructor

```java
public PullException(String message, Throwable cause)
```

TODO: Provide detailed design explanation for `PullException`.

---
