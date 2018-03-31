package cz.melkamar.andruian.viewlink.ui.main;

import android.support.v7.widget.SwitchCompat;

import com.google.android.gms.maps.GoogleMap;

import java.util.List;

import cz.melkamar.andruian.viewlink.model.datadef.DataDef;
import cz.melkamar.andruian.viewlink.model.place.Place;
import cz.melkamar.andruian.viewlink.ui.base.BaseView;

/**
 * Created by Martin on 11.03.2018.
 */

public interface MainView extends BaseView {
    void showManageDatasourcesActivity();
    void setKeepMapCenteredIcons(boolean keepCentered);
    void showDataDefsInDrawer(List<DataDef> dataDefList);
    void clearMapMarkers(DataDef dataDef);
    void addMapMarkers(DataDef dataDef, List<Place> places);
    void replaceMapMarkers(DataDef dataDef, List<Place> places);
    void showProgressBar();
    void hideProgressBar();
    void showUpdatePlacesButton();
    void hideUpdatePlacesButton();
    void reclusterMarkers();
    void setSwitchButtonColor(SwitchCompat switchButton, DataDef dataDef, boolean enabled);
    GoogleMap getMap();
}
