# RepoProgress — Software Detailed Design

> **API Documentation:** [RepoProgress.md](./RepoProgress.md)  
> **Source File:** [RepoProgress.java](../../../../../../../src/main/java/org/almond/minecraftgit/repo/RepoProgress.java)

## Table of Contents

- [Usage](#usage)
- [1. Overview](#1-overview)
- [2. Package Declaration & Imports](#2-package-declaration-imports)
- [3. Class Declaration](#3-class-declaration)

## Usage

Passed as the last argument to the transport methods (`fetch(remote, progress)`, `push(remote, force, progress)`, `planPull(remote, progress)`); the repository layer adapts it to JGit's `ProgressMonitor` through `JGitProgressMonitor.of`. Existing commands pass `RepoProgress.NOOP`, but an action-bar implementation can be supplied without the repo layer depending on Minecraft classes.

```java
repo.push("origin", false, (task, completed, total) -> {
    // runs on the minecraftgit-io worker thread
    latest = task + " " + completed + "/" + total;
});
```

---

## 1. Overview

Transport progress sink, called from a worker thread.

---

## 2. Package Declaration & Imports

```java
package org.almond.minecraftgit.repo;
```

No imports are required: the interface only uses `String` and `int`, keeping the repository layer independent of Minecraft and JGit UI types.

---

## 3. Class Declaration

```java
public interface RepoProgress {
```

A single-abstract-method interface (`update`), hence usable as a lambda; `done()` has a no-op default so implementations that only render a percentage need not implement it. The `NOOP` constant (a lambda with an empty body) is the canonical "no progress" instance, and `JGitProgressMonitor.of` recognises it to substitute JGit's `NullProgressMonitor`, avoiding per-update callbacks when nobody is listening.

---
