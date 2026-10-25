package org.almond.minecraftgit.serialization;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QuadrantTest {

    @Test
    void mapsRelativeCoordinatesToQuadrants() {
        assertEquals(Quadrant.PP, Quadrant.fromOffsets(0, 0));
        assertEquals(Quadrant.PP, Quadrant.fromOffsets(5, 7));
        assertEquals(Quadrant.PN, Quadrant.fromOffsets(5, -1));
        assertEquals(Quadrant.NP, Quadrant.fromOffsets(-1, 5));
        assertEquals(Quadrant.NN, Quadrant.fromOffsets(-1, -1));
    }

    @Test
    void rowAndColumnMappingGrowsAwayFromOrigin() {
        for (Quadrant quadrant : Quadrant.values()) {
            int x = quadrant.rowToX(0);
            int z = quadrant.columnToZ(0);
            assertEquals(0, quadrant.rowForX(x));
            assertEquals(0, quadrant.columnForZ(z));
            int x2 = quadrant.rowToX(3);
            int z2 = quadrant.columnToZ(4);
            assertEquals(3, quadrant.rowForX(x2));
            assertEquals(4, quadrant.columnForZ(z2));
        }
        assertEquals(0, Quadrant.PP.rowToX(0));
        assertEquals(0, Quadrant.PN.rowToX(0));
        assertEquals(-1, Quadrant.NP.rowToX(0));
        assertEquals(-1, Quadrant.NN.rowToX(0));
        assertEquals(-1, Quadrant.PN.columnToZ(0));
        assertEquals(0, Quadrant.NP.columnToZ(0));
    }
}
