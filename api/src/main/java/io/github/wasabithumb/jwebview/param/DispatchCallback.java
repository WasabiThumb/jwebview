package io.github.wasabithumb.jwebview.param;

import io.github.wasabithumb.jwebview.WebView;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * A callback for usage
 * with {@link WebView#dispatch(DispatchCallback)}.
 */
@ApiStatus.OverrideOnly
@FunctionalInterface
public interface DispatchCallback {

    /**
     * Called on the thread with the event loop.
     */
    void invoke(@NotNull WebView webView) throws Exception;

}
