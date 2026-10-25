package org.almond.minecraftgit.repo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.almond.minecraftgit.model.BlockPos;
import org.almond.minecraftgit.model.Cuboid;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Human- and machine-readable metadata committed next to each selection. It is
 * intentionally part of the Git tree (not local state) so a clone can shift a
 * build onto a new origin and later training-data tooling can resolve regions.
 */
public record CommitMeta(String selection, String world, String authorName, String authorUuid,
                         String gameMode, int formatVersion, String pluginVersion,
                         BlockPos origin, BlockPos previousOrigin, boolean reorigined,
                         String dimensions, int cuboidCount, int blockCount, int entityCount,
                         List<Cuboid> relativeCuboids, String createdAt) {

    public static final int FORMAT_VERSION = 1;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public CommitMeta {
        relativeCuboids = relativeCuboids == null ? List.of() : List.copyOf(relativeCuboids);
        createdAt = createdAt == null ? Instant.now().toString() : createdAt;
    }

    public void write(Path file) {
        try {
            Files.createDirectories(file.getParent());
            Files.writeString(file, GSON.toJson(this), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RepoException("Could not write commit metadata to " + file + ": " + e.getMessage(), e);
        }
    }

    public static Optional<CommitMeta> read(Path file) {
        if (!Files.isRegularFile(file)) {
            return Optional.empty();
        }
        try {
            CommitMeta meta = GSON.fromJson(Files.readString(file, StandardCharsets.UTF_8), CommitMeta.class);
            return Optional.ofNullable(meta);
        } catch (JsonSyntaxException e) {
            return Optional.empty();
        } catch (IOException e) {
            throw new RepoException("Could not read commit metadata at " + file + ": " + e.getMessage(), e);
        }
    }

    public String toJson() {
        return GSON.toJson(this);
    }
}
