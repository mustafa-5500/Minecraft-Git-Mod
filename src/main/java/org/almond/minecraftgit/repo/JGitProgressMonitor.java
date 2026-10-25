package org.almond.minecraftgit.repo;

import org.eclipse.jgit.lib.ProgressMonitor;

/** Adapts JGit's progress callbacks to our transport-agnostic sink. */
final class JGitProgressMonitor implements ProgressMonitor {

    private final RepoProgress progress;
    private String task = "";
    private int total;

    private JGitProgressMonitor(RepoProgress progress) {
        this.progress = progress == null ? RepoProgress.NOOP : progress;
    }

    static ProgressMonitor of(RepoProgress progress) {
        if (progress == null || progress == RepoProgress.NOOP) {
            return org.eclipse.jgit.lib.NullProgressMonitor.INSTANCE;
        }
        return new JGitProgressMonitor(progress);
    }

    @Override
    public void start(int totalTasks) {
        // No task count exposed; the command shows the current phase.
    }

    @Override
    public void beginTask(String title, int totalWork) {
        this.task = title == null ? "" : title;
        this.total = totalWork;
        progress.update(task, 0, total);
    }

    @Override
    public void update(int completed) {
        progress.update(task, completed, total);
    }

    @Override
    public void endTask() {
        progress.update(task, total, total);
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public void showDuration(boolean enabled) {
        // The action bar is updated on a fixed schedule; duration is not shown.
    }
}
