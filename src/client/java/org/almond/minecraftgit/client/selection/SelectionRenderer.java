package org.almond.minecraftgit.client.selection;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.BlockHitResult;
import org.almond.minecraftgit.client.GitServices;
import org.almond.minecraftgit.client.WorldContext;
import org.almond.minecraftgit.model.BlockPos;
import org.almond.minecraftgit.model.Cuboid;
import org.almond.minecraftgit.model.Selection;

import java.util.ArrayList;
import java.util.List;

/**
 * Budgeted particle outline for the box currently being clicked, the active
 * selection, a pending cuboid, and a temporary {@code /git selection show}
 * highlight. Particles are local to this client and never affect the server.
 */
public final class SelectionRenderer {

    /** Blocks between particles along an edge. */
    private static final double PARTICLE_SPACING = 2.0;
    /** Lift the outline just outside the selected blocks so they do not hide it. */
    private static final double EDGE_OFFSET = 0.02;

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

        // The box currently being clicked is the most immediate feedback, so it
        // is added first and cannot be starved by a large active selection.
        BlockPos anchor = services.selectionManager().inProgressCorner(client.player);
        if (anchor != null) {
            BlockPos target = aimedBlock(client);
            cuboids.add(Cuboid.of(anchor, target != null ? target : anchor));
        }

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
        // Draw on the outside faces of the outermost blocks, not through their
        // centres: a centre line sits inside the block and is hidden by it, so
        // a selection on solid terrain would show no particles at all.
        double x0 = cuboid.minX() - EDGE_OFFSET;
        double x1 = cuboid.maxX() + 1 + EDGE_OFFSET;
        double y0 = cuboid.minY() - EDGE_OFFSET;
        double y1 = cuboid.maxY() + 1 + EDGE_OFFSET;
        double z0 = cuboid.minZ() - EDGE_OFFSET;
        double z1 = cuboid.maxZ() + 1 + EDGE_OFFSET;

        // Bottom face.
        drawEdge(level, x0, y0, z0, x1, y0, z0, used, budget);
        drawEdge(level, x0, y0, z1, x1, y0, z1, used, budget);
        drawEdge(level, x0, y0, z0, x0, y0, z1, used, budget);
        drawEdge(level, x1, y0, z0, x1, y0, z1, used, budget);
        // Top face.
        drawEdge(level, x0, y1, z0, x1, y1, z0, used, budget);
        drawEdge(level, x0, y1, z1, x1, y1, z1, used, budget);
        drawEdge(level, x0, y1, z0, x0, y1, z1, used, budget);
        drawEdge(level, x1, y1, z0, x1, y1, z1, used, budget);
        // Vertical edges.
        drawEdge(level, x0, y0, z0, x0, y1, z0, used, budget);
        drawEdge(level, x1, y0, z0, x1, y1, z0, used, budget);
        drawEdge(level, x0, y0, z1, x0, y1, z1, used, budget);
        drawEdge(level, x1, y0, z1, x1, y1, z1, used, budget);
    }

    /** Particles every {@link #PARTICLE_SPACING} blocks along an edge, endpoints included. */
    private static void drawEdge(ClientLevel level, double x0, double y0, double z0,
                                 double x1, double y1, double z1, int[] used, int budget) {
        double dx = x1 - x0;
        double dy = y1 - y0;
        double dz = z1 - z0;
        double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
        int segments = Math.max(1, (int) Math.ceil(length / PARTICLE_SPACING));
        for (int i = 0; i <= segments; i++) {
            double t = (double) i / segments;
            draw(level, x0 + dx * t, y0 + dy * t, z0 + dz * t, used, budget);
        }
    }

    /**
     * Block under the crosshair, or {@code null} when the player is not aiming
     * at a block within reach. Without a target the in-progress corner is still
     * drawn as a single-block outline so the first click has visible feedback.
     */
    private static BlockPos aimedBlock(Minecraft client) {
        if (client.hitResult instanceof BlockHitResult hit) {
            var pos = hit.getBlockPos();
            return new BlockPos(pos.getX(), pos.getY(), pos.getZ());
        }
        return null;
    }

    private static void draw(ClientLevel level, double x, double y, double z, int[] used, int budget) {
        if (used[0] >= budget) {
            return;
        }
        level.addParticle(ParticleTypes.END_ROD, x, y, z, 0.0, 0.0, 0.0);
        used[0]++;
    }
}
