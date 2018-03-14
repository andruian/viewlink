package cz.melkamar.andruian.viewlink.ui;

import android.util.Log;

import java.util.List;

import cz.melkamar.andruian.viewlink.data.persistence.DaoHelper;
import cz.melkamar.andruian.viewlink.model.DataDef;
import cz.melkamar.andruian.viewlink.model.Place;

/**
 * Created by Martin Melka on 11.03.2018.
 */

public class MainPresenter implements MainMvpPresenter {
    private MainMvpView view;
    private List<DataDef> dataDefsShown = null; // To keep track of what is shown, so we can enable/disable it

    public MainPresenter(MainMvpView view) {
        this.view = view;
    }

    @Override
    public void manageDataSources() {
        Log.i("manageDataSources", "foo");
//        view.showMessage("add datasource: "+ DataManagerProvider.getDataManager().getHttpFile("someUrl"));
        view.showManageDatasourcesActivity();
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestroy() {
        view = null;
    }

    @Override
    public void onFabClicked() {
        // TODO This does not do anything on hardware
        view.setKeepMapCentered(true);
    }

    @Override
    public void refreshDatadefsShown() {
        DaoHelper.readAllDatadefs(view.getViewLinkApplication().getAppDatabase(), result -> {
            if (result.hasError()) {
                Log.w("refreshDatadefsShown", "An error occurred", result.getError());
            } else {
                this.dataDefsShown = result.getResult();
                view.showDataDefsInDrawer(result.getResult());
            }
        });
    }

    @Override
    public void dataDefSwitchClicked(int itemId, boolean enabled) {
        Log.d("dataDefSwitchClicked", "Enabled: "+enabled+"  for uri "+dataDefsShown.get(itemId));
        // TODO get all places around location
        // There will be a helper class that will send queries to index servers/sparql endpoints for places
    }

    @Override
    public void showItemsOnMap(List<Place> places) {
        // TODO implement
    }
}
