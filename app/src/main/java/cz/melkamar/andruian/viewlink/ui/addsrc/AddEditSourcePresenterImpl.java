package cz.melkamar.andruian.viewlink.ui.addsrc;

import android.util.Log;
import cz.melkamar.andruian.viewlink.data.DataManagerProvider;
import cz.melkamar.andruian.viewlink.model.DataSource;

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
//        new FetchDatasourceTask(this).execute(uri);
        DataManagerProvider.getDataManager().getDataSource(uri, dataSource -> onNewDatasourceFetched(dataSource));
    }

    @Override
    public void onNewDatasourceFetched(DataSource dataSource) {
        view.hideLoadingDialog();
        if (dataSource!=null) {
            view.showMessage("Add src: " + dataSource.getName() + " " + dataSource.getUrl() + " " + dataSource.getContent());
            // TODO use name field here
            view.returnActivityResult(dataSource);
        } else {
            Log.i("onNewDatasourceFetched", "datasource is null");
            view.showMessage("Could not fetch the datasource. Check the URL.");
        }
    }


}
