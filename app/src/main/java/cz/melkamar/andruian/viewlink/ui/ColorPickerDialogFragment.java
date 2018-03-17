package cz.melkamar.andruian.viewlink.ui;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.melkamar.andruian.viewlink.R;

/**
 * A dialog fragment for picking a hue of a color by the user.
 * Using a SeekBar the user chooses a color and the dialog listener will be
 * notified with the hue (0-360) the user has chosen.
 */
public class ColorPickerDialogFragment extends DialogFragment {
    @BindView(R.id.dialog_color_imgview) ImageView imageView;
    @BindView(R.id.dialog_color_seekbar) SeekBar seekBar;
    private float hue;
    private ColorPickerListener listener;

    /**
     * Set an initial hue.
     */
    public void setHue(float hue) {
        this.hue = hue;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_color_picker, null);
        ButterKnife.bind(this, view);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int color = Color.HSVToColor(new float[]{i, 1, 1});
                ColorDrawable cd = new ColorDrawable(color);
                imageView.setImageDrawable(cd);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBar.setProgress(Math.round(hue));
        int color = Color.HSVToColor(new float[]{hue, 1, 1});
        ColorDrawable cd = new ColorDrawable(color);
        imageView.setImageDrawable(cd);

        builder.setView(view)
                .setPositiveButton("OK", (dialog, id) -> {
                    listener.onColorPicked(seekBar.getProgress());
                })
                .setNegativeButton("CANCEL", (dialog, id) -> ColorPickerDialogFragment.this.getDialog().cancel());


        return builder.create();
    }

    public interface ColorPickerListener {
        /**
         * Called when the user chooses a color. Gives a hue from HSV color schema.
         */
        void onColorPicked(float hue);
    }

    public void setListener(ColorPickerListener listener) {
        this.listener = listener;
    }
}
