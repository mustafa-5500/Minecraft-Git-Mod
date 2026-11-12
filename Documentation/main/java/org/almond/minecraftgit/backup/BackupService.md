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

`GitServices` builds one with `config.backupsRoot()` and `config.maxBackups`; `ApplyService` calls `store` before writing blocks and `/git pull --abort` calls `latest` to restore the newest backup. Example: `Path file = services.backups().store(world.id(), owner, name, preApplySnapshot);`

Local pre-apply snapshots so a pull or checkout can be undone. Backups are
gzipped binary snapshots under the mod data folder and are pruned to the
configured maximum.

---

## Fields

- `SEQUENCE` (`AtomicLong`) — Process-wide counter appended to the millisecond timestamp so backups created in the same millisecond keep unique, increasing names.
- `root` (`Path`) — Absolute normalized backup root (normally `config.backupsRoot()`); every lookup path resolves under it.
- `maxBackups` (`int`) — Maximum number of `.snap` files kept per selection directory, clamped to at least 1 in the constructor.
- `store` ([`SnapshotStore`](../staging/SnapshotStore.md)) — `SnapshotStore` used to gzip-write and read binary snapshots.

---

## BackupService (Constructor)

### Signature
```java
public BackupService(Path root, int maxBackups)
```

### Description
Creates a service rooted at the given directory and clamps `maxBackups` to at least one; no directories are created until `store` is called.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `root` | `Path` | Backup root; converted to an absolute normalized path. |
| `maxBackups` | `int` | Retention cap per selection; values below 1 are raised to 1. |

### Returns
`BackupService` instance.

---

## store

### Signature
```java
public Path store(String world, UUID owner, String selection, Snapshot snapshot)
```

### Description
Validates and creates the per-world/owner/selection directory, writes the snapshot to `<millis>-<sequence>.snap` through `SnapshotStore`, prunes older backups to `maxBackups`, and returns the new file path. IO failures become `BackupException`.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `world` | `String` | World id used (after sanitizing) as the first directory level. |
| `owner` | `UUID` | Player UUID used as the owner directory; `null` maps to `console`. |
| `selection` | `String` | Selection name, validated as a safe directory name. |
| `snapshot` | [`Snapshot`](../model/Snapshot.md) | Snapshot to persist. |

### Returns
`Path` — Path of the newly written `.snap` backup file.

---

## latest

### Signature
```java
public Optional<Backup> latest(String world, UUID owner, String selection)
```

### Description
Lists the selection's backups by name and reads the last (newest) through `SnapshotStore`, returning empty when none exist.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `world` | `String` | World id of the selection. |
| `owner` | `UUID` | Player UUID owning the selection. |
| `selection` | `String` | Selection name. |

### Returns
`Optional<Backup>` — The newest backup's snapshot and file path, or `Optional.empty()` when no backup exists.

---

## list

### Signature
```java
public List<Path> list(String world, UUID owner, String selection)
```

### Description
Returns the `.snap` files in the selection's backup directory sorted by file name; a missing directory yields an empty list and a directory IO failure throws `BackupException`.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `world` | `String` | World id of the selection. |
| `owner` | `UUID` | Player UUID owning the selection. |
| `selection` | `String` | Selection name. |

### Returns
`List<Path>` — Backup files ordered oldest to newest by file name (empty when none exist).

---

## directory

### Signature
```java
public Path directory(String world, UUID owner, String selection)
```

### Description
Computes the backup directory `<root>/<safe world>/<owner or console>/<selection>` without creating it; the selection name is validated first.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `world` | `String` | World id; sanitized with `Names.safeWorldName`. |
| `owner` | `UUID` | Player UUID, or `null` for a console-owned directory. |
| `selection` | `String` | Selection name. |

### Returns
`Path` — Absolute path of the selection's backup directory.

---

## prune

### Signature
```java
private void prune(Path directory)
```

### Description
Lists `.snap` files in the directory, sorts them by name and deletes the oldest (lowest names) until at most `maxBackups` remain; called after each successful store.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `directory` | `Path` | Selection backup directory to trim. |

### Returns
`void`

---

## requireName

### Signature
```java
private static String requireName(String selection)
```

### Description
Validates the selection name with `Names.requireSelectionName` and converts its `IllegalArgumentException` into a `BackupException` so callers only see backup-typed failures.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `selection` | `String` | Raw selection name from a command or metadata. |

### Returns
`String` — The validated selection name.

---

## Backup

### Signature
```java
public record Backup(Snapshot snapshot, Path file)
```

### Description
Immutable value returned by `latest`: the decoded pre-apply snapshot plus the file it came from.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `snapshot` | [`Snapshot`](../model/Snapshot.md) | Decoded snapshot stored in the backup. |
| `file` | `Path` | Path of the `.snap` file backing this record. |

### Returns
`record` — A `Backup` record carrying the decoded `Snapshot` and its backing `.snap` path.

---

## See Also

- **Software Detailed Design:** [BackupService_SDD.md](./BackupService_SDD.md)
