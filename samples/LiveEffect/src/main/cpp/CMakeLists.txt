#
# Copyright 2018 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
cmake_minimum_required(VERSION 3.4.1)
project(liveEffect LANGUAGES C CXX)

get_filename_component(SAMPLE_ROOT_DIR
    ${CMAKE_CURRENT_SOURCE_DIR}/../../../.. ABSOLUTE)

### INCLUDE OBOE LIBRARY ###
set (OBOE_DIR ${SAMPLE_ROOT_DIR}/..)
add_subdirectory(${OBOE_DIR} ./oboe-bin)

set(HOME "\\\\wsl.localhost/Debian/home/simdax/soundx-ai")
set(ANDROID_BUILD "${HOME}/android/build_${ANDROID_ABI}")
add_library(soundx STATIC IMPORTED)
set_target_properties(soundx PROPERTIES IMPORTED_LOCATION "${ANDROID_BUILD}/libsoundx_backend.a")
set(TFLITE_DIR "${ANDROID_BUILD}/tflite")

add_compile_definitions(NEON2SSE_DISABLE_PERFORMANCE_WARNING)

add_library(liveEffect
    SHARED
        LiveEffectEngine.cpp
        jni_bridge.cpp
        ${SAMPLE_ROOT_DIR}/debug-utils/trace.cpp)
