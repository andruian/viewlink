package cz.melkamar.andruian.viewlink.data;

import android.util.Log;

import org.eclipse.rdf4j.rio.RDFFormat;

import java.io.IOException;
import java.util.List;

import cz.melkamar.andruian.ddfparser.DataDefParser;
import cz.melkamar.andruian.ddfparser.exception.DataDefFormatException;
import cz.melkamar.andruian.ddfparser.exception.RdfFormatException;
import cz.melkamar.andruian.ddfparser.model.DataDef;
import cz.melkamar.andruian.viewlink.util.AsyncTaskResult;
import cz.melkamar.andruian.viewlink.util.KeyVal;

/**
 * Created by Martin Melka on 11.03.2018.
 */

public class DataManagerImpl implements DataManager {
    private final NetHelper netHelper;

    public DataManagerImpl(NetHelper netHelper) {
        this.netHelper = netHelper;
    }

    @Override
    public AsyncTaskResult<String> httpGet(String url, KeyVal[] data, KeyVal... headers) {
        return netHelper.httpGet(url, data, headers);
    }

    @Override
    public void getHttpFileAsync(String url, HttpRequestCallback callback) {
        netHelper.getHttpFileAsync(url, callback);
    }

    @Override
    public AsyncTaskResult<String> httpPost(String url, KeyVal[] data, KeyVal... keyVals) {
        return netHelper.httpPost(url, data, keyVals);
    }

    @Override
    public void httpPostAsync(HttpRequestCallback callback, String url, String data, KeyVal... keyVals) {
        netHelper.httpPostAsync(callback, url, data, keyVals);
    }

    @Override
    public void getDataDefs(String url, GetDataDefsCallback callback) {
//        netHelper.getHttpFileAsync(url, response -> finishGetDataDefs(response, callback));
        netHelper.getHttpFileAsync(url, result -> finishGetDataDefs(result, callback));
    }

    protected void finishGetDataDefs(AsyncTaskResult<String> result, GetDataDefsCallback callback) {
        if (result.hasError()) {
            Log.w("finishGetDataDefs", result.getError().getMessage(), result.getError());
            callback.onFetchError(result.getError().getMessage(), 3);
            return;
        }
        String response = result.getResult();

        try {
            if (response == null) throw new IOException("Response body is empty");

            Log.v("finishGetDataDefs body", response);
            List<DataDef> dataDefList = new DataDefParser().parse(response, RDFFormat.TURTLE);
            callback.onDataDefsFetched(dataDefList);
        } catch (IOException e) {
            Log.e("finishGetDataDefs", "Could not get response.body.string", e);
            e.printStackTrace();
            callback.onFetchError(e.toString(), 0);
        } catch (DataDefFormatException e) {
            Log.e("finishGetDataDefs", "Invalid datadef", e);
            e.printStackTrace();
            callback.onFetchError(e.toString(), 1);
        } catch (RdfFormatException e) {
            Log.e("finishGetDataDefs", "Invalid rdf format", e);
            e.printStackTrace();
            callback.onFetchError(e.toString(), 2);
        }
    }

}
