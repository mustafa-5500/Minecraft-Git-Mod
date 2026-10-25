package org.almond.minecraftgit.repo;

import org.almond.minecraftgit.debug.DebugLogger;
import org.almond.minecraftgit.util.Names;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.FileMode;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.RemoteRefUpdate;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.treewalk.TreeWalk;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

/**
 * Facade over JGit for one repository. All public methods are serialized by a
 * lock because a JGit {@link Repository} is not safe for concurrent mutation.
 * The world is never touched here; repository I/O is pure file/network work.
 */
public final class RepoService implements AutoCloseable {

    private final Path repoPath;
    private final RepoOptions options;
    private final DebugLogger debug;
    private final ReentrantLock lock = new ReentrantLock();

    private Git git;
    private Repository repository;
    private String authorName;
    private String authorEmail;

    public RepoService(Path repoPath, RepoOptions options, DebugLogger debug) {
        this.repoPath = repoPath.toAbsolutePath().normalize();
        this.options = options;
        this.debug = debug;
        this.authorName = options.authorName();
        this.authorEmail = options.authorEmail();
    }

    public Path path() {
        return repoPath;
    }

    public boolean isInitialized() {
        return Files.isDirectory(repoPath.resolve(Constants.DOT_GIT));
    }

    /**
     * Create the repository if needed (with a README and format documentation
     * commit), otherwise open it.
     */
    public RepoInfo init() {
        lock.lock();
        try {
            if (isInitialized()) {
                ensureOpenLocked();
                return infoLocked();
            }
            Files.createDirectories(repoPath);
            Git created = Git.init()
                    .setDirectory(repoPath.toFile())
                    .setInitialBranch(options.branch())
                    .call();
            this.git = created;
            this.repository = created.getRepository();
            configureAuthorLocked();
            writeBootstrapFiles();
            stageAllLocked();
            created.commit()
                    .setMessage("Initialize MinecraftGit repository\n\n"
                            + "Selection data is stored as four quadrant CSV files per Y level.\n")
                    .setAuthor(identityLocked())
                    .setCommitter(identityLocked())
                    .call();
            debug.log("repo", "Initialized repository at %s on branch %s", repoPath, currentBranchLocked());
            return infoLocked();
        } catch (IOException | GitAPIException e) {
            throw error("initialize repository at " + repoPath, e);
        } finally {
            lock.unlock();
        }
    }

    /** Open the repository if it is not already open. */
    public void open() {
        lock.lock();
        try {
            ensureOpenLocked();
        } finally {
            lock.unlock();
        }
    }

    private void ensureOpenLocked() {
        if (git != null && repository != null) {
            return;
        }
        if (!isInitialized()) {
            throw new RepoException("No Git repository exists at " + repoPath + ". Run /git init first.");
        }
        try {
            this.git = Git.open(repoPath.toFile());
            this.repository = git.getRepository();
            configureAuthorLocked();
            debug.log("repo", "Opened repository at %s", repoPath);
        } catch (IOException e) {
            throw error("open repository at " + repoPath, e);
        }
    }

    private void writeBootstrapFiles() throws IOException {
        Path readme = repoPath.resolve("README.md");
        if (!Files.exists(readme)) {
            Files.writeString(readme, """
                    # MinecraftGit repository

                    This repository is managed by the MinecraftGit plugin. Each named
                    selection is stored as text so normal Git tooling can inspect history:

                    ```
                    <selection>/y_<y>/<quadrant>.csv
                    <selection>/meta.json
                    ```

                    Quadrants are `pp` (+x,+z), `pn` (+x,-z), `np` (-x,+z), and `nn`
                    (-x,-z). Each CSV starts with a header recording the format version,
                    absolute origin, and Y level. Relative coordinates are measured from
                    the selection origin; rows are X and columns are Z, growing away from
                    the origin.

                    Do not edit the generated files by hand unless you also understand the
                    format in `Documentation/Git-Format.md`.
                    """, StandardCharsets.UTF_8);
        }
        Path attributes = repoPath.resolve(".gitattributes");
        if (!Files.exists(attributes)) {
            Files.writeString(attributes, "*.csv text eol=lf\n*.json text eol=lf\n", StandardCharsets.UTF_8);
        }
    }

