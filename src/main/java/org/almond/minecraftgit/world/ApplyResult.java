package org.almond.minecraftgit.world;

import java.nio.file.Path;
import java.util.List;

/** Result of applying a plan to the world. */
public record ApplyResult(int placed, int removed, int skipped, int entitiesPlaced,
                          int entitiesRemoved, Path backup, List<String> warnings) {

    public ApplyResult {
        warnings = List.copyOf(warnings);
    }

    public int changed() {
        return placed + removed;
    }

    public String summary() {
        return placed + " placed, " + removed + " removed"
                + (skipped > 0 ? ", " + skipped + " skipped" : "")
                + (entitiesPlaced + entitiesRemoved > 0
                ? ", entities +" + entitiesPlaced + "/-" + entitiesRemoved : "");
    }
}
