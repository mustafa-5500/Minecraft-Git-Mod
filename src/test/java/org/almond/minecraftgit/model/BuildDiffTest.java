package org.almond.minecraftgit.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BuildDiffTest {

    private static BlockSnapshot block(BlockPos origin, int x, int y, int z, String data) {
        return BlockSnapshot.at(new BlockPos(x, y, z), origin, data, null);
    }

    private static Snapshot snapshot(BlockPos origin, List<BlockSnapshot> blocks) {
        return new Snapshot("s", origin, blocks, List.of(), Instant.now());
    }

    @Test
    void detectsAddedChangedAndRemovedCoordinates() {
        BlockPos origin = BlockPos.ORIGIN;
        Snapshot before = snapshot(origin, List.of(
                block(origin, 0, 0, 0, "minecraft:stone"),
                block(origin, 1, 0, 0, "minecraft:dirt")));
        Snapshot after = snapshot(origin, List.of(
                block(origin, 0, 0, 0, "minecraft:gold_block"),
                block(origin, 2, 0, 0, "minecraft:air")));

        BuildDiff diff = BuildDiff.between(before, after);
        assertEquals(1, diff.added().size());
        assertEquals(1, diff.changed().size());
        assertEquals(1, diff.removed().size());
        assertEquals(0, diff.unchanged());
        assertEquals(3, diff.totalBlockChanges());
        assertFalse(diff.isEmpty());
    }

    @Test
    void identicalSnapshotsHaveEmptyDiff() {
        BlockPos origin = BlockPos.ORIGIN;
        Snapshot snapshot = snapshot(origin, List.of(block(origin, 0, 0, 0, "minecraft:stone")));
        BuildDiff diff = BuildDiff.between(snapshot, snapshot);
        assertTrue(diff.isEmpty());
        assertEquals(1, diff.unchanged());
    }

    @Test
    void nullBeforeMeansEverythingIsAdded() {
        BlockPos origin = new BlockPos(10, 64, 10);
        Snapshot after = snapshot(origin, List.of(block(origin, 10, 64, 10, "minecraft:air")));
        BuildDiff diff = BuildDiff.between(null, after);
        assertEquals(1, diff.added().size());
        assertEquals(0, diff.removed().size());
    }

    @Test
    void entityChangesAreCounted() {
        BlockPos origin = BlockPos.ORIGIN;
        EntitySnapshot entity = new EntitySnapshot(0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0f, 0f,
                "minecraft:item_frame", "data");
        Snapshot before = snapshot(origin, List.of());
        Snapshot after = new Snapshot("s", origin, List.of(), List.of(entity), Instant.now());
        BuildDiff diff = BuildDiff.between(before, after);
        assertEquals(1, diff.entitiesAdded().size());
        assertEquals(1, diff.totalEntityChanges());
    }
}
