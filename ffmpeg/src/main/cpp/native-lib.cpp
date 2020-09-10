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

extern "C" JNIEXPORT jstring JNICALL
Java_cn_noblel_ffmpeg_FFmpegHelper_getAvFormatInfo(
    JNIEnv *env,
    jclass) {
  char info[40000]={0};
  av_register_all();
  struct URLProtocol *pup = nullptr;
  //Input
  struct URLProtocol **p_temp = &pup;
  avio_enum_protocols((void **)p_temp, 0);
  while ((*p_temp) != nullptr){
    sprintf(info, "%s[In ][%10s]\n", info, avio_enum_protocols((void **)p_temp, 0));
  }
  pup = nullptr;
  //Output
  avio_enum_protocols((void **)p_temp, 1);
  while ((*p_temp) != nullptr){
    sprintf(info, "%s[Out][%10s]\n", info, avio_enum_protocols((void **)p_temp, 1));
  }
  return env->NewStringUTF(info);
}

#ifdef __cplusplus
};
#endif