package cz.melkamar.andruian.viewlink.model.ui;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.melkamar.andruian.viewlink.R;
import cz.melkamar.andruian.viewlink.model.datadef.DataDef;
import cz.melkamar.andruian.viewlink.ui.ColorPickerDialogFragment;
import cz.melkamar.andruian.viewlink.ui.srcmgr.DatasourcesPresenter;
import cz.melkamar.andruian.viewlink.ui.srcmgr.DatasourcesPresenterImpl;
import cz.melkamar.andruian.viewlink.util.Util;

public class DataDefAdapter extends RecyclerView.Adapter<DataDefAdapter.DataDefViewHolder> {
    private List<DataDef> dataDefs;
    private final DatasourcesPresenter presenter;

    public DataDefAdapter(List<DataDef> dataDefs, DatasourcesPresenter presenter) {
        this.dataDefs = dataDefs;
        this.presenter = presenter;
    }

    @Override
    public DataDefViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row_datadef, parent, false);
        return new DataDefViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DataDefViewHolder holder, int position) {
        holder.bindDataDef();
    }

    @Override
    public int getItemCount() {
        return dataDefs.size();
    }

    public class DataDefViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.recycler_row_datadef_label) TextView labelTV;
        @BindView(R.id.recycler_row_datadef_uri) TextView uriTV;
        @BindView(R.id.recycler_row_datadef_mapping) TextView mappingTV;
        @BindView(R.id.recycle_delete_button) Button button;
        @BindView(R.id.recycler_row_datadef_color_img) ImageView colorIV;

        public DataDefViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindDataDef() {
            DataDef dataDef = dataDefs.get(getAdapterPosition());
            labelTV.setText(dataDef.getLabel(Locale.getDefault().getLanguage()));
            uriTV.setText(dataDef.getUri());
            setColorPickerColor(dataDef.getMarkerColor());
            mappingTV.setText(dataDef.getSourceClassDef().getClassUri() + " -> " + dataDef.getLocationClassDef().getClassUri());
            button.setOnClickListener(view -> presenter.onDeleteDataDefClicked(getAdapterPosition(),
                    dataDefs.get(getAdapterPosition())));

            colorIV.setOnClickListener(view -> {
                ColorPickerDialogFragment dialogFragment = new ColorPickerDialogFragment();
                dialogFragment.setHue(dataDef.getMarkerColor());
                dialogFragment.setListener(color -> {
                    Log.i("adapter", "picked hue " + color);
                    dataDef.setMarkerColor(color);
                    presenter.onDatasourceColorChanged(dataDef, this);
                });
                dialogFragment.show(((DatasourcesPresenterImpl) presenter).getBaseView().getActivity().getSupportFragmentManager(), "atag");
            });

        }

        public void setColorPickerColor(float hue) {
            Drawable background = colorIV.getBackground().mutate();
            int color = Util.colorFromHue(hue);
//            Drawable dr = ((DatasourcesPresenterImpl)presenter).getBaseView().

//            background.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
            if (background instanceof ShapeDrawable) {
                ((ShapeDrawable) background).getPaint().setColor(color);
            } else if (background instanceof GradientDrawable) {
                ((GradientDrawable) background).setColor(color);
            } else if (background instanceof ColorDrawable) {
                ((ColorDrawable) background).setColor(color);
            }
//
//            colorIV.setBackground(background);
        }
    }

    public void deleteItem(int position) {
//        presenter.refreshDatadefsShownInDrawer();
        dataDefs.remove(position);
        notifyItemRemoved(position);
    }
}
