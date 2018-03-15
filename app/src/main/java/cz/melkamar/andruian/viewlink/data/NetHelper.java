package cz.melkamar.andruian.viewlink.data;

import cz.melkamar.andruian.viewlink.util.AsyncTaskResult;
import cz.melkamar.andruian.viewlink.util.KeyVal;

/**
 * Created by Martin Melka on 11.03.2018.
 */

public interface NetHelper {
    void getHttpFileAsync(String url, HttpRequestCallback callback);

    AsyncTaskResult<String> httpPost(String url, KeyVal[] data, KeyVal... keyVals);
    void httpPostAsync(HttpRequestCallback callback, String url, String data, KeyVal... keyVals);
    // TODO maybe get rid of async versions? Just make the caller handle it.

    interface HttpRequestCallback {
        void onRequestFinished(AsyncTaskResult<String> result);
    }

//    interface HttpPostListener {
//        void onRequestFinished(AsyncTaskResult<Response> result);
//    }
}
