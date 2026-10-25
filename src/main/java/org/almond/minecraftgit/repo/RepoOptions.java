package org.almond.minecraftgit.repo;

/**
 * Immutable repository configuration used by {@link RepoService}. Keeping this
 * free of Minecraft types lets the whole repository layer run in plain JVM tests.
 */
public record RepoOptions(String branch, String defaultRemote, String authorName, String authorEmail) {

    public RepoOptions {
        branch = blankTo(branch, "main");
        defaultRemote = blankTo(defaultRemote, "origin");
        authorName = blankTo(authorName, "MinecraftGit");
        authorEmail = blankTo(authorEmail, "minecraftgit@localhost");
    }

    private static String blankTo(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}
