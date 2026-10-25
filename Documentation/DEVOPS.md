# DevOps Guide

This guide covers the build, automation, and release tooling that ships with
this Fabric mod template. It is scoped to the tools actually present in the
repository, plus a few optional extensions.

## Toolchain at a glance

| Component | Version / location | Purpose |
|-----------|--------------------|---------|
| Minecraft | `1.21` (`gradle.properties`) | Target game version |
| Fabric Loader | `0.19.5` | Loads the mod into Minecraft |
| Fabric API | `0.102.0+1.21` | Shared Fabric APIs used by the example |
| Fabric Loom | `1.19.3` (`net.fabricmc.fabric-loom-remap`) | Resolves Minecraft, remaps, generates run tasks |
| Gradle | `9.8.1` (wrapper) | Reproducible build execution |
| Java target | `21` | Bytecode level of the built mod |
| Gradle runtime | JDK 25 | Loom 1.18+ uses the Java 25 FFM API |
| CI | GitHub Actions | Build, artifact upload, release |
| Dev log | `Utils` submodule + `devLog` Gradle task | Developer journal entries |

The two Java versions are intentional: Gradle and Loom run on JDK 25, while the
compiled mod uses `--release 21` so it runs on Minecraft 1.21's Java 21
runtime.

## 1. Local build workflow

### Prerequisites

- JDK 25 available to Gradle (`java -version` should report 25).
- Git, if you are cloning or publishing releases.
- Network access for the first build; Gradle downloads Minecraft, mappings, and
  Fabric dependencies.

The Gradle wrapper is committed, so no separate Gradle installation is needed.

### Documentation helpers in `Utils`

The `devLog` task is the supported entry point for the journal. The `Utils`
submodule also contains standalone `documentation-generator` and
`decomposition-generator` Python scripts. They are not wired into Gradle or CI;
run them manually and follow the markdown/SDD files beside them.

### Common commands

Windows PowerShell:

```powershell
.\gradlew.bat build
.\gradlew.bat clean build
.\gradlew.bat test
.\gradlew.bat runClient
.\gradlew.bat runServer
.\gradlew.bat genSources
.\gradlew.bat devLog
```

macOS / Linux:

```bash
./gradlew build
./gradlew clean build
./gradlew test
./gradlew runClient
./gradlew runServer
./gradlew genSources
./gradlew devLog
```

### Task reference

| Task | What it does |
|------|--------------|
| `build` | Runs `check`, compiles `main` and `client`, processes resources, builds the jar, and remaps it |
| `test` | Runs tests under `src/test`; currently `NO-SOURCE` because the template has no tests |
| `remapJar` | Remaps the development jar into the final production mod jar |
| `runClient` | Starts a development client using Loom's generated run configuration |
| `runServer` | Starts a dedicated development server |
| `genSources` | Decompiles Minecraft and attaches sources for IDE navigation |
| `devLog` | Creates or opens today's dev log under `Development-Notes/` |
| `clean` | Deletes `build/` only; it does not clear the Gradle dependency cache |

### Where build outputs live

- `build/libs/examplemod-1.0.0.jar` — the installable mod.
- `build/libs/examplemod-1.0.0-sources.jar` — sources for IDE/debugging use.
- `build/` and `run/` are ignored by Git.

`build` is a local artifact directory, not a deployment target. The mod is
distributed by attaching the jar to a GitHub Release (see below).

### First run and offline builds

The first build downloads Minecraft and Fabric artifacts and can take several
minutes. Later builds reuse the Gradle cache. After a successful online build:

```powershell
.\gradlew.bat build --offline
```

`--offline` is useful on a disconnected machine, but fails if a required
artifact was never downloaded.

### Dedicated server runs

`runServer` uses the `run/` directory. If the server exits because the EULA is
not accepted, edit `run/eula.txt` and set:

```text
eula=true
```

Then run `.\gradlew.bat runServer` again. `run/` is ignored, so the EULA,
world, logs, and server properties are not committed.

## 2. Gradle wrapper

Files:

- `gradlew` — POSIX launcher.
- `gradlew.bat` — Windows launcher.
- `gradle/wrapper/gradle-wrapper.jar` and `.properties` — pinned wrapper.

Commit all four files. The wrapper guarantees that local builds and CI use the
same Gradle version, even if a newer Gradle is installed on the machine.

Update the wrapper deliberately, then rebuild and commit the generated files:

```powershell
.\gradlew.bat wrapper --gradle-version 9.8.1 --distribution-type bin
.\gradlew.bat clean build
```

On Unix, keep the executable bit on `gradlew`:

```bash
chmod +x gradlew
git update-index --chmod=+x gradlew
```

The CI workflow also runs `chmod +x ./gradlew` as a safety net.

## 3. Fabric Loom configuration

Most build configuration lives in two files.

### `gradle.properties`

This is the single source of truth for versions:

