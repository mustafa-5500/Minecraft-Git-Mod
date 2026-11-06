# RepoCommands

> **Software Detailed Documentation:** [RepoCommands_SDD.md](./RepoCommands_SDD.md)  
> **Source File:** [RepoCommands.java](../../../../../../../../src/client/java/org/almond/minecraftgit/client/command/RepoCommands.java)

## Table of Contents

- [Usage](#usage)
- [Fields](#fields)
- [RepoCommands (Constructor)](#repocommands-constructor)
- [attach](#attach)
- [attachConfig](#attachconfig)
- [attachRemote](#attachremote)
- [init](#init)
- [repo](#repo)
- [showRepo](#showrepo)
- [setAuthor](#setauthor)
- [remoteAdd](#remoteadd)
- [remoteRemove](#remoteremove)
- [remoteList](#remotelist)
- [log](#log)
- [showLog](#showlog)
- [push](#push)
- [fetch](#fetch)
- [currentWorld](#currentworld)
- [shortId](#shortid)
- [See Also](#see-also)

## Usage

TODO: Add usage examples for `RepoCommands`.

Repository lifecycle and transport commands.

---

## Fields

- `DATE` (`DateTimeFormatter`) — TODO: describe field

---

## RepoCommands (Constructor)

### Signature
```java
private RepoCommands()
```

### Description
TODO: Describe what `RepoCommands` does.

### Parameters
None.

### Returns
`RepoCommands` instance.

---

## attach

### Signature
```java
public static void attach(LiteralArgumentBuilder<FabricClientCommandSource> root, GitServices services)
```

### Description
TODO: Describe what `attach` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `root` | `LiteralArgumentBuilder<FabricClientCommandSource>` | TODO: describe |
| `services` | [`GitServices`](../GitServices.md) | TODO: describe |

### Returns
`void`

---

## attachConfig

### Signature
```java
public static void attachConfig(LiteralArgumentBuilder<FabricClientCommandSource> config,
                                    GitServices services)
```

### Description
Adds the author subcommand to the config literal built by {@link GitCommand}.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `config` | `LiteralArgumentBuilder<FabricClientCommandSource>` | TODO: describe |
| `services` | [`GitServices`](../GitServices.md) | TODO: describe |

### Returns
`void`

---

## attachRemote

### Signature
```java
private static void attachRemote(LiteralArgumentBuilder<FabricClientCommandSource> root,
                                     GitServices services)
```

### Description
TODO: Describe what `attachRemote` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `root` | `LiteralArgumentBuilder<FabricClientCommandSource>` | TODO: describe |
| `services` | [`GitServices`](../GitServices.md) | TODO: describe |

### Returns
`void`

---

## init

### Signature
```java
private static int init(FabricClientCommandSource source, GitServices services)
```

### Description
TODO: Describe what `init` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | TODO: describe |
| `services` | [`GitServices`](../GitServices.md) | TODO: describe |

### Returns
`int` — TODO: describe return value.

---

## repo

### Signature
```java
private static int repo(FabricClientCommandSource source, GitServices services)
```

### Description
TODO: Describe what `repo` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | TODO: describe |
| `services` | [`GitServices`](../GitServices.md) | TODO: describe |

### Returns
`int` — TODO: describe return value.

---

## showRepo

### Signature
```java
private static void showRepo(FabricClientCommandSource source, GitServices services,
                                 WorldContext world, RepoInfo info)
```

### Description
TODO: Describe what `showRepo` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | TODO: describe |
| `services` | [`GitServices`](../GitServices.md) | TODO: describe |
| `world` | [`WorldContext`](../WorldContext.md) | TODO: describe |
| `info` | [`RepoInfo`](../../../../../../../main/java/org/almond/minecraftgit/repo/RepoInfo.md) | TODO: describe |

### Returns
`void`

---

## setAuthor

### Signature
```java
private static int setAuthor(FabricClientCommandSource source, GitServices services, String name, String email)
```

### Description
TODO: Describe what `setAuthor` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | TODO: describe |
| `services` | [`GitServices`](../GitServices.md) | TODO: describe |
| `name` | `String` | TODO: describe |
| `email` | `String` | TODO: describe |

### Returns
`int` — TODO: describe return value.

---

## remoteAdd

### Signature
```java
private static int remoteAdd(FabricClientCommandSource source, GitServices services,
                                 String name, String url)
```

### Description
TODO: Describe what `remoteAdd` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | TODO: describe |
| `services` | [`GitServices`](../GitServices.md) | TODO: describe |
| `name` | `String` | TODO: describe |
| `url` | `String` | TODO: describe |

### Returns
`int` — TODO: describe return value.

---

## remoteRemove

### Signature
```java
private static int remoteRemove(FabricClientCommandSource source, GitServices services, String name)
```

### Description
TODO: Describe what `remoteRemove` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | TODO: describe |
| `services` | [`GitServices`](../GitServices.md) | TODO: describe |
| `name` | `String` | TODO: describe |

### Returns
`int` — TODO: describe return value.

---

## remoteList

### Signature
```java
private static int remoteList(FabricClientCommandSource source, GitServices services)
```

### Description
TODO: Describe what `remoteList` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | TODO: describe |
| `services` | [`GitServices`](../GitServices.md) | TODO: describe |

### Returns
`int` — TODO: describe return value.

---

## log

### Signature
```java
private static int log(FabricClientCommandSource source, GitServices services, int count)
```

### Description
TODO: Describe what `log` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | TODO: describe |
| `services` | [`GitServices`](../GitServices.md) | TODO: describe |
| `count` | `int` | TODO: describe |

### Returns
`int` — TODO: describe return value.

---

## showLog

### Signature
```java
private static void showLog(FabricClientCommandSource source, GitServices services, List<CommitInfo> commits)
```

### Description
TODO: Describe what `showLog` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | TODO: describe |
| `services` | [`GitServices`](../GitServices.md) | TODO: describe |
| `commits` | `List<CommitInfo>` | TODO: describe |

### Returns
`void`

---

## push

### Signature
```java
private static int push(FabricClientCommandSource source, GitServices services, boolean force)
```

### Description
TODO: Describe what `push` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | TODO: describe |
| `services` | [`GitServices`](../GitServices.md) | TODO: describe |
| `force` | `boolean` | TODO: describe |

### Returns
`int` — TODO: describe return value.

---

## fetch

### Signature
```java
private static int fetch(FabricClientCommandSource source, GitServices services, String requestedRemote)
```

### Description
TODO: Describe what `fetch` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | TODO: describe |
| `services` | [`GitServices`](../GitServices.md) | TODO: describe |
| `requestedRemote` | `String` | TODO: describe |

### Returns
`int` — TODO: describe return value.

---

## currentWorld

### Signature
```java
private static WorldContext currentWorld(FabricClientCommandSource source)
```

### Description
TODO: Describe what `currentWorld` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | TODO: describe |

### Returns
[`WorldContext`](../WorldContext.md) — TODO: describe return value.

---

## shortId

### Signature
```java
private static String shortId(String id)
```

### Description
TODO: Describe what `shortId` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `id` | `String` | TODO: describe |

### Returns
`String` — TODO: describe return value.

---

## See Also

- **Software Detailed Design:** [RepoCommands_SDD.md](./RepoCommands_SDD.md)
