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

`BlockEntityCodec.encode` captures a block entity while a snapshot is taken and `apply` restores it after the block state has been placed; `SnapshotCell` stores the returned string in the CSV cell behind `|nbt|`. For example, encoding a chest returns `nbt:{id:"minecraft:chest",...}` and `apply` parses that SNBT back into a freshly placed chest.

Serializes block-entity data as native SNBT, prefixed with `nbt:` inside
the CSV cell. Unlike the Paper plugin's typed JSON subset this can carry the
full data the client knows about, including inventories and component data.
Payloads written by the plugin start with `{` and are deliberately
not parsed here; the block itself still applies and the caller can warn about
the unsupported payload.

---

## Fields

- `PREFIX` (`String`) — Literal `nbt:` that marks a payload as native SNBT; plugin JSON payloads start with `{` instead, so `isNative` can tell them apart.

---

## BlockEntityCodec (Constructor)

### Signature
```java
private BlockEntityCodec()
```

### Description
Private and empty: the class exposes only static methods, so no codec instance is ever constructed.

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
Calls `BlockEntity#saveWithFullMetadata(registries)` so the payload carries everything the client knows, then returns `nbt:` plus the tag's SNBT; an empty tag yields `null` because there is nothing worth storing. The payload can be large, so callers check it against the configured 256 KiB limit with `payloadBytes`.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `blockEntity` | `BlockEntity` | Block entity to capture. |
| `registries` | `HolderLookup.Provider` | Registry lookup provider needed to serialise component data. |

### Returns
`String` — the payload, or `null` when there is nothing worth storing.

---

## isNative

### Signature
```java
public static boolean isNative(String payload)
```

### Description
Returns `true` only when the payload is non-null and starts with `nbt:`; it is the guard that lets `apply` refuse the Paper plugin's JSON payloads rather than guessing at a conversion.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `payload` | `String` | Candidate payload from a CSV cell; may be `null`. |

### Returns
`boolean` — `true` for a native SNBT payload, `false` for `null` or a plugin JSON document.

---

## apply

### Signature
```java
public static boolean apply(BlockEntity blockEntity, String payload, HolderLookup.Provider registries)
```

### Description
Apply a native payload to a block entity that already exists at the target
position. Returns false for plugin/unknown payloads instead of guessing.

Invalid SNBT or a load failure raises `SerializationException`; the block state itself is already applied at that point.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `blockEntity` | `BlockEntity` | Existing block entity at the target position; the block itself is placed by the caller first. |
| `payload` | `String` | Cell payload; blank or non-native values are rejected. |
| `registries` | `HolderLookup.Provider` | Provider passed to `loadWithComponents` when loading the parsed tag. |

### Returns
`boolean` — `true` when the payload was parsed and loaded, `false` when it is blank or not native.

---

## payloadBytes

### Signature
```java
public static int payloadBytes(String payload)
```

### Description
Returns the UTF-8 byte length of the payload (`0` for `null`), so the caller can enforce the repository's 256 KiB block-entity payload limit before writing the cell.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `payload` | `String` | Payload to measure; may be `null`. |

### Returns
`int` — Number of UTF-8 bytes, never negative.

---

## See Also

- **Software Detailed Design:** [BlockEntityCodec_SDD.md](./BlockEntityCodec_SDD.md)
