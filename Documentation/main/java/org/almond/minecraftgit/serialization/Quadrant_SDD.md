# Quadrant — Software Detailed Design

> **API Documentation:** [Quadrant.md](./Quadrant.md)  
> **Source File:** [Quadrant.java](../../../../../../../src/main/java/org/almond/minecraftgit/serialization/Quadrant.java)

## Table of Contents

- [Usage](#usage)
- [1. Overview](#1-overview)
- [2. Package Declaration & Imports](#2-package-declaration-imports)
- [3. Class Declaration](#3-class-declaration)
- [4. Instance Fields](#4-instance-fields)
- [5. `code()`](#5-code)
- [6. `positiveX()`](#6-positivex)
- [7. `positiveZ()`](#7-positivez)
- [8. `rowToX()`](#8-rowtox)
- [9. `columnToZ()`](#9-columntoz)
- [10. `rowForX()`](#10-rowforx)
- [11. `columnForZ()`](#11-columnforz)
- [12. `fromOffsets()`](#12-fromoffsets)
- [13. `fromCode()`](#13-fromcode)

## Usage

TODO: Describe how `Quadrant` is instantiated and used.

---

## 1. Overview

Quadrant of the relative XZ plane. The four files per Y level are named by
the signs of X and Z, in that order.

---

## 2. Package Declaration & Imports

```java
package org.almond.minecraftgit.serialization;
```

---

## 3. Class Declaration

```java
public enum Quadrant {
```


---

## 4. Instance Fields

```java
private final String code;
private final boolean positiveX;
private final boolean positiveZ;
```

| Field | Type | Description |
|-------|------|-------------|
| `code` | `String` | TODO: describe |
| `positiveX` | `boolean` | TODO: describe |
| `positiveZ` | `boolean` | TODO: describe |

---

## 5. `code()`

```java
public String code()
```

TODO: Provide detailed design explanation for `code`.

---

## 6. `positiveX()`

```java
public boolean positiveX()
```

TODO: Provide detailed design explanation for `positiveX`.

---

## 7. `positiveZ()`

```java
public boolean positiveZ()
```

TODO: Provide detailed design explanation for `positiveZ`.

---

## 8. `rowToX()`

```java
public int rowToX(int row)
```

Row 0 is the origin-adjacent coordinate; rows grow away from the origin.

---

## 9. `columnToZ()`

```java
public int columnToZ(int column)
```

TODO: Provide detailed design explanation for `columnToZ`.

---

## 10. `rowForX()`

```java
public int rowForX(int rx)
```

TODO: Provide detailed design explanation for `rowForX`.

---

## 11. `columnForZ()`

```java
public int columnForZ(int rz)
```

TODO: Provide detailed design explanation for `columnForZ`.

---

## 12. `fromOffsets()`

```java
public static Quadrant fromOffsets(int rx, int rz)
```

TODO: Provide detailed design explanation for `fromOffsets`.

---

## 13. `fromCode()`

```java
public static Quadrant fromCode(String code)
```

TODO: Provide detailed design explanation for `fromCode`.

---
