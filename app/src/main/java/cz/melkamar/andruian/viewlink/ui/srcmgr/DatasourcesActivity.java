package cz.melkamar.andruian.viewlink.ui.srcmgr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.util.List;

import cz.melkamar.andruian.ddfparser.model.DataDef;
import cz.melkamar.andruian.viewlink.R;
import cz.melkamar.andruian.viewlink.ui.addsrc.AddEditSourceActivity;
import cz.melkamar.andruian.viewlink.ui.base.BaseActivity;

public class DatasourcesActivity extends BaseActivity implements DatasourcesView {

    DatasourcesPresenter presenter;

    private final int RESULT_ACTIVITY_NEW_DATASOURCE = 1;

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
    public void showAddNewResourceActivity() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        AlertDialog dialog = builder.create();
//        dialog.show();
//        NewDatasourceDialogFragment dialog = new NewDatasourceDialogFragment();
//        dialog.setListener(presenter);
//        dialog.show(getSupportFragmentManager(), "xxx");
        Intent intent = new Intent(this, AddEditSourceActivity.class);
        startActivityForResult(intent, RESULT_ACTIVITY_NEW_DATASOURCE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RESULT_ACTIVITY_NEW_DATASOURCE:
                if (resultCode == Activity.RESULT_OK) {
                    Log.d("onActivityResult", "Result ok");
                    List<DataDef> src = (List<DataDef>) data.getSerializableExtra(AddEditSourceActivity.TAG_RESULT_DATASOURCE);
                    presenter.onNewDatadefsAdded(src);
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Log.d("onActivityResult", "Result cancelled");
                }
                break;

            default:
                Log.e("onActivityResult", "unknown requestCode: " + requestCode);
        }
    }
}
