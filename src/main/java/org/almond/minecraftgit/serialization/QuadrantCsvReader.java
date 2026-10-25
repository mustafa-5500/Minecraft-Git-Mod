package org.almond.minecraftgit.serialization;

import org.almond.minecraftgit.model.BlockPos;
import org.almond.minecraftgit.model.BlockSnapshot;
import org.almond.minecraftgit.model.EntitySnapshot;
import org.almond.minecraftgit.model.Snapshot;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/** Reads the repository CSV format written by {@link QuadrantCsvWriter}. */
public final class QuadrantCsvReader {

    private static final Pattern BLOCK_HEADER = Pattern.compile(
            "^#\\s+minecraftgit-csv\\s+v1\\s+origin=(-?\\d+),(-?\\d+),(-?\\d+)\\s+y=(-?\\d+)\\s+quadrant=(pp|pn|np|nn)\\s*$");
    private static final Pattern ENTITY_HEADER = Pattern.compile(
            "^#\\s+minecraftgit-entities\\s+v1\\s+origin=(-?\\d+),(-?\\d+),(-?\\d+)\\s*$");

    private QuadrantCsvReader() {
    }

    public static Snapshot readSelection(Path selectionDir, String selectionName, BlockPos fallbackOrigin) {
        if (!Files.isDirectory(selectionDir)) {
            return Snapshot.empty(selectionName, fallbackOrigin == null ? BlockPos.ORIGIN : fallbackOrigin);
        }

        List<BlockSnapshot> blocks = new ArrayList<>();
        List<EntitySnapshot> entities = new ArrayList<>();
        BlockPos[] origin = {null};

        try (Stream<Path> files = Files.walk(selectionDir, 2)) {
            List<Path> csvFiles = files.filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".csv"))
                    .filter(path -> !path.getFileName().toString().equals("entities.csv"))
                    .sorted()
                    .toList();
            for (Path file : csvFiles) {
                readBlockFile(file, blocks, origin);
            }
            Path entitiesFile = selectionDir.resolve("entities.csv");
            if (Files.isRegularFile(entitiesFile)) {
                readEntityFile(entitiesFile, entities, origin);
            }
        } catch (IOException e) {
            throw new SerializationException("Could not read selection at " + selectionDir + ": " + e.getMessage(), e);
        }

        BlockPos resolvedOrigin = origin[0] != null ? origin[0] : (fallbackOrigin == null ? BlockPos.ORIGIN : fallbackOrigin);
        return new Snapshot(selectionName, resolvedOrigin, blocks, entities, Instant.now());
    }

    private static void readBlockFile(Path file, List<BlockSnapshot> blocks, BlockPos[] origin) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                throw new SerializationException("Empty CSV file: " + file);
            }
            Matcher header = BLOCK_HEADER.matcher(headerLine);
            if (!header.matches()) {
                throw new SerializationException("Unrecognized block CSV header in " + file + ": " + headerLine);
            }
            BlockPos fileOrigin = new BlockPos(Integer.parseInt(header.group(1)),
                    Integer.parseInt(header.group(2)), Integer.parseInt(header.group(3)));
            int y = Integer.parseInt(header.group(4));
            Quadrant quadrant = Quadrant.fromCode(header.group(5));
            mergeOrigin(origin, fileOrigin, file);

            String line;
            int row = 0;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    row++;
                    continue;
                }
                List<String> cells = CsvLine.parse(line);
                for (int column = 0; column < cells.size(); column++) {
                    String token = cells.get(column);
                    if (token.isEmpty()) {
                        continue;
                    }
                    SnapshotCell.Cell cell = SnapshotCell.decode(token);
                    int rx = quadrant.rowToX(row);
                    int rz = quadrant.columnToZ(column);
                    BlockPos absolute = fileOrigin.add(rx, y, rz);
                    blocks.add(BlockSnapshot.at(absolute, fileOrigin, cell.blockData(), cell.blockEntity()));
                }
                row++;
            }
        } catch (NumberFormatException e) {
            throw new SerializationException("Invalid number in CSV file " + file, e);
        }
    }

    private static void readEntityFile(Path file, List<EntitySnapshot> entities, BlockPos[] origin) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                return;
            }
            Matcher header = ENTITY_HEADER.matcher(headerLine);
            if (!header.matches()) {
                throw new SerializationException("Unrecognized entity CSV header in " + file + ": " + headerLine);
            }
            BlockPos fileOrigin = new BlockPos(Integer.parseInt(header.group(1)),
                    Integer.parseInt(header.group(2)), Integer.parseInt(header.group(3)));
            mergeOrigin(origin, fileOrigin, file);

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                List<String> fields = CsvLine.parse(line);
                if (fields.size() < 7) {
                    throw new SerializationException("Entity row needs 7 columns in " + file + ": " + line);
                }
                double rx = Double.parseDouble(fields.get(0));
                double ry = Double.parseDouble(fields.get(1));
                double rz = Double.parseDouble(fields.get(2));
                float yaw = Float.parseFloat(fields.get(3));
                float pitch = Float.parseFloat(fields.get(4));
                String type = fields.get(5);
                String data;
                try {
                    data = new String(Base64.getDecoder().decode(fields.get(6)), StandardCharsets.UTF_8);
                } catch (IllegalArgumentException e) {
                    throw new SerializationException("Invalid entity payload in " + file, e);
                }
                entities.add(new EntitySnapshot(fileOrigin.x() + rx, fileOrigin.y() + ry, fileOrigin.z() + rz,
                        rx, ry, rz, yaw, pitch, type, data));
            }
        } catch (NumberFormatException e) {
            throw new SerializationException("Invalid number in entity CSV " + file, e);
        }
    }

    private static void mergeOrigin(BlockPos[] origin, BlockPos fileOrigin, Path file) {
        if (origin[0] == null) {
            origin[0] = fileOrigin;
        } else if (!origin[0].equals(fileOrigin)) {
            throw new SerializationException("Mismatched origin in " + file + ": " + fileOrigin
                    + " (expected " + origin[0] + "). The selection may have been partially re-origined.");
        }
    }
}
