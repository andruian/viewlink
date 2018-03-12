package cz.melkamar.andruian.viewlink.ui.base;

import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;


public abstract class BaseActivity extends AppCompatActivity implements BaseView {

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
