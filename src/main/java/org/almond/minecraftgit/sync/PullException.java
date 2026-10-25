package org.almond.minecraftgit.sync;

import org.almond.minecraftgit.util.UserFacingException;

/** A pull/checkout preparation failed in a user-correctable way. */
public final class PullException extends UserFacingException {

    public PullException(String message) {
        super(message);
    }

    public PullException(String message, Throwable cause) {
        super(message, cause);
    }
}
