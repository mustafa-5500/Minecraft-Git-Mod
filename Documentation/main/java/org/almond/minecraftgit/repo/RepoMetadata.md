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

TODO: Add usage examples for `RepoMetadata`.

Per-world, local-only repository state. This file is never committed: it maps
mod concepts (world id, default remote, author) onto Git concepts and may
differ between machines that share the same repository.

---

## Fields

- `world` (`String`) — TODO: describe field
- `branch` (`String`) — TODO: describe field
- `remoteName` (`String`) — TODO: describe field
- `remoteUrl` (`String`) — TODO: describe field
- `authorName` (`String`) — TODO: describe field
- `authorEmail` (`String`) — TODO: describe field
- `lastSelection` (`String`) — TODO: describe field
- `createdAt` (`String`) — TODO: describe field

---

## defaults

### Signature
```java
public static RepoMetadata defaults(String world, RepoOptions options)
```

### Description
TODO: Describe what `defaults` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `world` | `String` | TODO: describe |
| `options` | [`RepoOptions`](RepoOptions.md) | TODO: describe |

### Returns
`RepoMetadata` — TODO: describe return value.

---

## normalize

### Signature
```java
public RepoMetadata normalize(String world, RepoOptions options)
```

### Description
Fill in blanks introduced by older or hand-edited metadata files.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `world` | `String` | TODO: describe |
| `options` | [`RepoOptions`](RepoOptions.md) | TODO: describe |

### Returns
`RepoMetadata` — TODO: describe return value.

---

## world

### Signature
```java
public String world()
```

### Description
TODO: Describe what `world` does.

### Parameters
None.

### Returns
`String` — TODO: describe return value.

---

## setWorld

### Signature
```java
public void setWorld(String world)
```

### Description
TODO: Describe what `setWorld` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `world` | `String` | TODO: describe |

### Returns
`void`

---

## branch

### Signature
```java
public String branch()
```

### Description
TODO: Describe what `branch` does.

### Parameters
None.

### Returns
`String` — TODO: describe return value.

---

## setBranch

### Signature
```java
public void setBranch(String branch)
```

### Description
TODO: Describe what `setBranch` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `branch` | `String` | TODO: describe |

### Returns
`void`

---

## remoteName

### Signature
```java
public String remoteName()
```

### Description
TODO: Describe what `remoteName` does.

### Parameters
None.

### Returns
`String` — TODO: describe return value.

---

## setRemoteName

### Signature
```java
public void setRemoteName(String remoteName)
```

### Description
TODO: Describe what `setRemoteName` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `remoteName` | `String` | TODO: describe |

### Returns
`void`

---

## remoteUrl

### Signature
```java
public String remoteUrl()
```

### Description
TODO: Describe what `remoteUrl` does.

### Parameters
None.

### Returns
`String` — TODO: describe return value.

---

## setRemoteUrl

### Signature
```java
public void setRemoteUrl(String remoteUrl)
```

### Description
TODO: Describe what `setRemoteUrl` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `remoteUrl` | `String` | TODO: describe |

### Returns
`void`

---

## authorName

### Signature
```java
public String authorName()
```

### Description
TODO: Describe what `authorName` does.

### Parameters
None.

### Returns
`String` — TODO: describe return value.

---

## setAuthorName

### Signature
```java
public void setAuthorName(String authorName)
```

### Description
TODO: Describe what `setAuthorName` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `authorName` | `String` | TODO: describe |

### Returns
`void`

---

## authorEmail

### Signature
```java
public String authorEmail()
```

### Description
TODO: Describe what `authorEmail` does.

### Parameters
None.

### Returns
`String` — TODO: describe return value.

---

## setAuthorEmail

### Signature
```java
public void setAuthorEmail(String authorEmail)
```

### Description
TODO: Describe what `setAuthorEmail` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `authorEmail` | `String` | TODO: describe |

### Returns
`void`

---

## lastSelection

### Signature
```java
public String lastSelection()
```

### Description
TODO: Describe what `lastSelection` does.

### Parameters
None.

### Returns
`String` — TODO: describe return value.

---

## setLastSelection

### Signature
```java
public void setLastSelection(String lastSelection)
```

### Description
TODO: Describe what `setLastSelection` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `lastSelection` | `String` | TODO: describe |

### Returns
`void`

---

## createdAt

### Signature
```java
public String createdAt()
```

### Description
TODO: Describe what `createdAt` does.

### Parameters
None.

### Returns
`String` — TODO: describe return value.

---

## See Also

- **Software Detailed Design:** [RepoMetadata_SDD.md](./RepoMetadata_SDD.md)
