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

`RepoCommands` is a static-only module reached through `GitCommand.register`, which calls `attach` for the `/git` root and `attachConfig` for the `/git config` subtree. It drives repository creation and inspection (`/git init`, `/git repo`), identity (`/git config author "Name" "you@example.com"`), remotes (`/git remote add origin https://example.com/repo.git`, `/git remote list`, `/git remote remove origin`), history (`/git log`, `/git log 20`) and transport (`/git push`, `/git push --force`, `/git fetch`, `/git fetch origin`).

Repository lifecycle and transport commands.

---

## Fields

- `DATE` (`DateTimeFormatter`) — Formatter `yyyy-MM-dd HH:mm` in the system time zone, used by `showLog` to render each commit's `Instant` timestamp.

---

## RepoCommands (Constructor)

### Signature
```java
private RepoCommands()
```

### Description
Private no-argument constructor for a static-only command module: the class has no instance state and is never instantiated, since `attach`/`attachConfig` wire all behaviour as static callbacks.

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
Registers the repository and transport commands on the `/git` root: `init`, `repo`, the `remote` subtree (through `attachRemote`), `log [count]`, `push [--force]` and `fetch [remote]`. The optional remote argument of `fetch` is completed with the remotes of the current world's repository, read live but defensively. Attaching only builds the Brigadier tree; repository access happens when a command executes.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `root` | `LiteralArgumentBuilder<FabricClientCommandSource>` | The `/git` literal builder that the repository, remote, log and transport commands are appended to. |
| `services` | [`GitServices`](../GitServices.md) | Shared services captured by the callbacks for repository access, config defaults and chat feedback. |

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
Adds the author subcommand to the config literal built by `GitCommand`.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `config` | `LiteralArgumentBuilder<FabricClientCommandSource>` | The `config` literal builder created by `GitCommand` before the root is registered. |
| `services` | [`GitServices`](../GitServices.md) | Shared services used by the author callback to persist and apply the new identity. |

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
Builds the `/git remote` subtree: bare `remote` and `remote list` both list remotes, `remote add <name> <url>` takes the URL as a greedy string (so unquoted URLs may contain spaces and characters), and `remote remove <name>` deletes a configured remote.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `root` | `LiteralArgumentBuilder<FabricClientCommandSource>` | The `/git` literal builder to which the `remote` literal is appended. |
| `services` | [`GitServices`](../GitServices.md) | Shared services used by the remote callbacks. |

### Returns
`void`

---

## init

### Signature
```java
private static int init(FabricClientCommandSource source, GitServices services)
```

### Description
Creates or opens the repository for the current world and reports its path, branch and HEAD. The author name defaults to the player's name (or `config.committerName` for a non-player source); `RepoManager.init` also derives a local email and `HEAD` is shown as the short id plus summary, or as "empty" when there are no commits. In multiplayer it warns that applying builds later requires single-player even though the repository commands work.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | Command source identifying the client/world; a missing player falls back to the configured committer name. |
| `services` | [`GitServices`](../GitServices.md) | Provides the repo manager and messaging; `init` runs asynchronously on the IO executor. |

### Returns
`int` — 1 immediately after the async initialization is scheduled, 0 after an error is reported in chat.

---

## repo

### Signature
```java
private static int repo(FabricClientCommandSource source, GitServices services)
```

### Description
Shows the current world's repository information via `RepoService.info()` on the IO executor. When the world has no repository yet it prints the "Run /git init" hint and performs no background work.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | Command source identifying the client/world and receiving the report. |
| `services` | [`GitServices`](../GitServices.md) | Provides the repo manager for the initialization check and the info lookup. |

### Returns
`int` — 1 when the check/report path completes, 0 after an error is reported in chat.

---

## showRepo

### Signature
```java
private static void showRepo(FabricClientCommandSource source, GitServices services,
                                 WorldContext world, RepoInfo info)
```

### Description
Renders a `RepoInfo` snapshot as chat lines: world id, path, branch, HEAD (short id + summary or `(no commits)`), author, remote (`(none)` or `name -> url`) and working-tree state (`clean`/`dirty`). It only formats; all values come from the snapshot produced on the IO thread.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | Command source receiving the formatted lines. |
| `services` | [`GitServices`](../GitServices.md) | Supplies the message helper used for raw output. |
| `world` | [`WorldContext`](../WorldContext.md) | Current world, used for the `Repository: <id>` header. |
| `info` | [`RepoInfo`](../../../../../../../main/java/org/almond/minecraftgit/repo/RepoInfo.md) | Repository snapshot whose path, branch, HEAD, author, remote and cleanliness are printed. |

### Returns
`void`

---

## setAuthor

### Signature
```java
private static int setAuthor(FabricClientCommandSource source, GitServices services, String name, String email)
```

### Description
Validates and persists the commit identity for the current world. Both arguments must be non-blank (otherwise a `UserFacingException` is reported); `RepoManager.setAuthor` writes the local metadata and updates the already-open `RepoService`, so subsequent commits use the new identity immediately. Runs asynchronously, with the success message delivered on the client thread.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | Command source identifying the client/world and receiving feedback. |
| `services` | [`GitServices`](../GitServices.md) | Provides the repo manager and messaging. |
| `name` | `String` | New author name; must not be blank. |
| `email` | `String` | New author email; must not be blank and is stored verbatim. |

### Returns
`int` — 1 when the update is scheduled, 0 after an error is reported in chat.

---

## remoteAdd

### Signature
```java
private static int remoteAdd(FabricClientCommandSource source, GitServices services,
                                 String name, String url)
```

