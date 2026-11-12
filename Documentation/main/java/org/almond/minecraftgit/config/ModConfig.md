# ModConfig

> **Software Detailed Documentation:** [ModConfig_SDD.md](./ModConfig_SDD.md)  
> **Source File:** [ModConfig.java](../../../../../../../src/main/java/org/almond/minecraftgit/config/ModConfig.java)

## Table of Contents

- [Usage](#usage)
- [Fields](#fields)
- [ModConfig (Constructor)](#modconfig-constructor)
- [load](#load)
- [root](#root)
- [file](#file)
- [reposRoot](#reposroot)
- [metaRoot](#metaroot)
- [selectionsRoot](#selectionsroot)
- [stagingRoot](#stagingroot)
- [backupsRoot](#backupsroot)
- [tmpRoot](#tmproot)
- [logsRoot](#logsroot)
- [normalize](#normalize)
- [save](#save)
- [nonBlank](#nonblank)
- [positive](#positive)
- [See Also](#see-also)

## Usage

Load it once during client initialization from the Fabric config directory and share the instance through `GitServices`; for example `ModConfig config = ModConfig.load(FabricLoader.getInstance().getConfigDir().resolve("minecraftgit"));`. Mutating a field takes effect immediately; call `save()` to persist it, as `/git config debug` does.

Typed view over `config/minecraftgit/config.json`.
The fields are the persisted schema. Defaults are deliberately conservative
for client memory/tick budgets; the plugin's much larger caps assume a server
thread that can spend seconds on a capture.

---

## Fields

- `debug` (`boolean`) — Enables debug logging (`false` by default); toggled by `/git config debug` and passed to `DebugLogger`.
- `debugBlocks` (`boolean`) — Enables per-block debug logging (`false`); independent of `debug` but only useful when debug logging is also on.
- `branch` (`String`) — Initial/local branch name (`main`); blank values normalize to `main`.
- `defaultRemote` (`String`) — Default Git remote name (`origin`); blank values normalize to `origin`.
- `wandItem` (`String`) — Registry id of the selection wand item (`minecraft:wooden_shovel`).
- `prefix` (`String`) — Chat message prefix (`[Git] `); null normalizes to the empty string.
- `committerName` (`String`) — Git author/committer name (`MinecraftGit`); blank values fall back to it.
- `committerEmail` (`String`) — Git author/committer email (`minecraftgit@localhost`).
- `maxCuboidsPerSelection` (`int`) — Maximum cuboids per selection (`64`), in cuboids; must be positive.
- `maxVolumePerSelection` (`long`) — Maximum blocks per selection (`1_000_000`), in blocks; must be positive.
- `maxBlocksPerCommit` (`long`) — Maximum blocks captured for one commit (`2_000_000`), in blocks.
- `maxEntitiesPerSelection` (`int`) — Maximum entities captured per selection (`2000`); extra entities are dropped with a warning.
- `maxBlockEntityBytes` (`int`) — Maximum serialized block-entity payload (`262_144`), in bytes.
- `statusMaxVolume` (`long`) — Selections above this volume (`250_000` blocks) are excluded from `/git status` capture and reported as too large.
- `blocksPerTick` (`int`) — Apply write budget (`4096` block writes per server tick).
- `maxBackups` (`int`) — Pre-apply backup retention per selection (`10`); `BackupService` clamps it to at least 1.
- `renderSelections` (`boolean`) — Whether selection outlines are rendered (`true`).
- `renderBudget` (`int`) — Cap on selection-outline particle draws per render pass (`256`).
- `root` (`transient Path`) — Transient absolute data root; not serialized, injected by `load`.
- `gson` (`transient Gson`) — Transient pretty-printing Gson instance; not serialized, injected by `load`.

---

## ModConfig (Constructor)

### Signature
```java
private ModConfig()
```

### Description
Private no-arg constructor used by Gson and by `load`; field initializers supply the defaults and `root`/`gson` stay null until `load` injects them.

### Parameters
None.

### Returns
`ModConfig` instance.

---

## load

### Signature
```java
public static ModConfig load(Path root)
```

### Description
Load the config, creating it with defaults when it does not exist.
A corrupt file is reported once and defaults are used; the old file is
left untouched so a player can fix it.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `root` | `Path` | Config/data root directory (normally `<game config>/minecraftgit`); `config.json` is resolved under it. |

### Returns
`ModConfig` — A normalized config whose transient `root` and `gson` are set; when `config.json` is missing, defaults are normalized and written, while invalid or unreadable files fall back to in-memory defaults and are left untouched.

---

## root

### Signature
```java
public Path root()
```

### Description
Returns the absolute normalized config root injected by `load`.

### Parameters
None.

### Returns
`Path` — The absolute normalized config/data root directory.

---

## file

### Signature
```java
public Path file()
```

### Description
Resolves the absolute `config.json` path under the loaded root.

### Parameters
None.

### Returns
`Path` — `<root>/config.json`, the file read by `load` and written by `save`.

---

## reposRoot

### Signature
```java
public Path reposRoot()
```

### Description
Resolves `<root>/repos`, the parent directory of per-world Git repositories.

### Parameters
None.

### Returns
`Path` — `<root>/repos`.

---

## metaRoot

### Signature
```java
public Path metaRoot()
```

### Description
Resolves `<root>/meta`, local per-world repository metadata.

### Parameters
None.

### Returns
`Path` — `<root>/meta`.

---

## selectionsRoot

### Signature
```java
public Path selectionsRoot()
```

### Description
Resolves `<root>/selections`, per-world/player selection metadata.

### Parameters
None.

### Returns
`Path` — `<root>/selections`.

---

## stagingRoot

### Signature
```java
public Path stagingRoot()
```

### Description
Resolves `<root>/staging`, the gzipped staged-snapshot root.

### Parameters
None.

### Returns
`Path` — `<root>/staging`.

---

## backupsRoot

### Signature
```java
public Path backupsRoot()
```

### Description
Resolves `<root>/backups`, the pre-apply backup root.

### Parameters
None.

### Returns
`Path` — `<root>/backups`.

---

## tmpRoot

### Signature
```java
public Path tmpRoot()
```

### Description
Resolves `<root>/tmp`, used for transient commit and pull trees.

### Parameters
None.

### Returns
`Path` — `<root>/tmp`.

---

## logsRoot

### Signature
```java
public Path logsRoot()
```

### Description
Resolves `<root>/logs`, the optional debug-log root.

### Parameters
None.

### Returns
`Path` — `<root>/logs`.

---

## normalize

### Signature
```java
public void normalize()
```

### Description
Fill in blanks and reject non-positive sizes.

### Parameters
None.

### Returns
`void`

---

## save

### Signature
```java
public void save()
```

### Description
Serializes the public fields as pretty-printed JSON to `config.json` through a sibling `.tmp` file, then replaces the target with an atomic move (falling back to a plain replace when unsupported). It is `synchronized`, no-ops while `root` or `gson` is unset, and logs rather than throws IO failures.

### Parameters
None.

### Returns
`void`

---

## nonBlank

### Signature
```java
private static String nonBlank(String value, String fallback)
```

### Description
Returns `fallback` when `value` is null or blank, otherwise the original value; used by `normalize` for required strings.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `value` | `String` | Candidate value from the loaded JSON. |
| `fallback` | `String` | Default to use when the candidate is null or blank. |

### Returns
`String` — The original value when non-blank, otherwise `fallback`.

---

## positive

### Signature
```java
private static int positive(int value, int fallback)
```

### Description
Returns `value` when greater than zero, otherwise `fallback`; used by `normalize` to reject non-positive int limits.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `value` | `int` | Candidate limit from the loaded JSON. |
| `fallback` | `int` | Default to use when the candidate is non-positive. |

### Returns
`int` — The original value when positive, otherwise `fallback`.

---

## positive

### Signature
```java
private static long positive(long value, long fallback)
```

### Description
Long overload of the same guard, used by `normalize` for volume and block limits.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `value` | `long` | Candidate limit from the loaded JSON. |
| `fallback` | `long` | Default to use when the candidate is non-positive. |

### Returns
`long` — The original value when greater than zero, otherwise `fallback`.

---

## See Also

- **Software Detailed Design:** [ModConfig_SDD.md](./ModConfig_SDD.md)
