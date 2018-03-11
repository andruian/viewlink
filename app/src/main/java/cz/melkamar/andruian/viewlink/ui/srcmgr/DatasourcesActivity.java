package cz.melkamar.andruian.viewlink.ui.srcmgr;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;
import cz.melkamar.andruian.viewlink.R;

public class DatasourcesActivity extends AppCompatActivity implements DatasourcesView {

    DatasourcesPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datasources);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        presenter = new DatasourcesPresenterImpl(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onAddDatasourceClicked();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void showNewResourceDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        AlertDialog dialog = builder.create();
//        dialog.show();
        NewDatasourceDialogFragment dialog = new NewDatasourceDialogFragment();
        dialog.setListener(presenter);
        dialog.show(getSupportFragmentManager(), "xxx");
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
