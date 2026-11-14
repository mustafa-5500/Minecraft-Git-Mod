# SerializationException

> **Software Detailed Documentation:** [SerializationException_SDD.md](./SerializationException_SDD.md)  
> **Source File:** [SerializationException.java](../../../../../../../src/main/java/org/almond/minecraftgit/serialization/SerializationException.java)

## Table of Contents

- [Usage](#usage)
- [SerializationException (Constructor)](#serializationexception-constructor)
- [See Also](#see-also)

## Usage

`SerializationException` is the single error type of the serialization package: readers and writers throw it for malformed CSV, mismatched origins, unknown quadrants and invalid NBT, and the command layer catches it to show the message to the player. For example, a selection whose files disagree on the origin raises `new SerializationException("Mismatched origin in " + file + ...)` from `QuadrantCsvReader`.

A repository-format error that should be shown to the user.

---

## SerializationException (Constructor)

### Signature
```java
public SerializationException(String message)
```

### Description
Creates a user-facing repository-format error with no underlying cause; the message is expected to name the offending file or operation.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `message` | `String` | Message shown to the user, for example `Unknown CSV quadrant 'xx'.` |

### Returns
`SerializationException` instance.

---

## SerializationException (Constructor)

### Signature
```java
public SerializationException(String message, Throwable cause)
```

### Description
Creates a repository-format error that keeps the lower-level cause — typically an `IOException`, `NumberFormatException` or Brigadier `CommandSyntaxException` — for logging while still exposing a readable message.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `message` | `String` | User-facing description of what failed. |
| `cause` | `Throwable` | Original exception, preserved for the stack trace. |

### Returns
`SerializationException` instance.

---

## See Also

- **Software Detailed Design:** [SerializationException_SDD.md](./SerializationException_SDD.md)
