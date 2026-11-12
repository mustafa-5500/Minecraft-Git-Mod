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

Referenced statically from both source sets; the client entrypoint reads `MOD_ID` for its config directory, main and client code log through `LOGGER`, and `id()` creates namespaced resources.

---

## 1. Overview

Shared identity for the client-side Minecraft Git mod.
This class lives in `src/main` so the repository and serialization
layers can refer to the mod id without pulling in client-only classes.

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
| `ResourceLocation` | Return type of `id()` for namespaced identifiers. |
| `Logger` | SLF4J logging facade shared by all layers. |
| `LoggerFactory` | Creates the shared logger named after the mod id. |

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
| `MOD_ID` | `String` | Single source of truth for the mod id and resource namespace (`minecraftgit`). |
| `LOGGER` | `Logger` | Shared logger created from `MOD_ID` at class initialization. |

---

## 5. Constructor

```java
private MinecraftGit()
```

Private and empty: the class is a static identity holder, and preventing instantiation keeps the API to constants and `id()`.

---

## 6. `id()`

```java
public static ResourceLocation id(String path)
```

Creates a `ResourceLocation` in this mod's namespace.

---
