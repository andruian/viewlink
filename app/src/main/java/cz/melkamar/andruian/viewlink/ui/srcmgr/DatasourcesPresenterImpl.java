package cz.melkamar.andruian.viewlink.ui.srcmgr;

/**
 * Created by Martin Melka on 11.03.2018.
 */

public class DatasourcesPresenterImpl implements DatasourcesPresenter {
    private DatasourcesView view;

    public DatasourcesPresenterImpl(DatasourcesView view) {
        this.view = view;
    }

    @Override
    public void onAddDatasourceClicked() {
        view.showNewResourceDialog();
    }

    @Override
    public void onAddClick(String datasourceName, String datasourceUri) {
        view.showMessage(datasourceName+" - "+datasourceUri);
    }
}
