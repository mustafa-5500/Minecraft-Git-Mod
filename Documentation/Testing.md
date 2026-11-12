# Testing

This document covers the automated suite and the manual acceptance checklist for Minecraft Git.

## Automated tests

```powershell
.\gradlew.bat test
```

Tests are pure JVM (no Minecraft client/server is launched) and cover the parts that are easy to get
wrong: coordinate math, the CSV format, repository lifecycle, staging, commits, status, pull
planning, apply plans, and backups. The current suite has **64 test methods in 17 suites** and runs
from `./gradlew --offline test` with 0 failures.

| Suite | What it covers |
|---|---|
| `CuboidTest` | corner normalization, negative coordinates, volume, ordering, intersection |
| `SelectionTest` | origin seeding, E2 (removing the origin cuboid), E3 (reorigin), persistence fields |
| `SelectionMetadataStoreTest` | per-world/per-player JSON round-trip, isolation, corrupt-file errors |
| `QuadrantTest`, `CsvLineTest`, `QuadrantCsvTest` | quadrant math, CSV quoting, all-quadrant round-trip, sparse gaps, tracked air vs empty, entities, block-entity cells, mismatched origins |
| `BlockStateCodecTest` | canonical property ordering, stair state round-trip, unknown blocks → air |
| `BuildDiffTest` | added/changed/removed coordinates, entity changes, null base |
| `CommitMetaTest` | metadata JSON round-trip, missing/invalid files |
| `RepoServiceTest` | JGit init/bootstrap commit, workspace commit, push to a bare remote, fetch, fast-forward plan, unchanged commit |
| `SnapshotStoreTest` | gzipped snapshot round-trip, missing/truncated files |
| `StagingServiceTest` | stage/load/unstage, restrict after cuboid removal, reorigin rebase |
| `CommitServiceTest` | CSV tree + meta written, second commit diff, unchanged rejection, explicit empty commit |
| `StatusServiceTest` | untracked/clean/dirty/staged reports, coordinate samples, status volume cap |
| `ApplyPlanTest` | same-origin placements/removals, moved origin, backup restore coordinates |
| `BackupServiceTest` | latest backup, pruning, missing backup |
| `PullServiceTest` | remote fetch + prepared pull with no local selection, checkout export and local HEAD |

Run a single suite:

```powershell
.\gradlew.bat test --tests "org.almond.minecraftgit.serialization.QuadrantCsvTest"
```

The HTML report is `build/reports/tests/test/index.html`.

## Client smoke test

The development client must register the mod entrypoint and command tree without a server plugin:

```powershell
.\gradlew.bat runClient
```

Expected log lines:

```text
(minecraftgit) JGit is available to Minecraft Git
(minecraftgit) Minecraft Git initialized; local data is under .../config/minecraftgit
```

Then join a single-player world. If the client command dispatcher is registered correctly,
`/git help` lists the command groups. Commands run locally, so operator status is not required.

> In this development environment the Loom asset download is unavailable offline. Running
> `runClient -x downloadAssets` can still boot the client far enough to verify mod initialization,
> but missing assets cause many sound/resource warnings and no world can be joined comfortably.

## Manual acceptance checklist

These require an actual client/window and are not automated yet.

### Selection and stage

- [ ] `/git init` creates `config/minecraftgit/repos/<world>__overworld`.
- [ ] `/git wand` gives a named wooden shovel in single-player, or the sneak-with-item fallback
      works in multiplayer.
- [ ] Left/right clicking corners with the wand does **not** break or place blocks.
- [ ] The first box is held as pending; `/git selection create castle` consumes it.
- [ ] Adding a second box shows a confirmation prompt; `/git selection confirm` adds it.
- [ ] `/git selection cancel` discards a pending box without changing the selection.
- [ ] After confirming, `/git selection list` shows 2 cuboids.
- [ ] `/git selection show castle` draws particles for 8 seconds.
- [ ] Restart the game; the selection, origin, and active selection are still present.
- [ ] `/git selection remove 1` trims the staged snapshot if one exists.
- [ ] `/git selection reorigin` changes the origin; the staged snapshot is rebased.

### Commit

- [ ] Build a small structure (including a chest with items and a sign).
- [ ] `/git add castle` reports captured blocks/entities.
- [ ] `/git commit` writes a commit; `/git log` shows it.
- [ ] `git -C config/minecraftgit/repos/<world>__overworld show --stat HEAD` shows
      `castle/y_*/…csv`, `castle/entities.csv`, and `castle/meta.json`.
- [ ] Open `pp.csv` and confirm the header origin/Y/quadrant and the block-state strings.
- [ ] Change one block; `/git status` reports 1 changed.
- [ ] `/git diff castle` shows the changed relative coordinate.
- [ ] `/git add` + `/git commit` again; the second commit contains only the changed cells/tree.
- [ ] Remove a cuboid, stage, commit; cells outside the remaining cuboids disappear.
- [ ] Reorigin, stage, commit; `meta.json` has `reorigined: true` and the previous origin.

### Multiplayer behavior

- [ ] Join a remote server; `/git help`, `/git init`, `/git selection ...`, `/git add`,
      `/git commit`, `/git push`, `/git fetch` work.
- [ ] The wand sneak-with-item fallback does not send break/place packets.
- [ ] `/git pull`, `/git checkout`, and `/git pull --abort` refuse and explain that applying needs
      single-player; the repository is not moved.

### Two-world round trip

- [ ] World A: build, select, add, commit, `/git remote add origin <bare-repo>`, `/git push`.
- [ ] World B: `/git init`, `/git remote add origin <same>`, `/git pull`.
- [ ] The build is placed at the player's position; a local selection with the remote cuboids is
      created automatically.
- [ ] `/git status` is clean for the pulled selection (relative coordinates match HEAD).
- [ ] `/git pull --abort` restores the pre-pull blocks.
- [ ] `/git checkout <old-commit> castle` restores an older version without moving the branch.
- [ ] Re-capture the build after pull and confirm `/git diff` reports no changes.

### Failure paths

- [ ] `/git pull` with no remote reports the configured remote and suggests `/git remote add`.
- [ ] Push to a diverged remote is rejected without `--force`.
- [ ] Pull with diverged histories is refused; no branch movement.
- [ ] Applying a commit with an out-of-range Y reports skipped coordinates, not a clamp.
- [ ] Applying a plugin commit with JSON block-entity payloads applies block states and warns about
      the skipped payload.
- [ ] Player quits during a long capture/apply; no crash on return to the title screen.

## Known gaps

- Apply/checkout are not automated because they need an integrated server and a real world.
- Client-loaded capture in multiplayer cannot see unloaded chunks.
- Entity backups are not restored by `--abort`.
- Status has no capture cache; large selections are limited by `status-max-volume`.
- The mod is not tested against every modded block/entity state. Unknown states decode to air; an
  unsupported NBT payload is skipped with a warning.
