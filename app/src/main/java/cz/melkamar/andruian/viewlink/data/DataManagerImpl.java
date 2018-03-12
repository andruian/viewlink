package cz.melkamar.andruian.viewlink.data;

import android.util.Log;
import cz.melkamar.andruian.viewlink.model.DataSource;
import okhttp3.Response;

import java.io.IOException;

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
    public void getDataSource(String url, GetDataSourceCallback callback) {
        netHelper.getHttpFile(url, response -> finishGetDataSource(response, callback));
    }

    protected void finishGetDataSource(Response response, GetDataSourceCallback callback){
        try {
            DataSource dataSource = new DataSource("xxx", response.request().url().toString(), response.body().string());
            callback.onDataSourceFetched(dataSource);
        } catch (IOException e) {
            Log.e("finishGetDataSource", "Could not get response.body.string", e);
            e.printStackTrace();
        }
    }


//    @Override
//    public String getHttpFile(String url) {
//        return netHelper.getHttpFile(url);
//    }
//
//    @Override
//    public DataSource getDataSource(String url) {
//        String srcRaw = netHelper.getHttpFile(url);
//         TODO PARSE HERE
//        try {
//            Thread.sleep(2500);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        Random random = new Random();
//        return new DataSource(random.nextInt()+"", url, srcRaw);
//    }

}
