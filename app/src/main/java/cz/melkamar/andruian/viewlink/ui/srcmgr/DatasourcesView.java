package cz.melkamar.andruian.viewlink.ui.srcmgr;

import java.util.List;

import cz.melkamar.andruian.viewlink.model.datadef.DataDef;
import cz.melkamar.andruian.viewlink.ui.base.BaseView;

/**
 * Created by Martin Melka on 11.03.2018.
 */

public interface DatasourcesView extends BaseView {
    /**
     * Open the Add datadef activity.
     */
    void showAddNewResourceActivity();

    /**
     * Show the given list of data definitions on the screen.
     *
     * @param dataDefList The list of data definitions to show.
     */
    void showDataDefs(List<DataDef> dataDefList);

    /**
     * Delete an item from the shown list of data definitions.
     *
     * @param position The position of the item to be deleted from the list.
     */
    void deleteFromRecycler(int position);
}
