package cz.melkamar.andruian.viewlink.data;

import android.os.AsyncTask;
import android.util.Log;

import cz.melkamar.andruian.viewlink.util.AsyncTaskResult;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Martin Melka on 11.03.2018.
 */

public class NetHelperImpl implements NetHelper {
    OkHttpClient client = new OkHttpClient();

    @Override
    public void getHttpFile(String url, HttpRequestCallback callback) {
        new HttpGetATask(callback).execute(url);
    }

    private class HttpGetATask extends AsyncTask<String, Void, AsyncTaskResult<Response>> {
        private final HttpRequestCallback callback;

        private HttpGetATask(HttpRequestCallback callback) {
            this.callback = callback;
        }

        @Override
        protected AsyncTaskResult<Response> doInBackground(String... strings) {
            try {
                Request request = new Request.Builder()
                        .url(strings[0])
                        .build();
                Response response = client.newCall(request).execute();
                try {
                    Thread.sleep(1000); // TODO remove this artificial wait
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return new AsyncTaskResult<>(response);
            } catch (Exception e) {
                Log.w("HttpGetATask", e.getMessage(), e);
                return new AsyncTaskResult<>(e);
            }
        }

        @Override
        protected void onPostExecute(AsyncTaskResult<Response> result) {
            callback.onRequestFinished(result);
        }
    }
}
