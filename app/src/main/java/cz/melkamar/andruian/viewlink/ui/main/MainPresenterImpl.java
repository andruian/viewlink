package cz.melkamar.andruian.viewlink.ui.main;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Location;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

public class MainPresenterImpl extends BasePresenterImpl implements MainPresenter {
    private MainView view;
    private List<DataDef> dataDefsShownInDrawer = null; // To keep track of what is shown, so we can enable/disable it
    private Location lastLocation = null;

    private boolean prefAutoRefreshMarkers = true;
    private boolean refreshMarkersWhenDdfReady = false; // If true, refresh markers shown as soon as datadefs are loaded

    private MapViewPort lastRefreshedArea = null;
    private Map<DataDef, FetchPlacesAT> fetchPlacesTasks = new HashMap<>();

    public static final String KEY_PREF_AUTO_REFRESH = "settings_autorefresh_map";
    public static final int AUTO_ZOOM_THRESHOLD = 13;

    public MainPresenterImpl(MainView view) {
        super(view);
        this.view = view;
        updatePrefs();
    }

    public boolean isRefreshMarkersWhenDdfReady() {
        return refreshMarkersWhenDdfReady;
    }

    @Override
    public void manageDataSources() {
        Log.i("manageDataSources", "foo");
//        view.showMessage("add datasource: "+ DataDefHelperProvider.getDataDefHelper().getHttpFileAsync("someUrl"));
        view.showManageDatasourcesActivity();
    }

    protected void updatePrefs() {
        this.prefAutoRefreshMarkers = PreferenceManager.getDefaultSharedPreferences(view.getActivity()).getBoolean(KEY_PREF_AUTO_REFRESH, true);

        Log.v("MainPresenterImpl", "updatePrefs | prefAutoRefreshMarkers " + this.prefAutoRefreshMarkers);
    }

    @Override
    public void onViewAttached(MainView view) {
        this.view = view;
        updatePrefs();
    }

    @Override
    public void onViewDetached() {
        this.view = null;
        for (FetchPlacesAT fetchPlacesAT : fetchPlacesTasks.values()) {
            Log.v("MainPresenterImpl", "onViewDetached - cancelling fetch task for " + fetchPlacesAT.dataDef.getUri());
            fetchPlacesAT.cancel(true);
        }
    }

    @Override
    public void onFabClicked() {
        view.setKeepMapCentered(true);
    }

    @Override
    public void refreshDatadefsShownInDrawer() {
        Log.v("MainPresenterImpl", "refreshDatadefsShownInDrawer");
        new RefreshDdfInDrawerAT(view, this).execute();
    }

    private double getRadiusFromMap() {
        // Calculate radius to show as the distance from the middle of the map to the border
        //  - whichever direction is longer
        LatLng northeast = view.getMap().getProjection().getVisibleRegion().latLngBounds.northeast;
        LatLng camTarget = view.getMap().getCameraPosition().target;
        double radius = Math.max(
                Math.abs(northeast.latitude - camTarget.latitude),
                Math.abs(northeast.longitude - camTarget.longitude));

        // Sometimes it happens that the camTarget gives normal values but northeast contains 0,0
        // -- not sure why and when, so just to avoid weird behavior, replace radius with a reasonably
        // small value when that happens.
        if (Math.abs(northeast.longitude) < 0.0001 && Math.abs(northeast.latitude) < 0.0001 &&
                Math.abs(camTarget.latitude) > 1 && Math.abs(camTarget.longitude) > 1) {
            Log.wtf("MainPresenterImpl", "getRadiusFromMap - northeast gives 0,0 value :(");
            radius = 0.001;
        }
        Log.v("MainPresenterImpl", "getRadiusFromMap " + radius + " (NE: " + northeast + ", tgt: " + camTarget + ")");
        return radius;
    }

