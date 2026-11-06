package org.almond.minecraftgit.world;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import org.almond.minecraftgit.config.ModConfig;
import org.almond.minecraftgit.debug.DebugLogger;
import org.almond.minecraftgit.model.BlockPos;
import org.almond.minecraftgit.model.BlockSnapshot;
import org.almond.minecraftgit.model.Cuboid;
import org.almond.minecraftgit.model.EntitySnapshot;
import org.almond.minecraftgit.model.Selection;
import org.almond.minecraftgit.model.Snapshot;
import org.almond.minecraftgit.selection.SelectionException;
import org.almond.minecraftgit.serialization.BlockEntityCodec;
import org.almond.minecraftgit.serialization.BlockStateCodec;
import org.almond.minecraftgit.serialization.EntityCodec;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

/**
 * Reads blocks, block entities and entities from a {@link Level}. The caller
 * must run this on the owning thread: the client thread for a
 * {@code ClientLevel}, or the server thread for an integrated-server level.
 *
 * <p>Unloaded chunks are skipped and counted. When {@code loadChunks} is true
 * and the level is a {@link ServerLevel}, chunks are loaded on demand (used by
 * single-player staging/status/backups so a commit does not silently truncate).</p>
 */
public final class SnapshotService {

    private static final int MAX_WARNINGS = 20;

    private final ModConfig config;
    private final DebugLogger debug;

    /**
     * {@link SnapshotService} Constructor
     * @param config
     * @param debug
     */
    public SnapshotService(ModConfig config, DebugLogger debug) {
        this.config = config;
        this.debug = debug;
    }

    /**
     * {@link CaptureResult} of the blocks at a y-level range for a selection.
     * @param selection
     * @param level
     * @return 
     */
    public CaptureResult capture(Selection selection, Level level) {
        return capture(selection, level, false);
    }

