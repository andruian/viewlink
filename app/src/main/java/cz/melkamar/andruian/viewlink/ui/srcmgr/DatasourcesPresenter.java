package cz.melkamar.andruian.viewlink.ui.srcmgr;

import java.util.List;

import cz.melkamar.andruian.ddfparser.model.DataDef;

/**
 * Created by Martin Melka on 11.03.2018.
 */

public interface DatasourcesPresenter
//        extends NewDatasourceDialogFragment.NewDatasourceDialogListener
{
    void onNewDatadefsAdded(List<DataDef> dataDefs);
    void onAddDatasourceClicked();
}
