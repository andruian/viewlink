package cz.melkamar.andruian.viewlink.ui.srcmgr;

import cz.melkamar.andruian.viewlink.model.datadef.DataDef;
import cz.melkamar.andruian.viewlink.model.ui.DataDefAdapter;
import cz.melkamar.andruian.viewlink.ui.base.BasePresenter;

/**
 * Created by Martin Melka on 11.03.2018.
 */

public interface DatasourcesPresenter extends BasePresenter {
    /**
     * Refresh the data definitions currently shown in the list from the database.
     */
    void refreshDatadefsShown();

    /**
     * Called when the add datasource button is tapped.
     */
    void onAddDatasourceClicked();

    /**
     * Called when a delete button of a data definition is tapped.
     *
     * @param position The position of the data definition in the recyclerview adapter.
     * @param dataDef  The data definition object that is being removed.
     */
    void onDeleteDataDefClicked(int position, DataDef dataDef);

    /**
     * Called when the color of a data definition should be changed. The method shall make necessary changes in
     * the underlying database layer.
     *
     * @param dataDef    The data definition whose color to change.
     * @param viewHolder The viewholder object associated with the data definition. It is passed in so that the method
     *                   may change the color of the color view. This would probably be better to have as a method of
     *                   the view, but oh well.
     */
    void onDatasourceColorChanged(DataDef dataDef, DataDefAdapter.DataDefViewHolder viewHolder);
}
