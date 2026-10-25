package org.almond.minecraftgit.repo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.almond.minecraftgit.debug.DebugLogger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Reads and writes local repository metadata, one JSON file per world.
 */
public final class RepoMetadataStore {

    private final Path metadataRoot;
    private final DebugLogger debug;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public RepoMetadataStore(Path metadataRoot, DebugLogger debug) {
        this.metadataRoot = metadataRoot;
        this.debug = debug;
    }

    public RepoMetadata load(String world, RepoOptions defaults) {
        Path file = file(world);
        if (!Files.isRegularFile(file)) {
            return RepoMetadata.defaults(world, defaults);
        }
        try {
            String json = Files.readString(file, StandardCharsets.UTF_8);
            RepoMetadata metadata = gson.fromJson(json, RepoMetadata.class);
            if (metadata == null) {
                throw new RepoException("Repository metadata at " + file + " is empty. Delete it to recreate it.");
            }
            return metadata.normalize(world, defaults);
        } catch (JsonSyntaxException e) {
            throw new RepoException("Repository metadata at " + file + " is not valid JSON. "
                    + "Delete it to recreate it, or fix the file.", e);
        } catch (IOException e) {
            throw new RepoException("Could not read repository metadata at " + file + ": " + e.getMessage(), e);
        }
    }

    public void save(RepoMetadata metadata) {
        Path file = file(metadata.world());
        try {
            Files.createDirectories(metadataRoot);
            Path tmp = file.resolveSibling(file.getFileName() + ".tmp");
            Files.writeString(tmp, gson.toJson(metadata), StandardCharsets.UTF_8);
            try {
                Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING);
            }
            debug.log("repo", "Saved metadata for world %s", metadata.world());
        } catch (IOException e) {
            throw new RepoException("Could not save repository metadata at " + file + ": " + e.getMessage(), e);
        }
    }

    public Path file(String world) {
        return metadataRoot.resolve(org.almond.minecraftgit.util.Names.safeWorldName(world) + ".json");
    }
}
