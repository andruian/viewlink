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
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by Martin Melka on 11.03.2018.
 */

public class DataManagerImpl implements DataManager {
    private final NetHelper netHelper;

    public DataManagerImpl(NetHelper netHelper) {
        this.netHelper = netHelper;
    }

    @Override
    public void getHttpFile(String url, HttpRequestCallback callback) {
        netHelper.getHttpFile(url, callback);
    }

    @Override
    public void getDataDefs(String url, GetDataDefsCallback callback) {
//        netHelper.getHttpFile(url, response -> finishGetDataDefs(response, callback));
        netHelper.getHttpFile(url, result -> finishGetDataDefs(result, callback));
    }

    protected void finishGetDataDefs(AsyncTaskResult<Response> result, GetDataDefsCallback callback) {
        if (result.hasError()){
            Log.w("finishGetDataDefs", result.getError().getMessage(), result.getError());
            callback.onFetchError(result.getError().getMessage(), 3);
            return;
        }
        Response response = result.getResult();

        try {
            ResponseBody responseBody = response.body();
            if (responseBody == null) throw new IOException("Response body is empty");
            String body = responseBody.string();
            Log.v("finishGetDataDefs body", body);
            List<DataDef> dataDefList = new DataDefParser().parse(body, RDFFormat.TURTLE);
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


//    @Override
//    public String getHttpFile(String url) {
//        return netHelper.getHttpFile(url);
//    }
//
//    @Override
//    public DataDef getDataDefs(String url) {
//        String srcRaw = netHelper.getHttpFile(url);
//         TODO PARSE HERE
//        try {
//            Thread.sleep(2500);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        Random random = new Random();
//        return new DataDef(random.nextInt()+"", url, srcRaw);
//    }

}
