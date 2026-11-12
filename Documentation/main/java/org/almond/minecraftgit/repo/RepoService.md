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

`RepoService` is reached through [`RepoManager`](RepoManager.md): `RepoManager.service(worldId)` caches one instance per world id, so every command touching that world shares the same JGit handles. All methods are blocking file or network I/O protected by an internal lock, which is why commands call them on the async executor and apply results on the client thread. Failures are thrown as [`RepoException`](RepoException.md) with one actionable sentence.

```java
RepoService repo = repos.service("MyWorld__overworld");
repo.init();                                   // creates .git and the bootstrap commit on first use
String commit = repo.commitWorkspace(files, List.of("castle"), "Update castle", false);
repo.push("origin", false);                    // what /git push origin runs
```

Facade over JGit for one repository. All public methods are serialized by a
lock because a JGit `Repository` is not safe for concurrent mutation.
The world is never touched here; repository I/O is pure file/network work.

---

## Fields

- `repoPath` (`Path`) — absolute, normalized worktree directory; every containment check in `safeResolve` is relative to it.
- `options` ([`RepoOptions`](RepoOptions.md)) — immutable defaults captured at construction: branch name, default remote and initial author identity.
- `debug` ([`DebugLogger`](../debug/DebugLogger.md)) — sink for `repo`-category diagnostics; its output never reaches the user directly.
- `lock` (`ReentrantLock`) — serializes every public operation and the `*Locked` helpers called under it; reentrant, so public methods may call each other.
- `git` (`Git`) — JGit façade, opened lazily by `ensureOpenLocked` and cleared by `close`.
- `repository` (`Repository`) — underlying repository for config, ref and tree reads; non-null exactly while `git` is open.
- `authorName` (`String`) — commit identity name, seeded from `RepoOptions` and mirrored into `user.name` by `configureAuthorLocked`.
- `authorEmail` (`String`) — commit identity email, seeded from `RepoOptions` and mirrored into `user.email` by `configureAuthorLocked`.

---

## RepoService (Constructor)

### Signature
```java
public RepoService(Path repoPath, RepoOptions options, DebugLogger debug)
```

### Description
Stores the repository location, options and logger without touching the filesystem or JGit; the path is made absolute and normalized so later containment checks are reliable. The author identity starts from `RepoOptions`, which already substitutes defaults for blank values.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `repoPath` | `Path` | Worktree directory for this world; it may not exist until `init()` creates it. |
| `options` | [`RepoOptions`](RepoOptions.md) | Branch, default remote and initial author identity; must not be `null`. |
| `debug` | [`DebugLogger`](../debug/DebugLogger.md) | Logger for open/init/commit/sync diagnostics; must not be `null`. |

### Returns
`RepoService` instance.

---

## path

### Signature
```java
public Path path()
```

### Description
Returns the normalized directory stored at construction; it does not check that the directory exists and performs no I/O.

### Parameters
None.

### Returns
`Path` — the absolute worktree path used as the base for all repository-relative resolution.

---

## isInitialized

### Signature
```java
public boolean isInitialized()
```

### Description
Reports whether `<repoPath>/.git` is a directory, which is the only initialization signal this layer uses; a bare repository or a missing path counts as uninitialized. It is a cheap filesystem probe that neither opens JGit nor takes the lock.

### Parameters
None.

### Returns
`boolean` — `true` when a `.git` directory exists under `path()`.

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
[`RepoInfo`](RepoInfo.md) — snapshot of the created or opened repository: branch, HEAD id and summary, remote name and URL, author identity, `hasCommits` and the clean flag.

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
Idempotently opens the JGit handles: it returns immediately when `git` and `repository` are already set, otherwise throws `RepoException` if `.git` is missing or calls `Git.open` and re-applies the author identity to the repository config. Callers must already hold `lock`.

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
Writes `README.md` (format summary pointing at `Documentation/Git-Format.md`) and `.gitattributes` (`*.csv` and `*.json` as LF text) only when they do not already exist, so user edits survive. Both files are UTF-8 and are staged into the bootstrap commit created by `init()`.

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
Locks, ensures the repository is open and returns a fresh `RepoInfo`; the clean flag comes from a JGit status of the working tree, so it reflects any pending selection changes.

### Parameters
None.

### Returns
[`RepoInfo`](RepoInfo.md) — point-in-time state for `/git repo`, with an empty head id and summary before the first commit and the configured or default remote.

---

## infoLocked

### Signature
```java
private RepoInfo infoLocked()
```

