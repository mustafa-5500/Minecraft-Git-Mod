package org.almond.minecraftgit.world;

import org.almond.minecraftgit.model.BlockPos;
import org.almond.minecraftgit.model.BlockSnapshot;
import org.almond.minecraftgit.model.Cuboid;
import org.almond.minecraftgit.model.Selection;
import org.almond.minecraftgit.model.Snapshot;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApplyPlanTest {

    private static Selection selection() {
        Selection selection = new Selection("castle", UUID.randomUUID(), "Almond", "world", BlockPos.ORIGIN);
        selection.addCuboid(Cuboid.of(new BlockPos(0, 0, 0), new BlockPos(2, 0, 0)));
        return selection;
    }

    private static BlockSnapshot block(BlockPos origin, int x, String data) {
        return BlockSnapshot.at(new BlockPos(x, 64, 0), origin, data, null);
    }

    @Test
    void computesPlacementsAndRemovalsAtTheSameOrigin() {
        BlockPos origin = BlockPos.ORIGIN;
        Snapshot old = new Snapshot("castle", origin, List.of(
                block(origin, 0, "minecraft:stone"),
                block(origin, 1, "minecraft:dirt")), List.of(), Instant.now());
        Snapshot target = new Snapshot("castle", origin, List.of(
                block(origin, 0, "minecraft:gold_block"),
                block(origin, 2, "minecraft:air")), List.of(), Instant.now());

        ApplyPlan plan = ApplyPlan.between(selection(), old, origin, target, origin, true);
        assertEquals(2, plan.placements().size());
        assertEquals(1, plan.removals().size());
        assertTrue(plan.removals().contains(new BlockPos(1, 64, 0)));
        assertEquals("minecraft:gold_block", plan.placements().get(new BlockPos(0, 64, 0)).blockData());
    }

    @Test
    void movingOriginMovesEveryBlock() {
        BlockPos oldOrigin = BlockPos.ORIGIN;
        BlockPos newOrigin = new BlockPos(10, 0, 0);
        Snapshot old = new Snapshot("castle", oldOrigin, List.of(
                block(oldOrigin, 0, "minecraft:stone"),
                block(oldOrigin, 1, "minecraft:dirt")), List.of(), Instant.now());
        Snapshot target = new Snapshot("castle", newOrigin, List.of(
                block(newOrigin, 10, "minecraft:stone"),
                block(newOrigin, 11, "minecraft:dirt")), List.of(), Instant.now());

        ApplyPlan plan = ApplyPlan.between(selection(), old, oldOrigin, target, newOrigin, true);
        assertEquals(2, plan.placements().size());
        assertEquals(2, plan.removals().size());
        assertTrue(plan.placements().containsKey(new BlockPos(10, 64, 0)));
        assertTrue(plan.removals().contains(new BlockPos(0, 64, 0)));
    }

    @Test
    void restoringBackupUsesAbsoluteCoordinates() {
        BlockPos origin = new BlockPos(5, 60, 5);
        Snapshot backup = new Snapshot("castle", origin,
                List.of(BlockSnapshot.at(new BlockPos(5, 60, 5), origin, "minecraft:chest", "{}")),
                List.of(), Instant.now());
        ApplyPlan plan = ApplyPlan.restore(selection(), backup);
        assertEquals(1, plan.placements().size());
        assertTrue(plan.placements().containsKey(new BlockPos(5, 60, 5)));
        assertEquals(0, plan.removals().size());
    }
}
