package cz.melkamar.andruian.viewlink.data;

import cz.melkamar.andruian.viewlink.util.AsyncTaskResult;

/**
 * Created by Martin Melka on 11.03.2018.
 */

public interface NetHelper {
    void getHttpFile(String url, HttpRequestCallback callback);

    void httpPost(HttpRequestCallback callback, String url, String data, NetHelperImpl.Header... headers);

    interface HttpRequestCallback {
        void onRequestFinished(AsyncTaskResult<String> result);
    }

//    interface HttpPostListener {
//        void onRequestFinished(AsyncTaskResult<Response> result);
//    }
}
