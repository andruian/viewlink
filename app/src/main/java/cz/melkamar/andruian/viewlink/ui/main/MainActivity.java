package cz.melkamar.andruian.viewlink.ui.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
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
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.ui.IconGenerator;
import com.google.maps.android.ui.SquareTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import cz.melkamar.andruian.viewlink.R;
import cz.melkamar.andruian.viewlink.model.datadef.DataDef;
import cz.melkamar.andruian.viewlink.model.place.Place;
import cz.melkamar.andruian.viewlink.ui.base.BaseActivity;
import cz.melkamar.andruian.viewlink.ui.placedetail.PlaceDetailActivity;
import cz.melkamar.andruian.viewlink.ui.settings.SettingsActivity;
import cz.melkamar.andruian.viewlink.ui.srcmgr.DatasourcesActivity;
import cz.melkamar.andruian.viewlink.util.Util;


public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, MainView, OnMapReadyCallback {


    private MainPresenter presenter;
    private GoogleMap map;
    private MultiClusterListener<Place> clusterListener;
    Map<DataDef, ClusterManager<Place>> clusterMgrs = new HashMap<>();
    Map<DataDef, List<Place>> placesStoredMap = new HashMap<>();
    Map<DataDef, List<Marker>> preclusteredMarkers = new HashMap<>();

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

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        presenter.refreshDatadefsShownInDrawer();
    }

    /**
     * Set icons to reflect the camera following or not following the user.
     * This affects the FAB button icon.
     *
     * @param keepCentered
     */
    @Override
    public void setKeepMapCenteredIcons(boolean keepCentered) {
        if (keepCentered) {
            fab.setImageDrawable(iconGpsLocked);
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
            setSwitchButtonColor(switchButton, dataDef.getMarkerColor(), dataDef.isEnabled());
            switchButton.setOnCheckedChangeListener((compoundButton, b) -> {
                presenter.dataDefSwitchClicked(switchButton, (int) compoundButton.getTag(R.id.tag_switch_drawer_pos), b);
            });


            i++;
        }
    }

    public Map<DataDef, List<Place>> getPlacesStoredMap() {
        return placesStoredMap;
    }

    /**
     * Change color of the navigation drawer switch button. When disabled, use predefined gray values.
     * When enabled, use the {@link DataDef} marker color as the thumb color and calculate a slightly
     * darker version for the track color.
     */
    @Override
    public void setSwitchButtonColor(SwitchCompat switchButton, float color, boolean enabled) {
        float hsv[] = new float[]{color, 1, 0.8f};
        int trackColor = Color.HSVToColor(hsv);

        switchButton.getThumbDrawable().setColorFilter(
                enabled ? Util.colorFromHue(color)
                        : getResources().getColor(R.color.switch_disabled_thumb), PorterDuff.Mode.MULTIPLY);

        switchButton.getTrackDrawable().setColorFilter(
                enabled ? trackColor :
                        getResources().getColor(R.color.switch_disabled_track), PorterDuff.Mode.MULTIPLY);
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

        List<Place> placesList = placesStoredMap.get(dataDef);
        if (placesList != null) {
            placesStoredMap.remove(dataDef);
        }

        List<Marker> preclusteredMarkersList = preclusteredMarkers.get(dataDef);
        if (preclusteredMarkersList !=null){
            Log.v("MainActivity", "clearMapMarkers - preclusteredMarkers exist: "+preclusteredMarkersList.size());
            for (Marker marker : preclusteredMarkersList) {
                marker.remove();
            }
            preclusteredMarkers.remove(dataDef);
        }
    }

    static class ClusterIconGenerator {
        private final IconGenerator mIconGenerator;
        private final ShapeDrawable mColoredCircleBackground;
        private final float mDensity;

        private SparseArray<BitmapDescriptor> mIcons = new SparseArray();

        public ClusterIconGenerator(Context context) {
            mIconGenerator = new IconGenerator(context);
            mColoredCircleBackground = new ShapeDrawable(new OvalShape());
            mDensity = context.getResources().getDisplayMetrics().density;

            SquareTextView squareTextView = new SquareTextView(context);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(-2, -2);
            squareTextView.setLayoutParams(layoutParams);
            squareTextView.setId(com.google.maps.android.R.id.amu_text);
            int twelveDpi = (int) (12.0F * this.mDensity);
            squareTextView.setPadding(twelveDpi, twelveDpi, twelveDpi, twelveDpi);
            this.mIconGenerator.setContentView(squareTextView);

            mIconGenerator.setTextAppearance(com.google.maps.android.R.style.amu_ClusterIcon_TextAppearance);

            ShapeDrawable outline = new ShapeDrawable(new OvalShape());
            outline.getPaint().setColor(Color.parseColor("#FFFFFF"));
            LayerDrawable background = new LayerDrawable(new Drawable[]{outline, this.mColoredCircleBackground});
            int strokeWidth = (int) (this.mDensity * 3.0F);
            background.setLayerInset(1, strokeWidth, strokeWidth, strokeWidth, strokeWidth);
            mIconGenerator.setBackground(background);
        }

        public BitmapDescriptor getDescriptor(int color, String text) {
            BitmapDescriptor descriptor = (BitmapDescriptor) this.mIcons.get(color);
            if (descriptor == null) {
                this.mColoredCircleBackground.getPaint().setColor(color);
                descriptor = BitmapDescriptorFactory.fromBitmap(this.mIconGenerator.makeIcon(text));
                this.mIcons.put(color, descriptor);
            }

            return descriptor;
        }
    }


    @Override
    public void addMapMarkers(DataDef datadef, List<Place> places, boolean preclustered) {
        if (map == null) {
            Log.e("addMapMarkers", "map is null!");
            return;
        }
        if (places == null || places.isEmpty()) {
            Log.d("addMapMarkers", "List empty.");
            return;
        }

        Log.d("addMapMarkers", "Adding " + places.size() + " markers");

        // if preclustered, add one marker per server-cluster
        // use https://stackoverflow.com/questions/36137333/display-the-number-of-clustered-markers-exactly maybe to create icon to drawzt
        if (preclustered) {
            ClusterIconGenerator iconGenerator = new ClusterIconGenerator(this);
            List<Marker> preclusteredMarkersOfDatadef =preclusteredMarkers.get(datadef);
            if (preclusteredMarkersOfDatadef == null) {
                preclusteredMarkersOfDatadef = new ArrayList<>();
                preclusteredMarkers.put(datadef, preclusteredMarkersOfDatadef);
            }

            for (Place place : places) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(place.getPosition());
                markerOptions.icon(iconGenerator.getDescriptor(Util.colorFromHue(datadef.getMarkerColor()), "666"));

                Marker marker = map.addMarker(markerOptions);
                preclusteredMarkersOfDatadef.add(marker);
            }
            return;
        }


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

        List<Place> placesList = placesStoredMap.get(datadef);
        if (placesList == null) {
            placesList = new ArrayList<>();
            placesStoredMap.put(datadef, placesList);
        }
        placesList.addAll(places);

        clusterManager.addItems(places);
        clusterManager.cluster();
    }

    @Override
    public void replaceMapMarkers(DataDef dataDef, List<Place> places, boolean preclustered) {
        clearMapMarkers(dataDef);
        addMapMarkers(dataDef, places, preclustered);
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
    public GoogleMap getMap() {
        return map;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.v("MainActivity", "onMapReady");
        this.map = googleMap;

        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);


        googleMap.setOnCameraMoveStartedListener(reason -> {
            Log.v("MainActivity", "onMapCameraMoved " + reason);
            presenter.onMapCameraMoved(map, reason);
        });

        // CLUSTERS
        clusterListener = new MultiClusterListener<>();
        googleMap.setOnCameraIdleListener(() ->
        {
            Log.v("MainActivity", "onCameraIdle");
            presenter.onMapCameraIdle(map);
        });

        googleMap.setOnInfoWindowClickListener(marker -> {
            Log.d("MainActivity", "onInfoWindowClick " + marker);
            clusterListener.onInfoWindowClick(marker);
        });

        presenter.onMapReady(map);
    }

    @Override
    public void reclusterMarkers() {
        clusterListener.onCameraIdle();
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
        presenter.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onRestoreMapPosition();
    }


    @Override
    protected void onPause() {
        super.onPause();
        presenter.onSaveMapPosition();
    }


    @Override
    protected void onStop() {
        super.onStop();
        presenter.onViewDetached();
    }

    public Map<DataDef, ClusterManager<Place>> getClusterMgrs() {
        return clusterMgrs;
    }

    @Override
    public void showManageDatasourcesActivity() {
        startActivity(new Intent(this, DatasourcesActivity.class));
    }

    public MainPresenter getPresenter() {
        return this.presenter;
    }
}
