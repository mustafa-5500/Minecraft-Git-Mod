package org.almond.minecraftgit.commit;

import org.almond.minecraftgit.config.ModConfig;
import org.almond.minecraftgit.debug.DebugLogger;
import org.almond.minecraftgit.model.BlockPos;
import org.almond.minecraftgit.model.BuildDiff;
import org.almond.minecraftgit.model.Cuboid;
import org.almond.minecraftgit.model.Selection;
import org.almond.minecraftgit.model.Snapshot;
import org.almond.minecraftgit.repo.CommitMeta;
import org.almond.minecraftgit.repo.RepoManager;
import org.almond.minecraftgit.repo.RepoService;
import org.almond.minecraftgit.serialization.QuadrantCsvReader;
import org.almond.minecraftgit.serialization.QuadrantCsvWriter;
import org.almond.minecraftgit.staging.StagedSelection;
import org.almond.minecraftgit.staging.StagingService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

/**
 * Turns staged snapshots into repository commits: writes the CSV tree, writes
 * per-selection metadata, produces a readable commit message, and verifies that
 * something actually changed before touching Git.
 */
public final class CommitService {

    private final RepoManager repos;
    private final StagingService staging;
    private final ModConfig config;
    private final DebugLogger debug;
    private final String modVersion;
    private final ReentrantLock commitLock = new ReentrantLock();

    public CommitService(RepoManager repos, StagingService staging, ModConfig config,
                         DebugLogger debug, String modVersion) {
        this.repos = repos;
        this.staging = staging;
        this.config = config;
        this.debug = debug;
        this.modVersion = modVersion;
    }

    public CommitResult commit(CommitRequest request) {
        commitLock.lock();
        try {
            RepoService repo = repos.service(request.worldName());
            if (!repo.isInitialized()) {
                throw new CommitException("No Git repository exists for this world. Run /git init first.");
            }
            if (request.selections().isEmpty() && !request.allowEmpty()) {
                throw new CommitException("Nothing is staged. Use /git add <selection> first.");
            }

            Files.createDirectories(config.tmpRoot());
            Map<String, byte[]> files = new LinkedHashMap<>();
            Set<String> directories = new LinkedHashSet<>();
            List<String> committedNames = new ArrayList<>();
            List<String> unchangedNames = new ArrayList<>();
            long added = 0;
            long changed = 0;
            long removed = 0;
            long entityChanges = 0;

            for (Selection selection : request.selections()) {
                StagedSelection staged = staging
                        .load(request.worldName(), selection.owner(), selection.name())
                        .orElseThrow(() -> new CommitException("Selection '" + selection.name()
                                + "' is not staged. Run /git add " + selection.name() + " first."));
                Snapshot stagedSnapshot = staged.snapshot();

                Path oldDirectory = repo.path().resolve(selection.name());
                boolean existed = Files.isDirectory(oldDirectory);
                Optional<CommitMeta> oldMeta = existed
                        ? CommitMeta.read(oldDirectory.resolve("meta.json")) : Optional.empty();
                Snapshot oldSnapshot;
                BlockPos previousOrigin = null;
                if (existed) {
                    BlockPos fallbackOrigin = oldMeta.map(CommitMeta::origin).orElse(stagedSnapshot.origin());
                    oldSnapshot = QuadrantCsvReader.readSelection(oldDirectory, selection.name(), fallbackOrigin);
                    previousOrigin = oldMeta.map(CommitMeta::origin).orElse(oldSnapshot.origin());
                } else {
                    oldSnapshot = Snapshot.empty(selection.name(), stagedSnapshot.origin());
                }

                // Relative coordinates are canonical; a pure origin change still
                // needs a commit so the new absolute origin is recorded in meta.json.
                boolean reorigined = previousOrigin != null && !previousOrigin.equals(stagedSnapshot.origin());
                BuildDiff diff = BuildDiff.between(oldSnapshot, stagedSnapshot);
                if (diff.isEmpty() && !reorigined) {
                    unchangedNames.add(selection.name());
                    continue;
                }
                added += diff.added().size();
                changed += diff.changed().size();
                removed += diff.removed().size();
                entityChanges += diff.totalEntityChanges();
                committedNames.add(selection.name());
                directories.add(selection.name());

                CommitMeta meta = buildMeta(request, selection, stagedSnapshot, previousOrigin);
                Path temp = Files.createTempDirectory(config.tmpRoot(), "commit-");
                try {
                    QuadrantCsvWriter.writeSelection(temp, stagedSnapshot);
                    collectFiles(temp, selection.name(), files);
                    Path metaFile = temp.resolve("meta.json");
                    meta.write(metaFile);
                    files.put(selection.name() + "/meta.json", Files.readAllBytes(metaFile));
                } finally {
                    deleteRecursively(temp);
                }
            }

            if (committedNames.isEmpty() && !request.allowEmpty()) {
                throw new CommitException("Nothing to commit: " + (unchangedNames.isEmpty()
                        ? "no staged selections changed." : "the staged selection(s) are unchanged."));
            }

            String message = commitMessage(request, committedNames, added, changed, removed, entityChanges);
            String commitId = repo.commitWorkspace(files, directories, message,
                    request.allowEmpty() || committedNames.isEmpty());
            if (commitId == null) {
                throw new CommitException("Nothing to commit; the repository is unchanged.");
            }

            for (Selection selection : request.selections()) {
                staging.deleteAll(request.worldName(), selection.owner(), selection.name());
            }
            debug.log("commit", "Committed %s with %d changed selection(s), %d blocks",
                    commitId.substring(0, 8), committedNames.size(), added + changed + removed);
            return new CommitResult(commitId, commitId.substring(0, Math.min(8, commitId.length())),
                    List.copyOf(committedNames), added, changed, removed, entityChanges, message);
        } catch (IOException e) {
            throw new CommitException("Could not build the commit tree: " + e.getMessage(), e);
        } finally {
            commitLock.unlock();
        }
    }

