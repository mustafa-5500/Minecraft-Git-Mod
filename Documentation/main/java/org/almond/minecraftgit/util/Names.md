# Names

> **Software Detailed Documentation:** [Names_SDD.md](./Names_SDD.md)  
> **Source File:** [Names.java](../../../../../../../src/main/java/org/almond/minecraftgit/util/Names.java)

## Table of Contents

- [Usage](#usage)
- [Fields](#fields)
- [Names (Constructor)](#names-constructor)
- [isValidSelectionName](#isvalidselectionname)
- [requireSelectionName](#requireselectionname)
- [safeWorldName](#safeworldname)
- [lower](#lower)
- [shorten](#shorten)
- [See Also](#see-also)

## Usage

TODO: Add usage examples for `Names`.

Selection names become directory names inside a Git repository, so they are
restricted instead of being sanitized after the fact. World/player ids are
sanitized because the player never types them.

---

## Fields

- `SELECTION` (`Pattern`) — TODO: describe field
- `WORLD` (`Pattern`) — TODO: describe field

---

## Names (Constructor)

### Signature
```java
private Names()
```

### Description
TODO: Describe what `Names` does.

### Parameters
None.

### Returns
`Names` instance.

---

## isValidSelectionName

### Signature
```java
public static boolean isValidSelectionName(String name)
```

### Description
TODO: Describe what `isValidSelectionName` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `name` | `String` | TODO: describe |

### Returns
`boolean` — TODO: describe return value.

---

## requireSelectionName

### Signature
```java
public static String requireSelectionName(String name)
```

### Description
TODO: Describe what `requireSelectionName` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `name` | `String` | TODO: describe |

### Returns
`String` — TODO: describe return value.

---

## safeWorldName

### Signature
```java
public static String safeWorldName(String world)
```

### Description
TODO: Describe what `safeWorldName` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `world` | `String` | TODO: describe |

### Returns
`String` — TODO: describe return value.

---

## lower

### Signature
```java
public static String lower(String value)
```

### Description
TODO: Describe what `lower` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `value` | `String` | TODO: describe |

### Returns
`String` — TODO: describe return value.

---

## shorten

### Signature
```java
public static String shorten(String value, int max)
```

### Description
TODO: Describe what `shorten` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `value` | `String` | TODO: describe |
| `max` | `int` | TODO: describe |

### Returns
`String` — TODO: describe return value.

---

## See Also

- **Software Detailed Design:** [Names_SDD.md](./Names_SDD.md)
