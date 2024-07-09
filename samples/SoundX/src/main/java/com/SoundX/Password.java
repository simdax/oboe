package com.SoundX;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.concurrent.Callable;

public class Password extends DialogFragment {
    public static Callable<Void> OK;
    public EditText input;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        input = new EditText(getContext());
        builder.setTitle("Play")
                .setMessage("Enter password please:")
                .setView(input)
                .setPositiveButton("Ok", (dialog, whichButton) -> {
                    Editable value = input.getText();
                    if (value.toString().equals(MainActivity.Pass)) {
                        try {
                            Password.OK.call();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }).setNegativeButton("Cancel", (dialog, whichButton) -> {
                });
        return builder.create();
    }
}


