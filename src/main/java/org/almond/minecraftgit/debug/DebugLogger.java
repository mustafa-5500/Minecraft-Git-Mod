package org.almond.minecraftgit.debug;

import org.almond.minecraftgit.MinecraftGit;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Optional, per-session debug log. Block-level logging is opt-in because a
 * single large capture would otherwise write millions of lines.
 */
public final class DebugLogger implements AutoCloseable {

    private static final DateTimeFormatter STAMP =
            DateTimeFormatter.ofPattern("HH:mm:ss.SSS", Locale.ROOT).withZone(ZoneId.systemDefault());

    private final Path logsRoot;
    private final Path file;
    private volatile boolean enabled;
    private volatile boolean blocks;
    private BufferedWriter writer;
    private int buffered;
    private boolean failed;

    public DebugLogger(Path logsRoot, boolean enabled, boolean blocks) {
        this.logsRoot = logsRoot;
        this.enabled = enabled;
        this.blocks = blocks;
        this.file = logsRoot.resolve("debug-" + System.currentTimeMillis() + ".log");
    }

    public Path file() {
        return file;
    }

    public boolean enabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setBlocks(boolean blocks) {
        this.blocks = blocks;
    }

    public synchronized void log(String category, String format, Object... args) {
        if (!enabled) {
            return;
        }
        try {
            if (writer == null) {
                Files.createDirectories(logsRoot);
                writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                writer.write("# MinecraftGit debug log " + Instant.now());
                writer.newLine();
            }
            String message = args.length == 0 ? format : String.format(Locale.ROOT, format, args);
            writer.write(STAMP.format(Instant.now()));
            writer.write(" [");
            writer.write(category);
            writer.write("] ");
            writer.write(message);
            writer.newLine();
            if (++buffered >= 32) {
                writer.flush();
                buffered = 0;
            }
        } catch (IOException e) {
            if (!failed) {
                failed = true;
                MinecraftGit.LOGGER.warn("Could not write debug log {}: {}", file, e.getMessage());
            }
        }
    }

    public void blockRead(String world, int x, int y, int z, String state) {
        if (blocks) {
            log("block-read", "%s %d %d %d %s", world, x, y, z, state);
        }
    }

    public void blockWrite(String world, int x, int y, int z, String state) {
        if (blocks) {
            log("block-write", "%s %d %d %d %s", world, x, y, z, state);
        }
    }

    @Override
    public synchronized void close() {
        if (writer != null) {
            try {
                writer.flush();
                writer.close();
            } catch (IOException ignored) {
                // Best effort on shutdown.
            }
            writer = null;
        }
    }
}
