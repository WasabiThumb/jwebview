cmake_minimum_required(VERSION 3.31)
project(jwebview C CXX)

set(CMAKE_C_STANDARD 17)
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -O0")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -O0")

file(GLOB_RECURSE sources CONFIGURE_DEPENDS src/*.c src/*.h)

find_package(JNI)
include(FetchContent)
FetchContent_Declare(
        webview
        GIT_REPOSITORY https://github.com/webview/webview
        GIT_TAG 0.12.0)
FetchContent_MakeAvailable(webview)

add_library(jwebview SHARED ${sources})
target_include_directories(jwebview PUBLIC include)
target_link_libraries(jwebview PRIVATE JNI::JNI webview::core_static)
