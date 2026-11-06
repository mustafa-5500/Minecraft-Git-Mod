# RepoService

> **Software Detailed Documentation:** [RepoService_SDD.md](./RepoService_SDD.md)  
> **Source File:** [RepoService.java](../../../../../../../src/main/java/org/almond/minecraftgit/repo/RepoService.java)

## Table of Contents

- [Usage](#usage)
- [Fields](#fields)
- [RepoService (Constructor)](#reposervice-constructor)
- [path](#path)
- [isInitialized](#isinitialized)
- [init](#init)
- [open](#open)
- [ensureOpenLocked](#ensureopenlocked)
- [writeBootstrapFiles](#writebootstrapfiles)
- [info](#info)
- [infoLocked](#infolocked)
- [currentBranch](#currentbranch)
- [currentBranchLocked](#currentbranchlocked)
- [headId](#headid)
- [commitAll](#commitall)
- [commitEmpty](#commitempty)
- [commitWorkspace](#commitworkspace)
- [safeResolve](#saferesolve)
- [deleteRecursively](#deleterecursively)
- [stageAllLocked](#stagealllocked)
- [statusLocked](#statuslocked)
- [headCommit](#headcommit)
- [log](#log)
- [setAuthor](#setauthor)
- [authorName](#authorname)
- [authorEmail](#authoremail)
- [configureAuthorLocked](#configureauthorlocked)
- [identityLocked](#identitylocked)
- [remoteAdd](#remoteadd)
- [remoteRemove](#remoteremove)
- [remoteList](#remotelist)
- [remote](#remote)
- [fetch](#fetch)
- [fetchLocked](#fetchlocked)
- [push](#push)
- [planPull](#planpull)
- [fastForward](#fastforward)
- [isPristineInitLocked](#ispristineinitlocked)
- [headObject](#headobject)
- [resolve](#resolve)
- [listSelectionsAt](#listselectionsat)
- [listSelectionsAtLocked](#listselectionsatlocked)
- [exportSelectionAt](#exportselectionat)
- [requireRemoteLocked](#requireremotelocked)
- [requireValidRemoteName](#requirevalidremotename)
- [toUri](#touri)
- [error](#error)
- [friendlyMessage](#friendlymessage)
- [firstLine](#firstline)
- [close](#close)
- [PushSummary](#pushsummary)
- [FetchSummary](#fetchsummary)
- [See Also](#see-also)

## Usage

TODO: Add usage examples for `RepoService`.

Facade over JGit for one repository. All public methods are serialized by a
lock because a JGit {@link Repository} is not safe for concurrent mutation.
The world is never touched here; repository I/O is pure file/network work.

---

## Fields

- `repoPath` (`Path`) — TODO: describe field
- `options` ([`RepoOptions`](RepoOptions.md)) — TODO: describe field
- `debug` ([`DebugLogger`](../debug/DebugLogger.md)) — TODO: describe field
- `lock` (`ReentrantLock`) — TODO: describe field
- `git` (`Git`) — TODO: describe field
- `repository` (`Repository`) — TODO: describe field
- `authorName` (`String`) — TODO: describe field
- `authorEmail` (`String`) — TODO: describe field

---

## RepoService (Constructor)

### Signature
```java
public RepoService(Path repoPath, RepoOptions options, DebugLogger debug)
```

### Description
TODO: Describe what `RepoService` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `repoPath` | `Path` | TODO: describe |
| `options` | [`RepoOptions`](RepoOptions.md) | TODO: describe |
| `debug` | [`DebugLogger`](../debug/DebugLogger.md) | TODO: describe |

### Returns
`RepoService` instance.

---

## path

### Signature
```java
public Path path()
```

### Description
TODO: Describe what `path` does.

### Parameters
None.

### Returns
`Path` — TODO: describe return value.

---

## isInitialized

### Signature
```java
public boolean isInitialized()
```

### Description
TODO: Describe what `isInitialized` does.

### Parameters
None.

### Returns
`boolean` — TODO: describe return value.

---

## init

### Signature
```java
public RepoInfo init()
```

### Description
Create the repository if needed (with a README and format documentation
commit), otherwise open it.

### Parameters
None.

### Returns
[`RepoInfo`](RepoInfo.md) — TODO: describe return value.

---

## open

### Signature
```java
public void open()
```

### Description
Open the repository if it is not already open.

### Parameters
None.

### Returns
`void`

---

## ensureOpenLocked

### Signature
```java
private void ensureOpenLocked()
```

### Description
TODO: Describe what `ensureOpenLocked` does.

### Parameters
None.

### Returns
`void`

---

## writeBootstrapFiles

### Signature
```java
private void writeBootstrapFiles()
```

### Description
TODO: Describe what `writeBootstrapFiles` does.

### Parameters
None.

### Returns
`void`

---

## info

### Signature
```java
public RepoInfo info()
```

### Description
TODO: Describe what `info` does.

### Parameters
None.

### Returns
[`RepoInfo`](RepoInfo.md) — TODO: describe return value.

---

## infoLocked

### Signature
```java
private RepoInfo infoLocked()
```

### Description
TODO: Describe what `infoLocked` does.

### Parameters
None.

### Returns
[`RepoInfo`](RepoInfo.md) — TODO: describe return value.

---

## currentBranch

### Signature
```java
public String currentBranch()
```

### Description
Current branch name, or an empty string for a detached HEAD.

### Parameters
None.

### Returns
`String` — TODO: describe return value.

---

## currentBranchLocked

### Signature
```java
private String currentBranchLocked()
```

### Description
TODO: Describe what `currentBranchLocked` does.

### Parameters
None.

### Returns
`String` — TODO: describe return value.

---

## headId

### Signature
```java
public Optional<String> headId()
```

### Description
TODO: Describe what `headId` does.

### Parameters
None.

### Returns
`Optional<String>` — TODO: describe return value.

---

## commitAll

### Signature
```java
public String commitAll(String message, boolean allowEmpty)
```

### Description
Stage the whole working tree and commit it.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `message` | `String` | TODO: describe |
| `allowEmpty` | `boolean` | TODO: describe |

### Returns
`String` — the new commit id, or {@code null} when there was nothing to commit and {@code allowEmpty} is false

---

## commitEmpty

### Signature
```java
public String commitEmpty(String message)
```

### Description
Create an empty commit, useful for verifying a configured remote.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `message` | `String` | TODO: describe |

### Returns
`String` — TODO: describe return value.

---

## commitWorkspace

### Signature
```java
public String commitWorkspace(Map<String, byte[]> files, Collection<String> directoriesToReplace,
                                  String message, boolean allowEmpty)
```

### Description
Replace whole selection directories and commit the result atomically from
the caller's perspective. Paths are relative to the repository root and
are rejected if they escape it.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `` | `Map<String` | TODO: describe |
| `files` | `byte[]>` | relative path to UTF-8 bytes |
| `directoriesToReplace` | `Collection<String>` | directories removed before {@code files} are written |
| `message` | `String` | TODO: describe |
| `allowEmpty` | `boolean` | TODO: describe |

### Returns
`String` — the commit id, or {@code null} when nothing changed and {@code allowEmpty} is false

---

## safeResolve

### Signature
```java
private Path safeResolve(String relative)
```

### Description
TODO: Describe what `safeResolve` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `relative` | `String` | TODO: describe |

### Returns
`Path` — TODO: describe return value.

---

## deleteRecursively

### Signature
```java
private static void deleteRecursively(Path path)
```

### Description
TODO: Describe what `deleteRecursively` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `path` | `Path` | TODO: describe |

### Returns
`void`

---

## stageAllLocked

### Signature
```java
private void stageAllLocked()
```

### Description
TODO: Describe what `stageAllLocked` does.

### Parameters
None.

### Returns
`void`

---

## statusLocked

### Signature
```java
private Status statusLocked()
```

### Description
TODO: Describe what `statusLocked` does.

### Parameters
None.

### Returns
`Status` — TODO: describe return value.

---

## headCommit

### Signature
```java
public Optional<CommitInfo> headCommit()
```

### Description
TODO: Describe what `headCommit` does.

### Parameters
None.

### Returns
`Optional<CommitInfo>` — TODO: describe return value.

---

## log

### Signature
```java
public List<CommitInfo> log(int limit)
```

### Description
TODO: Describe what `log` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `limit` | `int` | TODO: describe |

### Returns
`List<CommitInfo>` — TODO: describe return value.

---

## setAuthor

### Signature
```java
public void setAuthor(String name, String email)
```

### Description
TODO: Describe what `setAuthor` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `name` | `String` | TODO: describe |
| `email` | `String` | TODO: describe |

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

## configureAuthorLocked

### Signature
```java
private void configureAuthorLocked()
```

### Description
TODO: Describe what `configureAuthorLocked` does.

### Parameters
None.

### Returns
`void`

---

## identityLocked

### Signature
```java
private PersonIdent identityLocked()
```

### Description
TODO: Describe what `identityLocked` does.

### Parameters
None.

### Returns
`PersonIdent` — TODO: describe return value.

---

## remoteAdd

### Signature
```java
public void remoteAdd(String name, String url)
```

### Description
TODO: Describe what `remoteAdd` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `name` | `String` | TODO: describe |
| `url` | `String` | TODO: describe |

### Returns
`void`

---

## remoteRemove

### Signature
```java
public void remoteRemove(String name)
```

### Description
TODO: Describe what `remoteRemove` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `name` | `String` | TODO: describe |

### Returns
`void`

---

## remoteList

### Signature
```java
public List<RemoteInfo> remoteList()
```

### Description
TODO: Describe what `remoteList` does.

### Parameters
None.

### Returns
`List<RemoteInfo>` — TODO: describe return value.

---

## remote

### Signature
```java
public Optional<RemoteInfo> remote(String name)
```

### Description
TODO: Describe what `remote` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `name` | `String` | TODO: describe |

### Returns
`Optional<RemoteInfo>` — TODO: describe return value.

---

## fetch

### Signature
```java
public FetchSummary fetch(String remote)
```

### Description
TODO: Describe what `fetch` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `remote` | `String` | TODO: describe |

### Returns
`FetchSummary` — TODO: describe return value.

---

## fetch

### Signature
```java
public FetchSummary fetch(String remote, RepoProgress progress)
```

### Description
TODO: Describe what `fetch` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `remote` | `String` | TODO: describe |
| `progress` | [`RepoProgress`](RepoProgress.md) | TODO: describe |

### Returns
`FetchSummary` — TODO: describe return value.

---

## fetchLocked

### Signature
```java
private FetchResult fetchLocked(String remote, RepoProgress progress)
```

### Description
TODO: Describe what `fetchLocked` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `remote` | `String` | TODO: describe |
| `progress` | [`RepoProgress`](RepoProgress.md) | TODO: describe |

### Returns
`FetchResult` — TODO: describe return value.

---

## push

### Signature
```java
public PushSummary push(String remote, boolean force)
```

### Description
TODO: Describe what `push` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `remote` | `String` | TODO: describe |
| `force` | `boolean` | TODO: describe |

### Returns
`PushSummary` — TODO: describe return value.

---

## push

### Signature
```java
public PushSummary push(String remote, boolean force, RepoProgress progress)
```

### Description
TODO: Describe what `push` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `remote` | `String` | TODO: describe |
| `force` | `boolean` | TODO: describe |
| `progress` | [`RepoProgress`](RepoProgress.md) | TODO: describe |

### Returns
`PushSummary` — TODO: describe return value.

---

## planPull

### Signature
```java
public PullPlan planPull(String remote, RepoProgress progress)
```

### Description
Fetch and classify the relationship between the local branch and its
remote. The caller applies the remote snapshot to the world before calling
{@link #fastForward(PullPlan)}, so a failed world write does not move Git.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `remote` | `String` | TODO: describe |
| `progress` | [`RepoProgress`](RepoProgress.md) | TODO: describe |

### Returns
[`PullPlan`](PullPlan.md) — TODO: describe return value.

---

## fastForward

### Signature
```java
public boolean fastForward(PullPlan plan)
```

### Description
Move the local branch to the plan's remote head after a successful apply.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `plan` | [`PullPlan`](PullPlan.md) | TODO: describe |

### Returns
`boolean` — TODO: describe return value.

---

## isPristineInitLocked

### Signature
```java
private boolean isPristineInitLocked(RevCommit commit)
```

### Description
TODO: Describe what `isPristineInitLocked` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `commit` | `RevCommit` | TODO: describe |

### Returns
`boolean` — TODO: describe return value.

---

## headObject

### Signature
```java
public Optional<ObjectId> headObject()
```

### Description
TODO: Describe what `headObject` does.

### Parameters
None.

### Returns
`Optional<ObjectId>` — TODO: describe return value.

---

## resolve

### Signature
```java
public Optional<ObjectId> resolve(String revision)
```

### Description
TODO: Describe what `resolve` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `revision` | `String` | TODO: describe |

### Returns
`Optional<ObjectId>` — TODO: describe return value.

---

## listSelectionsAt

### Signature
```java
public Set<String> listSelectionsAt(ObjectId commitId)
```

### Description
Selection names that exist in a commit's tree.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `commitId` | `ObjectId` | TODO: describe |

### Returns
`Set<String>` — TODO: describe return value.

---

## listSelectionsAtLocked

### Signature
```java
private Set<String> listSelectionsAtLocked(ObjectId commitId)
```

### Description
TODO: Describe what `listSelectionsAtLocked` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `commitId` | `ObjectId` | TODO: describe |

### Returns
`Set<String>` — TODO: describe return value.

---

## exportSelectionAt

### Signature
```java
public boolean exportSelectionAt(ObjectId commitId, String selection, Path target)
```

### Description
Write one selection's files from a commit into {@code target}. Used by pull
and checkout to apply a snapshot without disturbing the repository worktree.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `commitId` | `ObjectId` | TODO: describe |
| `selection` | `String` | TODO: describe |
| `target` | `Path` | TODO: describe |

### Returns
`boolean` — TODO: describe return value.

---

## requireRemoteLocked

### Signature
```java
private void requireRemoteLocked(String name)
```

### Description
TODO: Describe what `requireRemoteLocked` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `name` | `String` | TODO: describe |

### Returns
`void`

---

## requireValidRemoteName

### Signature
```java
private static void requireValidRemoteName(String name)
```

### Description
TODO: Describe what `requireValidRemoteName` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `name` | `String` | TODO: describe |

### Returns
`void`

---

## toUri

### Signature
```java
private static URIish toUri(String url)
```

### Description
TODO: Describe what `toUri` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `url` | `String` | TODO: describe |

### Returns
`URIish` — TODO: describe return value.

---

## error

### Signature
```java
private RepoException error(String action, Exception cause)
```

### Description
TODO: Describe what `error` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `action` | `String` | TODO: describe |
| `cause` | `Exception` | TODO: describe |

### Returns
[`RepoException`](RepoException.md) — TODO: describe return value.

---

## friendlyMessage

### Signature
```java
private static String friendlyMessage(Exception cause)
```

### Description
TODO: Describe what `friendlyMessage` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `cause` | `Exception` | TODO: describe |

### Returns
`String` — TODO: describe return value.

---

## firstLine

### Signature
```java
private static String firstLine(String message)
```

### Description
TODO: Describe what `firstLine` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `message` | `String` | TODO: describe |

### Returns
`String` — TODO: describe return value.

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

## PushSummary

### Signature
```java
public record PushSummary(String remote, String branch, int updatedRefs, boolean upToDate)
```

### Description
TODO: Describe what `PushSummary` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `remote` | `String` | TODO: describe |
| `branch` | `String` | TODO: describe |
| `updatedRefs` | `int` | TODO: describe |
| `upToDate` | `boolean` | TODO: describe |

### Returns
`record` — TODO: describe return value.

---

## FetchSummary

### Signature
```java
public record FetchSummary(String remote, int updatedRefs, String messages)
```

### Description
TODO: Describe what `FetchSummary` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `remote` | `String` | TODO: describe |
| `updatedRefs` | `int` | TODO: describe |
| `messages` | `String` | TODO: describe |

### Returns
`record` — TODO: describe return value.

---

## See Also

- **Software Detailed Design:** [RepoService_SDD.md](./RepoService_SDD.md)
