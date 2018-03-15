package cz.melkamar.andruian.viewlink.data.place;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cz.melkamar.andruian.viewlink.R;
import cz.melkamar.andruian.viewlink.data.DataManager;
import cz.melkamar.andruian.viewlink.data.DataManagerProvider;
import cz.melkamar.andruian.viewlink.data.persistence.AppDatabase;
import cz.melkamar.andruian.viewlink.exception.PlaceFetchException;
import cz.melkamar.andruian.viewlink.exception.ReservedNameUsedException;
import cz.melkamar.andruian.viewlink.model.Place;
import cz.melkamar.andruian.viewlink.model.datadef.ClassToLocPath;
import cz.melkamar.andruian.viewlink.model.datadef.DataDef;
import cz.melkamar.andruian.viewlink.model.datadef.SelectProperty;
import cz.melkamar.andruian.viewlink.ui.base.BaseView;
import cz.melkamar.andruian.viewlink.util.AsyncTaskResult;
import cz.melkamar.andruian.viewlink.util.KeyVal;
import cz.melkamar.andruian.viewlink.util.Util;

public class SparqlPlaceFetcher {

    public List<Place> fetchPlaces(BaseView baseView, DataDef dataDef, double latitude, double longitude, double radius) throws PlaceFetchException, ReservedNameUsedException, IOException {
        AppDatabase database = baseView.getViewLinkApplication().getAppDatabase();
        List<ClassToLocPath> classToLocPaths = database.classToLocPathDao().getAllForDataDefUriAndClass(dataDef.getUri(), dataDef.getLocationClassDef().getClassUri());
        List<SelectProperty> selectProperties = database.selectPropertyDao().getAllForDataDefUri(dataDef.getUri());

        if (classToLocPaths.size() != 1)
            Log.e("getSparqlQuery", classToLocPaths.size() + " ClassToLocPaths found for "
                    + dataDef.getUri() + " and class " + dataDef.getLocationClassDef().getClassUri());

        String queryTemplate = null;
        try {
            queryTemplate = Util.readRawTextFile(baseView.getActivity(), R.raw.placequery);
        } catch (IOException e) {
            Log.e("IndexSparqlQueryBuilder", "Could not find queryTemplate query.", e);
            throw new IOException("Could not read query template from resources", e);
        }
        String query = getSparqlQuery(queryTemplate, dataDef, classToLocPaths.get(0), selectProperties, latitude, longitude, radius);

        DataManager mgr = DataManagerProvider.getDataManager();
        AsyncTaskResult<String> result = mgr.httpPost(
                dataDef.getSourceClassDef().getSparqlEndpoint(),
                new KeyVal[]{new KeyVal("query", query)},
                new KeyVal("Accept", "text/csv"));

        if (result.hasError()) {
            throw new PlaceFetchException(result.getError().getMessage(), result.getError());
        }

        longLog("x", result.getResult());

        //TODO parse csv here
        return new ArrayList<>();
    }

    /**
     * Construct a SPARQL query for the given DataDef. This cannot be run on the UI thread.
     *
     * @param dataDef
     * @param classToLocPath
     * @param selectProperties
     * @param latitude
     * @param longitude
     * @param radius
     * @return
     * @throws ReservedNameUsedException
     */
    private String getSparqlQuery(String queryTemplate,
                                  DataDef dataDef, ClassToLocPath classToLocPath,
                                  List<SelectProperty> selectProperties,
                                  double latitude, double longitude, double radius)
            throws ReservedNameUsedException {
        IndexSparqlQueryBuilder builder = new IndexSparqlQueryBuilder(
                queryTemplate,
                dataDef.getSourceClassDef().getClassUri(),
                dataDef.getSourceClassDef().getPathToLocationClass(),
                dataDef.getLocationClassDef().getSparqlEndpoint(),
                classToLocPath.getLatCoord(),
                classToLocPath.getLongCoord());

        for (SelectProperty selectProperty : selectProperties) {
            builder.addSelectProperty(selectProperty);
        }
        return builder.build();
    }

    private void longLog(String TAG, String txt) {
        int maxLogSize = 1000;
        for (int i = 0; i <= txt.length() / maxLogSize; i++) {
            int start = i * maxLogSize;
            int end = (i + 1) * maxLogSize;
            end = end > txt.length() ? txt.length() : end;
            Log.v(TAG, txt.substring(start, end));
        }
    }
}
