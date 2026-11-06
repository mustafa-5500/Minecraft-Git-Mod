# BlockEntityCodec

> **Software Detailed Documentation:** [BlockEntityCodec_SDD.md](./BlockEntityCodec_SDD.md)  
> **Source File:** [BlockEntityCodec.java](../../../../../../../src/main/java/org/almond/minecraftgit/serialization/BlockEntityCodec.java)

## Table of Contents

- [Usage](#usage)
- [Fields](#fields)
- [BlockEntityCodec (Constructor)](#blockentitycodec-constructor)
- [encode](#encode)
- [isNative](#isnative)
- [apply](#apply)
- [payloadBytes](#payloadbytes)
- [See Also](#see-also)

## Usage

TODO: Add usage examples for `BlockEntityCodec`.

Serializes block-entity data as native SNBT, prefixed with {@code nbt:} inside
the CSV cell. Unlike the Paper plugin's typed JSON subset this can carry the
full data the client knows about, including inventories and component data.
<p>Payloads written by the plugin start with {@code {} and are deliberately
not parsed here; the block itself still applies and the caller can warn about
the unsupported payload.</p>

---

## Fields

- `PREFIX` (`String`) — TODO: describe field

---

## BlockEntityCodec (Constructor)

### Signature
```java
private BlockEntityCodec()
```

### Description
TODO: Describe what `BlockEntityCodec` does.

### Parameters
None.

### Returns
`BlockEntityCodec` instance.

---

## encode

### Signature
```java
public static String encode(BlockEntity blockEntity, HolderLookup.Provider registries)
```

### Description
TODO: Describe what `encode` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `blockEntity` | `BlockEntity` | TODO: describe |
| `registries` | `HolderLookup.Provider` | TODO: describe |

### Returns
`String` — the payload, or {@code null} when there is nothing worth storing.

---

## isNative

### Signature
```java
public static boolean isNative(String payload)
```

### Description
TODO: Describe what `isNative` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `payload` | `String` | TODO: describe |

### Returns
`boolean` — TODO: describe return value.

---

## apply

### Signature
```java
public static boolean apply(BlockEntity blockEntity, String payload, HolderLookup.Provider registries)
```

### Description
Apply a native payload to a block entity that already exists at the target
position. Returns false for plugin/unknown payloads instead of guessing.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `blockEntity` | `BlockEntity` | TODO: describe |
| `payload` | `String` | TODO: describe |
| `registries` | `HolderLookup.Provider` | TODO: describe |

### Returns
`boolean` — TODO: describe return value.

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

- **Software Detailed Design:** [BlockEntityCodec_SDD.md](./BlockEntityCodec_SDD.md)
