package org.almond.minecraftgit.client.world;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.almond.minecraftgit.backup.BackupService;
import org.almond.minecraftgit.client.WorldContext;
import org.almond.minecraftgit.config.ModConfig;
import org.almond.minecraftgit.debug.DebugLogger;
import org.almond.minecraftgit.model.BlockPos;
import org.almond.minecraftgit.model.BlockSnapshot;
import org.almond.minecraftgit.model.EntitySnapshot;
import org.almond.minecraftgit.model.Snapshot;
import org.almond.minecraftgit.serialization.BlockEntityCodec;
import org.almond.minecraftgit.serialization.BlockStateCodec;
import org.almond.minecraftgit.serialization.EntityCodec;
import org.almond.minecraftgit.world.ApplyException;
import org.almond.minecraftgit.world.ApplyPlan;
import org.almond.minecraftgit.world.ApplyResult;
import org.almond.minecraftgit.world.SnapshotService;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * Applies an {@link ApplyPlan} through the integrated server in tick batches.
 * This is deliberately single-player only: in multiplayer the server is
 * authoritative and a client-only mod cannot safely write blocks or entities.
 */
public final class ApplyService {

    private static final int MAX_WARNINGS = 20;

    private final SnapshotService snapshots;
    private final BackupService backups;
    private final ModConfig config;
    private final DebugLogger debug;
    private final ExecutorService io;

    public ApplyService(SnapshotService snapshots, BackupService backups, ModConfig config,
                        DebugLogger debug, ExecutorService io) {
        this.snapshots = snapshots;
        this.backups = backups;
        this.config = config;
        this.debug = debug;
        this.io = io;
    }

    public CompletableFuture<ApplyResult> apply(WorldContext world, ApplyPlan plan) {
        Minecraft client = Minecraft.getInstance();
        MinecraftServer server = client.getSingleplayerServer();
        ServerLevel level = world.serverLevel(client);
        if (server == null || level == null) {
            return CompletableFuture.failedFuture(new ApplyException(
                    "Applying a build requires a single-player world (integrated server). "
                            + "Repository commands still work in multiplayer."));
        }
        CompletableFuture<ApplyResult> future = new CompletableFuture<>();
        server.execute(() -> begin(server, level, world, plan, future));
        return future;
    }

    private void begin(MinecraftServer server, ServerLevel level, WorldContext world, ApplyPlan plan,
                       CompletableFuture<ApplyResult> future) {
        if (!server.isRunning()) {
            future.completeExceptionally(new ApplyException("The integrated server is stopping."));
            return;
        }
        if (!plan.createBackup()) {
            beginWrites(server, level, plan, null, future);
            return;
        }
        Snapshot backup;
        try {
            backup = snapshots.capturePositions(level, plan.selectionName(), plan.targetOrigin(),
                    plan.allPositions(), true);
        } catch (RuntimeException e) {
            future.completeExceptionally(new ApplyException("Could not capture a pre-apply backup: "
                    + e.getMessage(), e));
            return;
        }
        CompletableFuture.supplyAsync(
                        () -> backups.store(world.id(), plan.owner(), plan.selectionName(), backup), io)
                .whenComplete((path, error) -> server.execute(() -> {
                    if (error != null) {
                        Throwable cause = error.getCause() == null ? error : error.getCause();
                        future.completeExceptionally(new ApplyException("Could not write a pre-apply backup: "
                                + cause.getMessage(), cause));
                        return;
                    }
                    beginWrites(server, level, plan, path, future);
                }));
    }

    private void beginWrites(MinecraftServer server, ServerLevel level, ApplyPlan plan, Path backup,
                             CompletableFuture<ApplyResult> future) {
        List<BlockWrite> writes = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        int skipped = 0;
        for (BlockPos position : plan.removals()) {
            if (outOfRange(level, position)) {
                skipped++;
                continue;
            }
            writes.add(new BlockWrite(position, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(),
                    null, true));
        }
        for (var entry : plan.placements().entrySet()) {
            BlockPos position = entry.getKey();
            if (outOfRange(level, position)) {
                skipped++;
                warn(warnings, "Skipped out-of-range coordinate " + position + " ("
                        + level.getMinBuildHeight() + ".." + (level.getMaxBuildHeight() - 1) + ").");
                continue;
            }
            BlockSnapshot block = entry.getValue();
            if (!BlockStateCodec.isKnown(block.blockData())) {
                warn(warnings, "Unknown block state '" + block.blockData() + "' at " + position
                        + " was applied as air.");
            }
            BlockState state = BlockStateCodec.decode(block.blockData());
            writes.add(new BlockWrite(position, state, block.blockEntity(), false));
        }
        writes.sort(Comparator.comparingInt((BlockWrite write) -> write.position().y())
                .thenComparingInt(write -> write.position().x())
                .thenComparingInt(write -> write.position().z()));

        new Job(server, level, plan, writes, backup, warnings, skipped, future).process();
    }

