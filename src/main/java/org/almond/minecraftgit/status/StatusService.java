package org.almond.minecraftgit.status;

import org.almond.minecraftgit.debug.DebugLogger;
import org.almond.minecraftgit.model.BlockPos;
import org.almond.minecraftgit.model.BuildDiff;
import org.almond.minecraftgit.model.Selection;
import org.almond.minecraftgit.model.Snapshot;
import org.almond.minecraftgit.repo.CommitMeta;
import org.almond.minecraftgit.repo.RepoManager;
import org.almond.minecraftgit.repo.RepoService;
import org.almond.minecraftgit.serialization.QuadrantCsvReader;
import org.almond.minecraftgit.staging.StagedSelection;
import org.almond.minecraftgit.staging.StagingService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Computes status and diffs by comparing three snapshots per selection: HEAD
 * (committed), staged (local index), and a current capture (the world).
 */
public final class StatusService {

    private final RepoManager repos;
    private final StagingService staging;
    private final DebugLogger debug;

    public StatusService(RepoManager repos, StagingService staging, DebugLogger debug) {
        this.repos = repos;
        this.staging = staging;
        this.debug = debug;
    }

    public Report report(String worldId, UUID owner, List<Selection> selections,
                         Map<String, Snapshot> current, Set<String> tooLarge, Set<String> incomplete) {
        RepoService repo = repos.service(worldId);
        boolean initialized = repo.isInitialized();
        String branch = "";
        String head = "";
        if (initialized) {
            try {
                branch = repo.currentBranch();
                head = repo.headCommit().map(commit -> commit.shortId()).orElse("");
            } catch (RuntimeException e) {
                debug.log("status", "Could not read repository info: %s", e.getMessage());
            }
        }
        List<SelectionStatus> statuses = new ArrayList<>();
        for (Selection selection : selections) {
            statuses.add(statusOf(initialized, repo, selection, current.get(selection.name()),
                    tooLarge.contains(selection.name()), incomplete.contains(selection.name())));
        }
        statuses.sort(Comparator.comparing(status -> status.name().toLowerCase(java.util.Locale.ROOT)));
        return new Report(initialized, branch, head, statuses);
    }

    public DiffReport diff(String worldId, UUID owner, Selection selection, Snapshot current,
                           boolean tooLarge, boolean incomplete) {
        RepoService repo = repos.service(worldId);
        boolean initialized = repo.isInitialized();
        Snapshot head = initialized ? readHead(repo, selection) : null;
        Optional<StagedSelection> staged = loadStaged(worldId, owner, selection);
        Snapshot stagedSnapshot = staged.map(StagedSelection::snapshot).orElse(null);
        Snapshot base = stagedSnapshot != null ? stagedSnapshot
                : head != null ? head
                : Snapshot.empty(selection.name(), selection.hasOrigin() ? selection.origin() : BlockPos.ORIGIN);

        BuildDiff stagedDiff = BuildDiff.between(head == null
                        ? Snapshot.empty(selection.name(), base.origin()) : head,
                stagedSnapshot == null ? base : stagedSnapshot);
        BuildDiff workingDiff = current == null || incomplete ? BuildDiff.between(null, null)
                : BuildDiff.between(base, current);
        SelectionStatus status = statusOf(initialized, repo, selection, current, tooLarge, incomplete);
        return new DiffReport(status, stagedDiff, workingDiff, current);
    }

