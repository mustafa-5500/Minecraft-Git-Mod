package org.almond.minecraftgit.staging;

import org.almond.minecraftgit.debug.DebugLogger;
import org.almond.minecraftgit.model.BlockPos;
import org.almond.minecraftgit.model.BlockSnapshot;
import org.almond.minecraftgit.model.Cuboid;
import org.almond.minecraftgit.model.Selection;
import org.almond.minecraftgit.model.Snapshot;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StagingServiceTest {

    private static final UUID OWNER = UUID.fromString("22222222-2222-2222-2222-222222222222");

    @TempDir
    Path temp;

    private StagingService service() {
        return new StagingService(temp.resolve("staging"), new SnapshotStore(),
                new DebugLogger(temp.resolve("logs"), false, false));
    }

    private Selection selection() {
        Selection selection = new Selection("castle", OWNER, "Player", "world__overworld",
                new BlockPos(0, 0, 0));
        selection.addCuboid(new Cuboid(0, 0, 0, 10, 10, 10));
        return selection;
    }

    private Snapshot snapshot() {
        BlockPos origin = new BlockPos(0, 0, 0);
        return new Snapshot("castle", origin, List.of(
                BlockSnapshot.at(new BlockPos(0, 0, 0), origin, "minecraft:stone", null),
                BlockSnapshot.at(new BlockPos(5, 5, 5), origin, "minecraft:air", null),
                BlockSnapshot.at(new BlockPos(20, 0, 0), origin, "minecraft:dirt", null)),
                List.of(), Instant.now());
    }

    @Test
    void stageLoadAndUnstage() {
        StagingService service = service();
        Selection selection = selection();
        StagedSelection staged = service.stage(selection, snapshot());

        assertTrue(Files.isRegularFile(staged.file()));
        assertTrue(service.hasStaged(selection.world(), OWNER, selection.name()));
        assertEquals(3, service.load(selection.world(), OWNER, selection.name())
                .orElseThrow().blockCount());
        assertEquals(List.of("castle"), service.stagedNames(selection.world(), OWNER));

        service.unstage(selection.world(), OWNER, selection.name());
        assertFalse(service.hasStaged(selection.world(), OWNER, selection.name()));
    }

    @Test
    void restrictStagedDropsCoordinatesOutsideCuboids() {
        StagingService service = service();
        Selection selection = selection();
        service.stage(selection, snapshot());

        selection.removeCuboidAt(0);
        selection.addCuboid(new Cuboid(0, 0, 0, 5, 5, 5));
        service.restrictStaged(selection.world(), OWNER, selection);

        Snapshot staged = service.load(selection.world(), OWNER, selection.name())
                .orElseThrow().snapshot();
        assertEquals(2, staged.blockCount());
        assertFalse(staged.byRelative().containsKey(new BlockPos(20, 0, 0)));
    }

    @Test
    void rebaseStagedChangesRelativeCoordinates() {
        StagingService service = service();
        Selection selection = selection();
        service.stage(selection, snapshot());

        service.rebaseStaged(selection.world(), OWNER, selection.name(), new BlockPos(5, 0, 0));
        Snapshot staged = service.load(selection.world(), OWNER, selection.name())
                .orElseThrow().snapshot();

        assertEquals(new BlockPos(5, 0, 0), staged.origin());
        assertTrue(staged.byRelative().containsKey(new BlockPos(-5, 0, 0)));
        assertTrue(staged.blocks().stream().anyMatch(block -> block.x() == 0));
    }
}
