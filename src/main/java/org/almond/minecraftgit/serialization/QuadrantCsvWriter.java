package org.almond.minecraftgit.serialization;

import org.almond.minecraftgit.model.BlockPos;
import org.almond.minecraftgit.model.BlockSnapshot;
import org.almond.minecraftgit.model.EntitySnapshot;
import org.almond.minecraftgit.model.Snapshot;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

/**
 * Serializes a snapshot into the repository format:
 *
 * <pre>
 * &lt;selection&gt;/y_&lt;y&gt;/pp.csv
 * &lt;selection&gt;/y_&lt;y&gt;/pn.csv
 * &lt;selection&gt;/y_&lt;y&gt;/np.csv
 * &lt;selection&gt;/y_&lt;y&gt;/nn.csv
 * &lt;selection&gt;/entities.csv
 * </pre>
 *
 * Each block CSV has one header line and then one row per relative X, with one
 * column per relative Z. Empty cells mean "not tracked", which is distinct from
 * a cell containing {@code minecraft:air} (tracked and currently air).
 */
public final class QuadrantCsvWriter {

    private QuadrantCsvWriter() {
    }

    public static void writeSelection(Path selectionDir, Snapshot snapshot) {
        try {
            Files.createDirectories(selectionDir);
            cleanPrevious(selectionDir);
            if (snapshot.blocks().isEmpty() && snapshot.entities().isEmpty()) {
                return;
            }
            Map<Integer, Map<Quadrant, Map<CellKey, BlockSnapshot>>> byY = new TreeMap<>();
            for (BlockSnapshot block : snapshot.blocks()) {
                Quadrant quadrant = Quadrant.fromOffsets(block.rx(), block.rz());
                byY.computeIfAbsent(block.ry(), ignored -> new EnumMap<>(Quadrant.class))
                        .computeIfAbsent(quadrant, ignored -> new HashMap<>())
                        .put(new CellKey(block.rx(), block.rz()), block);
            }
            BlockPos origin = snapshot.origin();
            for (Map.Entry<Integer, Map<Quadrant, Map<CellKey, BlockSnapshot>>> yEntry : byY.entrySet()) {
                int y = yEntry.getKey();
                Path yDir = selectionDir.resolve("y_" + y);
                Files.createDirectories(yDir);
                for (Quadrant quadrant : Quadrant.values()) {
                    Map<CellKey, BlockSnapshot> cells = yEntry.getValue().get(quadrant);
                    if (cells == null || cells.isEmpty()) {
                        continue;
                    }
                    writeQuadrant(yDir.resolve(quadrant.code() + ".csv"), origin, y, quadrant, cells);
                }
            }
            if (!snapshot.entities().isEmpty()) {
                writeEntities(selectionDir.resolve("entities.csv"), origin, snapshot.entities());
            }
        } catch (IOException e) {
            throw new SerializationException("Could not write selection '" + snapshot.selection()
                    + "' at " + selectionDir + ": " + e.getMessage(), e);
        }
    }

    private static void writeQuadrant(Path file, BlockPos origin, int y, Quadrant quadrant,
                                      Map<CellKey, BlockSnapshot> cells) throws IOException {
        int maxRow = 0;
        int maxColumn = 0;
        for (BlockSnapshot block : cells.values()) {
            maxRow = Math.max(maxRow, quadrant.rowForX(block.rx()));
            maxColumn = Math.max(maxColumn, quadrant.columnForZ(block.rz()));
        }
        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            writeLine(writer, "# minecraftgit-csv v1 origin=" + origin.x() + "," + origin.y() + "," + origin.z()
                    + " y=" + y + " quadrant=" + quadrant.code());
            List<String> row = new ArrayList<>(maxColumn + 1);
            for (int rowIndex = 0; rowIndex <= maxRow; rowIndex++) {
                row.clear();
                for (int column = 0; column <= maxColumn; column++) {
                    BlockSnapshot block = cells.get(new CellKey(quadrant.rowToX(rowIndex), quadrant.columnToZ(column)));
                    row.add(block == null ? "" : SnapshotCell.encode(block.blockData(), block.blockEntity()));
                }
                writeLine(writer, CsvLine.format(row));
            }
        }
    }

    private static void writeEntities(Path file, BlockPos origin, List<EntitySnapshot> entities) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            writeLine(writer, "# minecraftgit-entities v1 origin=" + origin.x() + "," + origin.y() + "," + origin.z());
            for (EntitySnapshot entity : entities) {
                String encoded = Base64.getEncoder().encodeToString(entity.data().getBytes(StandardCharsets.UTF_8));
                writeLine(writer, CsvLine.format(List.of(
                        Double.toString(entity.rx()),
                        Double.toString(entity.ry()),
                        Double.toString(entity.rz()),
                        Float.toString(entity.yaw()),
                        Float.toString(entity.pitch()),
                        entity.type(),
                        encoded)));
            }
        }
    }

    private static void writeLine(BufferedWriter writer, String line) throws IOException {
        writer.write(line);
        writer.write('\n');
    }

    /** Remove generated CSV files while preserving a later-written meta.json. */
    private static void cleanPrevious(Path selectionDir) throws IOException {
        try (Stream<Path> children = Files.list(selectionDir)) {
            for (Path child : children.toList()) {
                String name = child.getFileName().toString();
                if (name.equals("entities.csv") || name.matches("y_-?\\d+")) {
                    deleteRecursively(child);
                }
            }
        }
    }

    private static void deleteRecursively(Path path) throws IOException {
        if (!Files.exists(path)) {
            return;
        }
        try (Stream<Path> walk = Files.walk(path)) {
            for (Path entry : walk.sorted(Comparator.reverseOrder()).toList()) {
                Files.deleteIfExists(entry);
            }
        }
    }

    private record CellKey(int rx, int rz) {
    }
}
