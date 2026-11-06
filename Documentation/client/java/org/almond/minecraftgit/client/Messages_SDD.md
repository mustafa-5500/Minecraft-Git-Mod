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

TODO: Describe how `Messages` is instantiated and used.

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
| `ChatFormatting` | TODO: describe purpose |
| `Component` | TODO: describe purpose |
| `FabricClientCommandSource` | TODO: describe purpose |
| [`ModConfig`](../../../../../../main/java/org/almond/minecraftgit/config/ModConfig.md) | TODO: describe purpose |
| `Locale` | TODO: describe purpose |

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
| `config` | [`ModConfig`](../../../../../../main/java/org/almond/minecraftgit/config/ModConfig.md) | TODO: describe |

---

## 5. Constructor

```java
public Messages(ModConfig config)
```

TODO: Provide detailed design explanation for `Messages`.

---

## 6. `prefix()`

```java
private Component prefix()
```

TODO: Provide detailed design explanation for `prefix`.

---

## 7. `send()`

```java
public void send(FabricClientCommandSource source, String message)
```

TODO: Provide detailed design explanation for `send`.

---

## 8. `success()`

```java
public void success(FabricClientCommandSource source, String message)
```

TODO: Provide detailed design explanation for `success`.

---

## 9. `warn()`

```java
public void warn(FabricClientCommandSource source, String message)
```

TODO: Provide detailed design explanation for `warn`.

---

## 10. `error()`

```java
public void error(FabricClientCommandSource source, String message)
```

TODO: Provide detailed design explanation for `error`.

---

## 11. `raw()`

```java
public void raw(FabricClientCommandSource source, String message)
```

TODO: Provide detailed design explanation for `raw`.

---

## 12. `format()`

```java
public void format(FabricClientCommandSource source, String format, Object... args)
```

TODO: Provide detailed design explanation for `format`.

---
