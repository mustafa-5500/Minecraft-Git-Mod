package org.almond.minecraftgit.selection;

import org.almond.minecraftgit.util.UserFacingException;

/** A selection or wand operation the user can correct. */
public final class SelectionException extends UserFacingException {

    public SelectionException(String message) {
        super(message);
    }
}
