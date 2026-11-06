# Names — Software Detailed Design

> **API Documentation:** [Names.md](./Names.md)  
> **Source File:** [Names.java](../../../../../../../src/main/java/org/almond/minecraftgit/util/Names.java)

## Table of Contents

- [Usage](#usage)
- [1. Overview](#1-overview)
- [2. Package Declaration & Imports](#2-package-declaration-imports)
- [3. Class Declaration](#3-class-declaration)
- [4. Instance Fields](#4-instance-fields)
- [5. Constructor](#5-constructor)
- [6. `isValidSelectionName()`](#6-isvalidselectionname)
- [7. `requireSelectionName()`](#7-requireselectionname)
- [8. `safeWorldName()`](#8-safeworldname)
- [9. `lower()`](#9-lower)
- [10. `shorten()`](#10-shorten)

## Usage

TODO: Describe how `Names` is instantiated and used.

---

## 1. Overview

Selection names become directory names inside a Git repository, so they are
restricted instead of being sanitized after the fact. World/player ids are
sanitized because the player never types them.

---

## 2. Package Declaration & Imports

```java
package org.almond.minecraftgit.util;
```

```java
import java.util.Locale;
import java.util.regex.Pattern;
```

| Import | Purpose |
|--------|---------|
| `Locale` | TODO: describe purpose |
| `Pattern` | TODO: describe purpose |

---

## 3. Class Declaration

```java
public final class Names {
```


---

## 4. Instance Fields

```java
private static final Pattern SELECTION;
private static final Pattern WORLD;
```

| Field | Type | Description |
|-------|------|-------------|
| `SELECTION` | `Pattern` | TODO: describe |
| `WORLD` | `Pattern` | TODO: describe |

---

## 5. Constructor

```java
private Names()
```

TODO: Provide detailed design explanation for `Names`.

---

## 6. `isValidSelectionName()`

```java
public static boolean isValidSelectionName(String name)
```

TODO: Provide detailed design explanation for `isValidSelectionName`.

---

## 7. `requireSelectionName()`

```java
public static String requireSelectionName(String name)
```

TODO: Provide detailed design explanation for `requireSelectionName`.

---

## 8. `safeWorldName()`

```java
public static String safeWorldName(String world)
```

TODO: Provide detailed design explanation for `safeWorldName`.

---

## 9. `lower()`

```java
public static String lower(String value)
```

TODO: Provide detailed design explanation for `lower`.

---

## 10. `shorten()`

```java
public static String shorten(String value, int max)
```

TODO: Provide detailed design explanation for `shorten`.

---
