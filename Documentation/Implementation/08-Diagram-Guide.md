# 8. Diagram Guide

The diagrams are plain PlantUML (`.puml`) under [`diagrams/`](diagrams). They are
committed as sources so they review in a diff and can be re-rendered without a
binary asset in the repository.

## 8.1 What each level means here

| Level | File(s) | Purpose | Typical question answered |
|-------|---------|---------|---------------------------|
| L1 — system context | `level-1-system-context.puml` | The player, Minecraft client, integrated server, the mod, local data and a Git remote. | Who/what talks to the mod, and where does data leave the machine? |
| L2 — components | `level-2-component-architecture.puml` | Services/packages inside the mod and their dependencies. | Which service owns which responsibility? |
| L2 — packages | `level-2-package-dependencies.puml` | Allowed dependency direction between Java packages/source sets. | Can this package depend on that one? |
| L3 — data model | `level-3-data-model.puml` | Model classes and the data they carry. | What is a `Snapshot`/`Selection`/`BuildDiff` made of? |
| L3 — data flow | `level-3-data-flow-overview.puml` | Where build data moves: world -> snapshot -> staging -> commit -> remote -> apply -> backup. | What are the major data hops? |
| L4 — workflows | `level-4-sequence-*.puml` | One class/method sequence per player workflow. | In what order do objects collaborate? |
| L5 — function data flow | `level-5-function-data-flow-*.puml` | Cross-class function calls with the data passed on each edge. | Which function passes which value to which function in another class? |

The L5 diagrams are the answer to the issue's "lowest level being the data flow
between functions in different classes". They are deliberately narrower than the
L4 sequences: they focus on the edges that carry `BlockPos`, `Cuboid`,
`Selection`, `Snapshot`, `CaptureResult`, `StagedSelection`, `CommitRequest`,
`CommitResult`, `PullPlan`, `PreparedPull`, `ApplyPlan` and `ApplyResult`.

## 8.2 Catalogue

| Diagram | Workflow / subject |
|---------|--------------------|
| `level-1-system-context.puml` | System context and trust boundary. |
| `level-2-component-architecture.puml` | Service graph and component dependencies. |
| `level-2-package-dependencies.puml` | Package/source-set dependency rules. |
| `level-3-data-model.puml` | Model records/classes and relationships. |
| `level-3-data-flow-overview.puml` | End-to-end data hops. |
| `level-4-sequence-selection-wand.puml` | Wand click -> corner -> cuboid -> selection. |
| `level-4-sequence-staging-commit.puml` | `/git add` -> staging -> `/git commit` -> JGit. |
| `level-4-sequence-status-diff.puml` | `/git status` and `/git diff`. |
| `level-4-sequence-pull-apply-abort.puml` | `/git pull`, apply job, `/git pull --abort`. |
| `level-4-sequence-checkout.puml` | `/git checkout <commit>`. |
| `level-4-sequence-init-remote.puml` | `/git init` and remote/fetch/push/log. |
| `level-5-function-data-flow-selection.puml` | Selection/wand cross-class function edges. |
| `level-5-function-data-flow-staging-commit.puml` | Add/stage/commit cross-class function edges. |
| `level-5-function-data-flow-status.puml` | Status/diff cross-class function edges. |
| `level-5-function-data-flow-pull-plan.puml` | Pull plan + remote-tree preparation function edges. |
| `level-5-function-data-flow-pull-apply.puml` | Apply job + backup + fast-forward function edges. |
| `level-5-function-data-flow-pull-abort-checkout.puml` | Abort/checkout function edges. |

The companion tables in [05-Low-Level-Data-Flow.md](05-Low-Level-Data-Flow.md)
are the authoritative text for the same edges; if a diagram and a table
disagree, treat the source and then the table as correct.

## 8.3 Rendering

With the PlantUML CLI:

```bash
# one SVG next to each source
plantuml -tsvg Documentation/Implementation/diagrams/*.puml
```

With the VS Code PlantUML extension, open a `.puml` file and use
"PlantUML: Preview Current Diagram". With the PlantUML server, paste the file's
contents (the sources use only standard PlantUML syntax; no custom preprocessor
includes).

The diagrams intentionally avoid external includes, images and custom fonts, so
they render in the default PlantUML runtime. UTF-8 text is fine.

## 8.4 Regenerating the code-level decomposition

The `Utils` submodule provides two generators used by this repository:

- `Utils/documentation-generator/generate_docs.py` — creates/merges the
  per-class API and SDD pages under `Documentation/<source path>/`. It preserves
  authored prose and only fills newly discovered fields/methods. The Gradle task
  is:

  ```bash
  ./gradlew documentation
  ```

  It runs `python Utils/documentation-generator/generate_docs.py src Documentation`.
- `Utils/decomposition-generator/generate_decomp.py` — creates per-file PlantUML
  function-call decomposition diagrams (light/dark/none themes). Example:

  ```bash
  python Utils/decomposition-generator/generate_decomp.py src Documentation/Implementation/decompositions light
  ```

  That generator only follows calls to methods defined in the same file, so it
  complements (does not replace) the cross-class L5 diagrams here. To add a new
  source file to the documentation set, run both generators and then fill any
  new placeholder markers; do not hand-edit generated method/field tables.

## 8.5 Keeping the diagrams truthful

When a workflow changes:

1. Update the relevant sequence (`level-4-*.puml`) and, if a data type changed,
   the model (`level-3-data-model.puml`).
2. Update the cross-class edges (`level-5-*.puml`) and the matching table in
   [05-Low-Level-Data-Flow.md](05-Low-Level-Data-Flow.md).
3. Update the class entry in [06-Class-Reference.md](06-Class-Reference.md).
4. Re-run `./gradlew documentation` only if you also want the generated
   per-class pages refreshed; it preserves the authored prose but may add new
   sections for new public methods.

A diagram is documentation of the code, not a specification: if the code and the
diagram disagree, fix the diagram. The source, the tests and
[`../Git-Format.md`](../Git-Format.md) are the specification for behaviour and
on-disk format.