target_include_directories(liveEffect
    PRIVATE
        ${SAMPLE_ROOT_DIR}/debug-utils
        ${OBOE_DIR}/include
        "${HOME}/android/include"
)
target_compile_features(liveEffect PRIVATE cxx_std_17)
target_link_libraries(liveEffect
    PRIVATE
        oboe
        android
        atomic
        log
        soundx
        ${TFLITE_DIR}/_deps/farmhash-build/libfarmhash.a
        ${TFLITE_DIR}/libtensorflow-lite.a
        ${TFLITE_DIR}/pthreadpool/libpthreadpool.a
        ${TFLITE_DIR}/_deps/cpuinfo-build/libcpuinfo.a
        ${TFLITE_DIR}/_deps/xnnpack-build/libXNNPACK.a
        ${TFLITE_DIR}/_deps/abseil-cpp-build/absl/flags/libabsl_flags_marshalling.a
${TFLITE_DIR}/_deps/abseil-cpp-build/absl/flags/libabsl_flags_program_name.a
${TFLITE_DIR}/_deps/abseil-cpp-build/absl/flags/libabsl_flags_reflection.a
${TFLITE_DIR}/_deps/abseil-cpp-build/absl/flags/libabsl_flags_private_handle_accessor.a
${TFLITE_DIR}/_deps/abseil-cpp-build/absl/flags/libabsl_flags_config.a
${TFLITE_DIR}/_deps/abseil-cpp-build/absl/flags/libabsl_flags_internal.a
${TFLITE_DIR}/_deps/abseil-cpp-build/absl/flags/libabsl_flags_commandlineflag_internal.a
${TFLITE_DIR}/_deps/abseil-cpp-build/absl/flags/libabsl_flags_commandlineflag.a
${TFLITE_DIR}/_deps/abseil-cpp-build/absl/flags/libabsl_flags.a
${TFLITE_DIR}/_deps/abseil-cpp-build/absl/strings/libabsl_cordz_functions.a
${TFLITE_DIR}/_deps/abseil-cpp-build/absl/strings/libabsl_strings_internal.a
${TFLITE_DIR}/_deps/abseil-cpp-build/absl/strings/libabsl_cordz_handle.a
${TFLITE_DIR}/_deps/abseil-cpp-build/absl/strings/libabsl_str_format_internal.a
${TFLITE_DIR}/_deps/abseil-cpp-build/absl/strings/libabsl_cordz_info.a
${TFLITE_DIR}/_deps/abseil-cpp-build/absl/strings/libabsl_cord.a
${TFLITE_DIR}/_deps/abseil-cpp-build/absl/strings/libabsl_cord_internal.a
${TFLITE_DIR}/_deps/abseil-cpp-build/absl/strings/libabsl_strings.a
${TFLITE_DIR}/_deps/abseil-cpp-build/absl/container/libabsl_raw_hash_set.a
${TFLITE_DIR}/_deps/abseil-cpp-build/absl/container/libabsl_hashtablez_sampler.a
${TFLITE_DIR}/_deps/abseil-cpp-build/absl/synchronization/libabsl_synchronization.a
${TFLITE_DIR}/_deps/abseil-cpp-build/absl/synchronization/libabsl_graphcycles_internal.a
${TFLITE_DIR}/_deps/abseil-cpp-build/absl/hash/libabsl_city.a
${TFLITE_DIR}/_deps/abseil-cpp-build/absl/hash/libabsl_hash.a
${TFLITE_DIR}/_deps/abseil-cpp-build/absl/hash/libabsl_low_level_hash.a
${TFLITE_DIR}/_deps/abseil-cpp-build/absl/time/libabsl_civil_time.a
${TFLITE_DIR}/_deps/abseil-cpp-build/absl/time/libabsl_time_zone.a
${TFLITE_DIR}/_deps/abseil-cpp-build/absl/time/libabsl_time.a
${TFLITE_DIR}/_deps/abseil-cpp-build/absl/profiling/libabsl_exponential_biased.a
${TFLITE_DIR}/_deps/abseil-cpp-build/absl/types/libabsl_bad_optional_access.a
${TFLITE_DIR}/_deps/abseil-cpp-build/absl/types/libabsl_bad_variant_access.a
${TFLITE_DIR}/_deps/abseil-cpp-build/absl/numeric/libabsl_int128.a
${TFLITE_DIR}/_deps/abseil-cpp-build/absl/base/libabsl_raw_logging_internal.a
${TFLITE_DIR}/_deps/abseil-cpp-build/absl/base/libabsl_throw_delegate.a
${TFLITE_DIR}/_deps/abseil-cpp-build/absl/base/libabsl_base.a
${TFLITE_DIR}/_deps/abseil-cpp-build/absl/base/libabsl_log_severity.a
${TFLITE_DIR}/_deps/abseil-cpp-build/absl/base/libabsl_spinlock_wait.a
${TFLITE_DIR}/_deps/abseil-cpp-build/absl/base/libabsl_malloc_internal.a
${TFLITE_DIR}/_deps/abseil-cpp-build/absl/status/libabsl_status.a
${TFLITE_DIR}/_deps/abseil-cpp-build/absl/debugging/libabsl_stacktrace.a
${TFLITE_DIR}/_deps/abseil-cpp-build/absl/debugging/libabsl_demangle_internal.a
${TFLITE_DIR}/_deps/abseil-cpp-build/absl/debugging/libabsl_symbolize.a
${TFLITE_DIR}/_deps/abseil-cpp-build/absl/debugging/libabsl_debugging_internal.a
${TFLITE_DIR}/_deps/flatbuffers-build/libflatbuffers.a
${TFLITE_DIR}/_deps/ruy-build/ruy/libruy_ctx.a
${TFLITE_DIR}/_deps/ruy-build/ruy/libruy_kernel_avx.a
${TFLITE_DIR}/_deps/ruy-build/ruy/libruy_have_built_path_for_avx512.a
${TFLITE_DIR}/_deps/ruy-build/ruy/libruy_cpuinfo.a
${TFLITE_DIR}/_deps/ruy-build/ruy/libruy_frontend.a
${TFLITE_DIR}/_deps/ruy-build/ruy/libruy_blocking_counter.a
${TFLITE_DIR}/_deps/ruy-build/ruy/libruy_kernel_avx512.a
${TFLITE_DIR}/_deps/ruy-build/ruy/libruy_apply_multiplier.a
${TFLITE_DIR}/_deps/ruy-build/ruy/libruy_block_map.a
${TFLITE_DIR}/_deps/ruy-build/ruy/libruy_trmul.a
${TFLITE_DIR}/_deps/ruy-build/ruy/libruy_kernel_avx2_fma.a
${TFLITE_DIR}/_deps/ruy-build/ruy/profiler/libruy_profiler_instrumentation.a
${TFLITE_DIR}/_deps/ruy-build/ruy/libruy_denormal.a
${TFLITE_DIR}/_deps/ruy-build/ruy/libruy_prepacked_cache.a
${TFLITE_DIR}/_deps/ruy-build/ruy/libruy_system_aligned_alloc.a
${TFLITE_DIR}/_deps/ruy-build/ruy/libruy_context_get_ctx.a
${TFLITE_DIR}/_deps/ruy-build/ruy/libruy_allocator.a
${TFLITE_DIR}/_deps/ruy-build/ruy/libruy_have_built_path_for_avx2_fma.a
${TFLITE_DIR}/_deps/ruy-build/ruy/libruy_pack_avx.a
${TFLITE_DIR}/_deps/ruy-build/ruy/libruy_wait.a
${TFLITE_DIR}/_deps/ruy-build/ruy/libruy_context.a
${TFLITE_DIR}/_deps/ruy-build/ruy/libruy_pack_avx2_fma.a
${TFLITE_DIR}/_deps/ruy-build/ruy/libruy_thread_pool.a
${TFLITE_DIR}/_deps/ruy-build/ruy/libruy_pack_arm.a
${TFLITE_DIR}/_deps/ruy-build/ruy/libruy_tune.a
${TFLITE_DIR}/_deps/ruy-build/ruy/libruy_kernel_arm.a
${TFLITE_DIR}/_deps/ruy-build/ruy/libruy_have_built_path_for_avx.a
${TFLITE_DIR}/_deps/ruy-build/ruy/libruy_prepare_packed_matrices.a
${TFLITE_DIR}/_deps/ruy-build/ruy/libruy_pack_avx512.a
${TFLITE_DIR}/_deps/fft2d-build/libfft2d_fftsg.a
${TFLITE_DIR}/_deps/fft2d-build/libfft2d_fftsg2d.a
)

# Enable optimization flags: if having problems with source level debugging,
# disable -Ofast ( and debug ), re-enable it after done debugging.
#target_compile_options(liveEffect PRIVATE -Wall -Werror "$<$<CONFIG:RELEASE>:-Ofast>")
target_compile_options(liveEffect PRIVATE -Wnognu-inline-cpp-without-extern "$<$<CONFIG:RELEASE>:-Ofast>")


