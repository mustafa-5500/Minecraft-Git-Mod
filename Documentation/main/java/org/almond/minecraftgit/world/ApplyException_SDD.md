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

TODO: Describe how `ApplyException` is instantiated and used.

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
| [`UserFacingException`](../util/UserFacingException.md) | TODO: describe purpose |

---

## 3. Class Declaration

```java
public final class ApplyException extends UserFacingException {
```

- **`extends UserFacingException`** — TODO: explain inheritance.

---

## 4. Constructor

```java
public ApplyException(String message)
```

TODO: Provide detailed design explanation for `ApplyException`.

---

## 5. Constructor

```java
public ApplyException(String message, Throwable cause)
```

TODO: Provide detailed design explanation for `ApplyException`.

---
