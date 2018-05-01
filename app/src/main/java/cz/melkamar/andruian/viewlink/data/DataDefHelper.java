package cz.melkamar.andruian.viewlink.data;

import java.util.List;

import cz.melkamar.andruian.ddfparser.model.DataDef;
import cz.melkamar.andruian.viewlink.util.AsyncTaskResult;

/**
 * An interface for functionality related to data definitions.
 */
public interface DataDefHelper {
    /**
     * Parse a list of data definitions from a RDF file at a given URL.
     *
     * @param url URL of a file to parse for data definitions.
     * @return
     */
    AsyncTaskResult<List<DataDef>> getDataDefs(String url);
}
