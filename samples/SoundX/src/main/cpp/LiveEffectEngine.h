#ifndef OBOE_LIVEEFFECTENGINE_H
#define OBOE_LIVEEFFECTENGINE_H

#include <jni.h>
#include <oboe/Oboe.h>
#include <string>
#include <thread>
#include "FullDuplexPass.h"

class LiveEffectEngine : public oboe::AudioStreamCallback {
public:
    LiveEffectEngine();

    void setRecordingDeviceId(int32_t deviceId);
    void setPlaybackDeviceId(int32_t deviceId);

    /**
     * @param isOn
     * @return true if it succeeds
     */
    bool setEffectOn(bool isOn);

    /*
     * oboe::AudioStreamDataCallback interface implementation
     */
    oboe::DataCallbackResult onAudioReady(oboe::AudioStream *oboeStream,
                                          void *audioData, int32_t numFrames) override;

    /*
     * oboe::AudioStreamErrorCallback interface implementation
     */
    void onErrorBeforeClose(oboe::AudioStream *oboeStream, oboe::Result error) override;
    void onErrorAfterClose(oboe::AudioStream *oboeStream, oboe::Result error) override;

    bool setAudioApi(oboe::AudioApi);
    static bool isAAudioRecommended();

    FullDuplexPass    mFullDuplexPass;

private:
    bool              mIsEffectOn = false;
    int32_t           mRecordingDeviceId = oboe::kUnspecified;
    int32_t           mPlaybackDeviceId = oboe::kUnspecified;
    //const oboe::AudioFormat mFormat = oboe::AudioFormat::Float; // for easier processing
    const oboe::AudioFormat mFormat = oboe::AudioFormat::Unspecified; // for easier processing
    oboe::AudioApi    mAudioApi = oboe::AudioApi::AAudio;
    int32_t           mSampleRate = 48000;//oboe::kUnspecified;
    const int32_t     mInputChannelCount = oboe::ChannelCount::Unspecified;
    const int32_t     mOutputChannelCount = oboe::ChannelCount::Unspecified;

    std::shared_ptr<oboe::AudioStream> mRecordingStream;
    std::shared_ptr<oboe::AudioStream> mPlayStream;

    oboe::Result openStreams();

    void closeStreams();

    static void closeStream(std::shared_ptr<oboe::AudioStream> &stream);

    oboe::AudioStreamBuilder *setupCommonStreamParameters(
        oboe::AudioStreamBuilder *builder);
    oboe::AudioStreamBuilder *setupRecordingStreamParameters(
        oboe::AudioStreamBuilder *builder, int32_t sampleRate);
    oboe::AudioStreamBuilder *setupPlaybackStreamParameters(
        oboe::AudioStreamBuilder *builder);
    static void warnIfNotLowLatency(std::shared_ptr<oboe::AudioStream> &stream);
};

extern LiveEffectEngine *engine;

#endif  // OBOE_LIVEEFFECTENGINE_H

