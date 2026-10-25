# Fabric Mod Template

A starter repository for Minecraft **Fabric** mods. It contains a working Gradle
build, a small example mod, Loom run tasks, and a GitHub Actions workflow.

The example registers an item (`examplemod:example_item`), adds it to the
Ingredients creative tab, registers `/examplemod give`, and logs from a mixin.
Use it as a reference, then replace it with your own code.

## Features

- **Minecraft 1.21** with Fabric Loader 0.19.5, Fabric API 0.102.0+1.21 and Loom 1.19.3
- **Java 21 bytecode target** managed by Gradle; JDK 25 runs Gradle/Loom
- **Split source sets** — common code in `src/main`, client-only code in `src/client`
- **Version injection** — mod version, Minecraft version, loader version and Java
  version are expanded into `fabric.mod.json` at build time
- **Example content** — item registration, creative tab integration, a Brigadier
  command, a mixin, assets, translations, and a mod icon
- **Loom run tasks** — `runClient` and `runServer` with no manual server setup
- **GitHub Actions CI** — builds on pushes and pull requests, and attaches the
  built jars to a GitHub Release when a `v*` tag is pushed
- **DevOps guide** — build, CI, release, and operations documentation in
  `Documentation/DEVOPS.md`
- **Dev log helper** — `gradlew devLog` from the bundled `Utils` submodule

## Requirements

- **JDK 25** to run Gradle/Loom (Loom 1.19 uses the Java 25 FFM API)
- The produced mod targets **Java 21** and **Minecraft 1.21**
- Git (only needed for cloning and releases)
- The Gradle wrapper is included; you do not need to install Gradle

Check your Java version with:

```bash
java -version
```

## Quick Start

