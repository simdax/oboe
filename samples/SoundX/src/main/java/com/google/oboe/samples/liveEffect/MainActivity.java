package com.google.oboe.samples.liveEffect;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.oboe.samples.audio_device.AudioDeviceListEntry;
import com.google.oboe.samples.audio_device.AudioDeviceSpinner;

public class MainActivity extends Activity
        implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = MainActivity.class.getName();
    private static final int AUDIO_EFFECT_REQUEST = 0;
    private static final int OBOE_API_AAUDIO = 0;
    private static final int OBOE_API_OPENSL_ES=1;

    static {
        System.loadLibrary("liveEffect");
    }
    private TextView Presets;
    private SeekBar Voice;
    private SeekBar Instruments;
    private SeekBar Bass;
    private SeekBar Sub;

    private SeekBar CreateSlider(int id)
    {
        SeekBar Slider = findViewById(id);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Slider.setMin(-96);
        }
        Slider.setMax(0);
        Slider.setEnabled(false);
        return Slider;
    }

    private Button ManualModeButton;
    void Set(CharSequence str, double[] gains)
    {
        Presets.setText(str);
        Voice.setProgress((int)gains[0]);
        Instruments.setProgress((int)gains[2]);
        Bass.setProgress((int)gains[4]);
        Sub.setProgress((int)gains[6]);
    }
    native void create(TextView view);

    private TextView statusText;
    private Button toggleEffectButton;
    private Button changeEffectButton;
    private Button presetsButton;
    private AudioDeviceSpinner recordingDeviceSpinner;
    private AudioDeviceSpinner playbackDeviceSpinner;
    private boolean isPlaying = false;

    private int apiSelection = OBOE_API_AAUDIO;
    private boolean mAAudioRecommended = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ManualModeButton = findViewById(R.id.manualMode);
        ManualModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean IaOn = LiveEffectEngine.Ai();

                Voice       .setEnabled(!IaOn);
                Instruments .setEnabled(!IaOn);
                Bass        .setEnabled(!IaOn);
                Sub         .setEnabled(!IaOn);
            }
        });
        Presets = findViewById(R.id.presets);
        Voice= CreateSlider(R.id.voiceSlider);
        Instruments = CreateSlider(R.id.InstrumentsSlider);
        Bass = CreateSlider(R.id.BasseSlider);
        Sub = CreateSlider(R.id.SubBasseSlider);

        statusText = findViewById(R.id.status_view_text);
        toggleEffectButton = findViewById(R.id.button_toggle_effect);
        toggleEffectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleEffect();
            }
        });
        toggleEffectButton.setText(getString(R.string.start_effect));
        recordingDeviceSpinner = findViewById(R.id.recording_devices_spinner);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            recordingDeviceSpinner.setDirectionType(AudioManager.GET_DEVICES_INPUTS);
            recordingDeviceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    LiveEffectEngine.setRecordingDeviceId(getRecordingDeviceId());
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    // Do nothing
                }
            });
        }

        playbackDeviceSpinner = findViewById(R.id.playback_devices_spinner);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            playbackDeviceSpinner.setDirectionType(AudioManager.GET_DEVICES_OUTPUTS);
            playbackDeviceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    LiveEffectEngine.setPlaybackDeviceId(getPlaybackDeviceId());
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    // Do nothing
                }
            });
        }

        ((RadioGroup)findViewById(R.id.apiSelectionGroup)).check(R.id.aaudioButton);
        findViewById(R.id.aaudioButton).setOnClickListener(new RadioButton.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (((RadioButton)v).isChecked()) {
                    apiSelection = OBOE_API_AAUDIO;
                    setSpinnersEnabled(true);
                }
            }
        });
        findViewById(R.id.slesButton).setOnClickListener(new RadioButton.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (((RadioButton)v).isChecked()) {
                    apiSelection = OBOE_API_OPENSL_ES;
                    setSpinnersEnabled(false);
                }
            }
        });

        LiveEffectEngine.setDefaultStreamValues(this);
    }

    private void EnableAudioApiUI(boolean enable) {
        if(apiSelection == OBOE_API_AAUDIO && !mAAudioRecommended)
        {
            apiSelection = OBOE_API_OPENSL_ES;
        }
        findViewById(R.id.slesButton).setEnabled(enable);
        if(!mAAudioRecommended) {
            findViewById(R.id.aaudioButton).setEnabled(false);
        } else {
            findViewById(R.id.aaudioButton).setEnabled(enable);
        }

        ((RadioGroup)findViewById(R.id.apiSelectionGroup))
          .check(apiSelection == OBOE_API_AAUDIO ? R.id.aaudioButton : R.id.slesButton);
        setSpinnersEnabled(enable);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    protected void onResume() {
        super.onResume();
        create(findViewById(R.id.presets));
        mAAudioRecommended = LiveEffectEngine.isAAudioRecommended();
        EnableAudioApiUI(true);
        LiveEffectEngine.setAPI(apiSelection);
    }
    @Override
    protected void onPause() {
        stopEffect();
        LiveEffectEngine.delete();
        super.onPause();
    }

    public void toggleEffect() {
        if (isPlaying) {
            stopEffect();
        } else {
            LiveEffectEngine.setAPI(apiSelection);
            startEffect();
        }
    }

    private void startEffect() {
        Log.d(TAG, "Attempting to start");

        if (!isRecordPermissionGranted()){
            requestRecordPermission();
            return;
        }

        boolean success = LiveEffectEngine.setEffectOn(true);
        if (success) {
            statusText.setText(R.string.status_playing);
            toggleEffectButton.setText(R.string.stop_effect);
            isPlaying = true;
            EnableAudioApiUI(false);
        } else {
            statusText.setText(R.string.status_open_failed);
            isPlaying = false;
        }
    }

    private void stopEffect() {
        Log.d(TAG, "Playing, attempting to stop");
        LiveEffectEngine.setEffectOn(false);
        resetStatusView();
        toggleEffectButton.setText(R.string.start_effect);
        isPlaying = false;
        EnableAudioApiUI(true);
    }

    private void setSpinnersEnabled(boolean isEnabled){
        if (((RadioButton)findViewById(R.id.slesButton)).isChecked())
        {
            isEnabled = false;
            playbackDeviceSpinner.setSelection(0);
            recordingDeviceSpinner.setSelection(0);
        }
        recordingDeviceSpinner.setEnabled(isEnabled);
        playbackDeviceSpinner.setEnabled(isEnabled);
    }

    private int getRecordingDeviceId(){
        return ((AudioDeviceListEntry)recordingDeviceSpinner.getSelectedItem()).getId();
    }

    private int getPlaybackDeviceId(){
        return ((AudioDeviceListEntry)playbackDeviceSpinner.getSelectedItem()).getId();
    }

    private boolean isRecordPermissionGranted() {
        return (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) ==
                PackageManager.PERMISSION_GRANTED);
    }

    private void requestRecordPermission(){
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.RECORD_AUDIO},
                AUDIO_EFFECT_REQUEST);
    }

    private void resetStatusView() {
        statusText.setText(R.string.status_warning);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (AUDIO_EFFECT_REQUEST != requestCode) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 1 ||
                grantResults[0] != PackageManager.PERMISSION_GRANTED) {

            // User denied the permission, without this we cannot record audio
            // Show a toast and update the status accordingly
            statusText.setText(R.string.status_record_audio_denied);
            Toast.makeText(getApplicationContext(),
                    getString(R.string.need_record_audio_permission),
                    Toast.LENGTH_SHORT)
                    .show();
        } else {
            // Permission was granted, start live effect
            toggleEffect();
        }
    }
}