```properties
minecraft_version=1.21
loader_version=0.19.5
loom_version=1.19.3
fabric_api_version=0.102.0+1.21
java_version=21
```

`maven_group`, `archives_base_name`, and `mod_version` control the project
identity and jar name.

For a 1.21.1 build instead of 1.21, use:

```properties
minecraft_version=1.21.1
fabric_api_version=0.116.17+1.21.1
```

The rest of the toolchain stays the same.

### `build.gradle`

Key pieces:

- `net.fabricmc.fabric-loom-remap` is the Loom variant for obfuscated Minecraft
  versions such as 1.21. The unobfuscated 26.x versions use
  `net.fabricmc.fabric-loom` instead.
- `mappings loom.officialMojangMappings()` selects the official Mojang mapping
  set. Loom prints a licensing notice for these mappings on the first run; read
  it before redistributing mapped artifacts.
- Most Fabric tutorials use Yarn names. This template uses official Mojang
  mappings, so the imports differ: for example, `ResourceLocation` instead of
  `Identifier`, and `CreativeModeTabs` instead of `ItemGroups`.
- `modImplementation` is used for Fabric Loader and Fabric API because they are
  mod dependencies that Loom must remap.
- `splitEnvironmentSourceSets()` keeps client-only code in `src/client`.
- `processResources` expands `${version}`, `${minecraft_version}`,
  `${loader_version}`, and `${java_version}` in `fabric.mod.json` so the built
  metadata cannot drift from `gradle.properties`.

### Adding dependencies

- Add normal Java libraries with `implementation` or `api` and a repository in
  `build.gradle` if Maven Central does not carry them.
- Add other Fabric mods with `modImplementation`, and declare their Maven
  repository in `build.gradle`.
- Use `include` only when you intentionally want to bundle a dependency inside
  your jar.

### Item models on 1.21

Minecraft 1.21 uses the classic item model layout:

```text
assets/examplemod/models/item/example_item.json
```

The separate `assets/<namespace>/items/<name>.json` item-model definition files
were introduced in 1.21.4. Do not copy those into a 1.21 project.

### Updating versions

