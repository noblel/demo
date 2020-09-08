/*
 * Tencent is pleased to support the open source community by making wechat-matrix available.
 * Copyright (C) 2018 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the BSD 3-Clause License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

//
// jni interfaces
// posix io hook
// Created by liyongjie on 2017/6/7.
//

#include <jni.h>
#include <cstddef>
#include <cstring>
#include <android/log.h>
#include <assert.h>
#include <xhook.h>
#include <string>
#include <algorithm>

namespace ThreadHook {

static const char *const kTag = "ThreadHook.JNI";

static int (*original_thread_create)(pthread_t *thread,
                                     const pthread_attr_t *attr,
                                     void *(*start_routine)(void *),
                                     void *arg);

static JavaVM *kJvm;

const static char *TARGET_MODULES[] = {
    "libart.so"
};
const static size_t
    TARGET_MODULE_COUNT = sizeof(TARGET_MODULES) / sizeof(char *);

extern "C" {

int ProxyThreadCreate(pthread_t *thread,
                      const pthread_attr_t *attr,
                      void *(*start_routine)(void *),
                      void *arg) {
  __android_log_print(ANDROID_LOG_INFO,
                      kTag,
                      "create in %s.",
                      "libart.so");
  return original_thread_create(thread, attr, start_routine, arg);
}

JNIEXPORT jboolean JNICALL
Java_cn_noblel_threadhook_ThreadHook_doHook(JNIEnv *env,
                                                               jclass type) {
  __android_log_print(ANDROID_LOG_INFO, kTag, "doHook");

  for (int i = 0; i < TARGET_MODULE_COUNT; ++i) {
    const char *so_name = TARGET_MODULES[i];
    __android_log_print(ANDROID_LOG_INFO,
                        kTag,
                        "try to hook function in %s.",
                        so_name);

    void *soinfo = xhook_elf_open(so_name);
    if (!soinfo) {
      __android_log_print(ANDROID_LOG_WARN,
                          kTag,
                          "Failure to open %s, try next.",
                          so_name);
      continue;
    }

    xhook_hook_symbol(soinfo,
                      "pthread_create",
                      (void *) ProxyThreadCreate,
                      (void **) &original_thread_create);

    xhook_elf_close(soinfo);
  }

  __android_log_print(ANDROID_LOG_INFO, kTag, "doHook done.");
  return JNI_TRUE;
}

JNIEXPORT jboolean JNICALL
Java_cn_noblel_threadhook_ThreadHook_doUnHook(JNIEnv *env, jclass type) {
  __android_log_print(ANDROID_LOG_INFO, kTag, "doUnHook");
  for (int i = 0; i < TARGET_MODULE_COUNT; ++i) {
    const char *so_name = TARGET_MODULES[i];
    void *soinfo = xhook_elf_open(so_name);
    if (!soinfo) {
      continue;
    }
    xhook_hook_symbol(soinfo,
                      "pthread_create",
                      (void *) original_thread_create,
                      nullptr);
    xhook_elf_close(soinfo);
  }
  return JNI_TRUE;
}

static bool InitJniEnv(JavaVM *vm) {
  kJvm = vm;
  JNIEnv *env = NULL;
  if (kJvm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
    __android_log_print(ANDROID_LOG_ERROR, kTag, "InitJniEnv GetEnv !JNI_OK");
    return false;
  }
  return true;
}

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
  __android_log_print(ANDROID_LOG_DEBUG, kTag, "JNI_OnLoad");

  if (!InitJniEnv(vm)) {
    return -1;
  }

  __android_log_print(ANDROID_LOG_DEBUG, kTag, "JNI_OnLoad done");
  return JNI_VERSION_1_6;
}

JNIEXPORT void JNICALL JNI_OnUnload(JavaVM *vm, void *reserved) {
  __android_log_print(ANDROID_LOG_DEBUG, kTag, "JNI_OnUnload done");
  JNIEnv *env;
  kJvm->GetEnv((void **) &env, JNI_VERSION_1_6);
}

}
}
