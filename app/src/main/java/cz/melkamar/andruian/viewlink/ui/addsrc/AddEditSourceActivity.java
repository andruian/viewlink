package cz.melkamar.andruian.viewlink.ui.addsrc;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.Serializable;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.melkamar.andruian.ddfparser.model.DataDef;
import cz.melkamar.andruian.viewlink.R;
import cz.melkamar.andruian.viewlink.ui.base.BaseActivity;

public class AddEditSourceActivity extends BaseActivity implements AddEditSourceView {

    public static final String TAG_RESULT_DATASOURCE = "datasource";
    private ProgressDialog progressDialog;

    private AddEditSourcePresenter presenter;

    @BindView(R.id.new_datasource_url) EditText srcUri;
    @BindView(R.id.errorNameTextView) TextView errorTitleTV;
    @BindView(R.id.errorDescriptionTextView) TextView errorMessageTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_source);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        presenter = new AddEditSourcePresenterImpl(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onConfirmButtonClicked();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public String getSrcUri() {
        return srcUri.getText().toString();
    }

    @Override
    public void showError(String title, String message) {
        errorTitleTV.setText(title);
        errorMessageTV.setText(message);
        errorTitleTV.setVisibility(View.VISIBLE);
        errorMessageTV.setVisibility(View.VISIBLE);
    }

    @Override
    public void returnActivityResult(List<DataDef> dataDefs) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(TAG_RESULT_DATASOURCE, (Serializable) dataDefs);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void returnActivityCancelled() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    @Override
    public void showLoadingDialog(String title, String message) {
        hideLoadingDialog();

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    @Override
    public void hideLoadingDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
}
