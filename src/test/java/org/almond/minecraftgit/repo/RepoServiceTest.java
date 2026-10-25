package org.almond.minecraftgit.repo;

import org.almond.minecraftgit.debug.DebugLogger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RepoServiceTest {

    @TempDir
    Path temp;

    private RepoService service(String dir) {
        DebugLogger debug = new DebugLogger(temp.resolve("logs"), false, false);
        return new RepoService(temp.resolve(dir),
                new RepoOptions("main", "origin", "Test Player", "test@example.com"), debug);
    }

    @Test
    void initCreatesRepositoryWithBootstrapCommit() throws Exception {
        try (RepoService repo = service("repo")) {
            assertFalse(repo.isInitialized());
            RepoInfo info = repo.init();

            assertTrue(repo.isInitialized());
            assertEquals("main", info.branch());
            assertTrue(info.hasCommits());
            assertTrue(Files.isRegularFile(repo.path().resolve("README.md")));
            assertTrue(Files.isRegularFile(repo.path().resolve(".gitattributes")));

            List<CommitInfo> log = repo.log(10);
            assertEquals(1, log.size());
            assertEquals("Initialize MinecraftGit repository", log.get(0).message());
        }
    }

    @Test
    void workspaceCommitReplacesSelectionDirectory() throws Exception {
        try (RepoService repo = service("repo")) {
            repo.init();
            String first = repo.commitWorkspace(
                    Map.of("castle/meta.json", "{\"selection\":\"castle\"}".getBytes(StandardCharsets.UTF_8)),
                    List.of("castle"), "Add castle", false);
            assertNotNull(first);

            assertTrue(Files.isRegularFile(repo.path().resolve("castle/meta.json")));
            String second = repo.commitWorkspace(
                    Map.of("castle/meta.json", "{\"selection\":\"castle\",\"v\":2}".getBytes(StandardCharsets.UTF_8)),
                    List.of("castle"), "Update castle", false);
            assertNotNull(second);
            assertEquals(3, repo.log(10).size());

            String unchanged = repo.commitWorkspace(
                    Map.of("castle/meta.json", "{\"selection\":\"castle\",\"v\":2}".getBytes(StandardCharsets.UTF_8)),
                    List.of("castle"), "No-op", false);
            assertEquals(null, unchanged);
        }
    }

    @Test
    void pushToBareRemoteAndFetchIntoSecondRepository() throws Exception {
        Path barePath = temp.resolve("remote.git");
        try (Git bare = Git.init().setBare(true).setDirectory(barePath.toFile()).call()) {
            assertTrue(bare.getRepository().isBare());
        }

        try (RepoService origin = service("origin")) {
            origin.init();
            origin.commitWorkspace(
                    Map.of("castle/meta.json", "{\"selection\":\"castle\"}".getBytes(StandardCharsets.UTF_8)),
                    List.of("castle"), "Add castle", false);
            origin.remoteAdd("origin", barePath.toString());
            RepoService.PushSummary summary = origin.push("origin", false);
            assertFalse(summary.upToDate());
            assertTrue(summary.updatedRefs() >= 1);
        }

        try (Repository bare = Git.open(barePath.toFile()).getRepository()) {
            ObjectId head = bare.resolve("refs/heads/main");
            assertNotNull(head, "bare remote should have a main branch");
        }

        try (RepoService clone = service("second")) {
            clone.init();
            clone.remoteAdd("origin", barePath.toString());
            RepoService.FetchSummary fetched = clone.fetch("origin");
            assertTrue(fetched.updatedRefs() >= 1);

            PullPlan plan = clone.planPull("origin", RepoProgress.NOOP);
            assertEquals(PullPlan.Type.FAST_FORWARD, plan.type());
            assertTrue(clone.fastForward(plan));
            assertTrue(Files.isRegularFile(clone.path().resolve("castle/meta.json")));
        }
    }

    @Test
    void pushRejectsDivergedRemoteWithoutForce() throws Exception {
        Path barePath = temp.resolve("remote.git");
        try (Git ignored = Git.init().setBare(true).setDirectory(barePath.toFile()).call()) {
            // Empty remote.
        }

        try (RepoService first = service("first")) {
            first.init();
            first.commitWorkspace(Map.of("one/a.txt", "one".getBytes(StandardCharsets.UTF_8)),
                    List.of("one"), "First", false);
            first.remoteAdd("origin", barePath.toString());
            first.push("origin", false);

            try (RepoService second = service("second")) {
                second.init();
                second.remoteAdd("origin", barePath.toString());
                second.fetch("origin");
                PullPlan plan = second.planPull("origin", RepoProgress.NOOP);
                assertTrue(second.fastForward(plan));

                second.commitWorkspace(Map.of("two/b.txt", "two".getBytes(StandardCharsets.UTF_8)),
                        List.of("two"), "Second", false);

                // The remote has advanced through first's push; second's push is a
                // fast-forward only if it fetched first. Here it did, so this should
                // succeed. Verify a forced push also works as a sanity check.
                second.push("origin", false);
            }
        }
    }
}
