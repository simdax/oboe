#ifndef SAMPLES_FULLDUPLEXPASS_H
#define SAMPLES_FULLDUPLEXPASS_H

#include "SoundX/Audio.h"

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
        callback->settings.samplerate = 48000;
        callback->Compressor->_prepare(48000, 1024);
        callback->Compressor->setAttackTime(0.000012);
        callback->Compressor->setReleaseTime(0.195);
        callback->Compressor->setKnee(0);
        callback->Compressor->setRatio(100);
        callback->Compressor->setThreshold(-6.3);
        callback->Compressor->setMakeUpGain(5);
        callback->setPresetMode(0);

        //const auto& Settings = *callback->settings.Presets.find("Default");
        //callback->UpdateSettings(Settings.second);
        //setDefault();
    }

    void setDefault()
    {
        PresetSettings Nul;

        Nul.fc_low_1 = { 0,0,0,0,0 };
        Nul.fc_high_1 = { 20000, 20000, 20000, 20000, 20000};
        Nul.fc_low_2 = { 0,0,0,0,0 };
        Nul.fc_high_2 = { 20000, 20000, 20000, 20000, 20000 };
        Nul.pitch = { 0, 0, 0, 0, 0};
        Nul.gain_L = { 0, 0, 0, 0, 0};
        Nul.gain_R = { 0, 0, 0, 0, 0};
        callback->UpdateSettings(Nul);
    }

    oboe::AudioFormat mFormat = oboe::AudioFormat::I16;

    virtual oboe::DataCallbackResult
    onBothStreamsReady(
            const void *inputData,
            int   numInputFrames,
            void *outputData,
            int   numOutputFrames) {
        if (mFormat == oboe::AudioFormat::I16)
        {
            using format = short;
            const auto* in = static_cast<const format*>(inputData);
            auto* out = static_cast<format*>(outputData);
            callback->tickShort(in, out, numInputFrames);
        }
        return oboe::DataCallbackResult::Continue;
    }

    std::unique_ptr<CallbackDataStruct> callback;
};
#endif //SAMPLES_FULLDUPLEXPASS_H
