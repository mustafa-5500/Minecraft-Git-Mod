package org.almond.minecraftgit.serialization;

/** A repository-format error that should be shown to the user. */
public final class SerializationException extends RuntimeException {

    public SerializationException(String message) {
        super(message);
    }

    public SerializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
