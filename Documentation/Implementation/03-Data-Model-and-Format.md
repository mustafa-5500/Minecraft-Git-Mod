# 3. Data Model and Storage

This chapter describes the data that moves through the system and where it is
stored. The authoritative repository format remains
[`../Git-Format.md`](../Git-Format.md); this chapter explains how the code models
it and why.

## 3.1 Coordinates

Two coordinate systems are used everywhere:

- **Absolute** — a real world block position (`x, y, z`), used by the world and
  by apply/debug code.
- **Relative** — `absolute - selection.origin`, used by the repository and by
  diffs.

`BlockPos` is the shared integer vector. `BlockSnapshot` and `EntitySnapshot` keep
**both** coordinate sets so the repository writer can use relative coordinates
without re-reading the world and apply/debug code can use absolute coordinates
without re-deriving the origin.

The origin is the absolute world position that relative coordinates are measured
from. It is:

- seeded by the first cuboid added to a selection
  (`Selection.addCuboid` -> `cuboid.smallestCorner()`),
- **not moved** when that cuboid is removed (plan edge case E2), because the
  remaining blocks still reference it,
- changed only by an explicit `/git selection reorigin` (E3), which moves it to
  the smallest corner of the remaining cuboids, or by creating a new selection.

When the origin changes, everything derived from it is rebased:

- `Snapshot.rebase(newOrigin)` keeps absolute positions and recomputes relative
  coordinates,
- `StagingService.rebaseStaged` rewrites a staged `.snap`,
- `ApplyPlan.between` detects `oldOrigin != targetOrigin` and treats the build as
  moved: the whole build is removed from the old absolute location and placed at
  the new one (E4).

Relative coordinates are canonical because a repository can then be applied at a
different local origin — for example a pull into a new world at the player's
current position.

## 3.2 Selection and Cuboid

`Selection` is a named, player-owned, multi-cuboid region in one world id:

```text
Selection
  name        String  1-32 chars, [A-Za-z0-9][A-Za-z0-9_-]*
  owner       UUID
  ownerName   String
  world       String  world id (includes dimension/server)
  origin      BlockPos
  cuboids     List<Cuboid> sorted by Cuboid.SMALLEST_CORNER_ORDER
  tracked     boolean (staged/committed at least once)
  createdAt / updatedAt  ISO-8601 strings
```

`Cuboid` is an inclusive box. Its canonical constructor normalises the two
corners, so callers do not need to know which corner the player clicked first.
Important properties:

- `SMALLEST_CORNER_ORDER` sorts by `(minX, minY, minZ, maxX, maxY, maxZ)`;
  `Selection` re-sorts after every add/remove (E1).
- `volume()` is saturated at `Long.MAX_VALUE` rather than overflowing.
- `Selection.volume()` sums cuboid volumes and counts overlaps twice; this is
  intentional: it is a cheap upper bound used for the capture caps. The actual
  capture de-duplicates positions with a `Map`/`Set`.
- `contains` is a union of the cuboids; empty space between cuboids is not
  tracked.

Selections are **local-only**. They contain absolute coordinates and a player
identity and are stored under `selections/<world>/<uuid>.json`, never committed.

## 3.3 Snapshot, BlockSnapshot, EntitySnapshot

A `Snapshot` is an immutable capture of one selection at one instant:

```text
Snapshot
  selection   String
  origin      BlockPos
  blocks      List<BlockSnapshot> sorted by (y, x, z)
  entities    List<EntitySnapshot> sorted by (y, x, z)
  capturedAt  Instant
```

`BlockSnapshot` carries:

- absolute `(x, y, z)` and relative `(rx, ry, rz)`,
- `blockData` — the canonical block-state string,
- `blockEntity` — optional payload (`nbt:<SNBT>`), or `null`.

`EntitySnapshot` carries:

- absolute `(x, y, z)` and relative `(rx, ry, rz)` doubles (entities are not
  grid-aligned),
- `yaw`, `pitch`,
- `type` — entity registry id,
- `data` — serialized entity payload (`nbt:<SNBT>`).

The empty-vs-air distinction is load-bearing:

- an **empty CSV cell** means the coordinate is not tracked at all; removing a
  cuboid makes cells disappear,
- `minecraft:air` means the coordinate is tracked and currently air; placing air
  records a change.

The writer omits empty cells; the reader only materialises non-empty cells. The
diff layer compares by relative coordinate and treats two blocks as the same only
when both `blockData` and `blockEntity` match (`BuildDiff.sameBlock`).

Useful snapshot operations:

