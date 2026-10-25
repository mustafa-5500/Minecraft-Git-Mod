**Date:** 2026-10-23
Previous Entry: None (this is the first entry)

**Developer:**
    - Mustafa Faheem

**Comments:**
Creating a Minecraft Mod template repository to make client side mod development easier.

The original repository was a Paper plugin template. I replaced the Paper/Shadow
build with Fabric Loom so the same repository can bootstrap client and server
Fabric mods. The final template targets Minecraft 1.21, Fabric Loader 0.19.5,
Fabric API 0.102.0+1.21, and Loom 1.19.3, with Java 21 bytecode and JDK 25
running Gradle/Loom.

I initially built against the unobfuscated 26.3 game and then retargeted the
template to 1.21. That required switching to Loom's `fabric-loom-remap` plugin,
enabling official Mojang mappings, using `modImplementation` dependencies,
porting the example to 1.21 APIs (`ResourceLocation`, `ItemGroupEvents`, the
classic `models/item` layout), and lowering the mixin compatibility level to
Java 21. The dedicated server was started once to confirm the mod, command
registration, and mixin all load on 1.21.

I also added `Documentation/DEVOPS.md` as an operations guide for the Gradle wrapper,
Loom, GitHub Actions, artifacts, releases, version updates, caching, security,
and troubleshooting. CI now caches Gradle dependencies through `setup-java`.

Paper-specific pieces (Shadow, `plugin.yml`, `paper-api`, server jar copying, and
the old Paper IDE launch config) were removed. The existing `devLog` task and
`Utils` submodule were preserved.

_Note: You can add `~~` at the start and end of a line to cross it out; So if an action or goal is complete, crossing it out will communicate that to other developers._
**Goals:**
    - ~~Create a Fabric mod template repository that builds and runs out of the box.~~
    - ~~Replace the Paper plugin Gradle workflow with Fabric Loom.~~
    - ~~Document customization and version updates for future mods.~~
    - ~~Document the build, CI, and release tooling for future maintainers.~~
    - Keep the template current when Minecraft and Fabric versions change.

**Actions:**
    - ~~Rewrite `build.gradle`, `settings.gradle`, and add `gradle.properties` for Fabric Loom.~~
    - ~~Add the Gradle 9.8.1 wrapper, `.gitignore`, `.gitattributes`, and CI workflow.~~
    - ~~Add the example mod: initializer, item registry, command, mixin, client entrypoint, assets, and translations.~~
    - ~~Retarget the template to Minecraft 1.21 and Java 21 with Loom's remap plugin and official mappings.~~
    - ~~Port the example to 1.21 APIs and the classic item model layout.~~
    - ~~Add `Documentation/DEVOPS.md` and enable Gradle caching in CI.~~
    - ~~Build the mod and run a dedicated server to verify startup and mixin loading.~~
    - ~~Rewrite the README for Fabric.~~
