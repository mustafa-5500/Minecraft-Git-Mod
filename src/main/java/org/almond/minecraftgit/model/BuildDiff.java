package org.almond.minecraftgit.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Coordinate-level difference between two snapshots, using relative
 * coordinates. The final state of a coordinate wins; there is no line-based
 * three-way merge at this layer.
 */
public final class BuildDiff {

    private final List<BlockSnapshot> added;
    private final List<BlockSnapshot> changed;
    private final List<BlockSnapshot> removed;
    private final List<EntitySnapshot> entitiesAdded;
    private final List<EntitySnapshot> entitiesRemoved;
    private final int unchanged;

    private BuildDiff(List<BlockSnapshot> added, List<BlockSnapshot> changed, List<BlockSnapshot> removed,
                      List<EntitySnapshot> entitiesAdded, List<EntitySnapshot> entitiesRemoved, int unchanged) {
        this.added = List.copyOf(added);
        this.changed = List.copyOf(changed);
        this.removed = List.copyOf(removed);
        this.entitiesAdded = List.copyOf(entitiesAdded);
        this.entitiesRemoved = List.copyOf(entitiesRemoved);
        this.unchanged = unchanged;
    }

    public static BuildDiff between(Snapshot before, Snapshot after) {
        Map<BlockPos, BlockSnapshot> oldBlocks = before == null ? Map.of() : before.byRelative();
        Map<BlockPos, BlockSnapshot> newBlocks = after == null ? Map.of() : after.byRelative();
        List<BlockSnapshot> added = new ArrayList<>();
        List<BlockSnapshot> changed = new ArrayList<>();
        List<BlockSnapshot> removed = new ArrayList<>();
        int unchanged = 0;

        for (Map.Entry<BlockPos, BlockSnapshot> entry : newBlocks.entrySet()) {
            BlockSnapshot oldBlock = oldBlocks.get(entry.getKey());
            if (oldBlock == null) {
                added.add(entry.getValue());
            } else if (!sameBlock(oldBlock, entry.getValue())) {
                changed.add(entry.getValue());
            } else {
                unchanged++;
            }
        }
        for (Map.Entry<BlockPos, BlockSnapshot> entry : oldBlocks.entrySet()) {
            if (!newBlocks.containsKey(entry.getKey())) {
                removed.add(entry.getValue());
            }
        }

        List<EntitySnapshot> oldEntities = new ArrayList<>(before == null ? List.of() : before.entities());
        List<EntitySnapshot> newEntities = new ArrayList<>(after == null ? List.of() : after.entities());
        List<EntitySnapshot> entitiesAdded = new ArrayList<>();
        for (EntitySnapshot entity : newEntities) {
            if (!oldEntities.remove(entity)) {
                entitiesAdded.add(entity);
            }
        }
        List<EntitySnapshot> entitiesRemoved = oldEntities;

        added.sort(BlockSnapshot.ORDER);
        changed.sort(BlockSnapshot.ORDER);
        removed.sort(BlockSnapshot.ORDER);
        entitiesAdded.sort(EntitySnapshot.ORDER);
        entitiesRemoved.sort(EntitySnapshot.ORDER);
        return new BuildDiff(added, changed, removed, entitiesAdded, entitiesRemoved, unchanged);
    }

    private static boolean sameBlock(BlockSnapshot a, BlockSnapshot b) {
        return a.blockData().equals(b.blockData())
                && java.util.Objects.equals(a.blockEntity(), b.blockEntity());
    }

    /** Merge maps keyed by relative coordinate, final state wins. */
    public static Map<BlockPos, BlockSnapshot> mergeBlocks(Map<BlockPos, BlockSnapshot>... maps) {
        Map<BlockPos, BlockSnapshot> result = new LinkedHashMap<>();
        for (Map<BlockPos, BlockSnapshot> map : maps) {
            result.putAll(map);
        }
        return result;
    }

    public List<BlockSnapshot> added() {
        return added;
    }

    public List<BlockSnapshot> changed() {
        return changed;
    }

    public List<BlockSnapshot> removed() {
        return removed;
    }

    public List<EntitySnapshot> entitiesAdded() {
        return entitiesAdded;
    }

    public List<EntitySnapshot> entitiesRemoved() {
        return entitiesRemoved;
    }

    public int unchanged() {
        return unchanged;
    }

    public long totalBlockChanges() {
        return (long) added.size() + changed.size() + removed.size();
    }

    public long totalEntityChanges() {
        return (long) entitiesAdded.size() + entitiesRemoved.size();
    }

    public boolean isEmpty() {
        return added.isEmpty() && changed.isEmpty() && removed.isEmpty()
                && entitiesAdded.isEmpty() && entitiesRemoved.isEmpty();
    }

    public String summary() {
        return added.size() + " added, " + changed.size() + " changed, " + removed.size() + " removed";
    }
}
