package io.github.wasabithumb.jwebview.except;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Supertype of exceptions raised by
 * the webview library.
 */
public abstract class WebViewException extends RuntimeException {

    protected final int code;
    protected final String rawMessage;

    protected WebViewException(int code, @Nullable String rawMessage) {
        super("Error " + code + " (" + rawMessage + ")");
        this.code = code;
        this.rawMessage = rawMessage;
    }

    //

    public int getCode() {
        return this.code;
    }

    public @Nullable String getRawMessage() {
        return this.rawMessage;
    }

    @Override
    public @NotNull String getMessage() {
        return super.getMessage();
    }

}
