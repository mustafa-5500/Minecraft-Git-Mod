# SelectionRenderer — Software Detailed Design

> **API Documentation:** [SelectionRenderer.md](./SelectionRenderer.md)  
> **Source File:** [SelectionRenderer.java](../../../../../../../../src/client/java/org/almond/minecraftgit/client/selection/SelectionRenderer.java)

## Table of Contents

- [Usage](#usage)
- [1. Overview](#1-overview)
- [2. Package Declaration & Imports](#2-package-declaration-imports)
- [3. Class Declaration](#3-class-declaration)
- [4. Instance Fields](#4-instance-fields)
- [5. Constructor](#5-constructor)
- [6. `register()`](#6-register)
- [7. `highlight()`](#7-highlight)
- [8. `tick()`](#8-tick)
- [9. `drawCuboid()`](#9-drawcuboid)
- [10. `draw()`](#10-draw)

## Usage

Registered from `MinecraftGitClient.onInitializeClient()` as `SelectionRenderer.register(services)`, which attaches the tick callback; no instance is created. Commands reach the static `highlight` for a temporary outline, and the pending cuboid produced by wand clicks is picked up automatically from `SelectionManager`.

---

## 1. Overview

Budgeted particle outline for the active selection, a pending cuboid, and a
temporary `/git selection show` highlight. Particles are local to this
client and never affect the server.

---

## 2. Package Declaration & Imports

```java
package org.almond.minecraftgit.client.selection;
```

```java
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import org.almond.minecraftgit.client.GitServices;
import org.almond.minecraftgit.client.WorldContext;
import org.almond.minecraftgit.model.Cuboid;
import org.almond.minecraftgit.model.Selection;
import java.util.ArrayList;
import java.util.List;
```

| Import | Purpose |
|--------|---------|
| `ClientTickEvents` | Provides `END_CLIENT_TICK`, the hook that drives the renderer about twice a second. |
| `Minecraft` | Supplies `level` and `player` inside the tick callback. |
| `ClientLevel` | Receives `addParticle` calls and is the client-side particle sink. |
| `ParticleTypes` | Supplies `END_ROD`, the small white particle used for the outline. |
| [`GitServices`](../GitServices.md) | Source of config flags, the selection store and the selection manager. |
| [`WorldContext`](../WorldContext.md) | Resolves the current world id and can throw when no world is open, which the renderer catches. |
| [`Cuboid`](../../../../../../../main/java/org/almond/minecraftgit/model/Cuboid.md) | The region whose edges are sampled into particles. |
| [`Selection`](../../../../../../../main/java/org/almond/minecraftgit/model/Selection.md) | Materializes the active or highlighted selection so its cuboid list can be drawn. |
| `ArrayList` | Builds the per-tick list of cuboids to draw. |
| `List` | Type of that per-tick cuboid list. |

---

## 3. Class Declaration

```java
public final class SelectionRenderer {
```


---

## 4. Instance Fields

```java
private static int tickCounter;
private static String highlightWorld;
private static String highlightSelection;
private static long highlightUntil;
```

| Field | Type | Description |
|-------|------|-------------|
| `tickCounter` | `int` | Free-running tick counter; `tickCounter++ % 10 != 0` throttles drawing to every tenth tick. |
| `highlightWorld` | `String` | World id captured by `highlight`; the highlight only draws while it equals the current id. |
| `highlightSelection` | `String` | Selection name to outline temporarily, or null when no highlight is active. |
| `highlightUntil` | `long` | Wall-clock deadline in epoch milliseconds; the highlight expires without any explicit reset. |

---

## 5. Constructor

```java
private SelectionRenderer()
```

Private no-op constructor: all state is static and the class is used purely through `register`, `highlight` and the tick callback, so instantiation is prevented.

---

## 6. `register()`

```java
public static void register(GitServices services)
```

Registers a lambda on `ClientTickEvents.END_CLIENT_TICK` that calls `tick(services, client)`. The services reference is captured for the lifetime of the client, so the graph must exist before registration; `MinecraftGitClient` calls this once.

---

## 7. `highlight()`

```java
public static void highlight(String worldId, String selection, int seconds)
```

Sets the three static highlight fields and computes `now + seconds * 1000L`. It performs no lookup and no cleanup; a stale highlight is ignored because every tick compares the world id and the deadline.

---

## 8. `tick()`

```java
private static void tick(GitServices services, Minecraft client)
```

Returns early when rendering is disabled or the player or level is absent, then throttles to every tenth tick. It builds a list holding the active selection's cuboids, the pending cuboid and the unexpired highlight's cuboids for the current world, then draws each cuboid against a shared `config.renderBudget`, stopping between cuboids once the budget is exhausted. `WorldContext.current` may throw when no world is open and is caught as "nothing to draw".

---

## 9. `drawCuboid()`

```java
private static void drawCuboid(ClientLevel level, Cuboid cuboid, int[] used, int budget)
```

Samples the twelve edges of the cuboid with a stride of two blocks, iterating x-edges, then z-edges, then y-edges, and calls `draw` for each sample. It does not check the budget between samples; the counter and guard live in `draw`, so a large selection simply fills the budget from the first edges sampled.

---

## 10. `draw()`

```java
private static void draw(ClientLevel level, int x, int y, int z, int[] used, int budget)
```

Returns without drawing when `used[0] >= budget`, otherwise spawns one `END_ROD` particle at the block centre and increments the counter. The `int[]` holder is a deliberately mutable counter shared across every cuboid in the tick.

---
