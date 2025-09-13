#include <stdlib.h>
#include "bind.h"

//

void bind_callback(const char *id, const char *req, void *arg) {
    const bind_registration_t *registration = arg;
    JavaVM *vm = registration->handler->vm;
    jmethodID m_invoke = registration->handler->m_invoke;
    bridge_t *bridge = registration->handler->bridge;
    jobject cb = registration->callback_ref;

    JNIEnv *env;
    (*vm)->AttachCurrentThread(vm, (void **) &env, NULL);

    jvalue jv;
    jv.l = bridge_string_decode(bridge, env, req);
    jstring ret = (*env)->CallObjectMethodA(env, cb, m_invoke, &jv);

    jthrowable ex = (*env)->ExceptionOccurred(env);
    if (ex != NULL) {
        (*env)->ExceptionClear(env);
        const char *msg = bridge_describe(bridge, env, ex);
        webview_return(registration->wv, id, 1, msg);
    } else {
        const char *result;
        if (ret == NULL) {
            result = "";
        } else {
            result = bridge_string_encode(bridge, env, ret);
        }
        webview_return(registration->wv, id, 0, result);
    }

    (*vm)->DetachCurrentThread(vm);
}

//

int bind_handler_init(bind_handler_t *handler, JNIEnv *env, bridge_t *bridge) {
    JavaVM *vm;
    (*env)->GetJavaVM(env, &vm);

    jclass c_bind_callback = (*env)->FindClass(env, "io/github/wasabithumb/jwebview/param/BindCallback");
    if (c_bind_callback == NULL) return 1;

    jmethodID m_invoke = (*env)->GetMethodID(env, c_bind_callback, "invoke", "(Ljava/lang/String;)Ljava/lang/String;");
    if (m_invoke == NULL) return 1;

    handler->vm = vm;
    handler->bridge = bridge;
    handler->c_bind_callback;
    handler->m_invoke = m_invoke;
    return 0;
}

webview_error_t bind_handler_register(bind_handler_t *handler, JNIEnv *env, bind_registration_t *registration, webview_t wv, const char *name, jobject callback) {
    jobject ref = (*env)->NewGlobalRef(env, callback);
    if (ref == NULL) return WEBVIEW_ERROR_UNSPECIFIED;

    registration->handler = handler;
    registration->callback_ref = ref;
    registration->wv = wv;

    return webview_bind(wv, name, bind_callback, registration);
}

webview_error_t bind_handler_unregister(bind_registration_t *registration, JNIEnv *env, const char *name) {
    webview_t wv = registration->wv;
    (*env)->DeleteGlobalRef(env, registration->callback_ref);
    return webview_unbind(wv, name);
}
