package org.almond.minecraftgit.world;

import org.almond.minecraftgit.model.Snapshot;

import java.util.List;

/** Result of a capture, including everything that could not be read. */
public record CaptureResult(Snapshot snapshot, int skippedChunks, long skippedBlocks,
                            int skippedEntities, List<String> warnings) {

    public CaptureResult {
        warnings = List.copyOf(warnings);
    }

    public boolean partial() {
        return skippedChunks > 0 || skippedBlocks > 0 || skippedEntities > 0;
    }

    public String summary() {
        if (!partial()) {
            return "Captured " + snapshot.blockCount() + " block(s) and " + snapshot.entityCount() + " entities.";
        }
        StringBuilder builder = new StringBuilder();
        builder.append("Captured ").append(snapshot.blockCount()).append(" block(s)");
        if (skippedChunks > 0) {
            builder.append(", skipped ").append(skippedChunks).append(" unloaded chunk(s)");
        }
        if (skippedEntities > 0) {
            builder.append(", skipped ").append(skippedEntities).append(" entity(ies)");
        }
        return builder.append('.').toString();
    }
}
