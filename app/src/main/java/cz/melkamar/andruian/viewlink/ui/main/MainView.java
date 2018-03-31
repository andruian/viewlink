package cz.melkamar.andruian.viewlink.ui.main;

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
    GoogleMap getMap();
}
