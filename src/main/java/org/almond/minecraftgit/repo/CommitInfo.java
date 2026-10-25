package org.almond.minecraftgit.repo;

import java.time.Instant;

/** One row of {@code /git log}. */
public record CommitInfo(String id, String shortId, String author, String email, Instant when, String message) {
}
