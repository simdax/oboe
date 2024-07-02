package com.google.oboe.samples.liveEffect;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class PresetVibrations extends DialogFragment {
    boolean[] CheckItems;
    String[] Presets;

    public void setPresets(String[] P)
    {
        Presets = P;
        if (CheckItems.length == 0)
        {
            CheckItems = new boolean[Presets.length];
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder
                .setTitle("Vibration Preset")
                .setPositiveButton("OK", (dialog, which) -> {

                })
                .setMultiChoiceItems(Presets, CheckItems,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which,
                                                boolean isChecked) {
                            }
                });
        return builder.create();
    }
}
