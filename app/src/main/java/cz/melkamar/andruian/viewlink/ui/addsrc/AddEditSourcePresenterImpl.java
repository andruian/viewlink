package cz.melkamar.andruian.viewlink.ui.addsrc;

import android.util.Log;

import java.util.List;

import cz.melkamar.andruian.ddfparser.model.DataDef;
import cz.melkamar.andruian.viewlink.data.DataManager;
import cz.melkamar.andruian.viewlink.data.DataManagerProvider;

/**
 * Created by Martin Melka on 12.03.2018.
 */

public class AddEditSourcePresenterImpl implements AddEditSourcePresenter {
    AddEditSourceView view;

    public AddEditSourcePresenterImpl(AddEditSourceView view) {
        this.view = view;
    }

    @Override
    public void onConfirmButtonClicked() {
        String uri = view.getSrcUri();

        view.showLoadingDialog("Fetching data source", "Contacting URI: "+uri);
        DataManagerProvider.getDataManager().getDataDefs(uri, new DataManager.GetDataDefsCallback() {
            @Override
            public void onDataDefsFetched(List<DataDef> dataDefs) {
                // TODO
            }

            @Override
            public void onFetchError(String error, int errorCode) {
                // TODO
            }
        });
    }

    @Override
    public void onNewDataDefsFetched(List<DataDef> dataDefs) {
        view.hideLoadingDialog();
        if (dataDefs!=null) {
//            view.showMessage("Add src: " + dataSource.getName() + " " + dataSource.getUrl() + " " + dataSource.getContent());
            view.showMessage("Fetched "+dataDefs.size()+ " datadefs");
            // TODO use name field here
            view.returnActivityResult(dataDefs);
        } else {
            Log.i("onNewDatasourceFetched", "datasource is null");
            view.showMessage("Could not fetch the datasource. Check the URL.");
        }
    }


}
