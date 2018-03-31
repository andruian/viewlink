package cz.melkamar.andruian.viewlink.ui.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterManager;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import cz.melkamar.andruian.viewlink.R;
import cz.melkamar.andruian.viewlink.exception.PermissionException;
import cz.melkamar.andruian.viewlink.model.datadef.DataDef;
import cz.melkamar.andruian.viewlink.model.place.Place;
import cz.melkamar.andruian.viewlink.ui.base.BaseActivity;
import cz.melkamar.andruian.viewlink.ui.placedetail.PlaceDetailActivity;
import cz.melkamar.andruian.viewlink.ui.settings.SettingsActivity;
import cz.melkamar.andruian.viewlink.ui.srcmgr.DatasourcesActivity;
import cz.melkamar.andruian.viewlink.util.LocationHelper;


public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, MainView, LocationListener, OnMapReadyCallback {

    private final static String TAG_MAP_POSITION = "map_position";

    private MainPresenterImpl presenter;
    private LocationHelper locationHelper;
    private GoogleMap map;
    private MultiClusterListener<Place> clusterListener;
    Map<DataDef, ClusterManager<Place>> clusterMgrs = new HashMap<>();

    private boolean centerMapOnNextLocation = false;
    private boolean keepMapCentered = false;
    private CameraPosition preferredCameraPosition = null; // If non-null, set map to this position as soon as possible (after it's loaded)
    private boolean updateMarkersWhenPossible = false;
    private int lastCameraMoveReason = 0;

    @BindView(R.id.fab)
    protected FloatingActionButton fab;
    @BindView(R.id.progressbar)
    protected ProgressBar progressBar;
    @BindView(R.id.update_places_btn)
    protected Button updatePlacesButton;

    @BindDrawable(R.drawable.ic_location_searching_black_24dp)
    protected Drawable iconGpsSearching;
    @BindDrawable(R.drawable.ic_gps_fixed_black_24dp)
    protected Drawable iconGpsLocked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("MainActivity", "onCreate | saved bundle: " + savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> presenter.onFabClicked());
        updatePlacesButton.setOnClickListener(view -> presenter.onUpdatePlacesButtonClicked());

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        presenter = new MainPresenterImpl(this);
        locationHelper = new LocationHelper(this, this);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        presenter.refreshDatadefsShownInDrawer();
        // TODO on activity resume check already-displayed colors of markers vs what is defined in DataDefs - in case the user changed a color
    }

    public void centerCamera() {
        centerMapOnNextLocation = false;
        if (!locationHelper.isReportingGps()) {
            try {
                locationHelper.startReportingGps();
            } catch (PermissionException e) {
                Log.w("centerMapOCLoc", "GPS not permitted", e);
                showMessage("GPS permission not granted. Cannot provide location.");
                return;
            }
        }

        if (map != null && locationHelper.getLastKnownLocation() != null) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(
                    new LatLng(locationHelper.getLastKnownLocation().getLatitude(),
                            locationHelper.getLastKnownLocation().getLongitude()));
            map.animateCamera(cameraUpdate, 500, null);
        } else {
            centerMapOnNextLocation = true;
        }
    }

    @Override
    public void setKeepMapCentered(boolean keepCentered) {
        Log.d("setKeepMapCentered", keepCentered + "");
        keepMapCentered = keepCentered;

        if (keepCentered) {
            fab.setImageDrawable(iconGpsLocked);
            centerCamera();
        } else {
            fab.setImageDrawable(iconGpsSearching);
        }
    }

    /**
     * Show the given {@link DataDef} objects in the NavDrawer.
     *
     * @param dataDefList
     */
    @Override
    public void showDataDefsInDrawer(List<DataDef> dataDefList) {
        NavigationView navigationView = findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        menu.removeGroup(R.id.nav_group_datasources);
        int i = 0;
        for (DataDef dataDef : dataDefList) {
            MenuItem menuItem = menu.add(R.id.nav_group_datasources, Menu.NONE, Menu.NONE, dataDef.getLabel(Locale.getDefault().getLanguage()));

            View menuItemView = getLayoutInflater().inflate(R.layout.switch_item, null);

            // To each Switch assign its position in the Drawer (=position in array of DataDefs)
            menuItemView.findViewById(R.id.nav_switch).setTag(R.id.tag_switch_drawer_pos, i);
            menuItem.setActionView(menuItemView);
            menuItem.setCheckable(true);

            SwitchCompat switchButton = menuItemView.findViewById(R.id.nav_switch);
            switchButton.setChecked(dataDef.isEnabled());
            presenter.setSwitchButtonColor(switchButton, dataDef, dataDef.isEnabled());
            switchButton.setOnCheckedChangeListener((compoundButton, b) -> {
                presenter.dataDefSwitchClicked(switchButton, (int) compoundButton.getTag(R.id.tag_switch_drawer_pos), b);
            });


            i++;
        }
    }

    /*
     ***********************************************************************************************
     * MAP and markers related functionality
     */


    @Override
    public void clearMapMarkers() {
        // TODO
//        map.getProjection().getVisibleRegion().
    }

    @Override
    public void clearMapMarkers(DataDef dataDef) {
        Log.d("MainActivity", "clearMapMarkers - removing markers for" + dataDef.getUri());

        ClusterManager<Place> clusterManager = clusterMgrs.get(dataDef);
        if (clusterManager != null) {
            clusterManager.clearItems();
            clusterManager.cluster();
            Log.v("MainActivity", "clearMapMarkers - now size: " + clusterManager.getClusterMarkerCollection().getMarkers().size());
        } else {
            Log.v("MainActivity", "clearMapMarkers - clusterManager was null for " + dataDef.getUri());
        }
    }

    @Override
    public void addMapMarkers(DataDef datadef, List<Place> places) {
        if (map == null) {
            Log.e("addMapMarkers", "map is null!");
            return;
        }
        if (places == null || places.isEmpty()) {
            Log.d("addMapMarkers", "List empty.");
            return;
        }

        Log.d("addMapMarkers", "Adding " + places.size() + " markers");


        ClusterManager<Place> clusterManager = clusterMgrs.get(datadef);
        if (clusterManager == null) {
            clusterManager = new ClusterManager<>(this, map);
            clusterManager.setRenderer(new MarkerRenderer(datadef, this, map, clusterManager));
            clusterManager.setOnClusterItemInfoWindowClickListener(place -> {
                Log.v("MainActivity", "OnClusterItemInfoWindowClick " + place);
                Intent i = new Intent(this, PlaceDetailActivity.class);
                i.putExtra(PlaceDetailActivity.TAG_DATA_PLACE, place);
                startActivity(i);
            });

            clusterMgrs.put(datadef, clusterManager);
            clusterListener.addListener(datadef, clusterManager);
        }

        clusterManager.addItems(places);
        clusterManager.cluster();
    }

    @Override
    public void replaceMapMarkers(DataDef dataDef, List<Place> places) {
        clearMapMarkers(dataDef);
        addMapMarkers(dataDef, places);
    }

    @Override
    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showUpdatePlacesButton() {
        updatePlacesButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideUpdatePlacesButton() {
        updatePlacesButton.setVisibility(View.GONE);
    }

    @Override
    public boolean isCameraFollowing() {
        return keepMapCentered;
    }

    @Override
    public GoogleMap getMap() {
        return map;
    }

    @Override
    public void updateMarkersWhenPossible(){
        if (map == null) {
            updateMarkersWhenPossible = true;
            return;
        }

        presenter.onUpdatePlacesButtonClicked();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.v("onMapReady", "onMapReady");
        this.map = googleMap;
        if (!locationHelper.checkPermissions()) {
            locationHelper.requestPermissions();
            Log.w("onMapReady", "Requesting permissions");
            return;
        }

        Log.d("onMapReady", "Permissions ok. PreferredCameraPosition: " + preferredCameraPosition);
        googleMap.setMyLocationEnabled(true); // Permissions are always granted here
//        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(49.747283, 13.387336), 15), 250, null);
        if (preferredCameraPosition == null) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(50.07644607071266, 14.43346828222275), 17));
            setKeepMapCentered(true);
        } else {
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(preferredCameraPosition));
        }

        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);


        googleMap.setOnCameraMoveStartedListener(reason -> {
            Log.v("MainActivity", "onMapCameraMoved " + reason);
            lastCameraMoveReason = reason;

            if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                Log.d("cameraMovedListener", "stopping centering camera");
                setKeepMapCentered(false);
                presenter.onMapCameraMoved(map, reason);
            }


        });

        // CLUSTERS
        clusterListener = new MultiClusterListener<>();
        googleMap.setOnCameraIdleListener(() ->
        {
            Log.v("MainActivity", "onCameraIdle - lastReason " + lastCameraMoveReason);
            if (lastCameraMoveReason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                presenter.onMapCameraIdle(map);
                clusterListener.onCameraIdle();
            }
        });

        googleMap.setOnInfoWindowClickListener(marker -> {
            Log.d("MainActivity", "onInfoWindowClick " + marker);
            clusterListener.onInfoWindowClick(marker);
        });

        if (updateMarkersWhenPossible){
            Log.d("MainActivity", "onMapReady - updating places");
            presenter.onUpdatePlacesButtonClicked();
        }
    }



    /*
     ***********************************************************************************************
     */

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (id == R.id.nav_manage_sources) {
            presenter.manageDataSources();
            return true;
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return false;
    }

    /**
     * Method is called by the Android framework when permission request result is returned.
     * <p>
     * MissingPermission warnings are supressed, because if the code flow gets to the switch clause,
     * we already have permissions. Otherwise the catch clause would be taken and method stopped earlier.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        try {
            locationHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
        } catch (PermissionException e) {
            Log.w("req perms result", "GPS not permitted.", e);
            showMessage("GPS permission not granted. Cannot provide location.");
            return;
        }

        switch (requestCode) {
            case LocationHelper.LOC_REQUEST_MAP:
                if (map != null) map.setMyLocationEnabled(true);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Log.i("onOptionsItemSelected", id + "");

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.onViewAttached(this);

        try {
            locationHelper.startReportingGps();
        } catch (PermissionException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        float zoom = restoreMapPosition();
        if (zoom > MainPresenterImpl.AUTO_ZOOM_THRESHOLD) {
            updateMarkersWhenPossible();
        }
    }

    protected float restoreMapPosition() {
        SharedPreferences prefs = this.getSharedPreferences("com.ex.app.loc", MODE_PRIVATE);
        if (prefs.contains("lat") && prefs.contains("long") && prefs.contains("zoom")) {
            double lat = Double.longBitsToDouble(prefs.getLong("lat", 0));
            double lng = Double.longBitsToDouble(prefs.getLong("long", 0));
            float zoom = prefs.getFloat("zoom", 10);
            boolean keepCentered = prefs.getBoolean("keepCentered", true);
            Log.d("MainActivity", "onResume - restoring map position: " + lat + "," + lng + "(" + zoom + "). Center: " + keepCentered+". map: "+map);

            setKeepMapCentered(keepCentered);

            if (map != null) {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), zoom));
            } else {
                preferredCameraPosition = CameraPosition.fromLatLngZoom(new LatLng(lat, lng), zoom);
            }
            return zoom;
        } else {
            Log.d("MainActivity", "onResume - not restoring map position");
            return -1;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveMapPosition();
    }

    protected void saveMapPosition() {
        if (map != null) {
            SharedPreferences prefs = this.getSharedPreferences("com.ex.app.loc", MODE_PRIVATE);
            prefs.edit()
                    .putLong("lat", Double.doubleToRawLongBits(map.getCameraPosition().target.latitude))
                    .putLong("long", Double.doubleToRawLongBits(map.getCameraPosition().target.longitude))
                    .putFloat("zoom", map.getCameraPosition().zoom)
                    .putBoolean("keepCentered", keepMapCentered)
                    .apply();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        locationHelper.stopReportingGps();
        presenter.onViewDetached();
    }

    @Override
    public void showManageDatasourcesActivity() {
        startActivity(new Intent(this, DatasourcesActivity.class));
    }


    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            Log.v("onLocationChanged", "[" + location.getLatitude() + "," + location.getLongitude() + "] keepCentered: " + keepMapCentered + " | centerOnNextLoc: " + centerMapOnNextLocation);
            if (keepMapCentered) centerMapOnNextLocation = true;
            if (centerMapOnNextLocation) centerCamera();
            presenter.onLocationChanged(location);
        }
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