### Description
Assembles `RepoInfo` by resolving `HEAD` (id and short message), reading the current branch, taking that branch's configured remote or falling back to `options.defaultRemote()`, and reading `remote.<name>.url`. It also reports the in-memory author identity, whether any commit exists and whether `statusLocked()` is clean.

### Parameters
None.

### Returns
[`RepoInfo`](RepoInfo.md) — populated snapshot; `hasCommits` is false and `headId` empty on an unborn branch.

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
`String` — checked-out branch name, or an empty string when `HEAD` is detached.

---

## currentBranchLocked

### Signature
```java
private String currentBranchLocked()
```

### Description
Reads `repository.getBranch()` and maps a detached `HEAD` (where JGit returns the commit id instead of a ref name) to an empty string. Any `IOException` becomes a `RepoException`.

### Parameters
None.

### Returns
`String` — branch name, or an empty string for a detached `HEAD`.

---

## headId

### Signature
```java
public Optional<String> headId()
```

### Description
Resolves `Constants.HEAD` to a commit id and returns its full hex form without walking history; an unborn branch yields `Optional.empty()`.

### Parameters
None.

### Returns
`Optional<String>` — full hex `HEAD` commit id, or empty when the branch has no commits.

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
| `message` | `String` | Commit message; only its first line is used for debug logging. |
| `allowEmpty` | `boolean` | When `true`, commit even if the staged tree matches `HEAD`; when `false` the call returns `null` instead. |

### Returns
`String` — the new commit id, or `null` when there was nothing to commit and `allowEmpty` is false

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
| `message` | `String` | Message for the empty commit; its first line is logged. |

### Returns
`String` — the new commit id, or `null` if JGit produced no commit.

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
| `files` | `Map<String, byte[]>` | Repository-relative path to UTF-8 file bytes; parent directories are created as needed. |
| `directoriesToReplace` | `Collection<String>` | directories removed before `files` are written |
| `message` | `String` | Commit message; only its first line is logged. |
| `allowEmpty` | `boolean` | When `false`, an unchanged tree returns `null` instead of creating an empty commit. |

### Returns
`String` — the commit id, or `null` when nothing changed and `allowEmpty` is false

---

## safeResolve

### Signature
```java
private Path safeResolve(String relative)
```

### Description
Resolves the candidate against the repository root, normalizes it and rejects anything that is not a strict descendant of the root, so `../` segments and absolute paths cannot write or delete outside the repository.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `relative` | `String` | Repository-relative path such as `castle/meta.json`; `..` segments and absolute paths are rejected. |

### Returns
`Path` — normalized absolute target guaranteed to be a strict descendant of `path()`.

---

## deleteRecursively

### Signature
```java
private static void deleteRecursively(Path path)
```

### Description
Recursively deletes a file or directory tree, visiting children before parents via `Files.walk` sorted in reverse order, and treats a missing path as a no-op. It is used to wipe replaced selection directories and the export target.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `path` | `Path` | Absolute path to remove; callers are responsible for having validated it first. |

### Returns
`void`

---

## stageAllLocked

### Signature
```java
private void stageAllLocked()
```

### Description
Stages the whole worktree in two passes: `addFilepattern(".")` for new and modified files, then `setUpdate(true)` so deletions are recorded too. Both passes run under `lock` and failures are translated by `error("stage files", ...)`.

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
Calls `git.status()` and returns JGit's `Status` for the worktree against the index and `HEAD`; the checked `GitAPIException` is declared so the locked caller can translate it.

### Parameters
None.

### Returns
`Status` — JGit status snapshot used for no-op checks and for `RepoInfo.clean`.

---

## headCommit

### Signature
```java
public Optional<CommitInfo> headCommit()
```

### Description
Returns the newest entry of `log(1)`, i.e. the commit `HEAD` points at; an unborn branch yields `Optional.empty()`.

### Parameters
None.

### Returns
`Optional<CommitInfo>` — most recent commit on the current branch, empty before the first commit.

---

## log

### Signature
```java
public List<CommitInfo> log(int limit)
```

### Description
Walks back from `HEAD` with `git.log()` and returns at most `limit` commits, or an empty list on an unborn branch (unresolved `HEAD` or `NoHeadException`). Each row carries full and 8-character ids, author name and email (falling back to the committer, then `unknown` and `Instant.EPOCH`), timestamp and message subject.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `limit` | `int` | Maximum number of commits to return; values below 1 are clamped to 1. |

### Returns
`List<CommitInfo>` — newest-first commits, empty when the branch has no commits.

---

## setAuthor

### Signature
```java
public void setAuthor(String name, String email)
```

