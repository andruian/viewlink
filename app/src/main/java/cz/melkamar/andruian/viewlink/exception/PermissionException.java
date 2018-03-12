package cz.melkamar.andruian.viewlink.exception;

/**
 * Created by Martin Melka on 12.03.2018.
 */

public class PermissionException extends Exception {
    public PermissionException() {
    }

    public PermissionException(String message) {
        super(message);
    }

    public PermissionException(String message, Throwable cause) {
        super(message, cause);
    }

    public PermissionException(Throwable cause) {
        super(cause);
    }
}
