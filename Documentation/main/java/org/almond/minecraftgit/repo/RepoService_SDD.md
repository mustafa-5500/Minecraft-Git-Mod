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

TODO: Describe how `RepoService` is instantiated and used.

---

## 1. Overview

Facade over JGit for one repository. All public methods are serialized by a
lock because a JGit {@link Repository} is not safe for concurrent mutation.
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
| [`DebugLogger`](../debug/DebugLogger.md) | TODO: describe purpose |
| [`Names`](../util/Names.md) | TODO: describe purpose |
| `Git` | TODO: describe purpose |
| `ResetCommand` | TODO: describe purpose |
| `Status` | TODO: describe purpose |
| `GitAPIException` | TODO: describe purpose |
| `NoHeadException` | TODO: describe purpose |
| `TransportException` | TODO: describe purpose |
| `Constants` | TODO: describe purpose |
| `FileMode` | TODO: describe purpose |
| `ObjectId` | TODO: describe purpose |
| `ObjectLoader` | TODO: describe purpose |
| `PersonIdent` | TODO: describe purpose |
| `Ref` | TODO: describe purpose |
| `Repository` | TODO: describe purpose |
| `StoredConfig` | TODO: describe purpose |
| `RevCommit` | TODO: describe purpose |
| `RevWalk` | TODO: describe purpose |
| `RevFilter` | TODO: describe purpose |
| `FetchResult` | TODO: describe purpose |
| `PushResult` | TODO: describe purpose |
| `RefSpec` | TODO: describe purpose |
| `RemoteConfig` | TODO: describe purpose |
| `RemoteRefUpdate` | TODO: describe purpose |
| `URIish` | TODO: describe purpose |
| `TreeWalk` | TODO: describe purpose |
| `File` | TODO: describe purpose |
| `IOException` | TODO: describe purpose |
| `URISyntaxException` | TODO: describe purpose |
| `StandardCharsets` | TODO: describe purpose |
| `Files` | TODO: describe purpose |
| `Path` | TODO: describe purpose |
| `ArrayList` | TODO: describe purpose |
| `Collection` | TODO: describe purpose |
| `Comparator` | TODO: describe purpose |
| `LinkedHashSet` | TODO: describe purpose |
| `List` | TODO: describe purpose |
| `Locale` | TODO: describe purpose |
| `Map` | TODO: describe purpose |
| `Optional` | TODO: describe purpose |
| `Set` | TODO: describe purpose |
| `ReentrantLock` | TODO: describe purpose |
| `Stream` | TODO: describe purpose |

---

## 3. Class Declaration

```java
public final class RepoService implements AutoCloseable {
```

- **`implements AutoCloseable`** — TODO: explain interfaces.

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
| `repoPath` | `Path` | TODO: describe |
| `options` | [`RepoOptions`](RepoOptions.md) | TODO: describe |
| `debug` | [`DebugLogger`](../debug/DebugLogger.md) | TODO: describe |
| `lock` | `ReentrantLock` | TODO: describe |
| `git` | `Git` | TODO: describe |
| `repository` | `Repository` | TODO: describe |
| `authorName` | `String` | TODO: describe |
| `authorEmail` | `String` | TODO: describe |

---

## 5. Constructor

```java
public RepoService(Path repoPath, RepoOptions options, DebugLogger debug)
```

TODO: Provide detailed design explanation for `RepoService`.

---

## 6. `path()`

```java
public Path path()
```

TODO: Provide detailed design explanation for `path`.

---

## 7. `isInitialized()`

```java
public boolean isInitialized()
```

TODO: Provide detailed design explanation for `isInitialized`.

---

## 8. `init()`

```java
public RepoInfo init()
```

Create the repository if needed (with a README and format documentation
commit), otherwise open it.

---

## 9. `open()`

```java
public void open()
```

Open the repository if it is not already open.

---

## 10. `ensureOpenLocked()`

```java
private void ensureOpenLocked()
```

TODO: Provide detailed design explanation for `ensureOpenLocked`.

---

## 11. `writeBootstrapFiles()`

```java
private void writeBootstrapFiles()
```

TODO: Provide detailed design explanation for `writeBootstrapFiles`.

---

## 12. `info()`

```java
public RepoInfo info()
```

TODO: Provide detailed design explanation for `info`.

---

## 13. `infoLocked()`

```java
private RepoInfo infoLocked()
```

TODO: Provide detailed design explanation for `infoLocked`.

---

## 14. `currentBranch()`

```java
public String currentBranch()
```

Current branch name, or an empty string for a detached HEAD.

---

## 15. `currentBranchLocked()`

```java
private String currentBranchLocked()
```

TODO: Provide detailed design explanation for `currentBranchLocked`.

---

## 16. `headId()`

```java
public Optional<String> headId()
```

TODO: Provide detailed design explanation for `headId`.

---

## 17. `commitAll()`

```java
public String commitAll(String message, boolean allowEmpty)
```

Stage the whole working tree and commit it.

---

## 18. `commitEmpty()`

```java
public String commitEmpty(String message)
```

Create an empty commit, useful for verifying a configured remote.

---

## 19. `commitWorkspace()`

```java
public String commitWorkspace(Map<String, byte[]> files, Collection<String> directoriesToReplace,
                                  String message, boolean allowEmpty)
```

Replace whole selection directories and commit the result atomically from
the caller's perspective. Paths are relative to the repository root and
are rejected if they escape it.

---

## 20. `safeResolve()`

```java
private Path safeResolve(String relative)
```

TODO: Provide detailed design explanation for `safeResolve`.

---

## 21. `deleteRecursively()`

```java
private static void deleteRecursively(Path path)
```