### Description
Validates that both values are non-blank, updates the in-memory identity and, when a repository exists, opens it if needed and saves `user.name`/`user.email` so later commits and external Git tooling agree. It does not create a repository when none is initialized.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `name` | `String` | Non-blank author name written to `user.name`. |
| `email` | `String` | Non-blank author email written to `user.email`. |

### Returns
`void`

---

## authorName

### Signature
```java
public String authorName()
```

### Description
Returns the identity name used for new commits; it is set by the constructor and replaced by `setAuthor`.

### Parameters
None.

### Returns
`String` — current author name.

---

## authorEmail

### Signature
```java
public String authorEmail()
```

### Description
Returns the identity email used for new commits; it is set by the constructor and replaced by `setAuthor`.

### Parameters
None.

### Returns
`String` — current author email.

---

## configureAuthorLocked

### Signature
```java
private void configureAuthorLocked()
```

### Description
Writes `user.name` and `user.email` into the cached `StoredConfig` and saves it; called after `open`/`init` and from `setAuthor`. Requires the lock and an open repository, and turns an `IOException` into `RepoException`.

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
Builds a fresh `PersonIdent` from the current name and email so each commit records the configured identity with the current timestamp.

### Parameters
None.

### Returns
`PersonIdent` — author/committer identity passed to JGit commit calls.

---

## remoteAdd

### Signature
```java
public void remoteAdd(String name, String url)
```

### Description
Validates the remote name, rejects a blank URL and converts it with `toUri` (so Windows drive, UNC and POSIX paths become `file:` URIs), then registers it through `git.remoteAdd`. If the new remote has no fetch refspec it adds `+refs/heads/*:refs/remotes/<name>/*`, which `planPull` relies on; an unparsable URL surfaces as `RepoException`.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `name` | `String` | Remote name, 1-64 characters of letters, digits, `.`, `_` or `-`. |
| `url` | `String` | Non-blank HTTPS/SSH URL or local path; local paths are converted to `file:` URIs. |

### Returns
`void`

---

## remoteRemove

### Signature
```java
public void remoteRemove(String name)
```

### Description
Validates the name and removes the remote from the repository config via `git.remoteRemove`; already-fetched remote-tracking refs are left in place.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `name` | `String` | Name of a configured remote, validated with the same 1-64 character rule. |

### Returns
`void`

---

## remoteList

### Signature
```java
public List<RemoteInfo> remoteList()
```

### Description
Lists configured remotes via `git.remoteList()`, mapping each `RemoteConfig` to a `RemoteInfo` with its first URI, or an empty string when it has none.

### Parameters
None.

### Returns
`List<RemoteInfo>` — configured remotes in config order, empty when none are configured.

---

## remote

### Signature
```java
public Optional<RemoteInfo> remote(String name)
```

### Description
Filters `remoteList()` for an exact name match, so it inherits that call's locking and error translation rather than reading config itself.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `name` | `String` | Exact remote name to look up. |

### Returns
`Optional<RemoteInfo>` — the matching remote, or empty when it is not configured.

---

## fetch

### Signature
```java
public FetchSummary fetch(String remote)
```

### Description
Convenience overload that fetches without progress reporting by delegating to `fetch(remote, RepoProgress.NOOP)`.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `remote` | `String` | Configured remote name to fetch from. |

### Returns
`FetchSummary` — remote name, number of updated remote-tracking refs and JGit's transport messages.

---

## fetch

### Signature
```java
public FetchSummary fetch(String remote, RepoProgress progress)
```

### Description
Fetches into the remote-tracking refs under the instance lock and summarises the result; the working tree, index and local branches are untouched.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `remote` | `String` | Configured remote name; it must exist and have a URL. |
| `progress` | [`RepoProgress`](RepoProgress.md) | Optional transport progress sink; `null` or `NOOP` disables monitoring. |

### Returns
`FetchSummary` — updated ref count from `FetchResult.getTrackingRefUpdates()` plus server messages.

---

## fetchLocked

### Signature
```java
private FetchResult fetchLocked(String remote, RepoProgress progress)
```

### Description
Validates the name and requires the remote to be configured, then fetches with an explicit `+refs/heads/*:refs/remotes/<remote>/*` refspec, `setRemoveDeletedRefs(true)` to prune deleted branches and a `JGitProgressMonitor` adapter. The caller holds the lock and `GitAPIException` propagates for translation.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `remote` | `String` | Validated, configured remote name. |
| `progress` | [`RepoProgress`](RepoProgress.md) | Requested progress sink; `NOOP` maps to JGit's null monitor. |

