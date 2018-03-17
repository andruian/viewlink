package cz.melkamar.andruian.viewlink.ui.main;

import android.location.Location;

import com.google.android.gms.maps.GoogleMap;

import cz.melkamar.andruian.viewlink.ui.base.BasePresenter;

/**
 * Created by Martin on 11.03.2018.
 */

public interface MainMvpPresenter extends BasePresenter {
    void manageDataSources();
    void onResume();
    void onDestroy();
    void onFabClicked();
    void refreshDatadefsShownInDrawer();

    /**
     * Called when a DataDef switch in the navigation drawer is clicked.
     */
    void dataDefSwitchClicked(int itemId, boolean enabled);
    void onMapCameraMoved(GoogleMap googleMap, int reason);
    void onLocationChanged(Location newLocation);
    void onUpdatePlacesButtonClicked();
}