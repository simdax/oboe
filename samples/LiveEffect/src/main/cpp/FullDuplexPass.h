#ifndef SAMPLES_FULLDUPLEXPASS_H
#define SAMPLES_FULLDUPLEXPASS_H

#include "SoundX/Audio.h"

using namespace std::literals;

class FullDuplexPass : public oboe::FullDuplexStream {
public:
    FullDuplexPass()
    {
        callback = std::make_unique<CallbackDataStruct>();
        callback->input1LChannels = { 0 };
        callback->input1RChannels = { 1 };
        callback->output1LChannels = { 0 };
        callback->output1RChannels = { 1 };
        callback->maxIn = 1;
        callback->maxOut = 1;
        callback->settings.ai = true;
        callback->CompressorOn = true;
        callback->settings.fadeOn = false;
        callback->settings.peakFilter = false;
        callback->settings.master_gain_L = 10;
        callback->settings.master_gain_R = 10;
        callback->settings.samplerate = 48000;
        callback->Compressor->_prepare(48000, 1024);
        callback->Compressor->setAttackTime(0.000012);
        callback->Compressor->setReleaseTime(0.195);
        callback->Compressor->setKnee(0);
        callback->Compressor->setRatio(100);
        callback->Compressor->setThreshold(-6.3);
        callback->Compressor->setMakeUpGain(5);
        callback->setPresetMode(0);

        const auto& Settings = *callback->settings.Presets.find("MUSIC_INSTRUMENTS_KEYBOARD_SYNTHESIZER");
        callback->UpdateSettings(Settings.second);
        std::thread([this](){
            while (true)
            {
                LOGD("first: %f", frames_mono[0]);
                std::this_thread::sleep_for(0.5s);
            }
        }).detach();
    }

    static constexpr size_t b = 1024;
    float frames_mono[b];
    float frames_stereo_L[b];
    float frames_stereo_R[b];
    float frames_band[b];
    float frames_band_L[b];
    float frames_band_R[b];

    void process(const size_t bufferFrames) {
        for (unsigned int i = 0; i < bufferFrames; i++) {
            frames_stereo_L[i] = 0;
            frames_stereo_R[i] = 0;
        }
        for (int band = 0; band < callback->settings.freq_bands - 1; band++) {
            if (!callback->settings.mute[band] &&
                (!callback->settings.solo_on || callback->settings.solo[band])) { // Solo and Mute logic
                std::memcpy(frames_band, frames_mono, bufferFrames * sizeof(float));
                callback->apply_bandpass_filter(*callback->filters_low_1[band], *callback->filters_high_1[band], frames_band, bufferFrames); // Filters 1
                auto octave = callback->settings.pitch[band];
                callback->apply_pitcher(frames_band, *callback->pitchers[band], bufferFrames); // Pitchers
                if (octave < 0) {
                    callback->apply_gain_db(frames_band, 3.4 * -octave, bufferFrames);
                }
                callback->apply_bandpass_filter(*callback->filters_low_2[band], *callback->filters_high_2[band], frames_band, bufferFrames); // Filters 2
                std::memcpy(frames_band_L, frames_band, bufferFrames * sizeof(float));
                std::memcpy(frames_band_R, frames_band, bufferFrames * sizeof(float));
                callback->apply_gain_ratio(frames_band_L, 1.5 * 0.5 / (callback->settings.freq_bands - 1), bufferFrames); // Gain ratio
                callback->apply_gain_ratio(frames_band_R, 1.5 * 0.5 / (callback->settings.freq_bands - 1), bufferFrames);
                callback->apply_gain_db(frames_band_L, callback->settings.gain_L[band], bufferFrames);
                callback->apply_gain_db(frames_band_R, callback->settings.gain_R[band], bufferFrames);
                for (unsigned int i = 0; i < bufferFrames; i++) {
                    const auto& [sample_L, sample_R] = callback->UpdateGains(frames_band_L[i], frames_band_R[i], band);
                    frames_stereo_L[i] += sample_L;
                    frames_stereo_R[i] += sample_R;
                }
            }
        }
        callback->apply_gain_db(frames_stereo_L, callback->settings.master_gain_L, bufferFrames);
        callback->apply_gain_db(frames_stereo_R, callback->settings.master_gain_R, bufferFrames);
        callback->apply_gain_db(frames_stereo_L, 6, bufferFrames);
        callback->apply_gain_db(frames_stereo_R, 6, bufferFrames);
        float* channels[2] = { &frames_stereo_L[0], &frames_stereo_R[0] };
        callback->Compressor->_process2(channels, 2, bufferFrames);
    }

    virtual oboe::DataCallbackResult
    onBothStreamsReady(
            const void *inputData,
            int   numInputFrames,
            void *outputData,
            int   numOutputFrames) {

        const float *inputFloats = static_cast<const float *>(inputData);
        float *outputFloats = static_cast<float *>(outputData);

        for (int32_t i = 0; i < numInputFrames; i++) {
            frames_mono[i] = (inputFloats[i * 2]  + inputFloats[i * 2 + 1]) / 3; // do some arbitrary processing
        }
        process(numInputFrames);
        for (int32_t i = 0; i < numInputFrames; i++) {
            outputFloats[i * 2] = frames_stereo_L[i];
            outputFloats[i * 2 + 1] = frames_stereo_R[i];
        }
        return oboe::DataCallbackResult::Continue;

        for (int32_t i = 0; i < numInputFrames * 2; i++) {
            *outputFloats++ = *inputFloats++ * 0.95f; // do some arbitrary processing
        }
        return oboe::DataCallbackResult::Continue;
    }

    std::unique_ptr<CallbackDataStruct> callback;
};
#endif //SAMPLES_FULLDUPLEXPASS_H
