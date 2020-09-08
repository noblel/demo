# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

set(cpp_dir ${CMAKE_SOURCE_DIR}/src/main/cpp)

add_library( # Sets the name of the library.
        ffmpeg

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        src/main/cpp/native-lib.cpp)

find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log )

include_directories(${cpp_dir})

add_library( avcodec
        SHARED
        IMPORTED)
set_target_properties( avcodec
        PROPERTIES
        IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/jniLibs/${ANDROID_ABI}/libavcodec.so)

add_library( avdevice SHARED IMPORTED)
set_target_properties( avdevice
        PROPERTIES
        IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/jniLibs/${ANDROID_ABI}/libavdevice.so)

add_library( avfilter SHARED IMPORTED)
set_target_properties( avfilter
        PROPERTIES
        IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/jniLibs/${ANDROID_ABI}/libavfilter.so)

add_library( avformat  SHARED IMPORTED)
set_target_properties( avformat
        PROPERTIES
        IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/jniLibs/${ANDROID_ABI}/libavformat.so)

add_library( avutil SHARED IMPORTED)
set_target_properties( avutil
        PROPERTIES
        IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/jniLibs/${ANDROID_ABI}/libavutil.so)

add_library( postproc SHARED IMPORTED)
set_target_properties( postproc
        PROPERTIES
        IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/jniLibs/${ANDROID_ABI}/libpostproc.so)

add_library( swresample
        SHARED
        IMPORTED)
set_target_properties( swresample
        PROPERTIES
        IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/jniLibs/${ANDROID_ABI}/libswresample.so)

add_library( swscale
        SHARED
        IMPORTED)
set_target_properties( swscale
        PROPERTIES
        IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/jniLibs/${ANDROID_ABI}/libswscale.so)


target_link_libraries( # Specifies the target library.
        ffmpeg
        #ffmpeg------start----------
        avcodec
        avdevice
        avfilter
        avformat
        avutil
        postproc
        swresample
        swscale
        #ffmpeg------end------------
        # Links the target library to the log library
        # included in the NDK.
        ${log-lib} )