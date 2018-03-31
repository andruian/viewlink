package cz.melkamar.andruian.viewlink.ui.main;

import android.support.annotation.NonNull;
import android.support.v7.widget.SwitchCompat;

import com.google.android.gms.maps.GoogleMap;

import cz.melkamar.andruian.viewlink.model.datadef.DataDef;
import cz.melkamar.andruian.viewlink.ui.base.BasePresenter;

/**
 * Created by Martin on 11.03.2018.
 */

public interface MainPresenter extends BasePresenter {
    void manageDataSources();

    void onViewAttached(MainView view);

    void onViewDetached();

    void onFabClicked();

    void refreshDatadefsShownInDrawer();

    void onPlacesFetched(DataDef dataDef);

    /**
     * Called when a DataDef switch in the navigation drawer is clicked.
     */
    void dataDefSwitchClicked(SwitchCompat switchButton, int itemId, boolean enabled);

    void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);

    void onMapReady(GoogleMap googleMap);

    void onMapCameraMoved(GoogleMap googleMap, int reason);

    void onMapCameraIdle(GoogleMap googleMap);

    void onUpdatePlacesButtonClicked();

    void setSwitchButtonColor(SwitchCompat switchButton, DataDef dataDef, boolean enabled);

    void onSaveMapPosition();
    void onRestoreMapPosition();
}
