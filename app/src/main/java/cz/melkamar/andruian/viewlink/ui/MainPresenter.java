package cz.melkamar.andruian.viewlink.ui;

import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

import cz.melkamar.andruian.viewlink.data.persistence.DaoHelper;
import cz.melkamar.andruian.viewlink.data.place.IndexServerPlaceFetcher;
import cz.melkamar.andruian.viewlink.data.place.PlaceFetcher;
import cz.melkamar.andruian.viewlink.data.place.SparqlPlaceFetcher;
import cz.melkamar.andruian.viewlink.model.datadef.DataDef;
import cz.melkamar.andruian.viewlink.model.place.Place;
import cz.melkamar.andruian.viewlink.util.AsyncTaskResult;

/**
 * Created by Martin Melka on 11.03.2018.
 */

public class MainPresenter implements MainMvpPresenter {
    private MainMvpView view;
    private List<DataDef> dataDefsShown = null; // To keep track of what is shown, so we can enable/disable it

    public MainPresenter(MainMvpView view) {
        this.view = view;
    }

    @Override
    public void manageDataSources() {
        Log.i("manageDataSources", "foo");
//        view.showMessage("add datasource: "+ DataManagerProvider.getDataManager().getHttpFileAsync("someUrl"));
        view.showManageDatasourcesActivity();
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestroy() {
        view = null;
    }

    @Override
    public void onFabClicked() {
        // TODO This takes ages on hardware after start
//        view.setKeepMapCentered(true);
        playground();
    }

    @Override
    public void refreshDatadefsShown() {
        DaoHelper.readAllDatadefs(view.getViewLinkApplication().getAppDatabase(), result -> {
            if (result.hasError()) {
                Log.w("refreshDatadefsShown", "An error occurred", result.getError());
            } else {
                this.dataDefsShown = result.getResult();
                view.showDataDefsInDrawer(result.getResult());
            }
        });
    }

    @Override
    public void dataDefSwitchClicked(int itemId, boolean enabled) {
        Log.d("dataDefSwitchClicked", "Enabled: " + enabled + "  for uri " + dataDefsShown.get(itemId));
        // TODO get all places around location

        PlaceFetcher placeFetcher = new PlaceFetcher(
                new IndexServerPlaceFetcher(),
                new SparqlPlaceFetcher()
        );

        new FetchPlacesAT(placeFetcher, view, dataDefsShown.get(itemId), 14, 50, 100).execute();
    }

    @Override
    public void showItemsOnMap(List<Place> places) {
        // TODO implement
    }

    private static class FetchPlacesAT extends AsyncTask<Void, Void, AsyncTaskResult<List<Place>>> {
        private final PlaceFetcher placeFetcher;
        private final MainMvpView view;
        private final DataDef dataDef;
        private final double latitude;
        private final double longitude;
        private final double radius;

        private FetchPlacesAT(PlaceFetcher placeFetcher, MainMvpView view, DataDef dataDef, double latitude, double longitude, double radius) {
            this.placeFetcher = placeFetcher;
            this.view = view;
            this.dataDef = dataDef;
            this.latitude = latitude;
            this.longitude = longitude;
            this.radius = radius;
        }

        @Override
        protected AsyncTaskResult<List<Place>> doInBackground(Void... voids) {
            try {
                List<Place> result = placeFetcher.fetchPlaces(view, dataDef, latitude, longitude, radius);
                return new AsyncTaskResult<>(result);
            } catch (Exception e) {
                e.printStackTrace();
                return new AsyncTaskResult<>(e);
            }
        }

        @Override
        protected void onPostExecute(AsyncTaskResult<List<Place>> result) {
            if (result.hasError()) {
                view.showMessage("An error occurred when fetching places: " + result.getError().getMessage());
                Log.w("FetchPlacesAT", "onPostExecute", result.getError());
                return;
            }

            Log.i("from datadef", dataDef.getUri());
            for (Place place : result.getResult()) {
                Log.i("    result    ", place.toString());
            }

        }
    }

    public void playground() {

    }
}
