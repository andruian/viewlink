package cz.melkamar.andruian.viewlink.ui.base;

import android.support.v7.app.AppCompatActivity;

import cz.melkamar.andruian.viewlink.ViewLinkApplication;

/**
 * Created by Martin Melka on 11.03.2018.
 */

public interface BaseView {
    void showMessage(String message);
    AppCompatActivity getActivity();
    ViewLinkApplication getViewLinkApplication();

    void showLoadingDialog(String title, String message);
    void dismissLoadingDialog();
}
