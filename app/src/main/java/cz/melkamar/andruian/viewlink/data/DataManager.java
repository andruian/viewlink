package cz.melkamar.andruian.viewlink.data;

import cz.melkamar.andruian.viewlink.model.DataSource;

/**
 * Created by Martin Melka on 11.03.2018.
 */

public interface DataManager extends NetHelper {
    void getDataSource(String url, GetDataSourceCallback callback);

    interface GetDataSourceCallback {
        void onDataSourceFetched(DataSource dataSource);
    }
}
