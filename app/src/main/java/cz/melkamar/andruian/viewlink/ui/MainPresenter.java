package cz.melkamar.andruian.viewlink.ui;

import android.util.Log;
import cz.melkamar.andruian.viewlink.data.DataManagerProvider;

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

    @Override
    public void onFabClicked() {
//        view.setKeepMapCentered(true);
        DataManagerProvider.getDataManager().getDataSource("https://raw.githubusercontent.com/andruian/example-data/master/ruian/ruian-datadef.ttl", dataSource ->
            Log.i(dataSource.getName(), dataSource.getContent())
        );
    }
}
