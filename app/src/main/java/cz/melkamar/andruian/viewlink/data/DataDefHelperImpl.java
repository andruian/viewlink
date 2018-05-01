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

public class DataDefHelperImpl implements DataDefHelper {
    private final NetHelper netHelper;

    public DataDefHelperImpl(NetHelper netHelper) {
        this.netHelper = netHelper;
    }

    @Override
    public AsyncTaskResult<List<DataDef>> getDataDefs(String url) {
        AsyncTaskResult<String> result = netHelper.httpGet(url, new KeyVal[0]);

        if (result.hasError()) {
            Log.w("finishGetDataDefs", result.getError().getMessage(), result.getError());
            return new AsyncTaskResult<>(result.getError());
        }
        String response = result.getResult();

        try {
            if (response == null) throw new IOException("Response body is empty");

            Log.v("finishGetDataDefs body", response);
            List<DataDef> dataDefList = new DataDefParser().parse(response, RDFFormat.TURTLE);
            return new AsyncTaskResult<>(dataDefList);
        } catch (IOException | DataDefFormatException | RdfFormatException e) {
            Log.e("getDataDefs", e.getMessage(), e);
            return new AsyncTaskResult<>(e);
        }
    }
}
