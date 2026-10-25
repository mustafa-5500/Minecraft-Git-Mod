package org.almond.minecraftgit.serialization;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * A CSV cell is either empty (coordinate not tracked), a block data string
 * alone, or block data followed by {@code |nbt|} and Base64-encoded block-entity
 * JSON. Base64 keeps commas, quotes, and newlines out of the CSV layer.
 */
public final class SnapshotCell {

    public static final String ENTITY_SEPARATOR = "|nbt|";

    private SnapshotCell() {
    }

    public static String encode(String blockData, String blockEntityJson) {
        if (blockEntityJson == null || blockEntityJson.isBlank()) {
            return blockData;
        }
        return blockData + ENTITY_SEPARATOR
                + Base64.getEncoder().encodeToString(blockEntityJson.getBytes(StandardCharsets.UTF_8));
    }

    public static Cell decode(String token) {
        int separator = token.indexOf(ENTITY_SEPARATOR);
        if (separator < 0) {
            return new Cell(token, null);
        }
        String blockData = token.substring(0, separator);
        String encoded = token.substring(separator + ENTITY_SEPARATOR.length());
        try {
            String entity = new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);
            return new Cell(blockData, entity.isBlank() ? null : entity);
        } catch (IllegalArgumentException e) {
            throw new SerializationException("Invalid block-entity payload in CSV cell.", e);
        }
    }

    public record Cell(String blockData, String blockEntity) {
    }
}
