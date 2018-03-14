package cz.melkamar.andruian.viewlink.ui.srcmgr;

import java.util.List;

import cz.melkamar.andruian.viewlink.model.DataDef;
import cz.melkamar.andruian.viewlink.ui.base.BaseView;

/**
 * Created by Martin Melka on 11.03.2018.
 */

public interface DatasourcesView extends BaseView {
    void showAddNewResourceActivity();
    void showDataDefs(List<DataDef> dataDefList);
    void deleteFromRecycler(int position);
}
