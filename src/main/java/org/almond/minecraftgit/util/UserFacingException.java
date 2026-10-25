package org.almond.minecraftgit.util;

/** Base type for failures that are safe and useful to show in chat. */
public class UserFacingException extends RuntimeException {

    public UserFacingException(String message) {
        super(message);
    }

    public UserFacingException(String message, Throwable cause) {
        super(message, cause);
    }
}
