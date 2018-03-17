package cz.melkamar.andruian.viewlink.ui.addsrc;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.melkamar.andruian.viewlink.R;
import cz.melkamar.andruian.viewlink.ui.base.BaseActivity;

public class AddEditSourceActivity extends BaseActivity implements AddEditSourceView {

    public static final String TAG_RESULT_DATASOURCE = "datasource";

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
    public void returnActivityResult(int resultCode) {
        Intent returnIntent = new Intent();
        setResult(resultCode, returnIntent);
        finish();
    }


}
