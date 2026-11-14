# StagingException

> **Software Detailed Documentation:** [StagingException_SDD.md](./StagingException_SDD.md)  
> **Source File:** [StagingException.java](../../../../../../../src/main/java/org/almond/minecraftgit/staging/StagingException.java)

## Table of Contents

- [Usage](#usage)
- [StagingException (Constructor)](#stagingexception-constructor)
- [See Also](#see-also)

## Usage

Throw it from staging code when the player can correct the problem, for example `throw new StagingException("Selection '" + name + "' has no origin; add a cuboid first.");`. The command layer catches [`UserFacingException`](../util/UserFacingException.md) and prints the message in chat instead of a crash report.

A staging operation failed in a way the user can fix.

---

## StagingException (Constructor)

### Signature
```java
public StagingException(String message)
```

### Description
Creates an exception carrying a user-facing explanation of the staging failure.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `message` | `String` | Message shown to the player; it should describe the cause and, where possible, the fix. |

### Returns
`StagingException` instance.

---

## StagingException (Constructor)

### Signature
```java
public StagingException(String message, Throwable cause)
```

### Description
Creates an exception with a user-facing explanation and the underlying cause for diagnostics.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `message` | `String` | Message shown to the player. |
| `cause` | `Throwable` | Underlying I/O or format failure, retained for logs rather than shown verbatim. |

### Returns
`StagingException` instance.

---

## See Also

- **Software Detailed Design:** [StagingException_SDD.md](./StagingException_SDD.md)
