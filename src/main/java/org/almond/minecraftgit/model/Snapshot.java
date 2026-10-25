package org.almond.minecraftgit.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Immutable capture of one selection at one moment. Blocks and entities carry
 * coordinates relative to {@link #origin()} so the repository representation is
 * portable between worlds.
 */
public final class Snapshot {

    private final String selection;
    private final BlockPos origin;
    private final List<BlockSnapshot> blocks;
    private final List<EntitySnapshot> entities;
    private final Instant capturedAt;

    public Snapshot(String selection, BlockPos origin, List<BlockSnapshot> blocks,
                    List<EntitySnapshot> entities, Instant capturedAt) {
        if (origin == null) {
            throw new IllegalArgumentException("Snapshot origin must not be null.");
        }
        this.selection = selection;
        this.origin = origin;
        this.blocks = new ArrayList<>(blocks == null ? List.of() : blocks);
        this.entities = new ArrayList<>(entities == null ? List.of() : entities);
        this.blocks.removeIf(java.util.Objects::isNull);
        this.entities.removeIf(java.util.Objects::isNull);
        this.blocks.sort(BlockSnapshot.ORDER);
        this.entities.sort(EntitySnapshot.ORDER);
        this.capturedAt = capturedAt == null ? Instant.now() : capturedAt;
    }

    public static Snapshot empty(String selection, BlockPos origin) {
        return new Snapshot(selection, origin, List.of(), List.of(), Instant.now());
    }

    public String selection() {
        return selection;
    }

    public BlockPos origin() {
        return origin;
    }

    public List<BlockSnapshot> blocks() {
        return Collections.unmodifiableList(blocks);
    }

    public List<EntitySnapshot> entities() {
        return Collections.unmodifiableList(entities);
    }

    public Instant capturedAt() {
        return capturedAt;
    }

    public int blockCount() {
        return blocks.size();
    }

    public int entityCount() {
        return entities.size();
    }

    public boolean isEmpty() {
        return blocks.isEmpty() && entities.isEmpty();
    }

    /**
     * Return an equivalent snapshot measured from a new origin. Absolute world
     * positions do not change; only the relative coordinates do.
     */
    public Snapshot rebase(BlockPos newOrigin) {
        if (newOrigin == null) {
            throw new IllegalArgumentException("New origin must not be null.");
        }
        List<BlockSnapshot> rebasedBlocks = new ArrayList<>(blocks.size());
        for (BlockSnapshot block : blocks) {
            BlockPos relative = block.absolute().subtract(newOrigin);
            rebasedBlocks.add(new BlockSnapshot(block.x(), block.y(), block.z(),
                    relative.x(), relative.y(), relative.z(), block.blockData(), block.blockEntity()));
        }
        List<EntitySnapshot> rebasedEntities = new ArrayList<>(entities.size());
        for (EntitySnapshot entity : entities) {
            rebasedEntities.add(new EntitySnapshot(entity.x(), entity.y(), entity.z(),
                    entity.x() - newOrigin.x(), entity.y() - newOrigin.y(), entity.z() - newOrigin.z(),
                    entity.yaw(), entity.pitch(), entity.type(), entity.data()));
        }
        return new Snapshot(selection, newOrigin, rebasedBlocks, rebasedEntities, capturedAt);
    }

    /** Keep only blocks and entities inside the selection's current cuboids. */
    public Snapshot retain(Selection selection) {
        List<BlockSnapshot> kept = new ArrayList<>();
        for (BlockSnapshot block : blocks) {
            if (selection.contains(block.absolute())) {
                kept.add(block);
            }
        }
        List<EntitySnapshot> keptEntities = new ArrayList<>();
        for (EntitySnapshot entity : entities) {
            BlockPos pos = new BlockPos((int) Math.floor(entity.x()), (int) Math.floor(entity.y()),
                    (int) Math.floor(entity.z()));
            if (selection.contains(pos)) {
                keptEntities.add(entity);
            }
        }
        return new Snapshot(selection.name(), selection.origin(), kept, keptEntities, capturedAt);
    }

    public Map<BlockPos, BlockSnapshot> byRelative() {
        Map<BlockPos, BlockSnapshot> map = new LinkedHashMap<>();
        for (BlockSnapshot block : blocks) {
            map.put(block.relative(), block);
        }
        return map;
    }

    public Map<BlockPos, BlockSnapshot> byAbsolute() {
        Map<BlockPos, BlockSnapshot> map = new LinkedHashMap<>();
        for (BlockSnapshot block : blocks) {
            map.put(block.absolute(), block);
        }
        return map;
    }

    public long volume() {
        return blocks.size();
    }

    public String dimensions() {
        if (blocks.isEmpty()) {
            return "0x0x0";
        }
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        int maxZ = Integer.MIN_VALUE;
        for (BlockSnapshot block : blocks) {
            minX = Math.min(minX, block.rx());
            minY = Math.min(minY, block.ry());
            minZ = Math.min(minZ, block.rz());
            maxX = Math.max(maxX, block.rx());
            maxY = Math.max(maxY, block.ry());
            maxZ = Math.max(maxZ, block.rz());
        }
        return (maxX - minX + 1) + "x" + (maxY - minY + 1) + "x" + (maxZ - minZ + 1);
    }

    @Override
    public String toString() {
        return selection + " @ " + origin + " (" + blocks.size() + " blocks, " + entities.size() + " entities)";
    }
}