    private final class Job {
        private final MinecraftServer server;
        private final ServerLevel level;
        private final ApplyPlan plan;
        private final List<BlockWrite> writes;
        private final Path backup;
        private final List<String> warnings;
        private final int skipped;
        private final CompletableFuture<ApplyResult> future;
        private int index;
        private int placed;
        private int removed;
        private int failed;

        private Job(MinecraftServer server, ServerLevel level, ApplyPlan plan, List<BlockWrite> writes,
                    Path backup, List<String> warnings, int skipped, CompletableFuture<ApplyResult> future) {
            this.server = server;
            this.level = level;
            this.plan = plan;
            this.writes = writes;
            this.backup = backup;
            this.warnings = warnings;
            this.skipped = skipped;
            this.future = future;
        }

        private void process() {
            if (!server.isRunning()) {
                future.completeExceptionally(new ApplyException("The integrated server stopped during apply."));
                return;
            }
            int processed = 0;
            while (index < writes.size() && processed < config.blocksPerTick) {
                BlockWrite write = writes.get(index++);
                try {
                    var mcPos = new net.minecraft.core.BlockPos(write.position().x(),
                            write.position().y(), write.position().z());
                    boolean alreadyCurrent = level.getBlockState(mcPos).equals(write.state());
                    boolean changed = alreadyCurrent
                            || level.setBlock(mcPos, write.state(), Block.UPDATE_CLIENTS);
                    if (!changed) {
                        failed++;
                        warn(warnings, "Could not write " + write.position() + ".");
                    } else {
                        if (!write.removal() && write.blockEntity() != null) {
                            BlockEntity blockEntity = level.getBlockEntity(mcPos);
                            if (blockEntity == null) {
                                warn(warnings, "Block at " + write.position()
                                        + " has no block entity to receive its NBT payload.");
                            } else if (!BlockEntityCodec.apply(blockEntity, write.blockEntity(),
                                    level.registryAccess())) {
                                warn(warnings, "Skipped an unsupported block-entity payload at "
                                        + write.position() + ".");
                            }
                        }
                        debug.blockWrite(plan.selectionName(), write.position().x(), write.position().y(),
                                write.position().z(), write.removal() ? "minecraft:air"
                                        : write.state().toString());
                        if (write.removal()) {
                            removed++;
                        } else {
                            placed++;
                        }
                    }
                } catch (RuntimeException e) {
                    failed++;
                    warn(warnings, "Could not write " + write.position() + ": " + e.getMessage());
                }
                processed++;
            }
            if (index < writes.size()) {
                server.execute(this::process);
                return;
            }
            finish();
        }

        private void finish() {
            int entitiesRemoved = removeEntities();
            int entitiesPlaced = placeEntities();
            future.complete(new ApplyResult(placed, removed, skipped + failed,
                    entitiesPlaced, entitiesRemoved, backup, warnings));
        }

        private int removeEntities() {
            if (plan.entityRemovals().isEmpty() || plan.oldEntityOrigin() == null) {
                return 0;
            }
            int count = 0;
            for (EntitySnapshot snapshot : plan.entityRemovals()) {
                double x = plan.oldEntityOrigin().x() + snapshot.rx();
                double y = plan.oldEntityOrigin().y() + snapshot.ry();
                double z = plan.oldEntityOrigin().z() + snapshot.rz();
                AABB box = new AABB(x - 1.5, y - 1.5, z - 1.5, x + 1.5, y + 1.5, z + 1.5);
                for (Entity entity : level.getEntitiesOfClass(Entity.class, box,
                        candidate -> !(candidate instanceof Player)
                                && EntityType.getKey(candidate.getType()).toString().equals(snapshot.type()))) {
                    if (entity.distanceToSqr(x, y, z) > 9.0) {
                        continue;
                    }
                    entity.discard();
                    count++;
                }
            }
            return count;
        }

        private int placeEntities() {
            int count = 0;
            for (ApplyPlan.EntityPlacement placement : plan.entityPlacements()) {
                try {
                    Entity entity = EntityCodec.apply(level, placement.snapshot(),
                            placement.x(), placement.y(), placement.z());
                    if (entity != null) {
                        count++;
                    }
                } catch (RuntimeException e) {
                    warn(warnings, "Could not restore entity " + placement.snapshot().type()
                            + ": " + e.getMessage());
                }
            }
            return count;
        }
    }

    private static boolean outOfRange(ServerLevel level, BlockPos position) {
        return position.y() < level.getMinBuildHeight() || position.y() >= level.getMaxBuildHeight();
    }

    private static void warn(List<String> warnings, String warning) {
        if (warnings.size() < MAX_WARNINGS) {
            warnings.add(warning);
        }
    }

    private record BlockWrite(BlockPos position, BlockState state, String blockEntity, boolean removal) {
    }
}