| Operation | Behaviour |
|-----------|-----------|
| `Snapshot.empty(selection, origin)` | Zero blocks/entities; used as the "before" side of a first commit, status or diff. |
| `Snapshot.rebase(newOrigin)` | Same absolute world content, new relative coordinates. |
| `Snapshot.retain(selection)` | Keeps only blocks/entities whose absolute position is inside the selection's current cuboids; used after a cuboid is removed. |
| `Snapshot.byRelative()` / `byAbsolute()` | Maps used by diff/apply; a later duplicate key wins. |
| `Snapshot.dimensions()` | `sizeX x sizeY x sizeZ` of the captured blocks, or `0x0x0`. |

## 3.4 BuildDiff

`BuildDiff.between(before, after)` compares two snapshots by relative coordinate:

- `added` — coordinates present only in `after`,
- `changed` — coordinates present in both but with a different block state or
  block-entity payload,
- `removed` — coordinates present only in `before`,
- `entitiesAdded` / `entitiesRemoved` — multiset comparison of entity snapshots
  (identity is the full record, including type, position, rotation and payload),
- `unchanged` — count of identical block coordinates.

There is **no three-way merge** at this layer: "the final state of a coordinate
wins". That is sufficient for status, commit messages and apply plans, and it is
why diverged histories are refused rather than merged.

## 3.5 Commit metadata (`meta.json`)

Every selection directory in a commit contains a `meta.json` written by
`CommitMeta`:

| Field | Meaning |
|-------|---------|
| `selection` | Selection name. |
| `world` | World id where it was committed. |
| `authorName`, `authorUuid` | Player identity. |
| `gameMode` | Game mode at commit time. |
| `formatVersion` | `CommitMeta.FORMAT_VERSION` (1). |
| `pluginVersion` | Mod version from `GitServices.modVersion()` (the field name is historical). |
| `origin` | Absolute origin at commit time. |
| `previousOrigin` | Origin before a re-origin, or `null`. |
| `reorigined` | Whether `origin` differs from `previousOrigin`. |
| `dimensions` | `x x y x z` of the selection bounds. |
| `cuboidCount`, `blockCount`, `entityCount` | Capture statistics. |
| `relativeCuboids` | Cuboids translated relative to `origin`. |
| `createdAt` | ISO-8601 commit time. |

`meta.json` is part of the Git tree because a clone needs to know where the build
was and how to shift it onto a new origin. It is written after the CSV tree in a
temporary directory and then copied into the commit file map
(`CommitService.buildMeta`).

`CommitMeta.read` returns `Optional.empty()` for a missing or syntactically
invalid file; callers fall back to the snapshot/selection origin. A read error
other than syntax is a `RepoException`.

## 3.6 Local repository metadata (`meta/<world>.json`)

`RepoMetadata` is local-only state for one world id, stored as JSON by
`RepoMetadataStore`:

| Field | Meaning |
|-------|---------|
| `world` | World id. |
| `branch` | Branch to create/use (default `main`). |
| `remoteName` | Default remote name (default `origin`). |
| `remoteUrl` | Last known remote URL (diagnostic). |
| `authorName`, `authorEmail` | Commit identity. |
| `lastSelection` | Active selection name, used by commands without an explicit selection. |
| `createdAt` | Creation time. |

It maps mod concepts (world, default remote, author) onto Git concepts and may
differ between machines that share a repository. `RepoMetadata.normalize` fills
blanks introduced by older or hand-edited files. `RepoManager` caches it per
world and writes it through on every change.

## 3.7 Repository tree

The committed tree is:

```text
<selection>/
├── meta.json
├── entities.csv                 # optional
└── y_<relativeY>/
    ├── pp.csv                   # +x, +z
    ├── pn.csv                   # +x, -z
    ├── np.csv                   # -x, +z
    └── nn.csv                   # -x, -z
```

The writer (`QuadrantCsvWriter`) groups blocks by relative Y and quadrant. Each
CSV starts with a header recording the format version, absolute origin, Y level
and quadrant:

```text
# minecraftgit-csv v1 origin=<x>,<y>,<z> y=<ry> quadrant=<pp|pn|np|nn>
```

Rows are relative X, columns are relative Z; row/column 0 is the boundary next to
the origin and grows away from it (`Quadrant.rowToX`, `columnToZ`). Cells are
RFC-4180 fields (`CsvLine`). A cell is either:

- empty — not tracked,
- a block-state string — tracked block,
- `block-state|nbt|<base64>` — tracked block with a block-entity payload.

Block states are canonicalised by `BlockStateCodec` as
`namespace:block[prop=value,...]` with properties sorted by name. Unknown blocks
or properties decode to air/default values instead of aborting an apply.

Block entities use native NBT (`nbt:` prefix) inside the Base64 cell
(`BlockEntityCodec`), which is fuller than the plugin's typed JSON. A plugin JSON
payload is left untouched by the writer and skipped with a warning on apply; the
block state still applies. The reverse is also lossy: the plugin cannot parse
`nbt:` block-entity payloads, but it still applies the block state.

