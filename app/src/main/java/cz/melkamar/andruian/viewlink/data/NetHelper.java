package cz.melkamar.andruian.viewlink.data;

import cz.melkamar.andruian.viewlink.util.AsyncTaskResult;
import okhttp3.Response;

/**
 * Created by Martin Melka on 11.03.2018.
 */

public interface NetHelper {
    void getHttpFile(String url, HttpRequestCallback callback);

    interface HttpRequestCallback {
        void onRequestFinished(AsyncTaskResult<Response> result);
    }
}
