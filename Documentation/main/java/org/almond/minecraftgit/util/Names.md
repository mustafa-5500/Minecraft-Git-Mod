# Names

> **Software Detailed Documentation:** [Names_SDD.md](./Names_SDD.md)  
> **Source File:** [Names.java](../../../../../../../src/main/java/org/almond/minecraftgit/util/Names.java)

## Table of Contents

- [Usage](#usage)
- [Fields](#fields)
- [Names (Constructor)](#names-constructor)
- [isValidSelectionName](#isvalidselectionname)
- [requireSelectionName](#requireselectionname)
- [safeWorldName](#safeworldname)
- [lower](#lower)
- [shorten](#shorten)
- [See Also](#see-also)

## Usage

Called statically before creating or staging a selection (`requireSelectionName`) and when mapping world ids to directories (`safeWorldName`); for example, `Names.safeWorldName(worldId)` maps spaces and unsafe characters to underscores, while `Names.requireSelectionName(name)` returns a valid name unchanged.

Selection names become directory names inside a Git repository, so they are
restricted instead of being sanitized after the fact. World/player ids are
sanitized because the player never types them.

---

## Fields

- `SELECTION` (`Pattern`) — Compiled pattern `[A-Za-z0-9][A-Za-z0-9_-]{0,31}` enforcing 1-32 characters, an alphanumeric first character, then letters, digits, underscore or hyphen.
- `WORLD` (`Pattern`) — Compiled pattern `[^A-Za-z0-9_.-]` matching every character that must be replaced to make a world id filesystem-safe.

---

## Names (Constructor)

### Signature
```java
private Names()
```

### Description
Private no-op constructor; the class is a static utility holder and is not instantiable.

### Parameters
None.

### Returns
`Names` instance.

---

## isValidSelectionName

### Signature
```java
public static boolean isValidSelectionName(String name)
```

### Description
Returns true only for non-null names of 1-32 characters that start with a letter or digit and continue with letters, digits, `_` or `-`, so a validated name is always safe as a repository path component.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `name` | `String` | Candidate name; null is rejected. |

### Returns
`boolean` — true when the name fully matches the `SELECTION` pattern.

---

## requireSelectionName

### Signature
```java
public static String requireSelectionName(String name)
```

### Description
Returns the name unchanged when valid, otherwise throws `IllegalArgumentException` with the exact rule text; callers such as `SelectionService` translate that into a user-facing exception.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `name` | `String` | Name to validate and return. |

### Returns
`String` — the same, valid name.

---

## safeWorldName

### Signature
```java
public static String safeWorldName(String world)
```

### Description
Replaces every character outside `[A-Za-z0-9_.-]` with `_` and maps null to the `world` sentinel, so arbitrary world ids or dimension strings can be used as directory and file names.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `world` | `String` | World id to sanitize; null becomes the `world` sentinel. |

### Returns
`String` — sanitized world name.

---

## lower

### Signature
```java
public static String lower(String value)
```

### Description
Null-safe lower-casing with `Locale.ROOT`, giving deterministic comparisons independent of the player's default locale.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `value` | `String` | String to lower-case; null becomes empty. |

### Returns
`String` — lower-cased value, or an empty string for null.

---

## shorten

### Signature
```java
public static String shorten(String value, int max)
```

### Description
Returns the value unchanged when it fits in `max` characters, otherwise truncates it at `max` and appends `...`; null becomes an empty string. Intended for chat and table output.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `value` | `String` | Text to abbreviate. |
| `max` | `int` | Maximum length before the ellipsis is appended. |

### Returns
`String` — possibly truncated text ending in `...`.

---

## See Also

- **Software Detailed Design:** [Names_SDD.md](./Names_SDD.md)
