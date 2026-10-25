package org.almond.minecraftgit.repo;

import java.nio.file.Path;
import java.time.Instant;

/** Snapshot of a repository's state for {@code /git repo}. */
public record RepoInfo(Path path, String branch, String headId, String headSummary,
                       String remoteName, String remoteUrl, String authorName, String authorEmail,
                       boolean hasCommits, boolean clean) {
}
