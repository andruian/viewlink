package cz.melkamar.andruian.viewlink.data.place;

import android.util.Log;

import java.util.List;

import cz.melkamar.andruian.viewlink.data.NetHelperImpl;
import cz.melkamar.andruian.viewlink.data.NetHelperProvider;
import cz.melkamar.andruian.viewlink.data.persistence.AppDatabase;
import cz.melkamar.andruian.viewlink.data.persistence.DaoHelper;
import cz.melkamar.andruian.viewlink.exception.PlaceFetchException;
import cz.melkamar.andruian.viewlink.exception.ReservedNameUsedException;
import cz.melkamar.andruian.viewlink.model.datadef.ClassToLocPath;
import cz.melkamar.andruian.viewlink.model.datadef.DataDef;
import cz.melkamar.andruian.viewlink.model.datadef.SelectProperty;
import cz.melkamar.andruian.viewlink.ui.base.BaseView;
import cz.melkamar.andruian.viewlink.util.AsyncTaskResult;

public class SparqlPlaceFetcher {

    public void fetchPlaces(BaseView baseView, DataDef dataDef, double latitude, double longitude, double radius, PlaceFetcherHelper.Listener listener) throws PlaceFetchException, ReservedNameUsedException {
        getSparqlQuery(baseView, dataDef, query -> {
            try {
                // TODO handle errors in this callback hell. Maybe just do everything in one asynctask? that would be easier -- yeah just locally create an asynctask and do everything there...
                fetchPlacesPostQuery(query, dataDef, latitude, longitude, radius, listener);
            } catch (PlaceFetchException e) {
                e.printStackTrace();
            } catch (ReservedNameUsedException e) {
                e.printStackTrace();
            }
        });
    }

    public void fetchPlacesPostQuery(String query, DataDef dataDef, double latitude, double longitude, double radius, PlaceFetcherHelper.Listener listener) throws PlaceFetchException, ReservedNameUsedException {
//        try {
            NetHelperProvider.getNetHelper().httpPost(result -> onRequestComplete(result, dataDef, listener),
                    dataDef.getSourceClassDef().getSparqlEndpoint(),
//                    URLEncoder.encode(query, "UTF-8"),
                    query,
                    new NetHelperImpl.Header("Accept", "text/csv"));
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//            Log.e("fetchPlaces", e.getMessage(), e);
//        }
    }

    public void onRequestComplete(AsyncTaskResult<String> result, DataDef fromDataDef, PlaceFetcherHelper.Listener listener) {
        // TODO convert CSV to places
        if (result.hasError()) {
            result.getError().printStackTrace();
            Log.e("onRequestComplete", result.getError().getMessage(), result.getError());
            return;
        }

            longLog("onRequestComplete", result.getResult() + "[xxx]");
//        listener.onPlacesFetched(fromDataDef, );
    }

    private void getSparqlQuery(BaseView baseView, DataDef dataDef, QueryBuiltListener listener) throws ReservedNameUsedException {
        AppDatabase database = baseView.getViewLinkApplication().getAppDatabase();
        // Asynchronously get all needed stuff from the database
        DaoHelper.readForSparqlQuery(database, dataDef.getUri(), dataDef.getLocationClassDef().getClassUri(), result -> {
            if (result.hasError()) {
                Log.e("getSparqlQuery", "readForSparqlQuery error" + result.getError().getMessage(), result.getError());
            } else {
                getSparqlQueryPostDb(baseView, dataDef, result.getResult(), listener);
            }
        });
    }

    private void getSparqlQueryPostDb(BaseView baseView, DataDef dataDef, DaoHelper.ReadForSparqlQueryATask.ClassToLocAndSelectPropsResult result, QueryBuiltListener listener) throws ReservedNameUsedException {
        List<ClassToLocPath> classToLocPaths = result.classToLocPaths;
        List<SelectProperty> selectProperties = result.selectProperties;

        if (classToLocPaths.size() != 1)
            Log.e("getSparqlQuery", classToLocPaths.size() + " ClassToLocPaths found for "
                    + dataDef.getUri() + " and class " + dataDef.getLocationClassDef().getClassUri());

        // TODO Ugly but quick. Handle errors eventually.
        ClassToLocPath classToLocPath = classToLocPaths.get(0);
        IndexSparqlQueryBuilder builder = new IndexSparqlQueryBuilder(
                baseView.getActivity(),
                dataDef.getSourceClassDef().getClassUri(),
                dataDef.getSourceClassDef().getPathToLocationClass(),
                dataDef.getLocationClassDef().getSparqlEndpoint(),
                classToLocPath.getLatCoord(),
                classToLocPath.getLongCoord());

        for (SelectProperty selectProperty : selectProperties) {
            builder.addSelectProperty(selectProperty);
        }
        listener.onQueryBuilt(builder.build());
    }

    private static interface QueryBuiltListener {
        public void onQueryBuilt(String query);
    }

    private void query() {
//        curl http://localhost:3030/ruian/query -X POST --data 'query=%0A%0ASELECT+%3Fsubject+%3Fpredicate+%3Fobject%0AWHERE+%7B%0A++%3Fsubject+%3Fpredicate+%3Fobject%0A%7D%0ALIMIT+25' -H 'Accept: text/csv'
    }

    private void longLog(String TAG, String txt){
        int maxLogSize = 1000;
        for(int i = 0; i <= txt.length() / maxLogSize; i++) {
            int start = i * maxLogSize;
            int end = (i+1) * maxLogSize;
            end = end > txt.length() ? txt.length() : end;
            Log.v(TAG, txt.substring(start, end));
        }
    }
}
