package org.almond.minecraftgit.selection;

import org.almond.minecraftgit.model.BlockPos;
import org.almond.minecraftgit.model.Cuboid;
import org.almond.minecraftgit.model.Selection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SelectionMetadataStoreTest {

    private static final UUID OWNER = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @TempDir
    Path temp;

    @Test
    void roundTripsSelectionsPerWorldAndOwner() {
        SelectionMetadataStore store = new SelectionMetadataStore(temp);
        Selection castle = new Selection("castle", OWNER, "Player", "world__overworld",
                new BlockPos(10, 20, 30));
        castle.addCuboid(new Cuboid(10, 20, 30, 20, 30, 40));
        castle.setTracked(true);

        store.save("world__overworld", OWNER, "Player", List.of(castle));
        assertTrue(Files.isRegularFile(store.file("world__overworld", OWNER)));

        List<Selection> loaded = store.load("world__overworld", OWNER);
        assertEquals(1, loaded.size());
        Selection roundTrip = loaded.get(0);
        assertEquals("castle", roundTrip.name());
        assertEquals(new BlockPos(10, 20, 30), roundTrip.origin());
        assertEquals(1, roundTrip.cuboidCount());
        assertTrue(roundTrip.isTracked());
    }

    @Test
    void differentWorldsAreIsolated() {
        SelectionMetadataStore store = new SelectionMetadataStore(temp);
        store.save("overworld", OWNER, "Player", List.of(
                new Selection("a", OWNER, "Player", "overworld", null)));
        store.save("nether", OWNER, "Player", List.of(
                new Selection("b", OWNER, "Player", "nether", null)));

        assertEquals("a", store.load("overworld", OWNER).get(0).name());
        assertEquals("b", store.load("nether", OWNER).get(0).name());
    }

    @Test
    void corruptFileFailsWithUserFacingMessage() throws Exception {
        SelectionMetadataStore store = new SelectionMetadataStore(temp);
        Path file = store.file("world__overworld", OWNER);
        Files.createDirectories(file.getParent());
        Files.writeString(file, "{ not json");

        SelectionException error = assertThrows(SelectionException.class,
                () -> store.load("world__overworld", OWNER));
        assertTrue(error.getMessage().contains("corrupt"));
    }
}
