package cz.melkamar.andruian.viewlink.data.place;

import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import cz.melkamar.andruian.viewlink.data.NetHelperProvider;
import cz.melkamar.andruian.viewlink.data.persistence.AppDatabase;
import cz.melkamar.andruian.viewlink.exception.PlaceFetchException;
import cz.melkamar.andruian.viewlink.exception.ReservedNameUsedException;
import cz.melkamar.andruian.viewlink.model.datadef.ClassToLocPath;
import cz.melkamar.andruian.viewlink.model.datadef.DataDef;
import cz.melkamar.andruian.viewlink.model.datadef.SelectProperty;
import cz.melkamar.andruian.viewlink.ui.base.BaseView;
import cz.melkamar.andruian.viewlink.util.AsyncTaskResult;
import okhttp3.Response;

public class SparqlPlaceFetcher {

    public void fetchPlaces(BaseView baseView, DataDef dataDef, double latitude, double longitude, double radius, PlaceFetcherHelper.Listener listener) throws PlaceFetchException, ReservedNameUsedException {
        String query = getSparqlQuery(baseView, dataDef);
        // TODO filter query lat long
        try {
            NetHelperProvider.getNetHelper().httpPost(result -> onRequestComplete(result, dataDef, listener),
                    dataDef.getSourceClassDef().getSparqlEndpoint(),
                    URLEncoder.encode(query, "UTF-8"),
                    "Accept: text/csv");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.e("fetchPlaces", e.getMessage(), e);
        }
    }

    public void onRequestComplete(AsyncTaskResult<Response> result, DataDef fromDataDef, PlaceFetcherHelper.Listener listener){
        // TODO convert CSV to places
        if (result.hasError()){
            result.getError().printStackTrace();
            Log.e("onRequestComplete", result.getError().getMessage(), result.getError());
            return;
        }

        try {
            Log.i("onRequestComplete", result.getResult().body().string());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("onRequestComplete", result.getError().getMessage(), result.getError());
        }
//        listener.onPlacesFetched(fromDataDef, );
    }

    private String getSparqlQuery(BaseView baseView, DataDef dataDef) throws ReservedNameUsedException {
        AppDatabase database = baseView.getViewLinkApplication().getAppDatabase();
        // Get ClassToLocPath for this location class
        List<ClassToLocPath> classToLocPaths = database.classToLocPathDao()
                .getAllForDataDefUriAndClass(dataDef.getUri(),
                        dataDef.getLocationClassDef().getClassUri()
                );

        if (classToLocPaths.size() != 1)
            Log.e("getSparqlQuery", classToLocPaths.size() + " ClassToLocPaths found for "
                    + dataDef.getUri() + " and class " + dataDef.getLocationClassDef().getClassUri());

        // Get selected properties
        List<SelectProperty> selectProperties = database.selectPropertyDao().getAllForDataDefUri(dataDef.getUri());

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
        return builder.build();
    }

    private void query() {
//        curl http://localhost:3030/ruian/query -X POST --data 'query=%0A%0ASELECT+%3Fsubject+%3Fpredicate+%3Fobject%0AWHERE+%7B%0A++%3Fsubject+%3Fpredicate+%3Fobject%0A%7D%0ALIMIT+25' -H 'Accept: text/csv'
    }
}
