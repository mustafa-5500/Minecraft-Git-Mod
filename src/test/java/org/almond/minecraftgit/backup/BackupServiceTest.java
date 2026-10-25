package org.almond.minecraftgit.backup;

import org.almond.minecraftgit.model.BlockPos;
import org.almond.minecraftgit.model.BlockSnapshot;
import org.almond.minecraftgit.model.Snapshot;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BackupServiceTest {

    @TempDir
    Path temp;

    private Snapshot snapshot(BlockPos origin, String data) {
        return new Snapshot("castle", origin,
                List.of(BlockSnapshot.at(origin, origin, data, null)), List.of(), Instant.now());
    }

    @Test
    void storesAndReadsLatestBackup() {
        BackupService service = new BackupService(temp, 10);
        UUID owner = UUID.randomUUID();
        service.store("world", owner, "castle", snapshot(BlockPos.ORIGIN, "minecraft:stone"));
        service.store("world", owner, "castle", snapshot(BlockPos.ORIGIN, "minecraft:gold_block"));

        BackupService.Backup latest = service.latest("world", owner, "castle").orElseThrow();
        assertEquals("minecraft:gold_block", latest.snapshot().blocks().get(0).blockData());
        assertTrue(java.nio.file.Files.isRegularFile(latest.file()));
    }

    @Test
    void prunesOldBackups() {
        BackupService service = new BackupService(temp, 2);
        UUID owner = UUID.randomUUID();
        service.store("world", owner, "castle", snapshot(BlockPos.ORIGIN, "minecraft:stone"));
        service.store("world", owner, "castle", snapshot(BlockPos.ORIGIN, "minecraft:dirt"));
        service.store("world", owner, "castle", snapshot(BlockPos.ORIGIN, "minecraft:gold_block"));
        assertEquals(2, service.list("world", owner, "castle").size());
    }

    @Test
    void missingBackupIsEmpty() {
        BackupService service = new BackupService(temp, 2);
        assertFalse(service.latest("world", UUID.randomUUID(), "castle").isPresent());
    }
}
