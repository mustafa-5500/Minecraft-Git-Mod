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

TODO: Describe how `SelectionRenderer` is instantiated and used.

---

## 1. Overview

Budgeted particle outline for the active selection, a pending cuboid, and a
temporary {@code /git selection show} highlight. Particles are local to this
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
| `ClientTickEvents` | TODO: describe purpose |
| `Minecraft` | TODO: describe purpose |
| `ClientLevel` | TODO: describe purpose |
| `ParticleTypes` | TODO: describe purpose |
| [`GitServices`](../GitServices.md) | TODO: describe purpose |
| [`WorldContext`](../WorldContext.md) | TODO: describe purpose |
| [`Cuboid`](../../../../../../../main/java/org/almond/minecraftgit/model/Cuboid.md) | TODO: describe purpose |
| [`Selection`](../../../../../../../main/java/org/almond/minecraftgit/model/Selection.md) | TODO: describe purpose |
| `ArrayList` | TODO: describe purpose |
| `List` | TODO: describe purpose |

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
| `tickCounter` | `int` | TODO: describe |
| `highlightWorld` | `String` | TODO: describe |
| `highlightSelection` | `String` | TODO: describe |
| `highlightUntil` | `long` | TODO: describe |

---

## 5. Constructor

```java
private SelectionRenderer()
```

TODO: Provide detailed design explanation for `SelectionRenderer`.

---

## 6. `register()`

```java
public static void register(GitServices services)
```

TODO: Provide detailed design explanation for `register`.

---

## 7. `highlight()`

```java
public static void highlight(String worldId, String selection, int seconds)
```

TODO: Provide detailed design explanation for `highlight`.

---

## 8. `tick()`

```java
private static void tick(GitServices services, Minecraft client)
```

TODO: Provide detailed design explanation for `tick`.

---

## 9. `drawCuboid()`

```java
private static void drawCuboid(ClientLevel level, Cuboid cuboid, int[] used, int budget)
```

TODO: Provide detailed design explanation for `drawCuboid`.

---

## 10. `draw()`

```java
private static void draw(ClientLevel level, int x, int y, int z, int[] used, int budget)
```

TODO: Provide detailed design explanation for `draw`.

---
