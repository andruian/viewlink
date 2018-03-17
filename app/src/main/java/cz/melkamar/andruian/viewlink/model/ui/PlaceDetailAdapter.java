package cz.melkamar.andruian.viewlink.model.ui;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.melkamar.andruian.viewlink.R;
import cz.melkamar.andruian.viewlink.model.place.Place;
import cz.melkamar.andruian.viewlink.ui.placedetail.PlaceDetailActivity;

public class PlaceDetailAdapter extends RecyclerView.Adapter<PlaceDetailAdapter.DataDefViewHolder> {
    private final Place place;
    private final PlaceDetailActivity activity;

    public PlaceDetailAdapter(Place place, PlaceDetailActivity activity) {
        this.place = place;
        this.activity = activity;
    }


    @Override
    public DataDefViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row_place_detail, parent, false);
        return new DataDefViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DataDefViewHolder holder, int position) {
        holder.bindDataDef();
    }

    @Override
    public int getItemCount() {
        return place.getProperties().size()
                + 1 // latlong
                + 1 // uri
                + 1 // locationObjectUri
                + 1 // classType
                + 1; // parentDatadefUri
    }

    public class DataDefViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.recycler_row_place_detail_label) TextView labelTV;
        @BindView(R.id.recycler_row_place_detail_value) TextView valueTV;
        @BindView(R.id.recycler_row_place_detail_button) Button button;
        @BindView(R.id.recycler_row_constr_layout) ConstraintLayout constraintLayout;

        public DataDefViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindDataDef() {
            switch (getAdapterPosition()) {
                case 0:
                    labelTV.setText("Object IRI");
                    valueTV.setText(place.getUri());

                    showButton(true);
                    button.setText("Open in browser");
                    button.setOnClickListener(view -> {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(place.getUri()));
                        activity.startActivity(intent);
                    });
                    break;

                case 1:
                    labelTV.setText("Object location");
                    valueTV.setText(place.getLatitude() + ", " + place.getLongitude());

                    showButton(true);
                    button.setText("Open in maps");
                    button.setOnClickListener(view -> {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(
                                String.format(Locale.ENGLISH, "geo:%f,%f?q=%f,%f(%s)",
                                        place.getLatitude(), place.getLongitude(),
                                        place.getLatitude(), place.getLongitude(),
                                        place.getDisplayName()
                                        )
                        ));
                        activity.startActivity(intent);
                    });
                    break;

                case 2:
                    labelTV.setText("Location object IRI");
                    valueTV.setText(place.getLocationObjectUri());

                    showButton(true);
                    button.setText("Open in browser");
                    button.setOnClickListener(view -> {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(place.getLocationObjectUri()));
                        activity.startActivity(intent);
                    });
                    break;
                case 3:
                    labelTV.setText("Object type");
                    valueTV.setText(place.getClassType());
                    showButton(false);

                    break;
                case 4:
                    labelTV.setText("Containing data definition");
                    valueTV.setText(place.getParentDatadef().getUri());

                    showButton(true);
                    button.setText("Open in browser");
                    button.setOnClickListener(view -> {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(place.getParentDatadef().getUri()));
                        activity.startActivity(intent);
                    });
                    break;
                default:
                    String value = place.getProperties().get(getAdapterPosition() - 5).getValue();
                    labelTV.setText(place.getProperties().get(getAdapterPosition() - 5).getName());
                    valueTV.setText(value);
                    if (URLUtil.isValidUrl(value)) {
                        showButton(true);
                        button.setText("Open in browser");
                        button.setOnClickListener(view -> {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(value));
                            activity.startActivity(intent);
                        });
                    } else {
                        showButton(false);
                    }
            }
        }

        private void showButton(boolean show){
            if (show){
                button.setVisibility(View.VISIBLE);
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(constraintLayout);
                constraintSet.clear(R.id.recycler_row_place_detail_value, ConstraintSet.BOTTOM);
                constraintSet.applyTo(constraintLayout);
            } else {
                Resources r = activity.getResources();
                int px = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, r.getDisplayMetrics()));

                button.setVisibility(View.GONE);
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(constraintLayout);
                constraintSet.connect(R.id.recycler_row_place_detail_value, ConstraintSet.BOTTOM, R.id.recycler_row_constr_layout, ConstraintSet.BOTTOM, px);
                constraintSet.applyTo(constraintLayout);
            }
        }
    }
}
