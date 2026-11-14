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

`RepoManager` is the entry point to the repository layer for the currently open world: it resolves paths, caches one `RepoService` per world id, and persists local-only metadata. The client builds one instance in `GitServices` and exposes it as `services.repos()`, so commands never construct `RepoService` directly.

```java
RepoManager repos = new RepoManager(config, debug);
String worldId = WorldContext.current(client).id();
RepoInfo info = repos.init(worldId, player.getName().getString(), null);
repos.setLastSelection(worldId, "castle");
```

Owns the per-world-id `RepoService` instances and their local metadata.
A "world" here is the client's `<world>__<dimension>` key, not a
server world object.

---

## Fields

- `config` ([`ModConfig`](../config/ModConfig.md)) — Supplies `reposRoot()` and `metaRoot()` plus the default branch, default remote and committer identity used when a world has no metadata yet.
- `debug` ([`DebugLogger`](../debug/DebugLogger.md)) — Shared logger passed to the metadata store and to every `RepoService`; records repository open/close and metadata writes.
- `locator` ([`RepoLocator`](RepoLocator.md)) — Immutable path policy translating a world id into its repository directory and metadata file.
- `metadataStore` ([`RepoMetadataStore`](RepoMetadataStore.md)) — Reads and atomically writes the per-world JSON metadata files under the locator's metadata root.
- `services` (`Map<String, RepoService>`) — Concurrent cache of repository facades keyed by world id; entries are created lazily by `service()` and removed only by `close()`/`reload()`.
- `metadataCache` (`Map<String, RepoMetadata>`) — Concurrent cache of the loaded metadata objects keyed by world id, so all callers mutate the same instance between saves.

---

## RepoManager (Constructor)

### Signature
```java
public RepoManager(ModConfig config, DebugLogger debug)
```

### Description
Builds the `RepoLocator` from `config.reposRoot()` and `config.metaRoot()`, then a `RepoMetadataStore` over `locator.metadataRoot()`. No directory is created and no repository is opened until `init` or `service` is called.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `config` | [`ModConfig`](../config/ModConfig.md) | Mod configuration providing the repository/metadata roots and the fallback branch, remote and author identity. |
| `debug` | [`DebugLogger`](../debug/DebugLogger.md) | Debug logger shared with the metadata store and every repository service created later. |

### Returns
`RepoManager` instance.

---

## locator

### Signature
```java
public RepoLocator locator()
```

### Description
Returns the shared `RepoLocator`, giving callers the same path policy (normalized roots and world-id sanitization) without duplicating it. The manager uses it internally to build the metadata store and to answer `repoPath`; the instance is created once in the constructor and never replaced.

### Parameters
None.

### Returns
[`RepoLocator`](RepoLocator.md) — The locator created from the configured roots.

---

## repoPath

### Signature
```java
public Path repoPath(String worldId)
```

### Description
Delegates to `RepoLocator.repoPath`, returning `config/minecraftgit/repos/<sanitized world id>`. The path is computed only: it is absolute and normalized, but the directory may not exist and no IO happens.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | World key (`<world>__<dimension>`) whose repository directory should be resolved; characters outside letters, digits, `_`, `-` and `.` are replaced with `_`. |

### Returns
`Path` — Absolute path of the repository directory for the world, whether or not it exists.

---

## isInitialized

### Signature
```java
public boolean isInitialized(String worldId)
```

### Description
Reports whether a Git repository already exists for the world. If a `RepoService` is cached its own `isInitialized()` is used; otherwise the method checks that `repoPath(worldId)/.git` is a directory. Nothing is created and JGit is not opened.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | World key to test. |

### Returns
`boolean` — `true` when `repoPath(worldId)/.git` is an existing directory.

---

## metadata

### Signature
```java
public RepoMetadata metadata(String worldId)
```

### Description
Returns the cached metadata for the world, loading it from `RepoMetadataStore` on first access with defaults from `defaults()`; blanks are normalized during the load. `computeIfAbsent` makes the load happen once per world even if several command threads ask concurrently.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | World key whose local metadata should be returned. |

### Returns
[`RepoMetadata`](RepoMetadata.md) — The mutable, cached metadata instance for the world; never `null`, but corrupt JSON on disk raises `RepoException`.

---

## saveMetadata

### Signature
```java
public void saveMetadata(RepoMetadata metadata)
```