### Returns
`FetchResult` — JGit result whose tracking-ref updates and messages become the `FetchSummary`.

---

## push

### Signature
```java
public PushSummary push(String remote, boolean force)
```

### Description
Convenience overload that pushes without progress reporting by delegating to `push(remote, force, RepoProgress.NOOP)`.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `remote` | `String` | Configured remote name to push to. |
| `force` | `boolean` | When `true`, allows overwriting a non-fast-forward remote branch. |

### Returns
`PushSummary` — remote, branch, updated ref count and whether the remote was already up to date.

---

## push

### Signature
```java
public PushSummary push(String remote, boolean force, RepoProgress progress)
```

### Description
Pushes `refs/heads/<branch>` to the same branch on the remote under the lock, then records `branch.<branch>.remote` and `branch.<branch>.merge` so a later pull knows its upstream. Ref updates rejected as non-fast-forward, remote-changed or other reasons are collected into a `RepoException` that suggests fetching or pulling first, or using `--force`; a detached `HEAD` and a missing commit are rejected up front.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `remote` | `String` | Configured remote name; it must exist. |
| `force` | `boolean` | Passed to `git.push().setForce(...)`; `false` protects remote history. |
| `progress` | [`RepoProgress`](RepoProgress.md) | Optional transport progress sink for the push. |

### Returns
`PushSummary` — `upToDate()` is true when JGit reported `UP_TO_DATE` or no ref was updated at all.

---

## planPull

### Signature
```java
public PullPlan planPull(String remote, RepoProgress progress)
```

### Description
Fetch and classify the relationship between the local branch and its
remote. The caller applies the remote snapshot to the world before calling
`.fastForward(PullPlan)`, so a failed world write does not move Git.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `remote` | `String` | Remote to fetch and compare; it must be configured. |
| `progress` | [`RepoProgress`](RepoProgress.md) | Progress sink for the embedded fetch. |

### Returns
[`PullPlan`](PullPlan.md) — local and remote heads, merge base, `UP_TO_DATE`/`FAST_FORWARD`/`DIVERGED` classification and fetch count; no ref or worktree is modified.

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
| `plan` | [`PullPlan`](PullPlan.md) | Plan returned by `planPull`; only a `FAST_FORWARD` plan is applied. |

### Returns
`boolean` — `true` after a hard reset to the remote head, `false` when the plan was not a fast-forward.

---

## isPristineInitLocked

### Signature
```java
private boolean isPristineInitLocked(RevCommit commit)
```

### Description
Recognises the untouched scaffold commit produced by `init()`: its subject starts with `Initialize MinecraftGit repository` and its tree contains only `README.md` and `.gitattributes`. Such a commit is treated as an unborn branch by `planPull`, so a build can be pulled into a freshly initialized world whose history is unrelated.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `commit` | `RevCommit` | Commit to test, already parsed from this repository. |

### Returns
`boolean` — `true` when the commit is the bootstrap commit with no build data.

---

## headObject

### Signature
```java
public Optional<ObjectId> headObject()
```

### Description
Resolves `HEAD` and returns the raw `ObjectId`, which callers pass to tree-reading methods such as `listSelectionsAt` and `exportSelectionAt`.

### Parameters
None.

### Returns
`Optional<ObjectId>` — the `HEAD` object id, empty on an unborn branch.

---

## resolve

### Signature
```java
public Optional<ObjectId> resolve(String revision)
```

### Description
Resolves any JGit revision string (`HEAD~1`, a branch name, a full or abbreviated hash) against the open repository and returns empty instead of throwing when nothing matches.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `revision` | `String` | JGit revision expression to resolve. |

### Returns
`Optional<ObjectId>` — resolved object id, or empty when the revision does not exist.

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
| `commitId` | `ObjectId` | Commit whose tree is scanned for selection directories. |

### Returns
`Set<String>` — distinct top-level names that pass `Names.isValidSelectionName`, in tree order.

---

## listSelectionsAtLocked

### Signature
```java
private Set<String> listSelectionsAtLocked(ObjectId commitId)
```

### Description
Parses the commit and walks its tree recursively, taking the first path segment of every entry and keeping names accepted by `Names.isValidSelectionName` in a `LinkedHashSet`. That filter keeps repository files such as `README.md` out of the selection list.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `commitId` | `ObjectId` | Commit to inspect; it must exist in this repository. |

### Returns
`Set<String>` — valid selection directory names, deduplicated, in traversal order.

---

## exportSelectionAt

### Signature
```java
public boolean exportSelectionAt(ObjectId commitId, String selection, Path target)
```

