package org.almond.minecraftgit.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import org.almond.minecraftgit.util.Names;
import org.almond.minecraftgit.util.UserFacingException;

/**
 * Identity of the currently open world/dimension and the bridge to the
 * integrated server when the player is hosting the world.
 *
 * <p>A repo is keyed by {@code <world>__<dimension>} so the Overworld, Nether
 * and End never share coordinates. The constant is local-only; committed
 * metadata records it for diagnostics.</p>
 */
public record WorldContext(String id, String displayName, ResourceKey<Level> dimension, boolean singleplayer) {

    public static WorldContext current(Minecraft client) {
        ClientLevel level = client.level;
        if (level == null) {
            throw new UserFacingException("Open a world before using /git.");
        }
        MinecraftServer server = client.getSingleplayerServer();
        String name;
        if (server != null) {
            name = server.getWorldData().getLevelName();
        } else {
            ServerData data = client.getCurrentServer();
            if (data == null) {
                name = "multiplayer";
            } else {
                String base = data.name == null || data.name.isBlank() ? data.ip : data.name;
                // Include the address so two servers with the same display name do
                // not share a local repository or selection list.
                name = data.ip == null || data.ip.isBlank() || base.contains(data.ip)
                        ? base : base + "_" + data.ip;
            }
        }
        ResourceKey<Level> dimension = level.dimension();
        String dimensionName = dimension.location().getPath();
        String id = Names.safeWorldName(name) + "__" + Names.safeWorldName(dimensionName);
        return new WorldContext(id, name, dimension, server != null);
    }

    /** @return the integrated server level, or {@code null} outside single-player. */
    public ServerLevel serverLevel(Minecraft client) {
        MinecraftServer server = client.getSingleplayerServer();
        if (server == null) {
            return null;
        }
        return server.getLevel(dimension);
    }

    public String gameMode(Minecraft client) {
        if (client.gameMode == null) {
            return "unknown";
        }
        GameType mode = client.gameMode.getPlayerMode();
        return mode == null ? "unknown" : mode.getName();
    }

    /**
     * Current screen title, used only in debug output before a world is open.
     */
    public static String currentScreen(Minecraft client) {
        Screen screen = client.screen;
        return screen == null ? "none" : screen.getClass().getSimpleName();
    }
}
