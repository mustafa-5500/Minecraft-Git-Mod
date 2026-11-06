# BlockStateCodec — Software Detailed Design

> **API Documentation:** [BlockStateCodec.md](./BlockStateCodec.md)  
> **Source File:** [BlockStateCodec.java](../../../../../../../src/main/java/org/almond/minecraftgit/serialization/BlockStateCodec.java)

## Table of Contents

- [Usage](#usage)
- [1. Overview](#1-overview)
- [2. Package Declaration & Imports](#2-package-declaration-imports)
- [3. Class Declaration](#3-class-declaration)
- [4. Constructor](#4-constructor)
- [5. `encode()`](#5-encode)
- [6. `valueName()`](#6-valuename)
- [7. `decode()`](#7-decode)
- [8. `applyProperty()`](#8-applyproperty)
- [9. `isAir()`](#9-isair)
- [10. `isKnown()`](#10-isknown)

## Usage

TODO: Describe how `BlockStateCodec` is instantiated and used.

---

## 1. Overview

Converts block states to and from the canonical repository string.
<p>Format: {@code namespace:block[property=value,property=value]}. Properties
are sorted by name so captures are reproducible regardless of registry
iteration order. Unknown blocks and properties decode to air/default values
rather than aborting an apply.</p>

---

## 2. Package Declaration & Imports

```java
package org.almond.minecraftgit.serialization;
```

```java
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
```

| Import | Purpose |
|--------|---------|
| `BuiltInRegistries` | TODO: describe purpose |
| `ResourceLocation` | TODO: describe purpose |
| `Block` | TODO: describe purpose |
| `Blocks` | TODO: describe purpose |
| `BlockState` | TODO: describe purpose |
| `Property` | TODO: describe purpose |
| `ArrayList` | TODO: describe purpose |
| `Comparator` | TODO: describe purpose |
| `List` | TODO: describe purpose |

---

## 3. Class Declaration

```java
public final class BlockStateCodec {
```


---

## 4. Constructor

```java
private BlockStateCodec()
```

TODO: Provide detailed design explanation for `BlockStateCodec`.

---

## 5. `encode()`

```java
public static String encode(BlockState state)
```

TODO: Provide detailed design explanation for `encode`.

---

## 6. `valueName()`

```java
@SuppressWarnings({"unchecked", "rawtypes"})
private static String valueName(BlockState state, Property property)
```

TODO: Provide detailed design explanation for `valueName`.

---

## 7. `decode()`

```java
public static BlockState decode(String blockData)
```

Registry-safe decode; a missing/unknown state becomes air.

---

## 8. `applyProperty()`

```java
@SuppressWarnings("unchecked")
private static <T extends Comparable<T>> BlockState applyProperty(BlockState state, Property<T> property,
                                                                      String value)
```

TODO: Provide detailed design explanation for `applyProperty`.

---

## 9. `isAir()`

```java
public static boolean isAir(String blockData)
```

TODO: Provide detailed design explanation for `isAir`.

---

## 10. `isKnown()`

```java
public static boolean isKnown(String blockData)
```

True when the block id exists in this registry (properties are optional).

---
