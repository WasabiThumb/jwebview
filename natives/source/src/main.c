#include <stdlib.h>
#include <webview.h>
#include "lib/bridge.h"
#include "lib/dispatch.h"
#include "lib/bind.h"
#include "io_github_wasabithumb_jwebview_JWebViewProviderImpl.h"
#include "io_github_wasabithumb_jwebview_JWebViewImpl.h"
#include "io_github_wasabithumb_jwebview_WebViewImpl.h"
#include "io_github_wasabithumb_jwebview_except_WebViewExceptionImpl.h"

// Global State

static bridge_t BRIDGE;
static dispatch_handler_t DISPATCH_HANDLER;
static bind_handler_t BIND_HANDLER;

// Helpers

#ifdef _WIN32
#pragma pack(push, 1) 
#define PACKED
#else
#define PACKED __attribute__((packed))
#endif

typedef struct PACKED metrics {
    int none[2];
    int min[2];
    int max[2];
    int fixed[2];
} metrics_t;

#ifdef _WIN32
#pragma pack(pop)
#endif


#define CHECK(x) do {                                                          \
                     webview_error_t err = (x);                                \
                     if (WEBVIEW_FAILED(err)) bridge_raise(&BRIDGE, env, err); \
                 } while (0)

// JNI Calls

JNIEXPORT jint JNICALL Java_io_github_wasabithumb_jwebview_JWebViewProviderImpl_setup(JNIEnv *env, jclass cls) {
    return bridge_init(&BRIDGE, env) ||
        dispatch_handler_init(&DISPATCH_HANDLER, env) ||
        bind_handler_init(&BIND_HANDLER, env, &BRIDGE);
}

JNIEXPORT jstring JNICALL Java_io_github_wasabithumb_jwebview_JWebViewImpl_version(JNIEnv *env, jobject obj) {
    const webview_version_info_t *info = webview_version();
    const char *str = info->version_number;
    return bridge_string_decode(&BRIDGE, env, str);
}

JNIEXPORT jlong JNICALL Java_io_github_wasabithumb_jwebview_JWebViewImpl_create0(JNIEnv *env, jobject obj, jboolean debug, jobject buf) {
    webview_t handle = webview_create(debug == JNI_TRUE ? 1 : 0, NULL);
    if (handle == NULL) {
        bridge_raise(&BRIDGE, env, WEBVIEW_ERROR_UNSPECIFIED);
        return 0;
    }

    void *ptr = (*env)->GetDirectBufferAddress(env, buf);
    const metrics_t *metrics = ptr;

    if (metrics->none[0])
        webview_set_size(handle, metrics->none[0], metrics->none[1], WEBVIEW_HINT_NONE);

    if (metrics->min[0])
        webview_set_size(handle, metrics->min[0], metrics->min[1], WEBVIEW_HINT_MIN);

    if (metrics->max[0])
        webview_set_size(handle, metrics->max[0], metrics->max[1], WEBVIEW_HINT_MAX);

    if (metrics->fixed[0])
        webview_set_size(handle, metrics->fixed[0], metrics->fixed[1], WEBVIEW_HINT_FIXED);

    return (jlong) handle;
}

JNIEXPORT jstring JNICALL Java_io_github_wasabithumb_jwebview_except_WebViewExceptionImpl_strerror(JNIEnv *env, jclass cls, jint code) {
    const char *str;
    switch (code) {
        case WEBVIEW_ERROR_MISSING_DEPENDENCY:
            str = "Missing dependency";
            break;
        case WEBVIEW_ERROR_CANCELED:
            str = "Operation canceled";
            break;
        case WEBVIEW_ERROR_INVALID_STATE:
            str = "Invalid state detected";
            break;
        case WEBVIEW_ERROR_INVALID_ARGUMENT:
            str = "Invalid argument";
            break;
        case WEBVIEW_ERROR_UNSPECIFIED:
            str = "An unspecified error occurred";
            break;
        case WEBVIEW_ERROR_OK:
            str = "OK";
            break;
        case WEBVIEW_ERROR_DUPLICATE:
            str = "Already exists";
            break;
        case WEBVIEW_ERROR_NOT_FOUND:
            str = "Not found";
            break;
        default:
            str = "Unknown error";
            break;
    }
    return bridge_string_decode(&BRIDGE, env, str);
}

JNIEXPORT void JNICALL Java_io_github_wasabithumb_jwebview_WebViewImpl_run0(JNIEnv *env, jobject obj) {
    webview_t wv = bridge_handle_of(&BRIDGE, env, obj);
    CHECK(webview_run(wv));
}

JNIEXPORT void JNICALL Java_io_github_wasabithumb_jwebview_WebViewImpl_terminate0(JNIEnv *env, jobject obj) {
    webview_t wv = bridge_handle_of(&BRIDGE, env, obj);
    CHECK(webview_terminate(wv));
}

