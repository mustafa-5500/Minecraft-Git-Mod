# BackupException

> **Software Detailed Documentation:** [BackupException_SDD.md](./BackupException_SDD.md)  
> **Source File:** [BackupException.java](../../../../../../../src/main/java/org/almond/minecraftgit/backup/BackupException.java)

## Table of Contents

- [Usage](#usage)
- [BackupException (Constructor)](#backupexception-constructor)
- [See Also](#see-also)

## Usage

`BackupService` throws it when a pre-apply backup cannot be written or listed, or when a selection name is invalid; the pull/apply path surfaces the message during `/git pull --abort`. Example: `throw new BackupException("Could not create a pre-apply backup: " + e.getMessage(), e);`

A backup could not be created or read.

---

## BackupException (Constructor)

### Signature
```java
public BackupException(String message)
```

### Description
Creates an exception with a chat-safe message describing the backup failure; no cause is attached.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `message` | `String` | User-facing explanation of the backup failure. |

### Returns
`BackupException` instance.

---

## BackupException (Constructor)

### Signature
```java
public BackupException(String message, Throwable cause)
```

### Description
Creates an exception with a chat-safe message and retains the underlying `IOException` or name-validation cause.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `message` | `String` | User-facing explanation of the failure. |
| `cause` | `Throwable` | Underlying throwable retained for diagnostics. |

### Returns
`BackupException` instance.

---

## See Also

- **Software Detailed Design:** [BackupException_SDD.md](./BackupException_SDD.md)
