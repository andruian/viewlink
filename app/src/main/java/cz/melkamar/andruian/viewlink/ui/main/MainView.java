package cz.melkamar.andruian.viewlink.ui.main;

import android.support.v7.widget.SwitchCompat;

import com.google.android.gms.maps.GoogleMap;

import java.util.List;

import cz.melkamar.andruian.viewlink.data.place.PlaceFetcher;
import cz.melkamar.andruian.viewlink.model.datadef.DataDef;
import cz.melkamar.andruian.viewlink.ui.base.BaseView;

/**
 * Created by Martin on 11.03.2018.
 */

public interface MainView extends BaseView {
    /**
     * Show the Manage datasources activity.
     */
    void showManageDatasourcesActivity();

    /**
     * Set the FAB icon to reflect whether or not the app follows user's location.
     *
     * @param keepCentered
     */
    void setKeepMapCenteredIcons(boolean keepCentered);

    /**
     * Fill the navigation drawer with data definitions.
     *
     * @param dataDefList The data definitions to show in the drawer.
     */
    void showDataDefsInDrawer(List<DataDef> dataDefList);

    /**
     * Delete all map markers of a given data definition.
     *
     * @param dataDef
     */
    void clearMapMarkers(DataDef dataDef);

    /**
     * Show places on the map and associate them with a data definition.
     * @param dataDef      The data definition to associate with the markers.
     * @param fetchResult       The places for which to show markers on the map.
     */
    void addMapMarkers(DataDef dataDef, PlaceFetcher.FetchPlacesResult fetchResult);

    /**
     * Replace existing map markers of a particular data definition with new ones.
     * @param dataDef      The data definition for which to replace the markers.
     * @param fetchResult       The places for which to show markers on the map.
     */
    void replaceMapMarkers(DataDef dataDef, PlaceFetcher.FetchPlacesResult fetchResult);

    /**
     * Show a loading progress bar for when the places are being fetched.
     */
    void showProgressBar();

    /**
     * Hide the fetching progress bar shown by {@link MainView#showProgressBar()}.
     */
    void hideProgressBar();

    /**
     * Show the update markers button. The button is shown when the map is moved
     * and the markers are not automatically refreshed. The user can manually trigger
     * the refresh this way.
     */
    void showUpdatePlacesButton();

    /**
     * Hide the update markers button. This will be typically done when the map markers have
     * been refreshed.
     */
    void hideUpdatePlacesButton();

    /**
     * Reevaluate the clustering of markers on the map. This is done when the map has been
     * moved by a user gesture and the {@link com.google.maps.android.clustering.ClusterManager}
     * needs to be notified of that.
     */
    void reclusterMarkers();

    /**
     * Set a color of a switch in the drawer navigation menu.
     *
     * @param switchButton The button whose color to change.
     * @param color        The new color of the button.
     * @param enabled      Is the button now in enabled or disabled state? Disabled buttons will all be
     *                     gray, regardless of their on-color.
     */
    void setSwitchButtonColor(SwitchCompat switchButton, float color, boolean enabled);

    GoogleMap getMap();
}
