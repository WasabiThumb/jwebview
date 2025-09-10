#pragma once

#include <jni.h>
#include <webview.h>
#include "bridge.h"

//

typedef struct bind_handler {
    JavaVM *vm;
    bridge_t *bridge;
    jclass c_bind_callback;
    jmethodID m_invoke;
} bind_handler_t;

typedef struct bind_registration {
    bind_handler_t *handler;
    webview_t wv;
    jobject callback_ref;
} bind_registration_t;

//

int bind_handler_init(bind_handler_t *handler, JNIEnv *env, bridge_t *bridge);

webview_error_t bind_handler_register(bind_handler_t *handler, JNIEnv *env, bind_registration_t *registration, webview_t wv, const char *name, jobject callback);

webview_error_t bind_handler_unregister(bind_registration_t *registration, JNIEnv *env, const char *name);