    /**
     * Captures The Block & Entity data within a world level of a selection, stored in
     * a {@code {position,data}} map.
     * @param selection
     * @param level
     * @param loadChunks
     * @return result - {@code List<EntitySnapshots>} In the given selection
     */
    public CaptureResult capture(Selection selection, Level level, boolean loadChunks) {
        // No Region Exceptions
        if (!selection.hasOrigin()) { 
            throw new SelectionException("Selection '" + selection.name()
                    + "' has no origin yet; add a cuboid region first.");
        }
        if (selection.cuboidCount() == 0) {
            throw new SelectionException("Selection '" + selection.name() + "' has no cuboid regions.");
        }
        // Size Exceeded Exception
        long volume = selection.volume();
        if (volume > config.maxBlocksPerCommit) {
            throw new SelectionException("Selection volume " + volume
                    + " exceeds the configured capture limit of " + config.maxBlocksPerCommit + " blocks.");
        }

        BlockPos origin = selection.origin();
        Map<BlockPos, BlockSnapshot> blocks = new LinkedHashMap<>();
        Set<Long> unloadedChunks = new HashSet<>();
        Set<Long> loadedChunks = new HashSet<>();
        List<String> warnings = new ArrayList<>();
        long skippedBlocks = 0;
        long blockEntityBytes = 0;
        int maxEntityBytes = config.maxBlockEntityBytes;

        // O(n*x*y*z)
        for (Cuboid cuboid : selection.cuboids()) {
            for (int y = cuboid.minY(); y <= cuboid.maxY(); y++) {
                for (int x = cuboid.minX(); x <= cuboid.maxX(); x++) {
                    for (int z = cuboid.minZ(); z <= cuboid.maxZ(); z++) {

                        // Only check for blocks within the y-level range
                        if (y < level.getMinBuildHeight() || y >= level.getMaxBuildHeight()) {
                            skippedBlocks++;
                            continue;
                        }

                        // Only check Loaded Chunks, and/or load unloaded chunks if the given boolean is true.
                        if (!ensureLoaded(level, x, z, loadChunks, loadedChunks)) {
                            unloadedChunks.add(chunkKey(x >> 4, z >> 4));
                            skippedBlocks++;
                            continue;
                        }

                        // If Block position is already captured, continue
                        BlockPos absolute = new BlockPos(x, y, z);
                        if (blocks.containsKey(absolute)) {
                            continue;
                        }

                        var mcPos = new net.minecraft.core.BlockPos(x, y, z);
                        String blockData = BlockStateCodec.encode(level.getBlockState(mcPos));
                        debug.blockRead(selection.world(), x, y, z, blockData);

                        String blockEntity = null;
                        BlockEntity entity = level.getBlockEntity(mcPos);
                        if (entity != null) {
                            try {
                                String payload = BlockEntityCodec.encode(entity, level.registryAccess());
                                if (payload != null) {
                                    int bytes = BlockEntityCodec.payloadBytes(payload);
                                    // If Entity Data overflows the byte limit, do not store it
                                    if (bytes > maxEntityBytes) {
                                        warn(warnings, "Block-entity data at " + absolute + " is " + bytes
                                                + " bytes (limit " + maxEntityBytes + "); stored without Entity Data.");
                                    } else {
                                        blockEntity = payload;
                                        blockEntityBytes += bytes;
                                    }
                                }
                            } catch (RuntimeException e) {
                                warn(warnings, "Could not read block entity at " + absolute + ": "
                                        + e.getMessage());
                            }
                        }
                        // Update blocks map with the block and entity data at the position
                        blocks.put(absolute, BlockSnapshot.at(absolute, origin, blockData, blockEntity));
                    }
                }
            }
        }

        // Capture a List of Entity Snap shots
        List<EntitySnapshot> entities = captureEntities(level, selection, origin, warnings);
        int capturedEntities = entities.size();
        int skippedEntities = 0;

        // Skip Entities after the capture cap.
        if (capturedEntities > config.maxEntitiesPerSelection) {
            entities = new ArrayList<>(entities.subList(0, config.maxEntitiesPerSelection));
            skippedEntities = capturedEntities - config.maxEntitiesPerSelection;
            warn(warnings, "Entity capture was capped at " + config.maxEntitiesPerSelection + ".");
        }
        if (!unloadedChunks.isEmpty()) {
            warnings.add("Skipped " + unloadedChunks.size()
                    + " unloaded chunk(s); load them and re-capture to include them.");
        }

        Snapshot snapshot = new Snapshot(selection.name(), origin, new ArrayList<>(blocks.values()), entities,
                Instant.now());
        debug.log("capture", "Captured '%s': %d blocks, %d entities, %d skipped blocks, %d chunks, %d block-entity bytes",
                selection.name(), snapshot.blockCount(), snapshot.entityCount(), skippedBlocks,
                unloadedChunks.size(), blockEntityBytes);
        return new CaptureResult(snapshot, unloadedChunks.size(), skippedBlocks, skippedEntities, warnings);
    }

    /**
     * Capture exactly the given positions, for a pre-apply backup. Entities are
     * not captured here; apply restores blocks and block entities.
     */
    public Snapshot capturePositions(Level level, String selectionName, BlockPos origin,
                                     Collection<BlockPos> positions, boolean loadChunks) {
        Map<BlockPos, BlockSnapshot> blocks = new LinkedHashMap<>();
        Set<Long> loadedChunks = new HashSet<>();
        for (BlockPos position : new TreeSet<>(positions)) {
            if (position.y() < level.getMinBuildHeight() || position.y() >= level.getMaxBuildHeight()) {
                continue;
            }
            if (!ensureLoaded(level, position.x(), position.z(), loadChunks, loadedChunks)) {
                continue;
            }
            var mcPos = new net.minecraft.core.BlockPos(position.x(), position.y(), position.z());
            String blockData = BlockStateCodec.encode(level.getBlockState(mcPos));
            String blockEntity = null;
            BlockEntity entity = level.getBlockEntity(mcPos);
            if (entity != null) {
                try {
                    String payload = BlockEntityCodec.encode(entity, level.registryAccess());
                    if (payload != null && BlockEntityCodec.payloadBytes(payload) <= config.maxBlockEntityBytes) {
                        blockEntity = payload;
                    }
                } catch (RuntimeException ignored) {
                    // A backup is best-effort for a single broken tile entity.
                }
            }
            debug.blockRead(selectionName, position.x(), position.y(), position.z(), blockData);
            blocks.put(position, BlockSnapshot.at(position, origin, blockData, blockEntity));
        }
        return new Snapshot(selectionName, origin, new ArrayList<>(blocks.values()), List.of(), Instant.now());
    }

