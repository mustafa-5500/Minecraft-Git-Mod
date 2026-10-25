package org.almond.minecraftgit.repo;

import org.almond.minecraftgit.util.UserFacingException;

/**
 * A repository operation failed in a way the user can act on. Commands show
 * {@link #getMessage()} directly; the JGit cause is kept for debug logs.
 */
public class RepoException extends UserFacingException {

    public RepoException(String message) {
        super(message);
    }

    public RepoException(String message, Throwable cause) {
        super(message, cause);
    }
}
