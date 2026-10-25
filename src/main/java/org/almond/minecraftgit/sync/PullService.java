package org.almond.minecraftgit.sync;

import org.almond.minecraftgit.model.BlockPos;
import org.almond.minecraftgit.model.Selection;
import org.almond.minecraftgit.model.Snapshot;
import org.almond.minecraftgit.repo.CommitMeta;
import org.almond.minecraftgit.repo.PullPlan;
import org.almond.minecraftgit.repo.RepoService;
import org.almond.minecraftgit.serialization.QuadrantCsvReader;
import org.eclipse.jgit.lib.ObjectId;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Reads remote commit trees and local HEAD state into snapshots that the apply
 * service can turn into world writes. All filesystem/repository work here runs
 * off the client thread; only selection metadata and world writes happen later.
 */
public final class PullService {

    private final Path tmpRoot;

    public PullService(Path tmpRoot) {
        this.tmpRoot = tmpRoot.toAbsolutePath().normalize();
    }

    public List<PreparedPull> prepare(String worldId, List<Selection> localSelections, BlockPos playerOrigin,
                                      RepoService repo, PullPlan plan, String onlySelection) {
        List<String> remoteNames = new ArrayList<>(repo.listSelectionsAt(plan.remoteHead()));
        remoteNames.sort(Comparator.naturalOrder());
        if (onlySelection != null && !onlySelection.isBlank()) {
            String wanted = onlySelection;
            boolean present = remoteNames.stream().anyMatch(name -> name.equalsIgnoreCase(wanted));
            if (!present) {
                throw new PullException("Selection '" + onlySelection + "' is not in the remote commit "
                        + shortId(plan.remoteHead()) + ".");
            }
            if (remoteNames.size() > 1) {
                // A fast-forward moves the branch to the whole remote tree. Applying
                // only one selection would leave the other selections in HEAD but
                // not in the world, so partial pulls are refused rather than
                // silently desynchronizing the branch from the world.
                throw new PullException("This remote contains " + remoteNames.size()
                        + " selections. Pull all of them with /git pull; partial pulls are not "
                        + "supported in version 1.");
            }
        }
        if (remoteNames.isEmpty()) {
            return List.of();
        }
        Path root = null;
        try {
            Files.createDirectories(tmpRoot);
            root = Files.createTempDirectory(tmpRoot, "pull-");
            List<PreparedPull> prepared = new ArrayList<>();
            for (String name : remoteNames) {
                if (onlySelection != null && !onlySelection.isBlank()
                        && !name.equalsIgnoreCase(onlySelection)) {
                    continue;
                }
                Path targetDir = root.resolve(name);
                repo.exportSelectionAt(plan.remoteHead(), name, targetDir);
                CommitMeta meta = CommitMeta.read(targetDir.resolve("meta.json")).orElse(null);
                BlockPos fallback = meta == null ? BlockPos.ORIGIN : meta.origin();
                Snapshot target = QuadrantCsvReader.readSelection(targetDir, name, fallback);

                Selection localSelection = localSelections.stream()
                        .filter(candidate -> candidate.name().equalsIgnoreCase(name))
                        .findFirst().orElse(null);
                BlockPos localOrigin = localSelection != null && localSelection.hasOrigin()
                        ? localSelection.origin() : playerOrigin;

                Snapshot localHead = null;
                BlockPos localHeadOrigin = null;
                Path localDir = repo.path().resolve(name);
                if (Files.isDirectory(localDir)) {
                    CommitMeta localMeta = CommitMeta.read(localDir.resolve("meta.json")).orElse(null);
                    BlockPos localFallback = localMeta != null ? localMeta.origin()
                            : (localSelection != null && localSelection.hasOrigin()
                            ? localSelection.origin() : localOrigin);
                    localHead = QuadrantCsvReader.readSelection(localDir, name, localFallback);
                    localHeadOrigin = localMeta != null ? localMeta.origin() : localHead.origin();
                }
                prepared.add(new PreparedPull(name, target, meta, localSelection, localOrigin,
                        localHead, localHeadOrigin, targetDir));
            }
            return prepared;
        } catch (IOException e) {
            cleanup(root);
            throw new PullException("Could not prepare pull: " + e.getMessage(), e);
        } catch (RuntimeException e) {
            cleanup(root);
            throw e;
        }
    }

    public PreparedCheckout prepareCheckout(Selection selection, RepoService repo, ObjectId commit) {
        Path root = null;
        try {
            Files.createDirectories(tmpRoot);
            root = Files.createTempDirectory(tmpRoot, "checkout-");
            Path targetDir = root.resolve(selection.name());
            boolean found = repo.exportSelectionAt(commit, selection.name(), targetDir);
            if (!found) {
                throw new PullException("Commit " + shortId(commit) + " has no selection '"
                        + selection.name() + "'.");
            }
            CommitMeta meta = CommitMeta.read(targetDir.resolve("meta.json")).orElse(null);
            BlockPos fallback = meta == null ? selection.origin() : meta.origin();
            Snapshot target = QuadrantCsvReader.readSelection(targetDir, selection.name(), fallback);

            Snapshot localHead = null;
            BlockPos localHeadOrigin = null;
            Path localDir = repo.path().resolve(selection.name());
            if (Files.isDirectory(localDir)) {
                CommitMeta localMeta = CommitMeta.read(localDir.resolve("meta.json")).orElse(null);
                BlockPos localFallback = localMeta != null ? localMeta.origin() : selection.origin();
                localHead = QuadrantCsvReader.readSelection(localDir, selection.name(), localFallback);
                localHeadOrigin = localMeta != null ? localMeta.origin() : localHead.origin();
            }
            return new PreparedCheckout(target, meta, localHead, localHeadOrigin, root);
        } catch (IOException e) {
            cleanup(root);
            throw new PullException("Could not prepare checkout: " + e.getMessage(), e);
        } catch (RuntimeException e) {
            cleanup(root);
            throw e;
        }
    }

    public void cleanup(Path root) {
        if (root == null || !Files.exists(root)) {
            return;
        }
        try (var walk = Files.walk(root)) {
            for (Path path : walk.sorted(Comparator.reverseOrder()).toList()) {
                Files.deleteIfExists(path);
            }
        } catch (IOException ignored) {
            // Temporary directory cleanup is best-effort.
        }
    }

    public void cleanup(List<PreparedPull> prepared) {
        if (!prepared.isEmpty()) {
            cleanup(prepared.get(0).tempDirectory().getParent());
        }
    }

    private static String shortId(ObjectId id) {
        return id == null ? "?" : id.abbreviate(8).name();
    }

    public record PreparedPull(String name, Snapshot target, CommitMeta meta, Selection localSelection,
                               BlockPos localOrigin, Snapshot localHead, BlockPos localHeadOrigin,
                               Path tempDirectory) {
    }

    public record PreparedCheckout(Snapshot target, CommitMeta meta, Snapshot localHead,
                                   BlockPos localHeadOrigin, Path tempDirectory) {
    }
}
