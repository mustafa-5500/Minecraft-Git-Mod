package org.almond.minecraftgit.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * A named, multi-cuboid build selection owned by one player in one world.
 *
 * <p>Selections are persisted per world + player in the plugin data folder and
 * are never committed. The origin is the absolute world position that relative
 * repository coordinates are measured from. It is set by the first cuboid and
 * deliberately does <em>not</em> move when that cuboid is removed (E2); only an
 * explicit re-origin or a new selection changes it.</p>
 */
public final class Selection {

    private String name;
    private UUID owner;
    private String ownerName;
    private String world;
    private BlockPos origin;
    private List<Cuboid> cuboids = new ArrayList<>();
    private boolean tracked;
    private String createdAt = Instant.now().toString();
    private String updatedAt = Instant.now().toString();

    public Selection() {
    }

    public Selection(String name, UUID owner, String ownerName, String world, BlockPos origin) {
        this.name = name;
        this.owner = owner;
        this.ownerName = ownerName;
        this.world = world;
        this.origin = origin;
    }

    public Selection normalize(String fallbackWorld, UUID fallbackOwner, String fallbackOwnerName) {
        if (world == null || world.isBlank()) {
            world = fallbackWorld;
        }
        if (owner == null) {
            owner = fallbackOwner;
        }
        if (ownerName == null || ownerName.isBlank()) {
            ownerName = fallbackOwnerName;
        }
        if (cuboids == null) {
            cuboids = new ArrayList<>();
        }
        cuboids.removeIf(java.util.Objects::isNull);
        cuboids.sort(Cuboid.SMALLEST_CORNER_ORDER);
        if (origin == null && !cuboids.isEmpty()) {
            origin = cuboids.get(0).smallestCorner();
        }
        if (createdAt == null || createdAt.isBlank()) {
            createdAt = Instant.now().toString();
        }
        if (updatedAt == null || updatedAt.isBlank()) {
            updatedAt = createdAt;
        }
        return this;
    }

    public String name() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID owner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public String ownerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String world() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public BlockPos origin() {
        return origin;
    }

    public void setOrigin(BlockPos origin) {
        this.origin = origin;
        touch();
    }

    public boolean hasOrigin() {
        return origin != null;
    }

    public List<Cuboid> cuboids() {
        return Collections.unmodifiableList(cuboids);
    }

    public void setCuboids(List<Cuboid> cuboids) {
        this.cuboids = cuboids == null ? new ArrayList<>() : new ArrayList<>(cuboids);
        this.cuboids.sort(Cuboid.SMALLEST_CORNER_ORDER);
        if (origin == null && !this.cuboids.isEmpty()) {
            origin = this.cuboids.get(0).smallestCorner();
        }
        touch();
    }

    /** Append a cuboid, re-sort by smallest corner, and seed the origin. */
    public void addCuboid(Cuboid cuboid) {
        if (cuboid == null) {
            throw new IllegalArgumentException("cuboid must not be null");
        }
        cuboids.add(cuboid);
        cuboids.sort(Cuboid.SMALLEST_CORNER_ORDER);
        if (origin == null) {
            origin = cuboid.smallestCorner();
        }
        touch();
    }

    /** Remove a cuboid by zero-based position in the sorted list. */
    public Cuboid removeCuboidAt(int index) {
        Cuboid removed = cuboids.remove(index);
        cuboids.sort(Cuboid.SMALLEST_CORNER_ORDER);
        // E2: the origin deliberately stays where it is; remaining blocks still
        // reference it. Re-origin is an explicit operation.
        touch();
        return removed;
    }

    /**
     * Rebase the origin onto the smallest remaining cuboid corner (E3).
     *
     * @return the previous origin
     */
    public BlockPos reorigin() {
        if (cuboids.isEmpty()) {
            throw new IllegalStateException("Cannot re-origin an empty selection.");
        }
        BlockPos previous = origin;
        origin = cuboids.get(0).smallestCorner();
        touch();
        return previous;
    }

    public boolean isTracked() {
        return tracked;
    }

    public void setTracked(boolean tracked) {
        this.tracked = tracked;
        touch();
    }

    public String createdAt() {
        return createdAt;
    }

    public String updatedAt() {
        return updatedAt;
    }

    public void touch() {
        updatedAt = Instant.now().toString();
    }

    public int cuboidCount() {
        return cuboids.size();
    }

    /** Sum of cuboid volumes, saturated. Overlaps are counted twice. */
    public long volume() {
        long total = 0;
        for (Cuboid cuboid : cuboids) {
            long volume = cuboid.volume();
            if (total > Long.MAX_VALUE - volume) {
                return Long.MAX_VALUE;
            }
            total += volume;
        }
        return total;
    }

    public BlockPos boundsMin() {
        if (cuboids.isEmpty()) {
            return origin;
        }
        int x = Integer.MAX_VALUE;
        int y = Integer.MAX_VALUE;
        int z = Integer.MAX_VALUE;
        for (Cuboid cuboid : cuboids) {
            x = Math.min(x, cuboid.minX());
            y = Math.min(y, cuboid.minY());
            z = Math.min(z, cuboid.minZ());
        }
        return new BlockPos(x, y, z);
    }

    public BlockPos boundsMax() {
        if (cuboids.isEmpty()) {
            return origin;
        }
        int x = Integer.MIN_VALUE;
        int y = Integer.MIN_VALUE;
        int z = Integer.MIN_VALUE;
        for (Cuboid cuboid : cuboids) {
            x = Math.max(x, cuboid.maxX());
            y = Math.max(y, cuboid.maxY());
            z = Math.max(z, cuboid.maxZ());
        }
        return new BlockPos(x, y, z);
    }

    public boolean contains(BlockPos pos) {
        for (Cuboid cuboid : cuboids) {
            if (cuboid.contains(pos)) {
                return true;
            }
        }
        return false;
    }

    public BlockPos relative(BlockPos absolute) {
        if (origin == null) {
            throw new IllegalStateException("Selection has no origin yet.");
        }
        return absolute.subtract(origin);
    }

    public BlockPos absolute(BlockPos relative) {
        if (origin == null) {
            throw new IllegalStateException("Selection has no origin yet.");
        }
        return origin.add(relative);
    }

    public static Comparator<Selection> byName() {
        return Comparator.comparing(selection -> selection.name().toLowerCase(java.util.Locale.ROOT));
    }

    @Override
    public String toString() {
        return name + " (" + cuboids.size() + " cuboids, origin " + origin + ")";
    }
}
