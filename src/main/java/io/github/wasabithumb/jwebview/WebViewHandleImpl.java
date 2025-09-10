package io.github.wasabithumb.jwebview;

import io.github.wasabithumb.jwebview.param.DispatchCallback;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
final class WebViewHandleImpl implements WebViewHandle {

    private final WebView wv;
    private final Thread thread;

    public WebViewHandleImpl(@NotNull WebView wv, @NotNull Thread thread) {
        this.wv = wv;
        this.thread = thread;
    }

    //


    @Override
    public boolean isOpen() {
        return this.wv.isOpen();
    }

    @Override
    public void use(@NotNull DispatchCallback fn) {
        this.wv.dispatch(fn);
    }

    @Override
    public void join() throws InterruptedException {
        this.thread.join();
    }

}
