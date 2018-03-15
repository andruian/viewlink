package cz.melkamar.andruian.viewlink.data;

import android.os.AsyncTask;
import android.util.Log;

import java.util.Arrays;

import cz.melkamar.andruian.viewlink.util.AsyncTaskResult;
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
    public void getHttpFile(String url, HttpRequestCallback callback) {
        new HttpGetATask(callback).execute(url);
    }

    @Override
    public void httpPost(HttpRequestCallback callback, String url, String data, Header... headers) {
        new HttpPostATask(callback).execute(url, data, headers);
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
                Header[] headers = (Header[]) strings[2];
                Log.v("HttpPostATask", "url:"+url+" | data: "+data+" | headers: "+ Arrays.toString(headers));

                Request.Builder builder = new Request.Builder().url(url);
                if (headers != null) {
                    for (Header header : headers) {
                        builder.addHeader(header.name, header.value);
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

    public static class Header {
        public final String name;
        public final String value;

        public Header(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }
}
