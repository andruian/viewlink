package cz.melkamar.andruian.viewlink.data;

import java.util.List;

import cz.melkamar.andruian.ddfparser.model.DataDef;
import cz.melkamar.andruian.viewlink.data.dao.DataDefDao;

/**
 * Created by Martin Melka on 11.03.2018.
 */

public interface DataManager extends NetHelper {
    void getDataDefs(String url, GetDataDefsCallback callback);
    DataDefDao getDataDefDao();

    interface GetDataDefsCallback {
        void onDataDefsFetched(List<DataDef> dataDefs);
        void onFetchError(String error, int errorCode);
    }
}
