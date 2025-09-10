package io.github.wasabithumb.jwebview.param;

import io.github.wasabithumb.jwebview.WebView;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;
import java.util.logging.Logger;

@ApiStatus.Internal
public final class LoggingDispatchCallback extends FilterDispatchCallback {

    public static @NotNull LoggingDispatchCallback of(@NotNull DispatchCallback backing, @NotNull Logger logger) {
        return new LoggingDispatchCallback(backing, logger);
    }

    //

    private final Logger logger;

    private LoggingDispatchCallback(@NotNull DispatchCallback backing, @NotNull Logger logger) {
        super(backing);
        this.logger = logger;
    }

    //


    @Override
    public void invoke(@NotNull WebView webView) {
        try {
            super.invoke(webView);
        } catch (Exception e) {
            this.logger.log(Level.WARNING, "Error in dispatch callback", e);
        }
    }

}
