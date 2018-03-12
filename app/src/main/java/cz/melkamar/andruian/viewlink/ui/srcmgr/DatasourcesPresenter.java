package cz.melkamar.andruian.viewlink.ui.srcmgr;

import cz.melkamar.andruian.viewlink.model.DataSource;

/**
 * Created by Martin Melka on 11.03.2018.
 */

public interface DatasourcesPresenter
//        extends NewDatasourceDialogFragment.NewDatasourceDialogListener
{
    void onNewDatasrcAdded(DataSource dataSource);
    void onAddDatasourceClicked();
}
