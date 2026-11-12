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

`BlockStateCodec.encode` produces the canonical cell text for a live `BlockState` and `decode` turns arbitrary cell text back into a registry state, while `isAir` and `isKnown` classify text without applying it. For example, `BlockStateCodec.encode(state)` for a north-facing bottom stair returns `minecraft:oak_stairs[facing=north,half=bottom]`, and `decode` of an unknown id returns air instead of throwing.

Converts block states to and from the canonical repository string.
Format: `namespace:block[property=value,property=value]`. Properties
are sorted by name so captures are reproducible regardless of registry
iteration order. Unknown blocks and properties decode to air/default values
rather than aborting an apply.

---

## BlockStateCodec (Constructor)

### Signature
```java
private BlockStateCodec()
```

### Description
Private and empty: the class is a namespace for static conversion helpers and holds no per-instance state.

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
Looks up the block's registry id, copies its properties into a list and sorts them by `Property#getName`, so the output does not depend on registry iteration order. With no properties the bare id is returned; otherwise it appends `[name=value,...]` using `valueName` for each value.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `state` | `BlockState` | Block state to serialise; must not be `null`. |

### Returns
`String` — Registry id such as `minecraft:chest`, plus sorted properties in brackets when the state has any.

---

## valueName

### Signature
```java
@SuppressWarnings({"unchecked", "rawtypes"})
private static String valueName(BlockState state, Property property)
```

### Description
Resolves the string form of one property value by calling `state.getValue(property)` and then `Property#getName(Comparable)`; the raw cast is needed because the property types are heterogeneous and the method is only called with a property taken from the same state.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `state` | `BlockState` | State that owns the property value. |
| `property` | `Property` | Property definition whose value name is wanted. |

### Returns
`String` — Canonical value name as defined by the property itself, for example `north` or `bottom`.

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
| `blockData` | `String` | Cell text of the form `namespace:block[props]`; `null`, blank or an unknown id all yield air. |

### Returns
`BlockState` — Air for unknown blocks, otherwise the block's default state with each recognised property applied.

---

## applyProperty

### Signature
```java
@SuppressWarnings("unchecked")
private static <T extends Comparable<T>> BlockState applyProperty(BlockState state, Property<T> property,
                                                                      String value)
```

### Description
Parses `value` with `Property#getValue(String)` and returns `state.setValue(property, parsed)` when it is valid, otherwise the original state unchanged, so one unknown value does not abort the whole decode.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `state` | `BlockState` | State being built up; not mutated. |
| `property` | `Property<T>` | Property definition taken from the block's state definition. |
| `value` | `String` | Raw value text from the CSV, already trimmed. |

### Returns
`<T extends Comparable<T>> BlockState` — A state with the property applied, or the same `state` when the value is not valid for it.

---

## isAir

### Signature
```java
public static boolean isAir(String blockData)
```

### Description
Returns `true` for `null` and for any text that decodes to an air block state, including empty text and unknown ids, so callers can classify a cell without applying it.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `blockData` | `String` | Cell text to classify; may be `null`. |

### Returns
`boolean` — `true` when the cell denotes air.

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
| `blockData` | `String` | Cell text; any `[...]` suffix is stripped before the id lookup, and `null` or blank returns `false`. |

### Returns
`boolean` — `true` when `BuiltInRegistries.BLOCK` contains the id; the properties themselves are not validated.

---

## See Also

- **Software Detailed Design:** [BlockStateCodec_SDD.md](./BlockStateCodec_SDD.md)
