package io.github.wasabithumb.jwebview;

import io.github.wasabithumb.jwebview.param.BindCallback;
import io.github.wasabithumb.jwebview.param.DispatchCallback;
import io.github.wasabithumb.jwebview.param.SizeHint;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.NotNull;

/**
 * A web view window created by
 * JWebView.
 */
@ApiStatus.NonExtendable
public interface WebView extends AutoCloseable {

    /**
     * Returns true if the window is open
     * (has not been closed by the user or programmatically)
     */
    boolean isOpen();

    /**
     * Runs the main loop until
     * it's terminated.
     */
    @Blocking
    void run();

    /**
     * Stops the main loop.
     * Not to be confused with {@link #close()}.
     */
    void terminate();

    /**
     * Schedules a task to run on the thread with
     * the event loop.
     */
    void dispatch(@NotNull DispatchCallback fn);

    /**
     * Updates the title of the window.
     */
    void setTitle(@NotNull String title);

    /**
     * Updates the size of the window
     * as indicated by the provided {@link SizeHint hint}.
     */
    void setSize(int width, int height, @NotNull SizeHint hint);

    /**
     * Updates the size of the window.
     */
    default void setSize(int width, int height) {
        this.setSize(width, height, SizeHint.NONE);
    }

    /**
     * Navigates to the specified URL.
     */
    void navigate(@NotNull String url);

    /**
     * Updates the content of the document with
     * the given HTML string.
     */
    void setHtml(@NotNull @Language("HTML") String html);

    /**
     * Sets a JavaScript expression to evaluate when
     * before the page loads.
     */
    void init(@NotNull @Language("JS") String js);

    /**
     * Evaluates a JavaScript expression. For
     * more complex actions, use {@link #bind(String, BindCallback)}.
     */
    void eval(@NotNull @Language("JS") String js);

    /**
     * Defines a global function in the JavaScript context
     * which executes the given callback. This callback will receive
     * the array of arguments in JSON format, and should return a JSON
     * string containing the return value.
     * @see #unbind(String)
     */
    void bind(@NotNull String name, @NotNull BindCallback fn);

    /**
     * Deletes a global function defined with {@link #bind(String, BindCallback)}
     * by its name.
     */
    void unbind(@NotNull String name);

    /**
     * Destroys the window and
     * frees its associated resources.
     */
    @Override
    void close();

}
