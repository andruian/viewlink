package cz.melkamar.andruian.viewlink.data;

import android.os.AsyncTask;
import android.util.Log;

import java.util.Arrays;

import cz.melkamar.andruian.viewlink.util.AsyncTaskResult;
import cz.melkamar.andruian.viewlink.util.KeyVal;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Martin Melka on 11.03.2018.
 */

public class NetHelperImpl implements NetHelper {
    OkHttpClient client = new OkHttpClient();

    @Override
    public void getHttpFileAsync(String url, HttpRequestCallback callback) {
        new HttpGetATask(callback).execute(url);
    }

    /**
     * Synchronous HTTP POST request. This cannot be called on the UI thread.
     * @param url
     * @param data
     * @param headers
     * @return
     */
    @Override
    public AsyncTaskResult<String> httpPost(String url, KeyVal[] data, KeyVal... headers) {
        try {
            Log.v("httpPost", "url:"+url+" | data: "+data+" | keyVals: "+ Arrays.toString(headers));

            Request.Builder builder = new Request.Builder().url(url);
            if (headers != null) {
                for (KeyVal keyVal : headers) {
                    builder.addHeader(keyVal.name, keyVal.value);
                }
            }

            FormBody.Builder bodyBuilder = new FormBody.Builder();
            for (KeyVal datum : data) {
                bodyBuilder.add(datum.name, datum.value);
            }
            RequestBody body = bodyBuilder.build();

            Request request = builder.post(body).build();
            Response response = client.newCall(request).execute();
            try {
                Thread.sleep(1000); // TODO remove this artificial wait
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return new AsyncTaskResult<>(response.body().string());
        } catch (Exception e) {
            Log.w("HttpGetATask", e.getMessage(), e);
            return new AsyncTaskResult<>(e);
        }
    }

    @Override
    public void httpPostAsync(HttpRequestCallback callback, String url, String data, KeyVal... keyVals) {
        new HttpPostATask(callback).execute(url, data, keyVals);
    }

    private class HttpGetATask extends AsyncTask<String, Void, AsyncTaskResult<String>> {
        private final HttpRequestCallback callback;

        private HttpGetATask(HttpRequestCallback callback) {
            this.callback = callback;
        }

        @Override
        protected AsyncTaskResult<String> doInBackground(String... strings) {
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
                return new AsyncTaskResult<>(response.body().string());
            } catch (Exception e) {
                Log.w("HttpGetATask", e.getMessage(), e);
                return new AsyncTaskResult<>(e);
            }
        }

        @Override
        protected void onPostExecute(AsyncTaskResult<String> result) {
            callback.onRequestFinished(result);
        }
    }

    private class HttpPostATask extends AsyncTask<Object, Void, AsyncTaskResult<String>> {
        private final HttpRequestCallback callback;

        private HttpPostATask(HttpRequestCallback callback) {
            this.callback = callback;
        }

        @Override
        protected AsyncTaskResult<String> doInBackground(Object... strings) {
            try {
                String url = (String) strings[0];
                String data = (String) strings[1];
                KeyVal[] keyVals = (KeyVal[]) strings[2];
                Log.v("HttpPostATask", "url:"+url+" | data: "+data+" | keyVals: "+ Arrays.toString(keyVals));

                Request.Builder builder = new Request.Builder().url(url);
                if (keyVals != null) {
                    for (KeyVal keyVal : keyVals) {
                        builder.addHeader(keyVal.name, keyVal.value);
                    }
                }

                RequestBody body = new FormBody.Builder()
                        .add("query", data)
                        .build();

                Request request = builder.post(body).build();

                Response response = client.newCall(request).execute();
                try {
                    Thread.sleep(1000); // TODO remove this artificial wait
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return new AsyncTaskResult<>(response.body().string());
            } catch (Exception e) {
                Log.w("HttpGetATask", e.getMessage(), e);
                return new AsyncTaskResult<>(e);
            }
        }

        @Override
        protected void onPostExecute(AsyncTaskResult<String> result) {
            callback.onRequestFinished(result);
        }
    }

}
