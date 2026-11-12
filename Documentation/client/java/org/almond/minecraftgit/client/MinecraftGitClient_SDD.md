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

Fabric instantiates this class by name from `fabric.mod.json` (`"environment": "client"`) and invokes `onInitializeClient()` once during client startup; there is no server entrypoint. It is the only place that constructs `ModConfig`, `DebugLogger` and `GitServices`, and the only writer of the static `services` field.

---

## 1. Overview

Client entrypoint. All commands, world access and rendering are client-side;
the mod is marked `"environment": "client"` and has no server
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
| `ClientModInitializer` | The Fabric client entrypoint interface whose `onInitializeClient()` Fabric calls once at startup. |
| `ClientCommandRegistrationCallback` | Registers the `/git` command tree through `GitCommand.register` on the client dispatcher. |
| `ClientLifecycleEvents` | Supplies the `CLIENT_STOPPING` event used to close the service graph when the game exits. |
| `FabricLoader` | Locates the config directory (`getConfigDir()`) under which the mod data root is created. |
| [`MinecraftGit`](../../../../../../main/java/org/almond/minecraftgit/MinecraftGit.md) | Provides `MOD_ID`, the shared `LOGGER` and the mod-container identity used during startup. |
| [`GitCommand`](command/GitCommand.md) | Builds and registers the whole `/git` command tree against the service graph. |
| [`SelectionRenderer`](selection/SelectionRenderer.md) | Registers the tick-based particle outline renderer. |
| [`WandEvents`](selection/WandEvents.md) | Registers the block attack and use callbacks that turn wand clicks into corners. |
| [`ModConfig`](../../../../../../main/java/org/almond/minecraftgit/config/ModConfig.md) | Loaded from the config directory and handed to the logger and every service. |
| [`DebugLogger`](../../../../../../main/java/org/almond/minecraftgit/debug/DebugLogger.md) | Created from the config's log root and debug flags before the services that use it. |
| `Path` | Type of the resolved mod data root under the Fabric config directory. |

---

## 3. Class Declaration

```java
public final class MinecraftGitClient implements ClientModInitializer {
```

- **`implements ClientModInitializer`** — Fabric's client-only entrypoint contract; `onInitializeClient()` returns void and is expected not to throw, so the JGit probe is wrapped in a try/catch.

---

## 4. Instance Fields

```java
private static GitServices services;
```

| Field | Type | Description |
|-------|------|-------------|
| `services` | [`GitServices`](GitServices.md) | Holds the single `GitServices` graph. Static because command lambdas and static registrations resolve it through `services()`; written once in `onInitializeClient()` and cleared on `CLIENT_STOPPING`. |

---

## 5. `onInitializeClient()`

```java
@Override
public void onInitializeClient()
```

Resolves `<config dir>/minecraftgit`, loads or creates `config.json`, builds the `DebugLogger` and then the graph, in that order, because the logger needs `logsRoot()` and the graph needs both. It then probes JGit, registers `GitCommand` with `ClientCommandRegistrationCallback`, registers `WandEvents` and `SelectionRenderer`, and finally registers a `CLIENT_STOPPING` listener that closes and nulls the graph. The registrations all capture the same `services` reference, and the renderer re-reads config each tick, so later config edits are honoured.

---

## 6. `services()`

```java
public static GitServices services()
```

Reads the static field into a local and throws `IllegalStateException("Minecraft Git is not initialized.")` when it is null, which happens before initialization and after shutdown. Using the local avoids a race where `CLIENT_STOPPING` clears the field between the null check and the use.

---
