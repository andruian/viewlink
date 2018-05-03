package cz.melkamar.andruian.viewlink.ui.base;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import cz.melkamar.andruian.viewlink.ViewLinkApplication;

/**
 * A base for all activities in ViewLink.
 *
 * It implements most lifecycle methods and just logs them for debugging. Also provides a shortcut to
 * get a reference to a {@link ViewLinkApplication} instance.
 */
public abstract class BaseActivity extends AppCompatActivity implements BaseView {

    protected ProgressDialog progressDialog;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("Lifecycle", getClass()+" - onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v("Lifecycle", getClass()+" - onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v("Lifecycle", getClass()+" - onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v("Lifecycle", getClass()+" - onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v("Lifecycle", getClass()+" - onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v("Lifecycle", getClass()+" - onDestroy");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.v("Lifecycle", getClass()+" - onSaveInstanceState");
    }

    /**
     * Show a modal loading dialog with the given text.
     */
    @Override
    public void showLoadingDialog(String title, String message) {
        dismissLoadingDialog();

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    /**
     * Hide a previously shown modal loading dialog. Do nothing if no dialog is currently shown.
     */
    @Override
    public void dismissLoadingDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
}
