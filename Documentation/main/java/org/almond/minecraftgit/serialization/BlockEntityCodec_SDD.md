# BlockEntityCodec — Software Detailed Design

> **API Documentation:** [BlockEntityCodec.md](./BlockEntityCodec.md)  
> **Source File:** [BlockEntityCodec.java](../../../../../../../src/main/java/org/almond/minecraftgit/serialization/BlockEntityCodec.java)

## Table of Contents

- [Usage](#usage)
- [1. Overview](#1-overview)
- [2. Package Declaration & Imports](#2-package-declaration-imports)
- [3. Class Declaration](#3-class-declaration)
- [4. Instance Fields](#4-instance-fields)
- [5. Constructor](#5-constructor)
- [6. `encode()`](#6-encode)
- [7. `isNative()`](#7-isnative)
- [8. `apply()`](#8-apply)
- [9. `payloadBytes()`](#9-payloadbytes)

## Usage

TODO: Describe how `BlockEntityCodec` is instantiated and used.

---

## 1. Overview

Serializes block-entity data as native SNBT, prefixed with {@code nbt:} inside
the CSV cell. Unlike the Paper plugin's typed JSON subset this can carry the
full data the client knows about, including inventories and component data.
<p>Payloads written by the plugin start with {@code {} and are deliberately
not parsed here; the block itself still applies and the caller can warn about
the unsupported payload.</p>

---

## 2. Package Declaration & Imports

```java
package org.almond.minecraftgit.serialization;
```

```java
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.level.block.entity.BlockEntity;
```

| Import | Purpose |
|--------|---------|
| `CommandSyntaxException` | TODO: describe purpose |
| `HolderLookup` | TODO: describe purpose |
| `CompoundTag` | TODO: describe purpose |
| `TagParser` | TODO: describe purpose |
| `BlockEntity` | TODO: describe purpose |

---

## 3. Class Declaration

```java
public final class BlockEntityCodec {
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
private BlockEntityCodec()
```

TODO: Provide detailed design explanation for `BlockEntityCodec`.

---

## 6. `encode()`

```java
public static String encode(BlockEntity blockEntity, HolderLookup.Provider registries)
```

TODO: Provide detailed design explanation for `encode`.

---

## 7. `isNative()`

```java
public static boolean isNative(String payload)
```

TODO: Provide detailed design explanation for `isNative`.

---

## 8. `apply()`

```java
public static boolean apply(BlockEntity blockEntity, String payload, HolderLookup.Provider registries)
```

Apply a native payload to a block entity that already exists at the target
position. Returns false for plugin/unknown payloads instead of guessing.

---

## 9. `payloadBytes()`

```java
public static int payloadBytes(String payload)
```

TODO: Provide detailed design explanation for `payloadBytes`.

---
