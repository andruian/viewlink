package cz.melkamar.andruian.viewlink.ui.addsrc;

import cz.melkamar.andruian.viewlink.model.DataSource;

/**
 * Created by Martin Melka on 12.03.2018.
 */

public interface AddEditSourcePresenter {
    void onConfirmButtonClicked();
    void onNewDatasourceFetched(DataSource dataSource);
}
