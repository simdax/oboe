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
        callback->output2LChannels = {  };
        callback->output2RChannels = {  };
        callback->maxIn = 2;
        callback->maxOut = 2;
        callback->settings.ai = true;
        callback->CompressorOn = false;
        callback->settings.fadeOn = false;
        callback->settings.peakFilter = false;
        callback->settings.samplerate = 48000;
        callback->settings.master_gain_L = 15;
        callback->settings.master_gain_R = 15;
        callback->Compressor->_prepare(48000, 1024);
        callback->Compressor->setAttackTime(0.000012);
        callback->Compressor->setReleaseTime(0.195);
        callback->Compressor->setKnee(0);
        callback->Compressor->setRatio(100);
        callback->Compressor->setThreshold(-6.3);
        callback->Compressor->setMakeUpGain(5);
        callback->setPresetMode(0);
    }

    virtual oboe::DataCallbackResult
    onBothStreamsReady(
            const void *inputData,
            int   numInputFrames,
            void *outputData,
            int   numOutputFrames) {
        const float *inputFloats = static_cast<const float *>(inputData);
        float *outputFloats = static_cast<float *>(outputData);
        int32_t samplesPerFrame = getOutputStream()->getChannelCount();
        int32_t numInputSamples = numInputFrames * samplesPerFrame;

        callback->tick(inputFloats, outputFloats, numInputFrames); // * 0.515; // do some arbitrary processing
        return oboe::DataCallbackResult::Continue;
    }

    std::unique_ptr<CallbackDataStruct> callback;
};
#endif //SAMPLES_FULLDUPLEXPASS_H
