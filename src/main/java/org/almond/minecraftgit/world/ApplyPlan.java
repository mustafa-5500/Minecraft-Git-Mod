package org.almond.minecraftgit.world;

import org.almond.minecraftgit.model.BlockPos;
import org.almond.minecraftgit.model.BlockSnapshot;
import org.almond.minecraftgit.model.EntitySnapshot;
import org.almond.minecraftgit.model.Selection;
import org.almond.minecraftgit.model.Snapshot;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Absolute world writes needed to turn one snapshot into another.
 *
 * <p>Relative coordinates are the canonical Git representation. Placements land
 * at {@code targetOrigin + relative}; removals happen at
 * {@code oldOrigin + relative}. If the local origin moved, the whole build is
 * removed from the old location and placed at the new one (E4).</p>
 */
public record ApplyPlan(String selectionName, UUID owner, BlockPos targetOrigin, BlockPos oldEntityOrigin,
                        Map<BlockPos, BlockSnapshot> placements, Set<BlockPos> removals,
                        List<EntitySnapshot> entityRemovals, List<EntityPlacement> entityPlacements,
                        boolean createBackup) {

    public static ApplyPlan between(Selection selection, Snapshot old, BlockPos oldOrigin,
                                    Snapshot target, BlockPos targetOrigin, boolean createBackup) {
        Map<BlockPos, BlockSnapshot> oldBlocks = old == null ? Map.of() : old.byRelative();
        Map<BlockPos, BlockSnapshot> targetBlocks = target.byRelative();
        boolean moved = old != null && oldOrigin != null && !oldOrigin.equals(targetOrigin);

        Map<BlockPos, BlockSnapshot> placements = new LinkedHashMap<>();
        Set<BlockPos> removals = new LinkedHashSet<>();

        if (old == null || moved) {
            for (Map.Entry<BlockPos, BlockSnapshot> entry : targetBlocks.entrySet()) {
                BlockPos absolute = targetOrigin.add(entry.getKey());
                placements.put(absolute, absolute(absolute, entry.getKey(), entry.getValue()));
            }
        } else {
            for (Map.Entry<BlockPos, BlockSnapshot> entry : targetBlocks.entrySet()) {
                BlockSnapshot previous = oldBlocks.get(entry.getKey());
                if (previous == null || !sameBlock(previous, entry.getValue())) {
                    BlockPos absolute = targetOrigin.add(entry.getKey());
                    placements.put(absolute, absolute(absolute, entry.getKey(), entry.getValue()));
                }
            }
        }

        if (old != null) {
            BlockPos removalOrigin = oldOrigin == null ? targetOrigin : oldOrigin;
            for (BlockPos relative : oldBlocks.keySet()) {
                BlockPos absolute = removalOrigin.add(relative);
                if ((moved || !targetBlocks.containsKey(relative)) && !placements.containsKey(absolute)) {
                    removals.add(absolute);
                }
            }
        }

        List<EntitySnapshot> oldEntities = old == null ? List.of() : old.entities();
        List<EntitySnapshot> targetEntities = target.entities();
        List<EntityPlacement> entityPlacements = new ArrayList<>();
        List<EntitySnapshot> entityRemovals = new ArrayList<>();
        if (old == null || moved) {
            for (EntitySnapshot entity : targetEntities) {
                entityPlacements.add(place(targetOrigin, entity));
            }
            entityRemovals.addAll(oldEntities);
        } else {
            for (EntitySnapshot entity : targetEntities) {
                if (!oldEntities.contains(entity)) {
                    entityPlacements.add(place(targetOrigin, entity));
                }
            }
            for (EntitySnapshot entity : oldEntities) {
                if (!targetEntities.contains(entity)) {
                    entityRemovals.add(entity);
                }
            }
        }

        return new ApplyPlan(selection.name(), selection.owner(), targetOrigin, oldOrigin,
                placements, removals, List.copyOf(entityRemovals), List.copyOf(entityPlacements), createBackup);
    }

    /** Restore a pre-apply backup, whose block snapshots already carry absolute coordinates. */
    public static ApplyPlan restore(Selection selection, Snapshot backup) {
        Map<BlockPos, BlockSnapshot> placements = new LinkedHashMap<>();
        for (BlockSnapshot block : backup.blocks()) {
            placements.put(block.absolute(), block);
        }
        List<EntityPlacement> entities = new ArrayList<>();
        for (EntitySnapshot entity : backup.entities()) {
            entities.add(new EntityPlacement(entity.x(), entity.y(), entity.z(),
                    entity.yaw(), entity.pitch(), entity));
        }
        return new ApplyPlan(selection.name(), selection.owner(), backup.origin(), null,
                placements, Set.of(), List.of(), List.copyOf(entities), true);
    }

    private static BlockSnapshot absolute(BlockPos absolute, BlockPos relative, BlockSnapshot source) {
        return new BlockSnapshot(absolute.x(), absolute.y(), absolute.z(),
                relative.x(), relative.y(), relative.z(), source.blockData(), source.blockEntity());
    }

    private static EntityPlacement place(BlockPos origin, EntitySnapshot entity) {
        return new EntityPlacement(origin.x() + entity.rx(), origin.y() + entity.ry(),
                origin.z() + entity.rz(), entity.yaw(), entity.pitch(), entity);
    }

    private static boolean sameBlock(BlockSnapshot a, BlockSnapshot b) {
        return a.blockData().equals(b.blockData())
                && java.util.Objects.equals(a.blockEntity(), b.blockEntity());
    }

    public Set<BlockPos> allPositions() {
        Set<BlockPos> positions = new LinkedHashSet<>(placements.keySet());
        positions.addAll(removals);
        return positions;
    }

    public record EntityPlacement(double x, double y, double z, float yaw, float pitch,
                                  EntitySnapshot snapshot) {
    }
}
