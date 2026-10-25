package org.almond.minecraftgit.commit;

import org.almond.minecraftgit.config.ModConfig;
import org.almond.minecraftgit.debug.DebugLogger;
import org.almond.minecraftgit.model.BlockPos;
import org.almond.minecraftgit.model.BlockSnapshot;
import org.almond.minecraftgit.model.Cuboid;
import org.almond.minecraftgit.model.Selection;
import org.almond.minecraftgit.model.Snapshot;
import org.almond.minecraftgit.repo.CommitInfo;
import org.almond.minecraftgit.repo.CommitMeta;
import org.almond.minecraftgit.repo.RepoManager;
import org.almond.minecraftgit.staging.SnapshotStore;
import org.almond.minecraftgit.staging.StagingService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommitServiceTest {

    private static final UUID OWNER = UUID.fromString("33333333-3333-3333-3333-333333333333");
    private static final String WORLD = "world__overworld";

    @TempDir
    Path temp;

    private ModConfig config;
    private DebugLogger debug;
    private RepoManager repos;
    private StagingService staging;
    private CommitService commits;
    private Selection selection;

    @BeforeEach
    void setUp() {
        config = ModConfig.load(temp.resolve("data"));
        debug = new DebugLogger(config.logsRoot(), false, false);
        repos = new RepoManager(config, debug);
        staging = new StagingService(config.stagingRoot(), new SnapshotStore(), debug);
        commits = new CommitService(repos, staging, config, debug, "test");
        repos.init(WORLD, "Tester", null);
        selection = new Selection("castle", OWNER, "Tester", WORLD, new BlockPos(0, 0, 0));
        selection.addCuboid(new Cuboid(0, 0, 0, 4, 4, 4));
    }

    @AfterEach
    void tearDown() {
        repos.close();
        debug.close();
    }

    private Snapshot snapshot(String blockAtOrigin, String blockAtOne) {
        BlockPos origin = new BlockPos(0, 0, 0);
        return new Snapshot(selection.name(), origin, List.of(
                BlockSnapshot.at(new BlockPos(0, 0, 0), origin, blockAtOrigin, null),
                BlockSnapshot.at(new BlockPos(1, 0, 0), origin, blockAtOne, null)),
                List.of(), Instant.now());
    }

    private CommitRequest request() {
        return new CommitRequest("Tester", OWNER.toString(), WORLD, "SURVIVAL",
                List.of(selection), null, false);
    }

    @Test
    void commitsStagedSelectionAndWritesFormatterTree() throws Exception {
        staging.stage(selection, snapshot("minecraft:stone", "minecraft:dirt"));

        CommitResult result = commits.commit(request());

        assertTrue(result.commitId().length() >= 8);
        assertEquals(List.of("castle"), result.selections());
        assertEquals(2, result.added());
        assertEquals(0, result.changed());
        assertTrue(Files.isRegularFile(repos.repoPath(WORLD).resolve("castle/meta.json")));
        assertTrue(Files.isRegularFile(repos.repoPath(WORLD).resolve("castle/y_0/pp.csv")));
        assertFalse(staging.hasStaged(WORLD, OWNER, "castle"));

        List<CommitInfo> log = repos.service(WORLD).log(10);
        assertEquals(2, log.size());
        assertTrue(log.get(0).message().startsWith("Tester: castle"));

        CommitMeta meta = CommitMeta.read(repos.repoPath(WORLD).resolve("castle/meta.json")).orElseThrow();
        assertEquals(new BlockPos(0, 0, 0), meta.origin());
        assertEquals("castle", meta.selection());
        assertEquals(WORLD, meta.world());
        assertEquals(1, meta.cuboidCount());
    }

    @Test
    void secondCommitReportsChanges() {
        staging.stage(selection, snapshot("minecraft:stone", "minecraft:dirt"));
        commits.commit(request());

        staging.stage(selection, snapshot("minecraft:gold_block", "minecraft:dirt"));
        CommitResult result = commits.commit(request());

        assertEquals(0, result.added());
        assertEquals(1, result.changed());
        assertEquals(0, result.removed());
    }

    @Test
    void unchangedStagingIsRejectedWithoutDeletingIt() {
        staging.stage(selection, snapshot("minecraft:stone", "minecraft:dirt"));
        commits.commit(request());
        staging.stage(selection, snapshot("minecraft:stone", "minecraft:dirt"));

        CommitException error = assertThrows(CommitException.class, () -> commits.commit(request()));
        assertTrue(error.getMessage().contains("unchanged"));
        assertTrue(staging.hasStaged(WORLD, OWNER, "castle"));
    }

    @Test
    void stagedButMissingSnapshotIsReported() {
        selection.setTracked(true);
        CommitException error = assertThrows(CommitException.class, () -> commits.commit(request()));
        assertTrue(error.getMessage().contains("not staged"));
    }

    @Test
    void emptyCommitIsAllowedExplicitly() {
        CommitRequest empty = new CommitRequest("Tester", OWNER.toString(), WORLD, "SURVIVAL",
                List.of(), "checkpoint", true);
        CommitResult result = commits.commit(empty);
        assertTrue(result.selections().isEmpty());
        assertTrue(repos.service(WORLD).log(10).get(0).message().startsWith("checkpoint"));
    }
}