### Description
Write one selection's files from a commit into `target`. Used by pull
and checkout to apply a snapshot without disturbing the repository worktree.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `commitId` | `ObjectId` | Commit holding the snapshot to export. |
| `selection` | `String` | Selection name, validated with `Names.requireSelectionName` so it cannot escape the target. |
| `target` | `Path` | Directory that is deleted and recreated before the files are written. |

### Returns
`boolean` — `true` when at least one file was exported, `false` when the commit has no files for that selection.

---

## requireRemoteLocked

### Signature
```java
private void requireRemoteLocked(String name)
```

### Description
Looks up `remote.<name>.url` and throws a `RepoException` naming `/git remote add <name> <url>` when the remote is absent or has a blank URL; it runs before any fetch or push touches the network.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `name` | `String` | Remote name already validated by `requireValidRemoteName`. |

### Returns
`void`

---

## requireValidRemoteName

### Signature
```java
private static void requireValidRemoteName(String name)
```

### Description
Enforces the remote name grammar `[A-Za-z0-9][A-Za-z0-9_.-]{0,63}` so a name is safe both as a config subsection and as a refspec component; violations throw `RepoException`.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `name` | `String` | Candidate remote name; `null` is rejected. |

### Returns
`void`

---

## toUri

### Signature
```java
private static URIish toUri(String url)
```

### Description
Builds the `URIish` used by `git.remoteAdd`, converting local paths (Windows drive letters, UNC shares, `/...`, `./...`, `../...`) through `File.toURI()` so JGit does not mistake a drive-letter path for a one-letter URI scheme. Any other value is parsed as a URL, and `URISyntaxException` is translated by the caller.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `url` | `String` | Remote URL or local filesystem path supplied to `/git remote add`. |

### Returns
`URIish` — parsed remote location ready for JGit.

---

## error

### Signature
```java
private RepoException error(String action, Exception cause)
```

### Description
Central translation point: logs the full cause at `repo` level and returns a `RepoException` whose message is `Could not <action>: <friendly reason>`. The original exception is kept as the cause, so the actionable one-liner is what commands display while the JGit detail stays in the game log.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `action` | `String` | Lower-case verb phrase such as `push to origin`, describing what failed. |
| `cause` | `Exception` | Underlying `IOException`, `GitAPIException` or transport failure. |

### Returns
[`RepoException`](RepoException.md) — user-facing exception wrapping `cause`; call sites throw it.

---

## friendlyMessage

### Signature
```java
private static String friendlyMessage(Exception cause)
```

### Description
Maps common transport failures to short actionable sentences: authentication, 401 and 403 to a credentials hint; unknown host, connection refused, timed out and unreachable network to a connectivity hint; not found to a missing-repository message. Everything else, including a `null` message, falls back to the raw message or the exception's simple class name.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `cause` | `Exception` | Failure to summarise. |

### Returns
`String` — short reason suitable for command output.

---

## firstLine

### Signature
```java
private static String firstLine(String message)
```

### Description
Returns the commit subject: the text before the first newline, trimmed, or an empty string for `null`.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `message` | `String` | Full commit message, possibly multi-line or `null`. |

### Returns
`String` — first line without surrounding whitespace.

---

## close

### Signature
```java
@Override
public void close()
```

### Description
Closes the JGit `Git` handle (which also closes its `Repository`) and clears both fields under the lock; calling it twice is safe. The service stays reusable afterwards because `ensureOpenLocked` reopens on demand, and `RepoManager` relies on this when worlds reload.

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
Immutable result of a push, used by `/git push` to report how many refs moved and whether there was anything to send.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `remote` | `String` | Remote that was pushed to. |
| `branch` | `String` | Local branch, which is also the remote branch name. |
| `updatedRefs` | `int` | Number of remote ref updates reported `OK`. |
| `upToDate` | `boolean` | `true` when the remote already matched or no ref was updated. |

### Returns
`record` — immutable `PushSummary` carrying the fields above.

---

## FetchSummary

### Signature
```java
public record FetchSummary(String remote, int updatedRefs, String messages)
```

### Description
Immutable result of a fetch, used by `/git fetch` to report how many remote-tracking refs changed and to surface server messages.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `remote` | `String` | Remote that was fetched. |
| `updatedRefs` | `int` | Number of tracking refs updated by the fetch. |
| `messages` | `String` | JGit/server message text, possibly empty or `null`. |

### Returns
`record` — immutable `FetchSummary` carrying the fields above.

---

## See Also

- **Software Detailed Design:** [RepoService_SDD.md](./RepoService_SDD.md)