### Description
Adds a named remote to the current world's repository from the trimmed URL, and configures the standard `+refs/heads/*:refs/remotes/<name>/*` fetch refspec when JGit has not already set one. The success message echoes the stored `name -> url`; blank URLs, invalid names and unusable URLs are reported by `RepoService` through `CommandSupport`'s error handler.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | Command source identifying the client/world and receiving feedback. |
| `services` | [`GitServices`](../GitServices.md) | Provides the repo service and messaging. |
| `name` | `String` | Remote name, conventionally `origin`; validated by `RepoService.remoteAdd`. |
| `url` | `String` | Remote URL taken as a greedy string; surrounding whitespace is trimmed before being saved. |

### Returns
`int` — 1 when the add is scheduled, 0 after an error is reported in chat.

---

## remoteRemove

### Signature
```java
private static int remoteRemove(FabricClientCommandSource source, GitServices services, String name)
```

### Description
Removes the named remote from the current world's repository on the IO executor and confirms with "Removed remote '<name>'."; an unknown remote is surfaced as a chat error by `RepoService`.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | Command source identifying the client/world and receiving feedback. |
| `services` | [`GitServices`](../GitServices.md) | Provides the repo service and messaging. |
| `name` | `String` | Name of the configured remote to remove. |

### Returns
`int` — 1 when the removal is scheduled, 0 after an error is reported in chat.

---

## remoteList

### Signature
```java
private static int remoteList(FabricClientCommandSource source, GitServices services)
```

### Description
Lists the current world's configured remotes as `name -> url` lines on the IO executor. An empty result prints a hint to run `/git remote add origin <url>` instead of a list.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | Command source identifying the client/world and receiving the list. |
| `services` | [`GitServices`](../GitServices.md) | Provides the repo service and messaging. |

### Returns
`int` — 1 when the listing is scheduled, 0 after an error is reported in chat.

---

## log

### Signature
```java
private static int log(FabricClientCommandSource source, GitServices services, int count)
```

### Description
Reads up to `count` commits from the current world's repository on the IO executor and hands them to `showLog`. Without an argument the command passes 10; the literal restricts the argument with `IntegerArgumentType.integer(1, 100)`. An uninitialized world is reported before any background work.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | Command source identifying the client/world and receiving the history. |
| `services` | [`GitServices`](../GitServices.md) | Provides the repo manager and messaging. |
| `count` | `int` | Maximum number of commits to print, 1–100; defaults to 10 when omitted. |

### Returns
`int` — 1 when the log is scheduled (including the uninitialized-repo hint), 0 after an error is reported in chat.

---

## showLog

### Signature
```java
private static void showLog(FabricClientCommandSource source, GitServices services, List<CommitInfo> commits)
```

### Description
Formats the history returned by `RepoService.log`, each row as `shortId  yyyy-MM-dd HH:mm  author  message` using the class-level `DATE` formatter in the system time zone. An empty list prints "No commits yet." rather than an empty header.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | Command source receiving the formatted history. |
| `services` | [`GitServices`](../GitServices.md) | Supplies the message helper used for raw output. |
| `commits` | `List<CommitInfo>` | Commits to print, newest first as returned by the repository; may be empty. |

### Returns
`void`

---

## push

### Signature
```java
private static int push(FabricClientCommandSource source, GitServices services, boolean force)
```

### Description
Pushes the current branch to a remote on the IO executor, using the remote recorded in the world's repo metadata or `config.defaultRemote` when metadata has none. `--force` maps to `RepoService.push(remote, force, RepoProgress.NOOP)`, so non-fast-forward rejections become chat errors unless forced. The summary is reported as "Remote is already up to date." when `upToDate()`, otherwise as the branch, remote and number of updated refs.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | Command source identifying the client/world and receiving progress and results. |
| `services` | [`GitServices`](../GitServices.md) | Provides the repo manager/service, config defaults and messaging. |
| `force` | `boolean` | `true` when the `--force` literal was used, allowing a non-fast-forward overwrite of the remote branch. |

### Returns
`int` — 1 when the push is scheduled, 0 after an error is reported in chat.

---

## fetch

### Signature
```java
private static int fetch(FabricClientCommandSource source, GitServices services, String requestedRemote)
```

### Description
Fetches refs from a remote on the IO executor. The remote is the non-blank `requestedRemote`, otherwise the name stored in the world's repo metadata, otherwise `config.defaultRemote`; this metadata-derived selection means `/git fetch` follows the repository the world was last configured for. Success reports the remote and the number of updated refs.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | Command source identifying the client/world and receiving progress and results. |
| `services` | [`GitServices`](../GitServices.md) | Provides the repo manager/service, config defaults and messaging. |
| `requestedRemote` | `String` | Explicit remote name, or `null` when the argument was omitted; blank values also fall back to metadata/config. |

### Returns
`int` — 1 when the fetch is scheduled, 0 after an error is reported in chat.

---

## currentWorld

### Signature
```java
private static WorldContext currentWorld(FabricClientCommandSource source)
```

### Description
Returns `WorldContext.current(source.getClient())`, the `<world>__<dimension>` identity used to locate this world's repository and metadata. It throws a `UserFacingException` when no world is open, so every repo command effectively requires being in-game.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `source` | `FabricClientCommandSource` | Command source providing the Minecraft client whose current level is described. |

### Returns
[`WorldContext`](../WorldContext.md) — id, display name, dimension key and single-player flag of the open world.

---

## shortId

### Signature
```java
private static String shortId(String id)
```

### Description
Truncates a commit id to its first 8 characters for chat display. A null id or one shorter than 8 characters is returned unchanged, so an empty/unknown HEAD never throws.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `id` | `String` | Full commit hash, or null when HEAD is unborn. |

### Returns
`String` — the first 8 characters of `id`, or `id` unchanged when null or shorter than 8 characters.

---

## See Also

- **Software Detailed Design:** [RepoCommands_SDD.md](./RepoCommands_SDD.md)
