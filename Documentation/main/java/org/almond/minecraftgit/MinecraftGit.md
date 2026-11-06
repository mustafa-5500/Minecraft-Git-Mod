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

TODO: Add usage examples for `MinecraftGit`.

Shared identity for the client-side Minecraft Git mod.
<p>This class lives in {@code src/main} so the repository and serialization
layers can refer to the mod id without pulling in client-only classes.</p>

---

## Fields

- `MOD_ID` (`String`) — TODO: describe field
- `LOGGER` (`Logger`) — TODO: describe field

---

## MinecraftGit (Constructor)

### Signature
```java
private MinecraftGit()
```

### Description
TODO: Describe what `MinecraftGit` does.

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
Creates a {@link ResourceLocation} in this mod's namespace.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `path` | `String` | TODO: describe |

### Returns
`ResourceLocation` — TODO: describe return value.

---

## See Also

- **Software Detailed Design:** [MinecraftGit_SDD.md](./MinecraftGit_SDD.md)
