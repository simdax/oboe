package com.google.oboe.samples.liveEffect;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Objects;

public class CustomizeIA extends DialogFragment {
    private SeekBar Voice;
    private SeekBar Instruments;
    private SeekBar Bass;
    private SeekBar Sub;
    private SeekBar Clarity;
    private SeekBar Quiet;

    private SeekBar CreateSlider(SeekBar Slider, int min, int max)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Slider.setMin(min);
        }
        Slider.setMax(max);
        return Slider;
    }

    public void SetupView(@NonNull View view) {
        Voice = CreateSlider(view.findViewById(R.id.voiceSlider), -50, 12);
        Voice .setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                LiveEffectEngine.setGain(1, 1, progress);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        Instruments = CreateSlider(view.findViewById(R.id.InstrumentsSlider), -50, 12);
        Instruments.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                LiveEffectEngine.setGain(2, 1, progress);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        double[] bandBassAndSubGen = {-50.0, -10.0, -5.0, 0.0, 2.0};
        Bass= CreateSlider(view.findViewById(R.id.BasseSlider), 0, 4);
        Bass.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                LiveEffectEngine.setGain(4, 0, bandBassAndSubGen[progress]);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        Sub = CreateSlider(view.findViewById(R.id.SubBasseSlider), 0, 4);
        Sub.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                LiveEffectEngine.setGain(0, 0, bandBassAndSubGen[progress]);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = requireActivity().getLayoutInflater().inflate(R.layout.vibrations, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        SetupView(v);
        builder.setTitle("Vibration Preset")
                .setPositiveButton("OK", (dialog, which) -> {})
                .setView(v);
        return builder.create();
    }
}
