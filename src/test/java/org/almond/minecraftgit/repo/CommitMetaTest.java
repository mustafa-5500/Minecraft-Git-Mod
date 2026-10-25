package org.almond.minecraftgit.repo;

import org.almond.minecraftgit.model.BlockPos;
import org.almond.minecraftgit.model.Cuboid;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommitMetaTest {

    @TempDir
    Path temp;

    private CommitMeta meta() {
        return new CommitMeta("castle", "world", "Almond", "uuid-1", "CREATIVE", 1, "1.0.0",
                new BlockPos(-10, 60, 20), new BlockPos(-12, 60, 20), true,
                "20x10x30", 2, 1234, 3,
                List.of(new Cuboid(0, 0, 0, 10, 5, 10), new Cuboid(20, 0, 20, 22, 5, 22)),
                "2026-10-23T00:00:00Z");
    }

    @Test
    void roundTripsThroughJsonFile() {
        Path file = temp.resolve("castle/meta.json");
        meta().write(file);
        assertTrue(java.nio.file.Files.isRegularFile(file));

        CommitMeta loaded = CommitMeta.read(file).orElseThrow();
        assertEquals("castle", loaded.selection());
        assertEquals(new BlockPos(-10, 60, 20), loaded.origin());
        assertEquals(new BlockPos(-12, 60, 20), loaded.previousOrigin());
        assertTrue(loaded.reorigined());
        assertEquals(2, loaded.relativeCuboids().size());
        assertEquals(new Cuboid(0, 0, 0, 10, 5, 10), loaded.relativeCuboids().get(0));
        assertEquals(1234, loaded.blockCount());
    }

    @Test
    void missingMetadataReturnsEmpty() {
        assertFalse(CommitMeta.read(temp.resolve("missing.json")).isPresent());
    }

    @Test
    void invalidMetadataReturnsEmpty() throws Exception {
        Path file = temp.resolve("bad.json");
        java.nio.file.Files.writeString(file, "{not json");
        assertFalse(CommitMeta.read(file).isPresent());
    }
}
