package org.almond.minecraftgit.model;

import java.util.Comparator;

/**
 * One captured block. Both coordinate systems are kept: Git stores the relative
 * coordinates, while apply/debug tooling needs the absolute world position.
 */
public record BlockSnapshot(int x, int y, int z, int rx, int ry, int rz,
                            String blockData, String blockEntity) {

    public static final Comparator<BlockSnapshot> ORDER =
            Comparator.comparingInt(BlockSnapshot::y)
                    .thenComparingInt(BlockSnapshot::x)
                    .thenComparingInt(BlockSnapshot::z);

    public BlockSnapshot {
        if (blockData == null || blockData.isBlank()) {
            blockData = "minecraft:air";
        }
        if (blockEntity != null && blockEntity.isBlank()) {
            blockEntity = null;
        }
    }

    public static BlockSnapshot at(BlockPos absolute, BlockPos origin, String blockData, String blockEntity) {
        BlockPos relative = absolute.subtract(origin);
        return new BlockSnapshot(absolute.x(), absolute.y(), absolute.z(),
                relative.x(), relative.y(), relative.z(), blockData, blockEntity);
    }

    public BlockPos absolute() {
        return new BlockPos(x, y, z);
    }

    public BlockPos relative() {
        return new BlockPos(rx, ry, rz);
    }

    public boolean hasBlockEntity() {
        return blockEntity != null;
    }

    public boolean isAir() {
        return blockData.startsWith("minecraft:air") || blockData.equals("air");
    }
}
