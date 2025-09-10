#include <stdlib.h>
#include <stdint.h>
#include <string.h>
#include "bridge.h"

//

int bridge_init(bridge_t *bridge, JNIEnv *env) {
    jclass c_bridge = (*env)->FindClass(env, "io/github/wasabithumb/jwebview/util/Bridge");
    if (c_bridge == NULL) return 1;
    jmethodID m_raise = (*env)->GetStaticMethodID(env, c_bridge, "raise", "(I)V");
    if (m_raise == NULL) return 1;
    jmethodID m_handle_of = (*env)->GetStaticMethodID(env, c_bridge, "handleOf", "(Lio/github/wasabithumb/jwebview/util/RemoteObject;)J");
    if (m_handle_of == NULL) return 1;
    jmethodID m_string_encode = (*env)->GetStaticMethodID(env, c_bridge, "stringEncode", "(Ljava/lang/String;)Ljava/nio/ByteBuffer;");
    if (m_string_encode == NULL) return 1;
    jmethodID m_string_decode = (*env)->GetStaticMethodID(env, c_bridge, "stringDecode", "(Ljava/nio/ByteBuffer;)Ljava/lang/String;");
    if (m_string_decode == NULL) return 1;
    jmethodID m_describe = (*env)->GetStaticMethodID(env, c_bridge, "describe", "(Ljava/lang/Throwable;)Ljava/nio/ByteBuffer;");
    if (m_describe == NULL) return 1;

    bridge->c_bridge = c_bridge;
    bridge->m_raise = m_raise;
    bridge->m_handle_of = m_handle_of;
    bridge->m_string_encode = m_string_encode;
    bridge->m_string_decode = m_string_decode;
    bridge->m_describe = m_describe;
    return 0;
}

void bridge_raise(bridge_t *bridge, JNIEnv *env, int code) {
    jvalue arg;
    arg.i = (jint) code;
    (*env)->CallStaticVoidMethodA(env, bridge->c_bridge, bridge->m_raise, &arg);
}

void *bridge_handle_of(bridge_t *bridge, JNIEnv *env, jobject remote) {
    jvalue arg;
    arg.l = remote;

    jlong val = (*env)->CallStaticLongMethodA(env, bridge->c_bridge, bridge->m_handle_of, &arg);
    uintptr_t ptr = (uintptr_t) val;
    return (void *) ptr;
}

const char *bridge_string_encode(bridge_t *bridge, JNIEnv *env, jstring s) {
    jvalue arg;
    arg.l = s;

    jobject buf = (*env)->CallStaticObjectMethodA(env, bridge->c_bridge, bridge->m_string_encode, &arg);
    void *addr = (*env)->GetDirectBufferAddress(env, buf);
    return addr;
}

jstring bridge_string_decode(bridge_t *bridge, JNIEnv *env, const char *s) {
    jobject buf = (*env)->NewDirectByteBuffer(env, (void *) s, (jlong) strlen(s));
    jvalue arg;
    arg.l = buf;

    jobject str = (*env)->CallStaticObjectMethodA(env, bridge->c_bridge, bridge->m_string_decode, &arg);
    return str;
}

const char *bridge_describe(bridge_t *bridge, JNIEnv *env, jthrowable t) {
    jvalue arg;
    arg.l = t;

    jobject buf = (*env)->CallStaticObjectMethodA(env, bridge->c_bridge, bridge->m_describe, &arg);
    void *addr = (*env)->GetDirectBufferAddress(env, buf);
    return addr;
}
