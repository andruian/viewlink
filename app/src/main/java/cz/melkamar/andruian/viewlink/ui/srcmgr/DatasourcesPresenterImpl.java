package cz.melkamar.andruian.viewlink.ui.srcmgr;


import android.util.Log;


/**
 * Created by Martin Melka on 11.03.2018.
 */

public class DatasourcesPresenterImpl implements DatasourcesPresenter {
    private DatasourcesView view;

    public DatasourcesPresenterImpl(DatasourcesView view) {
        this.view = view;
    }

    @Override
    public void onNewDatadefsAdded() {
        // TODO this should get all datadefs from database
        Log.i("onNewDatadefsAdded", "Loading new datadefs from the database.");
        view.showMessage("Loading new datadefs from the database.");
    }

    @Override
    public void onAddDatasourceClicked() {
        view.showAddNewResourceActivity();
    }

}
