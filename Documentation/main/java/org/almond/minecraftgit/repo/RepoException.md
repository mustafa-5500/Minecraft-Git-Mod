# RepoException

> **Software Detailed Documentation:** [RepoException_SDD.md](./RepoException_SDD.md)  
> **Source File:** [RepoException.java](../../../../../../../src/main/java/org/almond/minecraftgit/repo/RepoException.java)

## Table of Contents

- [Usage](#usage)
- [RepoException (Constructor)](#repoexception-constructor)
- [See Also](#see-also)

## Usage

Thrown by the repository layer when an operation fails in a way the player can fix — a missing repository, an invalid remote URL, corrupt metadata — with a message written for chat. `CommandSupport.handleError` recognises the `UserFacingException` base type and prints `getMessage()` directly, while the attached JGit cause is only used for debug logging.

```java
if (!repo.isInitialized()) {
    throw new RepoException("No Git repository exists at " + repo.path() + ". Run /git init first.");
}
```

A repository operation failed in a way the user can act on. Commands show
`.getMessage()` directly; the JGit cause is kept for debug logs.

---

## RepoException (Constructor)

### Signature
```java
public RepoException(String message)
```

### Description
Creates a user-facing repository failure with no underlying cause, for cases where the mod knows exactly what went wrong (e.g. "Remote URL must not be blank."). The message is displayed verbatim in chat, so it is written as a complete sentence.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `message` | `String` | Complete, player-facing description of the failure and, where possible, the action to take. |

### Returns
`RepoException` instance.

---

## RepoException (Constructor)

### Signature
```java
public RepoException(String message, Throwable cause)
```

### Description
Wraps a lower-level failure (typically `IOException` or a JGit `GitAPIException`) while keeping the friendly message on top. The cause is preserved for debug logs and stack traces but is not shown to the player.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `message` | `String` | Player-facing summary that hides JGit internals. |
| `cause` | `Throwable` | Original failure, retained for diagnostics. |

### Returns
`RepoException` instance.

---

## See Also

- **Software Detailed Design:** [RepoException_SDD.md](./RepoException_SDD.md)
