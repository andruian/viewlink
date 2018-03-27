package cz.melkamar.andruian.viewlink.ui.main;

import android.location.Location;
import android.support.v7.widget.SwitchCompat;

import com.google.android.gms.maps.GoogleMap;

import cz.melkamar.andruian.viewlink.ui.base.BasePresenter;

/**
 * Created by Martin on 11.03.2018.
 */

public interface MainMvpPresenter extends BasePresenter {
    void manageDataSources();
    void onViewAttached(MainMvpView view);
    void onViewDetached();

    void onFabClicked();
    void refreshDatadefsShownInDrawer();

    /**
     * Called when a DataDef switch in the navigation drawer is clicked.
     */
    void dataDefSwitchClicked(SwitchCompat switchButton, int itemId, boolean enabled);
    void onMapCameraMoved(GoogleMap googleMap, int reason);
    void onMapCameraIdle(GoogleMap googleMap);
    void onLocationChanged(Location newLocation);
    void onUpdatePlacesButtonClicked();
}