    private SelectionStatus statusOf(boolean initialized, RepoService repo, Selection selection,
                                     Snapshot current, boolean tooLarge, boolean incomplete) {
        Optional<StagedSelection> staged = loadStaged(selection.world(), selection.owner(), selection);
        Snapshot head = initialized ? readHead(repo, selection) : null;
        boolean inHead = head != null;
        String note = null;

        DiffCounts stagedCounts = DiffCounts.zero();
        if (staged.isPresent()) {
            Snapshot base = head == null ? Snapshot.empty(selection.name(), staged.get().snapshot().origin())
                    : head;
            stagedCounts = DiffCounts.of(BuildDiff.between(base, staged.get().snapshot()));
        }

        DiffCounts workingCounts = DiffCounts.zero();
        if (current != null && !incomplete) {
            Snapshot base;
            if (staged.isPresent()) {
                base = staged.get().snapshot();
            } else if (head != null) {
                base = head;
            } else {
                base = Snapshot.empty(selection.name(), current.origin());
            }
            workingCounts = DiffCounts.of(BuildDiff.between(base, current));
        } else if (tooLarge) {
            note = "selection volume exceeds the status capture limit";
        } else if (incomplete) {
            note = "current capture skipped unloaded chunks; working diff omitted";
        } else if (selection.isTracked() || inHead || staged.isPresent()) {
            note = "current blocks were not captured";
        }

        return new SelectionStatus(selection.name(), selection.isTracked(), inHead, staged.isPresent(),
                selection.cuboidCount(), selection.volume(), current != null && !incomplete, tooLarge,
                stagedCounts, workingCounts, note);
    }

    private Optional<StagedSelection> loadStaged(String worldId, UUID owner, Selection selection) {
        try {
            return staging.load(worldId, owner, selection.name());
        } catch (RuntimeException e) {
            debug.log("status", "Could not read staged snapshot for %s: %s", selection.name(), e.getMessage());
            return Optional.empty();
        }
    }

    private Snapshot readHead(RepoService repo, Selection selection) {
        Path directory = repo.path().resolve(selection.name());
        if (!Files.isDirectory(directory)) {
            return null;
        }
        try {
            BlockPos origin = CommitMeta.read(directory.resolve("meta.json"))
                    .map(CommitMeta::origin)
                    .orElse(selection.hasOrigin() ? selection.origin() : BlockPos.ORIGIN);
            return QuadrantCsvReader.readSelection(directory, selection.name(), origin);
        } catch (RuntimeException e) {
            debug.log("status", "Could not read HEAD snapshot for %s: %s", selection.name(), e.getMessage());
            return null;
        }
    }

    public record DiffCounts(int added, int changed, int removed, int entityChanges) {

        public static DiffCounts zero() {
            return new DiffCounts(0, 0, 0, 0);
        }

        public static DiffCounts of(BuildDiff diff) {
            return new DiffCounts(diff.added().size(), diff.changed().size(), diff.removed().size(),
                    (int) diff.totalEntityChanges());
        }

        public boolean clean() {
            return added == 0 && changed == 0 && removed == 0 && entityChanges == 0;
        }

        public int total() {
            return added + changed + removed + entityChanges;
        }

        public String summary() {
            return "+" + added + " ~" + changed + " -" + removed
                    + (entityChanges > 0 ? " e" + entityChanges : "");
        }
    }

    public record SelectionStatus(String name, boolean tracked, boolean inHead, boolean staged,
                                  int cuboidCount, long volume, boolean currentChecked, boolean tooLarge,
                                  DiffCounts stagedDiff, DiffCounts workingDiff, String note) {

        public boolean clean() {
            return stagedDiff.clean() && workingDiff.clean() && note == null;
        }

        public boolean untracked() {
            return !tracked && !inHead && !staged;
        }

        public boolean dirty() {
            return !untracked() && !clean();
        }
    }

    public record Report(boolean repoInitialized, String branch, String head, List<SelectionStatus> selections) {

        public long dirtyCount() {
            return selections.stream().filter(SelectionStatus::dirty).count();
        }

        public long untrackedCount() {
            return selections.stream().filter(SelectionStatus::untracked).count();
        }

        public long cleanCount() {
            return selections.stream().filter(status -> !status.untracked() && status.clean()).count();
        }
    }

    public record DiffReport(SelectionStatus status, BuildDiff stagedDiff, BuildDiff workingDiff,
                             Snapshot current) {
    }
}
