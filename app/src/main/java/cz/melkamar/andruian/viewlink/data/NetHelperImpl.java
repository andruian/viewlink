package cz.melkamar.andruian.viewlink.data;

import android.util.Log;
import cz.melkamar.andruian.viewlink.exception.HttpException;
import cz.melkamar.andruian.viewlink.util.AsyncTaskResult;
import cz.melkamar.andruian.viewlink.util.KeyVal;
import okhttp3.*;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Created by Martin Melka on 11.03.2018.
 */

public class NetHelperImpl implements NetHelper {
    private OkHttpClient client;

    public NetHelperImpl() {
        client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    @Override
    public AsyncTaskResult<String> httpGet(String url, KeyVal[] params, KeyVal... headers) {
        try {
            Log.v("httpGet", "url:" + url + " | params: " + Arrays.toString(params) + " | keyVals: " + Arrays.toString(headers));
            url = url.trim();

            if (!url.startsWith("http")) url = "http://"+url;
            HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
            for (KeyVal param : params) {
                urlBuilder.addQueryParameter(param.name, param.value);
            }

            Request.Builder builder = new Request.Builder().url(urlBuilder.build());
            if (headers != null) {
                for (KeyVal keyVal : headers) {
                    builder.addHeader(keyVal.name, keyVal.value);
                }
            }

            Request request = builder.get().build();
            Response response = client.newCall(request).execute();
            String body = null;
            if (response.body() != null) {
                body = response.body().string();
            }
            if (response.code() >= 400) {
                throw new HttpException("HTTP " + response.code() + ": " + body);
            }

            return new AsyncTaskResult<>(body);
        } catch (Exception e) {
            Log.w("httpGet", e.getMessage(), e);
            return new AsyncTaskResult<>(e);
        }
    }

    /**
     * Synchronous HTTP POST request. This cannot be called on the UI thread.
     *
     * @param url
     * @param data
     * @param headers
     * @return
     */
    @Override
    public AsyncTaskResult<String> httpPost(String url, KeyVal[] data, KeyVal... headers) {
        try {
            Log.v("httpPost", "url:" + url + " | data: " + Arrays.toString(data) + " | keyVals: " + Arrays.toString(headers));

            if (!url.startsWith("http")) url = "http://"+url;
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
            String bodyStr = null;
            if (response.body() != null) {
                bodyStr = response.body().string();
            }
            if (response.code() >= 400) {
                throw new HttpException("HTTP " + response.code() + ": " + bodyStr);
            }
            return new AsyncTaskResult<>(bodyStr);
        } catch (Exception e) {
            Log.w("httpPost", e.getMessage(), e);
            return new AsyncTaskResult<>(e);
        }
    }
}
