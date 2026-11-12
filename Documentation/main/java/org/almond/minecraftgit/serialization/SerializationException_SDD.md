# SerializationException — Software Detailed Design

> **API Documentation:** [SerializationException.md](./SerializationException.md)  
> **Source File:** [SerializationException.java](../../../../../../../src/main/java/org/almond/minecraftgit/serialization/SerializationException.java)

## Table of Contents

- [Usage](#usage)
- [1. Overview](#1-overview)
- [2. Package Declaration & Imports](#2-package-declaration-imports)
- [3. Class Declaration](#3-class-declaration)
- [4. Constructor](#4-constructor)
- [5. Constructor](#5-constructor)

## Usage

`SerializationException` is thrown by the readers and writers in this package and caught once near the command boundary, where `getMessage()` is reported to the player. Because it is unchecked, helpers such as the regex readers can throw it without widening their signatures, and lambdas used with the file streams do not need checked-exception plumbing; callers that cannot recover simply let it propagate.

---

## 1. Overview

A repository-format error that should be shown to the user.

---

## 2. Package Declaration & Imports

```java
package org.almond.minecraftgit.serialization;
```

---

## 3. Class Declaration

```java
public final class SerializationException extends RuntimeException {
```

- **`extends RuntimeException`** — Unchecked, so it can cross stream/lambda boundaries without checked declarations; `final`, so it cannot be subclassed and callers can treat it as the complete error type of the serialization package.

---

## 4. Constructor

```java
public SerializationException(String message)
```

Delegates to `RuntimeException(String)`, storing only the message. Used when the failure is self-describing and no lower-level exception exists — an unknown quadrant code, an empty or unrecognised header, or a mismatched origin.

---

## 5. Constructor

```java
public SerializationException(String message, Throwable cause)
```

Delegates to `RuntimeException(String, Throwable)`, preserving the original `IOException`, `NumberFormatException`, `IllegalArgumentException` or `CommandSyntaxException` as the cause. The message stays user-facing while the cause keeps the technical detail available in logs and crash reports.

---
