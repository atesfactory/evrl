package io.atesfactory.evrl.transformer;

public class TransformerException extends RuntimeException {
    public TransformerException(String message) {
        super(message);
    }

    public TransformerException(Throwable cause) {
        super(cause);
    }
}
