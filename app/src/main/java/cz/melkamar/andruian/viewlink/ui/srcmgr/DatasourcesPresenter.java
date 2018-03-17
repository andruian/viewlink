package cz.melkamar.andruian.viewlink.ui.srcmgr;

import cz.melkamar.andruian.viewlink.model.datadef.DataDef;
import cz.melkamar.andruian.viewlink.model.ui.DataDefAdapter;
import cz.melkamar.andruian.viewlink.ui.base.BasePresenter;

/**
 * Created by Martin Melka on 11.03.2018.
 */

public interface DatasourcesPresenter extends BasePresenter
//        extends NewDatasourceDialogFragment.NewDatasourceDialogListener
{
    void refreshDatadefsShown();
    void onAddDatasourceClicked();
    void onDeleteDataDefClicked(int position, DataDef dataDef);
    void onDatasourceColorChanged(DataDef dataDef, DataDefAdapter.DataDefViewHolder viewHolder);
}
