package cz.melkamar.andruian.viewlink.ui.main;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import cz.melkamar.andruian.viewlink.R;
import cz.melkamar.andruian.viewlink.data.persistence.AppDatabase;
import cz.melkamar.andruian.viewlink.data.persistence.DaoHelper;
import cz.melkamar.andruian.viewlink.data.place.IndexServerPlaceFetcher;
import cz.melkamar.andruian.viewlink.data.place.PlaceFetcher;
import cz.melkamar.andruian.viewlink.data.place.SparqlPlaceFetcher;
import cz.melkamar.andruian.viewlink.model.datadef.DataDef;
import cz.melkamar.andruian.viewlink.model.place.Place;
import cz.melkamar.andruian.viewlink.ui.base.BasePresenterImpl;
import cz.melkamar.andruian.viewlink.util.AsyncTaskResult;
import cz.melkamar.andruian.viewlink.util.Util;

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
//        view.showMessage("add datasource: "+ DataDefHelperProvider.getDataDefHelper().getHttpFileAsync("someUrl"));
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
        Log.v("MainPresenter", "refreshDatadefsShownInDrawer");
        new RefreshDdfInDrawerAT(view, this).execute();
    }


    private static class RefreshDdfInDrawerAT extends AsyncTask<Void, Void, AsyncTaskResult<List<DataDef>>> {
        private final MainMvpView view;
        private final MainPresenter presenter;

        private RefreshDdfInDrawerAT(MainMvpView view, MainPresenter presenter) {
            this.view = view;
            this.presenter = presenter;
        }

        @Override
        protected AsyncTaskResult<List<DataDef>> doInBackground(Void... voids) {
            try {
                return new AsyncTaskResult<>(DaoHelper.readAllDatadefs(view.getViewLinkApplication().getAppDatabase()));
            } catch (Exception e) {
                return new AsyncTaskResult<>(e);
            }
        }

        @Override
        protected void onPostExecute(AsyncTaskResult<List<DataDef>> result) {
            if (result.hasError()) {
                Log.w("refDatadefsShownDrawer", "An error occurred", result.getError());
            } else {
                for (DataDef dataDef : result.getResult()) {
                    Log.v("refreshDatadefsSID", dataDef.toString());
                }
                presenter.dataDefsShownInDrawer = result.getResult();
                view.showDataDefsInDrawer(result.getResult());
            }
        }
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
    public void dataDefSwitchClicked(SwitchCompat switchButton, int itemId, boolean enabled) {
        Log.d("dataDefSwitchClicked", "Enabled: " + enabled + "  for uri " + dataDefsShownInDrawer.get(itemId));
        DataDef dataDef = dataDefsShownInDrawer.get(itemId);
        dataDef.setEnabled(enabled);
        setSwitchButtonColor(switchButton, dataDef, enabled);

        new SaveDataDefATask(dataDefsShownInDrawer.get(itemId), view.getViewLinkApplication().getAppDatabase()).execute();

        // TODO get all places around location
        if (enabled) {
            // TODO add progressbar to the main activity while loading markers
            if (lastLocation != null) {
                fetchNewPlaces(view, dataDefsShownInDrawer.get(itemId),
                        view.getMap().getCameraPosition().target.latitude,
                        view.getMap().getCameraPosition().target.longitude,
                        getRadiusFromMap()); // TODO what radius?
            }
        } else {
            view.clearMapMarkers(dataDefsShownInDrawer.get(itemId));
        }
    }

    /**
     * Change color of the navigation drawer switch button. When disabled, use predefined gray values.
     * When enabled, use the {@link DataDef} marker color as the thumb color and calculate a slightly
     * darker version for the track color.
     */
    private void setSwitchButtonColor(SwitchCompat switchButton, DataDef dataDef, boolean enabled) {
        float hsv[] = new float[]{dataDef.getMarkerColor(), 1, 0.8f};
        int trackColor = Color.HSVToColor(hsv);

        switchButton.getThumbDrawable().setColorFilter(
                enabled ? Util.colorFromHue(dataDef.getMarkerColor())
                        : view.getActivity().getResources().getColor(R.color.switch_disabled_thumb), PorterDuff.Mode.MULTIPLY);

        switchButton.getTrackDrawable().setColorFilter(
                enabled ? trackColor :
                        view.getActivity().getResources().getColor(R.color.switch_disabled_track), PorterDuff.Mode.MULTIPLY);
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
    public void onMapCameraIdle(GoogleMap googleMap) {
        LatLng mapPosition = view.getMap().getCameraPosition().target;
        refreshMarkers(mapPosition.latitude, mapPosition.longitude);
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

    static class SaveDataDefATask extends AsyncTask<Void, Void, AsyncTaskResult<Object>> {
        private final DataDef dataDef;
        private final AppDatabase appDatabase;

        SaveDataDefATask(DataDef dataDef, AppDatabase appDatabase) {
            this.dataDef = dataDef;
            this.appDatabase = appDatabase;
        }


        @Override
        protected AsyncTaskResult<Object> doInBackground(Void... voids) {
            appDatabase.dataDefDao().update(dataDef);
            return new AsyncTaskResult<>("ok");
        }

        @Override
        protected void onPostExecute(AsyncTaskResult<Object> result) {
            if (result.hasError()) {
                Log.e("updateDdf", result.getError().getMessage(), result.getError());
                return;
            }

            Log.d("postDdfUpdate", "Saved datadef");

        }
    }
}
