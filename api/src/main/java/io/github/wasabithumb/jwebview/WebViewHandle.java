package io.github.wasabithumb.jwebview;

import io.github.wasabithumb.jwebview.param.DispatchCallback;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.NotNull;

/**
 * Thread-safe access to an asynchronously
 * polling {@link WebView}.
 */
@ApiStatus.NonExtendable
public interface WebViewHandle {

    /**
     * Returns true if the {@link WebView} is
     * {@link WebView#isOpen() open}.
     */
    boolean isOpen();

    /**
     * Provides thread-safe access to the
     * {@link WebView}.
     */
    void use(@NotNull DispatchCallback fn);

    /**
     * Waits for the window to close.
     * @throws InterruptedException Thread was interrupted
     */
    @Blocking
    void join() throws InterruptedException;

    /**
     * Signals that the window
     * should close and waits for it
     * to do so.
     */
    default void close() {
        if (this.isOpen()) this.use(WebView::terminate);
        try {
            this.join();
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }

}
