package cz.melkamar.andruian.viewlink.ui;

import com.google.android.gms.maps.GoogleMap;

/**
 * Created by Martin on 11.03.2018.
 */

public interface MainMvpPresenter {
    void manageDataSources();
    void onResume();
    void onDestroy();
    void onFabClicked();
    void refreshDatadefsShown();

    /**
     * Called when a DataDef switch in the navigation drawer is clicked.
     */
    void dataDefSwitchClicked(int itemId, boolean enabled);
    void onMapCameraMoved(GoogleMap googleMap, int reason);
}
