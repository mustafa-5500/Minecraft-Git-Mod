package org.almond.minecraftgit.model;

/**
 * An integer world position. All Git-facing coordinates are block positions;
 * relative positions are produced by subtracting a selection origin.
 */
public record BlockPos(int x, int y, int z) implements Comparable<BlockPos> {

    public static final BlockPos ORIGIN = new BlockPos(0, 0, 0);

    public BlockPos add(BlockPos other) {
        return new BlockPos(x + other.x, y + other.y, z + other.z);
    }

    public BlockPos add(int dx, int dy, int dz) {
        return new BlockPos(x + dx, y + dy, z + dz);
    }

    public BlockPos subtract(BlockPos other) {
        return new BlockPos(x - other.x, y - other.y, z - other.z);
    }

    public BlockPos relativeTo(BlockPos origin) {
        return subtract(origin);
    }

    public BlockPos absoluteFrom(BlockPos origin) {
        return add(origin);
    }

    @Override
    public int compareTo(BlockPos other) {
        int cx = Integer.compare(x, other.x);
        if (cx != 0) {
            return cx;
        }
        int cy = Integer.compare(y, other.y);
        return cy != 0 ? cy : Integer.compare(z, other.z);
    }

    @Override
    public String toString() {
        return x + ", " + y + ", " + z;
    }
}