Entities are written to `entities.csv` when present:

```text
# minecraftgit-entities v1 origin=<x>,<y>,<z>
<rx>,<ry>,<rz>,<yaw>,<pitch>,<type>,<base64>
```

The reader (`QuadrantCsvReader`) walks the selection directory, validates every
header, and requires all files to agree on the origin. Mismatched origins are a
`SerializationException` ("the selection may have been partially re-origined").

## 3.8 Staging format (`*.snap`)

Staging is deliberately **not** the commit format. It is local, machine-only,
possibly large, and never shared, so `SnapshotStore` writes a gzipped binary
stream with:

- magic `MCGITSNP` (8 bytes),
- format version `1`,
- selection name string, origin `(x,y,z)`,
- block count + each block's absolute/relative coordinates, block data,
  optional block-entity payload,
- entity count + each entity's absolute/relative doubles, yaw/pitch, type,
  payload.

Every string/count is bounds-checked (`MAX_BLOCKS`, `MAX_ENTITIES`,
`MAX_BLOCK_DATA_BYTES`, `MAX_ENTITY_BYTES`, `MAX_SELECTION_BYTES`). Writes go to a
`.tmp` sibling and are moved into place with `ATOMIC_MOVE` (falling back to a
plain replace). A missing/truncated/unknown-magic file becomes a
`StagingException`.

Staging files live at:

```text
staging/<safe world id>/<player uuid>/<selection>.snap
```

They are keyed by world + player + selection, never by repository, so a player
can re-init a repository without losing staged work (and vice versa).

## 3.9 Backups

A pre-apply backup is also a `SnapshotStore` file, at:

```text
backups/<safe world id>/<player uuid>/<selection>/<millis>-<seq>.snap
```

`BackupService.store` writes the snapshot, then prunes the directory to
`config.maxBackups`. `latest` returns the lexicographically last file (the newest
because of the `millis-sequence` name). Backups contain blocks and block entities;
entity backups are not included, so abort does not restore entities.

## 3.10 Configuration schema

`ModConfig` is the persisted schema (`config/minecraftgit/config.json`). Defaults
are conservative for client memory/tick budgets:

| Setting | Default | Purpose |
|---------|---------|---------|
| `debug`, `debugBlocks` | `false` | Debug log and per-block logging. |
| `branch` | `main` | Branch created/used. |
| `defaultRemote` | `origin` | Remote when metadata has none. |
| `wandItem` | `minecraft:wooden_shovel` | Multiplayer fallback wand item. |
| `prefix` | `[Git] ` | Chat prefix. |
| `committerName`, `committerEmail` | `MinecraftGit`, `minecraftgit@localhost` | Initial identity. |
| `maxCuboidsPerSelection` | 64 | Cuboid count cap. |
| `maxVolumePerSelection` | 1,000,000 | Selection volume cap. |
| `maxBlocksPerCommit` | 2,000,000 | Capture/commit volume cap. |
| `maxEntitiesPerSelection` | 2,000 | Entity cap per capture. |
| `maxBlockEntityBytes` | 262,144 | Per-payload cap (block entity or entity). |
| `statusMaxVolume` | 250,000 | Status skips larger selections. |
| `blocksPerTick` | 4,096 | Apply write batch. |
| `maxBackups` | 10 | Backups kept per selection. |
| `renderSelections` | `true` | Particle preview on/off. |
| `renderBudget` | 256 | Particle budget per render tick pass. |

`ModConfig.load` creates the file if missing, normalises blanks/non-positive
values, and saves it back atomically. A corrupt file is reported once and defaults
are used; the old file is left untouched so a player can fix it.

## 3.11 Invariants

These are the invariants worth checking when changing the data layer:

1. A snapshot origin is never `null`.
2. Relative coordinates equal `absolute - origin` for both blocks and entities.
3. A snapshot's blocks/entities are sorted and de-duplicated by coordinate.
4. `Selection.origin` does not move when a cuboid is removed.
5. Removing a cuboid trims staged data (`retain`) but does not move the origin.
6. Re-origining rebases staged data and makes the next commit record
   `reorigined=true` and `previousOrigin`.
7. All CSV files in one selection agree on the origin.
8. An empty cell means untracked; `minecraft:air` means tracked air.
9. Staged files write atomically; a crash cannot leave a half-written `.snap`.
10. `meta.json` is never deleted by `QuadrantCsvWriter.cleanPrevious`.
11. World ids are sanitised before becoming directory names.
12. A repository path is rejected if it escapes the repository root
    (`RepoService.safeResolve`, `exportSelectionAt`).
