package io.github.wasabithumb.jwebview;

import io.github.wasabithumb.jwebview.option.JWebViewOptions;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Entry point for JWebView.
 * @see #jWebView()
 */
@ApiStatus.NonExtendable
public interface JWebView {

    @Contract("_ -> new")
    static @NotNull JWebView jWebView(@NotNull JWebViewOptions options) {
        return JWebViewProvider.get().newInstance(options);
    }

    @Contract("_ -> new")
    static @NotNull JWebView jWebView(@NotNull JWebViewOptions.Builder optionsBuilder) {
        return jWebView(optionsBuilder.build());
    }

    @Contract("-> new")
    static @NotNull JWebView jWebView() {
        return jWebView(JWebViewOptions.defaults());
    }

    //

    /**
     * Reports the running version
     * of <a href="https://github.com/webview/webview/">webview</a>.
     */
    @NotNull String version();

    /**
     * Creates a new web view
     * window.
     */
    @NotNull WebView create();

    /**
     * Creates a web view managed
     * exclusively by a worker thread.
     * @param configure Task to execute before starting the worker.
     *                  Can be used to set up scripts, page content, etc.
     */
    @NotNull WebViewHandle createAsync(@Nullable Consumer<WebView> configure);

    /**
     * Creates a web view managed
     * exclusively by a worker thread.
     */
    default @NotNull WebViewHandle createAsync() {
        return this.createAsync(null);
    }

}
