cmake_minimum_required(VERSION 3.4.1)

project(thread-hook)

add_subdirectory(../xhook/src/main/cpp ${CMAKE_CURRENT_BINARY_DIR}/libxhook)

include_directories(src/main/cpp)

add_library(thread-hook
        SHARED
        src/main/cpp/threadhook_jni.cc
        )

find_library(log-lib
        log)

target_link_libraries(thread-hook
        ${log-lib}
        xhook)