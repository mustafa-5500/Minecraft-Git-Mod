package org.almond.minecraftgit.repo;

import org.almond.minecraftgit.util.Names;

import java.nio.file.Path;

/**
 * Decides where repositories and their local-only metadata live.
 *
 * <p>Resolved open question Q1: repositories live in the plugin data folder
 * ({@code plugins/MinecraftGitPlugin/repos/<world>}), not inside the world
 * folder. World backups therefore never capture a hot {@code .git} directory,
 * and a repository is not accidentally copied with the map.</p>
 */
public final class RepoLocator {

    private final Path repoRoot;
    private final Path metadataRoot;

    public RepoLocator(Path repoRoot, Path metadataRoot) {
        this.repoRoot = repoRoot.toAbsolutePath().normalize();
        this.metadataRoot = metadataRoot.toAbsolutePath().normalize();
    }

    public Path repoRoot() {
        return repoRoot;
    }

    public Path metadataRoot() {
        return metadataRoot;
    }

    public Path repoPath(String worldName) {
        return repoRoot.resolve(Names.safeWorldName(worldName));
    }

    public Path metadataPath(String worldName) {
        return metadataRoot.resolve(Names.safeWorldName(worldName) + ".json");
    }
}
