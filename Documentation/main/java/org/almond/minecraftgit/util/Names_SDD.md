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

Used statically wherever a selection name becomes a path or a world id becomes a directory name; call sites include `SelectionService.requireName`, `SelectionMetadataStore.file` and `StagingService`.

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
| `Locale` | `Locale.ROOT` makes `lower()` deterministic across player locales. |
| `Pattern` | Compiles the validation and sanitization regexes once as static fields. |

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
| `SELECTION` | `Pattern` | Allow-list pattern for user-typed selection names. |
| `WORLD` | `Pattern` | Deny-list pattern of characters replaced when generating safe world names. |

---

## 5. Constructor

```java
private Names()
```

Private to make the utility non-instantiable; the class has no state and every method is static.

---

## 6. `isValidSelectionName()`

```java
public static boolean isValidSelectionName(String name)
```

Single `matcher.matches()` check with an explicit null guard; the full-match pattern enforces length, first character and allowed alphabet in one pass.

---

## 7. `requireSelectionName()`

```java
public static String requireSelectionName(String name)
```

Returns the name when `isValidSelectionName` passes, otherwise throws `IllegalArgumentException` with the canonical rule message. Returning the value lets callers use it as a normalizing expression, and validation always happens before a path is built.

---

## 8. `safeWorldName()`

```java
public static String safeWorldName(String world)
```

Runs the `WORLD` pattern replacement of each unsafe character with `_` and substitutes the `world` sentinel for null; this keeps arbitrary ids from escaping the data directory or producing invalid file names.

---

## 9. `lower()`

```java
public static String lower(String value)
```

Wraps null as an empty string and calls `toLowerCase(Locale.ROOT)`, avoiding default-locale casing surprises such as the Turkish dotless i.

---

## 10. `shorten()`

```java
public static String shorten(String value, int max)
```

Null-safe length check: values within `max` are returned as-is, while longer ones are cut at `max` and suffixed with `...`. No Unicode-aware truncation is attempted.

---
