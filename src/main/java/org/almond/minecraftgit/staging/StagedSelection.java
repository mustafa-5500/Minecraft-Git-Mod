package org.almond.minecraftgit.staging;

import org.almond.minecraftgit.model.Snapshot;

import java.nio.file.Path;
import java.time.Instant;

/** A snapshot persisted locally, ready for a commit. */
public record StagedSelection(Snapshot snapshot, Path file, Instant stagedAt) {

    public String selectionName() {
        return snapshot.selection();
    }

    public int blockCount() {
        return snapshot.blockCount();
    }

    public int entityCount() {
        return snapshot.entityCount();
    }
}
