# RepoOptions

> **Source File:** [RepoOptions.java](../../../../../../../src/main/java/org/almond/minecraftgit/repo/RepoOptions.java)

## Usage

Immutable configuration passed into a [`RepoService`](RepoService.md). Keeping it
free of Minecraft types lets the repository layer run in plain-JVM tests.
Blank values are normalised to the defaults by the canonical constructor.

```java
RepoOptions options = new RepoOptions("main", "origin", "Builder", "builder@example.com");
RepoService repo = new RepoService(path, options, debug);
```

## Fields

| Name | Type | Default | Description |
|------|------|---------|-------------|
| `branch` | `String` | `main` | Branch to create and use when the repository has none. |
| `defaultRemote` | `String` | `origin` | Remote name used when metadata does not specify one. |
| `authorName` | `String` | `MinecraftGit` | Commit author/committer name. |
| `authorEmail` | `String` | `minecraftgit@localhost` | Commit author/committer email. |

## See Also

- [RepoService](RepoService.md) — consumes the options.
- [RepoMetadata](RepoMetadata.md) — persists the effective values per world.
- [Implementation class reference](../../../../../../Implementation/06-Class-Reference.md)
