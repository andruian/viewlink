package cz.melkamar.andruian.viewlink.ui.addsrc;

import cz.melkamar.andruian.viewlink.model.DataSource;
import cz.melkamar.andruian.viewlink.ui.base.BaseView;

/**
 * Created by Martin Melka on 12.03.2018.
 */

public interface AddEditSourceView extends BaseView {
    String getSrcName();
    String getSrcUri();
    void returnActivityResult(DataSource dataSource);
    void returnActivityCancelled();

    void showLoadingDialog(String title, String message);
    void hideLoadingDialog();
}
