package org.almond.minecraftgit.world;

import org.almond.minecraftgit.util.UserFacingException;

/** A world apply could not proceed safely. */
public final class ApplyException extends UserFacingException {

    public ApplyException(String message) {
        super(message);
    }

    public ApplyException(String message, Throwable cause) {
        super(message, cause);
    }
}
