# CommitMeta

> **Source File:** [CommitMeta.java](../../../../../../../src/main/java/org/almond/minecraftgit/repo/CommitMeta.java)

## Usage

Metadata committed next to each selection as `meta.json`. It is part of the Git
tree (not local state) so a clone can shift a build onto a new origin and tools
can resolve regions. Written by [`CommitService`](../commit/CommitService.md)
into a temporary tree, then copied into the commit.

```java
CommitMeta meta = new CommitMeta(name, worldId, playerName, playerUuid, gameMode,
        CommitMeta.FORMAT_VERSION, modVersion, origin, previousOrigin, reorigined,
        dimensions, cuboidCount, blockCount, entityCount, relativeCuboids,
        Instant.now().toString());
meta.write(tempDir.resolve("meta.json"));
```

## Fields

| Name | Type | Description |
|------|------|-------------|
| `selection` | `String` | Selection name. |
| `world` | `String` | World id where the commit was made. |
| `authorName`, `authorUuid` | `String` | Player identity. |
| `gameMode` | `String` | Game mode at commit time. |
| `formatVersion` | `int` | `FORMAT_VERSION` (1). |
| `pluginVersion` | `String` | Mod version (historical field name). |
| `origin` | [`BlockPos`](../model/BlockPos.md) | Absolute origin at commit time. |
| `previousOrigin` | [`BlockPos`](../model/BlockPos.md) | Origin before a re-origin, or `null`. |
| `reorigined` | `boolean` | Whether `origin` differs from `previousOrigin`. |
| `dimensions` | `String` | `x x y x z` of the selection bounds. |
| `cuboidCount`, `blockCount`, `entityCount` | `int` | Capture statistics. |
| `relativeCuboids` | `List<Cuboid>` | Cuboids translated relative to `origin`. |
| `createdAt` | `String` | ISO-8601 commit time. |

## Methods

| Method | Description |
|--------|-------------|
| `write(Path)` | Pretty-printed JSON; creates parent directories; `RepoException` on IO failure. |
| `read(Path)` | `Optional<CommitMeta>`; empty for missing or syntactically invalid JSON; `RepoException` on other IO failure. |
| `toJson()` | JSON string. |
| `FORMAT_VERSION` | `1`. |

## See Also

- [CommitService](../commit/CommitService.md) — writes and reads the metadata.
- [Git-Format.md](../../../../../../Git-Format.md) — committed `meta.json` schema.
- [Implementation class reference](../../../../../../Implementation/06-Class-Reference.md)
