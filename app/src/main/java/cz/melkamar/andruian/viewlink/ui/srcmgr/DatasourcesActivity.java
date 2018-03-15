package cz.melkamar.andruian.viewlink.ui.srcmgr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.melkamar.andruian.viewlink.R;
import cz.melkamar.andruian.viewlink.model.datadef.DataDef;
import cz.melkamar.andruian.viewlink.model.ui.DataDefAdapter;
import cz.melkamar.andruian.viewlink.ui.addsrc.AddEditSourceActivity;
import cz.melkamar.andruian.viewlink.ui.base.BaseActivity;

public class DatasourcesActivity extends BaseActivity implements DatasourcesView {

    DatasourcesPresenter presenter;

    @BindView(R.id.datadefs_rv) RecyclerView rv;
    @BindView(R.id.no_datasources_tv) TextView noDatadefsTV;

    private final int RESULT_ACTIVITY_NEW_DATASOURCE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datasources);
        ButterKnife.bind(this);

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

        /*
         * Setting up RecyclerView, assigning an adapter to it.
         */
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(manager);

//        DividerItemDecoration divider = new DividerItemDecoration(rv.getContext(), manager.getOrientation());
//        rv.addItemDecoration(divider);

        presenter.refreshDatadefsShown();
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
    public void showDataDefs(List<DataDef> dataDefList) {
        DataDefAdapter adapter = new DataDefAdapter(dataDefList, presenter);
        rv.setAdapter(adapter);

        if (dataDefList.size() == 0){
            rv.setVisibility(View.GONE);
            noDatadefsTV.setVisibility(View.VISIBLE);
        } else {
            rv.setVisibility(View.VISIBLE);
            noDatadefsTV.setVisibility(View.GONE);
        }
    }

    @Override
    public void deleteFromRecycler(int position) {
        RecyclerView.Adapter adapter = rv.getAdapter();
        if (adapter != null ){
            DataDefAdapter dataDefAdapter = (DataDefAdapter) adapter;
            dataDefAdapter.deleteItem(position);
        } else {
            Log.w("deleteFromRecycler", "Adapter is null");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RESULT_ACTIVITY_NEW_DATASOURCE:
                if (resultCode == Activity.RESULT_OK) {
                    Log.d("onActivityResult", "Result ok");
                    presenter.refreshDatadefsShown();
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Log.d("onActivityResult", "Result cancelled");
                }
                break;

            default:
                Log.e("onActivityResult", "unknown requestCode: " + requestCode);
        }
    }
}
