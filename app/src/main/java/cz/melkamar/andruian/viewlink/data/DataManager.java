package cz.melkamar.andruian.viewlink.data;

import java.util.List;

import cz.melkamar.andruian.ddfparser.model.DataDef;
import cz.melkamar.andruian.viewlink.util.AsyncTaskResult;

/**
 * Created by Martin Melka on 11.03.2018.
 */

public interface DataManager {
    AsyncTaskResult<List<DataDef>> getDataDefs(String url);
}
