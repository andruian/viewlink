package cz.melkamar.andruian.viewlink.exception;

public class PlaceFetchException extends Exception {
    public PlaceFetchException() {
    }

    public PlaceFetchException(String message) {
        super(message);
    }

    public PlaceFetchException(String message, Throwable cause) {
        super(message, cause);
    }

    public PlaceFetchException(Throwable cause) {
        super(cause);
    }
}
