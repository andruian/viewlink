package cz.melkamar.andruian.viewlink.ui.srcmgr;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import cz.melkamar.andruian.viewlink.R;

/**
 * Created by Martin Melka on 11.03.2018.
 */

public class NewDatasourceDialogFragment extends DialogFragment {
    private NewDatasourceDialogListener listener;

    public void setListener(NewDatasourceDialogListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View dialogView = inflater.inflate(R.layout.dialog_new_datasource, null);
        builder.setView(dialogView)
                // Add action buttons
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        EditText nameEditText = dialogView.findViewById(R.id.new_datasource_name);
                        EditText urlEditText = dialogView.findViewById(R.id.new_datasource_url);
                        if (listener != null)
                            listener.onAddClick(nameEditText.getText().toString(), urlEditText.getText().toString());
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        NewDatasourceDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    public interface NewDatasourceDialogListener {
        void onAddClick(String datasourceName, String datasourceUri);
    }
}
