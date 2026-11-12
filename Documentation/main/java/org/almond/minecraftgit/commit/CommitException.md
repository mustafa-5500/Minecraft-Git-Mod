# CommitException

> **Software Detailed Documentation:** [CommitException_SDD.md](./CommitException_SDD.md)  
> **Source File:** [CommitException.java](../../../../../../../src/main/java/org/almond/minecraftgit/commit/CommitException.java)

## Table of Contents

- [Usage](#usage)
- [CommitException (Constructor)](#commitexception-constructor)
- [See Also](#see-also)

## Usage

`CommitService` throws it for user-correctable commit failures such as an uninitialized repository, no staged selections, a selection that is no longer staged, or nothing to commit; the command layer catches the `UserFacingException` base type and prints the message in chat. Example: `throw new CommitException("Nothing is staged. Use /git add <selection> first.");`

A commit could not be created for a user-correctable reason.

---

## CommitException (Constructor)

### Signature
```java
public CommitException(String message)
```

### Description
Creates an exception with a chat-safe message that explains the commit failure; no cause is attached.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `message` | `String` | User-facing explanation of why the commit could not be created. |

### Returns
`CommitException` instance.

---

## CommitException (Constructor)

### Signature
```java
public CommitException(String message, Throwable cause)
```

### Description
Creates an exception with a chat-safe message and retains the underlying cause (for example an `IOException` from building the commit tree) for logging.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `message` | `String` | User-facing explanation of the failure. |
| `cause` | `Throwable` | Underlying throwable, preserved for diagnostics but not shown verbatim to the player. |

### Returns
`CommitException` instance.

---

## See Also

- **Software Detailed Design:** [CommitException_SDD.md](./CommitException_SDD.md)
