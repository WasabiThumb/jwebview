#pragma once

#include <jni.h>
#include <webview.h>

//

typedef struct dispatch_handler {
    JavaVM *vm;
    jclass c_dispatch_callback;
    jmethodID m_invoke;
} dispatch_handler_t;

//

int dispatch_handler_init(dispatch_handler_t *handler, JNIEnv *env);

void dispatch_handler_queue(dispatch_handler_t *handler, JNIEnv *env, webview_t wv, jobject obj, jobject callback);

