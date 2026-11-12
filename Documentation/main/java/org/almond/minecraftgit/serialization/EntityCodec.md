# EntityCodec

> **Software Detailed Documentation:** [EntityCodec_SDD.md](./EntityCodec_SDD.md)  
> **Source File:** [EntityCodec.java](../../../../../../../src/main/java/org/almond/minecraftgit/serialization/EntityCodec.java)

## Table of Contents

- [Usage](#usage)
- [Fields](#fields)
- [EntityCodec (Constructor)](#entitycodec-constructor)
- [encode](#encode)
- [apply](#apply)
- [payloadBytes](#payloadbytes)
- [See Also](#see-also)

## Usage

`EntityCodec.encode` serialises a live entity during capture and `apply` recreates it in a `ServerLevel` during a pull; the encoded text becomes the Base64 column of `entities.csv`. For example, an item frame is encoded as `nbt:{...}` and applied at the absolute position computed from the snapshot's relative offsets plus the selection origin.

Serializes entities as SNBT plus their registry type id. The position and
rotation live in `EntitySnapshot` so a build can be shifted to a new
origin without rewriting NBT.

---

## Fields

- `PREFIX` (`String`) — Literal `nbt:` prefixed to every encoded entity and stripped by `apply` before parsing; it identifies the payload as native SNBT in the CSV.

---

## EntityCodec (Constructor)

### Signature
```java
private EntityCodec()
```

### Description
Private and empty: the class is a static namespace for entity serialisation and is never instantiated.

### Parameters
None.

### Returns
`EntityCodec` instance.

---

## encode

### Signature
```java
public static String encode(Entity entity)
```

### Description
Calls `Entity#saveWithoutId(new CompoundTag())` and returns `nbt:` plus the resulting SNBT, so the entity type is stored separately in its own CSV column and re-inserted as `id` when the entity is recreated. Position and rotation are also carried in the `EntitySnapshot`, which is what allows a build to be shifted to a new origin without rewriting NBT.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `entity` | `Entity` | Entity to serialise; must not be `null`. |

### Returns
`String` — SNBT payload beginning with `nbt:`.

---

## apply

### Signature
```java
public static Entity apply(ServerLevel level, EntitySnapshot snapshot,
                               double x, double y, double z)
```

### Description
Create and add the entity at an absolute position. Returns `null`
when the payload or type is not loadable in this world.

A parse or creation failure raises `SerializationException` naming the entity type.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `level` | `ServerLevel` | Server level that receives the new entity. |
| `snapshot` | [`EntitySnapshot`](../model/EntitySnapshot.md) | Snapshot supplying the type, yaw, pitch and SNBT payload. |
| `x` | `double` | Absolute world X for the entity position. |
| `y` | `double` | Absolute world Y for the entity position. |
| `z` | `double` | Absolute world Z for the entity position. |

### Returns
`Entity` — The entity added to the level, or `null` when the payload is blank or the type cannot be created.

---

## payloadBytes

### Signature
```java
public static int payloadBytes(String payload)
```

### Description
Returns the UTF-8 byte length of the payload (`0` for `null`), used by the capture code to enforce the configured payload byte limit before storing the entity.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `payload` | `String` | Payload to measure; may be `null`. |

### Returns
`int` — Number of UTF-8 bytes, never negative.

---

## See Also

- **Software Detailed Design:** [EntityCodec_SDD.md](./EntityCodec_SDD.md)
