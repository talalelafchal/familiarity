package com.gmail.fedorenko.kostia.app1lesson4;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by kfedoren on 30.09.2015.
 */
public class RegionPickerFragment extends DialogFragment {

    private int itemNumber;

    public interface RegionPickerListener {
        public void onDialogPositiveClick(String string);
    }

    RegionPickerListener pListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.pick_region)
                .setSingleChoiceItems(R.array.regions_array, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        itemNumber = which;
                    }
                }).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                pListener.onDialogPositiveClick(getResources().getStringArray(R.array.regions_array)[itemNumber]);
            }
        }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            pListener = (RegionPickerListener) activity;
        } catch (ClassCastException ex) {
            ex.printStackTrace();
        }
    }
}