### Description
Persists `metadata` through `RepoMetadataStore.save` (atomic temp-file write) and then puts the same instance into `metadataCache`. Because callers mutate the cached object in place, the cache already holds the values being written; if the write fails with a `RepoException` the cache is left unchanged.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `metadata` | [`RepoMetadata`](RepoMetadata.md) | Metadata to serialize; its `world()` selects the JSON file name. |

### Returns
`void`

---

## service

### Signature
```java
public RepoService service(String worldId)
```

### Description
Returns the cached `RepoService` for the world, creating it on first use with `RepoOptions` derived from that world's metadata (branch, remote name, author name and email). Creation only builds the facade; JGit is opened lazily by the first operation or by `init`. The options are captured at creation time, so later metadata changes reach an existing service only through methods such as `setAuthor`.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | World key whose repository service should be returned. |

### Returns
[`RepoService`](RepoService.md) — The shared service for the world, located at `repoPath(worldId)`.

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

Concretely, when `playerName` is non-blank and the stored author is blank or `MinecraftGit`, the name is replaced by `playerName`, and a blank `playerEmail` becomes `<sanitized name>@minecraftgit.local`; the service is then opened/created, given that identity, and `RepoService.init()` is called. The resulting branch (falling back to the configured branch when blank), author and non-blank remote URL are written back to metadata before the `RepoInfo` is returned.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | World key whose repository should be created or opened. |
| `playerName` | `String` | Local player name adopted as author while the identity is still the default. |
| `playerEmail` | `String` | Optional author email; when `null`/blank one is derived from the name. |

### Returns
[`RepoInfo`](RepoInfo.md) — Snapshot of the repository after initialization: path, branch, HEAD id/summary, remote, author, and clean flags.

---

## setAuthor

### Signature
```java
public void setAuthor(String worldId, String name, String email)
```

### Description
Stores the author name/email in the world's metadata and saves it, then forwards the identity to the live `RepoService`: the cached one if present, otherwise a service is created only when the repository already exists. This also rewrites the repository's `user.name`/`user.email`; blank values are rejected by `RepoService.setAuthor` with a `RepoException`.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | World key whose author should change. |
| `name` | `String` | New Git author/committer name; must not be blank. |
| `email` | `String` | New Git author/committer email; must not be blank. |

### Returns
`void`

---

## setLastSelection

### Signature
```java
public void setLastSelection(String worldId, String selection)
```

### Description
Remembers the active selection name for the world in local metadata so the wand can restore it after a restart; `null` is stored as an empty string. The selection is not validated here — `SelectionManager` checks that it still exists before using it.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | World key whose remembered selection should change. |
| `selection` | `String` | Selection name to remember, or `null` to clear. |

### Returns
`void`

---

## lastSelection

### Signature
```java
public String lastSelection(String worldId)
```

### Description
Returns the remembered active selection name for the world by reading through `metadata(worldId)`, so the first call may load the JSON file. It does not check that the selection still exists.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `worldId` | `String` | World key whose remembered selection should be read. |

### Returns
`String` — The stored selection name, or `""` when none was ever saved.

---

## reload

### Signature
```java
public void reload()
```

### Description
Drops all runtime state: closes every cached `RepoService`, clears the service cache and clears the metadata cache (once via `close()` and once explicitly). The next command therefore re-reads metadata from disk and rebuilds services from current config values.

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
Adapts the mutable config fields to the immutable `RepoOptions` record used by the metadata store and services: `config.branch`, `config.defaultRemote`, `config.committerName` and `config.committerEmail`. `RepoOptions` itself substitutes its own fallbacks for blank values.

### Parameters
None.

### Returns
[`RepoOptions`](RepoOptions.md) — Default options for a world without usable metadata.

---

## isDefaultIdentity

### Signature
```java
private static boolean isDefaultIdentity(String authorName)
```

### Description
Decides whether `init` should adopt the local player's identity. An author name that is `null`, blank or exactly the shipped default `MinecraftGit` counts as unset.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `authorName` | `String` | Author name loaded from metadata, possibly `null` for legacy files. |

### Returns
`boolean` — `true` when the identity is unset or still the default.

---

## close

### Signature
```java
@Override
public void close()
```

### Description
Closes every cached `RepoService`, releasing JGit's repository handles, and clears both the service and metadata caches. Calling it twice is harmless: the map iterations simply find nothing.

### Parameters
None.

### Returns
`void`

---

## See Also

- **Software Detailed Design:** [RepoManager_SDD.md](./RepoManager_SDD.md)
