# DevOps Guide

Operational notes for building, testing, releasing, and maintaining Minecraft Git. The repository
started as a Fabric mod template; the Paper-specific build (Shadow, `plugin.yml`, server jar) is
gone.

## Toolchain

| Component | Version / source | Purpose |
|---|---|---|
| Minecraft | `1.21` (`gradle.properties`) | Target game version |
| Fabric Loader | `0.19.5` | Mod loader |
| Fabric API | `0.102.0+1.21` | Client commands, lifecycle events, interaction events |
| Fabric Loom | `1.19.3` (`net.fabricmc.fabric-loom-remap`) | Minecraft setup, remap, run configs |
| Gradle | `9.8.1` (wrapper) | Reproducible builds |
| Java target | `21` | Mod bytecode |
| Gradle runtime | JDK 25 | Loom 1.19 uses the Java 25 FFM API |
| JGit | `6.10.0.202406032230-r` | Pure-Java Git implementation, bundled |
| JavaEWAH | `1.2.3` | JGit runtime dependency, bundled |
| JUnit | `5.11.4` | Pure-JVM tests |
| CI | GitHub Actions | Build, artifact upload, release |

The version split is intentional: Gradle/Loom run on JDK 25, while the compiled mod uses
`--release 21` and runs on Minecraft 1.21's Java 21 runtime.

## Local build

Prerequisites: JDK 25 on `PATH` (or `JAVA_HOME`), Git, and network access for the first build.

```powershell
.\gradlew.bat build
```

The build:

1. resolves Minecraft, mappings, Loader, Fabric API, JGit, and JavaEWAH,
2. compiles `src/main` and `src/client`,
3. runs JUnit tests,
4. nests JGit and JavaEWAH under `META-INF/jars/`,
5. remaps and writes `build/libs/minecraftgit-1.0.0.jar` plus a sources jar.

The first build downloads Minecraft and Loom artifacts. Later builds reuse the Gradle and Loom
caches. Offline builds are possible after a successful online build:

```powershell
.\gradlew.bat --offline build
```

`runClient` additionally needs Minecraft assets. Offline, Loom will fail its `downloadAssets` task;
`-x downloadAssets` skips it for a startup-only smoke test.

### Common tasks

| Task | Description |
|---|---|
| `build` | Compile, test, remap, and assemble |
| `test` | Run JUnit tests |
| `runClient` | Launch the development client |
| `runClient -x downloadAssets` | Launch without downloading assets (startup smoke test only) |
| `genSources` | Decompile and attach Minecraft sources |
| `devLog` | Create/open today's dev log (requires `Utils` submodule and Python) |
| `clean` | Delete `build/` |

`runServer` is not a useful target for this client-only mod: the loader will not load it on a
dedicated server. It can still validate that the jar is ignored rather than crashing.

### Where outputs live

- `build/libs/minecraftgit-1.0.0.jar` — installable mod.
- `build/libs/minecraftgit-1.0.0-sources.jar` — sources.
- `run/` — development client directory (ignored).
- `config/minecraftgit/` under the game directory — mod data (repositories, selections, staging,
  backups).

## Bundling JGit

`build.gradle` declares:

```groovy
implementation "org.eclipse.jgit:org.eclipse.jgit:${project.jgit_version}"
include "org.eclipse.jgit:org.eclipse.jgit:${project.jgit_version}"
include "com.googlecode.javaewah:JavaEWAH:${project.javaewah_version}"
```

Loom's `include` configuration nests those jars in the remapped mod jar and adds a `jars` section
to the built `fabric.mod.json`. Fabric Loader loads the nested jars, so players do not need a Git
executable. Verify a build with:

```powershell
tar -tf build/libs/minecraftgit-1.0.0.jar | Select-String "META-INF/jars"
```

Expected entries include `org.eclipse.jgit-6.10.0.202406032230-r.jar` and `JavaEWAH-1.2.3.jar`.
Loom prints a harmless warning that JGit's version is not valid semver while generating nested mod
metadata. The client log should then contain:

```text
(minecraftgit) JGit is available to Minecraft Git
```

JGit's `commons-codec` and `slf4j-api` dependencies are supplied by Minecraft's library set. The
optional SSH transport module is not bundled; add `org.eclipse.jgit.ssh.apache` and its SSH
dependencies if SSH remotes are required.

## Tests

```powershell
.\gradlew.bat test
```

The suite is pure JVM and includes real JGit integration tests (init, commit, push, fetch,
fast-forward planning) using temporary directories. See [Testing.md](Testing.md) for coverage and
the manual checklist.

Tests should never require a Minecraft client or a running server. Anything that needs a `Level`
(capture/apply) belongs in `src/client` and is verified manually or with a future test harness.

## Running the client

```powershell
.\gradlew.bat runClient
```

The development run directory is `run/`. The first launch downloads Minecraft and assets and is
slow. Join a single-player world to exercise commands, or a multiplayer server to verify the
read-only behavior.

