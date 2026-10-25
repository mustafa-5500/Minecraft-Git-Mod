package org.almond.minecraftgit.model;

import java.util.Comparator;

/**
 * One captured entity. {@code data} is the server's serialized entity string
 * (Paper's {@code Entity#getAsString()}); the position is kept separately so the
 * same build can be placed at a different origin.
 */
public record EntitySnapshot(double x, double y, double z,
                             double rx, double ry, double rz,
                             float yaw, float pitch,
                             String type, String data) {

    public static final Comparator<EntitySnapshot> ORDER =
            Comparator.comparingDouble(EntitySnapshot::y)
                    .thenComparingDouble(EntitySnapshot::x)
                    .thenComparingDouble(EntitySnapshot::z);

    public EntitySnapshot {
        if (type == null || type.isBlank()) {
            type = "unknown";
        }
        if (data == null) {
            data = "";
        }
    }

    public BlockPos relativeBlock() {
        return new BlockPos((int) Math.floor(rx), (int) Math.floor(ry), (int) Math.floor(rz));
    }
}