1. Check [fabricmc.net/develop](https://fabricmc.net/develop/) for compatible
   Minecraft, Loader, Loom, and Fabric API versions.
2. Update `gradle.properties`.
3. If `java_version` changes, update `compatibilityLevel` in
   `examplemod.mixins.json` and the JDK used by CI.
4. Run `.\gradlew.bat clean build`.
5. Fix any mapping or API changes in the example code.
6. Run `.\gradlew.bat runServer` once to catch mixin and registration errors that
   compilation alone will not catch.

## 4. GitHub Actions CI

The workflow is `.github/workflows/build.yml`.

### Triggers

- Pushes to `main`, `master`, and `develop`.
- Pull requests.
- Pushes of tags matching `v*`.

### Steps

1. **Checkout** the repository.
2. **Validate the Gradle wrapper** with
   `gradle/actions/wrapper-validation`. This checks that the committed wrapper
   jar is the genuine Gradle wrapper.
3. **Install JDK 25** with `actions/setup-java` and enable the Gradle cache.
4. **Build** with `./gradlew build --stacktrace`.
5. **Upload artifacts** from `build/libs/` with `actions/upload-artifact`.
6. **Create a release** with the GitHub CLI (`gh release create`) when the ref is
   a `v*` tag.

### Permissions

The workflow requests:

```yaml
permissions:
  contents: write
```

This is needed for the release step. Pull requests from forks do not receive
write tokens. For a stricter supply chain, move `contents: write` to a separate
release job instead of granting it to the whole workflow.

### Artifacts vs releases

- **Workflow artifact**: retained by GitHub Actions for a limited time; useful
  for reviewing a specific run.
- **GitHub Release**: permanent, user-facing distribution with release notes.

Both the mod jar and the sources jar are included because the workflow uploads
`build/libs/*.jar`.

## 5. Release process

Use semantic versions such as `v1.0.0`, `v1.0.1`, or `v1.1.0-beta.1`.

1. Set `mod_version` in `gradle.properties`.
2. Run a clean build and, ideally, a server smoke test:

   ```powershell
   .\gradlew.bat clean build
   .\gradlew.bat runServer
   ```

3. Commit the version change.
4. Create and push the tag:

   ```bash
   git tag v1.0.0
   git push origin v1.0.0
   ```

5. GitHub Actions builds the project and creates a release named after the tag.
6. Open the release and verify the jar names and notes.

The release step uses `gh release create ... --generate-notes`. If a release
already exists for the tag, the step fails instead of overwriting it. Either
delete the old release or upload manually with:

```bash
gh release upload v1.0.0 build/libs/*.jar --clobber
```

For a prerelease, add `--prerelease` to the release command or mark it in the
GitHub UI.

## 6. Caching and performance

- `actions/setup-java` with `cache: gradle` caches the Gradle dependency cache
  and wrapper distribution in CI.
- Loom stores Minecraft and remap caches under
  `~/.gradle/caches/fabric-loom/<minecraft-version>/`.
- `org.gradle.parallel=true` and `org.gradle.configuration-cache=true` are
  enabled in `gradle.properties`.
- `build` does not normally need a clean. Use `clean build` for release builds
  or after changing build logic.

If a build behaves inconsistently, try:

```powershell
.\gradlew.bat --stop
.\gradlew.bat clean build
```

Deleting `~/.gradle/caches/fabric-loom/1.21` is a last resort and forces Loom
to re-download and re-split Minecraft.

## 7. Security and repository hygiene

- Keep the Gradle wrapper validated in CI.
- Do not commit `build/`, `run/`, `.gradle/`, IDE files, EULA files, logs, or
  locally installed mods.
- Do not put secrets in `gradle.properties`; CI uses the short-lived
  `${{ github.token }}` for releases.
- Pin third-party GitHub Actions to commit SHAs if your project requires strict
  supply-chain pinning. Dependabot can keep action and Gradle versions current.
- Read and respect the Minecraft EULA and the official mappings license printed
  by Loom. The template uses Mojang's official mappings for development.
- Review `.gitattributes` before changing line endings: `gradlew` must stay LF
  for Unix/CI, and `*.bat` must stay CRLF for Windows.

Optional hardening:

- Enable branch protection and require the `build` check before merging.
- Split release permissions into a dedicated job.
- Add a `CODEOWNERS` file for build and workflow changes.

## 8. Branching and PR workflow

A simple flow that fits this template:

1. Branch from `main` (or `master`): `feature/my-change`.
2. Make changes and run `.\gradlew.bat build` locally.
3. Open a pull request.
4. Wait for the `build` workflow and review the artifacts.
5. Merge to `main`.
6. Tag a release when the merged state is ready.

The workflow also runs on `develop`, so a Git Flow-style branch layout works
without edits.

## 9. Troubleshooting

| Symptom | Likely cause | Fix |
|---------|--------------|-----|
| `Permission denied: ./gradlew` | Executable bit lost | `chmod +x gradlew` and commit the mode change |
| `Unsupported class file major version` | Gradle is running on an old JDK | Run Gradle with JDK 25 |
| `Failed to setup Minecraft` | Network, proxy, or a corrupted Loom cache | Check `maven.fabricmc.net` access, remove the Loom cache for the version, retry |
| `No matching variant` / cannot resolve Fabric API | Minecraft/API/Loader versions do not match | Align the four versions in `gradle.properties` |
| Mixin apply error | Target method changed or mixin compat is wrong | Run `genSources`, inspect the target, update `examplemod.mixins.json` |
| Server exits immediately | EULA not accepted | Set `eula=true` in `run/eula.txt` |
| `Address already in use` | Another server is using port 25565 | Stop it or change `server-port` in `run/server.properties` |
| Release step gets 403 | Workflow lacks `contents: write` | Restore the permission or use a token with release access |
| `items/example_item.json` is ignored | That model format is for 1.21.4+ | Use `models/item/example_item.json` on 1.21 |
| CI is slow on every run | Caches missing or invalidated | Confirm `cache: gradle` is present and that the wrapper version is unchanged |

For build failures, prefer `--stacktrace` first:

```powershell
.\gradlew.bat build --stacktrace
```

Add `--info` or `--debug` only when the stack trace is not enough; the logs are
large.

## 10. Optional extensions

These are not configured in the template, but are natural next steps:

- **Unit tests**: add JUnit under `src/test/java`; `build` already runs `test`.
- **Static analysis**: add Checkstyle/Spotless and wire them into `check`.
- **Publishing**: add a `publish` repository or a Modrinth/CurseForge step after
  a successful build.
- **Dependency updates**: add Dependabot for `gradle` and `github-actions`.
- **Multi-version builds**: keep one branch per Minecraft version and reuse the
  same workflow; do not try to build incompatible versions in one jar.
- **Release automation**: add a changelog or conventional-commit generator before
  `gh release create`.
- **Artifact retention**: lower the retention period for pull-request artifacts
  while keeping release assets permanent.

## 11. Quick reference

| Goal | Command |
|------|---------|
| Full build | `.\gradlew.bat build` |
| Clean release build | `.\gradlew.bat clean build` |
| Run tests | `.\gradlew.bat test` |
| Development client | `.\gradlew.bat runClient` |
| Development server | `.\gradlew.bat runServer` |
| Decompile Minecraft | `.\gradlew.bat genSources` |
| New dev-log entry | `.\gradlew.bat devLog` |
| Update wrapper | `.\gradlew.bat wrapper --gradle-version 9.8.1` |
| Offline build | `.\gradlew.bat build --offline` |
| Release | `git tag v1.0.0 && git push origin v1.0.0` |
