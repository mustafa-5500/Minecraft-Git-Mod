package org.almond.minecraftgit.util;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Selection names become directory names inside a Git repository, so they are
 * restricted instead of being sanitized after the fact. World/player ids are
 * sanitized because the player never types them.
 */
public final class Names {

    private static final Pattern SELECTION = Pattern.compile("[A-Za-z0-9][A-Za-z0-9_-]{0,31}");
    private static final Pattern WORLD = Pattern.compile("[^A-Za-z0-9_.-]");

    private Names() {
    }

    public static boolean isValidSelectionName(String name) {
        return name != null && SELECTION.matcher(name).matches();
    }

    public static String requireSelectionName(String name) {
        if (!isValidSelectionName(name)) {
            throw new IllegalArgumentException(
                    "Selection names must be 1-32 characters of letters, digits, '_' or '-', "
                            + "starting with a letter or digit.");
        }
        return name;
    }

    public static String safeWorldName(String world) {
        return WORLD.matcher(world == null ? "world" : world).replaceAll("_");
    }

    public static String lower(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT);
    }

    public static String shorten(String value, int max) {
        if (value == null) {
            return "";
        }
        return value.length() <= max ? value : value.substring(0, max) + "...";
    }
}
