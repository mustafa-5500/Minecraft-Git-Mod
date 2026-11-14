# Type — Software Detailed Design

> **API Documentation:** [Type.md](./Type.md)  
> **Source File:** [PullPlan.java](../../../../../../../src/main/java/org/almond/minecraftgit/repo/PullPlan.java)

## Table of Contents

- [Usage](#usage)
- [1. Overview](#1-overview)
- [2. Package Declaration & Imports](#2-package-declaration-imports)
- [3. Class Declaration](#3-class-declaration)
- [4. `PullPlan()`](#4-pullplan)
- [5. `canFastForward()`](#5-canfastforward)

## Usage

Read by `PullCommands` after `planPull` returns: `UP_TO_DATE` short-circuits the command, `FAST_FORWARD` triggers applying the remote snapshot to the world followed by `RepoService.fastForward`, and `DIVERGED` aborts with a message instead of writing anything. The enum lives inside `PullPlan` so a plan cannot exist without its classification.

```java
if (plan.type() == PullPlan.Type.DIVERGED) {
    throw new RepoException("Local and remote histories have diverged; resolve manually.");
}
```

---

## 1. Overview

`PullPlan.Type` describes the relationship between the local branch and its remote counterpart after a fetch, using JGit's ancestry walk. The three states map directly to the three possible pull outcomes: nothing to do, a safe pointer move, or a conflict the mod refuses to automate. A pristine bootstrap commit is deliberately reported as `FAST_FORWARD` even though the histories are technically unrelated, so that pulling a build into a freshly initialized world works. `PullPlan` pairs the enum with the commit ids and merge base so the decision and the data it was made from travel together.

---

## 2. Package Declaration & Imports

```java
package org.almond.minecraftgit.repo;
```

```java
import org.eclipse.jgit.lib.ObjectId;
```

| Import | Purpose |
|--------|---------|
| `ObjectId` | Type of the `localHead`, `remoteHead` and `mergeBase` components of the enclosing `PullPlan` record. |

---

## 3. Class Declaration

```java
public enum Type {
```

Nested inside the `PullPlan` record and implicitly `static`; the enum constants are the complete classification vocabulary, and because the type is nested, callers refer to `PullPlan.Type`. It carries no fields or methods of its own — the only behaviour (`canFastForward`) lives on the enclosing record.

---

## 4. `PullPlan()`

```java
public record PullPlan(String remote, String branch, ObjectId localHead, ObjectId remoteHead,
                       ObjectId mergeBase, Type type, int fetchedRefs)
```

A fetch plus the relationship between local and remote branches. The caller
may apply the remote snapshots to the world before `RepoService.fastForward`
moves the local branch, so a failed world write never leaves Git ahead of the
world.

---

## 5. `canFastForward()`

```java
public boolean canFastForward()
```

The single behavioural method on the record: `return type == Type.FAST_FORWARD;`. It keeps callers from comparing enum constants directly and gives `RepoService.fastForward` a single guard to re-check before performing a hard reset to `remoteHead`. `null` types cannot occur because `planPull` always assigns a constant.

---
