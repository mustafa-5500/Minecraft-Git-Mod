package org.almond.minecraftgit.repo;

import java.time.Instant;

/**
 * Per-world, local-only repository state. This file is never committed: it maps
 * plugin concepts (world name, default remote, author) onto Git concepts and may
 * differ between machines that share the same repository.
 */
public final class RepoMetadata {

    private String world;
    private String branch = "main";
    private String remoteName = "origin";
    private String remoteUrl = "";
    private String authorName = "MinecraftGit";
    private String authorEmail = "minecraftgit@localhost";
    private String lastSelection = "";
    private String createdAt = Instant.now().toString();

    public static RepoMetadata defaults(String world, RepoOptions options) {
        RepoMetadata metadata = new RepoMetadata();
        metadata.world = world;
        metadata.branch = options.branch();
        metadata.remoteName = options.defaultRemote();
        metadata.authorName = options.authorName();
        metadata.authorEmail = options.authorEmail();
        metadata.createdAt = Instant.now().toString();
        return metadata;
    }

    /** Fill in blanks introduced by older or hand-edited metadata files. */
    public RepoMetadata normalize(String world, RepoOptions options) {
        RepoMetadata defaults = defaults(world, options);
        if (this.world == null || this.world.isBlank()) {
            this.world = defaults.world;
        }
        if (branch == null || branch.isBlank()) {
            branch = defaults.branch;
        }
        if (remoteName == null || remoteName.isBlank()) {
            remoteName = defaults.remoteName;
        }
        if (remoteUrl == null) {
            remoteUrl = "";
        }
        if (authorName == null || authorName.isBlank()) {
            authorName = defaults.authorName;
        }
        if (authorEmail == null || authorEmail.isBlank()) {
            authorEmail = defaults.authorEmail;
        }
        if (lastSelection == null) {
            lastSelection = "";
        }
        if (createdAt == null || createdAt.isBlank()) {
            createdAt = defaults.createdAt;
        }
        return this;
    }

    public String world() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public String branch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String remoteName() {
        return remoteName;
    }

    public void setRemoteName(String remoteName) {
        this.remoteName = remoteName;
    }

    public String remoteUrl() {
        return remoteUrl;
    }

    public void setRemoteUrl(String remoteUrl) {
        this.remoteUrl = remoteUrl;
    }

    public String authorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String authorEmail() {
        return authorEmail;
    }

    public void setAuthorEmail(String authorEmail) {
        this.authorEmail = authorEmail;
    }

    public String lastSelection() {
        return lastSelection;
    }

    public void setLastSelection(String lastSelection) {
        this.lastSelection = lastSelection;
    }

    public String createdAt() {
        return createdAt;
    }
}
