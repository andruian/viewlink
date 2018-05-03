package cz.melkamar.andruian.viewlink.ui.addsrc;

import cz.melkamar.andruian.viewlink.ui.base.BaseView;

/**
 * Created by Martin Melka on 12.03.2018.
 */

public interface AddEditSourceView extends BaseView {
    /**
     * Read the contents of the URL textview.
     *
     * @return The string contents of the view.
     */
    String getSrcUri();

    /**
     * Show an error message to the user.
     *
     * @param title   The title of the message.
     * @param message The body of the message.
     */
    void showError(String title, String message);

    /**
     * Return the result of this activity to the calling activity. Ends this activity.
     *
     * @param resultCode The result code.
     */
    void returnActivityResult(int resultCode);
}
