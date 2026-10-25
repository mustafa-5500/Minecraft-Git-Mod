# Repository Format (v1)

Minecraft Git commits builds as text files inside a normal Git repository. The format is shared
with the Paper **Minecraft Git Plugin**; both tools read the same tree. This document describes
version 1 of the format.

## Tree layout

```text
<selection>/
├── meta.json
├── entities.csv                 # optional; only when entities were captured
├── y_<relativeY>/
│   ├── pp.csv                    # +x, +z
│   ├── pn.csv                    # +x, -z
│   ├── np.csv                    # -x, +z
│   └── nn.csv                    # -x, -z
```

Each named selection is a directory. Selection names match `[A-Za-z0-9][A-Za-z0-9_-]{0,31}`.
`y_<relativeY>` is the Y coordinate relative to the selection origin; `y_0` is the origin's
Y level and negative levels are `y_-1`, `y_-2`, and so on.

## Quadrant CSV files

A quadrant file contains one header line and then one row per relative X coordinate. Columns are
relative Z coordinates. Coordinates start at the quadrant boundary next to the origin and grow
away from it:

- `pp` rows are `x = 0, 1, 2, …`, columns are `z = 0, 1, 2, …`
- `pn` rows are `x = 0, 1, 2, …`, columns are `z = -1, -2, -3, …`
- `np` rows are `x = -1, -2, -3, …`, columns are `z = 0, 1, 2, …`
- `nn` rows are `x = -1, -2, -3, …`, columns are `z = -1, -2, -3, …`

Header:

```text
# minecraftgit-csv v1 origin=<originX>,<originY>,<originZ> y=<relativeY> quadrant=<pp|pn|np|nn>
```

Example (`pp.csv` for a build whose absolute origin is `-10,60,20`):

```text
# minecraftgit-csv v1 origin=-10,60,20 y=0 quadrant=pp
minecraft:oak_planks,,,minecraft:stone
,minecraft:air,,
```

Each cell is one of:

| Cell | Meaning |
|---|---|
| empty | coordinate is **not tracked** (no line will be generated for it) |
| `minecraft:air` | coordinate is tracked and currently air |
| `minecraft:stone` | tracked block state |
| `minecraft:oak_stairs[facing=north,half=bottom,...]` | tracked block state with sorted properties |
| `minecraft:chest[...]\|nbt\|<base64>` | tracked block state plus a block-entity payload |

The distinction between empty and air is load-bearing: removing a cuboid removes cells, while
placing air keeps a tracked cell and records the change.

Cells are RFC-4180 CSV fields. Block-state strings and Base64 payloads do not normally need
quoting, but comma/quote/newline characters are escaped by the writer.

### Block-state strings

The block state is encoded as:

```text
namespace:block[property=value,property=value,...]
```

Properties are sorted by name, so the same state always produces the same string. States without
properties omit the brackets. Unknown blocks or property values decode to air/defaults rather than
aborting an apply.

### Block-entity payloads

The Paper plugin used typed JSON. Minecraft Git uses the client's native NBT because it is both
fuller and simpler to round-trip:

```text
<block-state>|nbt|<Base64 of "nbt:" + SNBT compound>
```

For example, after Base64 decoding, the payload begins with `nbt:{id:"minecraft:chest",...}`.
The `nbt:` prefix is the marker that distinguishes native NBT from the plugin's JSON. When the mod
encounters a plugin JSON payload it applies the block state but skips the payload with a warning,
because guessing at a partial conversion would silently lose data.

## Entity CSV

When entities are captured, `entities.csv` is written next to `meta.json`:

```text
# minecraftgit-entities v1 origin=<originX>,<originY>,<originZ>
<rx>,<ry>,<rz>,<yaw>,<pitch>,<type>,<base64>
```

- `rx, ry, rz` are absolute-position-minus-origin doubles.
- `yaw` and `pitch` are floats.
- `type` is the entity registry id (`minecraft:item_frame`).
- The last column is Base64-encoded SNBT entity data (with an `nbt:` prefix in the mod). The
  entity UUID is intentionally not used for identity; apply matches by type and position.

If a selection contains no entities the file is omitted. The reader merges the origin recorded in
each header and rejects files with mismatched origins.

## `meta.json`

Every selection directory contains a metadata file committed with the tree:

```json
{
  "selection": "castle",
  "world": "world__overworld",
  "authorName": "Mustafa",
  "authorUuid": "…",
  "gameMode": "CREATIVE",
  "formatVersion": 1,
  "pluginVersion": "1.0.0",
  "origin": { "x": -10, "y": 60, "z": 20 },
  "previousOrigin": null,
  "reorigined": false,
  "dimensions": "20x10x30",
  "cuboidCount": 2,
  "blockCount": 1234,
  "entityCount": 3,
  "relativeCuboids": [
    { "minX": 0, "minY": 0, "minZ": 0, "maxX": 10, "maxY": 5, "maxZ": 10 }
  ],
  "createdAt": "2026-10-24T00:00:00Z"
}
```

`origin` is the absolute position the relative coordinates were measured from. `previousOrigin`
and `reorigined` record an explicit `/git selection reorigin`. `relativeCuboids` lets a pull create
local cuboids without scanning the CSV files.

`world` is the local world key (`<world>__<dimension>`). It is diagnostic: the repository tree
itself is world-independent because all coordinates are relative.

## Repository bootstrap

`/git init` creates a non-bare repository with:

- branch `main` (configurable),
- `README.md` explaining the layout,
- `.gitattributes` forcing LF for CSV/JSON,
- an initial commit message starting with `Initialize MinecraftGit repository`.

The pristine-init marker is deliberately shared with the plugin: pulling into a freshly initialized
world is treated as an unborn branch and can fast-forward over the README-only commit. A world with
build commits is still treated as diverged.

## Commit messages

The default first line is:

```text
<player>: <selection> - <n> blocks changed
```

for one selection, or:

```text
<player>: <n> selections - <n> blocks changed
```

for several. The body records:

```text
MinecraftGit-Commit: v1
Selections: castle, keep
Changes: +10 ~2 -1
Entities: 0
```

`/git commit <message>` replaces the first line. `/git commit --empty [message]` creates an
explicit checkpoint with no changed selection tree.

## Limits

Default limits are configurable in `config/minecraftgit/config.json`:

| Limit | Default |
|---|---|
| cuboids per selection | 64 |
| selection volume | 1,000,000 blocks |
| commit capture | 2,000,000 blocks |
| entities per selection | 2,000 |
| block-entity payload | 256 KiB |
| status capture | 250,000 blocks |
| apply blocks per tick | 4,096 |
| backups per selection | 10 |

The CSV format is versioned in every header so a future run-length or dimension extension can be
added without breaking readers.
