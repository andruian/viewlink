package cz.melkamar.andruian.viewlink.ui.srcmgr;

import cz.melkamar.andruian.viewlink.model.DataDef;

/**
 * Created by Martin Melka on 11.03.2018.
 */

public interface DatasourcesPresenter
//        extends NewDatasourceDialogFragment.NewDatasourceDialogListener
{
    void refreshDatadefsShown();
    void onAddDatasourceClicked();
    void onDeleteDataDefClicked(int position, DataDef dataDef);
}
