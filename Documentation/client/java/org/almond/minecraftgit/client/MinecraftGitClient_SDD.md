# MinecraftGitClient — Software Detailed Design

> **API Documentation:** [MinecraftGitClient.md](./MinecraftGitClient.md)  
> **Source File:** [MinecraftGitClient.java](../../../../../../../src/client/java/org/almond/minecraftgit/client/MinecraftGitClient.java)

## Table of Contents

- [Usage](#usage)
- [1. Overview](#1-overview)
- [2. Package Declaration & Imports](#2-package-declaration-imports)
- [3. Class Declaration](#3-class-declaration)
- [4. Instance Fields](#4-instance-fields)
- [5. `onInitializeClient()`](#5-oninitializeclient)
- [6. `services()`](#6-services)

## Usage

TODO: Describe how `MinecraftGitClient` is instantiated and used.

---

## 1. Overview

Client entrypoint. All commands, world access and rendering are client-side;
the mod is marked {@code "environment": "client"} and has no server
entrypoint.

---

## 2. Package Declaration & Imports

```java
package org.almond.minecraftgit.client;
```

```java
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import org.almond.minecraftgit.MinecraftGit;
import org.almond.minecraftgit.client.command.GitCommand;
import org.almond.minecraftgit.client.selection.SelectionRenderer;
import org.almond.minecraftgit.client.selection.WandEvents;
import org.almond.minecraftgit.config.ModConfig;
import org.almond.minecraftgit.debug.DebugLogger;
import java.nio.file.Path;
```

| Import | Purpose |
|--------|---------|
| `ClientModInitializer` | TODO: describe purpose |
| `ClientCommandRegistrationCallback` | TODO: describe purpose |
| `ClientLifecycleEvents` | TODO: describe purpose |
| `FabricLoader` | TODO: describe purpose |
| [`MinecraftGit`](../../../../../../main/java/org/almond/minecraftgit/MinecraftGit.md) | TODO: describe purpose |
| [`GitCommand`](command/GitCommand.md) | TODO: describe purpose |
| [`SelectionRenderer`](selection/SelectionRenderer.md) | TODO: describe purpose |
| [`WandEvents`](selection/WandEvents.md) | TODO: describe purpose |
| [`ModConfig`](../../../../../../main/java/org/almond/minecraftgit/config/ModConfig.md) | TODO: describe purpose |
| [`DebugLogger`](../../../../../../main/java/org/almond/minecraftgit/debug/DebugLogger.md) | TODO: describe purpose |
| `Path` | TODO: describe purpose |

---

## 3. Class Declaration

```java
public final class MinecraftGitClient implements ClientModInitializer {
```

- **`implements ClientModInitializer`** — TODO: explain interfaces.

---

## 4. Instance Fields

```java
private static GitServices services;
```

| Field | Type | Description |
|-------|------|-------------|
| `services` | [`GitServices`](GitServices.md) | TODO: describe |

---

## 5. `onInitializeClient()`

```java
@Override
public void onInitializeClient()
```

TODO: Provide detailed design explanation for `onInitializeClient`.

---

## 6. `services()`

```java
public static GitServices services()
```

TODO: Provide detailed design explanation for `services`.

---
