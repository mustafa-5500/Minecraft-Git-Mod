package org.almond.minecraftgit.serialization;

import org.almond.minecraftgit.model.BlockPos;
import org.almond.minecraftgit.model.BlockSnapshot;
import org.almond.minecraftgit.model.EntitySnapshot;
import org.almond.minecraftgit.model.Snapshot;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuadrantCsvTest {

    @TempDir
    Path temp;

    private Snapshot sample() {
        BlockPos origin = new BlockPos(-10, 60, 20);
        List<BlockSnapshot> blocks = new ArrayList<>();
        blocks.add(block(origin, -10, 60, 20, "minecraft:oak_planks", null));
        blocks.add(block(origin, -9, 60, 20, "minecraft:stone", null));
        blocks.add(block(origin, -13, 60, 20, "minecraft:dirt", null));
        blocks.add(block(origin, -10, 60, 16, "minecraft:sand", null));
        blocks.add(block(origin, -12, 60, 18, "minecraft:glass", null));
        blocks.add(block(origin, -9, 64, 20,
                "minecraft:oak_stairs[facing=north,half=bottom,shape=straight,waterlogged=false]", null));
        blocks.add(block(origin, -10, 64, 20, "minecraft:chest[facing=north,type=single,waterlogged=false]",
                "{\"v\":1,\"inventory\":{\"0\":\"AA==\"}}"));
        blocks.add(block(origin, -9, 65, 21, "minecraft:air", null));
        List<EntitySnapshot> entities = List.of(new EntitySnapshot(-9.5, 60.5, 20.5,
                0.5, 0.5, 0.5, 90f, 0f, "minecraft:item_frame", "entity-data"));
        return new Snapshot("castle", origin, blocks, entities, Instant.now());
    }

    private static BlockSnapshot block(BlockPos origin, int x, int y, int z, String data, String entity) {
        return BlockSnapshot.at(new BlockPos(x, y, z), origin, data, entity);
    }

    @Test
    void roundTripsAllQuadrantsGapsAndEntities() {
        Snapshot snapshot = sample();
        Path dir = temp.resolve("castle");
        QuadrantCsvWriter.writeSelection(dir, snapshot);

        assertTrue(Files.isRegularFile(dir.resolve("y_0").resolve("pp.csv")));
        assertTrue(Files.isRegularFile(dir.resolve("y_0").resolve("np.csv")));
        assertTrue(Files.isRegularFile(dir.resolve("y_0").resolve("pn.csv")));
        assertTrue(Files.isRegularFile(dir.resolve("y_0").resolve("nn.csv")));
        assertTrue(Files.isRegularFile(dir.resolve("y_4").resolve("pp.csv")));
        assertTrue(Files.isRegularFile(dir.resolve("y_5").resolve("pp.csv")));
        assertTrue(Files.isRegularFile(dir.resolve("entities.csv")));

        Snapshot restored = QuadrantCsvReader.readSelection(dir, "castle", BlockPos.ORIGIN);
        assertEquals(snapshot.origin(), restored.origin());
        assertEquals(snapshot.blockCount(), restored.blockCount());
        assertEquals(snapshot.entityCount(), restored.entityCount());

        var original = snapshot.byRelative();
        var read = restored.byRelative();
        assertEquals(original.keySet(), read.keySet());
        for (var entry : original.entrySet()) {
            BlockSnapshot expected = entry.getValue();
            BlockSnapshot actual = read.get(entry.getKey());
            assertNotNull(actual);
            assertEquals(expected.blockData(), actual.blockData());
            assertEquals(expected.blockEntity(), actual.blockEntity());
        }
        EntitySnapshot entity = restored.entities().get(0);
        assertEquals("minecraft:item_frame", entity.type());
        assertEquals("entity-data", entity.data());
        assertEquals(0.5, entity.rx(), 0.0001);
    }

    @Test
    void headerRecordsOriginAndYLevel() throws Exception {
        Path dir = temp.resolve("castle");
        QuadrantCsvWriter.writeSelection(dir, sample());
        String header = Files.readString(dir.resolve("y_0").resolve("pp.csv")).lines().findFirst().orElseThrow();
        assertTrue(header.contains("origin=-10,60,20"));
        assertTrue(header.contains("y=0"));
        assertTrue(header.contains("quadrant=pp"));
    }

    @Test
    void sparseFarApartCoordinatesRoundTrip() {
        BlockPos origin = BlockPos.ORIGIN;
        List<BlockSnapshot> blocks = List.of(
                block(origin, 100, 0, 100, "minecraft:stone", null),
                block(origin, -100, 0, -100, "minecraft:dirt", null));
        Snapshot snapshot = new Snapshot("far", origin, blocks, List.of(), Instant.now());
        Path dir = temp.resolve("far");
        QuadrantCsvWriter.writeSelection(dir, snapshot);
        Snapshot restored = QuadrantCsvReader.readSelection(dir, "far", BlockPos.ORIGIN);
        assertEquals(2, restored.blockCount());
        assertTrue(restored.byRelative().containsKey(new BlockPos(100, 0, 100)));
        assertTrue(restored.byRelative().containsKey(new BlockPos(-100, 0, -100)));
    }

    @Test
    void emptySnapshotWritesNoCsvAndReadsBackEmpty() {
        Path dir = temp.resolve("empty");
        Snapshot snapshot = new Snapshot("empty", new BlockPos(5, 70, 5), List.of(), List.of(), Instant.now());
        QuadrantCsvWriter.writeSelection(dir, snapshot);
        assertFalse(Files.exists(dir.resolve("entities.csv")));
        Snapshot restored = QuadrantCsvReader.readSelection(dir, "empty", snapshot.origin());
        assertEquals(0, restored.blockCount());
        assertEquals(snapshot.origin(), restored.origin());
    }

    @Test
    void emptyCellsMeanUntrackedWhileAirIsWritten() throws Exception {
        BlockPos origin = BlockPos.ORIGIN;
        List<BlockSnapshot> blocks = List.of(
                block(origin, 0, 0, 0, "minecraft:air", null),
                block(origin, 2, 0, 2, "minecraft:stone", null));
        Path dir = temp.resolve("sparse");
        QuadrantCsvWriter.writeSelection(dir, new Snapshot("sparse", origin, blocks, List.of(), Instant.now()));
        List<String> lines = Files.readAllLines(dir.resolve("y_0").resolve("pp.csv"));
        assertEquals(4, lines.size());
        assertTrue(lines.get(1).startsWith("minecraft:air,"));
        assertFalse(lines.get(1).contains("stone"));
        assertTrue(lines.get(3).endsWith("minecraft:stone"));
    }

    @Test
    void mismatchedOriginsAreRejected() throws Exception {
        Path dir = temp.resolve("castle");
        QuadrantCsvWriter.writeSelection(dir, sample());
        Path pn = dir.resolve("y_0").resolve("pn.csv");
        String contents = Files.readString(pn).replace("origin=-10,60,20", "origin=0,0,0");
        Files.writeString(pn, contents);
        assertThrows(SerializationException.class,
                () -> QuadrantCsvReader.readSelection(dir, "castle", BlockPos.ORIGIN));
    }

    @Test
    void blockEntityRoundTrips() {
        Snapshot snapshot = sample();
        BlockSnapshot chest = snapshot.blocks().stream()
                .filter(block -> block.blockData().startsWith("minecraft:chest"))
                .findFirst().orElseThrow();
        assertNotNull(chest.blockEntity());
        assertNull(snapshot.blocks().stream()
                .filter(block -> block.blockData().startsWith("minecraft:oak_planks"))
                .findFirst().orElseThrow().blockEntity());
    }
}
