package cz.melkamar.andruian.viewlink.data;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

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

    private class HttpGetATask extends AsyncTask<String, Void, Response> {
        private final HttpRequestCallback callback;

        private HttpGetATask(HttpRequestCallback callback) {
            this.callback = callback;
        }

        @Override
        protected Response doInBackground(String... strings) {
            Request request = new Request.Builder()
                    .url(strings[0])
                    .build();

            Response response = null;
            try {
                response = client.newCall(request).execute();
                try {
                    Thread.sleep(1000); // TODO remove this artificial wait
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return response;
            } catch (IOException e) {
                Log.w("http get", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(Response response) {
            callback.onRequestFinished(response);
        }
    }
}