TODO: Provide detailed design explanation for `deleteRecursively`.

---

## 22. `stageAllLocked()`

```java
private void stageAllLocked()
```

TODO: Provide detailed design explanation for `stageAllLocked`.

---

## 23. `statusLocked()`

```java
private Status statusLocked()
```

TODO: Provide detailed design explanation for `statusLocked`.

---

## 24. `headCommit()`

```java
public Optional<CommitInfo> headCommit()
```

TODO: Provide detailed design explanation for `headCommit`.

---

## 25. `log()`

```java
public List<CommitInfo> log(int limit)
```

TODO: Provide detailed design explanation for `log`.

---

## 26. `setAuthor()`

```java
public void setAuthor(String name, String email)
```

TODO: Provide detailed design explanation for `setAuthor`.

---

## 27. `authorName()`

```java
public String authorName()
```

TODO: Provide detailed design explanation for `authorName`.

---

## 28. `authorEmail()`

```java
public String authorEmail()
```

TODO: Provide detailed design explanation for `authorEmail`.

---

## 29. `configureAuthorLocked()`

```java
private void configureAuthorLocked()
```

TODO: Provide detailed design explanation for `configureAuthorLocked`.

---

## 30. `identityLocked()`

```java
private PersonIdent identityLocked()
```

TODO: Provide detailed design explanation for `identityLocked`.

---

## 31. `remoteAdd()`

```java
public void remoteAdd(String name, String url)
```

TODO: Provide detailed design explanation for `remoteAdd`.

---

## 32. `remoteRemove()`

```java
public void remoteRemove(String name)
```

TODO: Provide detailed design explanation for `remoteRemove`.

---

## 33. `remoteList()`

```java
public List<RemoteInfo> remoteList()
```

TODO: Provide detailed design explanation for `remoteList`.

---

## 34. `remote()`

```java
public Optional<RemoteInfo> remote(String name)
```

TODO: Provide detailed design explanation for `remote`.

---

## 35. `fetch()`

```java
public FetchSummary fetch(String remote)
```

TODO: Provide detailed design explanation for `fetch`.

---

## 36. `fetch()`

```java
public FetchSummary fetch(String remote, RepoProgress progress)
```

TODO: Provide detailed design explanation for `fetch`.

---

## 37. `fetchLocked()`

```java
private FetchResult fetchLocked(String remote, RepoProgress progress)
```

TODO: Provide detailed design explanation for `fetchLocked`.

---

## 38. `push()`

```java
public PushSummary push(String remote, boolean force)
```

TODO: Provide detailed design explanation for `push`.

---

## 39. `push()`

```java
public PushSummary push(String remote, boolean force, RepoProgress progress)
```

TODO: Provide detailed design explanation for `push`.

---

## 40. `planPull()`

```java
public PullPlan planPull(String remote, RepoProgress progress)
```

Fetch and classify the relationship between the local branch and its
remote. The caller applies the remote snapshot to the world before calling
{@link #fastForward(PullPlan)}, so a failed world write does not move Git.

---

## 41. `fastForward()`

```java
public boolean fastForward(PullPlan plan)
```

Move the local branch to the plan's remote head after a successful apply.

---

## 42. `isPristineInitLocked()`

```java
private boolean isPristineInitLocked(RevCommit commit)
```

TODO: Provide detailed design explanation for `isPristineInitLocked`.

---

## 43. `headObject()`

```java
public Optional<ObjectId> headObject()
```

TODO: Provide detailed design explanation for `headObject`.

---

## 44. `resolve()`

```java
public Optional<ObjectId> resolve(String revision)
```

TODO: Provide detailed design explanation for `resolve`.

---

## 45. `listSelectionsAt()`

```java
public Set<String> listSelectionsAt(ObjectId commitId)
```

Selection names that exist in a commit's tree.

---

## 46. `listSelectionsAtLocked()`

```java
private Set<String> listSelectionsAtLocked(ObjectId commitId)
```

TODO: Provide detailed design explanation for `listSelectionsAtLocked`.

---

## 47. `exportSelectionAt()`

```java
public boolean exportSelectionAt(ObjectId commitId, String selection, Path target)
```

Write one selection's files from a commit into {@code target}. Used by pull
and checkout to apply a snapshot without disturbing the repository worktree.

---

## 48. `requireRemoteLocked()`

```java
private void requireRemoteLocked(String name)
```

TODO: Provide detailed design explanation for `requireRemoteLocked`.

---

## 49. `requireValidRemoteName()`

```java
private static void requireValidRemoteName(String name)
```

TODO: Provide detailed design explanation for `requireValidRemoteName`.

---

## 50. `toUri()`

```java
private static URIish toUri(String url)
```

TODO: Provide detailed design explanation for `toUri`.

---

## 51. `error()`

```java
private RepoException error(String action, Exception cause)
```

TODO: Provide detailed design explanation for `error`.

---

## 52. `friendlyMessage()`

```java
private static String friendlyMessage(Exception cause)
```

TODO: Provide detailed design explanation for `friendlyMessage`.

---

## 53. `firstLine()`

```java
private static String firstLine(String message)
```

TODO: Provide detailed design explanation for `firstLine`.

---

## 54. `close()`

```java
@Override
public void close()
```

TODO: Provide detailed design explanation for `close`.

---

## 55. `PushSummary()`

```java
public record PushSummary(String remote, String branch, int updatedRefs, boolean upToDate)
```

TODO: Provide detailed design explanation for `PushSummary`.

---

## 56. `FetchSummary()`

```java
public record FetchSummary(String remote, int updatedRefs, String messages)
```

TODO: Provide detailed design explanation for `FetchSummary`.

---
