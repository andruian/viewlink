package cz.melkamar.andruian.viewlink.ui.base;

import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import cz.melkamar.andruian.viewlink.ViewLinkApplication;


public abstract class BaseActivity extends AppCompatActivity implements BaseView {

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public AppCompatActivity getActivity() {
        return this;
    }

    @Override
    public ViewLinkApplication getViewLinkApplication() {
        return (ViewLinkApplication) getApplication();
    }
}
