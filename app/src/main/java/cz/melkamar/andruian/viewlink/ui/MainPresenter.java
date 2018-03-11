package cz.melkamar.andruian.viewlink.ui;

import android.util.Log;

/**
 * Created by Martin Melka on 11.03.2018.
 */

public class MainPresenter implements MainMvpPresenter {
    private MainMvpView view;

    public MainPresenter(MainMvpView view) {
        this.view = view;
    }

    @Override
    public void manageDataSources() {
        Log.i("manageDataSources", "foo");
//        view.showMessage("add datasource: "+ DataManagerProvider.getDataManager().getHttpFile("someUrl"));
        view.showManageDatasources();
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestroy() {
        view = null;
    }
}
