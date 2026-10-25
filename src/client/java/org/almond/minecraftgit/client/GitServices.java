package org.almond.minecraftgit.client;

import net.fabricmc.loader.api.FabricLoader;
import org.almond.minecraftgit.MinecraftGit;
import org.almond.minecraftgit.commit.CommitService;
import org.almond.minecraftgit.config.ModConfig;
import org.almond.minecraftgit.client.selection.SelectionManager;
import org.almond.minecraftgit.debug.DebugLogger;
import org.almond.minecraftgit.repo.RepoManager;
import org.almond.minecraftgit.selection.SelectionService;
import org.almond.minecraftgit.staging.SnapshotStore;
import org.almond.minecraftgit.staging.StagingService;
import org.almond.minecraftgit.world.SnapshotService;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Service graph for the client mod. Repository, selection, staging and commit
 * services are added here as phases land; the IO executor is created once so
 * JGit work never runs on the render thread.
 */
public final class GitServices implements AutoCloseable {

    private final Path root;
    private final ModConfig config;
    private final DebugLogger debug;
    private final Messages messages;
    private final RepoManager repos;
    private final SelectionService selections;
    private final SelectionManager selectionManager;
    private final SnapshotService snapshots;
    private final StagingService staging;
    private final CommitService commits;
    private final ExecutorService io;

    public GitServices(Path root, ModConfig config, DebugLogger debug) {
        this.root = root.toAbsolutePath().normalize();
        this.config = config;
        this.debug = debug;
        this.messages = new Messages(config);
        this.repos = new RepoManager(config, debug);
        this.selections = new SelectionService(config);
        this.selectionManager = new SelectionManager(selections, repos);
        this.snapshots = new SnapshotService(config, debug);
        this.staging = new StagingService(config.stagingRoot(), new SnapshotStore(), debug);
        this.commits = new CommitService(repos, staging, config, debug, modVersion());
        ThreadFactory factory = runnable -> {
            Thread thread = new Thread(runnable, "minecraftgit-io");
            thread.setDaemon(true);
            return thread;
        };
        this.io = Executors.newFixedThreadPool(2, factory);
    }

    public Path root() {
        return root;
    }

    public ModConfig config() {
        return config;
    }

    public DebugLogger debug() {
        return debug;
    }

    public Messages messages() {
        return messages;
    }

    public RepoManager repos() {
        return repos;
    }

    public SelectionService selections() {
        return selections;
    }

    public SelectionManager selectionManager() {
        return selectionManager;
    }

    public SnapshotService snapshots() {
        return snapshots;
    }

    public StagingService staging() {
        return staging;
    }

    public CommitService commits() {
        return commits;
    }

    public static String modVersion() {
        return FabricLoader.getInstance().getModContainer(MinecraftGit.MOD_ID)
                .map(container -> container.getMetadata().getVersion().getFriendlyString())
                .orElse("development");
    }

    public ExecutorService io() {
        return io;
    }

    public <T> CompletableFuture<T> async(Supplier<T> task) {
        return CompletableFuture.supplyAsync(task, io);
    }

    public CompletableFuture<Void> asyncRun(Runnable task) {
        return CompletableFuture.runAsync(task, io);
    }

    @Override
    public void close() {
        repos.close();
        debug.close();
        io.shutdown();
        try {
            if (!io.awaitTermination(2, TimeUnit.SECONDS)) {
                io.shutdownNow();
            }
        } catch (InterruptedException e) {
            io.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