1. Create a new repository from this template (or clone it).
2. Rename the mod details listed in [Customization](#customization).
3. Build the mod:

On Windows PowerShell:

```powershell
.\gradlew.bat build
```

On macOS / Linux:

```bash
./gradlew build
```

The mod jar is written to `build/libs/`, for example
`build/libs/examplemod-1.0.0.jar`. A sources jar is produced next to it.

> **Version note:** this template targets Minecraft **1.21**. To target 1.21.1
> instead, set `minecraft_version=1.21.1` and
> `fabric_api_version=0.116.17+1.21.1` in `gradle.properties`; the
> `fabric.mod.json` dependency is generated from those properties.

## Running the Game

Loom generates run configurations and Gradle tasks automatically:

```powershell
# Launch a development client
.\gradlew.bat runClient

# Launch a dedicated development server
.\gradlew.bat runServer
```

The first launch downloads Minecraft and its libraries, so it can take a while.
The server run directory is `run/`. If the dedicated server exits immediately,
open `run/eula.txt`, set `eula=true`, and run the task again.

In game, run:

```text
/examplemod give
```

The Example Item is also available in the Ingredients creative tab. The example
mixin writes a message to the log whenever a server level loads.

## Project Structure

```text
.
├── build.gradle                  # Loom setup, dependencies, resource expansion
├── gradle.properties             # Minecraft, loader, Loom, API and mod versions
├── settings.gradle               # Fabric Maven and project name
├── gradle/wrapper/               # Gradle wrapper (commit this)
├── src/
│   ├── main/
│   │   ├── java/com/example/examplemod/
│   │   │   ├── ExampleMod.java            # main entrypoint
│   │   │   ├── command/ExampleCommand.java
│   │   │   ├── item/ModItems.java
│   │   │   └── mixin/ExampleMixin.java
│   │   └── resources/
│   │       ├── fabric.mod.json            # mod metadata and entrypoints
│   │       ├── examplemod.mixins.json     # mixin configuration
│   │       └── assets/examplemod/         # icon, textures, models, lang
│   └── client/
│       └── java/com/example/examplemod/client/ExampleModClient.java
├── Documentation/DEVOPS.md                # build, CI, release, and operations guide
└── .github/workflows/build.yml   # CI and release workflow
```

Client-only classes belong in `src/client`; Loom puts them on the client
development classpath, and the loader only calls the `client` entrypoint on a
physical client.

## Gradle Tasks

| Task | Description |
|------|-------------|
| `build` | Compile, process resources, remap, and produce the mod jar |
| `remapJar` | Remap the compiled jar into the final mod jar |
| `runClient` | Launch a development client |
| `runServer` | Launch a development server |
| `genSources` | Decompile and attach Minecraft sources for your IDE |
| `test` | Run tests (add tests under `src/test` when needed) |
| `devLog` | Create or open today's dev log in `Development-Notes` |
| `clean` | Delete the `build` directory |

## DevOps Guide

The template includes a dedicated [DevOps guide](Documentation/DEVOPS.md) covering the
Gradle wrapper, Loom, GitHub Actions, artifacts, releases, version updates,
caching, security, and troubleshooting for the tools in this repository.

## Customization

Before publishing a mod, replace these placeholders:

| Location | What to change |
|----------|----------------|
| `gradle.properties` | `maven_group`, `archives_base_name`, `mod_version` |
| `settings.gradle` | `rootProject.name` |
| `src/main/resources/fabric.mod.json` | `id`, `name`, `description`, `authors`, `contact`, `license` |
| `src/main/java/com/example/examplemod/` | Package name and class names |
| `src/client/java/com/example/examplemod/client/` | Client package and class names |
| `src/main/resources/examplemod.mixins.json` | Mixin package and compat level |
| `src/main/resources/assets/examplemod/` | Namespace folders, icon, textures, models, lang |
| `build.gradle` | Loom `mods` block and access-widener path |

If you rename the mod id, update it consistently in:

- `ExampleMod.MOD_ID`
- `fabric.mod.json` (`id` and icon path)
- asset and data namespaces such as `assets/examplemod/`
- `examplemod.mixins.json` (file name and references in `fabric.mod.json`)
- `archives_base_name` if you want the jar file name to match

### Updating Minecraft / Fabric

1. Look up the current versions at [fabricmc.net/develop](https://fabricmc.net/develop/).
2. Update `minecraft_version`, `loader_version`, `loom_version`,
   `fabric_api_version`, and `java_version` in `gradle.properties`.
3. If the Java version changes, update `compatibilityLevel` in
   `examplemod.mixins.json` as well. The mod currently targets Java 21 while
   Gradle and Loom run on JDK 25.
4. Run `.\gradlew.bat build` and fix any API changes in the example code.

For 1.21.x the build uses the `net.fabricmc.fabric-loom-remap` plugin and
official Mojang mappings. The unobfuscated 26.x Minecraft versions use
`net.fabricmc.fabric-loom` without a mappings dependency instead.

Loom supplies the Minecraft and Fabric repositories automatically. Add extra
repositories in `build.gradle` only when you depend on other mods.

### Access Wideners

An example access-widener path is commented out in `build.gradle`. Add
`src/main/resources/examplemod.accesswidener`, uncomment the line, and declare
the file in `fabric.mod.json` if you need to widen access to game members.

## CI / Releases

`.github/workflows/build.yml` runs on pushes to `main`, `master`, and `develop`,
on pull requests, and on `v*` tags. It:

- validates the Gradle wrapper
- sets up JDK 25
- builds the project
- uploads `build/libs/` as a workflow artifact
- creates a GitHub Release with the jars when the ref is a `v*` tag

To publish a release:

```bash
git tag v1.0.0
git push origin v1.0.0
```

## Dev Logs

The `Utils` submodule provides a dev-log generator. After cloning with
submodules (`git clone --recurse-submodules ...`), create or open today's entry:

```powershell
.\gradlew.bat devLog
```

This requires Python and writes under `Development-Notes/Development-Log/`.
Use `-PnoOpen` to skip opening the file, or `-Pdate=YYYY-MM-DD` for a specific
date.

## Credits

The Gradle setup follows the current [Fabric example mod](https://github.com/FabricMC/fabric-example-mod)
and [Fabric documentation](https://docs.fabricmc.net/). The repository
previously contained a Paper plugin template; Paper-specific build logic
(Shadow, Paper API, `plugin.yml`, server jar copying) has been replaced with
Fabric Loom.
