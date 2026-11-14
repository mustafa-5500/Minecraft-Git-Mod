# Messages — Software Detailed Design

> **API Documentation:** [Messages.md](./Messages.md)  
> **Source File:** [Messages.java](../../../../../../../src/client/java/org/almond/minecraftgit/client/Messages.java)

## Table of Contents

- [Usage](#usage)
- [1. Overview](#1-overview)
- [2. Package Declaration & Imports](#2-package-declaration-imports)
- [3. Class Declaration](#3-class-declaration)
- [4. Instance Fields](#4-instance-fields)
- [5. Constructor](#5-constructor)
- [6. `prefix()`](#6-prefix)
- [7. `send()`](#7-send)
- [8. `success()`](#8-success)
- [9. `warn()`](#9-warn)
- [10. `error()`](#10-error)
- [11. `raw()`](#11-raw)
- [12. `format()`](#12-format)

## Usage

Owned by `GitServices` and reached through `services.messages()`, so every command shares one formatter and one prefix. The wand and renderer code build their own `Component` instances because they often bypass a command source (`displayClientMessage`, system messages), so this class intentionally covers only command feedback.

---

## 1. Overview

Chat formatting shared by all client commands.

---

## 2. Package Declaration & Imports

```java
package org.almond.minecraftgit.client;
```

```java
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import org.almond.minecraftgit.config.ModConfig;
import java.util.Locale;
```

| Import | Purpose |
|--------|---------|
| `ChatFormatting` | Provides `DARK_AQUA` for the prefix and `GRAY`, `GREEN`, `YELLOW` and `RED` for the bodies. |
| `Component` | Chat component type accepted by the command source and built by every method. |
| `FabricClientCommandSource` | Client command source whose `sendFeedback` and `sendError` methods deliver the message to the player. |
| [`ModConfig`](../../../../../../main/java/org/almond/minecraftgit/config/ModConfig.md) | Read for the `prefix` string; nothing else in the config is used. |
| `Locale` | Supplies `Locale.ROOT` so `format` is locale-independent. |

---

## 3. Class Declaration

```java
public final class Messages {
```


---

## 4. Instance Fields

```java
private final ModConfig config;
```

| Field | Type | Description |
|-------|------|-------------|
| `config` | [`ModConfig`](../../../../../../main/java/org/almond/minecraftgit/config/ModConfig.md) | Only `prefix` is consulted, each time `prefix()` runs, so the config remains the single source of truth for the chat prefix. |

---

## 5. Constructor

```java
public Messages(ModConfig config)
```

Stores the config reference and nothing else; no components are pre-built, so a prefix change takes effect immediately. `GitServices` constructs exactly one instance per service graph.

---

## 6. `prefix()`

```java
private Component prefix()
```

Returns `Component.literal(config.prefix == null ? "" : config.prefix)` styled `DARK_AQUA`. Every caller invokes `prefix().copy()` before appending, so the returned component is never mutated in place.

---

## 7. `send()`

```java
public void send(FabricClientCommandSource source, String message)
```

Appends a `GRAY` literal body to a copy of the prefix and calls `source.sendFeedback`; the player sees the prefix immediately followed by the message. No formatting codes in `message` are interpreted.

---

## 8. `success()`

```java
public void success(FabricClientCommandSource source, String message)
```

Same as `send` with `GREEN` styling, marking an operation that succeeded.

---

## 9. `warn()`

```java
public void warn(FabricClientCommandSource source, String message)
```

Same as `send` with `YELLOW` styling for advisory output such as the multiplayer wand caveat.

---

## 10. `error()`

```java
public void error(FabricClientCommandSource source, String message)
```

The only variant that calls `source.sendError`; the message is red and reported through the error channel, with the prefix still included.

---

## 11. `raw()`

```java
public void raw(FabricClientCommandSource source, String message)
```

Sends an unprefixed `GRAY` literal through `sendFeedback`; used for headers and row output so the caller controls indentation.

---

## 12. `format()`

```java
public void format(FabricClientCommandSource source, String format, Object... args)
```

Delegates to `send` after `String.format(Locale.ROOT, format, args)`, so every formatted message inherits the prefix and grey styling; a malformed pattern throws `IllegalFormatException` to the caller.

---
