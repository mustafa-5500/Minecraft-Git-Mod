# PullService — Software Detailed Design

> **API Documentation:** [PullService.md](./PullService.md)  
> **Source File:** [PullService.java](../../../../../../../src/main/java/org/almond/minecraftgit/sync/PullService.java)

## Table of Contents

- [Usage](#usage)
- [1. Overview](#1-overview)
- [2. Package Declaration & Imports](#2-package-declaration-imports)
- [3. Class Declaration](#3-class-declaration)
- [4. Instance Fields](#4-instance-fields)
- [5. Constructor](#5-constructor)
- [6. `prepare()`](#6-prepare)
- [7. `prepareCheckout()`](#7-preparecheckout)
- [8. `cleanup()`](#8-cleanup)
- [9. `cleanup()`](#9-cleanup)
- [10. `shortId()`](#10-shortid)
- [11. `PreparedPull()`](#11-preparedpull)
- [12. `PreparedCheckout()`](#12-preparedcheckout)

## Usage

TODO: Describe how `PullService` is instantiated and used.

---

## 1. Overview

Reads remote commit trees and local HEAD state into snapshots that the apply
service can turn into world writes. All filesystem/repository work here runs
off the client thread; only selection metadata and world writes happen later.

---

## 2. Package Declaration & Imports

```java
package org.almond.minecraftgit.sync;
```

```java
import org.almond.minecraftgit.model.BlockPos;
import org.almond.minecraftgit.model.Selection;
import org.almond.minecraftgit.model.Snapshot;
import org.almond.minecraftgit.repo.CommitMeta;
import org.almond.minecraftgit.repo.PullPlan;
import org.almond.minecraftgit.repo.RepoService;
import org.almond.minecraftgit.serialization.QuadrantCsvReader;
import org.eclipse.jgit.lib.ObjectId;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
```

| Import | Purpose |
|--------|---------|
| [`BlockPos`](../model/BlockPos.md) | TODO: describe purpose |
| [`Selection`](../model/Selection.md) | TODO: describe purpose |
| [`Snapshot`](../model/Snapshot.md) | TODO: describe purpose |
| [`CommitMeta`](../repo/CommitMeta.md) | TODO: describe purpose |
| [`PullPlan`](../repo/PullPlan.md) | TODO: describe purpose |
| [`RepoService`](../repo/RepoService.md) | TODO: describe purpose |
| [`QuadrantCsvReader`](../serialization/QuadrantCsvReader.md) | TODO: describe purpose |
| `ObjectId` | TODO: describe purpose |
| `IOException` | TODO: describe purpose |
| `Files` | TODO: describe purpose |
| `Path` | TODO: describe purpose |
| `ArrayList` | TODO: describe purpose |
| `Comparator` | TODO: describe purpose |
| `List` | TODO: describe purpose |

---

## 3. Class Declaration

```java
public final class PullService {
```


---

## 4. Instance Fields

```java
private final Path tmpRoot;
```

| Field | Type | Description |
|-------|------|-------------|
| `tmpRoot` | `Path` | TODO: describe |

---

## 5. Constructor

```java
public PullService(Path tmpRoot)
```

TODO: Provide detailed design explanation for `PullService`.

---

## 6. `prepare()`

```java
public List<PreparedPull> prepare(String worldId, List<Selection> localSelections, BlockPos playerOrigin,
                                      RepoService repo, PullPlan plan, String onlySelection)
```

TODO: Provide detailed design explanation for `prepare`.

---

## 7. `prepareCheckout()`

```java
public PreparedCheckout prepareCheckout(Selection selection, RepoService repo, ObjectId commit)
```

TODO: Provide detailed design explanation for `prepareCheckout`.

---

## 8. `cleanup()`

```java
public void cleanup(Path root)
```

TODO: Provide detailed design explanation for `cleanup`.

---

## 9. `cleanup()`

```java
public void cleanup(List<PreparedPull> prepared)
```

TODO: Provide detailed design explanation for `cleanup`.

---

## 10. `shortId()`

```java
private static String shortId(ObjectId id)
```

TODO: Provide detailed design explanation for `shortId`.

---

## 11. `PreparedPull()`

```java
public record PreparedPull(String name, Snapshot target, CommitMeta meta, Selection localSelection,
                               BlockPos localOrigin, Snapshot localHead, BlockPos localHeadOrigin,
                               Path tempDirectory)
```

TODO: Provide detailed design explanation for `PreparedPull`.

---

## 12. `PreparedCheckout()`

```java
public record PreparedCheckout(Snapshot target, CommitMeta meta, Snapshot localHead,
                                   BlockPos localHeadOrigin, Path tempDirectory)
```

TODO: Provide detailed design explanation for `PreparedCheckout`.

---
