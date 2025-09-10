#include <stdlib.h>
#include "dispatch.h"

//

typedef struct dispatch_registration {
    dispatch_handler_t *handler;
    jobject view_ref;
    jobject callback_ref;
} dispatch_registration_t;

static void dispatch_callback(webview_t __attribute__((unused)) wv, void *arg) {
    const dispatch_registration_t *registration = arg;
    JavaVM *vm = registration->handler->vm;
    jmethodID m_invoke = registration->handler->m_invoke;
    jobject view = registration->view_ref;
    jobject cb = registration->callback_ref;
    jvalue jv;
    jv.l = view;

    JNIEnv *env;
    (*vm)->AttachCurrentThread(vm, (void **) &env, NULL);
    (*env)->CallVoidMethodA(env, cb, m_invoke, &jv);
    (*env)->DeleteGlobalRef(env, cb);
    (*env)->DeleteGlobalRef(env, view);
    (*vm)->DetachCurrentThread(vm);
    free(arg);
}

//

int dispatch_handler_init(dispatch_handler_t *handler, JNIEnv *env) {
    JavaVM *vm;
    (*env)->GetJavaVM(env, &vm);

    jclass c_dispatch_callback = (*env)->FindClass(env, "io/github/wasabithumb/jwebview/param/DispatchCallback");
    if (c_dispatch_callback == NULL) return 1;

    jmethodID m_invoke = (*env)->GetMethodID(env, c_dispatch_callback, "invoke", "(Lio/github/wasabithumb/jwebview/WebView;)V");
    if (m_invoke == NULL) return 1;

    handler->vm = vm;
    handler->c_dispatch_callback = c_dispatch_callback;
    handler->m_invoke = m_invoke;
    return 0;
}

void dispatch_handler_queue(dispatch_handler_t *handler, JNIEnv *env, webview_t wv, jobject obj, jobject callback) {
    jobject view_ref = (*env)->NewGlobalRef(env, obj);
    if (view_ref == NULL) return;
    jobject callback_ref = (*env)->NewGlobalRef(env, callback);
    if (callback_ref == NULL) return;
    dispatch_registration_t *registration = malloc(sizeof(struct dispatch_registration));
    if (registration == NULL) return;

    registration->handler = handler;
    registration->view_ref = view_ref;
    registration->callback_ref = callback_ref;
    webview_dispatch(wv, dispatch_callback, registration);
}
