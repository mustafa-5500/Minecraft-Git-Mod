package org.almond.minecraftgit.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.almond.minecraftgit.MinecraftGit;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Typed view over {@code config/minecraftgit/config.json}.
 *
 * <p>The fields are the persisted schema. Defaults are deliberately conservative
 * for client memory/tick budgets; the plugin's much larger caps assume a server
 * thread that can spend seconds on a capture.</p>
 */
public final class ModConfig {

    public boolean debug = false;
    public boolean debugBlocks = false;

    public String branch = "main";
    public String defaultRemote = "origin";
    public String wandItem = "minecraft:wooden_shovel";
    public String prefix = "[Git] ";

    public String committerName = "MinecraftGit";
    public String committerEmail = "minecraftgit@localhost";

    public int maxCuboidsPerSelection = 64;
    public long maxVolumePerSelection = 1_000_000L;
    public long maxBlocksPerCommit = 2_000_000L;
    public int maxEntitiesPerSelection = 2000;
    public int maxBlockEntityBytes = 262_144;
    public long statusMaxVolume = 250_000L;

    public int blocksPerTick = 4096;
    public int maxBackups = 10;

    public boolean renderSelections = true;
    public int renderBudget = 256;

    private transient Path root;
    private transient Gson gson;

    private ModConfig() {
    }

    /**
     * Load the config, creating it with defaults when it does not exist.
     * A corrupt file is reported once and defaults are used; the old file is
     * left untouched so a player can fix it.
     */
    public static ModConfig load(Path root) {
        ModConfig config = new ModConfig();
        config.root = root.toAbsolutePath().normalize();
        config.gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        Path file = config.file();
        try {
            Files.createDirectories(config.root);
            if (Files.isRegularFile(file)) {
                String json = Files.readString(file, StandardCharsets.UTF_8);
                ModConfig parsed = config.gson.fromJson(json, ModConfig.class);
                if (parsed != null) {
                    parsed.root = config.root;
                    parsed.gson = config.gson;
                    config = parsed;
                }
            }
            config.normalize();
            config.save();
            return config;
        } catch (JsonSyntaxException e) {
            MinecraftGit.LOGGER.warn("Config at {} is not valid JSON; using defaults. {}", file, e.getMessage());
        } catch (IOException e) {
            MinecraftGit.LOGGER.warn("Could not read config at {}; using defaults. {}", file, e.getMessage());
        }
        config.normalize();
        return config;
    }

    public Path root() {
        return root;
    }

    public Path file() {
        return root.resolve("config.json");
    }

    public Path reposRoot() {
        return root.resolve("repos");
    }

    public Path metaRoot() {
        return root.resolve("meta");
    }

    public Path selectionsRoot() {
        return root.resolve("selections");
    }

    public Path stagingRoot() {
        return root.resolve("staging");
    }

    public Path backupsRoot() {
        return root.resolve("backups");
    }

    public Path tmpRoot() {
        return root.resolve("tmp");
    }

    public Path logsRoot() {
        return root.resolve("logs");
    }

    /** Fill in blanks and reject non-positive sizes. */
    public void normalize() {
        branch = nonBlank(branch, "main");
        defaultRemote = nonBlank(defaultRemote, "origin");
        wandItem = nonBlank(wandItem, "minecraft:wooden_shovel");
        prefix = prefix == null ? "" : prefix;
        committerName = nonBlank(committerName, "MinecraftGit");
        committerEmail = nonBlank(committerEmail, "minecraftgit@localhost");
        maxCuboidsPerSelection = positive(maxCuboidsPerSelection, 64);
        maxVolumePerSelection = positive(maxVolumePerSelection, 1_000_000L);
        maxBlocksPerCommit = positive(maxBlocksPerCommit, 2_000_000L);
        maxEntitiesPerSelection = positive(maxEntitiesPerSelection, 2000);
        maxBlockEntityBytes = positive(maxBlockEntityBytes, 262_144);
        statusMaxVolume = positive(statusMaxVolume, 250_000L);
        blocksPerTick = positive(blocksPerTick, 4096);
        maxBackups = positive(maxBackups, 10);
        renderBudget = positive(renderBudget, 256);
    }

    public synchronized void save() {
        if (root == null || gson == null) {
            return;
        }
        Path file = file();
        try {
            Files.createDirectories(root);
            Path tmp = file.resolveSibling(file.getFileName() + ".tmp");
            Files.writeString(tmp, gson.toJson(this), StandardCharsets.UTF_8);
            try {
                Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            MinecraftGit.LOGGER.warn("Could not save config at {}: {}", file, e.getMessage());
        }
    }

    private static String nonBlank(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private static int positive(int value, int fallback) {
        return value > 0 ? value : fallback;
    }

    private static long positive(long value, long fallback) {
        return value > 0 ? value : fallback;
    }
}
