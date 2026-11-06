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

TODO: Describe how `StagingException` is instantiated and used.

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
| [`UserFacingException`](../util/UserFacingException.md) | TODO: describe purpose |

---

## 3. Class Declaration

```java
public final class StagingException extends UserFacingException {
```

- **`extends UserFacingException`** — TODO: explain inheritance.

---

## 4. Constructor

```java
public StagingException(String message)
```

TODO: Provide detailed design explanation for `StagingException`.

---

## 5. Constructor

```java
public StagingException(String message, Throwable cause)
```

TODO: Provide detailed design explanation for `StagingException`.

---
