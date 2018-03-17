package cz.melkamar.andruian.viewlink.ui.main;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import cz.melkamar.andruian.viewlink.data.persistence.DaoHelper;
import cz.melkamar.andruian.viewlink.data.place.IndexServerPlaceFetcher;
import cz.melkamar.andruian.viewlink.data.place.PlaceFetcher;
import cz.melkamar.andruian.viewlink.data.place.SparqlPlaceFetcher;
import cz.melkamar.andruian.viewlink.model.datadef.DataDef;
import cz.melkamar.andruian.viewlink.model.place.Place;
import cz.melkamar.andruian.viewlink.ui.base.BasePresenterImpl;
import cz.melkamar.andruian.viewlink.util.AsyncTaskResult;

/**
 * Created by Martin Melka on 11.03.2018.
 */

public class MainPresenter extends BasePresenterImpl implements MainMvpPresenter {
    private MainMvpView view;
    private List<DataDef> dataDefsShownInDrawer = null; // To keep track of what is shown, so we can enable/disable it
    private Location lastLocation = null;

    public final int MIN_DIST_DATA_REFRESH = 200; // Minimal distance in meters to trigger data refresh

    public MainPresenter(MainMvpView view) {
        super(view);
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

    }

    @Override
    public void refreshDatadefsShownInDrawer() {
        Log.v("MainPresenter", "refreshDatadefsShownInDrawer");
        DaoHelper.readAllDatadefs(view.getViewLinkApplication().getAppDatabase(), result -> {
            if (result.hasError()) {
                Log.w("refDatadefsShownDrawer", "An error occurred", result.getError());
            } else {
                this.dataDefsShownInDrawer = result.getResult();
                view.showDataDefsInDrawer(result.getResult());
            }
        });
    }

    private double getRadiusFromMap() {
        // Calculate radius to show as the distance from the middle of the map to the border
        //  - whichever direction is longer
        LatLng northeast = view.getMap().getProjection().getVisibleRegion().latLngBounds.northeast;
        LatLng camTarget = view.getMap().getCameraPosition().target;
        // TODO remove 0.5, it's just debug
        return 0.5 * Math.max(
                Math.abs(northeast.latitude - camTarget.latitude),
                Math.abs(northeast.longitude - camTarget.longitude));
    }

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
                        getRadiusFromMap()); // TODO what radius?
            }
        } else {
            view.clearMapMarkers(dataDefsShownInDrawer.get(itemId));
        }
    }

    private void fetchNewPlaces(MainMvpView view, DataDef dataDef, double latitude, double longitude, double radius) {
        Log.i("fetchNewPlaces", dataDef.getUri() + " at " + latitude + "," + longitude + " (" + radius + ")");
        PlaceFetcher placeFetcher = new PlaceFetcher(
                new IndexServerPlaceFetcher(),
                new SparqlPlaceFetcher()
        );
        new FetchPlacesAT(placeFetcher, view, dataDef, latitude, longitude, radius).execute();
        view.showProgressBar();
    }

    @Override
    public void onMapCameraMoved(GoogleMap map, int reason) {
        if (view != null) {
            if (!view.isCameraFollowing()) {
                view.showUpdatePlacesButton();
            }
        }
//        map.getCameraPosition().zoom
    }

    @Override
    public void onLocationChanged(Location newLocation) {
        float metersDelta = 0;
        if (lastLocation != null)
            metersDelta = newLocation.distanceTo(lastLocation);

        // TODo maybe do not refresh on location changed but camera changed?
        // Automatically refresh data around user only if camera is following him - otherwise they can click the button when needed
        if (view != null && view.isCameraFollowing()) {
            if (lastLocation == null || metersDelta > MIN_DIST_DATA_REFRESH) {
                refreshMarkers(newLocation.getLatitude(), newLocation.getLongitude());
                lastLocation = newLocation;
            }
        }
    }

    @Override
    public void onUpdatePlacesButtonClicked() {
        LatLng mapPosition = view.getMap().getCameraPosition().target;
        refreshMarkers(mapPosition.latitude, mapPosition.longitude);
    }

    private void refreshMarkers(double lat, double lng) {
        view.hideUpdatePlacesButton();

        if (dataDefsShownInDrawer != null) {
            for (DataDef dataDef : dataDefsShownInDrawer) {
                if (dataDef.isEnabled()) {
                    Log.i("refreshMarkers", "Refreshing data shown for datadef " + dataDef.getUri());
                    fetchNewPlaces(view, dataDef, lat, lng, getRadiusFromMap());
                }
            }
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
            view.hideProgressBar();

            if (result.hasError()) {
                if (view != null)
                    view.showMessage("An error occurred when fetching places: " + result.getError().getMessage());

                Log.w("FetchPlacesAT", "onPostExecute", result.getError());
                return;
            }

            Log.v("postFetchPlaces", "Got " + result.getResult().size() + " places from datadef" + dataDef.getUri());
            for (Place place : result.getResult()) {
                Log.v("    result    ", place.toString());
            }
            // TODO for production do not delete markers - just add new ones - merge
            view.replaceMapMarkers(dataDef, result.getResult());
        }
    }
}
