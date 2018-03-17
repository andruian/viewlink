package cz.melkamar.andruian.viewlink.ui.main;

import com.google.android.gms.maps.GoogleMap;

import java.util.List;

import cz.melkamar.andruian.viewlink.model.datadef.DataDef;
import cz.melkamar.andruian.viewlink.model.place.Place;
import cz.melkamar.andruian.viewlink.ui.base.BaseView;

/**
 * Created by Martin on 11.03.2018.
 */

public interface MainMvpView extends BaseView {
    void showManageDatasourcesActivity();
    void setKeepMapCentered(boolean keepCentered);
    void showDataDefsInDrawer(List<DataDef> dataDefList);
    void clearMapMarkers();
    void clearMapMarkers(DataDef dataDef);
    void addMapMarkers(List<Place> places);
    void replaceMapMarkers(DataDef dataDef, List<Place> places);
    void showProgressBar();
    void hideProgressBar();
    void showUpdatePlacesButton();
    void hideUpdatePlacesButton();
    boolean isCameraFollowing();
    GoogleMap getMap();
}