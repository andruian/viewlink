package cz.melkamar.andruian.viewlink.ui;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;

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
    private List<DataDef> dataDefsShownInDrawer = null; // To keep track of what is shown, so we can enable/disable it
    private Location lastLocation = null;

    public final int MIN_DIST_DATA_REFRESH = 50; // Minimal distance in meters to trigger data refresh

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
        view.setKeepMapCentered(true);
    }

    @Override
    public void refreshDatadefsShownInDrawer() {
        DaoHelper.readAllDatadefs(view.getViewLinkApplication().getAppDatabase(), result -> {
            if (result.hasError()) {
                Log.w("refDatadefsShownDrawer", "An error occurred", result.getError());
            } else {
                this.dataDefsShownInDrawer = result.getResult();
                view.showDataDefsInDrawer(result.getResult());
            }
        });
    }

    private double radius = 100;

    @Override
    public void dataDefSwitchClicked(int itemId, boolean enabled) {
        Log.d("dataDefSwitchClicked", "Enabled: " + enabled + "  for uri " + dataDefsShownInDrawer.get(itemId));
        dataDefsShownInDrawer.get(itemId).setEnabled(enabled);

        // TODO get all places around location
        if (enabled) {
            // TODO add progressbar to the main activity while loading markers
            if (lastLocation != null) {
                fetchNewPlaces(view, dataDefsShownInDrawer.get(itemId),
                        lastLocation.getLatitude(), lastLocation.getLongitude(),
                        radius); // TODO what radius?
            }
        } else {
            view.clearMapMarkers(dataDefsShownInDrawer.get(itemId));
        }
    }

    private void fetchNewPlaces(MainMvpView view, DataDef dataDef, double latitude, double longitude, double radius) {
        PlaceFetcher placeFetcher = new PlaceFetcher(
                new IndexServerPlaceFetcher(),
                new SparqlPlaceFetcher()
        );
        new FetchPlacesAT(placeFetcher, view, dataDef, latitude, longitude, radius).execute();
    }

    @Override
    public void onMapCameraMoved(GoogleMap map, int reason) {
//        map.getCameraPosition().zoom
    }

    @Override
    public void onLocationChanged(Location newLocation) {
        float metersDelta = 0;
        if (lastLocation != null)
            metersDelta = newLocation.distanceTo(lastLocation);

        if (lastLocation == null || metersDelta > MIN_DIST_DATA_REFRESH) {
            if (dataDefsShownInDrawer != null) {
                for (DataDef dataDef : dataDefsShownInDrawer) {
                    if (dataDef.isEnabled()) {
                        Log.i("onLocationChanged", "Refreshing data shown for datadef " + dataDef.getUri());
                        fetchNewPlaces(view, dataDef, newLocation.getLatitude(),
                                newLocation.getLongitude(), radius);
                    }
                }
            }

            lastLocation = newLocation;
        }
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
                if (view != null)
                    view.showMessage("An error occurred when fetching places: " + result.getError().getMessage());

                Log.w("FetchPlacesAT", "onPostExecute", result.getError());
                return;
            }

            Log.v("from datadef", dataDef.getUri());
            for (Place place : result.getResult()) {
                Log.v("    result    ", place.toString());
            }

            view.addMapMarkers(result.getResult());
        }
    }

    public void playground() {

    }
}
