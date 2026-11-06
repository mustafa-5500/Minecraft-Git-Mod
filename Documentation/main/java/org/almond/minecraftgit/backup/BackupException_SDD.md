# BackupException — Software Detailed Design

> **API Documentation:** [BackupException.md](./BackupException.md)  
> **Source File:** [BackupException.java](../../../../../../../src/main/java/org/almond/minecraftgit/backup/BackupException.java)

## Table of Contents

- [Usage](#usage)
- [1. Overview](#1-overview)
- [2. Package Declaration & Imports](#2-package-declaration-imports)
- [3. Class Declaration](#3-class-declaration)
- [4. Constructor](#4-constructor)
- [5. Constructor](#5-constructor)

## Usage

TODO: Describe how `BackupException` is instantiated and used.

---

## 1. Overview

A backup could not be created or read.

---

## 2. Package Declaration & Imports

```java
package org.almond.minecraftgit.backup;
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
public final class BackupException extends UserFacingException {
```

- **`extends UserFacingException`** — TODO: explain inheritance.

---

## 4. Constructor

```java
public BackupException(String message)
```

TODO: Provide detailed design explanation for `BackupException`.

---

## 5. Constructor

```java
public BackupException(String message, Throwable cause)
```

TODO: Provide detailed design explanation for `BackupException`.

---
