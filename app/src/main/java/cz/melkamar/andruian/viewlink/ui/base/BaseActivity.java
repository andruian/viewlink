package cz.melkamar.andruian.viewlink.ui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
}
