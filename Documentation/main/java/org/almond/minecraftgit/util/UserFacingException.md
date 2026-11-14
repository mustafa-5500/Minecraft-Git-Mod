# UserFacingException

> **Software Detailed Documentation:** [UserFacingException_SDD.md](./UserFacingException_SDD.md)  
> **Source File:** [UserFacingException.java](../../../../../../../src/main/java/org/almond/minecraftgit/util/UserFacingException.java)

## Table of Contents

- [Usage](#usage)
- [UserFacingException (Constructor)](#userfacingexception-constructor)
- [See Also](#see-also)

## Usage

Extend it, or throw it directly, for failures whose message is meaningful to the player; `CommandSupport.handleError` prints `getMessage()` for any `UserFacingException`, prints `IllegalArgumentException` messages too, and logs a stack trace only for unexpected types. For example, throwing `new UserFacingException(message)` produces a red chat error without a log entry.

Base type for failures that are safe and useful to show in chat.

---

## UserFacingException (Constructor)

### Signature
```java
public UserFacingException(String message)
```

### Description
Creates a user-facing failure with a chat-ready message and no underlying cause.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `message` | `String` | Text shown verbatim to the player. |

### Returns
`UserFacingException` instance.

---

## UserFacingException (Constructor)

### Signature
```java
public UserFacingException(String message, Throwable cause)
```

### Description
Creates a user-facing failure that keeps an underlying cause for diagnostics while still showing only the message in chat.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `message` | `String` | Text shown verbatim to the player. |
| `cause` | `Throwable` | Underlying failure retained for logs and debugging. |

### Returns
`UserFacingException` instance.

---

## See Also

- **Software Detailed Design:** [UserFacingException_SDD.md](./UserFacingException_SDD.md)
