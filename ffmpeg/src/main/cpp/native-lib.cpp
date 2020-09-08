#include <jni.h>
#include <string>
#ifdef __cplusplus
extern "C" {
#endif

#include <libavformat/avformat.h>

extern "C" JNIEXPORT jstring JNICALL
Java_cn_noblel_ffmpeg_FFmpegHelper_getConfig(
    JNIEnv *env,
    jclass) {
  char info[10000] = { 0 };
  sprintf(info, "%s\n", avcodec_configuration());
  return env->NewStringUTF(info);
}
#ifdef __cplusplus
};
#endif