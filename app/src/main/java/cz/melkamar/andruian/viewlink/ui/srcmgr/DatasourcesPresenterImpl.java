package cz.melkamar.andruian.viewlink.ui.srcmgr;


import java.util.List;

import cz.melkamar.andruian.ddfparser.model.DataDef;

/**
 * Created by Martin Melka on 11.03.2018.
 */

public class DatasourcesPresenterImpl implements DatasourcesPresenter {
    private DatasourcesView view;

    public DatasourcesPresenterImpl(DatasourcesView view) {
        this.view = view;
    }

    @Override
    public void onNewDatadefsAdded(List<DataDef> dataDefs) {
        // TODO
    }

    @Override
    public void onAddDatasourceClicked() {
        view.showAddNewResourceActivity();
    }

//    @Override
//    public void onAddClick(String datasourceName, String datasourceUri) {
//        view.showMessage(datasourceName+" - "+datasourceUri);
//    }
}
