package org.almond.minecraftgit.model;

import java.util.Comparator;
import java.util.List;

/**
 * Inclusive cuboid region. Corners are normalized on construction so callers
 * never need to know which corner the player clicked first.
 *
 * <p>The ordering key is the smallest absolute corner, lexicographically
 * (x, then y, then z). The cuboid list is re-sorted with this key on every
 * add/remove (plan edge case E1).</p>
 */
public record Cuboid(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {

    public static final Comparator<Cuboid> SMALLEST_CORNER_ORDER =
            Comparator.comparingInt(Cuboid::minX)
                    .thenComparingInt(Cuboid::minY)
                    .thenComparingInt(Cuboid::minZ)
                    .thenComparingInt(Cuboid::maxX)
                    .thenComparingInt(Cuboid::maxY)
                    .thenComparingInt(Cuboid::maxZ);

    public Cuboid {
        int x1 = Math.min(minX, maxX);
        int x2 = Math.max(minX, maxX);
        int y1 = Math.min(minY, maxY);
        int y2 = Math.max(minY, maxY);
        int z1 = Math.min(minZ, maxZ);
        int z2 = Math.max(minZ, maxZ);
        minX = x1;
        minY = y1;
        minZ = z1;
        maxX = x2;
        maxY = y2;
        maxZ = z2;
    }

    public static Cuboid of(BlockPos a, BlockPos b) {
        return new Cuboid(a.x(), a.y(), a.z(), b.x(), b.y(), b.z());
    }

    public static Cuboid of(int x1, int y1, int z1, int x2, int y2, int z2) {
        return new Cuboid(x1, y1, z1, x2, y2, z2);
    }

    public BlockPos smallestCorner() {
        return new BlockPos(minX, minY, minZ);
    }

    public BlockPos largestCorner() {
        return new BlockPos(maxX, maxY, maxZ);
    }

    public int sizeX() {
        return maxX - minX + 1;
    }

    public int sizeY() {
        return maxY - minY + 1;
    }

    public int sizeZ() {
        return maxZ - minZ + 1;
    }

    /** Volume, saturated at {@link Long#MAX_VALUE} instead of overflowing. */
    public long volume() {
        long x = (long) maxX - minX + 1L;
        long y = (long) maxY - minY + 1L;
        long z = (long) maxZ - minZ + 1L;
        if (x > 0 && y > Long.MAX_VALUE / x) {
            return Long.MAX_VALUE;
        }
        long xy = x * y;
        if (z > 0 && xy > Long.MAX_VALUE / z) {
            return Long.MAX_VALUE;
        }
        return xy * z;
    }

    public boolean contains(BlockPos pos) {
        return contains(pos.x(), pos.y(), pos.z());
    }

    public boolean contains(int x, int y, int z) {
        return x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ;
    }

    public boolean intersects(Cuboid other) {
        return maxX >= other.minX && minX <= other.maxX
                && maxY >= other.minY && minY <= other.maxY
                && maxZ >= other.minZ && minZ <= other.maxZ;
    }

    public Cuboid union(Cuboid other) {
        return new Cuboid(Math.min(minX, other.minX), Math.min(minY, other.minY), Math.min(minZ, other.minZ),
                Math.max(maxX, other.maxX), Math.max(maxY, other.maxY), Math.max(maxZ, other.maxZ));
    }

    public Cuboid expand(int amount) {
        return new Cuboid(minX - amount, minY - amount, minZ - amount,
                maxX + amount, maxY + amount, maxZ + amount);
    }

    public List<BlockPos> corners() {
        return List.of(
                new BlockPos(minX, minY, minZ),
                new BlockPos(minX, minY, maxZ),
                new BlockPos(minX, maxY, minZ),
                new BlockPos(minX, maxY, maxZ),
                new BlockPos(maxX, minY, minZ),
                new BlockPos(maxX, minY, maxZ),
                new BlockPos(maxX, maxY, minZ),
                new BlockPos(maxX, maxY, maxZ));
    }

    public String dimensions() {
        return sizeX() + "x" + sizeY() + "x" + sizeZ();
    }

    @Override
    public String toString() {
        return "[" + minX + ".." + maxX + ", " + minY + ".." + maxY + ", " + minZ + ".." + maxZ + "]";
    }
}