    private CommitMeta buildMeta(CommitRequest request, Selection selection, Snapshot snapshot,
                                 BlockPos previousOrigin) {
        BlockPos origin = snapshot.origin();
        List<Cuboid> relativeCuboids = new ArrayList<>();
        for (Cuboid cuboid : selection.cuboids()) {
            relativeCuboids.add(new Cuboid(
                    cuboid.minX() - origin.x(), cuboid.minY() - origin.y(), cuboid.minZ() - origin.z(),
                    cuboid.maxX() - origin.x(), cuboid.maxY() - origin.y(), cuboid.maxZ() - origin.z()));
        }
        BlockPos min = selection.boundsMin();
        BlockPos max = selection.boundsMax();
        String dimensions = min == null || max == null ? snapshot.dimensions()
                : (max.x() - min.x() + 1) + "x" + (max.y() - min.y() + 1) + "x" + (max.z() - min.z() + 1);
        boolean reorigined = previousOrigin != null && !previousOrigin.equals(origin);
        return new CommitMeta(selection.name(), request.worldName(), request.playerName(), request.playerUuid(),
                request.gameMode(), CommitMeta.FORMAT_VERSION, modVersion,
                origin, previousOrigin, reorigined, dimensions, selection.cuboidCount(),
                snapshot.blockCount(), snapshot.entityCount(), relativeCuboids, Instant.now().toString());
    }

    private String commitMessage(CommitRequest request, List<String> committedNames,
                                 long added, long changed, long removed, long entityChanges) {
        String header;
        if (request.message() != null && !request.message().isBlank()) {
            header = request.message().trim();
        } else if (committedNames.isEmpty()) {
            header = request.playerName() + ": empty commit";
        } else if (committedNames.size() == 1) {
            header = request.playerName() + ": " + committedNames.get(0) + " - "
                    + (added + changed + removed) + " blocks changed";
        } else {
            header = request.playerName() + ": " + committedNames.size() + " selections - "
                    + (added + changed + removed) + " blocks changed";
        }
        return header + "\n\nMinecraftGit-Commit: v1\n"
                + "Selections: " + String.join(", ", committedNames) + "\n"
                + "Changes: +" + added + " ~" + changed + " -" + removed + "\n"
                + "Entities: " + entityChanges + "\n";
    }

    private static void collectFiles(Path root, String selection, Map<String, byte[]> files) throws IOException {
        try (Stream<Path> walk = Files.walk(root)) {
            for (Path path : walk.filter(Files::isRegularFile).toList()) {
                String relative = root.relativize(path).toString().replace('\\', '/');
                files.put(selection + "/" + relative, Files.readAllBytes(path));
            }
        }
    }

    private static void deleteRecursively(Path path) {
        if (!Files.exists(path)) {
            return;
        }
        try (Stream<Path> walk = Files.walk(path)) {
            for (Path entry : walk.sorted(java.util.Comparator.reverseOrder()).toList()) {
                Files.deleteIfExists(entry);
            }
        } catch (IOException ignored) {
            // Temporary directory cleanup is best-effort.
        }
    }
}
