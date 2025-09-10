package io.github.wasabithumb.jwebview.except;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public final class WebViewExceptionImpl extends WebViewException {

    private static native @Nullable String strerror(int code);

    //

    public WebViewExceptionImpl(int code) {
        super(code, strerror(code));
    }

}
