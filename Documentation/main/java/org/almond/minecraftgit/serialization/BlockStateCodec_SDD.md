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

`BlockStateCodec` is a static conversion layer, never instantiated. `QuadrantCsvWriter`/`SnapshotCell` use `encode` to turn each captured `BlockState` into cell text, and the world-apply code uses `decode` to turn committed text back into a state; `isAir` and `isKnown` are the cheap classification helpers used while comparing or validating cells.

---

## 1. Overview

Converts block states to and from the canonical repository string.
Format: `namespace:block[property=value,property=value]`. Properties
are sorted by name so captures are reproducible regardless of registry
iteration order. Unknown blocks and properties decode to air/default values
rather than aborting an apply.

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
| `BuiltInRegistries` | Resolves the block id for encoding and looks ids up for decoding, including the air fallback. |
| `ResourceLocation` | Parses and validates the `namespace:path` id half of a cell string. |
| `Block` | The block type used to reach the state definition when applying properties. |
| `Blocks` | Supplies `Blocks.AIR.defaultBlockState()` as the fallback for blank or unknown input. |
| `BlockState` | Input to `encode` and result of `decode`. |
| `Property` | Describes one state property and converts values to and from their string names. |
| `ArrayList` | Copies the state's property collection so it can be sorted without touching the state. |
| `Comparator` | Implements the name-based property ordering that makes captures reproducible. |
| `List` | Holds the sorted properties during encoding. |

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

Private no-argument constructor; the class is a collection of pure static functions over registry state, so instantiation would add nothing.

---

## 5. `encode()`

```java
public static String encode(BlockState state)
```

The block id comes from `BuiltInRegistries.BLOCK.getKey(state.getBlock())`, which is the same namespace:path used by commands and the plugin. `state.getProperties()` is copied into an `ArrayList` and sorted with `Comparator.comparing(Property::getName)` because the collection's own order follows registration and is not a stable contract; sorting makes the same state produce byte-identical text across runs and machines. Values are rendered by `valueName` rather than `toString`, and the brackets are omitted entirely when the list is empty, giving the `minecraft:stone` form required by the format.

---

## 6. `valueName()`

```java
@SuppressWarnings({"unchecked", "rawtypes"})
private static String valueName(BlockState state, Property property)
```

`Property` is generic in its value type, so the method accepts the raw type and casts the value to `Comparable` to call `Property#getName(Comparable)`. This keeps the generic complexity out of `encode`, which only needs a string. The unchecked annotation documents that the cast is safe by construction: the property instance always comes from `state.getProperties()`, so its value type matches.

---

## 7. `decode()`

```java
public static BlockState decode(String blockData)
```

Registry-safe decode; a missing/unknown state becomes air.

Null or blank input short-circuits to air. The method trims the text, splits on the first `[` only when the string also ends with `]`, and parses the remaining base with `ResourceLocation.tryParse`; if the id is malformed or absent from `BuiltInRegistries.BLOCK`, it returns air. A valid id starts from `block.defaultBlockState()` and then applies each comma-separated `name=value` token whose name matches a property in the block's state definition; tokens without `=` are skipped. Unknown properties are skipped and invalid values are ignored by `applyProperty`, so a state removed by a game update degrades to defaults instead of aborting the apply.

---

## 8. `applyProperty()`

```java
@SuppressWarnings("unchecked")
private static <T extends Comparable<T>> BlockState applyProperty(BlockState state, Property<T> property,
                                                                      String value)
```

`property.getValue(String)` returns an `Optional<T>`; `map` applies `state.setValue(property, parsed)` and `orElse(state)` keeps the previous state when parsing fails. States are immutable and `setValue` returns a new instance, so the accumulation in `decode` relies on this return value. The generic signature lets `decode` pass a `Property<?>` after an unchecked cast, since the value type is not known at the call site.

---

## 9. `isAir()`

```java
public static boolean isAir(String blockData)
```

A convenience predicate over `decode`: it treats `null` as air and otherwise decodes the text and calls `BlockState#isAir`. This means a known non-air block whose id appears with invalid properties is still non-air (defaults apply), while a removed block id is air, which is exactly the behaviour the diff and apply code needs.

---

## 10. `isKnown()`

```java
public static boolean isKnown(String blockData)
```

True when the block id exists in this registry (properties are optional).

Checks only the id: it trims the text, cuts at the first `[` if present, parses the base with `ResourceLocation.tryParse` and asks `BuiltInRegistries.BLOCK.containsKey`. Properties are deliberately ignored so that a known block with properties from a newer version is still considered known and its block can be placed with defaults.

---
