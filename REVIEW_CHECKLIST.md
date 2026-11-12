# Reviewer's Checklist

This checklist lists every file changed by PR #2, grouped for review. Tick an item when you have reviewed that change.
The checklist file itself is intentionally not listed.

## Implementation guide

- [ ] `Documentation/Implementation/01-Overview.md` — Added the high-level system overview, feature map, runtime topology, constraints, technology and data locations.
- [ ] `Documentation/Implementation/02-Architecture.md` — Added the layer/package architecture, service graph, threading model, lifecycle and extension points.
- [ ] `Documentation/Implementation/03-Data-Model-and-Format.md` — Added coordinate/data-model semantics, repository and local storage formats, config schema and invariants.
- [ ] `Documentation/Implementation/04-Workflows.md` — Added end-to-end workflows for init, selections, staging, commit, status/diff, pull/apply/abort, checkout and remote operations.
- [ ] `Documentation/Implementation/05-Low-Level-Data-Flow.md` — Added cross-class function-level data-flow tables for every major workflow.
- [ ] `Documentation/Implementation/06-Class-Reference.md` — Added a one-entry-per-class reference for every production class.
- [ ] `Documentation/Implementation/07-Error-Handling-and-Concurrency.md` — Added the exception hierarchy, command error path, thread ownership, locking, limits and sharp edges.
- [ ] `Documentation/Implementation/08-Diagram-Guide.md` — Added the diagram-level guide, catalogue, rendering instructions and regeneration notes.
- [ ] `Documentation/Implementation/README.md` — Added the implementation documentation index, reading order, level map, source map and conventions.

## PlantUML diagrams

- [ ] `Documentation/Implementation/diagrams/level-1-system-context.puml` — Added an L1 system-context diagram (player, client, integrated server, mod, local data, Git remote).
- [ ] `Documentation/Implementation/diagrams/level-2-component-architecture.puml` — Added an L2 component-architecture diagram of services, data and collaborators.
- [ ] `Documentation/Implementation/diagrams/level-2-package-dependencies.puml` — Added an L2 package/source-set dependency-rule diagram.
- [ ] `Documentation/Implementation/diagrams/level-3-data-flow-overview.puml` — Added an L3 end-to-end build-data-flow overview diagram.
- [ ] `Documentation/Implementation/diagrams/level-3-data-model.puml` — Added an L3 model class diagram for selections, snapshots, diffs and handoff records.
- [ ] `Documentation/Implementation/diagrams/level-4-sequence-checkout.puml` — Added an L4 sequence diagram for `/git checkout <commit>`.
- [ ] `Documentation/Implementation/diagrams/level-4-sequence-init-remote.puml` — Added an L4 sequence diagram for `/git init`, repository info and remote/fetch/push commands.
- [ ] `Documentation/Implementation/diagrams/level-4-sequence-pull-apply-abort.puml` — Added an L4 sequence diagram for `/git pull`, the apply job and `/git pull --abort`.
- [ ] `Documentation/Implementation/diagrams/level-4-sequence-selection-wand.puml` — Added an L4 sequence diagram for wand clicks, corners and selection cuboids.
- [ ] `Documentation/Implementation/diagrams/level-4-sequence-staging-commit.puml` — Added an L4 sequence diagram for `/git add` capture/staging and `/git commit`.
- [ ] `Documentation/Implementation/diagrams/level-4-sequence-status-diff.puml` — Added an L4 sequence diagram for `/git status` and `/git diff`.
- [ ] `Documentation/Implementation/diagrams/level-5-function-data-flow-pull-abort-checkout.puml` — Added an L5 cross-class function data-flow diagram for abort and checkout.
- [ ] `Documentation/Implementation/diagrams/level-5-function-data-flow-pull-apply.puml` — Added an L5 cross-class function data-flow diagram for the apply job and fast-forward ordering.
- [ ] `Documentation/Implementation/diagrams/level-5-function-data-flow-pull-plan.puml` — Added an L5 cross-class function data-flow diagram for pull planning and remote-tree preparation.
- [ ] `Documentation/Implementation/diagrams/level-5-function-data-flow-selection.puml` — Added an L5 cross-class function data-flow diagram for wand selection and persistence.
- [ ] `Documentation/Implementation/diagrams/level-5-function-data-flow-staging-commit.puml` — Added an L5 cross-class function data-flow diagram for capture, staging and commit.
- [ ] `Documentation/Implementation/diagrams/level-5-function-data-flow-status.puml` — Added an L5 cross-class function data-flow diagram for the three-way status/diff comparison.