JNIEXPORT void JNICALL Java_io_github_wasabithumb_jwebview_WebViewImpl_dispatch0(JNIEnv *env, jobject obj, jobject cb) {
    webview_t wv = bridge_handle_of(&BRIDGE, env, obj);
    dispatch_handler_queue(&DISPATCH_HANDLER, env, wv, obj, cb);
}

JNIEXPORT void JNICALL Java_io_github_wasabithumb_jwebview_WebViewImpl_setTitle0(JNIEnv *env, jobject obj, jstring str) {
    webview_t wv = bridge_handle_of(&BRIDGE, env, obj);
    const char *chars = bridge_string_encode(&BRIDGE, env, str);
    CHECK(webview_set_title(wv, chars));
}

JNIEXPORT void JNICALL Java_io_github_wasabithumb_jwebview_WebViewImpl_setSize0(JNIEnv *env, jobject obj, jint w, jint h, jint jh) {
    webview_hint_t hint;
    switch (jh) {
        case 0:
            hint = WEBVIEW_HINT_NONE;
            break;
        case 1:
            hint = WEBVIEW_HINT_MIN;
            break;
        case 2:
            hint = WEBVIEW_HINT_MAX;
            break;
        case 3:
            hint = WEBVIEW_HINT_FIXED;
            break;
        default:
            bridge_raise(&BRIDGE, env, WEBVIEW_ERROR_INVALID_ARGUMENT);
            return;
    }
    webview_t wv = bridge_handle_of(&BRIDGE, env, obj);
    CHECK(webview_set_size(wv, w, h, hint));
}

JNIEXPORT void JNICALL Java_io_github_wasabithumb_jwebview_WebViewImpl_navigate0(JNIEnv *env, jobject obj, jstring str) {
    webview_t wv = bridge_handle_of(&BRIDGE, env, obj);
    const char *chars = bridge_string_encode(&BRIDGE, env, str);
    CHECK(webview_navigate(wv, chars));
}

JNIEXPORT void JNICALL Java_io_github_wasabithumb_jwebview_WebViewImpl_setHtml0(JNIEnv *env, jobject obj, jstring str) {
    webview_t wv = bridge_handle_of(&BRIDGE, env, obj);
    const char *chars = bridge_string_encode(&BRIDGE, env, str);
    CHECK(webview_set_html(wv, chars));
}

JNIEXPORT void JNICALL Java_io_github_wasabithumb_jwebview_WebViewImpl_init0(JNIEnv *env, jobject obj, jstring str) {
    webview_t wv = bridge_handle_of(&BRIDGE, env, obj);
    const char *chars = bridge_string_encode(&BRIDGE, env, str);
    CHECK(webview_init(wv, chars));
}

JNIEXPORT void JNICALL Java_io_github_wasabithumb_jwebview_WebViewImpl_eval0(JNIEnv *env, jobject obj, jstring str) {
    webview_t wv = bridge_handle_of(&BRIDGE, env, obj);
    const char *chars = bridge_string_encode(&BRIDGE, env, str);
    CHECK(webview_eval(wv, chars));
}

JNIEXPORT jint JNICALL Java_io_github_wasabithumb_jwebview_WebViewImpl_bindDataSize(JNIEnv *env, jobject obj) {
    return sizeof(bind_registration_t);
}

JNIEXPORT void JNICALL Java_io_github_wasabithumb_jwebview_WebViewImpl_bind0(JNIEnv *env, jobject obj, jstring name, jobject cb, jobject buf) {
    webview_t wv = bridge_handle_of(&BRIDGE, env, obj);
    const char *chars = bridge_string_encode(&BRIDGE, env, name);
    void *ptr = (*env)->GetDirectBufferAddress(env, buf);
    CHECK(bind_handler_register(&BIND_HANDLER, env, ptr, wv, chars, cb));
}

JNIEXPORT void JNICALL Java_io_github_wasabithumb_jwebview_WebViewImpl_unbind0(JNIEnv *env, jobject obj, jstring name, jobject buf) {
    const char *chars = bridge_string_encode(&BRIDGE, env, name);
    void *ptr = (*env)->GetDirectBufferAddress(env, buf);
    CHECK(bind_handler_unregister(ptr, env, chars));
}

JNIEXPORT void JNICALL Java_io_github_wasabithumb_jwebview_WebViewImpl_destroy(JNIEnv *env, jobject obj) {
    webview_t wv = bridge_handle_of(&BRIDGE, env, obj);
    webview_error_t error = webview_destroy(wv);
    if (WEBVIEW_FAILED(error)) bridge_raise(&BRIDGE, env, error);
}
