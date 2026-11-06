# EntityCodec — Software Detailed Design

> **API Documentation:** [EntityCodec.md](./EntityCodec.md)  
> **Source File:** [EntityCodec.java](../../../../../../../src/main/java/org/almond/minecraftgit/serialization/EntityCodec.java)

## Table of Contents

- [Usage](#usage)
- [1. Overview](#1-overview)
- [2. Package Declaration & Imports](#2-package-declaration-imports)
- [3. Class Declaration](#3-class-declaration)
- [4. Instance Fields](#4-instance-fields)
- [5. Constructor](#5-constructor)
- [6. `encode()`](#6-encode)
- [7. `apply()`](#7-apply)
- [8. `payloadBytes()`](#8-payloadbytes)

## Usage

TODO: Describe how `EntityCodec` is instantiated and used.

---

## 1. Overview

Serializes entities as SNBT plus their registry type id. The position and
rotation live in {@link EntitySnapshot} so a build can be shifted to a new
origin without rewriting NBT.

---

## 2. Package Declaration & Imports

```java
package org.almond.minecraftgit.serialization;
```

```java
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.almond.minecraftgit.model.EntitySnapshot;
```

| Import | Purpose |
|--------|---------|
| `CommandSyntaxException` | TODO: describe purpose |
| `CompoundTag` | TODO: describe purpose |
| `TagParser` | TODO: describe purpose |
| `ServerLevel` | TODO: describe purpose |
| `Entity` | TODO: describe purpose |
| `EntityType` | TODO: describe purpose |
| [`EntitySnapshot`](../model/EntitySnapshot.md) | TODO: describe purpose |

---

## 3. Class Declaration

```java
public final class EntityCodec {
```


---

## 4. Instance Fields

```java
public static final String PREFIX;
```

| Field | Type | Description |
|-------|------|-------------|
| `PREFIX` | `String` | TODO: describe |

---

## 5. Constructor

```java
private EntityCodec()
```

TODO: Provide detailed design explanation for `EntityCodec`.

---

## 6. `encode()`

```java
public static String encode(Entity entity)
```

TODO: Provide detailed design explanation for `encode`.

---

## 7. `apply()`

```java
public static Entity apply(ServerLevel level, EntitySnapshot snapshot,
                               double x, double y, double z)
```

Create and add the entity at an absolute position. Returns {@code null}
when the payload or type is not loadable in this world.

---

## 8. `payloadBytes()`

```java
public static int payloadBytes(String payload)
```

TODO: Provide detailed design explanation for `payloadBytes`.

---
