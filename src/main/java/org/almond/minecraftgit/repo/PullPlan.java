package org.almond.minecraftgit.repo;

import org.eclipse.jgit.lib.ObjectId;

/**
 * A fetch plus the relationship between local and remote branches. The caller
 * may apply the remote snapshots to the world before {@link RepoService#fastForward}
 * moves the local branch, so a failed world write never leaves Git ahead of the
 * world.
 */
public record PullPlan(String remote, String branch, ObjectId localHead, ObjectId remoteHead,
                       ObjectId mergeBase, Type type, int fetchedRefs) {

    public enum Type {
        UP_TO_DATE,
        FAST_FORWARD,
        DIVERGED
    }

    public boolean canFastForward() {
        return type == Type.FAST_FORWARD;
    }
}
