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

TODO: Describe how `Type` is instantiated and used.

---

## 1. Overview

TODO: Provide a high-level overview of `Type`.

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
| `ObjectId` | TODO: describe purpose |

---

## 3. Class Declaration

```java
public enum Type {
```


---

## 4. `PullPlan()`

```java
public record PullPlan(String remote, String branch, ObjectId localHead, ObjectId remoteHead,
                       ObjectId mergeBase, Type type, int fetchedRefs)
```

A fetch plus the relationship between local and remote branches. The caller
may apply the remote snapshots to the world before {@link RepoService#fastForward}
moves the local branch, so a failed world write never leaves Git ahead of the
world.

---

## 5. `canFastForward()`

```java
public boolean canFastForward()
```

TODO: Provide detailed design explanation for `canFastForward`.

---
