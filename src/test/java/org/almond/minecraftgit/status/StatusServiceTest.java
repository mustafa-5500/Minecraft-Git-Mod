package org.almond.minecraftgit.status;

import org.almond.minecraftgit.commit.CommitRequest;
import org.almond.minecraftgit.commit.CommitService;
import org.almond.minecraftgit.config.ModConfig;
import org.almond.minecraftgit.debug.DebugLogger;
import org.almond.minecraftgit.model.BlockPos;
import org.almond.minecraftgit.model.BlockSnapshot;
import org.almond.minecraftgit.model.Cuboid;
import org.almond.minecraftgit.model.Selection;
import org.almond.minecraftgit.model.Snapshot;
import org.almond.minecraftgit.repo.RepoManager;
import org.almond.minecraftgit.staging.SnapshotStore;
import org.almond.minecraftgit.staging.StagingService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StatusServiceTest {

    private static final UUID OWNER = UUID.fromString("44444444-4444-4444-4444-444444444444");
    private static final String WORLD = "world__overworld";

    @TempDir
    Path temp;

    private ModConfig config;
    private DebugLogger debug;
    private RepoManager repos;
    private StagingService staging;
    private CommitService commits;
    private StatusService status;
    private Selection selection;

    @BeforeEach
    void setUp() {
        config = ModConfig.load(temp.resolve("data"));
        debug = new DebugLogger(config.logsRoot(), false, false);
        repos = new RepoManager(config, debug);
        staging = new StagingService(config.stagingRoot(), new SnapshotStore(), debug);
        commits = new CommitService(repos, staging, config, debug, "test");
        status = new StatusService(repos, staging, debug);
        repos.init(WORLD, "Tester", null);
        selection = new Selection("castle", OWNER, "Tester", WORLD, new BlockPos(0, 0, 0));
        selection.addCuboid(new Cuboid(0, 0, 0, 4, 4, 4));
    }

    @AfterEach
    void tearDown() {
        repos.close();
        debug.close();
    }

    private Snapshot snapshot(String block) {
        BlockPos origin = new BlockPos(0, 0, 0);
        return new Snapshot("castle", origin, List.of(
                BlockSnapshot.at(new BlockPos(0, 0, 0), origin, block, null)),
                List.of(), Instant.now());
    }

    private void commit(Snapshot snapshot) {
        staging.stage(selection, snapshot);
        commits.commit(new CommitRequest("Tester", OWNER.toString(), WORLD, "SURVIVAL",
                List.of(selection), null, false));
    }

    @Test
    void untrackedSelectionIsReported() {
        StatusService.Report report = status.report(WORLD, OWNER, List.of(selection),
                Map.of("castle", snapshot("minecraft:stone")), Set.of());
        StatusService.SelectionStatus result = report.selections().get(0);
        assertTrue(result.untracked());
        assertFalse(result.inHead());
        assertEquals(1, result.workingDiff().added());
    }

    @Test
    void committedSelectionIsCleanWhenCurrentMatches() {
        commit(snapshot("minecraft:stone"));

        StatusService.Report report = status.report(WORLD, OWNER, List.of(selection),
                Map.of("castle", snapshot("minecraft:stone")), Set.of());
        StatusService.SelectionStatus result = report.selections().get(0);
        assertTrue(result.inHead());
        assertTrue(result.clean());
        assertFalse(result.untracked());
        assertFalse(report.head().isBlank());
    }

    @Test
    void changedWorldBlockIsDirty() {
        commit(snapshot("minecraft:stone"));

        StatusService.Report report = status.report(WORLD, OWNER, List.of(selection),
                Map.of("castle", snapshot("minecraft:gold_block")), Set.of());
        StatusService.SelectionStatus result = report.selections().get(0);
        assertFalse(result.clean());
        assertTrue(result.dirty());
        assertEquals(1, result.workingDiff().changed());
    }

    @Test
    void stagedSnapshotIsComparedWithHead() {
        commit(snapshot("minecraft:stone"));
        staging.stage(selection, snapshot("minecraft:gold_block"));

        StatusService.Report report = status.report(WORLD, OWNER, List.of(selection),
                Map.of("castle", snapshot("minecraft:stone")), Set.of());
        StatusService.SelectionStatus result = report.selections().get(0);
        assertTrue(result.staged());
        assertEquals(1, result.stagedDiff().changed());
        assertEquals(1, result.workingDiff().changed());
    }

    @Test
    void diffReportsCoordinateLevelSamples() {
        commit(snapshot("minecraft:stone"));
        StatusService.DiffReport report = status.diff(WORLD, OWNER, selection,
                snapshot("minecraft:gold_block"), false);
        assertEquals(1, report.workingDiff().changed().size());
        assertEquals("minecraft:gold_block", report.workingDiff().changed().get(0).blockData());
        assertEquals(0, report.workingDiff().changed().get(0).rx());
    }

    @Test
    void tooLargeSelectionIsNotCaptured() {
        StatusService.Report report = status.report(WORLD, OWNER, List.of(selection), Map.of(),
                Set.of("castle"));
        StatusService.SelectionStatus result = report.selections().get(0);
        assertTrue(result.tooLarge());
        assertFalse(result.currentChecked());
        assertTrue(result.note().contains("status capture limit"));
    }
}
