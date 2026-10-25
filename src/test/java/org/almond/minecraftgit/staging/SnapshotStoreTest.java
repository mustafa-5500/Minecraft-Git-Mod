package org.almond.minecraftgit.staging;

import org.almond.minecraftgit.model.BlockPos;
import org.almond.minecraftgit.model.BlockSnapshot;
import org.almond.minecraftgit.model.EntitySnapshot;
import org.almond.minecraftgit.model.Snapshot;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SnapshotStoreTest {

    @TempDir
    Path temp;

    private Snapshot snapshot() {
        BlockPos origin = new BlockPos(-10, 64, 20);
        List<BlockSnapshot> blocks = List.of(
                BlockSnapshot.at(new BlockPos(-10, 64, 20), origin, "minecraft:stone", null),
                BlockSnapshot.at(new BlockPos(-9, 65, 21), origin, "minecraft:chest[facing=north,type=single,waterlogged=false]",
                        "{\"v\":1,\"inventory\":{\"0\":\"AA==\"}}"));
        List<EntitySnapshot> entities = List.of(new EntitySnapshot(-9.5, 65.5, 21.5, 0.5, 1.5, 1.5,
                90f, 10f, "minecraft:item_frame", "entity-data"));
        return new Snapshot("castle", origin, blocks, entities, Instant.now());
    }

    @Test
    void roundTripsEverything() {
        Path file = temp.resolve("world/uuid/castle.snap");
        SnapshotStore store = new SnapshotStore();
        Snapshot expected = snapshot();
        store.write(file, expected);

        assertTrue(Files.isRegularFile(file));
        Snapshot actual = store.read(file);
        assertEquals(expected.selection(), actual.selection());
        assertEquals(expected.origin(), actual.origin());
        assertEquals(expected.blockCount(), actual.blockCount());
        assertEquals(expected.entityCount(), actual.entityCount());
        assertEquals(expected.blocks(), actual.blocks());
        assertEquals(expected.entities(), actual.entities());
    }

    @Test
    void missingFileFailsActionably() {
        SnapshotStore store = new SnapshotStore();
        assertThrows(StagingException.class, () -> store.read(temp.resolve("missing.snap")));
    }

    @Test
    void truncatedFileIsDetected() throws Exception {
        Path file = temp.resolve("truncated.snap");
        SnapshotStore store = new SnapshotStore();
        store.write(file, snapshot());
        byte[] bytes = Files.readAllBytes(file);
        Files.write(file, java.util.Arrays.copyOf(bytes, bytes.length / 2));
        assertThrows(StagingException.class, () -> store.read(file));
    }
}
