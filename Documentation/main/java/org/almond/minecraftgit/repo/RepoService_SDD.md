# RepoService — Software Detailed Design

> **API Documentation:** [RepoService.md](./RepoService.md)  
> **Source File:** [RepoService.java](../../../../../../../src/main/java/org/almond/minecraftgit/repo/RepoService.java)

## Table of Contents

- [Usage](#usage)
- [1. Overview](#1-overview)
- [2. Package Declaration & Imports](#2-package-declaration-imports)
- [3. Class Declaration](#3-class-declaration)
- [4. Instance Fields](#4-instance-fields)
- [5. Constructor](#5-constructor)
- [6. `path()`](#6-path)
- [7. `isInitialized()`](#7-isinitialized)
- [8. `init()`](#8-init)
- [9. `open()`](#9-open)
- [10. `ensureOpenLocked()`](#10-ensureopenlocked)
- [11. `writeBootstrapFiles()`](#11-writebootstrapfiles)
- [12. `info()`](#12-info)
- [13. `infoLocked()`](#13-infolocked)
- [14. `currentBranch()`](#14-currentbranch)
- [15. `currentBranchLocked()`](#15-currentbranchlocked)
- [16. `headId()`](#16-headid)
- [17. `commitAll()`](#17-commitall)
- [18. `commitEmpty()`](#18-commitempty)
- [19. `commitWorkspace()`](#19-commitworkspace)
- [20. `safeResolve()`](#20-saferesolve)
- [21. `deleteRecursively()`](#21-deleterecursively)
- [22. `stageAllLocked()`](#22-stagealllocked)
- [23. `statusLocked()`](#23-statuslocked)
- [24. `headCommit()`](#24-headcommit)
- [25. `log()`](#25-log)
- [26. `setAuthor()`](#26-setauthor)
- [27. `authorName()`](#27-authorname)
- [28. `authorEmail()`](#28-authoremail)
- [29. `configureAuthorLocked()`](#29-configureauthorlocked)
- [30. `identityLocked()`](#30-identitylocked)
- [31. `remoteAdd()`](#31-remoteadd)
- [32. `remoteRemove()`](#32-remoteremove)
- [33. `remoteList()`](#33-remotelist)
- [34. `remote()`](#34-remote)
- [35. `fetch()`](#35-fetch)
- [36. `fetch()`](#36-fetch)
- [37. `fetchLocked()`](#37-fetchlocked)
- [38. `push()`](#38-push)
- [39. `push()`](#39-push)
- [40. `planPull()`](#40-planpull)
- [41. `fastForward()`](#41-fastforward)
- [42. `isPristineInitLocked()`](#42-ispristineinitlocked)
- [43. `headObject()`](#43-headobject)
- [44. `resolve()`](#44-resolve)
- [45. `listSelectionsAt()`](#45-listselectionsat)
- [46. `listSelectionsAtLocked()`](#46-listselectionsatlocked)
- [47. `exportSelectionAt()`](#47-exportselectionat)
- [48. `requireRemoteLocked()`](#48-requireremotelocked)
- [49. `requireValidRemoteName()`](#49-requirevalidremotename)
- [50. `toUri()`](#50-touri)
- [51. `error()`](#51-error)
- [52. `friendlyMessage()`](#52-friendlymessage)
- [53. `firstLine()`](#53-firstline)
- [54. `close()`](#54-close)
- [55. `PushSummary()`](#55-pushsummary)
- [56. `FetchSummary()`](#56-fetchsummary)

## Usage

`RepoManager` creates one `RepoService` per world id and closes it when worlds reload, so commands never construct it directly; the example below mirrors what `/git pull` does on the async executor.

```java
RepoService repo = repos.service(world.id());
PullPlan plan = repo.planPull("origin", RepoProgress.NOOP);
if (plan.canFastForward()) {
    for (String name : repo.listSelectionsAt(plan.remoteHead())) {
        repo.exportSelectionAt(plan.remoteHead(), name, stagingDir.resolve(name));
    }
    repo.fastForward(plan);   // only after the world apply succeeded
}
```

---

## 1. Overview

Facade over JGit for one repository. All public methods are serialized by a
lock because a JGit `Repository` is not safe for concurrent mutation.
The world is never touched here; repository I/O is pure file/network work.

---

## 2. Package Declaration & Imports

```java
package org.almond.minecraftgit.repo;
```

```java
import org.almond.minecraftgit.debug.DebugLogger;
import org.almond.minecraftgit.util.Names;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.FileMode;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.RemoteRefUpdate;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.treewalk.TreeWalk;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;
```

| Import | Purpose |
|--------|---------|
| [`DebugLogger`](../debug/DebugLogger.md) | Records open/init/commit/fetch/push outcomes and full failure causes under the `repo` category. |
| [`Names`](../util/Names.md) | Validates selection directory names when listing a commit tree and when exporting one selection. |
| `Git` | Main JGit façade: init/open/add/commit/status/log/fetch/push/reset and remote management. |
| `ResetCommand` | Supplies `ResetType.HARD`, used by `fastForward` to move `HEAD` and the worktree onto the remote commit. |
| `Status` | Return type of `git.status()`, used for no-op checks and the `RepoInfo.clean` flag. |
| `GitAPIException` | Common supertype of JGit command failures, caught and translated into `RepoException`. |
| `NoHeadException` | Thrown by `git.log()` on an unborn branch, where an empty history is returned instead. |
| `TransportException` | Marks network/authentication failures so `friendlyMessage` can produce an actionable hint. |
| `Constants` | Supplies `HEAD` and `DOT_GIT` instead of hard-coded literals. |
| `FileMode` | Filters tree entries to regular and executable files during export. |
| `ObjectId` | Commit and ref hashes used in method signatures and tree lookups. |
| `ObjectLoader` | Loads blob bytes from the object database when exporting a snapshot. |
| `PersonIdent` | Carries the author/committer identity and timestamp of a new commit. |
| `Ref` | Imported for JGit ref-typed helpers; the current code reads branch state through `Repository.resolve` and `Repository.getBranch` instead. |
| `Repository` | Underlying object, config and ref access behind the `Git` façade. |
| `StoredConfig` | Reads and writes the `user.*`, `remote.*` and `branch.*` sections of `.git/config`. |
| `RevCommit` | Parsed commit providing message, author, tree and ancestry links. |
| `RevWalk` | Parses commits, tests ancestry and computes the merge base. |
| `RevFilter` | `MERGE_BASE` filter used to classify local versus remote histories. |
| `FetchResult` | Fetch outcome: tracking-ref updates and server messages. |
| `PushResult` | Per-ref push statuses inspected to count updates and collect rejections. |
| `RefSpec` | Explicit fetch and push refspecs, so behaviour does not depend on stored config. |
| `RemoteConfig` | Configured remote entries and their URIs for `remoteList`. |
| `RemoteRefUpdate` | Individual remote ref status (`OK`, `UP_TO_DATE`, rejected) and its message. |
| `URIish` | Parsed remote location accepted by `git.remoteAdd`. |
| `TreeWalk` | Recursive tree traversal for selection listing, export and the pristine-init check. |
| `File` | Converts local path strings to `file:` URIs in `toUri`. |
| `IOException` | NIO/config failure type, caught and translated by `error`. |
| `URISyntaxException` | Invalid URL reported when constructing `URIish` in `remoteAdd`. |
| `StandardCharsets` | UTF-8 encoding for the bootstrap README and `.gitattributes`. |
| `Files` | Directory creation, existence checks, file reads/writes and deletion. |
| `Path` | Repository and target paths; `normalize` and `startsWith` underpin containment checks. |
| `ArrayList` | Mutable accumulators for log rows, remotes and push rejection messages. |
| `Collection` | Batch input for the directories removed by `commitWorkspace`. |
| `Comparator` | `reverseOrder()` for child-before-parent deletion. |
| `LinkedHashSet` | Order-preserving, duplicate-free selection names. |
| `List` | Return/parameter type for commit and remote collections and refspec lists. |
| `Locale` | `Locale.ROOT` lower-casing in `friendlyMessage`. |
| `Map` | File map for `commitWorkspace` and config value groups. |
| `Optional` | Absent results (`HEAD`, revision lookups) without exceptions. |
| `Set` | Result type of `listSelectionsAt`. |
| `ReentrantLock` | Serializes all JGit access; reentrant so public methods may call nested locked helpers. |
| `Stream` | `Files.walk` stream for recursive deletion. |

---

## 3. Class Declaration

```java
public final class RepoService implements AutoCloseable {
```

- **`implements AutoCloseable`** — Enables try-with-resources-based closing of the JGit handles; `close()` is idempotent and reopens lazily, and the class stays `final` because it holds mutable handles plus a lock and is not designed for extension.

---

## 4. Instance Fields

```java
private final Path repoPath;
private final RepoOptions options;
private final DebugLogger debug;
private final ReentrantLock lock;
private Git git;
private Repository repository;
private String authorName;
private String authorEmail;
```

| Field | Type | Description |
|-------|------|-------------|
| `repoPath` | `Path` | Absolute, normalized worktree root; the anchor for every containment test. |
| `options` | [`RepoOptions`](RepoOptions.md) | Immutable defaults (branch, default remote, fallback identity) captured once at construction. |
| `debug` | [`DebugLogger`](../debug/DebugLogger.md) | Diagnostic sink for open/init/commit/sync events and full failure causes; nothing logged here reaches the user directly. |
| `lock` | `ReentrantLock` | Single reentrant mutex around every JGit access; `*Locked` helpers assume it is held. |
| `git` | `Git` | JGit façade, opened lazily and closed explicitly; `null` while the repository is closed. |
| `repository` | `Repository` | Repository handle paired with `git`, used for config, ref and tree reads. |
| `authorName` | `String` | In-memory commit identity name, kept in sync with `user.name`. |
| `authorEmail` | `String` | In-memory commit identity email, kept in sync with `user.email`. |

---

## 5. Constructor

```java
public RepoService(Path repoPath, RepoOptions options, DebugLogger debug)
```

Normalizes the path with `toAbsolutePath().normalize()` before anything else, so later `startsWith(repoPath)` checks cannot be fooled by `..` segments. It captures the `RepoOptions` defaults and seeds the author fields, then returns without touching the filesystem or JGit — `init()` and `open()` remain the only places where the repository appears.

---

## 6. `path()`

```java
public Path path()
```

Pure accessor for the normalized root; since it performs no existence check, callers pair it with `isInitialized()` to distinguish a configured path from a created repository.

---

## 7. `isInitialized()`

```java
public boolean isInitialized()
```

Uses only `Files.isDirectory(<root>/.git)`, so it stays lock-free and never opens JGit; a bare repository or an unrelated directory reads as uninitialized. `RepoManager.isInitialized(worldId)` mirrors this check for services that have not been created yet.

---

## 8. `init()`

```java
public RepoInfo init()
```

On a fresh directory it creates the worktree, runs `Git.init` on `options.branch()`, saves the identity, writes the bootstrap files, stages everything and commits them, so a new world immediately has a valid `HEAD`; the bootstrap subject is exactly the prefix `isPristineInitLocked` later recognises. On an existing repository it only opens and returns `infoLocked()`, which makes the method idempotent for `/git init`.

---

## 9. `open()`

```java
public void open()
```

Locked one-liner over `ensureOpenLocked`, used when a caller only needs the handles open (for example after metadata or identity changes) without reading repository state.

---

## 10. `ensureOpenLocked()`

```java
private void ensureOpenLocked()
```

Lazy-open guard used by every public method: it short-circuits when both handles exist, otherwise checks `isInitialized()` and opens via `Git.open`, re-applies the author identity and logs. It is the single place that produces the "No Git repository exists … Run /git init first." message, and it assumes the lock is already held.

---

## 11. `writeBootstrapFiles()`

```java
private void writeBootstrapFiles()
```

Creates the bootstrap files with `Files.writeString(..., UTF_8)` only when each file is absent, so a fresh directory gets deterministic content and an existing README is never overwritten. The `.gitattributes` LF rules keep CSV and JSON snapshots stable across platforms.

---

## 12. `info()`

```java
public RepoInfo info()
```

Locking wrapper that guarantees an open repository and then delegates to `infoLocked()`; it backs `/git repo` and is safe to call while other repository operations are running.

---

## 13. `infoLocked()`

```java
private RepoInfo infoLocked()
```

Resolves `HEAD` once and reuses it for the id, the parsed short message and the `hasCommits` flag, then reads the branch remote from `StoredConfig`, falling back to `options.defaultRemote()` and an empty URL. The final field is a live `statusLocked()` clean check, which is why `info()` can be comparatively expensive on large selections.

---

## 14. `currentBranch()`

```java
public String currentBranch()
```

Locked public wrapper; the name is used as the push and pull ref component, and an empty result tells `push` and `planPull` to refuse with a detached-`HEAD` message.

---

## 15. `currentBranchLocked()`

```java
private String currentBranchLocked()
```

Reads `repository.getBranch()` and converts JGit's detached-`HEAD` answer (a 40-hex commit id) back to an empty string, which callers treat as "no branch to push or pull". It requires the lock and translates `IOException` into `RepoException`.

---

## 16. `headId()`

```java
public Optional<String> headId()
```

Resolves `Constants.HEAD` directly rather than walking history; it is the cheap "do we have a commit?" probe used before operations that need a HEAD, and it returns empty for an unborn branch.

---

## 17. `commitAll()`

```java
public String commitAll(String message, boolean allowEmpty)
```

Stages with `stageAllLocked()`, asks JGit for a status and returns `null` when the tree is clean unless `allowEmpty` is set, then commits with the configured identity and `setAllowEmpty`, returning the full id. Everything happens under one lock, so no other call can stage between the status check and the commit.

---

## 18. `commitEmpty()`

```java
public String commitEmpty(String message)
```

Delegates to `commitAll(message, true)`; JGit's `setAllowEmpty(true)` produces a commit that changes no trees, which is enough to exercise remote authentication and push permissions without touching snapshot data.

---

## 19. `commitWorkspace()`

```java
public String commitWorkspace(Map<String, byte[]> files, Collection<String> directoriesToReplace,
                                  String message, boolean allowEmpty)
```

Under one lock it removes each `directoriesToReplace` entry, writes the supplied file bytes, stages everything and commits, so other repository operations never observe a half-written workspace. Path validation happens per entry through `safeResolve`, but the filesystem writes themselves are not transactional: a crash mid-write leaves the worktree dirty, while the index and commit advance only on success.

---

## 20. `safeResolve()`

```java
private Path safeResolve(String relative)
```

Resolves the candidate against the root, normalizes it to collapse `.` and `..`, then rejects anything that is not a strict descendant of the root — the equality check stops a caller from deleting the repository itself. This is a lexical check only: symlinks inside the repository are not resolved.

---

## 21. `deleteRecursively()`

```java
private static void deleteRecursively(Path path)
```

Walks the path with `Files.walk`, sorts entries in reverse order so children precede parents, and deletes each with `deleteIfExists`; a missing path returns early. It serves both replaced selection directories and the export target.

---

## 22. `stageAllLocked()`

```java
private void stageAllLocked()
```

Two `git.add()` passes are required: the plain `addFilepattern(".")` never registers removals, so a second `setUpdate(true)` pass stages the deletions left by `commitWorkspace`'s directory replacement. Failures are rethrown through `error("stage files", ...)`.

---

## 23. `statusLocked()`

```java
private Status statusLocked()
```

Thin `git.status().call()` wrapper that keeps the lock and checked-exception discipline explicit: it declares `GitAPIException` so the calling method decides whether a failure is fatal (as in `info`) or a no-op signal (as in `commitAll`).

---

## 24. `headCommit()`

```java
public Optional<CommitInfo> headCommit()
```

Expressed as `log(1).stream().findFirst()` so it reuses the same mapping and empty-history handling as the log command instead of duplicating JGit calls.

---

## 25. `log()`

```java
public List<CommitInfo> log(int limit)
```

Guards the unborn-branch case before calling JGit (both an unresolved `HEAD` and `NoHeadException` return an empty list), clamps the limit to at least 1, and maps each `RevCommit` to a `CommitInfo`: full and 8-character ids, author falling back to committer, `Instant.EPOCH` when no identity exists and the subject from `firstLine`. Walk order is newest first, matching `/git log`.

---

## 26. `setAuthor()`

```java
public void setAuthor(String name, String email)
```

Validates both fields before mutating anything, so a rejected update leaves the previous identity intact. The config write is skipped when no repository exists yet and is re-applied on the next open or init; this is the hook used by `/git config author` and by `RepoManager.init` when adopting the player's name.

---

## 27. `authorName()`

```java
public String authorName()
```

Plain read of the in-memory field, deliberately lock-free because `setAuthor` replaces it atomically; the authoritative copy still lives in `.git/config`.

---

## 28. `authorEmail()`

```java
public String authorEmail()
```

Plain read of the in-memory field, written together with the name by `configureAuthorLocked`.

---

## 29. `configureAuthorLocked()`

```java
private void configureAuthorLocked()
```

Sets `user.name` and `user.email` on the cached `StoredConfig` and calls `save()`, so plain Git tooling sees the same identity as the mod. Invoked from `ensureOpenLocked`, from `init` before the bootstrap commit and from `setAuthor`; it requires the lock and an open repository.

---

## 30. `identityLocked()`

```java
private PersonIdent identityLocked()
```

Creates a new `PersonIdent` per commit instead of caching one, so the committer timestamp reflects the moment of the commit while the name and email always come from the validated fields.

---

## 31. `remoteAdd()`

```java
public void remoteAdd(String name, String url)
```

Validates the name, rejects a blank URL, converts local paths with `toUri`, then adds the remote through JGit and repairs a missing fetch refspec (`+refs/heads/*:refs/remotes/<name>/*`) in the stored config. Without that refspec later fetches would not update the tracking branch that `planPull` resolves.

---

## 32. `remoteRemove()`

```java
public void remoteRemove(String name)
```

Checks the name grammar and lets JGit drop the remote section; remote-tracking refs already fetched under `refs/remotes/<name>` are not pruned, they simply stop being updated.

---

## 33. `remoteList()`

```java
public List<RemoteInfo> remoteList()
```

Maps every `RemoteConfig` to a `RemoteInfo`, taking the first URI and tolerating remotes with none. Ordering follows the config file, which keeps `/git remote` output stable.

---

## 34. `remote()`

```java
public Optional<RemoteInfo> remote(String name)
```

Client-side filter over `remoteList()`; using the list call means the lookup inherits locking and error translation and there is no second config reader.

---

## 35. `fetch()`

```java
public FetchSummary fetch(String remote)
```

Default overload that supplies `RepoProgress.NOOP`, keeping call sites that do not display progress short.

---

## 36. `fetch()`

```java
public FetchSummary fetch(String remote, RepoProgress progress)
```

Locked fetch that converts `FetchResult` into a `FetchSummary` (remote name, number of tracking-ref updates, server messages). Only `refs/remotes/<remote>` changes; the worktree and local branches stay as they are.

---

## 37. `fetchLocked()`

```java
private FetchResult fetchLocked(String remote, RepoProgress progress)
```

Performs the actual fetch after validating the name and confirming a URL exists, using an explicit refspec rather than stored config, `setRemoveDeletedRefs(true)` to drop deleted remote branches and `JGitProgressMonitor.of(progress)` so `NOOP` becomes JGit's null monitor. It throws `GitAPIException` for the caller to translate.

---

## 38. `push()`

```java
public PushSummary push(String remote, boolean force)
```

Default overload supplying `RepoProgress.NOOP`.

---

## 39. `push()`

```java
public PushSummary push(String remote, boolean force, RepoProgress progress)
```

Validates remote, branch and HEAD before touching the network (a detached `HEAD` and an unborn branch each get a targeted message), pushes the current branch to the same name with the `force` flag, then walks every `RemoteRefUpdate` to count `OK`s, note `UP_TO_DATE` and collect rejection messages into one actionable `RepoException`. On success it writes `branch.<branch>.remote` and `merge`, establishing the upstream used by later pulls.

---

## 40. `planPull()`

```java
public PullPlan planPull(String remote, RepoProgress progress)
```

The fetch runs inside the same lock as the classification, so the tracking ref it compares cannot move underneath it; classification order is detached `HEAD`, missing remote branch, no local commit (fast-forward from nothing), equal heads, local ancestor of remote, remote ancestor of local, pristine bootstrap commit and finally `DIVERGED`, with the merge base from a `MERGE_BASE` `RevWalk` that may be `null` for unrelated histories. Nothing is written — no ref, index or worktree change — so the caller can apply the world snapshot first and only then call `fastForward`, which is why a failed world write never leaves Git ahead of the world.

---

## 41. `fastForward()`

```java
public boolean fastForward(PullPlan plan)
```

Returns `false` without side effects for `UP_TO_DATE` and `DIVERGED` plans, re-checks that the checked-out branch is still the one that was planned, then performs `git.reset().setMode(HARD).setRef(plan.remoteHead())` to move `HEAD` and the worktree. Because it is a hard reset it discards local worktree changes, which is safe only because the plan was classified as a fast-forward and the caller has already exported the snapshot.

---

## 42. `isPristineInitLocked()`

```java
private boolean isPristineInitLocked(RevCommit commit)
```

Detects the scaffold commit created by `init()`: the message prefix `Initialize MinecraftGit repository` plus a recursive tree containing only `README.md` and `.gitattributes`. `planPull` uses it to treat a freshly initialized world's unrelated history as an unborn branch, so a build can be pulled without a diverged-history error.

---

## 43. `headObject()`

```java
public Optional<ObjectId> headObject()
```

Locked wrapper around `repository.resolve(HEAD)` returning the raw `ObjectId`; status, pull and checkout need the object rather than the hex string to walk trees.

---

## 44. `resolve()`

```java
public Optional<ObjectId> resolve(String revision)
```

Generic revision resolution used for checkout targets and diagnostics; a `null` result from JGit becomes `Optional.empty()`, while `IOException` is translated by `error`.

---

## 45. `listSelectionsAt()`

```java
public Set<String> listSelectionsAt(ObjectId commitId)
```

Locked entry point that delegates to the tree walk; `PullService` uses it to discover which selections a remote commit contains before preparing an apply.

---

## 46. `listSelectionsAtLocked()`

```java
private Set<String> listSelectionsAtLocked(ObjectId commitId)
```

Recursive `TreeWalk` over the commit tree: for each entry it takes the substring before the first `/`, filters it through `Names.isValidSelectionName` and accumulates into a `LinkedHashSet`. That filter is what excludes repository-level files such as `README.md` and `.gitattributes` from the selection list.

---

## 47. `exportSelectionAt()`

```java
public boolean exportSelectionAt(ObjectId commitId, String selection, Path target)
```

The selection name is re-validated with `Names.requireSelectionName` (an `IllegalArgumentException` becomes a `RepoException`), the target directory is deleted and recreated, and only `REGULAR_FILE`/`EXECUTABLE_FILE` entries under `<selection>/` are written. Each output path is normalized and checked to stay inside the target, so a crafted tree cannot write outside it; the method returns `false` when the commit has no files for that selection, which callers treat as an empty selection. The repository worktree and index are untouched, which is exactly what lets pull apply the world before `fastForward`.

---

## 48. `requireRemoteLocked()`

```java
private void requireRemoteLocked(String name)
```

Reads `remote.<name>.url` and fails with a message naming the exact `/git remote add` command to run; it turns a configuration mistake into user guidance instead of a transport error.

---

## 49. `requireValidRemoteName()`

```java
private static void requireValidRemoteName(String name)
```

Static grammar check (`[A-Za-z0-9][A-Za-z0-9_.-]{0,63}`) applied before any config or refspec construction, so an invalid name can never become a config subsection or ref path component.

---

## 50. `toUri()`

```java
private static URIish toUri(String url)
```

Detects local filesystem forms (drive letter with separator, UNC path, absolute `/`, `./`, `../`) and converts them through `File.toURI()`; otherwise the string is parsed as a URL. This works around JGit treating a drive-letter path as a URI with a one-letter scheme.

---

## 51. `error()`

```java
private RepoException error(String action, Exception cause)
```

Builds the user-facing message `Could not <action>: <friendly reason>`, logs the complete cause through `DebugLogger` and returns the `RepoException` that the caller throws. The cause is preserved, so the game log keeps the JGit detail while chat shows one actionable line.

---

## 52. `friendlyMessage()`

```java
private static String friendlyMessage(Exception cause)
```

Normalises transport failures into three cases — credentials, unreachable host and missing repository — by inspecting `TransportException` or the word "transport" plus keywords such as `not authorized`, `unknown host` or `not found`. Non-transport failures fall back to the raw message, or the exception's simple class name when the message is blank.

---

## 53. `firstLine()`

```java
private static String firstLine(String message)
```

Null-safe subject extraction used for debug lines and `CommitInfo.message`, so a multi-line commit message never breaks one-line log output.

---

## 54. `close()`

```java
@Override
public void close()
```

Closes the `Git` handle (which closes its `Repository`), logs the event and clears both fields under the lock; it is idempotent and leaves the service reusable because the next call reopens through `ensureOpenLocked`. `RepoManager` calls it on reload and at shutdown.

---

## 55. `PushSummary()`

```java
public record PushSummary(String remote, String branch, int updatedRefs, boolean upToDate)
```

Record returned by both `push` overloads; commands read `updatedRefs()` and `upToDate()` to choose between "pushed N refs" and "already up to date" messaging. `upToDate` is computed as `upToDate || updated == 0`, so an empty update list also counts as up to date.

---

## 56. `FetchSummary()`

```java
public record FetchSummary(String remote, int updatedRefs, String messages)
```

Record returned by both `fetch` overloads; `updatedRefs()` counts tracking-ref changes (zero when already current) and `messages` carries JGit's transport text for display. It is a plain immutable data carrier with no validation.

---
