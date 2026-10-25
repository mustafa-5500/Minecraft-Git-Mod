package org.almond.minecraftgit.client.selection;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import org.almond.minecraftgit.client.GitServices;
import org.almond.minecraftgit.client.WorldContext;
import org.almond.minecraftgit.model.Cuboid;
import org.almond.minecraftgit.model.Selection;

import java.util.ArrayList;
import java.util.List;

/**
 * Budgeted particle outline for the active selection, a pending cuboid, and a
 * temporary {@code /git selection show} highlight. Particles are local to this
 * client and never affect the server.
 */
public final class SelectionRenderer {

    private static int tickCounter;
    private static String highlightWorld;
    private static String highlightSelection;
    private static long highlightUntil;

    private SelectionRenderer() {
    }

    public static void register(GitServices services) {
        ClientTickEvents.END_CLIENT_TICK.register(client -> tick(services, client));
    }

    public static void highlight(String worldId, String selection, int seconds) {
        highlightWorld = worldId;
        highlightSelection = selection;
        highlightUntil = System.currentTimeMillis() + seconds * 1000L;
    }

    private static void tick(GitServices services, Minecraft client) {
        if (!services.config().renderSelections || client.level == null || client.player == null) {
            return;
        }
        if (tickCounter++ % 10 != 0) {
            return;
        }
        WorldContext world;
        try {
            world = WorldContext.current(client);
        } catch (RuntimeException e) {
            return;
        }

        List<Cuboid> cuboids = new ArrayList<>();
        String active = services.selectionManager().active(world, client.player);
        if (active != null) {
            services.selections().find(world.id(), client.player.getUUID(), active)
                    .ifPresent(selection -> cuboids.addAll(selection.cuboids()));
        }
        Cuboid pending = services.selectionManager().peekPending(client.player);
        if (pending != null) {
            cuboids.add(pending);
        }
        if (highlightSelection != null && highlightWorld != null
                && highlightWorld.equals(world.id()) && System.currentTimeMillis() < highlightUntil) {
            services.selections().find(world.id(), client.player.getUUID(), highlightSelection)
                    .ifPresent(selection -> cuboids.addAll(selection.cuboids()));
        }
        if (cuboids.isEmpty()) {
            return;
        }

        int budget = services.config().renderBudget;
        int[] used = {0};
        for (Cuboid cuboid : cuboids) {
            drawCuboid(client.level, cuboid, used, budget);
            if (used[0] >= budget) {
                break;
            }
        }
    }

    private static void drawCuboid(ClientLevel level, Cuboid cuboid, int[] used, int budget) {
        int step = 2;
        for (int x = cuboid.minX(); x <= cuboid.maxX(); x += step) {
            draw(level, x, cuboid.minY(), cuboid.minZ(), used, budget);
            draw(level, x, cuboid.minY(), cuboid.maxZ(), used, budget);
            draw(level, x, cuboid.maxY(), cuboid.minZ(), used, budget);
            draw(level, x, cuboid.maxY(), cuboid.maxZ(), used, budget);
        }
        for (int z = cuboid.minZ(); z <= cuboid.maxZ(); z += step) {
            draw(level, cuboid.minX(), cuboid.minY(), z, used, budget);
            draw(level, cuboid.maxX(), cuboid.minY(), z, used, budget);
            draw(level, cuboid.minX(), cuboid.maxY(), z, used, budget);
            draw(level, cuboid.maxX(), cuboid.maxY(), z, used, budget);
        }
        for (int y = cuboid.minY(); y <= cuboid.maxY(); y += step) {
            draw(level, cuboid.minX(), y, cuboid.minZ(), used, budget);
            draw(level, cuboid.maxX(), y, cuboid.minZ(), used, budget);
            draw(level, cuboid.minX(), y, cuboid.maxZ(), used, budget);
            draw(level, cuboid.maxX(), y, cuboid.maxZ(), used, budget);
        }
    }

    private static void draw(ClientLevel level, int x, int y, int z, int[] used, int budget) {
        if (used[0] >= budget) {
            return;
        }
        level.addParticle(ParticleTypes.END_ROD, x + 0.5, y + 0.5, z + 0.5, 0.0, 0.0, 0.0);
        used[0]++;
    }
}
