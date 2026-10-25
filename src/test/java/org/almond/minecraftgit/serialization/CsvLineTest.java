package org.almond.minecraftgit.serialization;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CsvLineTest {

    @Test
    void handlesCommasQuotesAndEmptyCells() {
        List<String> fields = List.of("minecraft:oak_stairs[facing=north,half=bottom]", "", "say \"hi\", friend");
        String line = CsvLine.format(fields);
        assertEquals(fields, CsvLine.parse(line));
    }

    @Test
    void emptyLineIsOneEmptyField() {
        assertEquals(List.of(""), CsvLine.parse(""));
    }

    @Test
    void quotedNewlinesSurvive() {
        String line = CsvLine.format(List.of("a\nb", "c"));
        assertEquals(List.of("a\nb", "c"), CsvLine.parse(line));
    }
}
