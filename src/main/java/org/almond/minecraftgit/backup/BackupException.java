package org.almond.minecraftgit.backup;

import org.almond.minecraftgit.util.UserFacingException;

/** A backup could not be created or read. */
public final class BackupException extends UserFacingException {

    public BackupException(String message) {
        super(message);
    }

    public BackupException(String message, Throwable cause) {
        super(message, cause);
    }
}
