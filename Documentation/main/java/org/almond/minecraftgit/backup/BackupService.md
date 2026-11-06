# BackupService

> **Software Detailed Documentation:** [BackupService_SDD.md](./BackupService_SDD.md)  
> **Source File:** [BackupService.java](../../../../../../../src/main/java/org/almond/minecraftgit/backup/BackupService.java)

## Table of Contents

- [Usage](#usage)
- [Fields](#fields)
- [BackupService (Constructor)](#backupservice-constructor)
- [store](#store)
- [latest](#latest)
- [list](#list)
- [directory](#directory)
- [prune](#prune)
- [requireName](#requirename)
- [Backup](#backup)
- [See Also](#see-also)

## Usage

TODO: Add usage examples for `BackupService`.

Local pre-apply snapshots so a pull or checkout can be undone. Backups are
gzipped binary snapshots under the mod data folder and are pruned to the
configured maximum.

---

## Fields

- `SEQUENCE` (`AtomicLong`) — TODO: describe field
- `root` (`Path`) — TODO: describe field
- `maxBackups` (`int`) — TODO: describe field
- `store` ([`SnapshotStore`](../staging/SnapshotStore.md)) — TODO: describe field

---

## BackupService (Constructor)

### Signature
```java
public BackupService(Path root, int maxBackups)
```

### Description
TODO: Describe what `BackupService` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `root` | `Path` | TODO: describe |
| `maxBackups` | `int` | TODO: describe |

### Returns
`BackupService` instance.

---

## store

### Signature
```java
public Path store(String world, UUID owner, String selection, Snapshot snapshot)
```

### Description
TODO: Describe what `store` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `world` | `String` | TODO: describe |
| `owner` | `UUID` | TODO: describe |
| `selection` | `String` | TODO: describe |
| `snapshot` | [`Snapshot`](../model/Snapshot.md) | TODO: describe |

### Returns
`Path` — TODO: describe return value.

---

## latest

### Signature
```java
public Optional<Backup> latest(String world, UUID owner, String selection)
```

### Description
TODO: Describe what `latest` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `world` | `String` | TODO: describe |
| `owner` | `UUID` | TODO: describe |
| `selection` | `String` | TODO: describe |

### Returns
`Optional<Backup>` — TODO: describe return value.

---

## list

### Signature
```java
public List<Path> list(String world, UUID owner, String selection)
```

### Description
TODO: Describe what `list` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `world` | `String` | TODO: describe |
| `owner` | `UUID` | TODO: describe |
| `selection` | `String` | TODO: describe |

### Returns
`List<Path>` — TODO: describe return value.

---

## directory

### Signature
```java
public Path directory(String world, UUID owner, String selection)
```

### Description
TODO: Describe what `directory` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `world` | `String` | TODO: describe |
| `owner` | `UUID` | TODO: describe |
| `selection` | `String` | TODO: describe |

### Returns
`Path` — TODO: describe return value.

---

## prune

### Signature
```java
private void prune(Path directory)
```

### Description
TODO: Describe what `prune` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `directory` | `Path` | TODO: describe |

### Returns
`void`

---

## requireName

### Signature
```java
private static String requireName(String selection)
```

### Description
TODO: Describe what `requireName` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `selection` | `String` | TODO: describe |

### Returns
`String` — TODO: describe return value.

---

## Backup

### Signature
```java
public record Backup(Snapshot snapshot, Path file)
```

### Description
TODO: Describe what `Backup` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `snapshot` | [`Snapshot`](../model/Snapshot.md) | TODO: describe |
| `file` | `Path` | TODO: describe |

### Returns
`record` — TODO: describe return value.

---

## See Also

- **Software Detailed Design:** [BackupService_SDD.md](./BackupService_SDD.md)
