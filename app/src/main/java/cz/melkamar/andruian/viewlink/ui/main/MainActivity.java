package cz.melkamar.andruian.viewlink.ui.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Switch;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import cz.melkamar.andruian.viewlink.ui.srcmgr.DatasourcesActivity;
import cz.melkamar.andruian.viewlink.util.LocationHelper;


public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, MainMvpView, LocationListener, OnMapReadyCallback {

    private final static String TAG_MAP_POSITION = "map_position";

    private MainMvpPresenter presenter;
    private LocationHelper locationHelper;
    private GoogleMap map;

    private boolean centerMapOnNextLocation = false;
    private boolean keepMapCentered = false;
    private CameraPosition preferredCameraPosition = null; // If non-null, set map to this position as soon as possible (after it's loaded)

    @BindView(R.id.fab) protected FloatingActionButton fab;
    @BindView(R.id.progressbar) protected ProgressBar progressBar;
    @BindView(R.id.update_places_btn) protected Button updatePlacesButton;

    @BindDrawable(R.drawable.ic_location_searching_black_24dp) protected Drawable iconGpsSearching;
    @BindDrawable(R.drawable.ic_gps_fixed_black_24dp) protected Drawable iconGpsLocked;

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

        presenter = new MainPresenter(this);
        locationHelper = new LocationHelper(this, this);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        presenter.refreshDatadefsShownInDrawer();
        // TODO on activity resume check already-displayed colors of markers vs what is defined in DataDefs - in case the user changed a color
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.v("MainActivity", "onRestoreInstanceState");
        preferredCameraPosition = savedInstanceState.getParcelable(TAG_MAP_POSITION);
        Log.v("onRestoreInstanceState", "preferredCameraPosition: " + preferredCameraPosition);
        if (map != null) {
            map.animateCamera(CameraUpdateFactory.newCameraPosition(preferredCameraPosition));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (map != null)
            outState.putParcelable(TAG_MAP_POSITION, map.getCameraPosition());

        super.onSaveInstanceState(outState);
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
            MenuItem menuItem = menu.add(R.id.nav_group_datasources, Menu.NONE, Menu.NONE, dataDef.getUri());

            View menuItemView = getLayoutInflater().inflate(R.layout.switch_item, null);

            // To each Switch assign its position in the Drawer (=position in array of DataDefs)
            menuItemView.findViewById(R.id.nav_switch).setTag(R.id.tag_switch_drawer_pos, i);
            menuItem.setActionView(menuItemView);
            menuItem.setCheckable(true);

            Switch switchButton = menuItemView.findViewById(R.id.nav_switch);
            switchButton.setOnCheckedChangeListener((compoundButton, b) -> {
                presenter.dataDefSwitchClicked((int) compoundButton.getTag(R.id.tag_switch_drawer_pos), b);
            });
            switchButton.setChecked(dataDef.isEnabled());

            i++;
        }
    }

    /*
     ***********************************************************************************************
     * MAP and markers related functionality
     */
    Map<DataDef, List<Marker>> markers = new HashMap<>();

    @Override
    public void clearMapMarkers() {
        // TODO
//        map.getProjection().getVisibleRegion().
    }

    @Override
    public void clearMapMarkers(DataDef dataDef) {
        Log.d("clearMapMarkers", "Removing markers " + dataDef.getUri());
        List<Marker> markersOfDatadef = markers.get(dataDef);
        if (markersOfDatadef == null) return;

        for (Marker marker : markersOfDatadef) {
            marker.remove();
        }
    }

    @Override
    public void addMapMarkers(List<Place> places) {
        if (map == null) {
            Log.e("addMapMarkers", "map is null!");
            return;
        }
        Log.d("addMapMarkers", "Adding " + places.size() + " markers");
        for (Place place : places) {
            Marker marker = map.addMarker(new MarkerOptions()
                    .position(new LatLng(place.getLatitude(), place.getLongitude()))
                    .title(place.getDisplayName())
                    .icon(BitmapDescriptorFactory.defaultMarker(place.getParentDatadef().getMarkerColor()))
            );
            marker.setTag(place);
            List<Marker> markersOfDatadef = markers.get(place.getParentDatadef());
            if (markersOfDatadef == null) {
                markersOfDatadef = new ArrayList<>();
                markers.put(place.getParentDatadef(), markersOfDatadef);
            }
            markersOfDatadef.add(marker);
        }
    }

    @Override
    public void replaceMapMarkers(DataDef dataDef, List<Place> places) {
        clearMapMarkers(dataDef);
        addMapMarkers(places);
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

        Log.d("onMapReady", "Permissions ok");
        googleMap.setMyLocationEnabled(true); // Permissions are always granted here
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);

        setKeepMapCentered(true);
        googleMap.setOnCameraMoveStartedListener(reason -> {
            if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                Log.d("cameraMovedListener", "stopping centering camera");
                setKeepMapCentered(false);
            }

            presenter.onMapCameraMoved(map, reason);
        });

        googleMap.setOnInfoWindowClickListener(marker -> {
            Intent i = new Intent(this, PlaceDetailActivity.class);
            i.putExtra(PlaceDetailActivity.TAG_DATA_PLACE, (Place)marker.getTag());
            startActivity(i);
        });
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Log.i("onOptionsItemSelected", id + "");

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            new MyTask(this).execute();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Just a quick dirty helper method for deleting the database of DataDefs.
     */
    static class MyTask extends AsyncTask<Void, Void, String> {
        MainActivity activity;

        public MyTask(MainActivity activity) {
            this.activity = activity;
        }

        @Override
        protected String doInBackground(Void... voids) {
            int before = activity.getViewLinkApplication().getAppDatabase().dataDefDao().getAll().size();
            activity.getViewLinkApplication().getAppDatabase().dataDefDao().deleteAll();
            int after = activity.getViewLinkApplication().getAppDatabase().dataDefDao().getAll().size();
            return "Deleted datadefs:\n" + before + " -> " + after;
        }

        @Override
        protected void onPostExecute(String s) {
            activity.showMessage(s);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // TODO save map location and restore it onResume
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
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
