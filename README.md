# Minecraft Git (client mod)

A client-side **Fabric** mod that treats a Minecraft build like a Git repository. Select a build
with cuboids, stage it, commit it to a real Git repository, and push/pull builds between worlds and
players.

This is the client-side counterpart of the
[Minecraft Git Plugin](https://github.com/mustafa-5500/Minecraft-Git-Plugin). The repository format
(quadrant CSV files per Y level, entity CSV, `meta.json`) is shared, so builds committed on a Paper
server can be inspected and pulled by this mod.

> **Status:** implementation in progress. The bootstrap phase provides the client shell, config,
> data layout, and the `/git` command root. Repository, selection, commit, sync, and apply phases are
> landing next. See [Development-Notes/Plan/Git Mod Implementation Plan.md](Development-Notes/Plan/Git%20Mod%20Implementation%20Plan.md).

## Requirements

- Minecraft **1.21** (Fabric), Fabric Loader 0.19.5+, Fabric API 0.102.0+1.21
- Java **21** to run the mod; JDK 25 is used by Gradle/Loom
- No external Git installation: JGit is bundled in the mod jar

## Build

```powershell
.\gradlew.bat build
```

The mod jar is `build/libs/minecraftgit-1.0.0.jar`. A sources jar is produced next to it.

Offline build (after the first successful online build):

```powershell
.\gradlew.bat --offline build
```

## Run

```powershell
.\gradlew.bat runClient
```

The development run directory is `run/`. Single-player worlds are the supported environment for
applying builds; repository commands work in multiplayer too.

## Commands (planned and current)

| Command | Purpose | Status |
|---|---|---|
| `/git help` | List commands | available |
| `/git version` | Mod/Minecraft versions | available |
| `/git config show` | Effective settings | available |
| `/git config debug` | Toggle the debug log | available |
| `/git config blocks` | Toggle per-block debug logging | available |
| `/git init` | Create a repository for this world/dimension | next |
| `/git repo` | Repository info | next |
| `/git config author <name> <email>` | Commit identity | next |
| `/git remote add|remove|list` | Remotes | next |
| `/git log [count]` | Recent commits | next |
| `/git push`, `/git fetch` | Transport | next |
| `/git selection ...`, `/git wand` | Named multi-cuboid selections | next |
| `/git add`, `/git rm` | Stage/untrack a selection | next |
| `/git commit [message]` | Commit staged selections | next |
| `/git status`, `/git diff` | Compare HEAD/staged/world | next |
| `/git pull [selection]`, `/git pull --abort` | Apply a fast-forward / undo | next |
| `/git checkout <commit> [selection]` | Materialize an old commit | next |

## Local data

Everything is under `config/minecraftgit/`:

- `config.json` — settings and limits
- `repos/<world>__<dimension>/` — Git repositories
- `selections/` — named selections (never committed)
- `staging/` — gzipped staged snapshots (never committed)
- `backups/` — pre-apply snapshots for `--abort`
- `meta/` — per-world repo metadata
- `logs/` — optional debug logs

## Design notes and limitations

- **Single-player for writing.** Applying a pull/checkout writes through the integrated server. In
  multiplayer the mod refuses to modify the world; it can still capture client-loaded chunks,
  commit, and use remotes.
- **Client capture is limited to loaded chunks** in multiplayer. Single-player captures through
  the integrated server.
- **Pull is fast-forward-only** in v1. Diverged histories are reported instead of merged.
- **Block entities use native NBT** (`nbt:`-prefixed values inside the CSV cell). Plugin commits
  that used the plugin's typed JSON block-entity payloads still apply their block states; the
  payload itself is skipped with a warning.
- **JGit 6.10** is bundled with `include`; no Git binary is downloaded or executed.

## Development

- Plan: [Development-Notes/Plan/Git Mod Implementation Plan.md](Development-Notes/Plan/Git%20Mod%20Implementation%20Plan.md)
- DevOps: [Documentation/DEVOPS.md](Documentation/DEVOPS.md)
- Dev log: `.\gradlew.bat devLog` (requires the `Utils` submodule and Python)
