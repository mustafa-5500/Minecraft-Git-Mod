# MinecraftGit — Software Detailed Design

> **API Documentation:** [MinecraftGit.md](./MinecraftGit.md)  
> **Source File:** [MinecraftGit.java](../../../../../../src/main/java/org/almond/minecraftgit/MinecraftGit.java)

## Table of Contents

- [Usage](#usage)
- [1. Overview](#1-overview)
- [2. Package Declaration & Imports](#2-package-declaration-imports)
- [3. Class Declaration](#3-class-declaration)
- [4. Instance Fields](#4-instance-fields)
- [5. Constructor](#5-constructor)
- [6. `id()`](#6-id)

## Usage

TODO: Describe how `MinecraftGit` is instantiated and used.

---

## 1. Overview

Shared identity for the client-side Minecraft Git mod.
<p>This class lives in {@code src/main} so the repository and serialization
layers can refer to the mod id without pulling in client-only classes.</p>

---

## 2. Package Declaration & Imports

```java
package org.almond.minecraftgit;
```

```java
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
```

| Import | Purpose |
|--------|---------|
| `ResourceLocation` | TODO: describe purpose |
| `Logger` | TODO: describe purpose |
| `LoggerFactory` | TODO: describe purpose |

---

## 3. Class Declaration

```java
public final class MinecraftGit {
```


---

## 4. Instance Fields

```java
public static final String MOD_ID;
public static final Logger LOGGER;
```

| Field | Type | Description |
|-------|------|-------------|
| `MOD_ID` | `String` | TODO: describe |
| `LOGGER` | `Logger` | TODO: describe |

---

## 5. Constructor

```java
private MinecraftGit()
```

TODO: Provide detailed design explanation for `MinecraftGit`.

---

## 6. `id()`

```java
public static ResourceLocation id(String path)
```

Creates a {@link ResourceLocation} in this mod's namespace.

---