## New per-class API pages

- [ ] `Documentation/client/java/org/almond/minecraftgit/client/WorldContext.md` — Added API documentation for `WorldContext`: world-id construction, integrated-server bridge, game mode and screen helpers.
- [ ] `Documentation/main/java/org/almond/minecraftgit/commit/CommitRequest.md` — Added API documentation for `CommitRequest`.
- [ ] `Documentation/main/java/org/almond/minecraftgit/commit/CommitResult.md` — Added API documentation for `CommitResult`.
- [ ] `Documentation/main/java/org/almond/minecraftgit/model/BlockPos.md` — Added API documentation for `BlockPos`: absolute/relative coordinate helpers and ordering.
- [ ] `Documentation/main/java/org/almond/minecraftgit/model/BlockSnapshot.md` — Added API documentation for `BlockSnapshot`.
- [ ] `Documentation/main/java/org/almond/minecraftgit/model/Cuboid.md` — Added API documentation for `Cuboid`: inclusive regions, volume, ordering, containment and geometry helpers.
- [ ] `Documentation/main/java/org/almond/minecraftgit/model/EntitySnapshot.md` — Added API documentation for `EntitySnapshot`.
- [ ] `Documentation/main/java/org/almond/minecraftgit/repo/CommitInfo.md` — Added API documentation for `CommitInfo`.
- [ ] `Documentation/main/java/org/almond/minecraftgit/repo/CommitMeta.md` — Added API documentation for committed `meta.json`: fields, write/read contract and origin/reorigin semantics.
- [ ] `Documentation/main/java/org/almond/minecraftgit/repo/JGitProgressMonitor.md` — Added API documentation for the package-private JGit progress adapter.
- [ ] `Documentation/main/java/org/almond/minecraftgit/repo/PullPlan.md` — Added API documentation for `PullPlan`.
- [ ] `Documentation/main/java/org/almond/minecraftgit/repo/RemoteInfo.md` — Added API documentation for `RemoteInfo`.
- [ ] `Documentation/main/java/org/almond/minecraftgit/repo/RepoInfo.md` — Added API documentation for `RepoInfo`.
- [ ] `Documentation/main/java/org/almond/minecraftgit/repo/RepoOptions.md` — Added API documentation for `RepoOptions`.
- [ ] `Documentation/main/java/org/almond/minecraftgit/staging/StagedSelection.md` — Added API documentation for `StagedSelection`.
- [ ] `Documentation/main/java/org/almond/minecraftgit/world/ApplyPlan.md` — Added API documentation for `ApplyPlan`.
- [ ] `Documentation/main/java/org/almond/minecraftgit/world/ApplyResult.md` — Added API documentation for `ApplyResult`.
- [ ] `Documentation/main/java/org/almond/minecraftgit/world/CaptureResult.md` — Added API documentation for `CaptureResult`.

## Modified generated API/SDD pages

