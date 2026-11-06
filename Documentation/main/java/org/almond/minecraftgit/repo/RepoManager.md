# RepoManager

> **Software Detailed Documentation:** [RepoManager_SDD.md](./RepoManager_SDD.md)  
> **Source File:** [RepoManager.java](../../../../../../../src/main/java/org/almond/minecraftgit/repo/RepoManager.java)

## Table of Contents

- [Usage](#usage)
- [Fields](#fields)
- [RepoManager (Constructor)](#repomanager-constructor)
- [locator](#locator)
- [repoPath](#repopath)
- [isInitialized](#isinitialized)
- [metadata](#metadata)
- [saveMetadata](#savemetadata)
- [service](#service)
- [init](#init)
- [setAuthor](#setauthor)
- [setLastSelection](#setlastselection)
- [lastSelection](#lastselection)
- [reload](#reload)
- [defaults](#defaults)
- [isDefaultIdentity](#isdefaultidentity)
- [close](#close)
- [See Also](#see-also)

## Usage

TODO: Add usage examples for `RepoManager`.

Owns the per-world-id {@link RepoService} instances and their local metadata.
A "world" here is the client's {@code <world>__<dimension>} key, not a
server world object.

---

## Fields

- `config` ([`ModConfig`](../config/ModConfig.md)) — TODO: describe field
- `debug` ([`DebugLogger`](../debug/DebugLogger.md)) — TODO: describe field
- `locator` ([`RepoLocator`](RepoLocator.md)) — TODO: describe field
- `metadataStore` ([`RepoMetadataStore`](RepoMetadataStore.md)) — TODO: describe field
- `services` (`Map<String, RepoService>`) — TODO: describe field
- `metadataCache` (`Map<String, RepoMetadata>`) — TODO: describe field

---

## RepoManager (Constructor)

### Signature
```java
public RepoManager(ModConfig config, DebugLogger debug)
```

### Description
TODO: Describe what `RepoManager` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `config` | [`ModConfig`](../config/ModConfig.md) | TODO: describe |
| `debug` | [`DebugLogger`](../debug/DebugLogger.md) | TODO: describe |

### Returns
`RepoManager` instance.

---

## locator

### Signature
```java
public RepoLocator locator()
```

### Description
TODO: Describe what `locator` does.

### Parameters
None.

### Returns
[`RepoLocator`](RepoLocator.md) — TODO: describe return value.

---

## repoPath

### Signature
```java
public Path repoPath(String worldId)
```

### Description
TODO: Describe what `repoPath` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | TODO: describe |

### Returns
`Path` — TODO: describe return value.

---

## isInitialized

### Signature
```java
public boolean isInitialized(String worldId)
```

### Description
TODO: Describe what `isInitialized` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | TODO: describe |

### Returns
`boolean` — TODO: describe return value.

---

## metadata

### Signature
```java
public RepoMetadata metadata(String worldId)
```

### Description
TODO: Describe what `metadata` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | TODO: describe |

### Returns
[`RepoMetadata`](RepoMetadata.md) — TODO: describe return value.

---

## saveMetadata

### Signature
```java
public void saveMetadata(RepoMetadata metadata)
```

### Description
TODO: Describe what `saveMetadata` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `metadata` | [`RepoMetadata`](RepoMetadata.md) | TODO: describe |

### Returns
`void`

---

## service

### Signature
```java
public RepoService service(String worldId)
```

### Description
TODO: Describe what `service` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | TODO: describe |

### Returns
[`RepoService`](RepoService.md) — TODO: describe return value.

---

## init

### Signature
```java
public RepoInfo init(String worldId, String playerName, String playerEmail)
```

### Description
Create or open the repository. When the repository still uses the default
identity, adopt the local player's name so commits are attributable without
an extra setup step.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | TODO: describe |
| `playerName` | `String` | TODO: describe |
| `playerEmail` | `String` | TODO: describe |

### Returns
[`RepoInfo`](RepoInfo.md) — TODO: describe return value.

---

## setAuthor

### Signature
```java
public void setAuthor(String worldId, String name, String email)
```

### Description
TODO: Describe what `setAuthor` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | TODO: describe |
| `name` | `String` | TODO: describe |
| `email` | `String` | TODO: describe |

### Returns
`void`

---

## setLastSelection

### Signature
```java
public void setLastSelection(String worldId, String selection)
```

### Description
TODO: Describe what `setLastSelection` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | TODO: describe |
| `selection` | `String` | TODO: describe |

### Returns
`void`

---

## lastSelection

### Signature
```java
public String lastSelection(String worldId)
```

### Description
TODO: Describe what `lastSelection` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | TODO: describe |

### Returns
`String` — TODO: describe return value.

---

## reload

### Signature
```java
public void reload()
```

### Description
TODO: Describe what `reload` does.

### Parameters
None.

### Returns
`void`

---

## defaults

### Signature
```java
private RepoOptions defaults()
```

### Description
TODO: Describe what `defaults` does.

### Parameters
None.

### Returns
[`RepoOptions`](RepoOptions.md) — TODO: describe return value.

---

## isDefaultIdentity

### Signature
```java
private static boolean isDefaultIdentity(String authorName)
```

### Description
TODO: Describe what `isDefaultIdentity` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `authorName` | `String` | TODO: describe |

### Returns
`boolean` — TODO: describe return value.

---

## close

### Signature
```java
@Override
public void close()
```

### Description
TODO: Describe what `close` does.

### Parameters
None.

### Returns
`void`

---

## See Also

- **Software Detailed Design:** [RepoManager_SDD.md](./RepoManager_SDD.md)
