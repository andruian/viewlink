package cz.melkamar.andruian.viewlink.ui.srcmgr;

import cz.melkamar.andruian.viewlink.model.DataSource;

/**
 * Created by Martin Melka on 11.03.2018.
 */

public class DatasourcesPresenterImpl implements DatasourcesPresenter {
    private DatasourcesView view;

    public DatasourcesPresenterImpl(DatasourcesView view) {
        this.view = view;
    }

    @Override
    public void onNewDatasrcAdded(DataSource dataSource) {
        view.showMessage(dataSource.getName()+" - "+dataSource.getUrl());
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
