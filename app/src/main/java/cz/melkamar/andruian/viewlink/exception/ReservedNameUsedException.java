package cz.melkamar.andruian.viewlink.exception;

public class ReservedNameUsedException extends Exception {
    public ReservedNameUsedException() {
    }

    public ReservedNameUsedException(String message) {
        super(message);
    }

    public ReservedNameUsedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReservedNameUsedException(Throwable cause) {
        super(cause);
    }
}
