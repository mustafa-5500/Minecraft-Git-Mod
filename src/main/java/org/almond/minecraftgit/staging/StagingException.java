package org.almond.minecraftgit.staging;

import org.almond.minecraftgit.util.UserFacingException;

/** A staging operation failed in a way the user can fix. */
public final class StagingException extends UserFacingException {

    public StagingException(String message) {
        super(message);
    }

    public StagingException(String message, Throwable cause) {
        super(message, cause);
    }
}
