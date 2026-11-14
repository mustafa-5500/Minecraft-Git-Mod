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

`EntityCodec` is never instantiated: `QuadrantCsvWriter`/capture code calls `encode` for each `Entity`, and the pull/apply path calls `apply` once the target chunk and block states are ready. The encoded string is written as the seventh column of `entities.csv` after Base64 encoding, and the row's position/rotation columns come from the `EntitySnapshot` rather than from the payload.

---

## 1. Overview

Serializes entities as SNBT plus their registry type id. The position and
rotation live in `EntitySnapshot` so a build can be shifted to a new
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
| `CommandSyntaxException` | Checked failure of `TagParser.parseTag`, wrapped in `SerializationException`. |
| `CompoundTag` | Receives the saved entity NBT in `encode` and holds the parsed payload before loading in `apply`. |
| `TagParser` | Parses the SNBT string (after the prefix is stripped) into a `CompoundTag`. |
| `ServerLevel` | Destination level for `addFreshEntity`; entities can only be created server-side. |
| `Entity` | Return type and the object saved by `encode`. |
| `EntityType` | `loadEntityRecursive` creates the concrete entity from the `id` stored in the tag. |
| [`EntitySnapshot`](../model/EntitySnapshot.md) | Supplies the payload, registry type, yaw and pitch, and records the relative position in the CSV row. |

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
| `PREFIX` | `String` | Constant `nbt:` written before the SNBT; `apply` strips it when present, and it marks the payload as native rather than JSON. |

---

## 5. Constructor

```java
private EntityCodec()
```

Private no-argument constructor; entity encoding and creation are pure static operations with no cached state.

---

## 6. `encode()`

```java
public static String encode(Entity entity)
```

`entity.saveWithoutId(new CompoundTag())` serialises the entity NBT without the `id` field; the registry type is recorded in its own CSV column and re-inserted as `id` by `apply`. The returned string is `PREFIX` followed by `CompoundTag.toString()` (SNBT), so it is not pre-quoted for CSV: `SnapshotCell`/`writeEntities` handles the Base64 step. Position and rotation remain in `EntitySnapshot` so the coordinate columns can be re-based to a new origin without touching the NBT.

---

## 7. `apply()`

```java
public static Entity apply(ServerLevel level, EntitySnapshot snapshot,
                               double x, double y, double z)
```

Create and add the entity at an absolute position. Returns `null`
when the payload or type is not loadable in this world.

Blank payloads return `null` and create nothing, and a leading `nbt:` prefix is optional and stripped when present, which tolerates hand-written rows. The tag is parsed, `tag.putString("id", snapshot.type())` supplies the registry id that `saveWithoutId` omitted, and `EntityType.loadEntityRecursive(tag, level, loaded -> loaded)` builds the entity or returns `null` for an unknown/unloadable type (also returned as `null`). The method then overwrites position and rotation from the snapshot arguments before `level.addFreshEntity(entity)`, so the CSV coordinates, not stale `Pos`/`Rotation` NBT, decide placement; parse or creation failures are wrapped in `SerializationException` naming the entity type.

---

## 8. `payloadBytes()`

```java
public static int payloadBytes(String payload)
```

Null-safe UTF-8 byte length used for limit checks, mirroring `BlockEntityCodec.payloadBytes`; it measures the SNBT payload rather than the Base64 column so limits are comparable across encode and apply.

---