    @Override
    public void dataDefSwitchClicked(SwitchCompat switchButton, int itemId, boolean enabled) {
        Log.d("dataDefSwitchClicked", "Enabled: " + enabled + "  for uri " + dataDefsShownInDrawer.get(itemId));
        DataDef dataDef = dataDefsShownInDrawer.get(itemId);
        dataDef.setEnabled(enabled);
        setSwitchButtonColor(switchButton, dataDef, enabled);

        new SaveDataDefATask(dataDefsShownInDrawer.get(itemId), view.getViewLinkApplication().getAppDatabase()).execute();

        if (enabled) {
            if (view.getMap() != null && view.getMap().getCameraPosition().zoom > AUTO_ZOOM_THRESHOLD) {
                Log.d("dataDefSwitchClicked", "fetching places. Radius: " + getRadiusFromMap());
                fetchNewPlaces(view, dataDefsShownInDrawer.get(itemId),
                        view.getMap().getCameraPosition().target.latitude,
                        view.getMap().getCameraPosition().target.longitude,
                        getRadiusFromMap());
            } else {
                view.showUpdatePlacesButton();
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
    @Override
    public void setSwitchButtonColor(SwitchCompat switchButton, DataDef dataDef, boolean enabled) {
        float hsv[] = new float[]{dataDef.getMarkerColor(), 1, 0.8f};
        int trackColor = Color.HSVToColor(hsv);

        switchButton.getThumbDrawable().setColorFilter(
                enabled ? Util.colorFromHue(dataDef.getMarkerColor())
                        : view.getActivity().getResources().getColor(R.color.switch_disabled_thumb), PorterDuff.Mode.MULTIPLY);

        switchButton.getTrackDrawable().setColorFilter(
                enabled ? trackColor :
                        view.getActivity().getResources().getColor(R.color.switch_disabled_track), PorterDuff.Mode.MULTIPLY);
    }

    private void fetchNewPlaces(MainView view, DataDef dataDef, double latitude, double longitude, double radius) {
        Log.i("fetchNewPlaces", dataDef.getUri() + " at " + latitude + "," + longitude + " (" + radius + ")");
        PlaceFetcher placeFetcher = new PlaceFetcher(
                new IndexServerPlaceFetcher(),
                new SparqlPlaceFetcher()
        );
        FetchPlacesAT task = fetchPlacesTasks.get(dataDef);
        if (task != null){
            Log.v("MainPresenterImpl", "fetchNewPlaces - cancelling task for "+dataDef.getUri());
            task.cancel(true);
        }
        task = new FetchPlacesAT(placeFetcher, view, this, dataDef, latitude, longitude, radius);
        fetchPlacesTasks.put(dataDef, task);
        task.execute();
        view.showProgressBar();
    }

    @Override
    public void onMapCameraMoved(GoogleMap map, int reason) {
        if (view != null) {
            if (!view.isCameraFollowing() && !prefAutoRefreshMarkers) {
                view.showUpdatePlacesButton();
            }
        }
    }

    @Override
    public void onMapCameraIdle(GoogleMap googleMap) {
        if (googleMap.getCameraPosition().zoom > AUTO_ZOOM_THRESHOLD && prefAutoRefreshMarkers) {
            if (lastRefreshedArea != null) {
                LatLngBounds bounds = view.getMap().getProjection().getVisibleRegion().latLngBounds;
                MapViewPort newViewPort = new MapViewPort(bounds.northeast, bounds.southwest);
                if (lastRefreshedArea.contains(newViewPort.getMustBeVisiblePort())) {
                    Log.v("MainPresenterImpl", "onMapCameraIdle - new viewport is contained in the old, not refreshing");
                    return;
                }
                Log.v("MainPresenterImpl", "onMapCameraIdle - new viewport is NOT contained in the old, refreshing");
            } else {
                Log.v("MainPresenterImpl", "onMapCameraIdle - lastRefreshedArea is null, refreshing");
            }

            LatLng mapPosition = view.getMap().getCameraPosition().target;
            refreshMarkers(mapPosition.latitude, mapPosition.longitude);
        } else {
            view.showUpdatePlacesButton();
        }
    }

    @Override
    public void onLocationChanged(Location newLocation) {
        // When the location changes, call the map camera idle method, because that is only
        // called by the view when the user moves it manually - but in this case we want to update
        // it. We still dont want to call the onCameraIdle on every map movement though, for
        // example tapping a marker makes the camera move and we do not want to refresh markers then.

        if (view.isCameraFollowing() && view.getMap() != null)
            onMapCameraIdle(view.getMap());
    }

    @Override
    public void onUpdatePlacesButtonClicked() {
        if (view.getMap() != null) {
            LatLng mapPosition = view.getMap().getCameraPosition().target;
            refreshMarkers(mapPosition.latitude, mapPosition.longitude);
        } else {
            view.updateMarkersWhenPossible();
        }
    }

    /**
     * Fetch new places and refresh markers for all DataDefs currently ticked as enabled.
     */
    private void refreshMarkers(double lat, double lng) {
        Log.v("MainPresenterImpl", "refreshMarkers " + lat + "," + lng);
        view.hideUpdatePlacesButton();

        if (dataDefsShownInDrawer == null) {
            refreshMarkersWhenDdfReady = true;
            return;
        }

        if (view.getMap() != null) {
            LatLngBounds bounds = view.getMap().getProjection().getVisibleRegion().latLngBounds;
            lastRefreshedArea = new MapViewPort(bounds.northeast, bounds.southwest);
        }

        for (DataDef dataDef : dataDefsShownInDrawer) {
            if (dataDef.isEnabled()) {
                Log.i("MainPresenterImpl", "refreshMarkers - Refreshing data shown for datadef " + dataDef.getUri());
                fetchNewPlaces(view, dataDef, lat, lng, getRadiusFromMap());
            }
        }
    }

    @Override
    public void onPlacesFetched(DataDef dataDef) {
        fetchPlacesTasks.remove(dataDef);
        if (fetchPlacesTasks.size() == 0)
            view.hideProgressBar();
    }

    private static class FetchPlacesAT extends AsyncTask<Void, Void, AsyncTaskResult<List<Place>>> {
        private final PlaceFetcher placeFetcher;
        private final MainView view;
        private final MainPresenter presenter;
        private final DataDef dataDef;
        private final double latitude;
        private final double longitude;
        private final double radius;

        private FetchPlacesAT(PlaceFetcher placeFetcher, MainView view, MainPresenter presenter, DataDef dataDef, double latitude, double longitude, double radius) {
            this.placeFetcher = placeFetcher;
            this.view = view;
            this.presenter = presenter;
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
            presenter.onPlacesFetched(dataDef);

            if (result.hasError()) {
                if (view != null)
                    view.showMessage("An error occurred when fetching places: " + result.getError().getMessage());

                Log.w("FetchPlacesAT", "onPostExecute", result.getError());
                return;
            }

            Log.v("postFetchPlaces", "Got " + result.getResult().size() + " places from datadef" + dataDef.getUri());
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

    private static class RefreshDdfInDrawerAT extends AsyncTask<Void, Void, AsyncTaskResult<List<DataDef>>> {
        private final MainView view;
        private final MainPresenterImpl presenter;

        private RefreshDdfInDrawerAT(MainView view, MainPresenterImpl presenter) {
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

                if (presenter.isRefreshMarkersWhenDdfReady()) {
                    Log.d("MainPresenterImpl", "RefreshDdfInDrawerAT - refreshing shown markers");
                    presenter.onUpdatePlacesButtonClicked();
                }
            }
        }
    }
}