    public RepoInfo info() {
        lock.lock();
        try {
            ensureOpenLocked();
            return infoLocked();
        } finally {
            lock.unlock();
        }
    }

    private RepoInfo infoLocked() {
        try {
            StoredConfig config = repository.getConfig();
            ObjectId head = repository.resolve(Constants.HEAD);
            String branch = currentBranchLocked();
            String headSummary = "";
            if (head != null) {
                try (var walk = new org.eclipse.jgit.revwalk.RevWalk(repository)) {
                    RevCommit commit = walk.parseCommit(head);
                    headSummary = commit.getShortMessage();
                }
            }
            String remoteName = config.getString("branch", branch, "remote");
            if (remoteName == null || remoteName.isBlank()) {
                remoteName = options.defaultRemote();
            }
            String remoteUrl = config.getString("remote", remoteName, "url");
            if (remoteUrl == null) {
                remoteUrl = "";
            }
            return new RepoInfo(repoPath, branch, head == null ? "" : head.name(), headSummary,
                    remoteName, remoteUrl, authorName, authorEmail, head != null, statusLocked().isClean());
        } catch (IOException | GitAPIException e) {
            throw error("read repository information", e);
        }
    }

    /** Current branch name, or an empty string for a detached HEAD. */
    public String currentBranch() {
        lock.lock();
        try {
            ensureOpenLocked();
            return currentBranchLocked();
        } finally {
            lock.unlock();
        }
    }

    private String currentBranchLocked() {
        try {
            String branch = repository.getBranch();
            return ObjectId.isId(branch) ? "" : branch;
        } catch (IOException e) {
            throw error("read current branch", e);
        }
    }

