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

Subclassed by the mod's domain exceptions such as `SelectionException`, `CommitException`, `ApplyException`, `PullException` and `RepoException`, and thrown directly for simple preconditions such as `Open a world first.`

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

Unchecked so service code can throw it without declaring it; `CommandSupport` uses the type as the marker for show-this-message-without-a-stack-trace.

---

## 4. Constructor

```java
public UserFacingException(String message)
```

Passes the message to `RuntimeException` with no cause; used when there is nothing to chain.

---

## 5. Constructor

```java
public UserFacingException(String message, Throwable cause)
```

Passes both message and cause through, preserving the stack chain for debug logs while chat still shows only the message.

---
