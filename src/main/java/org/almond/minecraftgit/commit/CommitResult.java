package org.almond.minecraftgit.commit;

import java.util.List;

/** Result of a successful commit. */
public record CommitResult(String commitId, String shortId, List<String> selections,
                           long added, long changed, long removed, long entityChanges,
                           String message) {
}
