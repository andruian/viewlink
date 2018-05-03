package cz.melkamar.andruian.viewlink.ui.main;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.melkamar.andruian.viewlink.R;
import cz.melkamar.andruian.viewlink.data.location.LocationHelper;
import cz.melkamar.andruian.viewlink.data.location.LocationHelperProvider;
import cz.melkamar.andruian.viewlink.data.persistence.AppDatabase;
import cz.melkamar.andruian.viewlink.data.persistence.DaoHelper;
import cz.melkamar.andruian.viewlink.data.place.PlaceFetcher;
import cz.melkamar.andruian.viewlink.data.place.PlaceFetcherProvider;
import cz.melkamar.andruian.viewlink.exception.PermissionException;
import cz.melkamar.andruian.viewlink.model.datadef.DataDef;
import cz.melkamar.andruian.viewlink.ui.base.BasePresenterImpl;
import cz.melkamar.andruian.viewlink.util.AsyncTaskResult;

import static android.content.Context.MODE_PRIVATE;

/**
 * TODO document this.
 */

public class MainPresenterImpl extends BasePresenterImpl implements MainPresenter {
    private MainView view;
    private List<DataDef> dataDefsShownInDrawer = null; // To keep track of what is shown, so we can enable/disable it

    private LocationHelper locationHelper;

    private boolean prefAutoRefreshMarkers = true;
    private boolean refreshMarkersWhenDdfReady = false; // If true, refresh markers shown as soon as datadefs are loaded

    private MapViewPort lastRefreshedArea = null;
    private Map<DataDef, FetchPlacesAT> fetchPlacesTasks = new HashMap<>();
    private int lastCameraMoveReason = 0;

    /**
     * For each shown datadef keep track of whether markers or clusters are shown.
     * This is used to determine if zooming in should trigger a map refresh.
     * <p>
     * TODO maybe keep a separate MapViewPort so that each DataDef is refreshed independently?
     */
    private Map<DataDef, Integer> mapElementTypesShown = new HashMap<>();

    public static final String KEY_PREF_AUTO_REFRESH = "settings_autorefresh_map";
    public static final int AUTO_ZOOM_THRESHOLD = 13;
    public static final String KEY_SHAREDPREFS = "cz.melkamar.andruian.viewlink.PREFERENCES";

    public MainPresenterImpl(MainView view) {
        super(view);
        this.view = view;
        locationHelper = LocationHelperProvider.getProvider().getInstance(view.getActivity(), new MainLocationListener(this));
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
        try {
            locationHelper.startReportingGps();
        } catch (PermissionException e) {
            e.printStackTrace();
        }
        updatePrefs();
    }

    @Override
    public void onViewDetached() {
        this.view = null;
        locationHelper.stopReportingGps();
        for (FetchPlacesAT fetchPlacesAT : fetchPlacesTasks.values()) {
            Log.v("MainPresenterImpl", "onViewDetached - cancelling fetch task for " + fetchPlacesAT.dataDef.getUri());
            fetchPlacesAT.cancel(true);
        }
    }

    @Override
    public void onFabClicked() {
        keepCameraCentered();
    }

