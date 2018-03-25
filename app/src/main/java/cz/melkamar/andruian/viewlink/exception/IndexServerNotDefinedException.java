package cz.melkamar.andruian.viewlink.exception;

public class IndexServerNotDefinedException extends Exception{
    public IndexServerNotDefinedException() {
    }

    public IndexServerNotDefinedException(String message) {
        super(message);
    }

    public IndexServerNotDefinedException(String message, Throwable cause) {
        super(message, cause);
    }

    public IndexServerNotDefinedException(Throwable cause) {
        super(cause);
    }
}
