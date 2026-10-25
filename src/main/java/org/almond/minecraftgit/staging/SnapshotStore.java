package org.almond.minecraftgit.staging;

import org.almond.minecraftgit.model.BlockPos;
import org.almond.minecraftgit.model.BlockSnapshot;
import org.almond.minecraftgit.model.EntitySnapshot;
import org.almond.minecraftgit.model.Snapshot;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Compact binary persistence for staged snapshots and pre-pull backups. This is
 * deliberately not the commit format: staging files are machine-local, may be
 * large, and are never shared, so a gzipped binary stream is a better fit than
 * CSV. The magic header and version make corruption detectable.
 */
public final class SnapshotStore {

    private static final byte[] MAGIC = {'M', 'C', 'G', 'I', 'T', 'S', 'N', 'P'};
    private static final int VERSION = 1;
    private static final int MAX_BLOCKS = 10_000_000;
    private static final int MAX_ENTITIES = 1_000_000;
    private static final int MAX_BLOCK_DATA_BYTES = 1_048_576;
    private static final int MAX_ENTITY_BYTES = 8_388_608;
    private static final int MAX_SELECTION_BYTES = 128;

    public void write(Path file, Snapshot snapshot) {
        try {
            Files.createDirectories(file.getParent());
            Path tmp = file.resolveSibling(file.getFileName() + ".tmp");
            try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(
                    new GZIPOutputStream(Files.newOutputStream(tmp))))) {
                out.write(MAGIC);
                out.writeInt(VERSION);
                writeString(out, snapshot.selection(), MAX_SELECTION_BYTES);
                out.writeInt(snapshot.origin().x());
                out.writeInt(snapshot.origin().y());
                out.writeInt(snapshot.origin().z());
                out.writeInt(snapshot.blocks().size());
                for (BlockSnapshot block : snapshot.blocks()) {
                    out.writeInt(block.x());
                    out.writeInt(block.y());
                    out.writeInt(block.z());
                    out.writeInt(block.rx());
                    out.writeInt(block.ry());
                    out.writeInt(block.rz());
                    writeString(out, block.blockData(), MAX_BLOCK_DATA_BYTES);
                    out.writeBoolean(block.blockEntity() != null);
                    if (block.blockEntity() != null) {
                        writeString(out, block.blockEntity(), MAX_ENTITY_BYTES);
                    }
                }
                out.writeInt(snapshot.entities().size());
                for (EntitySnapshot entity : snapshot.entities()) {
                    out.writeDouble(entity.x());
                    out.writeDouble(entity.y());
                    out.writeDouble(entity.z());
                    out.writeDouble(entity.rx());
                    out.writeDouble(entity.ry());
                    out.writeDouble(entity.rz());
                    out.writeFloat(entity.yaw());
                    out.writeFloat(entity.pitch());
                    writeString(out, entity.type(), 256);
                    writeString(out, entity.data(), MAX_ENTITY_BYTES);
                }
            }
            try {
                Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new StagingException("Could not write staged snapshot at " + file + ": " + e.getMessage(), e);
        }
    }

    public Snapshot read(Path file) {
        if (!Files.isRegularFile(file)) {
            throw new StagingException("No staged snapshot exists at " + file + ".");
        }
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(
                new GZIPInputStream(Files.newInputStream(file))))) {
            byte[] magic = new byte[MAGIC.length];
            in.readFully(magic);
            for (int i = 0; i < MAGIC.length; i++) {
                if (magic[i] != MAGIC[i]) {
                    throw new StagingException("Staged snapshot at " + file + " has an unknown format magic.");
                }
            }
            int version = in.readInt();
            if (version != VERSION) {
                throw new StagingException("Staged snapshot at " + file + " uses unsupported format version " + version + ".");
            }
            String selection = readString(in, MAX_SELECTION_BYTES);
            BlockPos origin = new BlockPos(in.readInt(), in.readInt(), in.readInt());
            int blockCount = readCount(in, MAX_BLOCKS, "block");
            List<BlockSnapshot> blocks = new ArrayList<>(Math.min(blockCount, 1_000_000));
            for (int i = 0; i < blockCount; i++) {
                int x = in.readInt();
                int y = in.readInt();
                int z = in.readInt();
                int rx = in.readInt();
                int ry = in.readInt();
                int rz = in.readInt();
                String blockData = readString(in, MAX_BLOCK_DATA_BYTES);
                String blockEntity = in.readBoolean() ? readString(in, MAX_ENTITY_BYTES) : null;
                blocks.add(new BlockSnapshot(x, y, z, rx, ry, rz, blockData, blockEntity));
            }
            int entityCount = readCount(in, MAX_ENTITIES, "entity");
            List<EntitySnapshot> entities = new ArrayList<>(Math.min(entityCount, 100_000));
            for (int i = 0; i < entityCount; i++) {
                double x = in.readDouble();
                double y = in.readDouble();
                double z = in.readDouble();
                double rx = in.readDouble();
                double ry = in.readDouble();
                double rz = in.readDouble();
                float yaw = in.readFloat();
                float pitch = in.readFloat();
                String type = readString(in, 256);
                String data = readString(in, MAX_ENTITY_BYTES);
                entities.add(new EntitySnapshot(x, y, z, rx, ry, rz, yaw, pitch, type, data));
            }
            return new Snapshot(selection, origin, blocks, entities, Instant.now());
        } catch (EOFException e) {
            throw new StagingException("Staged snapshot at " + file + " is truncated.", e);
        } catch (IOException e) {
            throw new StagingException("Could not read staged snapshot at " + file + ": " + e.getMessage(), e);
        }
    }

    private static int readCount(DataInputStream in, int max, String label) throws IOException {
        int count = in.readInt();
        if (count < 0 || count > max) {
            throw new StagingException("Staged snapshot declares an unreasonable " + label + " count: " + count);
        }
        return count;
    }

    private static void writeString(DataOutputStream out, String value, int maxBytes) throws IOException {
        byte[] bytes = (value == null ? "" : value).getBytes(StandardCharsets.UTF_8);
        if (bytes.length > maxBytes) {
            throw new StagingException("A staging string exceeds the " + maxBytes + "-byte limit.");
        }
        out.writeInt(bytes.length);
        out.write(bytes);
    }

    private static String readString(DataInputStream in, int maxBytes) throws IOException {
        int length = in.readInt();
        if (length < 0 || length > maxBytes) {
            throw new StagingException("Staged snapshot declares an unreasonable string length: " + length);
        }
        byte[] bytes = new byte[length];
        in.readFully(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
