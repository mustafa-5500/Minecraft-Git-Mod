package org.almond.minecraftgit.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SelectionTest {

    private static final UUID OWNER = UUID.fromString("00000000-0000-0000-0000-000000000001");

    @Test
    void firstCuboidSeedsOriginAndSorts() {
        Selection selection = new Selection("castle", OWNER, "Player", "world__overworld", null);
        selection.addCuboid(new Cuboid(50, 60, 70, 55, 65, 75));
        selection.addCuboid(new Cuboid(0, 0, 0, 5, 5, 5));

        // The origin is seeded by the first cuboid added; sorting does not move it.
        assertEquals(new BlockPos(50, 60, 70), selection.origin());
        assertEquals(2, selection.cuboidCount());
        assertEquals(new BlockPos(0, 0, 0), selection.cuboids().get(0).smallestCorner());
    }

    @Test
    void removingOriginCuboidKeepsOrigin() {
        Selection selection = new Selection("castle", OWNER, "Player", "world__overworld", null);
        selection.addCuboid(new Cuboid(0, 0, 0, 5, 5, 5));
        selection.addCuboid(new Cuboid(20, 0, 0, 25, 5, 5));

        selection.removeCuboidAt(0);

        assertEquals(new BlockPos(0, 0, 0), selection.origin(),
                "E2: origin must not move when its cuboid is removed");
        assertEquals(1, selection.cuboidCount());
    }

    @Test
    void reoriginMovesToSmallestRemainingCorner() {
        Selection selection = new Selection("castle", OWNER, "Player", "world__overworld", null);
        selection.addCuboid(new Cuboid(-10, 0, 0, -5, 5, 5));
        selection.addCuboid(new Cuboid(30, 0, 0, 35, 5, 5));
        selection.removeCuboidAt(0);

        BlockPos previous = selection.reorigin();

        assertEquals(new BlockPos(-10, 0, 0), previous);
        assertEquals(new BlockPos(30, 0, 0), selection.origin());
    }

    @Test
    void normalizeFillsWorldOwnerAndSorts() {
        Selection selection = new Selection();
        selection.setCuboids(new java.util.ArrayList<>(java.util.List.of(
                new Cuboid(30, 0, 0, 35, 5, 5),
                new Cuboid(0, 0, 0, 5, 5, 5))));
        selection.normalize("world__overworld", OWNER, "Player");

        assertEquals("world__overworld", selection.world());
        assertEquals(OWNER, selection.owner());
        assertEquals("Player", selection.ownerName());
        assertEquals(new BlockPos(0, 0, 0), selection.origin());
        assertEquals(2, selection.cuboidCount());
    }

    @Test
    void emptySelectionHasNoOrigin() {
        Selection selection = new Selection("empty", OWNER, "Player", "world__overworld", null);
        assertNull(selection.origin());
        assertEquals(0L, selection.volume());
        assertTrue(selection.cuboids().isEmpty());
    }
}
