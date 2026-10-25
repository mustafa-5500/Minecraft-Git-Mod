package org.almond.minecraftgit.backup;

import org.almond.minecraftgit.model.Snapshot;
import org.almond.minecraftgit.staging.SnapshotStore;
import org.almond.minecraftgit.util.Names;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

/**
 * Local pre-apply snapshots so a pull or checkout can be undone. Backups are
 * gzipped binary snapshots under the mod data folder and are pruned to the
 * configured maximum.
 */
public final class BackupService {

    private static final AtomicLong SEQUENCE = new AtomicLong();

    private final Path root;
    private final int maxBackups;
    private final SnapshotStore store = new SnapshotStore();

    public BackupService(Path root, int maxBackups) {
        this.root = root.toAbsolutePath().normalize();
        this.maxBackups = Math.max(1, maxBackups);
    }

    public Path store(String world, UUID owner, String selection, Snapshot snapshot) {
        String name = requireName(selection);
        Path directory = directory(world, owner, name);
        try {
            Files.createDirectories(directory);
            String file = System.currentTimeMillis() + "-" + SEQUENCE.incrementAndGet() + ".snap";
            Path path = directory.resolve(file);
            store.write(path, snapshot);
            prune(directory);
            return path;
        } catch (IOException e) {
            throw new BackupException("Could not create a pre-apply backup: " + e.getMessage(), e);
        }
    }

    public Optional<Backup> latest(String world, UUID owner, String selection) {
        List<Path> files = list(world, owner, selection);
        if (files.isEmpty()) {
            return Optional.empty();
        }
        Path newest = files.get(files.size() - 1);
        return Optional.of(new Backup(store.read(newest), newest));
    }

    public List<Path> list(String world, UUID owner, String selection) {
        Path directory = directory(world, owner, requireName(selection));
        if (!Files.isDirectory(directory)) {
            return List.of();
        }
        try (Stream<Path> files = Files.list(directory)) {
            List<Path> result = new ArrayList<>(files
                    .filter(path -> path.getFileName().toString().endsWith(".snap"))
                    .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                    .toList());
            return result;
        } catch (IOException e) {
            throw new BackupException("Could not list backups: " + e.getMessage(), e);
        }
    }

    public Path directory(String world, UUID owner, String selection) {
        return root.resolve(Names.safeWorldName(world))
                .resolve(owner == null ? "console" : owner.toString())
                .resolve(requireName(selection));
    }

    private void prune(Path directory) throws IOException {
        List<Path> files;
        try (Stream<Path> stream = Files.list(directory)) {
            files = new ArrayList<>(stream.filter(path -> path.getFileName().toString().endsWith(".snap"))
                    .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                    .toList());
        }
        while (files.size() > maxBackups) {
            Files.deleteIfExists(files.remove(0));
        }
    }

    private static String requireName(String selection) {
        try {
            return Names.requireSelectionName(selection);
        } catch (IllegalArgumentException e) {
            throw new BackupException(e.getMessage(), e);
        }
    }

    public record Backup(Snapshot snapshot, Path file) {
    }
}
