package cz.melkamar.andruian.viewlink.ui;

import android.util.Log;

import java.util.List;

import cz.melkamar.andruian.ddfparser.model.DataDef;
import cz.melkamar.andruian.viewlink.data.DataManager;
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
//        view.setKeepMapCentered(true); // TODO enable this

        DataManagerProvider.getDataManager().getDataDefs("https://raw.githubusercontent.com/andruian/datadef-parser/master/src/test/resources/rdf/test-parse-datadef.ttl", new DataManager.GetDataDefsCallback() {
                    @Override
                    public void onDataDefsFetched(List<DataDef> dataDefs) {
                        Log.i("onDataDefsFetched", "Fetched datadefs: "+dataDefs.size());
                        for (DataDef dataDef : dataDefs) {
                            Log.d("ondataDefsFetched", dataDef.getUri());
                        }
                    }

                    @Override
                    public void onFetchError(String error, int errorCode) {
                        // TODO handle error
                    }
                }
        );
    }

}
