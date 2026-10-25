package org.almond.minecraftgit.staging;

import org.almond.minecraftgit.debug.DebugLogger;
import org.almond.minecraftgit.model.BlockPos;
import org.almond.minecraftgit.model.Selection;
import org.almond.minecraftgit.model.Snapshot;
import org.almond.minecraftgit.util.Names;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Durable staging area. {@code /git add} writes a captured snapshot here;
 * {@code /git commit} reads it later. Files survive restarts and are keyed by
 * world id + player + selection, never by repository.
 */
public final class StagingService {

    private final Path root;
    private final SnapshotStore store;
    private final DebugLogger debug;

    public StagingService(Path root, SnapshotStore store, DebugLogger debug) {
        this.root = root.toAbsolutePath().normalize();
        this.store = store;
        this.debug = debug;
    }

    public Path root() {
        return root;
    }

    public Path file(String worldId, UUID owner, String selection) {
        String name;
        try {
            name = Names.requireSelectionName(selection);
        } catch (IllegalArgumentException e) {
            throw new StagingException(e.getMessage());
        }
        return root.resolve(Names.safeWorldName(worldId)).resolve(owner.toString())
                .resolve(name + ".snap");
    }

    /** Write a capture to disk and return the staged handle once it is durable. */
    public StagedSelection stage(Selection selection, Snapshot snapshot) {
        if (!selection.hasOrigin()) {
            throw new StagingException("Selection '" + selection.name()
                    + "' has no origin; add a cuboid first.");
        }
        if (selection.owner() == null) {
            throw new StagingException("Selection '" + selection.name() + "' has no owner.");
        }
        Path file = file(selection.world(), selection.owner(), selection.name());
        Snapshot normalized = snapshot.origin().equals(selection.origin())
                ? snapshot : snapshot.rebase(selection.origin());
        store.write(file, normalized);
        debug.log("staging", "Staged %s (%d blocks, %d entities) at %s",
                selection.name(), normalized.blockCount(), normalized.entityCount(), file);
        return new StagedSelection(normalized, file, Instant.now());
    }

    public Optional<StagedSelection> load(String worldId, UUID owner, String selection) {
        Path file = file(worldId, owner, selection);
        if (!Files.isRegularFile(file)) {
            return Optional.empty();
        }
        Snapshot snapshot = store.read(file);
        Instant stagedAt;
        try {
            FileTime modified = Files.getLastModifiedTime(file);
            stagedAt = modified.toInstant();
        } catch (IOException e) {
            stagedAt = Instant.now();
        }
        return Optional.of(new StagedSelection(snapshot, file, stagedAt));
    }

    public boolean hasStaged(String worldId, UUID owner, String selection) {
        return Files.isRegularFile(file(worldId, owner, selection));
    }

    public List<String> stagedNames(String worldId, UUID owner) {
        Path directory = root.resolve(Names.safeWorldName(worldId)).resolve(owner.toString());
        if (!Files.isDirectory(directory)) {
            return List.of();
        }
        List<String> names = new ArrayList<>();
        try (Stream<Path> files = Files.list(directory)) {
            files.filter(path -> path.getFileName().toString().endsWith(".snap"))
                    .map(path -> path.getFileName().toString())
                    .map(name -> name.substring(0, name.length() - ".snap".length()))
                    .sorted()
                    .forEach(names::add);
        } catch (IOException e) {
            throw new StagingException("Could not list staged snapshots: " + e.getMessage(), e);
        }
        return names;
    }

    public void unstage(String worldId, UUID owner, String selection) {
        Path file = file(worldId, owner, selection);
        try {
            Files.deleteIfExists(file);
            debug.log("staging", "Removed staged snapshot %s", file);
        } catch (IOException e) {
            throw new StagingException("Could not remove staged snapshot at " + file + ": " + e.getMessage(), e);
        }
    }

    /** Rebase a staged snapshot after {@code /git selection reorigin}. */
    public void rebaseStaged(String worldId, UUID owner, String selection, BlockPos newOrigin) {
        Path file = file(worldId, owner, selection);
        if (!Files.isRegularFile(file)) {
            return;
        }
        Snapshot snapshot = store.read(file);
        Snapshot rebased = snapshot.rebase(newOrigin);
        store.write(file, rebased);
        debug.log("staging", "Rebased staged snapshot %s to %s", selection, newOrigin);
    }

    /**
     * Trim a staged snapshot after a cuboid is removed: coordinates outside the
     * remaining cuboids are untracked and must disappear from the next commit.
     * The origin itself does not move here (E2/E6).
     */
    public void restrictStaged(String worldId, UUID owner, Selection selection) {
        Path file = file(worldId, owner, selection.name());
        if (!Files.isRegularFile(file)) {
            return;
        }
        if (!selection.hasOrigin()) {
            return;
        }
        Snapshot snapshot = store.read(file);
        Snapshot retained = snapshot.retain(selection).rebase(selection.origin());
        store.write(file, retained);
        debug.log("staging", "Restricted staged snapshot %s to %d blocks",
                selection.name(), retained.blockCount());
    }

    public void deleteAll(String worldId, UUID owner, String selection) {
        try {
            Files.deleteIfExists(file(worldId, owner, selection));
        } catch (IOException e) {
            throw new StagingException("Could not delete staged snapshot: " + e.getMessage(), e);
        }
    }

    public SnapshotStore store() {
        return store;
    }
}
