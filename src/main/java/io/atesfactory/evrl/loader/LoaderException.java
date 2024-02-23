package io.atesfactory.evrl.loader;

public class LoaderException extends RuntimeException {
    public LoaderException(String message) {
        super(message);
    }

    public LoaderException(String message, Throwable cause) {
        super(message, cause);
    }
}
