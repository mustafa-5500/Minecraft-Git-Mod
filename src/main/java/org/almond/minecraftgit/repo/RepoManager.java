package org.almond.minecraftgit.repo;

import org.almond.minecraftgit.config.ModConfig;
import org.almond.minecraftgit.debug.DebugLogger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Owns the per-world-id {@link RepoService} instances and their local metadata.
 * A "world" here is the client's {@code <world>__<dimension>} key, not a Bukkit
 * world.
 */
public final class RepoManager implements AutoCloseable {

    private final ModConfig config;
    private final DebugLogger debug;
    private final RepoLocator locator;
    private final RepoMetadataStore metadataStore;
    private final Map<String, RepoService> services = new ConcurrentHashMap<>();

    public RepoManager(ModConfig config, DebugLogger debug) {
        this.config = config;
        this.debug = debug;
        this.locator = new RepoLocator(config.reposRoot(), config.metaRoot());
        this.metadataStore = new RepoMetadataStore(locator.metadataRoot(), debug);
    }

    public RepoLocator locator() {
        return locator;
    }

    public Path repoPath(String worldId) {
        return locator.repoPath(worldId);
    }

    public boolean isInitialized(String worldId) {
        RepoService existing = services.get(worldId);
        return existing != null ? existing.isInitialized()
                : Files.isDirectory(repoPath(worldId).resolve(".git"));
    }

    public RepoMetadata metadata(String worldId) {
        return metadataStore.load(worldId, defaults());
    }

    public void saveMetadata(RepoMetadata metadata) {
        metadataStore.save(metadata);
    }

    public RepoService service(String worldId) {
        return services.computeIfAbsent(worldId, id -> {
            RepoMetadata metadata = metadata(id);
            RepoOptions options = new RepoOptions(metadata.branch(), metadata.remoteName(),
                    metadata.authorName(), metadata.authorEmail());
            return new RepoService(locator.repoPath(id), options, debug);
        });
    }

    /**
     * Create or open the repository. When the repository still uses the default
     * identity, adopt the local player's name so commits are attributable without
     * an extra setup step.
     */
    public RepoInfo init(String worldId, String playerName, String playerEmail) {
        RepoMetadata metadata = metadata(worldId);
        if (isDefaultIdentity(metadata.authorName()) && playerName != null && !playerName.isBlank()) {
            metadata.setAuthorName(playerName);
            metadata.setAuthorEmail(playerEmail == null || playerEmail.isBlank()
                    ? playerName.replaceAll("[^A-Za-z0-9_.-]", "_") + "@minecraftgit.local"
                    : playerEmail);
            saveMetadata(metadata);
        }
        RepoService service = service(worldId);
        service.setAuthor(metadata.authorName(), metadata.authorEmail());
        RepoInfo info = service.init();
        metadata.setBranch(info.branch().isBlank() ? config.branch : info.branch());
        metadata.setAuthorName(service.authorName());
        metadata.setAuthorEmail(service.authorEmail());
        if (info.remoteUrl() != null && !info.remoteUrl().isBlank()) {
            metadata.setRemoteUrl(info.remoteUrl());
        }
        saveMetadata(metadata);
        return info;
    }

    public void setAuthor(String worldId, String name, String email) {
        RepoMetadata metadata = metadata(worldId);
        metadata.setAuthorName(name);
        metadata.setAuthorEmail(email);
        saveMetadata(metadata);
        RepoService service = services.get(worldId);
        if (service != null) {
            service.setAuthor(name, email);
        } else if (isInitialized(worldId)) {
            service(worldId).setAuthor(name, email);
        }
    }

    public void setLastSelection(String worldId, String selection) {
        RepoMetadata metadata = metadata(worldId);
        metadata.setLastSelection(selection == null ? "" : selection);
        saveMetadata(metadata);
    }

    public String lastSelection(String worldId) {
        return metadata(worldId).lastSelection();
    }

    public void reload() {
        close();
    }

    private RepoOptions defaults() {
        return new RepoOptions(config.branch, config.defaultRemote, config.committerName, config.committerEmail);
    }

    private static boolean isDefaultIdentity(String authorName) {
        return authorName == null || authorName.isBlank() || "MinecraftGit".equals(authorName);
    }

    @Override
    public void close() {
        services.values().forEach(RepoService::close);
        services.clear();
    }
}
