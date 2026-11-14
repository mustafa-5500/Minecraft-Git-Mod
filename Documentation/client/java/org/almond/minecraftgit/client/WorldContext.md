# WorldContext

> **Source File:** [WorldContext.java](../../../../../../../src/client/java/org/almond/minecraftgit/client/WorldContext.java)

## Usage

Identity of the currently open world/dimension and the bridge to the integrated
server when the player is hosting the world. Every command starts by calling
`WorldContext.current(client)`; the `id` keys repositories, metadata, selections,
staging and backups.

A world id is `safeWorldName(name) + "__" + safeWorldName(dimension path)`, e.g.
`world__overworld`. In multiplayer, the display name is suffixed with the server
address (unless it already contains it), so two servers with the same name do not
share local state.

```java
WorldContext world = WorldContext.current(Minecraft.getInstance());
String worldId = world.id();            // repos/<worldId>
ServerLevel level = world.serverLevel(Minecraft.getInstance());
boolean canApply = world.singleplayer();
```

## Fields

| Name | Type | Description |
|------|------|-------------|
| `id` | `String` | Repository/data key (world + dimension, plus server in multiplayer). |
| `displayName` | `String` | Human-readable world/server name. |
| `dimension` | `ResourceKey<Level>` | Dimension key. |
| `singleplayer` | `boolean` | True when an integrated server is hosting the world. |

## Methods

| Method | Description |
|--------|-------------|
| `current(Minecraft)` | Builds the record; throws `UserFacingException("Open a world before using /git.")` when no level is loaded. |
| `serverLevel(Minecraft)` | Returns the integrated `ServerLevel` for this dimension, or `null` outside single-player. |
| `gameMode(Minecraft)` | Player game mode name, or `"unknown"`. |
| `currentScreen(Minecraft)` | Current screen class name (debug only). |

## See Also

- [08-Diagram-Guide.md](../../../../../../Implementation/08-Diagram-Guide.md)
- [Implementation class reference](../../../../../../Implementation/06-Class-Reference.md)
