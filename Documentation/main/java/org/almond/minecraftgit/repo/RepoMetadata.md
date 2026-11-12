# RepoMetadata

> **Software Detailed Documentation:** [RepoMetadata_SDD.md](./RepoMetadata_SDD.md)  
> **Source File:** [RepoMetadata.java](../../../../../../../src/main/java/org/almond/minecraftgit/repo/RepoMetadata.java)

## Table of Contents

- [Usage](#usage)
- [Fields](#fields)
- [defaults](#defaults)
- [normalize](#normalize)
- [world](#world)
- [setWorld](#setworld)
- [branch](#branch)
- [setBranch](#setbranch)
- [remoteName](#remotename)
- [setRemoteName](#setremotename)
- [remoteUrl](#remoteurl)
- [setRemoteUrl](#setremoteurl)
- [authorName](#authorname)
- [setAuthorName](#setauthorname)
- [authorEmail](#authoremail)
- [setAuthorEmail](#setauthoremail)
- [lastSelection](#lastselection)
- [setLastSelection](#setlastselection)
- [createdAt](#createdat)
- [See Also](#see-also)

## Usage

A mutable, local-only record of per-world repository settings: which branch and remote the world uses, the author identity, and the last active selection. It is created by `RepoMetadata.defaults` or loaded by `RepoMetadataStore`, cached by `RepoManager`, mutated through setters, and persisted with `RepoManager.saveMetadata`.

```java
RepoMetadata metadata = services.repos().metadata(worldId);
metadata.setRemoteUrl("https://example.com/build.git");
metadata.setAuthorName("Alex");
services.repos().saveMetadata(metadata);
```

Per-world, local-only repository state. This file is never committed: it maps
mod concepts (world id, default remote, author) onto Git concepts and may
differ between machines that share the same repository.

---

## Fields

- `world` (`String`) — World key (`<world>__<dimension>`) this metadata belongs to; also determines the JSON file name used by the store.
- `branch` (`String`) — Local branch created by `init` and compared during pull; defaults to `main`.
- `remoteName` (`String`) — Name of the remote used when a command does not specify one; defaults to `origin`.
- `remoteUrl` (`String`) — Last known URL for that remote, or `""` when no remote has been configured yet.
- `authorName` (`String`) — Git author/committer name for this world, initially `MinecraftGit` and replaced by the player name on first init.
- `authorEmail` (`String`) — Git author/committer email, initially `minecraftgit@localhost`.
- `lastSelection` (`String`) — Name of the active selection remembered for the wand, or `""` when none.
- `createdAt` (`String`) — ISO-8601 instant string captured when the defaults were built; informational only and never updated afterwards.

---

## defaults

### Signature
```java
public static RepoMetadata defaults(String world, RepoOptions options)
```

### Description
Builds a fresh metadata object for a world that has no usable JSON file yet. It copies `branch`, `defaultRemote`, `authorName` and `authorEmail` from `options`, leaves `remoteUrl` and `lastSelection` empty, and stamps `createdAt` with `Instant.now()`.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `world` | `String` | World key assigned to the new metadata. |
| `options` | [`RepoOptions`](RepoOptions.md) | Default branch, remote name and author identity, already canonicalized against blanks. |

### Returns
`RepoMetadata` — A new instance with copied defaults and the current timestamp.

---

## normalize

### Signature
```java
public RepoMetadata normalize(String world, RepoOptions options)
```

### Description
Fill in blanks introduced by older or hand-edited metadata files.

Each field is checked independently: `world`, `branch`, `remoteName`, `authorName`, `authorEmail` and `createdAt` fall back to `defaults(world, options)`, while `remoteUrl` and `lastSelection` are only converted from `null` to `""` because no sensible default exists. Existing non-blank values are preserved, including a deliberately custom author, and the receiver is returned for chaining.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `world` | `String` | World key to substitute when the stored `world` is missing. |
| `options` | [`RepoOptions`](RepoOptions.md) | Source of fallback branch, remote name and author identity. |

### Returns
`RepoMetadata` — The same instance after in-place repair.

---

## world

### Signature
```java
public String world()
```

### Description
Returns the world key this metadata belongs to. After `defaults` or `normalize` it is never blank.

### Parameters
None.

### Returns
`String` — The stored world key.

---

## setWorld

### Signature
```java
public void setWorld(String world)
```

### Description
Overrides the world key. Note that `RepoMetadataStore.save` uses `metadata.world()` to choose the file, so changing it redirects subsequent saves to another world's file.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `world` | `String` | New world key; not validated or sanitized here. |

### Returns
`void`

---

## branch

### Signature
```java
public String branch()
```

### Description
Returns the configured local branch name. `RepoOptions` has already replaced blanks, so this is normally non-blank.

### Parameters
None.

### Returns
`String` — The branch used when creating or pulling the repository.

---

## setBranch

### Signature
```java
public void setBranch(String branch)
```

### Description
Updates the stored branch name used when the world's `RepoService` is created and when `init` reports the branch. Existing services keep their original options.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `branch` | `String` | New branch name; not validated. |

### Returns
`void`

---

## remoteName

### Signature
```java
public String remoteName()
```

### Description
Returns the remote name used by push/fetch/pull when the command does not name one explicitly.

### Parameters
None.

### Returns
`String` — The stored remote name, normally `origin`.

---

## setRemoteName

### Signature
```java
public void setRemoteName(String remoteName)
```

### Description
Updates the default remote name used by later commands and by newly created services. It does not create or rename the remote inside Git.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `remoteName` | `String` | New default remote name; not validated against existing remotes. |

### Returns
`void`

---

## remoteUrl

### Signature
```java
public String remoteUrl()
```

### Description
Returns the last URL recorded for the default remote, or `""` when the world has never configured one.

### Parameters
None.

### Returns
`String` — The stored remote URL, possibly empty.

---

## setRemoteUrl

### Signature
```java
public void setRemoteUrl(String remoteUrl)
```

### Description
Records the URL reported by `RepoInfo` after init so it can be shown or reused. It is informational: adding the actual Git remote remains `RepoService.remoteAdd`.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `remoteUrl` | `String` | Remote URL to remember; `null` is repaired to `""` only on the next normalize. |

### Returns
`void`

---

## authorName

### Signature
```java
public String authorName()
```

### Description
Returns the Git author/committer name stored for this world. It is used to build `RepoOptions` and to seed `RepoService`'s identity.

### Parameters
None.

### Returns
`String` — The stored author name.

---

## setAuthorName

### Signature
```java
public void setAuthorName(String authorName)
```

### Description
Updates the author name. `RepoManager.setAuthor` saves metadata and propagates the change to the live service, which also rewrites the repository's Git config.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `authorName` | `String` | New author name; blank values are rejected later by `RepoService.setAuthor`. |

### Returns
`void`

---

## authorEmail

### Signature
```java
public String authorEmail()
```

### Description
Returns the Git author/committer email stored for this world.

### Parameters
None.

### Returns
`String` — The stored author email.

---

## setAuthorEmail

### Signature
```java
public void setAuthorEmail(String authorEmail)
```

### Description
Updates the author email. As with the name, the value reaches Git only when `RepoManager.setAuthor` (or `init`) forwards it to the service.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `authorEmail` | `String` | New author email; blank values are rejected later by `RepoService.setAuthor`. |

### Returns
`void`

---

## lastSelection

### Signature
```java
public String lastSelection()
```

### Description
Returns the remembered active selection name for the world, or `""` when none has been stored.

### Parameters
None.

### Returns
`String` — The remembered selection name, possibly empty.

---

## setLastSelection

### Signature
```java
public void setLastSelection(String lastSelection)
```

### Description
Stores the wand's active selection name so it can be restored after a restart. `RepoManager.setLastSelection` coerces `null` to `""` before calling this setter.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `lastSelection` | `String` | Selection name to remember. |

### Returns
`void`

---

## createdAt

### Signature
```java
public String createdAt()
```

### Description
Returns the ISO-8601 timestamp string stamped when the metadata was first built. There is no setter, so the value is effectively immutable after `defaults`/`normalize`.

### Parameters
None.

### Returns
`String` — The creation timestamp, e.g. `2026-11-12T18:41:10.123Z`.

---

## See Also

- **Software Detailed Design:** [RepoMetadata_SDD.md](./RepoMetadata_SDD.md)
