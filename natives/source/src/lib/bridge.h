#pragma once

#include <jni.h>

//

typedef struct bridge {
    jclass c_bridge;
    jmethodID m_raise;
    jmethodID m_handle_of;
    jmethodID m_string_encode;
    jmethodID m_string_decode;
    jmethodID m_describe;
} bridge_t;

//

int bridge_init(bridge_t *bridge, JNIEnv *env);

void bridge_raise(bridge_t *bridge, JNIEnv *env, int code);

void *bridge_handle_of(bridge_t *bridge, JNIEnv *env, jobject remote);

const char *bridge_string_encode(bridge_t *bridge, JNIEnv *env, jstring s);

jstring bridge_string_decode(bridge_t *bridge, JNIEnv *env, const char *s);

const char *bridge_describe(bridge_t *bridge, JNIEnv *env, jthrowable t);
