# UserFacingException — Software Detailed Design

> **API Documentation:** [UserFacingException.md](./UserFacingException.md)  
> **Source File:** [UserFacingException.java](../../../../../../../src/main/java/org/almond/minecraftgit/util/UserFacingException.java)

## Table of Contents

- [Usage](#usage)
- [1. Overview](#1-overview)
- [2. Package Declaration & Imports](#2-package-declaration-imports)
- [3. Class Declaration](#3-class-declaration)
- [4. Constructor](#4-constructor)
- [5. Constructor](#5-constructor)

## Usage

TODO: Describe how `UserFacingException` is instantiated and used.

---

## 1. Overview

Base type for failures that are safe and useful to show in chat.

---

## 2. Package Declaration & Imports

```java
package org.almond.minecraftgit.util;
```

---

## 3. Class Declaration

```java
public class UserFacingException extends RuntimeException {
```

- **`extends RuntimeException`** — TODO: explain inheritance.

---

## 4. Constructor

```java
public UserFacingException(String message)
```

TODO: Provide detailed design explanation for `UserFacingException`.

---

## 5. Constructor

```java
public UserFacingException(String message, Throwable cause)
```

TODO: Provide detailed design explanation for `UserFacingException`.

---