    public void keepCameraCentered() {
        view.setKeepMapCenteredIcons(true);
        shouldKeepMapCentered = true;
        centerCamera();
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
        view.setSwitchButtonColor(switchButton, dataDef.getMarkerColor(), enabled);

        new SaveDataDefATask(dataDefsShownInDrawer.get(itemId), view.getViewLinkApplication().getAppDatabase()).execute();

        if (enabled) {
            if (view.getMap() != null) {
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


    private void fetchNewPlaces(MainView view, DataDef dataDef, double latitude, double longitude, double radius) {
        Log.i("fetchNewPlaces", dataDef.getUri() + " at " + latitude + "," + longitude + " (" + radius + ")");
        PlaceFetcher placeFetcher = PlaceFetcherProvider.getProvider().getInstance();
        FetchPlacesAT task = fetchPlacesTasks.get(dataDef);

        // TODO handle cancelling a running request. The following still waits for the request to
        // finish, but then cancels it before it can return a result. Also, it results in the
        // request falling back to the slow naive query.
//        if (task != null) {
//            Log.v("MainPresenterImpl", "fetchNewPlaces - cancelling task for " + dataDef.getUri());
//            task.cancel(true);
//        }
        task = new FetchPlacesAT(placeFetcher, view, this, dataDef, latitude, longitude, radius);
        fetchPlacesTasks.put(dataDef, task);
        task.execute();
        view.showProgressBar();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        try {
            locationHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
        } catch (PermissionException e) {
            Log.w("req perms result", "GPS not permitted.", e);
            view.showMessage(view.getActivity().getString(R.string.gps_not_granted));
            return;
        }

        switch (requestCode) {
            case LocationHelper.LOC_REQUEST:
                setMapMyLocationEnabled();
                break;

        }
    }

    private boolean mapFinishedSetup = false;

    private void setMapMyLocationEnabled() {
        if (ActivityCompat.checkSelfPermission(view.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(view.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (view.getMap() != null) {
            view.getMap().setMyLocationEnabled(true);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.v("MainPresenterImpl", "onMapReady");

        if (!locationHelper.checkPermissions()) {
            locationHelper.requestPermissions();
            Log.w("onMapReady", "Requesting permissions");
        }

        setMapMyLocationEnabled();

        if (preferredCameraPosition == null) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(50.07644607071266, 14.43346828222275), 17));
            keepCameraCentered();
        } else {
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(preferredCameraPosition));
        }

        if (updateMarkersWhenPossible) {
            Log.d("MainPresenterImpl", "onMapReady - updating places");
            refreshMarkers();
        }

        mapFinishedSetup = true;
    }

    @Override
    public void onMapCameraMoved(GoogleMap map, int reason) {
        lastCameraMoveReason = reason;
        lastDelayedRefreshTask = null;

        if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
            Log.d("cameraMovedListener", "stopping centering camera");
            shouldKeepMapCentered = false;
            centerMapOnNextLocation = false;
            if (view != null) {
                view.showUpdatePlacesButton();
                view.setKeepMapCenteredIcons(false);
            }
        }
    }

    @Override
    public void onMapCameraIdle(GoogleMap googleMap) {
        Log.v("MainPresenterImpl", "onCameraIdle - lastReason " + lastCameraMoveReason);
        if (lastCameraMoveReason != GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE &&
                lastCameraMoveReason != GoogleMap.OnCameraMoveStartedListener.REASON_DEVELOPER_ANIMATION) {
            return;
        }

        view.reclusterMarkers();

        if (prefAutoRefreshMarkers) {
            if (lastRefreshedArea != null) {
                LatLngBounds bounds = view.getMap().getProjection().getVisibleRegion().latLngBounds;
                MapViewPort newViewPort = new MapViewPort(bounds.northeast, bounds.southwest);
                if (lastRefreshedArea.contains(newViewPort.getMustBeVisiblePort())) {
                    if (mapElementTypesShown.containsValue(PlaceFetcher.FetchPlacesResult.RESULT_TYPE_CLUSTERS)) {
                        Log.v("MainPresenterImpl", "onMapCameraIdle - new viewport is contained in the old but clusters shown, refreshing");
                    } else {
                        Log.v("MainPresenterImpl", "onMapCameraIdle - new viewport is contained in the old, not refreshing");
                        return;
                    }
                }
                Log.v("MainPresenterImpl", "onMapCameraIdle - new viewport is NOT contained in the old, refreshing");
            } else {
                Log.v("MainPresenterImpl", "onMapCameraIdle - lastRefreshedArea is null, refreshing");
            }

            LatLng mapPosition = view.getMap().getCameraPosition().target;
            new RefreshMarkersDelayedAT(mapPosition.latitude, mapPosition.longitude, this).execute();
        } else {
            view.showUpdatePlacesButton();
        }
    }

    RefreshMarkersDelayedAT lastDelayedRefreshTask = null;

    /**
     * This is used when the user moves a map. Instead of calling the refresh method directly,
     * we first want to wait a small period of time to make sure no further movement will happen.
     *
     * 1. set presenter's lastDelayedRefreshTask to the asynctask object
     * 2. wait some time
     * 3. compare the lastDelayedRefreshTask to self. If matching, refresh. If not, other task is
     *    already running, so do nothing.
     */
    static class RefreshMarkersDelayedAT extends AsyncTask<Void, Void, Void> {
        private final double lat;
        private final double lng;
        private final MainPresenterImpl presenter;

        RefreshMarkersDelayedAT(double lat, double lng, MainPresenterImpl presenter) {
            this.lat = lat;
            this.lng = lng;
            this.presenter = presenter;
            presenter.lastDelayedRefreshTask = this;
        }


        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (presenter.lastDelayedRefreshTask == this){
                presenter.refreshMarkers(lat, lng);
            }
        }
    }


    private boolean centerMapOnNextLocation = false;
    private boolean shouldKeepMapCentered = false;
    private CameraPosition preferredCameraPosition = null; // If non-null, set map to this position as soon as possible (after it's loaded)
    private boolean updateMarkersWhenPossible = false;

    public void onLocationChanged(Location location) {
        // When the location changes, call the map camera idle method, because that is only
        // called by the view when the user moves it manually - but in this case we want to update
        // it. We still dont want to call the onCameraIdle on every map movement though, for
        // example tapping a marker makes the camera move and we do not want to refresh markers then.

//        if (view.getMap() != null)
//            onMapCameraIdle(view.getMap());


        if (location != null) {
            Log.v("onLocationChanged", "[" + location.getLatitude() + "," + location.getLongitude() + "] shouldKeepMapCentered: " + shouldKeepMapCentered + " | centerOnNextLoc: " + centerMapOnNextLocation);
            if (shouldKeepMapCentered) centerMapOnNextLocation = true;
            if (centerMapOnNextLocation) centerCamera();
        }
    }

    // For testing - to read the camera position. Cannot read map directly because not on main thread
    public LatLng lastCameraUpdatePosition;

    public void centerCamera() {
        Log.v("MainPresenterImpl", "centerCamera");
        centerMapOnNextLocation = false;
        if (!locationHelper.isReportingGps()) {
            try {
                locationHelper.startReportingGps();
            } catch (PermissionException e) {
                Log.w("centerMapOCLoc", "GPS not permitted", e);
                return;
            }
        }

        if (view.getMap() != null && locationHelper.getLastKnownLocation() != null) {
            lastCameraUpdatePosition = new LatLng(locationHelper.getLastKnownLocation().getLatitude(),
                    locationHelper.getLastKnownLocation().getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(lastCameraUpdatePosition);
            view.getMap().animateCamera(cameraUpdate, 500, null);
        } else {
            centerMapOnNextLocation = true;
        }
    }

    @Override
    public void onUpdatePlacesButtonClicked() {
        if (view == null) return;

        if (view.getMap() != null) {
            refreshMarkers();
        } else {
            updateMarkersWhenPossible = true;
        }
    }

    @Override
    public void onSaveMapPosition() {
        if (!mapFinishedSetup) return;

        Log.d("MainPresenterImpl", "onSaveMapPosition");
        GoogleMap map = view.getMap();

        if (map != null) {
            SharedPreferences prefs = view.getActivity().getSharedPreferences(KEY_SHAREDPREFS, MODE_PRIVATE);
            prefs.edit()
                    .putLong("lat", Double.doubleToRawLongBits(map.getCameraPosition().target.latitude))
                    .putLong("long", Double.doubleToRawLongBits(map.getCameraPosition().target.longitude))
                    .putFloat("zoom", map.getCameraPosition().zoom)
                    .putBoolean("keepCentered", shouldKeepMapCentered)
                    .apply();
        }
    }

    @Override
    public void onRestoreMapPosition() {
        SharedPreferences prefs = view.getActivity().getSharedPreferences(KEY_SHAREDPREFS, MODE_PRIVATE);
        if (prefs.contains("lat") && prefs.contains("long") && prefs.contains("zoom")) {
            long latL = prefs.getLong("lat", Long.MAX_VALUE);
            long lngL = prefs.getLong("long", Long.MAX_VALUE);

            // Default values in case nothing was saved
            double lat = defaultLat;
            double lng = defaultLng;
            float zoom = prefs.getFloat("zoom", defaultZoom);
            if (latL != Long.MAX_VALUE) {
                lat = Double.longBitsToDouble(latL);
                lng = Double.longBitsToDouble(lngL);
            }

            boolean keepCentered = prefs.getBoolean("keepCentered", true);
            Log.d("MainPresenterImpl", "onResume - restoring map position: " + lat + "," + lng + "(" + zoom + "). Center: " + keepCentered + ". map: " + view.getMap());

            view.setKeepMapCenteredIcons(keepCentered);

            if (view.getMap() != null) {
                view.getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), zoom));
            } else {
                preferredCameraPosition = CameraPosition.fromLatLngZoom(new LatLng(lat, lng), zoom);
            }

            refreshMarkers(lat, lng);
        } else {
            Log.d("MainPresenterImpl", "onResume - not restoring map position");
            preferredCameraPosition = CameraPosition.fromLatLngZoom(new LatLng(defaultLat, defaultLng), defaultZoom);
        }
    }

    private static final double defaultLat = 50.072445;
    private static final double defaultLng = 14.438272;
    private static final float defaultZoom = 15;

    private void refreshMarkers() {
        LatLng mapPosition = view.getMap().getCameraPosition().target;
        refreshMarkers(mapPosition.latitude, mapPosition.longitude);
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
    public void onPlacesFetched(DataDef dataDef, PlaceFetcher.FetchPlacesResult result, FetchPlacesAT task) {
        FetchPlacesAT t = fetchPlacesTasks.get(dataDef);
        // Only remove the datadef entry from running tasks is the task that just ended is the last one
        // When multiple tasks overlap, wait for the latest one. It may not always be the longest running,
        // but until we figure out how to immediately cancel a running task, this will have to do.
        if (t == task) {
            fetchPlacesTasks.remove(dataDef);
        }

        if (fetchPlacesTasks.size() == 0)
            view.hideProgressBar();

        if (result != null) {
            mapElementTypesShown.put(dataDef, result.resultType);
        }
    }

    static class FetchPlacesAT extends AsyncTask<Void, Void, AsyncTaskResult<PlaceFetcher.FetchPlacesResult>> {
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
        protected AsyncTaskResult<PlaceFetcher.FetchPlacesResult> doInBackground(Void... voids) {
            try {
                PlaceFetcher.FetchPlacesResult result = placeFetcher.fetchPlaces(view, dataDef, latitude, longitude, radius);
                return new AsyncTaskResult<>(result);
            } catch (Exception e) {
                e.printStackTrace();
                return new AsyncTaskResult<>(e);
            }
        }

        @Override
        protected void onPostExecute(AsyncTaskResult<PlaceFetcher.FetchPlacesResult> result) {
            if (result.hasError()) {
                presenter.onPlacesFetched(dataDef, null, this);

                if (view != null)
                    view.showMessage(view.getActivity().getString(R.string.error_fetching_places) + result.getError().getMessage());

                Log.w("FetchPlacesAT", "onPostExecute", result.getError());
                return;
            }

            Log.v("postFetchPlaces", "Got " + result.getResult().places.size() + " elements. Type: " + result.getResult().resultType + "from datadef" + dataDef.getUri());
            presenter.onPlacesFetched(dataDef, result.getResult(), this);

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

    public void setDataDefsShownInDrawer(List<DataDef> datadefs) {
        this.dataDefsShownInDrawer = datadefs;
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
                presenter.setDataDefsShownInDrawer(result.getResult());
                view.showDataDefsInDrawer(result.getResult());

                if (presenter.isRefreshMarkersWhenDdfReady()) {
                    Log.d("MainPresenterImpl", "RefreshDdfInDrawerAT - refreshing shown markers");
                    presenter.onUpdatePlacesButtonClicked();
                }
            }
        }
    }

    private static class MainLocationListener implements LocationListener {
        private final MainPresenterImpl presenter;

        private MainLocationListener(MainPresenterImpl presenter) {
            this.presenter = presenter;
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.v("MainLocationListener", "onLocationChanged");
            presenter.onLocationChanged(location);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }
}
