# PullException

> **Software Detailed Documentation:** [PullException_SDD.md](./PullException_SDD.md)  
> **Source File:** [PullException.java](../../../../../../../src/main/java/org/almond/minecraftgit/sync/PullException.java)

## Table of Contents

- [Usage](#usage)
- [PullException (Constructor)](#pullexception-constructor)
- [See Also](#see-also)

## Usage

Throw it during pull or checkout preparation when the player can fix the problem, for example `throw new PullException("Selection '" + name + "' is not in the remote commit.");`. Command handlers catch [`UserFacingException`](../util/UserFacingException.md) and show the message in chat.

A pull/checkout preparation failed in a user-correctable way.

---

## PullException (Constructor)

### Signature
```java
public PullException(String message)
```

### Description
Creates an exception carrying a user-facing explanation of the pull/checkout failure.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `message` | `String` | Message shown to the player; it should describe the cause and, where possible, the fix. |

### Returns
`PullException` instance.

---

## PullException (Constructor)

### Signature
```java
public PullException(String message, Throwable cause)
```

### Description
Creates an exception with a user-facing explanation and the underlying cause for diagnostics.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `message` | `String` | Message shown to the player. |
| `cause` | `Throwable` | Underlying I/O or export failure, retained for logs rather than shown verbatim. |

### Returns
`PullException` instance.

---

## See Also

- **Software Detailed Design:** [PullException_SDD.md](./PullException_SDD.md)
