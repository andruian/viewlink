package cz.melkamar.andruian.viewlink.model.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.melkamar.andruian.viewlink.R;
import cz.melkamar.andruian.viewlink.model.datadef.DataDef;
import cz.melkamar.andruian.viewlink.ui.srcmgr.DatasourcesPresenter;

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

        public DataDefViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindDataDef() {
            DataDef dataDef = dataDefs.get(getAdapterPosition());
            labelTV.setText(dataDef.getUri());
            uriTV.setText(dataDef.getUri());
            mappingTV.setText(dataDef.getSourceClassDef().getClassUri() + " -> " + dataDef.getLocationClassDef().getClassUri());
            button.setOnClickListener(view -> presenter.onDeleteDataDefClicked(getAdapterPosition(),
                    dataDefs.get(getAdapterPosition())));

        }
    }

    public void deleteItem(int position) {
//        presenter.refreshDatadefsShown();
        dataDefs.remove(position);
        notifyItemRemoved(position);
    }
}
