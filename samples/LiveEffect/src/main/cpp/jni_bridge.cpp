/**
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

#include <jni.h>
#include <logging_macros.h>
#include "LiveEffectEngine.h"

static const int kOboeApiAAudio = 0;
static const int kOboeApiOpenSLES = 1;

static LiveEffectEngine *engine = nullptr;
JavaVM* vm;
jobject Text;

extern "C" {

void Update(
        double presetSettings_gain_L_0, double presetSettings_gain_R_0,
        double presetSettings_gain_L_1, double presetSettings_gain_R_1,
        double presetSettings_gain_L_2, double presetSettings_gain_R_2,
        double presetSettings_gain_L_3, double presetSettings_gain_R_3,
        double presetSettings_gain_L_4, double presetSettings_gain_R_4,
        double presetSettings_gain_L_5, double presetSettings_gain_R_5,
        char *presets, char *meansData) {
    JNIEnv *e;

    vm->AttachCurrentThread(&e, nullptr);
    if (
            vm->GetEnv((void **) &e, JNI_VERSION_1_6) == JNI_OK ||
            vm->GetEnv((void **) &e, JNI_VERSION_1_4) == JNI_OK ||
            vm->GetEnv((void **) &e, JNI_VERSION_1_2) == JNI_OK ||
            vm->GetEnv((void **) &e, JNI_VERSION_1_1) == JNI_OK) {
        std::stringstream ss;
        double cpp_gains[12] = {
                presetSettings_gain_L_0, presetSettings_gain_R_0,
                presetSettings_gain_L_1, presetSettings_gain_R_1,
                presetSettings_gain_L_2, presetSettings_gain_R_2,
                presetSettings_gain_L_3, presetSettings_gain_R_3,
                presetSettings_gain_L_4, presetSettings_gain_R_4,
                presetSettings_gain_L_5, presetSettings_gain_R_5,
        };

        ss << "" << presets << std::endl
           << "" << meansData << std::endl;
        jdoubleArray gains = e->NewDoubleArray(12);
        e->SetDoubleArrayRegion(gains, 0, 12, &cpp_gains[0]);
        e->CallVoidMethod(Text,
                          e->GetMethodID(
                                  e->GetObjectClass(Text),
                                  "SetIAText",
                                  "(Ljava/lang/CharSequence;[D)V"),
                          e->NewStringUTF(ss.str().c_str()), gains
        );
    }
    vm->DetachCurrentThread();
}

JNIEXPORT void JNICALL
Java_com_google_oboe_samples_liveEffect_MainActivity_create(JNIEnv *env, jobject obj,
                                                            jobject view) {
    if (!engine->mDuplexStream->callback->on_settings_update)
    {
        env->GetJavaVM(&vm);
        Text = env->NewGlobalRef(obj);
        engine->mDuplexStream->callback->on_settings_update = Update;
    }
}

JNIEXPORT jboolean JNICALL
Java_com_google_oboe_samples_liveEffect_LiveEffectEngine_create(JNIEnv *env,
                                                               jclass) {
    if (engine == nullptr) {
        engine = new LiveEffectEngine();
    }

    return (engine != nullptr) ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT void JNICALL
Java_com_google_oboe_samples_liveEffect_LiveEffectEngine_delete(JNIEnv *env,
                                                               jclass) {
    if (engine) {
        engine->setEffectOn(false);
        delete engine;
        engine = nullptr;
    }
}

JNIEXPORT jboolean JNICALL
Java_com_google_oboe_samples_liveEffect_LiveEffectEngine_setEffectOn(
    JNIEnv *env, jclass, jboolean isEffectOn) {
    if (engine == nullptr) {
        LOGE(
            "Engine is null, you must call createEngine before calling this "
            "method");
        return JNI_FALSE;
    }

    return engine->setEffectOn(isEffectOn) ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT void JNICALL
Java_com_google_oboe_samples_liveEffect_LiveEffectEngine_setRecordingDeviceId(
    JNIEnv *env, jclass, jint deviceId) {
    if (engine == nullptr) {
        LOGE(
            "Engine is null, you must call createEngine before calling this "
            "method");
        return;
    }

    engine->setRecordingDeviceId(deviceId);
}

JNIEXPORT void JNICALL
Java_com_google_oboe_samples_liveEffect_LiveEffectEngine_setPlaybackDeviceId(
    JNIEnv *env, jclass, jint deviceId) {
    if (engine == nullptr) {
        LOGE(
            "Engine is null, you must call createEngine before calling this "
            "method");
        return;
    }

    engine->setPlaybackDeviceId(deviceId);
}

JNIEXPORT jboolean JNICALL
Java_com_google_oboe_samples_liveEffect_LiveEffectEngine_setAPI(JNIEnv *env,
                                                               jclass type,
                                                               jint apiType) {
    if (engine == nullptr) {
        LOGE(
            "Engine is null, you must call createEngine "
            "before calling this method");
        return JNI_FALSE;
    }

    oboe::AudioApi audioApi;
    switch (apiType) {
        case kOboeApiAAudio:
            audioApi = oboe::AudioApi::AAudio;
            break;
        case kOboeApiOpenSLES:
            audioApi = oboe::AudioApi::OpenSLES;
            break;
        default:
            LOGE("Unknown API selection to setAPI() %d", apiType);
            return JNI_FALSE;
    }

    return engine->setAudioApi(audioApi) ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jboolean JNICALL
Java_com_google_oboe_samples_liveEffect_LiveEffectEngine_isAAudioRecommended(
    JNIEnv *env, jclass type) {
    if (engine == nullptr) {
        LOGE(
            "Engine is null, you must call createEngine "
            "before calling this method");
        return JNI_FALSE;
    }
    return engine->isAAudioRecommended() ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT void JNICALL
Java_com_google_oboe_samples_liveEffect_LiveEffectEngine_native_1setDefaultStreamValues(JNIEnv *env,
                                               jclass type,
                                               jint sampleRate,
                                               jint framesPerBurst) {
    oboe::DefaultStreamValues::SampleRate = (int32_t) sampleRate;
    oboe::DefaultStreamValues::FramesPerBurst = (int32_t) framesPerBurst;
}
} // extern "C"

extern "C"
JNIEXPORT void JNICALL
Java_com_google_oboe_samples_liveEffect_LiveEffectEngine_Debug(JNIEnv *env, jclass clazz, jint setting, jint value) {
    if (!engine){
        return;
    }
    if (engine && engine->mDuplexStream &&
        engine->mDuplexStream->callback
            )
    {
        engine->mDuplexStream->callback->setPitchers(setting, value);
    }
}