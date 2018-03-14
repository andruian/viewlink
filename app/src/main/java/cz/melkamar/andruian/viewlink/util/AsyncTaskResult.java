package cz.melkamar.andruian.viewlink.util;

/**
 * Taken from https://stackoverflow.com/questions/1739515/asynctask-and-error-handling-on-android.
 */
public class AsyncTaskResult<T> {
    private T result;
    private Exception error;

    public T getResult() {
        return result;
    }

    public Exception getError() {
        return error;
    }

    public AsyncTaskResult(T result) {
        super();
        this.result = result;
    }

    public AsyncTaskResult(Exception error) {
        super();
        this.error = error;
    }

    public boolean hasError(){
        return error != null;
    }
}
