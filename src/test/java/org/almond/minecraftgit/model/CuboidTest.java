package org.almond.minecraftgit.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CuboidTest {

    @Test
    void normalizesCornersAndComputesVolume() {
        Cuboid cuboid = new Cuboid(10, 20, 30, 0, 0, 0);
        assertEquals(0, cuboid.minX());
        assertEquals(0, cuboid.minY());
        assertEquals(0, cuboid.minZ());
        assertEquals(10, cuboid.maxX());
        assertEquals(20, cuboid.maxY());
        assertEquals(30, cuboid.maxZ());
        assertEquals(11 * 21 * 31L, cuboid.volume());
        assertEquals("11x21x31", cuboid.dimensions());
        assertTrue(cuboid.contains(new BlockPos(5, 5, 5)));
        assertFalse(cuboid.contains(new BlockPos(11, 5, 5)));
    }

    @Test
    void negativeCoordinatesNormalize() {
        Cuboid cuboid = Cuboid.of(new BlockPos(-5, -2, -9), new BlockPos(-1, -1, -3));
        assertEquals(-5, cuboid.minX());
        assertEquals(-9, cuboid.minZ());
        assertEquals(-1, cuboid.maxX());
        assertEquals(-3, cuboid.maxZ());
        assertEquals(5L * 2L * 7L, cuboid.volume());
    }

    @Test
    void sortsBySmallestCorner() {
        Cuboid far = new Cuboid(100, 0, 0, 110, 10, 10);
        Cuboid near = new Cuboid(0, 5, 0, 10, 15, 10);
        Cuboid negative = new Cuboid(-20, 0, 0, -10, 10, 10);
        List<Cuboid> cuboids = new ArrayList<>(List.of(far, near, negative));
        cuboids.sort(Cuboid.SMALLEST_CORNER_ORDER);
        assertEquals(List.of(negative, near, far), cuboids);
    }

    @Test
    void intersectsAndUnions() {
        Cuboid a = new Cuboid(0, 0, 0, 10, 10, 10);
        Cuboid b = new Cuboid(5, 5, 5, 15, 15, 15);
        Cuboid c = new Cuboid(11, 0, 0, 20, 10, 10);
        assertTrue(a.intersects(b));
        assertFalse(a.intersects(c));
        assertEquals(new Cuboid(0, 0, 0, 15, 15, 15), a.union(b));
    }
}