    /**
     * 
     * @param level
     * @param selection
     * @param origin
     * @param warnings
     * @return
     */
    private List<EntitySnapshot> captureEntities(Level level, Selection selection, BlockPos origin,
                                                 List<String> warnings) {
        List<EntitySnapshot> result = new ArrayList<>();
        Set<UUID> seen = new HashSet<>();
        int maxBytes = config.maxBlockEntityBytes;

        //O(n*m)
        for (Cuboid cuboid : selection.cuboids()) {
            
            // Convert Cuboid Block selections to Continuous Axis Aligned Bounding Boxes.
            AABB box = new AABB(cuboid.minX(), cuboid.minY(), cuboid.minZ(),
                    cuboid.maxX() + 1.0, cuboid.maxY() + 1.0, cuboid.maxZ() + 1.0);
            
            // Check All non-player entities
            for (Entity entity : level.getEntitiesOfClass(Entity.class, box,
                    candidate -> !(candidate instanceof Player))) {
                
                // Continue if entity is already seen or is flagged for removal
                if (entity.isRemoved() || !seen.add(entity.getUUID())) {
                    continue;
                }
                
                // The entity is not within the cuboid box
                if (!entity.getBoundingBox().intersects(box)) {
                    continue;
                }

                try {
                    String payload = EntityCodec.encode(entity);
                    int bytes = EntityCodec.payloadBytes(payload);

                    // Byte Limite Exception
                    if (bytes > maxBytes) {
                        warn(warnings, "Entity " + EntityType.getKey(entity.getType()) + " at "
                                + entity.blockPosition() + " exceeded the entity payload limit and was skipped.");
                        continue;
                    }

                    double rx = entity.getX() - origin.x();
                    double ry = entity.getY() - origin.y();
                    double rz = entity.getZ() - origin.z();
                    result.add(new EntitySnapshot(entity.getX(), entity.getY(), entity.getZ(), rx, ry, rz,
                            entity.getYRot(), entity.getXRot(),
                            EntityType.getKey(entity.getType()).toString(), payload));
                    debug.log("capture", "Entity %s at %.2f %.2f %.2f", EntityType.getKey(entity.getType()),
                            entity.getX(), entity.getY(), entity.getZ());
                } catch (RuntimeException e) {
                    warn(warnings, "Could not serialize entity " + EntityType.getKey(entity.getType()) + ": "
                            + e.getMessage());
                }
            }
        }
        result.sort(EntitySnapshot.ORDER);
        return result;
    }

    /**
     * Checks and/or loads chunks at a given block position.
     * @param level
     * @param x
     * @param z
     * @param loadChunks
     * @param loadedChunks
     * @return
     */
    private static boolean ensureLoaded(Level level, int x, int z, boolean loadChunks, Set<Long> loadedChunks) {
        // Loads Chunk through the getChunk(), whether it was currently loaded or not.
        if (loadChunks && level instanceof ServerLevel serverLevel) {
            long key = chunkKey(x >> 4, z >> 4);
            if (loadedChunks.add(key)) {
                serverLevel.getChunk(x >> 4, z >> 4);
            }
        }
        return level.isLoaded(new net.minecraft.core.BlockPos(x, level.getMinBuildHeight(), z));
    }

    private static long chunkKey(int chunkX, int chunkZ) {
        return ((long) chunkX << 32) ^ (chunkZ & 0xFFFFFFFFL);
    }

    private static void warn(List<String> warnings, String warning) {
        if (warnings.size() < MAX_WARNINGS) {
            warnings.add(warning);
        }
    }

}
