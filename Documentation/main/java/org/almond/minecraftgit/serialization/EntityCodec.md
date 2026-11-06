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

TODO: Add usage examples for `EntityCodec`.

Serializes entities as SNBT plus their registry type id. The position and
rotation live in {@link EntitySnapshot} so a build can be shifted to a new
origin without rewriting NBT.

---

## Fields

- `PREFIX` (`String`) — TODO: describe field

---

## EntityCodec (Constructor)

### Signature
```java
private EntityCodec()
```

### Description
TODO: Describe what `EntityCodec` does.

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
TODO: Describe what `encode` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `entity` | `Entity` | TODO: describe |

### Returns
`String` — TODO: describe return value.

---

## apply

### Signature
```java
public static Entity apply(ServerLevel level, EntitySnapshot snapshot,
                               double x, double y, double z)
```

### Description
Create and add the entity at an absolute position. Returns {@code null}
when the payload or type is not loadable in this world.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `level` | `ServerLevel` | TODO: describe |
| `snapshot` | [`EntitySnapshot`](../model/EntitySnapshot.md) | TODO: describe |
| `x` | `double` | TODO: describe |
| `y` | `double` | TODO: describe |
| `z` | `double` | TODO: describe |

### Returns
`Entity` — TODO: describe return value.

---

## payloadBytes

### Signature
```java
public static int payloadBytes(String payload)
```

### Description
TODO: Describe what `payloadBytes` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `payload` | `String` | TODO: describe |

### Returns
`int` — TODO: describe return value.

---

## See Also

- **Software Detailed Design:** [EntityCodec_SDD.md](./EntityCodec_SDD.md)
