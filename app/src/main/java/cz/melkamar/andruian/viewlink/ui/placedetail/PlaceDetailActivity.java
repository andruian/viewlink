package cz.melkamar.andruian.viewlink.ui.placedetail;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.io.Serializable;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.melkamar.andruian.viewlink.R;
import cz.melkamar.andruian.viewlink.model.place.Place;
import cz.melkamar.andruian.viewlink.model.ui.PlaceDetailAdapter;
import cz.melkamar.andruian.viewlink.ui.base.BaseActivity;

public class PlaceDetailActivity extends BaseActivity {
    public static final String TAG_DATA_PLACE = "place";
    @BindView(R.id.place_detail_rv) RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(manager);

        Serializable placeSeri = getIntent().getSerializableExtra(TAG_DATA_PLACE);
        if (placeSeri == null) {
            Log.w("PlaceDetailActivity", "No place provided in Intent");
        } else {
            Place place = (Place) placeSeri;
            setTitle(place.getDisplayName());
            rv.setAdapter(new PlaceDetailAdapter((Place) placeSeri, this));
            fab.setOnClickListener(view -> {
                String url = String.format(Locale.ENGLISH, "https://www.google.com/maps/dir/?api=1&destination=%f,%f", place.getLatitude(), place.getLongitude());
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

                startActivity(intent);
            });
        }
    }
}