- [ ] `Documentation/client/java/org/almond/minecraftgit/client/GitServices.md` — Filled the generated `GitServices` API page: replaced TODO placeholders with source-derived field/method/usage documentation.
- [ ] `Documentation/client/java/org/almond/minecraftgit/client/GitServices_SDD.md` — Filled the generated `GitServices` SDD page: replaced TODO placeholders with source-derived design and data-flow text.
- [ ] `Documentation/client/java/org/almond/minecraftgit/client/Messages.md` — Filled the generated `Messages` API page: replaced TODO placeholders with source-derived field/method/usage documentation.
- [ ] `Documentation/client/java/org/almond/minecraftgit/client/Messages_SDD.md` — Filled the generated `Messages` SDD page: replaced TODO placeholders with source-derived design and data-flow text.
- [ ] `Documentation/client/java/org/almond/minecraftgit/client/MinecraftGitClient.md` — Filled the generated `MinecraftGitClient` API page: replaced TODO placeholders with source-derived field/method/usage documentation.
- [ ] `Documentation/client/java/org/almond/minecraftgit/client/MinecraftGitClient_SDD.md` — Filled the generated `MinecraftGitClient` SDD page: replaced TODO placeholders with source-derived design and data-flow text.
- [ ] `Documentation/client/java/org/almond/minecraftgit/client/command/CommandSupport.md` — Filled the generated `CommandSupport` API page: replaced TODO placeholders with source-derived field/method/usage documentation.
- [ ] `Documentation/client/java/org/almond/minecraftgit/client/command/CommandSupport_SDD.md` — Filled the generated `CommandSupport` SDD page: replaced TODO placeholders with source-derived design and data-flow text.
- [ ] `Documentation/client/java/org/almond/minecraftgit/client/command/CommitCommands.md` — Filled the generated `CommitCommands` API page: replaced TODO placeholders with source-derived field/method/usage documentation.
- [ ] `Documentation/client/java/org/almond/minecraftgit/client/command/CommitCommands_SDD.md` — Filled the generated `CommitCommands` SDD page: replaced TODO placeholders with source-derived design and data-flow text.
- [ ] `Documentation/client/java/org/almond/minecraftgit/client/command/GitCommand.md` — Filled the generated `GitCommand` API page: replaced TODO placeholders with source-derived field/method/usage documentation.
- [ ] `Documentation/client/java/org/almond/minecraftgit/client/command/GitCommand_SDD.md` — Filled the generated `GitCommand` SDD page: replaced TODO placeholders with source-derived design and data-flow text.
- [ ] `Documentation/client/java/org/almond/minecraftgit/client/command/PullCommands.md` — Filled the generated `PullCommands` API page: replaced TODO placeholders with source-derived field/method/usage documentation.
- [ ] `Documentation/client/java/org/almond/minecraftgit/client/command/PullCommands_SDD.md` — Filled the generated `PullCommands` SDD page: replaced TODO placeholders with source-derived design and data-flow text.
- [ ] `Documentation/client/java/org/almond/minecraftgit/client/command/RepoCommands.md` — Filled the generated `RepoCommands` API page: replaced TODO placeholders with source-derived field/method/usage documentation.
- [ ] `Documentation/client/java/org/almond/minecraftgit/client/command/RepoCommands_SDD.md` — Filled the generated `RepoCommands` SDD page: replaced TODO placeholders with source-derived design and data-flow text.
- [ ] `Documentation/client/java/org/almond/minecraftgit/client/command/SelectionCommands.md` — Filled the generated `SelectionCommands` API page: replaced TODO placeholders with source-derived field/method/usage documentation.
- [ ] `Documentation/client/java/org/almond/minecraftgit/client/command/SelectionCommands_SDD.md` — Filled the generated `SelectionCommands` SDD page: replaced TODO placeholders with source-derived design and data-flow text.
- [ ] `Documentation/client/java/org/almond/minecraftgit/client/command/StagingCommands.md` — Filled the generated `StagingCommands` API page: replaced TODO placeholders with source-derived field/method/usage documentation.
- [ ] `Documentation/client/java/org/almond/minecraftgit/client/command/StagingCommands_SDD.md` — Filled the generated `StagingCommands` SDD page: replaced TODO placeholders with source-derived design and data-flow text.
- [ ] `Documentation/client/java/org/almond/minecraftgit/client/command/StatusCommands.md` — Filled the generated `StatusCommands` API page: replaced TODO placeholders with source-derived field/method/usage documentation.
- [ ] `Documentation/client/java/org/almond/minecraftgit/client/command/StatusCommands_SDD.md` — Filled the generated `StatusCommands` SDD page: replaced TODO placeholders with source-derived design and data-flow text.
- [ ] `Documentation/client/java/org/almond/minecraftgit/client/selection/SelectionManager.md` — Filled the generated `SelectionManager` API page: replaced TODO placeholders with source-derived field/method/usage documentation.
- [ ] `Documentation/client/java/org/almond/minecraftgit/client/selection/SelectionManager_SDD.md` — Filled the generated `SelectionManager` SDD page: replaced TODO placeholders with source-derived design and data-flow text.
- [ ] `Documentation/client/java/org/almond/minecraftgit/client/selection/SelectionRenderer.md` — Filled the generated `SelectionRenderer` API page: replaced TODO placeholders with source-derived field/method/usage documentation.
- [ ] `Documentation/client/java/org/almond/minecraftgit/client/selection/SelectionRenderer_SDD.md` — Filled the generated `SelectionRenderer` SDD page: replaced TODO placeholders with source-derived design and data-flow text.
- [ ] `Documentation/client/java/org/almond/minecraftgit/client/selection/WandEvents.md` — Filled the generated `WandEvents` API page: replaced TODO placeholders with source-derived field/method/usage documentation.
- [ ] `Documentation/client/java/org/almond/minecraftgit/client/selection/WandEvents_SDD.md` — Filled the generated `WandEvents` SDD page: replaced TODO placeholders with source-derived design and data-flow text.
- [ ] `Documentation/client/java/org/almond/minecraftgit/client/selection/WandItem.md` — Filled the generated `WandItem` API page: replaced TODO placeholders with source-derived field/method/usage documentation.
- [ ] `Documentation/client/java/org/almond/minecraftgit/client/selection/WandItem_SDD.md` — Filled the generated `WandItem` SDD page: replaced TODO placeholders with source-derived design and data-flow text.
- [ ] `Documentation/client/java/org/almond/minecraftgit/client/world/ApplyService.md` — Filled the generated `ApplyService` API page: replaced TODO placeholders with source-derived field/method/usage documentation.
- [ ] `Documentation/client/java/org/almond/minecraftgit/client/world/ApplyService_SDD.md` — Filled the generated `ApplyService` SDD page: replaced TODO placeholders with source-derived design and data-flow text.
- [ ] `Documentation/client/java/org/almond/minecraftgit/client/world/WorldAccess.md` — Filled the generated `WorldAccess` API page: replaced TODO placeholders with source-derived field/method/usage documentation.
- [ ] `Documentation/client/java/org/almond/minecraftgit/client/world/WorldAccess_SDD.md` — Filled the generated `WorldAccess` SDD page: replaced TODO placeholders with source-derived design and data-flow text.
- [ ] `Documentation/main/java/org/almond/minecraftgit/MinecraftGit.md` — Filled the generated `MinecraftGit` API page: replaced TODO placeholders with source-derived field/method/usage documentation.
- [ ] `Documentation/main/java/org/almond/minecraftgit/MinecraftGit_SDD.md` — Filled the generated `MinecraftGit` SDD page: replaced TODO placeholders with source-derived design and data-flow text.
- [ ] `Documentation/main/java/org/almond/minecraftgit/backup/BackupException.md` — Filled the generated `BackupException` API page: replaced TODO placeholders with source-derived field/method/usage documentation.
- [ ] `Documentation/main/java/org/almond/minecraftgit/backup/BackupException_SDD.md` — Filled the generated `BackupException` SDD page: replaced TODO placeholders with source-derived design and data-flow text.
- [ ] `Documentation/main/java/org/almond/minecraftgit/backup/BackupService.md` — Filled the generated `BackupService` API page: replaced TODO placeholders with source-derived field/method/usage documentation.
- [ ] `Documentation/main/java/org/almond/minecraftgit/backup/BackupService_SDD.md` — Filled the generated `BackupService` SDD page: replaced TODO placeholders with source-derived design and data-flow text.
- [ ] `Documentation/main/java/org/almond/minecraftgit/commit/CommitException.md` — Filled the generated `CommitException` API page: replaced TODO placeholders with source-derived field/method/usage documentation.
- [ ] `Documentation/main/java/org/almond/minecraftgit/commit/CommitException_SDD.md` — Filled the generated `CommitException` SDD page: replaced TODO placeholders with source-derived design and data-flow text.
- [ ] `Documentation/main/java/org/almond/minecraftgit/commit/CommitService.md` — Filled the generated `CommitService` API page: replaced TODO placeholders with source-derived field/method/usage documentation.
- [ ] `Documentation/main/java/org/almond/minecraftgit/commit/CommitService_SDD.md` — Filled the generated `CommitService` SDD page: replaced TODO placeholders with source-derived design and data-flow text.
- [ ] `Documentation/main/java/org/almond/minecraftgit/config/ModConfig.md` — Filled the generated `ModConfig` API page: replaced TODO placeholders with source-derived field/method/usage documentation.
- [ ] `Documentation/main/java/org/almond/minecraftgit/config/ModConfig_SDD.md` — Filled the generated `ModConfig` SDD page: replaced TODO placeholders with source-derived design and data-flow text.
- [ ] `Documentation/main/java/org/almond/minecraftgit/debug/DebugLogger.md` — Filled the generated `DebugLogger` API page: replaced TODO placeholders with source-derived field/method/usage documentation.
- [ ] `Documentation/main/java/org/almond/minecraftgit/debug/DebugLogger_SDD.md` — Filled the generated `DebugLogger` SDD page: replaced TODO placeholders with source-derived design and data-flow text.
- [ ] `Documentation/main/java/org/almond/minecraftgit/model/BuildDiff.md` — Filled the generated `BuildDiff` API page: replaced TODO placeholders with source-derived field/method/usage documentation.
- [ ] `Documentation/main/java/org/almond/minecraftgit/model/BuildDiff_SDD.md` — Filled the generated `BuildDiff` SDD page: replaced TODO placeholders with source-derived design and data-flow text.
- [ ] `Documentation/main/java/org/almond/minecraftgit/model/Selection.md` — Filled the generated `Selection` API page: replaced TODO placeholders with source-derived field/method/usage documentation.
- [ ] `Documentation/main/java/org/almond/minecraftgit/model/Selection_SDD.md` — Filled the generated `Selection` SDD page: replaced TODO placeholders with source-derived design and data-flow text.
- [ ] `Documentation/main/java/org/almond/minecraftgit/model/Snapshot.md` — Filled the generated `Snapshot` API page: replaced TODO placeholders with source-derived field/method/usage documentation.
- [ ] `Documentation/main/java/org/almond/minecraftgit/model/Snapshot_SDD.md` — Filled the generated `Snapshot` SDD page: replaced TODO placeholders with source-derived design and data-flow text.
- [ ] `Documentation/main/java/org/almond/minecraftgit/repo/RepoException.md` — Filled the generated `RepoException` API page: replaced TODO placeholders with source-derived field/method/usage documentation.
- [ ] `Documentation/main/java/org/almond/minecraftgit/repo/RepoException_SDD.md` — Filled the generated `RepoException` SDD page: replaced TODO placeholders with source-derived design and data-flow text.
- [ ] `Documentation/main/java/org/almond/minecraftgit/repo/RepoLocator.md` — Filled the generated `RepoLocator` API page: replaced TODO placeholders with source-derived field/method/usage documentation.
- [ ] `Documentation/main/java/org/almond/minecraftgit/repo/RepoLocator_SDD.md` — Filled the generated `RepoLocator` SDD page: replaced TODO placeholders with source-derived design and data-flow text.
- [ ] `Documentation/main/java/org/almond/minecraftgit/repo/RepoManager.md` — Filled the generated `RepoManager` API page: replaced TODO placeholders with source-derived field/method/usage documentation.
- [ ] `Documentation/main/java/org/almond/minecraftgit/repo/RepoManager_SDD.md` — Filled the generated `RepoManager` SDD page: replaced TODO placeholders with source-derived design and data-flow text.
- [ ] `Documentation/main/java/org/almond/minecraftgit/repo/RepoMetadata.md` — Filled the generated `RepoMetadata` API page: replaced TODO placeholders with source-derived field/method/usage documentation.
- [ ] `Documentation/main/java/org/almond/minecraftgit/repo/RepoMetadataStore.md` — Filled the generated `RepoMetadataStore` API page: replaced TODO placeholders with source-derived field/method/usage documentation.
- [ ] `Documentation/main/java/org/almond/minecraftgit/repo/RepoMetadataStore_SDD.md` — Filled the generated `RepoMetadataStore` SDD page: replaced TODO placeholders with source-derived design and data-flow text.
- [ ] `Documentation/main/java/org/almond/minecraftgit/repo/RepoMetadata_SDD.md` — Filled the generated `RepoMetadata` SDD page: replaced TODO placeholders with source-derived design and data-flow text.
- [ ] `Documentation/main/java/org/almond/minecraftgit/repo/RepoProgress.md` — Filled the generated `RepoProgress` API page: replaced TODO placeholders with source-derived field/method/usage documentation.
- [ ] `Documentation/main/java/org/almond/minecraftgit/repo/RepoProgress_SDD.md` — Filled the generated `RepoProgress` SDD page: replaced TODO placeholders with source-derived design and data-flow text.
- [ ] `Documentation/main/java/org/almond/minecraftgit/repo/RepoService.md` — Filled the generated `RepoService` API page: replaced TODO placeholders with source-derived field/method/usage documentation.
- [ ] `Documentation/main/java/org/almond/minecraftgit/repo/RepoService_SDD.md` — Filled the generated `RepoService` SDD page: replaced TODO placeholders with source-derived design and data-flow text.
- [ ] `Documentation/main/java/org/almond/minecraftgit/repo/Type.md` — Filled the generated `Type` API page: replaced TODO placeholders with source-derived field/method/usage documentation.
- [ ] `Documentation/main/java/org/almond/minecraftgit/repo/Type_SDD.md` — Filled the generated `Type` SDD page: replaced TODO placeholders with source-derived design and data-flow text.
- [ ] `Documentation/main/java/org/almond/minecraftgit/selection/SelectionException.md` — Filled the generated `SelectionException` API page: replaced TODO placeholders with source-derived field/method/usage documentation.
- [ ] `Documentation/main/java/org/almond/minecraftgit/selection/SelectionException_SDD.md` — Filled the generated `SelectionException` SDD page: replaced TODO placeholders with source-derived design and data-flow text.
- [ ] `Documentation/main/java/org/almond/minecraftgit/selection/SelectionMetadataStore.md` — Filled the generated `SelectionMetadataStore` API page: replaced TODO placeholders with source-derived field/method/usage documentation.
- [ ] `Documentation/main/java/org/almond/minecraftgit/selection/SelectionMetadataStore_SDD.md` — Filled the generated `SelectionMetadataStore` SDD page: replaced TODO placeholders with source-derived design and data-flow text.
- [ ] `Documentation/main/java/org/almond/minecraftgit/selection/SelectionService.md` — Filled the generated `SelectionService` API page: replaced TODO placeholders with source-derived field/method/usage documentation.
- [ ] `Documentation/main/java/org/almond/minecraftgit/selection/SelectionService_SDD.md` — Filled the generated `SelectionService` SDD page: replaced TODO placeholders with source-derived design and data-flow text.
- [ ] `Documentation/main/java/org/almond/minecraftgit/serialization/BlockEntityCodec.md` — Filled the generated `BlockEntityCodec` API page: replaced TODO placeholders with source-derived field/method/usage documentation.
- [ ] `Documentation/main/java/org/almond/minecraftgit/serialization/BlockEntityCodec_SDD.md` — Filled the generated `BlockEntityCodec` SDD page: replaced TODO placeholders with source-derived design and data-flow text.
- [ ] `Documentation/main/java/org/almond/minecraftgit/serialization/BlockStateCodec.md` — Filled the generated `BlockStateCodec` API page: replaced TODO placeholders with source-derived field/method/usage documentation.
- [ ] `Documentation/main/java/org/almond/minecraftgit/serialization/BlockStateCodec_SDD.md` — Filled the generated `BlockStateCodec` SDD page: replaced TODO placeholders with source-derived design and data-flow text.
- [ ] `Documentation/main/java/org/almond/minecraftgit/serialization/CsvLine.md` — Filled the generated `CsvLine` API page: replaced TODO placeholders with source-derived field/method/usage documentation.
- [ ] `Documentation/main/java/org/almond/minecraftgit/serialization/CsvLine_SDD.md` — Filled the generated `CsvLine` SDD page: replaced TODO placeholders with source-derived design and data-flow text.
- [ ] `Documentation/main/java/org/almond/minecraftgit/serialization/EntityCodec.md` — Filled the generated `EntityCodec` API page: replaced TODO placeholders with source-derived field/method/usage documentation.
- [ ] `Documentation/main/java/org/almond/minecraftgit/serialization/EntityCodec_SDD.md` — Filled the generated `EntityCodec` SDD page: replaced TODO placeholders with source-derived design and data-flow text.
- [ ] `Documentation/main/java/org/almond/minecraftgit/serialization/Quadrant.md` — Filled the generated `Quadrant` API page: replaced TODO placeholders with source-derived field/method/usage documentation.
- [ ] `Documentation/main/java/org/almond/minecraftgit/serialization/QuadrantCsvReader.md` — Filled the generated `QuadrantCsvReader` API page: replaced TODO placeholders with source-derived field/method/usage documentation.
- [ ] `Documentation/main/java/org/almond/minecraftgit/serialization/QuadrantCsvReader_SDD.md` — Filled the generated `QuadrantCsvReader` SDD page: replaced TODO placeholders with source-derived design and data-flow text.
- [ ] `Documentation/main/java/org/almond/minecraftgit/serialization/QuadrantCsvWriter.md` — Filled the generated `QuadrantCsvWriter` API page: replaced TODO placeholders with source-derived field/method/usage documentation.
- [ ] `Documentation/main/java/org/almond/minecraftgit/serialization/QuadrantCsvWriter_SDD.md` — Filled the generated `QuadrantCsvWriter` SDD page: replaced TODO placeholders with source-derived design and data-flow text.
- [ ] `Documentation/main/java/org/almond/minecraftgit/serialization/Quadrant_SDD.md` — Filled the generated `Quadrant` SDD page: replaced TODO placeholders with source-derived design and data-flow text.
- [ ] `Documentation/main/java/org/almond/minecraftgit/serialization/SerializationException.md` — Filled the generated `SerializationException` API page: replaced TODO placeholders with source-derived field/method/usage documentation.
- [ ] `Documentation/main/java/org/almond/minecraftgit/serialization/SerializationException_SDD.md` — Filled the generated `SerializationException` SDD page: replaced TODO placeholders with source-derived design and data-flow text.
- [ ] `Documentation/main/java/org/almond/minecraftgit/serialization/SnapshotCell.md` — Filled the generated `SnapshotCell` API page: replaced TODO placeholders with source-derived field/method/usage documentation.
- [ ] `Documentation/main/java/org/almond/minecraftgit/serialization/SnapshotCell_SDD.md` — Filled the generated `SnapshotCell` SDD page: replaced TODO placeholders with source-derived design and data-flow text.
- [ ] `Documentation/main/java/org/almond/minecraftgit/staging/SnapshotStore.md` — Filled the generated `SnapshotStore` API page: replaced TODO placeholders with source-derived field/method/usage documentation.
- [ ] `Documentation/main/java/org/almond/minecraftgit/staging/SnapshotStore_SDD.md` — Filled the generated `SnapshotStore` SDD page: replaced TODO placeholders with source-derived design and data-flow text.
- [ ] `Documentation/main/java/org/almond/minecraftgit/staging/StagingException.md` — Filled the generated `StagingException` API page: replaced TODO placeholders with source-derived field/method/usage documentation.
- [ ] `Documentation/main/java/org/almond/minecraftgit/staging/StagingException_SDD.md` — Filled the generated `StagingException` SDD page: replaced TODO placeholders with source-derived design and data-flow text.
- [ ] `Documentation/main/java/org/almond/minecraftgit/staging/StagingService.md` — Filled the generated `StagingService` API page: replaced TODO placeholders with source-derived field/method/usage documentation.
- [ ] `Documentation/main/java/org/almond/minecraftgit/staging/StagingService_SDD.md` — Filled the generated `StagingService` SDD page: replaced TODO placeholders with source-derived design and data-flow text.
- [ ] `Documentation/main/java/org/almond/minecraftgit/status/StatusService.md` — Filled the generated `StatusService` API page: replaced TODO placeholders with source-derived field/method/usage documentation.
- [ ] `Documentation/main/java/org/almond/minecraftgit/status/StatusService_SDD.md` — Filled the generated `StatusService` SDD page: replaced TODO placeholders with source-derived design and data-flow text.
- [ ] `Documentation/main/java/org/almond/minecraftgit/sync/PullException.md` — Filled the generated `PullException` API page: replaced TODO placeholders with source-derived field/method/usage documentation.
- [ ] `Documentation/main/java/org/almond/minecraftgit/sync/PullException_SDD.md` — Filled the generated `PullException` SDD page: replaced TODO placeholders with source-derived design and data-flow text.
- [ ] `Documentation/main/java/org/almond/minecraftgit/sync/PullService.md` — Filled the generated `PullService` API page: replaced TODO placeholders with source-derived field/method/usage documentation.
- [ ] `Documentation/main/java/org/almond/minecraftgit/sync/PullService_SDD.md` — Filled the generated `PullService` SDD page: replaced TODO placeholders with source-derived design and data-flow text.
- [ ] `Documentation/main/java/org/almond/minecraftgit/util/Names.md` — Filled the generated `Names` API page: replaced TODO placeholders with source-derived field/method/usage documentation.
- [ ] `Documentation/main/java/org/almond/minecraftgit/util/Names_SDD.md` — Filled the generated `Names` SDD page: replaced TODO placeholders with source-derived design and data-flow text.
- [ ] `Documentation/main/java/org/almond/minecraftgit/util/UserFacingException.md` — Filled the generated `UserFacingException` API page: replaced TODO placeholders with source-derived field/method/usage documentation.
- [ ] `Documentation/main/java/org/almond/minecraftgit/util/UserFacingException_SDD.md` — Filled the generated `UserFacingException` SDD page: replaced TODO placeholders with source-derived design and data-flow text.
- [ ] `Documentation/main/java/org/almond/minecraftgit/world/ApplyException.md` — Filled the generated `ApplyException` API page: replaced TODO placeholders with source-derived field/method/usage documentation.
- [ ] `Documentation/main/java/org/almond/minecraftgit/world/ApplyException_SDD.md` — Filled the generated `ApplyException` SDD page: replaced TODO placeholders with source-derived design and data-flow text.
- [ ] `Documentation/main/java/org/almond/minecraftgit/world/SnapshotService.md` — Filled the generated `SnapshotService` API page: replaced TODO placeholders with source-derived field/method/usage documentation.
- [ ] `Documentation/main/java/org/almond/minecraftgit/world/SnapshotService_SDD.md` — Filled the generated `SnapshotService` SDD page: replaced TODO placeholders with source-derived design and data-flow text.

## Repository README

- [ ] `README.md` — Linked the new implementation documentation from the Development section.
