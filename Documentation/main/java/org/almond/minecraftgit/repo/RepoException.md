# RepoException

> **Software Detailed Documentation:** [RepoException_SDD.md](./RepoException_SDD.md)  
> **Source File:** [RepoException.java](../../../../../../../src/main/java/org/almond/minecraftgit/repo/RepoException.java)

## Table of Contents

- [Usage](#usage)
- [RepoException (Constructor)](#repoexception-constructor)
- [See Also](#see-also)

## Usage

TODO: Add usage examples for `RepoException`.

A repository operation failed in a way the user can act on. Commands show
{@link #getMessage()} directly; the JGit cause is kept for debug logs.

---

## RepoException (Constructor)

### Signature
```java
public RepoException(String message)
```

### Description
TODO: Describe what `RepoException` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `message` | `String` | TODO: describe |

### Returns
`RepoException` instance.

---

## RepoException (Constructor)

### Signature
```java
public RepoException(String message, Throwable cause)
```

### Description
TODO: Describe what `RepoException` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `message` | `String` | TODO: describe |
| `cause` | `Throwable` | TODO: describe |

### Returns
`RepoException` instance.

---

## See Also

- **Software Detailed Design:** [RepoException_SDD.md](./RepoException_SDD.md)
