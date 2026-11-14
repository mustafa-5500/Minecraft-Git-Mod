# MinecraftGit

> **Software Detailed Documentation:** [MinecraftGit_SDD.md](./MinecraftGit_SDD.md)  
> **Source File:** [MinecraftGit.java](../../../../../../src/main/java/org/almond/minecraftgit/MinecraftGit.java)

## Table of Contents

- [Usage](#usage)
- [Fields](#fields)
- [MinecraftGit (Constructor)](#minecraftgit-constructor)
- [id](#id)
- [See Also](#see-also)

## Usage

Use the constants and `id()` statically wherever the mod id or a logger is needed without importing client classes; the client entrypoint also derives its data directory from `MinecraftGit.MOD_ID`. For example, `MinecraftGit.id(path)` builds the namespaced id used by client resources, while `MinecraftGit.LOGGER.warn(...)` reports non-fatal problems.

Shared identity for the client-side Minecraft Git mod.
This class lives in `src/main` so the repository and serialization
layers can refer to the mod id without pulling in client-only classes.

---

## Fields

- `MOD_ID` (`String`) — The Fabric mod id `minecraftgit`, used as the namespace for `id()` and as the config-directory name.
- `LOGGER` (`Logger`) — Shared SLF4J logger named after `MOD_ID`, used by both source sets instead of per-class loggers.

---

## MinecraftGit (Constructor)

### Signature
```java
private MinecraftGit()
```

### Description
Private constructor; the class exposes only static constants and helpers and is never instantiated.

### Parameters
None.

### Returns
`MinecraftGit` instance.

---

## id

### Signature
```java
public static ResourceLocation id(String path)
```

### Description
Creates a `ResourceLocation` in this mod's namespace.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `path` | `String` | Path within the mod namespace, for example `textures/gui/icon.png`. |

### Returns
`ResourceLocation` — the `minecraftgit`-namespaced identifier for the path.

---

## See Also

- **Software Detailed Design:** [MinecraftGit_SDD.md](./MinecraftGit_SDD.md)
