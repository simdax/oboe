package com.google.oboe.samples.liveEffect;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class CustomizeIA extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder
                .setTitle("Vibration Preset")
                .setPositiveButton("OK", (dialog, which) -> {
                })
       //         .setView(R.id.VibrationLayout)
        ;

        return builder.create();
    }
}
