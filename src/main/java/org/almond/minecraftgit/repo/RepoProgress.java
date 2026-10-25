package org.almond.minecraftgit.repo;

/** Transport progress sink, called from a worker thread. */
public interface RepoProgress {

    void update(String task, int completed, int total);

    default void done() {
    }

    RepoProgress NOOP = (task, completed, total) -> {
    };
}
