# Minecraft Git — Implementation Documentation

This folder explains **how the mod is implemented**, from the high-level runtime
picture down to the function calls that move data between classes. It complements
the generated per-class API/SDD pages under [`Documentation/main`](../main) and
[`Documentation/client`](../client), and the format, testing and operations guides
in [`Documentation/Git-Format.md`](../Git-Format.md),
[`Documentation/Testing.md`](../Testing.md) and
[`Documentation/DEVOPS.md`](../DEVOPS.md).

## Reading order

| # | Document | Level | What it answers |
|---|----------|-------|-----------------|
| 1 | [01-Overview.md](01-Overview.md) | High | What the mod does, how it runs, and which constraints shape the design. |
| 2 | [02-Architecture.md](02-Architecture.md) | High / mid | Layers, packages, the service graph, threading and lifecycle. |
| 3 | [03-Data-Model-and-Format.md](03-Data-Model-and-Format.md) | Mid | Selections, cuboids, snapshots, coordinates, repository and local storage. |
| 4 | [04-Workflows.md](04-Workflows.md) | Mid | End-to-end flows for every player-visible operation. |
| 5 | [05-Low-Level-Data-Flow.md](05-Low-Level-Data-Flow.md) | Low | The exact cross-class call chains and the data passed at each hop. |
| 6 | [06-Class-Reference.md](06-Class-Reference.md) | Low | One entry per production class, its responsibility and collaborators. |
| 7 | [07-Error-Handling-and-Concurrency.md](07-Error-Handling-and-Concurrency.md) | Low | Exceptions, user-facing errors, locks, executors and failure ordering. |
| 8 | [08-Diagram-Guide.md](08-Diagram-Guide.md) | — | How to read/render the PlantUML diagrams and regenerate the code-level ones. |

## Diagram levels

The PlantUML sources live in [`diagrams/`](diagrams). They deliberately show five
levels, from the whole system down to individual function calls:

| Level | Diagram | Shows |
|-------|---------|-------|
| L1 | [`level-1-system-context.puml`](diagrams/level-1-system-context.puml) | The player, the Minecraft client/integrated server, the mod, local storage and a Git remote. |
| L2 | [`level-2-component-architecture.puml`](diagrams/level-2-component-architecture.puml) | Services and packages inside the mod and their dependencies. |
| L2 | [`level-2-package-dependencies.puml`](diagrams/level-2-package-dependencies.puml) | Allowed dependency direction between Java packages/source sets. |
| L3 | [`level-3-data-model.puml`](diagrams/level-3-data-model.puml) | The model classes and the relationships that carry build data. |
| L3 | [`level-3-data-flow-overview.puml`](diagrams/level-3-data-flow-overview.puml) | How data moves between the world, staging, Git and backups. |
| L4 | `level-4-sequence-*.puml` | One sequence per player workflow, at class/method granularity. |
| L5 | `level-5-function-data-flow-*.puml` | The lowest level: data passed between functions of different classes. |

The L4/L5 split is intentional. An L4 sequence answers "which objects talk in what
order"; an L5 diagram/table answers "which function passes which value to which
function in another class". [05-Low-Level-Data-Flow.md](05-Low-Level-Data-Flow.md)
contains the same information as tables so it can be reviewed in a diff without a
PlantUML renderer.

## Source map

| Source set / package | Responsibility |
|----------------------|----------------|
| `src/main/.../model` | Pure build data: positions, cuboids, selections, snapshots, diffs. No I/O, no Minecraft client types. |
| `src/main/.../serialization` | CSV tree, block-state strings, block-entity/entity SNBT, CSV quoting. |
| `src/main/.../repo` | JGit facade, local repository metadata, remotes, fetch/push, pull planning, commit trees. |
| `src/main/.../selection` | Durable per-world/per-player selections and their JSON store. |
| `src/main/.../staging` | Gzipped staged snapshots and their on-disk store. |
| `src/main/.../commit` | Staged snapshot -> commit tree -> JGit commit. |
| `src/main/.../status` | HEAD vs staged vs current comparisons. |
| `src/main/.../sync` | Reading remote commit trees into snapshots for apply. |
| `src/main/.../backup` | Pre-apply snapshots and pruning. |
| `src/main/.../world` | World capture and the apply plan/result records. |
| `src/main/.../config`, `.../debug`, `.../util` | Configuration, debug logging, names and shared exceptions. |
| `src/client/.../client` | Client entrypoint, service graph, chat formatting, world identity. |
| `src/client/.../command` | Brigadier `/git` command tree. |
| `src/client/.../selection` | Wand item/events, per-player session state, particle preview. |
| `src/client/.../world` | Choosing the thread that captures/applies and running the apply job. |

## Conventions used here

- **Absolute** coordinates are world block coordinates; **relative** coordinates
  are `absolute - selection.origin`, and relative coordinates are what Git stores.
- A **selection** is a named, per-player, multi-cuboid region in one world key.
  A **cuboid** is one inclusive box. A **snapshot** is the captured contents of a
  selection at one moment.
- "World id" means `<safe world name>__<safe dimension name>` (plus the server
  address in multiplayer), e.g. `world__overworld`.
- "HEAD" means the commit currently checked out in the per-world repository.
- The diagrams and prose describe the code at the version documented here; if an
  inline comment and this guide disagree, the source is authoritative.

## Rendering the diagrams

The `.puml` files are plain text. Render them with PlantUML (VS Code extension,
`plantuml` CLI, or the PlantUML server), for example:

```bash
plantuml -tsvg Documentation/Implementation/diagrams/*.puml
```

The L5 diagrams are also described as call tables, so a renderer is not required
to follow the lowest-level data flow.
