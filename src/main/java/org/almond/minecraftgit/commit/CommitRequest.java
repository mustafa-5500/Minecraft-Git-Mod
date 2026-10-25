package org.almond.minecraftgit.commit;

/** Immutable inputs for a commit, built on the client thread before IO work starts. */
public record CommitRequest(String playerName, String playerUuid, String worldName, String gameMode,
                            java.util.List<org.almond.minecraftgit.model.Selection> selections,
                            String message, boolean allowEmpty) {
}
