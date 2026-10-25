# Minecraft Git

A client-side **Fabric** mod that treats a Minecraft build like a Git repository. Select a build
with cuboids, stage it, commit it to a real Git repository, and push or pull builds between worlds
and players.

This is the client-side counterpart of the
[Minecraft Git Plugin](https://github.com/mustafa-5500/Minecraft-Git-Plugin). The repository format —
quadrant CSV files per Y level, `entities.csv`, and `meta.json` — is shared, so builds committed by
the plugin can be fetched and applied by this mod (and vice versa, with the block-entity caveat
below).

## Status

Version **1.0.0** implements the v1 workflow:

- named, multi-cuboid selections with a wand and particle preview
- snapshot capture of blocks, block entities, and entities
- `/git add` / `/git rm` staging
- commits with coordinate-level diffs and commit metadata
- `/git status` and `/git diff`
- local and remote Git operations (JGit, no external Git binary)
- fast-forward pull and checkout into single-player worlds
- automatic pre-apply backups and `/git pull --abort`

Applying builds is deliberately limited to **single-player** worlds. In multiplayer the mod can
capture client-loaded chunks, commit, and use remotes, but it will not write to the server's world.

## Requirements

- Minecraft **1.21** with **Fabric Loader 0.19.5+**
- **Fabric API 0.102.0+1.21**
- Java **21** for the mod runtime; JDK **25** for Gradle/Loom
- No external Git installation; JGit 6.10 is bundled in the jar

## Build

```powershell
.\gradlew.bat build
```

Outputs:

- `build/libs/minecraftgit-1.0.0.jar` — install this in the Fabric `mods` folder
- `build/libs/minecraftgit-1.0.0-sources.jar` — sources jar

After the first successful build, an offline build works with the cached dependencies:

```powershell
.\gradlew.bat --offline build
```

Run the test suite with:

```powershell
.\gradlew.bat test
```

## Run the development client

```powershell
.\gradlew.bat runClient
```

The run directory is `run/`. The first launch downloads Minecraft libraries and assets; later
launches are faster. `runServer` is not useful for this mod because it is client-only.

## Quick start (single-player)

1. Join a single-player world.
2. Run `/git init`, then `/git config author <name> <email>` if you want a custom identity.
3. Run `/git wand` to get the Git Wand (single-player). In multiplayer, hold the configured wand
   item (default wooden shovel) while sneaking, or use `/git selection pos1` / `pos2`.
4. Left-click one corner and right-click the opposite corner. The first completed box is held as a
   pending cuboid until you create or select a selection.
5. `/git selection create castle` creates the selection and consumes the pending cuboid.
   Add more boxes by clicking two corners while `castle` is the active selection.
6. `/git add castle` captures and stages the build.
7. `/git commit` writes the commit. `/git log` shows it.
8. Edit the build. `/git status` and `/git diff castle` show what changed.
9. `/git remote add origin <path-or-url>` and `/git push` to share it.
10. In another single-player world, `/git pull` (after adding the same remote) applies the build and
    fast-forwards the local branch. `/git pull --abort` restores the pre-apply backup.

## Commands

| Command | Purpose |
|---|---|
| `/git help` | Command overview |
| `/git version` | Mod/Minecraft versions |
| `/git init` | Create this world/dimension's repository |
| `/git repo` | Repository path, branch, HEAD, author, remote |
| `/git config show` | Effective settings |
| `/git config author <name> <email>` | Commit identity |
| `/git config debug` | Toggle the debug log |
| `/git config blocks` | Toggle per-block debug logging |
| `/git remote add <name> <url>` | Add a local path or HTTP remote |
| `/git remote remove <name>` | Remove a remote |
| `/git remote list` | List remotes |
| `/git log [count]` | Recent commits |
| `/git push [--force]` | Push the current branch |
| `/git fetch [remote]` | Fetch without applying |
| `/git selection create <name>` | Create and activate a selection |
| `/git selection list` | List selections and the active one |
| `/git selection select <name>` | Change the active selection |
| `/git selection show [name]` | Particle-highlight a selection for 8 seconds |
| `/git selection add [name]` | Add the pending wand cuboid |
| `/git selection remove <index> [name]` | Remove a sorted cuboid |
| `/git selection reorigin [name]` | Move the origin to the smallest corner |
| `/git selection delete <name>` | Delete a selection and its staging |
| `/git selection info [name]` | Origins, bounds, cuboids, tracking |
| `/git selection wand` / `/git wand` | Give the wand (single-player) |
| `/git selection pos1` / `pos2` | Set a corner from the block you are looking at |
| `/git add [name]` | Capture and stage a selection |
| `/git rm [name]` | Untrack and discard staging |
| `/git commit [message]` | Commit all staged selections |
| `/git commit --empty [message]` | Create an explicit empty checkpoint |
| `/git status` | Compare HEAD, staging, and the world |
| `/git diff [name]` | Coordinate-level diff summary and samples |
| `/git pull [name] [--remote r]` | Fetch, apply, then fast-forward |
| `/git pull --abort [name]` | Restore the latest pre-apply backup |
| `/git checkout <commit> [name]` | Apply an old commit without moving the branch |

Selection names are 1–32 characters of letters, digits, `_` or `-`, starting with a letter or digit.

## Local data

Everything is under `config/minecraftgit/` in the game directory:

```text
config/minecraftgit/
├── config.json
├── repos/<world>__<dimension>/       # real Git repositories
├── meta/<world>__<dimension>.json    # local repo settings, active selection
├── selections/<world>__<dimension>/  # per-player selection metadata
├── staging/<world>__<dimension>/     # gzipped staged snapshots
├── backups/<world>__<dimension>/     # pre-apply backups for --abort
├── tmp/                              # transient commit/pull trees
└── logs/                             # optional debug logs
```

A world key includes the dimension (`world__overworld`, `world__the_nether`, …), so dimensions
never share coordinates or selections. In multiplayer the key also includes the server address, so
two servers with the same display name do not share local state. Repositories live outside the save
folder, mirroring the plugin's data-folder decision; copy `config/minecraftgit/repos/` when moving a
repository to another machine.

## Repository format

The commit format is documented in [Documentation/Git-Format.md](Documentation/Git-Format.md).
In short:

```text
<selection>/y_<y>/<quadrant>.csv
<selection>/entities.csv
<selection>/meta.json
```

Quadrants are `pp` (+x,+z), `pn` (+x,−z), `np` (−x,+z), and `nn` (−x,−z). Cells are block-state
strings; an empty cell means “not tracked”, while `minecraft:air` means “tracked and air”. Block
entities are carried in the cell as Base64 native NBT with an `nbt:` prefix.

**Compatibility note:** the Paper plugin stored block-entity data as typed JSON. The mod stores
fuller native NBT. When applying a plugin commit, block states and entities still apply, but the
plugin's JSON block-entity payloads are skipped with a warning. Commits made by the mod are readable
by the plugin's CSV reader; the plugin would fail to parse the `nbt:` block-entity payload and skip
that block entity, while still applying the block state.

## Limitations

- **Single-player for writes.** Pull/checkout/abort require the integrated server. Multiplayer
  remains read/capture/commit/push/fetch only.
- **Pulls are fast-forward-only.** Diverged histories are reported, not merged.
- **Partial pulls are refused when the remote has several selections.** A fast-forward moves the
  branch to the whole remote tree; applying only one selection would leave the others in HEAD but
  not in the world. Pull all selections together.
- **Existing local cuboids are kept.** If a remote build extends outside a local selection that
  already has cuboids, the extra blocks are not tracked; grow the selection and pull again.
- **No branch UX.** Commits go to the repository's configured branch (default `main`).
- **Captures are volume-capped and synchronous on their owning thread.** Defaults: 1M blocks per
  selection, 2M per commit, 250k for status, 2,000 entities. A very large build can still cause a
  tick/frame spike.
- **Client capture in multiplayer only sees loaded chunks.** Single-player captures through the
  integrated server and can load chunks on demand.
- **Entity backups are not included.** `/git pull --abort` restores blocks and block entities.
- **SSH remotes are not bundled.** Local paths and anonymous HTTPS work; the optional JGit SSH
  transport module is not included.
- **Status is not cached.** Every `/git status` re-captures the tracked selections it can afford to
  check.

## Development

- Implementation plan: [Development-Notes/Plan/Git Mod Implementation Plan.md](Development-Notes/Plan/Git%20Mod%20Implementation%20Plan.md)
- Format: [Documentation/Git-Format.md](Documentation/Git-Format.md)
- Build/CI/release guide: [Documentation/DEVOPS.md](Documentation/DEVOPS.md)
- Testing and manual checklist: [Documentation/Testing.md](Documentation/Testing.md)
- Dev log: `.\gradlew.bat devLog` (requires the `Utils` submodule and Python)

The project uses Fabric Loom's split source sets:

- `src/main` — model, serialization, repository, staging, commit, status, and apply-plan code.
  These classes may use common Minecraft classes but never client-only ones.
- `src/client` — client entrypoint, `/git` commands, wand events, rendering, world capture, and
  integrated-server apply.

## Credits

Build setup follows the Fabric example mod and documentation. JGit is licensed under the Eclipse
Distribution License; its jar is nested in the mod jar by Loom.

## License

CC0-1.0, matching the template this repository started from.
