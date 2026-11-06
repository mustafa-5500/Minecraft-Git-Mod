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

TODO: Add usage examples for `ModConfig`.

Typed view over {@code config/minecraftgit/config.json}.
<p>The fields are the persisted schema. Defaults are deliberately conservative
for client memory/tick budgets; the plugin's much larger caps assume a server
thread that can spend seconds on a capture.</p>

---

## Fields

- `debug` (`boolean`) — TODO: describe field
- `debugBlocks` (`boolean`) — TODO: describe field
- `branch` (`String`) — TODO: describe field
- `defaultRemote` (`String`) — TODO: describe field
- `wandItem` (`String`) — TODO: describe field
- `prefix` (`String`) — TODO: describe field
- `committerName` (`String`) — TODO: describe field
- `committerEmail` (`String`) — TODO: describe field
- `maxCuboidsPerSelection` (`int`) — TODO: describe field
- `maxVolumePerSelection` (`long`) — TODO: describe field
- `maxBlocksPerCommit` (`long`) — TODO: describe field
- `maxEntitiesPerSelection` (`int`) — TODO: describe field
- `maxBlockEntityBytes` (`int`) — TODO: describe field
- `statusMaxVolume` (`long`) — TODO: describe field
- `blocksPerTick` (`int`) — TODO: describe field
- `maxBackups` (`int`) — TODO: describe field
- `renderSelections` (`boolean`) — TODO: describe field
- `renderBudget` (`int`) — TODO: describe field
- `root` (`transient Path`) — TODO: describe field
- `gson` (`transient Gson`) — TODO: describe field

---

## ModConfig (Constructor)

### Signature
```java
private ModConfig()
```

### Description
TODO: Describe what `ModConfig` does.

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
| `root` | `Path` | TODO: describe |

### Returns
`ModConfig` — TODO: describe return value.

---

## root

### Signature
```java
public Path root()
```

### Description
TODO: Describe what `root` does.

### Parameters
None.

### Returns
`Path` — TODO: describe return value.

---

## file

### Signature
```java
public Path file()
```

### Description
TODO: Describe what `file` does.

### Parameters
None.

### Returns
`Path` — TODO: describe return value.

---

## reposRoot

### Signature
```java
public Path reposRoot()
```

### Description
TODO: Describe what `reposRoot` does.

### Parameters
None.

### Returns
`Path` — TODO: describe return value.

---

## metaRoot

### Signature
```java
public Path metaRoot()
```

### Description
TODO: Describe what `metaRoot` does.

### Parameters
None.

### Returns
`Path` — TODO: describe return value.

---

## selectionsRoot

### Signature
```java
public Path selectionsRoot()
```

### Description
TODO: Describe what `selectionsRoot` does.

### Parameters
None.

### Returns
`Path` — TODO: describe return value.

---

## stagingRoot

### Signature
```java
public Path stagingRoot()
```

### Description
TODO: Describe what `stagingRoot` does.

### Parameters
None.

### Returns
`Path` — TODO: describe return value.

---

## backupsRoot

### Signature
```java
public Path backupsRoot()
```

### Description
TODO: Describe what `backupsRoot` does.

### Parameters
None.

### Returns
`Path` — TODO: describe return value.

---

## tmpRoot

### Signature
```java
public Path tmpRoot()
```

### Description
TODO: Describe what `tmpRoot` does.

### Parameters
None.

### Returns
`Path` — TODO: describe return value.

---

## logsRoot

### Signature
```java
public Path logsRoot()
```

### Description
TODO: Describe what `logsRoot` does.

### Parameters
None.

### Returns
`Path` — TODO: describe return value.

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
TODO: Describe what `save` does.

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
TODO: Describe what `nonBlank` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `value` | `String` | TODO: describe |
| `fallback` | `String` | TODO: describe |

### Returns
`String` — TODO: describe return value.

---

## positive

### Signature
```java
private static int positive(int value, int fallback)
```

### Description
TODO: Describe what `positive` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `value` | `int` | TODO: describe |
| `fallback` | `int` | TODO: describe |

### Returns
`int` — TODO: describe return value.

---

## positive

### Signature
```java
private static long positive(long value, long fallback)
```

### Description
TODO: Describe what `positive` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `value` | `long` | TODO: describe |
| `fallback` | `long` | TODO: describe |

### Returns
`long` — TODO: describe return value.

---

## See Also

- **Software Detailed Design:** [ModConfig_SDD.md](./ModConfig_SDD.md)
