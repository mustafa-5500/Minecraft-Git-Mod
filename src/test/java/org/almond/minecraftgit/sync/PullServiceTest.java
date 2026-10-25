package org.almond.minecraftgit.sync;

import org.almond.minecraftgit.commit.CommitRequest;
import org.almond.minecraftgit.commit.CommitService;
import org.almond.minecraftgit.config.ModConfig;
import org.almond.minecraftgit.debug.DebugLogger;
import org.almond.minecraftgit.model.BlockPos;
import org.almond.minecraftgit.model.BlockSnapshot;
import org.almond.minecraftgit.model.Cuboid;
import org.almond.minecraftgit.model.Selection;
import org.almond.minecraftgit.model.Snapshot;
import org.almond.minecraftgit.repo.PullPlan;
import org.almond.minecraftgit.repo.RepoManager;
import org.almond.minecraftgit.repo.RepoProgress;
import org.almond.minecraftgit.repo.RepoService;
import org.almond.minecraftgit.staging.SnapshotStore;
import org.almond.minecraftgit.staging.StagingService;
import org.eclipse.jgit.api.Git;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PullServiceTest {

    private static final UUID OWNER = UUID.fromString("55555555-5555-5555-5555-555555555555");
    private static final String WORLD = "world__overworld";

    @TempDir
    Path temp;

    private ModConfig config;
    private DebugLogger debug;
    private RepoManager repos;
    private StagingService staging;
    private CommitService commits;
    private PullService pull;
    private Selection selection;

    @BeforeEach
    void setUp() {
        config = ModConfig.load(temp.resolve("data"));
        debug = new DebugLogger(config.logsRoot(), false, false);
        repos = new RepoManager(config, debug);
        staging = new StagingService(config.stagingRoot(), new SnapshotStore(), debug);
        commits = new CommitService(repos, staging, config, debug, "test");
        pull = new PullService(config.tmpRoot());
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

    private void commitAndPush(Path barePath) {
        repos.init(WORLD, "Tester", null);
        staging.stage(selection, snapshot("minecraft:stone"));
        commits.commit(new CommitRequest("Tester", OWNER.toString(), WORLD, "SURVIVAL",
                List.of(selection), null, false));
        RepoService origin = repos.service(WORLD);
        origin.remoteAdd("origin", barePath.toString());
        origin.push("origin", false);
    }

    @Test
    void preparesRemoteSelectionForApplyWithoutLocalSelection() throws Exception {
        Path bare = temp.resolve("remote.git");
        try (Git ignored = Git.init().setBare(true).setDirectory(bare.toFile()).call()) {
            // Empty remote.
        }
        commitAndPush(bare);

        String second = "second__overworld";
        RepoService clone = repos.service(second);
        clone.init();
        clone.remoteAdd("origin", bare.toString());
        PullPlan plan = clone.planPull("origin", RepoProgress.NOOP);
        assertEquals(PullPlan.Type.FAST_FORWARD, plan.type());

        BlockPos playerOrigin = new BlockPos(100, 64, 100);
        List<PullService.PreparedPull> prepared = pull.prepare(second, List.of(), playerOrigin,
                clone, plan, null);

        assertEquals(1, prepared.size());
        PullService.PreparedPull result = prepared.get(0);
        assertEquals("castle", result.name());
        assertNull(result.localSelection());
        assertNull(result.localHead());
        assertEquals(playerOrigin, result.localOrigin());
        assertEquals("minecraft:stone", result.target().blocks().get(0).blockData());
        assertTrue(result.target().blockCount() == 1);
        pull.cleanup(prepared);
    }

    @Test
    void prepareCheckoutReadsLocalHeadAndTarget() throws Exception {
        Path bare = temp.resolve("remote.git");
        try (Git ignored = Git.init().setBare(true).setDirectory(bare.toFile()).call()) {
            // Empty remote.
        }
        commitAndPush(bare);
        RepoService repo = repos.service(WORLD);
        var head = repo.headCommit().orElseThrow();
        var commit = repo.resolve(head.id()).orElseThrow();

        PullService.PreparedCheckout checkout = pull.prepareCheckout(selection, repo, commit);

        assertEquals("castle", checkout.target().selection());
        assertEquals("minecraft:stone", checkout.target().blocks().get(0).blockData());
        assertEquals(1, checkout.localHead().blockCount());
        pull.cleanup(checkout.tempDirectory());
    }
}