    public Optional<String> headId() {
        lock.lock();
        try {
            ensureOpenLocked();
            ObjectId head = repository.resolve(Constants.HEAD);
            return head == null ? Optional.empty() : Optional.of(head.name());
        } catch (IOException e) {
            throw error("resolve HEAD", e);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Stage the whole working tree and commit it.
     *
     * @return the new commit id, or {@code null} when there was nothing to commit
     *         and {@code allowEmpty} is false
     */
    public String commitAll(String message, boolean allowEmpty) {
        lock.lock();
        try {
            ensureOpenLocked();
            stageAllLocked();
            Status status = statusLocked();
            if (status.isClean() && !allowEmpty) {
                return null;
            }
            RevCommit commit = git.commit()
                    .setMessage(message)
                    .setAuthor(identityLocked())
                    .setCommitter(identityLocked())
                    .setAllowEmpty(allowEmpty)
                    .call();
            if (commit == null) {
                return null;
            }
            debug.log("repo", "Committed %s: %s", commit.abbreviate(8).name(), firstLine(message));
            return commit.name();
        } catch (GitAPIException e) {
            throw error("commit changes", e);
        } finally {
            lock.unlock();
        }
    }

    /** Create an empty commit, useful for verifying a configured remote. */
    public String commitEmpty(String message) {
        return commitAll(message, true);
    }

    /**
     * Replace whole selection directories and commit the result atomically from
     * the caller's perspective. Paths are relative to the repository root and
     * are rejected if they escape it.
     *
     * @param files                  relative path to UTF-8 bytes
     * @param directoriesToReplace   directories removed before {@code files} are written
     * @return the commit id, or {@code null} when nothing changed and
     *         {@code allowEmpty} is false
     */
    public String commitWorkspace(Map<String, byte[]> files, Collection<String> directoriesToReplace,
                                  String message, boolean allowEmpty) {
        lock.lock();
        try {
            ensureOpenLocked();
            for (String directory : directoriesToReplace) {
                deleteRecursively(safeResolve(directory));
            }
            for (Map.Entry<String, byte[]> entry : files.entrySet()) {
                Path target = safeResolve(entry.getKey());
                Files.createDirectories(target.getParent());
                Files.write(target, entry.getValue());
            }
            stageAllLocked();
            Status status = statusLocked();
            if (status.isClean() && !allowEmpty) {
                return null;
            }
            RevCommit commit = git.commit()
                    .setMessage(message)
                    .setAuthor(identityLocked())
                    .setCommitter(identityLocked())
                    .setAllowEmpty(allowEmpty)
                    .call();
            if (commit == null) {
                return null;
            }
            debug.log("repo", "Committed %s: %s", commit.abbreviate(8).name(), firstLine(message));
            return commit.name();
        } catch (IOException | GitAPIException e) {
            throw error("commit workspace changes", e);
        } finally {
            lock.unlock();
        }
    }

    private Path safeResolve(String relative) {
        Path target = repoPath.resolve(relative).normalize();
        if (!target.startsWith(repoPath) || target.equals(repoPath)) {
            throw new RepoException("Refusing to write outside the repository: " + relative);
        }
        return target;
    }

    private static void deleteRecursively(Path path) throws IOException {
        if (!Files.exists(path)) {
            return;
        }
        try (Stream<Path> walk = Files.walk(path)) {
            for (Path entry : walk.sorted(Comparator.reverseOrder()).toList()) {
                Files.deleteIfExists(entry);
            }
        }
    }

    private void stageAllLocked() {
        try {
            git.add().addFilepattern(".").call();
            // The second pass stages deletions as well as modifications.
            git.add().setUpdate(true).addFilepattern(".").call();
        } catch (GitAPIException e) {
            throw error("stage files", e);
        }
    }

    private Status statusLocked() throws GitAPIException {
        return git.status().call();
    }

    public Optional<CommitInfo> headCommit() {
        return log(1).stream().findFirst();
    }

    public List<CommitInfo> log(int limit) {
        lock.lock();
        try {
            ensureOpenLocked();
            if (repository.resolve(Constants.HEAD) == null) {
                return List.of();
            }
            List<CommitInfo> result = new ArrayList<>();
            for (RevCommit commit : git.log().setMaxCount(Math.max(1, limit)).call()) {
                PersonIdent author = commit.getAuthorIdent() == null ? commit.getCommitterIdent() : commit.getAuthorIdent();
                String name = author == null ? "unknown" : author.getName();
                String email = author == null ? "" : author.getEmailAddress();
                result.add(new CommitInfo(commit.name(), commit.abbreviate(8).name(), name, email,
                        author == null ? java.time.Instant.EPOCH : author.getWhenAsInstant(),
                        firstLine(commit.getFullMessage())));
            }
            return result;
        } catch (NoHeadException e) {
            return List.of();
        } catch (GitAPIException | IOException e) {
            throw error("read commit log", e);
        } finally {
            lock.unlock();
        }
    }

    public void setAuthor(String name, String email) {
        lock.lock();
        try {
            if (name == null || name.isBlank() || email == null || email.isBlank()) {
                throw new RepoException("Author name and email must not be blank.");
            }
            this.authorName = name;
            this.authorEmail = email;
            if (isInitialized()) {
                ensureOpenLocked();
                configureAuthorLocked();
            }
        } finally {
            lock.unlock();
        }
    }

    public String authorName() {
        return authorName;
    }

    public String authorEmail() {
        return authorEmail;
    }

    private void configureAuthorLocked() {
        try {
            StoredConfig config = repository.getConfig();
            config.setString("user", null, "name", authorName);
            config.setString("user", null, "email", authorEmail);
            config.save();
        } catch (IOException e) {
            throw error("save repository author configuration", e);
        }
    }

    private PersonIdent identityLocked() {
        return new PersonIdent(authorName, authorEmail);
    }

    public void remoteAdd(String name, String url) {
        lock.lock();
        try {
            ensureOpenLocked();
            requireValidRemoteName(name);
            if (url == null || url.isBlank()) {
                throw new RepoException("Remote URL must not be blank.");
            }
            git.remoteAdd().setName(name).setUri(toUri(url)).call();
            StoredConfig config = repository.getConfig();
            if (config.getStringList("remote", name, "fetch").length == 0) {
                config.setStringList("remote", name, "fetch",
                        List.of("+refs/heads/*:refs/remotes/" + name + "/*"));
                config.save();
            }
            debug.log("repo", "Added remote %s -> %s", name, url);
        } catch (URISyntaxException e) {
            throw new RepoException("That remote URL is not valid: " + e.getMessage(), e);
        } catch (GitAPIException | IOException e) {
            throw error("add remote " + name, e);
        } finally {
            lock.unlock();
        }
    }

    public void remoteRemove(String name) {
        lock.lock();
        try {
            ensureOpenLocked();
            requireValidRemoteName(name);
            git.remoteRemove().setRemoteName(name).call();
            debug.log("repo", "Removed remote %s", name);
        } catch (GitAPIException e) {
            throw error("remove remote " + name, e);
        } finally {
            lock.unlock();
        }
    }

    public List<RemoteInfo> remoteList() {
        lock.lock();
        try {
            ensureOpenLocked();
            List<RemoteInfo> result = new ArrayList<>();
            for (RemoteConfig remote : git.remoteList().call()) {
                String url = remote.getURIs().isEmpty() ? "" : remote.getURIs().get(0).toString();
                result.add(new RemoteInfo(remote.getName(), url));
            }
            return result;
        } catch (GitAPIException e) {
            throw error("list remotes", e);
        } finally {
            lock.unlock();
        }
    }

    public Optional<RemoteInfo> remote(String name) {
        return remoteList().stream().filter(remote -> remote.name().equals(name)).findFirst();
    }

    public FetchSummary fetch(String remote) {
        return fetch(remote, RepoProgress.NOOP);
    }

    public FetchSummary fetch(String remote, RepoProgress progress) {
        lock.lock();
        try {
            ensureOpenLocked();
            FetchResult result = fetchLocked(remote, progress);
            int count = result.getTrackingRefUpdates().size();
            return new FetchSummary(remote, count, result.getMessages());
        } catch (GitAPIException e) {
            throw error("fetch from " + remote, e);
        } finally {
            lock.unlock();
        }
    }

    private FetchResult fetchLocked(String remote, RepoProgress progress) throws GitAPIException {
        requireValidRemoteName(remote);
        requireRemoteLocked(remote);
        FetchResult result = git.fetch()
                .setRemote(remote)
                .setRefSpecs(new RefSpec("+refs/heads/*:refs/remotes/" + remote + "/*"))
                .setRemoveDeletedRefs(true)
                .setProgressMonitor(JGitProgressMonitor.of(progress))
                .call();
        debug.log("repo", "Fetched %d ref update(s) from %s", result.getTrackingRefUpdates().size(), remote);
        return result;
    }

    public PushSummary push(String remote, boolean force) {
        return push(remote, force, RepoProgress.NOOP);
    }

    public PushSummary push(String remote, boolean force, RepoProgress progress) {
        lock.lock();
        try {
            ensureOpenLocked();
            requireValidRemoteName(remote);
            requireRemoteLocked(remote);
            String branch = currentBranchLocked();
            if (branch.isBlank()) {
                throw new RepoException("Cannot push from a detached HEAD; check out a branch first.");
            }
            if (repository.resolve(Constants.HEAD) == null) {
                throw new RepoException("There is nothing to push yet; make a commit first.");
            }
            String refspec = "refs/heads/" + branch + ":refs/heads/" + branch;
            Iterable<PushResult> results = git.push()
                    .setRemote(remote)
                    .setRefSpecs(new RefSpec(refspec))
                    .setForce(force)
                    .setProgressMonitor(JGitProgressMonitor.of(progress))
                    .call();
            int updated = 0;
            boolean upToDate = false;
            List<String> rejections = new ArrayList<>();
            for (PushResult result : results) {
                for (RemoteRefUpdate update : result.getRemoteUpdates()) {
                    switch (update.getStatus()) {
                        case OK -> updated++;
                        case UP_TO_DATE -> upToDate = true;
                        case REJECTED_NONFASTFORWARD, REJECTED_REMOTE_CHANGED, REJECTED_OTHER_REASON ->
                                rejections.add(update.getRemoteName() + ": " + update.getMessage());
                        default -> {
                        }
                    }
                }
            }
            if (!rejections.isEmpty()) {
                throw new RepoException("Push rejected. " + String.join("; ", rejections)
                        + " (fetch and pull first, or use --force if you intend to overwrite).");
            }
            StoredConfig config = repository.getConfig();
            config.setString("branch", branch, "remote", remote);
            config.setString("branch", branch, "merge", "refs/heads/" + branch);
            config.save();
            debug.log("repo", "Pushed %d ref update(s) to %s (upToDate=%s)", updated, remote, upToDate);
            return new PushSummary(remote, branch, updated, upToDate || updated == 0);
        } catch (IOException | GitAPIException e) {
            throw error("push to " + remote, e);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Fetch and classify the relationship between the local branch and its
     * remote. The caller applies the remote snapshot to the world before calling
     * {@link #fastForward(PullPlan)}, so a failed world write does not move Git.
     */
    public PullPlan planPull(String remote, RepoProgress progress) {
        lock.lock();
        try {
            ensureOpenLocked();
            FetchResult fetched = fetchLocked(remote, progress);
            String branch = currentBranchLocked();
            if (branch.isBlank()) {
                throw new RepoException("Cannot pull into a detached HEAD; check out a branch first.");
            }
            ObjectId remoteHead = repository.resolve("refs/remotes/" + remote + "/" + branch);
            if (remoteHead == null) {
                throw new RepoException("Remote '" + remote + "' has no branch '" + branch
                        + "'. Push a commit from the other world first.");
            }
            ObjectId localHead = repository.resolve(Constants.HEAD);
            int fetchedRefs = fetched.getTrackingRefUpdates().size();
            if (localHead == null) {
                return new PullPlan(remote, branch, null, remoteHead, null,
                        PullPlan.Type.FAST_FORWARD, fetchedRefs);
            }
            if (localHead.equals(remoteHead)) {
                return new PullPlan(remote, branch, localHead, remoteHead, localHead,
                        PullPlan.Type.UP_TO_DATE, fetchedRefs);
            }
            try (RevWalk walk = new RevWalk(repository)) {
                RevCommit local = walk.parseCommit(localHead);
                RevCommit remoteCommit = walk.parseCommit(remoteHead);
                boolean localIsAncestor = walk.isMergedInto(local, remoteCommit);
                boolean remoteIsAncestor = walk.isMergedInto(remoteCommit, local);
                ObjectId mergeBase = null;
                try {
                    walk.reset();
                    walk.setRevFilter(RevFilter.MERGE_BASE);
                    walk.markStart(local);
                    walk.markStart(remoteCommit);
                    RevCommit base = walk.next();
                    walk.setRevFilter(RevFilter.ALL);
                    if (base != null) {
                        mergeBase = base;
                    }
                } catch (RuntimeException | IOException ignored) {
                    // Unrelated histories have no merge base.
                }
                PullPlan.Type type;
                if (localIsAncestor) {
                    type = PullPlan.Type.FAST_FORWARD;
                } else if (remoteIsAncestor) {
                    type = PullPlan.Type.UP_TO_DATE;
                } else if (isPristineInitLocked(local)) {
                    // A freshly initialized world has an unrelated scaffold commit
                    // (README + .gitattributes) but no build data. Treat it as an
                    // unborn branch so pulling a build into a new world works.
                    type = PullPlan.Type.FAST_FORWARD;
                    mergeBase = null;
                } else {
                    type = PullPlan.Type.DIVERGED;
                }
                return new PullPlan(remote, branch, localHead, remoteHead, mergeBase, type, fetchedRefs);
            }
        } catch (IOException | GitAPIException e) {
            throw error("plan pull from " + remote, e);
        } finally {
            lock.unlock();
        }
    }

    /** Move the local branch to the plan's remote head after a successful apply. */
    public boolean fastForward(PullPlan plan) {
        lock.lock();
        try {
            ensureOpenLocked();
            if (!plan.canFastForward()) {
                return false;
            }
            String branch = currentBranchLocked();
            if (!branch.equals(plan.branch())) {
                throw new RepoException("The checked-out branch changed during the pull; try again.");
            }
            git.reset()
                    .setMode(ResetCommand.ResetType.HARD)
                    .setRef(plan.remoteHead().name())
                    .call();
            debug.log("repo", "Fast-forwarded %s to %s", branch, plan.remoteHead().name());
            return true;
        } catch (GitAPIException e) {
            throw error("fast-forward to " + plan.remoteHead().name(), e);
        } finally {
            lock.unlock();
        }
    }

    private boolean isPristineInitLocked(RevCommit commit) throws IOException {
        if (!commit.getShortMessage().startsWith("Initialize MinecraftGit repository")) {
            return false;
        }
        try (TreeWalk walk = new TreeWalk(repository)) {
            walk.addTree(commit.getTree());
            walk.setRecursive(true);
            while (walk.next()) {
                String path = walk.getPathString();
                if (!path.equals("README.md") && !path.equals(".gitattributes")) {
                    return false;
                }
            }
        }
        return true;
    }

    public Optional<ObjectId> headObject() {
        lock.lock();
        try {
            ensureOpenLocked();
            return Optional.ofNullable(repository.resolve(Constants.HEAD));
        } catch (IOException e) {
            throw error("resolve HEAD", e);
        } finally {
            lock.unlock();
        }
    }

    public Optional<ObjectId> resolve(String revision) {
        lock.lock();
        try {
            ensureOpenLocked();
            return Optional.ofNullable(repository.resolve(revision));
        } catch (IOException e) {
            throw error("resolve " + revision, e);
        } finally {
            lock.unlock();
        }
    }

    /** Selection names that exist in a commit's tree. */
    public Set<String> listSelectionsAt(ObjectId commitId) {
        lock.lock();
        try {
            ensureOpenLocked();
            return listSelectionsAtLocked(commitId);
        } catch (IOException e) {
            throw error("read selection list at " + commitId.name(), e);
        } finally {
            lock.unlock();
        }
    }

    private Set<String> listSelectionsAtLocked(ObjectId commitId) throws IOException {
        Set<String> names = new LinkedHashSet<>();
        try (RevWalk revWalk = new RevWalk(repository)) {
            RevCommit commit = revWalk.parseCommit(commitId);
            try (TreeWalk walk = new TreeWalk(repository)) {
                walk.addTree(commit.getTree());
                walk.setRecursive(true);
                while (walk.next()) {
                    String path = walk.getPathString();
                    int slash = path.indexOf('/');
                    if (slash > 0) {
                        String candidate = path.substring(0, slash);
                        if (Names.isValidSelectionName(candidate)) {
                            names.add(candidate);
                        }
                    }
                }
            }
        }
        return names;
    }

    /**
     * Write one selection's files from a commit into {@code target}. Used by pull
     * and checkout to apply a snapshot without disturbing the repository worktree.
     */
    public boolean exportSelectionAt(ObjectId commitId, String selection, Path target) {
        lock.lock();
        try {
            ensureOpenLocked();
            String requiredName;
            try {
                requiredName = Names.requireSelectionName(selection);
            } catch (IllegalArgumentException e) {
                throw new RepoException(e.getMessage(), e);
            }
            deleteRecursively(target);
            Files.createDirectories(target);
            String prefix = requiredName + "/";
            boolean found = false;
            try (RevWalk revWalk = new RevWalk(repository)) {
                RevCommit commit = revWalk.parseCommit(commitId);
                try (TreeWalk walk = new TreeWalk(repository)) {
                    walk.addTree(commit.getTree());
                    walk.setRecursive(true);
                    while (walk.next()) {
                        if (walk.getFileMode(0) != FileMode.REGULAR_FILE
                                && walk.getFileMode(0) != FileMode.EXECUTABLE_FILE) {
                            continue;
                        }
                        String path = walk.getPathString();
                        if (!path.startsWith(prefix)) {
                            continue;
                        }
                        String relative = path.substring(prefix.length());
                        Path output = target.resolve(relative).normalize();
                        if (!output.startsWith(target)) {
                            throw new RepoException("Refusing to export suspicious path " + path);
                        }
                        Files.createDirectories(output.getParent());
                        ObjectLoader loader = repository.open(walk.getObjectId(0));
                        Files.write(output, loader.getBytes());
                        found = true;
                    }
                }
            }
            return found;
        } catch (IOException e) {
            throw error("export selection " + selection + " at " + commitId.name(), e);
        } finally {
            lock.unlock();
        }
    }

    private void requireRemoteLocked(String name) {
        String url = repository.getConfig().getString("remote", name, "url");
        if (url == null || url.isBlank()) {
            throw new RepoException("Remote '" + name + "' is not configured. Use /git remote add " + name + " <url>.");
        }
    }

    private static void requireValidRemoteName(String name) {
        if (name == null || !name.matches("[A-Za-z0-9][A-Za-z0-9_.-]{0,63}")) {
            throw new RepoException("Remote names must be 1-64 characters of letters, digits, '.', '_' or '-'.");
        }
    }

    private static URIish toUri(String url) throws URISyntaxException {
        // JGit parses "C:\path" as a URI with a one-letter scheme; use File for
        // local Windows, UNC, and POSIX paths.
        if (url.matches("^[A-Za-z]:[\\\\/].*") || url.startsWith("\\\\")
                || url.startsWith("/") || url.startsWith("./") || url.startsWith("../")) {
            return new URIish(new File(url).toURI().toString());
        }
        return new URIish(url);
    }

    private RepoException error(String action, Exception cause) {
        String friendly = friendlyMessage(cause);
        String detail = cause.getMessage();
        debug.log("repo", "Failed to %s: %s", action, cause);
        String message = "Could not " + action + ": " + friendly;
        if (!friendly.equals(detail) && detail != null && !detail.isBlank() && friendly.endsWith(".")) {
            // Keep the actionable sentence short; the full detail is in the log.
            return new RepoException(message, cause);
        }
        return new RepoException(message, cause);
    }

    private static String friendlyMessage(Exception cause) {
        String message = cause.getMessage();
        if (message == null || message.isBlank()) {
            return cause.getClass().getSimpleName();
        }
        String lower = message.toLowerCase(Locale.ROOT);
        if (cause instanceof TransportException || lower.contains("transport")) {
            if (lower.contains("not authorized") || lower.contains("authentication")
                    || lower.contains("401") || lower.contains("403")) {
                return "Authentication failed. Check the remote URL and credentials.";
            }
            if (lower.contains("unknown host") || lower.contains("connection refused")
                    || lower.contains("timed out") || lower.contains("network is unreachable")) {
                return "Could not reach the remote. Check the URL and your connection.";
            }
            if (lower.contains("not found") || lower.contains("repository not found")) {
                return "The remote repository was not found.";
            }
        }
        return message;
    }

    private static String firstLine(String message) {
        if (message == null) {
            return "";
        }
        int newline = message.indexOf('\n');
        return (newline < 0 ? message : message.substring(0, newline)).trim();
    }

    @Override
    public void close() {
        lock.lock();
        try {
            if (git != null) {
                git.close();
                debug.log("repo", "Closed repository at %s", repoPath);
            }
            git = null;
            repository = null;
        } finally {
            lock.unlock();
        }
    }

    public record PushSummary(String remote, String branch, int updatedRefs, boolean upToDate) {
    }

    public record FetchSummary(String remote, int updatedRefs, String messages) {
    }
}
