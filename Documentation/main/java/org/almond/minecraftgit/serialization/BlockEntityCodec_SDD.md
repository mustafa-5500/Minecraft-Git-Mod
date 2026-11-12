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

`BlockEntityCodec` is never instantiated; capture code calls `encode` on each block entity and the world-apply path calls `apply` after placing the block. The encoded string is later Base64-wrapped by `SnapshotCell.encode`, so this class deals only with SNBT text and never with CSV quoting. A payload produced by the Paper plugin starts with `{` and fails `isNative`, which is how the mod avoids silently mis-converting it.

---

## 1. Overview

Serializes block-entity data as native SNBT, prefixed with `nbt:` inside
the CSV cell. Unlike the Paper plugin's typed JSON subset this can carry the
full data the client knows about, including inventories and component data.
Payloads written by the plugin start with `{` and are deliberately
not parsed here; the block itself still applies and the caller can warn about
the unsupported payload.

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
| `CommandSyntaxException` | Checked parse failure of `TagParser.parseTag`, wrapped into `SerializationException`. |
| `HolderLookup` | Registry access passed to both `saveWithFullMetadata` and `loadWithComponents`, so component data round-trips. |
| `CompoundTag` | In-memory NBT form of the block entity; `isEmpty` also decides whether to omit the payload. |
| `TagParser` | Parses the SNBT text after the `nbt:` prefix back into a `CompoundTag`. |
| `BlockEntity` | The live object being saved or restored. |

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
| `PREFIX` | `String` | Constant `nbt:` written before the SNBT and stripped before parsing; it is the marker that distinguishes native NBT from the plugin's JSON, which starts with `{`. |

---

## 5. Constructor

```java
private BlockEntityCodec()
```

Private no-argument constructor; the class is stateless and all entry points are static.

---

## 6. `encode()`

```java
public static String encode(BlockEntity blockEntity, HolderLookup.Provider registries)
```

`saveWithFullMetadata` is chosen over the lighter save methods because the repository is meant to round-trip what the client can see, including inventories and component data that the Paper plugin omitted. `CompoundTag.toString()` produces SNBT, so `PREFIX + tag` is directly the `nbt:{...}` payload documented in the format. An empty tag (a block entity with no state worth keeping) returns `null` rather than `nbt:{}`, which lets `SnapshotCell.encode` write the plain block state.

---

## 7. `isNative()`

```java
public static boolean isNative(String payload)
```

A null-safe `startsWith(PREFIX)` test. It has no side effects and is used both to reject unsupported payloads before parsing and to document in one place what "native" means: the `nbt:` marker, not the shape of the text that follows.

---

## 8. `apply()`

```java
public static boolean apply(BlockEntity blockEntity, String payload, HolderLookup.Provider registries)
```

Apply a native payload to a block entity that already exists at the target
position. Returns false for plugin/unknown payloads instead of guessing.

Blank and non-native payloads return `false` before any parsing, so a plugin JSON document leaves the freshly placed block entity at its default contents rather than partially overwriting it. Native payloads strip `PREFIX`, parse with `TagParser.parseTag` and load via `loadWithComponents`, after which `setChanged` marks the chunk dirty; parse or load failures are wrapped in `SerializationException`, so the block state stays applied while the apply aborts with a user-visible message.

---

## 9. `payloadBytes()`

```java
public static int payloadBytes(String payload)
```

A null-safe UTF-8 length used for limit enforcement; measuring the encoded payload (rather than the Base64 cell) matches the configured 256 KiB block-entity payload limit and keeps the check independent of CSV quoting. It allocates the byte array on each call, which is acceptable because it runs once per captured block entity.

---
