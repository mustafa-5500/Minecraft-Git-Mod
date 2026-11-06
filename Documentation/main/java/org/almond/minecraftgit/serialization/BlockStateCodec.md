# BlockStateCodec

> **Software Detailed Documentation:** [BlockStateCodec_SDD.md](./BlockStateCodec_SDD.md)  
> **Source File:** [BlockStateCodec.java](../../../../../../../src/main/java/org/almond/minecraftgit/serialization/BlockStateCodec.java)

## Table of Contents

- [Usage](#usage)
- [BlockStateCodec (Constructor)](#blockstatecodec-constructor)
- [encode](#encode)
- [valueName](#valuename)
- [decode](#decode)
- [applyProperty](#applyproperty)
- [isAir](#isair)
- [isKnown](#isknown)
- [See Also](#see-also)

## Usage

TODO: Add usage examples for `BlockStateCodec`.

Converts block states to and from the canonical repository string.
<p>Format: {@code namespace:block[property=value,property=value]}. Properties
are sorted by name so captures are reproducible regardless of registry
iteration order. Unknown blocks and properties decode to air/default values
rather than aborting an apply.</p>

---

## BlockStateCodec (Constructor)

### Signature
```java
private BlockStateCodec()
```

### Description
TODO: Describe what `BlockStateCodec` does.

### Parameters
None.

### Returns
`BlockStateCodec` instance.

---

## encode

### Signature
```java
public static String encode(BlockState state)
```

### Description
TODO: Describe what `encode` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `state` | `BlockState` | TODO: describe |

### Returns
`String` — TODO: describe return value.

---

## valueName

### Signature
```java
@SuppressWarnings({"unchecked", "rawtypes"})
private static String valueName(BlockState state, Property property)
```

### Description
TODO: Describe what `valueName` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `state` | `BlockState` | TODO: describe |
| `property` | `Property` | TODO: describe |

### Returns
`String` — TODO: describe return value.

---

## decode

### Signature
```java
public static BlockState decode(String blockData)
```

### Description
Registry-safe decode; a missing/unknown state becomes air.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `blockData` | `String` | TODO: describe |

### Returns
`BlockState` — TODO: describe return value.

---

## applyProperty

### Signature
```java
@SuppressWarnings("unchecked")
private static <T extends Comparable<T>> BlockState applyProperty(BlockState state, Property<T> property,
                                                                      String value)
```

### Description
TODO: Describe what `applyProperty` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `state` | `BlockState` | TODO: describe |
| `property` | `Property<T>` | TODO: describe |
| `value` | `String` | TODO: describe |

### Returns
`<T extends Comparable<T>> BlockState` — TODO: describe return value.

---

## isAir

### Signature
```java
public static boolean isAir(String blockData)
```

### Description
TODO: Describe what `isAir` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `blockData` | `String` | TODO: describe |

### Returns
`boolean` — TODO: describe return value.

---

## isKnown

### Signature
```java
public static boolean isKnown(String blockData)
```

### Description
True when the block id exists in this registry (properties are optional).

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `blockData` | `String` | TODO: describe |

### Returns
`boolean` — TODO: describe return value.

---

## See Also

- **Software Detailed Design:** [BlockStateCodec_SDD.md](./BlockStateCodec_SDD.md)