The client entrypoint is `org.almond.minecraftgit.client.MinecraftGitClient`; it is declared under
`entrypoints.client` in `fabric.mod.json`. The mod's `environment` is `client`, so dedicated
servers ignore it.

## CI and releases

`.github/workflows/build.yml` runs on pushes to `main`/`master`/`develop`, pull requests, and `v*`
tags. It:

1. checks out the repository,
2. validates the Gradle wrapper,
3. installs JDK 25 with Gradle caching,
4. runs `./gradlew build --stacktrace`,
5. uploads `build/libs/` as a workflow artifact,
6. creates a GitHub Release with the jars for `v*` tags.

To release:

1. Set `mod_version` in `gradle.properties`.
2. Run `.\gradlew.bat clean build` and the manual checklist.
3. Commit the version bump.
4. `git tag v1.0.0 && git push origin v1.0.0`.

The release step uses `gh release create ... --generate-notes`; delete an existing release or use
`gh release upload <tag> build/libs/*.jar --clobber` to replace assets.

## Branch and worktree workflow

Features are developed in git worktrees and squashed into local `develop`:

```powershell
git branch feature/my-feature develop
git worktree add ../feature/my-feature feature/my-feature
# work in ../feature/my-feature
git add -A
git commit -m "Describe the feature"
git -C ../develop merge --no-ff feature/my-feature
```

Worktree directories under `feature/` are ignored by the parent repository. Do not delete feature
branch names after merging; they document the phase history. The implementation plan records the
branch/phase mapping.

## Updating Minecraft or Fabric

1. Look up compatible versions at [fabricmc.net/develop](https://fabricmc.net/develop).
2. Update `minecraft_version`, `loader_version`, `loom_version`, `fabric_api_version`, and
   `java_version` in `gradle.properties`.
3. If the Java target changes, update the compiler release and CI JDK.
4. Run `.\gradlew.bat clean build` and fix mapping/API changes.
5. Run the client and the manual checklist, especially capture/apply (`BlockState` and `BlockEntity`
   signatures are mapping-sensitive).
6. Update this guide and the format document if behavior changes.

For 1.21.x, keep Loom's `-remap` plugin and official Mojang mappings. For unobfuscated future
versions, switch to `net.fabricmc.fabric-loom` without a mappings dependency as documented by
Fabric.

## Data operations

### Back up a repository

Repositories are normal Git repositories:

```powershell
git -C config/minecraftgit/repos/world__overworld bundle create minecraftgit.bundle --all
```

Copy `config/minecraftgit/repos/` and `config/minecraftgit/meta/` together; selection metadata and
staging are machine-local and need not travel with a repository. A build can be shared by adding a
bare repository or HTTPS URL as a remote.

### Clean local state

| Want to reset | Delete |
|---|---|
| repository + history | `config/minecraftgit/repos/<world>__<dimension>/` |
| remote/author/active selection | `config/minecraftgit/meta/<world>__<dimension>.json` |
| selections | `config/minecraftgit/selections/<world>__<dimension>/` |
| staged snapshots | `config/minecraftgit/staging/<world>__<dimension>/` |
| pre-apply backups | `config/minecraftgit/backups/<world>__<dimension>/` |
| all mod data | `config/minecraftgit/` |

The world itself is never modified by deleting these files.

## Security and hygiene

- Remote URLs may contain credentials; avoid committing them. The mod stores remotes in the local
  repository config, not in selection or commit metadata.
- Do not commit `build/`, `run/`, `.gradle/`, IDE files, logs, or local repositories.
- `Utils` is an empty submodule placeholder in this checkout; `devLog` requires it to be
  initialized.
- Read and respect the Minecraft EULA and Mojang mappings license.
- Pin third-party GitHub Actions to commit SHAs if strict supply-chain pinning is required.

## Troubleshooting

| Symptom | Likely cause | Fix |
|---|---|---|
| `downloadAssets` fails offline | Assets are not cached | Build online once, or use `-x downloadAssets` for a startup-only test |
| `Unsupported class file major version` | Gradle is running on an old JDK | Run Gradle with JDK 25 |
| `No matching variant` / Fabric API resolution | Minecraft/API/Loader versions do not match | Align versions in `gradle.properties` |
| JGit classes missing at runtime | `include` removed or jar not remapped | Check `META-INF/jars` and the startup log |
| `Could not open repository` | Wrong data folder or corrupt repo | Inspect `config/minecraftgit/repos/...`; normal `git fsck` applies |
| Push rejected | Remote advanced or diverged | Fetch/pull first, or use `--force` intentionally |
| Pull applies nothing | Remote has no matching selection | Check `/git log` and the remote branch |
| Apply refuses in multiplayer | By design | Use single-player or a server-side companion |
| Commands missing | Client command registration failed | Check the log for the client entrypoint and Fabric API version |
| Mixin/registration crash | Mod jar built for a different MC version | Rebuild against 1.21 and check the remapped jar |

Prefer `--stacktrace` first:

```powershell
.\gradlew.bat build --stacktrace
```
