package cz.melkamar.andruian.viewlink.ui.addsrc;

import java.util.List;

import cz.melkamar.andruian.ddfparser.model.DataDef;

/**
 * Created by Martin Melka on 12.03.2018.
 */

public interface AddEditSourcePresenter {
    void onConfirmButtonClicked();
    void onNewDataDefsFetched(List<DataDef> dataDefs);
}
