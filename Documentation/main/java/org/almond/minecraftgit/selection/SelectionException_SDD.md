# SelectionException — Software Detailed Design

> **API Documentation:** [SelectionException.md](./SelectionException.md)  
> **Source File:** [SelectionException.java](../../../../../../../src/main/java/org/almond/minecraftgit/selection/SelectionException.java)

## Table of Contents

- [Usage](#usage)
- [1. Overview](#1-overview)
- [2. Package Declaration & Imports](#2-package-declaration-imports)
- [3. Class Declaration](#3-class-declaration)
- [4. Constructor](#4-constructor)

## Usage

TODO: Describe how `SelectionException` is instantiated and used.

---

## 1. Overview

A selection or wand operation the user can correct.

---

## 2. Package Declaration & Imports

```java
package org.almond.minecraftgit.selection;
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
public final class SelectionException extends UserFacingException {
```

- **`extends UserFacingException`** — TODO: explain inheritance.

---

## 4. Constructor

```java
public SelectionException(String message)
```

TODO: Provide detailed design explanation for `SelectionException`.

---
