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

    virtual oboe::DataCallbackResult
    onBothStreamsReady(
            const void *inputData,
            int   numInputFrames,
            void *outputData,
            int   numOutputFrames) {
        // Copy the input samples to the output with a little arbitrary gain change.

        // This code assumes the data format for both streams is Float.
        const float *inputFloats = static_cast<const float *>(inputData);
        float *outputFloats = static_cast<float *>(outputData);

        // It also assumes the channel count for each stream is the same.
        int32_t samplesPerFrame = getOutputStream()->getChannelCount();
        int32_t numInputSamples = numInputFrames * samplesPerFrame;
        int32_t numOutputSamples = numOutputFrames * samplesPerFrame;

        // It is possible that there may be fewer input than output samples.
        int32_t samplesToProcess = std::min(numInputSamples, numOutputSamples);
        for (int32_t i = 0; i < samplesToProcess; i++) {
            *outputFloats++ = *inputFloats++ * 0.95; // do some arbitrary processing
        }

        // If there are fewer input samples then clear the rest of the buffer.
        int32_t samplesLeft = numOutputSamples - numInputSamples;
        for (int32_t i = 0; i < samplesLeft; i++) {
            *outputFloats++ = 0.0; // silence
        }

        return oboe::DataCallbackResult::Continue;
    }

//    oboe::AudioFormat mFormat = oboe::AudioFormat::I16;
//
//    virtual oboe::DataCallbackResult
//    onBothStreamsReady(
//            const void *inputData,
//            int   numInputFrames,
//            void *outputData,
//            int   numOutputFrames) {
//        if (mFormat == oboe::AudioFormat::I16)
//        {
//            using format = short;
//            const auto* in = static_cast<const format*>(inputData);
//            auto* out = static_cast<format*>(outputData);
//            callback->tickShort(in, out, numInputFrames);
//        }
//        return oboe::DataCallbackResult::Continue;
//    }

    std::unique_ptr<CallbackDataStruct> callback;
};
#endif //SAMPLES_FULLDUPLEXPASS_H
