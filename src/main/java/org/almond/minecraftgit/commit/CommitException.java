package org.almond.minecraftgit.commit;

import org.almond.minecraftgit.util.UserFacingException;

/** A commit could not be created for a user-correctable reason. */
public final class CommitException extends UserFacingException {

    public CommitException(String message) {
        super(message);
    }

    public CommitException(String message, Throwable cause) {
        super(message, cause);
    }
}
