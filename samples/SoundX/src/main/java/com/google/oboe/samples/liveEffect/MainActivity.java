package com.google.oboe.samples.liveEffect;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.oboe.samples.audio_device.AudioDeviceListEntry;
import com.google.oboe.samples.audio_device.AudioDeviceSpinner;

public class MainActivity extends FragmentActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = MainActivity.class.getName();
    private static final int AUDIO_EFFECT_REQUEST = 0;
    private static final int OBOE_API_AAUDIO = 0;
    private static final int OBOE_API_OPENSL_ES=1;

    static {
        System.loadLibrary("liveEffect");
    }
    private TextView Presets;

    private Password Password;
    //private PresetVibrations PresetVibrations;
    //private CustomizeIA CustomizeIA;

    native void create(TextView view);

    private TextView statusText;
    private Button toggleEffectButton;
    private AudioDeviceSpinner recordingDeviceSpinner;
    private AudioDeviceSpinner playbackDeviceSpinner;
    private boolean isPlaying = false;

    private int apiSelection = OBOE_API_AAUDIO;

    void Set(CharSequence str, double[] ignored_gains)
    {
        Presets.setText(str);
        // int i = Arrays.asList(PresetVibrations.Presets).indexOf(str);
        // if (i != -1 && PresetVibrations.CheckItems[i])
        // {
        //     VibrateFor(300);
        // }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Presets = findViewById(R.id.presets);
        statusText = findViewById(R.id.status_view_text);

        Password = new Password();
        //PresetVibrations = new PresetVibrations();
        //Button vibrationPresetsButton = findViewById(R.id.VibrationPresetsButton);
        //vibrationPresetsButton.setOnClickListener(v -> PresetVibrations.show(getSupportFragmentManager(), "Vibrations"));

        //CustomizeIA = new CustomizeIA();
        //Button CustomizeButton= findViewById(R.id.customize);
        //CustomizeButton.setOnClickListener(v -> CustomizeIA.show(getSupportFragmentManager(), "Customize"));

        toggleEffectButton = findViewById(R.id.button_toggle_effect);
        toggleEffectButton.setOnClickListener(view -> toggleEffect());
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
        findViewById(R.id.aaudioButton).setOnClickListener(v -> {
            if (((RadioButton)v).isChecked()) {
                apiSelection = OBOE_API_AAUDIO;
                setSpinnersEnabled(true);
            }
        });
        findViewById(R.id.slesButton).setOnClickListener(v -> {
            if (((RadioButton)v).isChecked()) {
                apiSelection = OBOE_API_OPENSL_ES;
                setSpinnersEnabled(false);
            }
        });
        LiveEffectEngine.setDefaultStreamValues(this);
    }

    private void EnableAudioApiUI(boolean enable) {
        boolean mAAudioRecommended = true;
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

    //private void VibrateFor(long Time) {
    //    Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
    //    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    //        v.vibrate(VibrationEffect.createOneShot(Time, VibrationEffect.DEFAULT_AMPLITUDE));
    //    } else {
    //        //deprecated in API 26
    //        v.vibrate(Time);
    //    }
    //}

    static class Bob implements AudioManager.OnAudioFocusChangeListener {
        @Override
        public void onAudioFocusChange(int focusChange) {
                switch (focusChange) {
                    case AudioManager.AUDIOFOCUS_GAIN:
//                                if (mPlaybackDelayed || mResumeOnFocusGain) {
//                                    synchronized (mFocusLock) {
//                                        mPlaybackDelayed = false;
//                                        mResumeOnFocusGain = false;
//                                    }
//                                    playbackNow();
//                                }
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS:
                        //synchronized (mFocusLock) {
                        //    // this is not a transient loss, we shouldn't automatically resume for now
                        //    mResumeOnFocusGain = false;
                        //    mPlaybackDelayed = false;
                        //}
                        //pausePlayback();
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                        // we handle all transient losses the same way because we never duck audio books
                        //synchronized (mFocusLock) {
                        //    // we should only resume if playback was interrupted
                        //    mResumeOnFocusGain = mMediaPlayer.isPlaying();
                        //    mPlaybackDelayed = false;
                        //}
                        //pausePlayback();
                        break;
                }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onStart() {
        super.onStart();
        AudioManager mAudioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
//        AudioAttributes mPlaybackAttributes = new AudioAttributes.Builder()
//               .setUsage(AudioAttributes.USAGE_MEDIA)
//                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
//                .build();
        AudioFocusRequest mFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
//                .setAudioAttributes(mPlaybackAttributes)
//                .setAcceptsDelayedFocusGain(true)
//                .setWillPauseWhenDucked(true)
                .setOnAudioFocusChangeListener(new Bob())
                .build();
        mAudioManager.requestAudioFocus(mFocusRequest);
        //setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    protected void onStop() {
        //stopEffect();
        //LiveEffectEngine.delete();
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        create(findViewById(R.id.presets));
        //mAAudioRecommended = LiveEffectEngine.isAAudioRecommended();
        //EnableAudioApiUI(true);
        //LiveEffectEngine.setAPI(apiSelection);
        //PresetVibrations.setPresets(LiveEffectEngine.GetPresets());
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

        if (!isRecordPermissionGranted()) {
            requestRecordPermission();
            return;
        }
        com.google.oboe.samples.liveEffect.Password.OK = () -> {
            Play();
            return null;
        };
        if (Password.input != null
                && Password.input.getText().toString().equals("A")) {
            Play();
        } else {
            Password.show(getSupportFragmentManager(), "Password");
        }
    }

    private void Play()
    {
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
