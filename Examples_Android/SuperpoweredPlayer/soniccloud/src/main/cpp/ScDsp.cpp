#include "ScDsp.h"
#include <android/log.h>

void ScDsp::setAudiogram(int numPoints, float *frequencies, float *leftValues, float *rightValues) {

    __android_log_print(ANDROID_LOG_ERROR, "SonicCloudSDK", "setAudiogram");
}

void ScDsp::setParameter(int index, float value) {

    __android_log_print(ANDROID_LOG_ERROR, "SonicCloudSDK", "setParameter");
}

void ScDsp::process(int numSamples, float *inL, float *inR, float *outL, float *outR) {
    __android_log_print(ANDROID_LOG_ERROR, "SonicCloudSDK", "process");
}
