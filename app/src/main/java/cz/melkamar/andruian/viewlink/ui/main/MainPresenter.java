package cz.melkamar.andruian.viewlink.ui.main;

import android.support.annotation.NonNull;
import android.support.v7.widget.SwitchCompat;

import com.google.android.gms.maps.GoogleMap;

import cz.melkamar.andruian.viewlink.data.place.PlaceFetcher;
import cz.melkamar.andruian.viewlink.model.datadef.DataDef;
import cz.melkamar.andruian.viewlink.ui.base.BasePresenter;

/**
 * Created by Martin on 11.03.2018.
 */

public interface MainPresenter extends BasePresenter {
    /**
     * Show the manage sources screen.
     */
    void manageDataSources();

    /**
     * Called when a View attaches (e.g. on Activity's onResume)
     *
     * @param view
     */
    void onViewAttached(MainView view);

    /**
     * Called when a View is detached (e.g. on Activity's onPause)
     */
    void onViewDetached();

    /**
     * Called when the FAB is tapped.
     */
    void onFabClicked();

    /**
     * Refresh the data definitions shown in the navigation drawer.
     * Reads the definitions from a local storage and populates the menu.
     */
    void refreshDatadefsShownInDrawer();

    /**
     * Called when places defined by the given data definition are downloaded.
     *
     * @param dataDef
     * @param result
     * @param task
     */
    void onPlacesFetched(DataDef dataDef, PlaceFetcher.FetchPlacesResult result, MainPresenterImpl.FetchPlacesAT task);

    /**
     * Called when a DataDef switch in the navigation drawer is clicked.
     */
    void dataDefSwitchClicked(SwitchCompat switchButton, int itemId, boolean enabled);

    /**
     * Called when the user accepts or denies a permission request.
     */
    void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);

    /**
     * Called when the Google Map Fragment component is ready to be interacted with.
     *
     * @param googleMap The Map that became ready.
     */
    void onMapReady(GoogleMap googleMap);

    /**
     * Called whenever the map camera is moved.
     *
     * @param googleMap The map moved.
     * @param reason    The reason why the map moved - user gesture? programmatic movement? The
     *                  Constants are defined as e.g. GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE
     */
    void onMapCameraMoved(GoogleMap googleMap, int reason);

    /**
     * Called whenever the map stops moving (and was moving before). This is called
     * after the camera started moving for any reason.
     * @param googleMap
     */
    void onMapCameraIdle(GoogleMap googleMap);

    /**
     * Called when the update places button is clicked.
     */
    void onUpdatePlacesButtonClicked();


    /**
     * Called when the map position should be saved to permanent storage.
     *
     * This is invoked when the view is exiting, e.g. Activity#onPause().
     */
    void onSaveMapPosition();

    /**
     * Called when the map position should be restored from permanent storage.
     *
     * This is invoked when the view is starting, e.g. Activity#onResume().
     */
    void onRestoreMapPosition();
}
