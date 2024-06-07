/*
 * Copyright 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
        callback->output2LChannels = { 2 };
        callback->output2RChannels = { 3 };
        callback->maxIn = 1;
        callback->maxOut = 2;
        callback->settings.ai = false;
        callback->CompressorOn = false;//true;
        callback->settings.fadeOn = false;//true;
        callback->settings.peakFilter = false;//true;

        callback->settings.samplerate = 48000;
        callback->settings.solo = { 0, 0, 0, 0, 0 };
        callback->settings.mute = { 0, 0, 0, 0, 0 };
        callback->settings.fc_low_1 = { 120, 450, 900, 1900 };
        callback->settings.fc_high_1 = { 450, 900, 1900, 18000 };
        callback->settings.pitch = { 0, 0, 0, 0, 0 };
        callback->settings.fc_low_2 = { 2, 100, 100, 70, 2 };
        callback->settings.fc_high_2 = { 20000, 20000, 20000, 20000, 400 };
        callback->settings.gain_L = { -6, -32, -38, -50, 0, 0 };
        callback->settings.gain_R = { -50, -2, -22, -22, -10, -1 };
        callback->settings.master_gain_L = 9;
        callback->settings.master_gain_R = 9;

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
        // Copy the input samples to the output with a little arbitrary gain change.

        // This code assumes the data format for both streams is Float.
        const float *inputFloats = static_cast<const float *>(inputData);
        float *outputFloats = static_cast<float *>(outputData);

        // It also assumes the channel count for each stream is the same.
        int32_t samplesPerFrame = getOutputStream()->getChannelCount();
        int32_t numInputSamples = numInputFrames * samplesPerFrame;
        int32_t numOutputSamples = numOutputFrames * samplesPerFrame;

        if (!soundx)
        {
        // It is possible that there may be fewer input than output samples.
            int32_t samplesToProcess = std::min(numInputSamples, numOutputSamples);
            for (int32_t i = 0; i < samplesToProcess; i++) {
                *outputFloats++ = *inputFloats++ * 1.915; // do some arbitrary processing
            }
        }
        else {
            callback->tick(inputFloats, outputFloats, numInputFrames); // * 0.515; // do some arbitrary processing
        }

        // If there are fewer input samples then clear the rest of the buffer.
        int32_t samplesLeft = numOutputSamples - numInputSamples;
        for (int32_t i = 0; i < samplesLeft; i++) {
            *outputFloats++ = 0.0; // silence
        }

        return oboe::DataCallbackResult::Continue;
    }

    bool soundx = false;
    std::unique_ptr<CallbackDataStruct> callback;
};
#endif //SAMPLES_FULLDUPLEXPASS_H
