package cz.melkamar.andruian.viewlink.ui.addsrc;

import cz.melkamar.andruian.viewlink.ui.base.BaseView;

/**
 * Created by Martin Melka on 12.03.2018.
 */

public interface AddEditSourceView extends BaseView {
    String getSrcUri();
    void showError(String title, String message);
    void returnActivityResult(int resultCode);

    void showLoadingDialog(String title, String message);
    void dismissLoadingDialog();
}
