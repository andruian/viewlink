package cz.melkamar.andruian.viewlink.ui.base;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by Martin Melka on 11.03.2018.
 */

public interface BaseView {
    void showMessage(String message);
    AppCompatActivity getActivity();
}
