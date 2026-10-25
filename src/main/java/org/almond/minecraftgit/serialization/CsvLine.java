package org.almond.minecraftgit.serialization;

import java.util.ArrayList;
import java.util.List;

/**
 * Minimal RFC-4180 CSV handling. Block data strings contain commas, so every
 * cell is quoted when needed rather than using a fragile split(',').
 */
public final class CsvLine {

    private CsvLine() {
    }

    public static String format(List<String> fields) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < fields.size(); i++) {
            if (i > 0) {
                builder.append(',');
            }
            builder.append(escape(fields.get(i)));
        }
        return builder.toString();
    }

    public static String escape(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        boolean quote = false;
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c == ',' || c == '"' || c == '\n' || c == '\r') {
                quote = true;
                break;
            }
        }
        if (!quote) {
            return value;
        }
        return '"' + value.replace("\"", "\"\"") + '"';
    }

    public static List<String> parse(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder cell = new StringBuilder();
        boolean quoted = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (quoted) {
                if (c == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        cell.append('"');
                        i++;
                    } else {
                        quoted = false;
                    }
                } else {
                    cell.append(c);
                }
            } else {
                if (c == ',') {
                    fields.add(cell.toString());
                    cell.setLength(0);
                } else if (c == '"' && cell.isEmpty()) {
                    quoted = true;
                } else {
                    cell.append(c);
                }
            }
        }
        fields.add(cell.toString());
        return fields;
    }
}
