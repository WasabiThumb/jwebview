package io.github.wasabithumb.jwebview;

import io.github.wasabithumb.jwebview.option.JWebViewOption;
import io.github.wasabithumb.jwebview.option.JWebViewOptions;
import io.github.wasabithumb.jwebview.util.Direct;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;
import java.util.concurrent.ThreadFactory;
import java.util.function.Consumer;
import java.util.logging.Level;

@ApiStatus.Internal
final class JWebViewImpl implements JWebView {

    private final JWebViewOptions options;

    JWebViewImpl(@NotNull JWebViewOptions options) {
        this.options = options;
    }

    //

    @Override
    public native @NotNull String version();

    @Override
    public @NotNull WebView create() {
        long handle = this.create0(
                this.options.get(JWebViewOption.DEVTOOLS),
                this.options.get(JWebViewOption.INITIAL_METRICS).buffer()
        );
        return new WebViewImpl(handle, this.options.get(JWebViewOption.LOGGER));
    }

    @Override
    public @NotNull WebViewHandle createAsync(@Nullable Consumer<WebView> fn) {
        final ThreadFactory factory = this.options.get(JWebViewOption.THREAD_FACTORY);
        final Runner runner = new Runner(this, fn);
        final Thread thread = factory.newThread(runner);
        thread.start();

        final WebView wv = runner.acquire();
        return new WebViewHandleImpl(wv, thread);
    }

    private native long create0(boolean debug, @NotNull @Direct ByteBuffer metrics);

    //

    private static final class Runner implements Runnable {

        private final Object mutex;
        private final JWebViewImpl jwv;
        private final Consumer<WebView> configure;
        private WebView instance;

        Runner(@NotNull JWebViewImpl jwv, @Nullable Consumer<WebView> configure) {
            this.mutex = new Object();
            this.jwv = jwv;
            this.configure = configure;
            this.instance = null;
        }

        //

        public @NotNull WebView acquire() {
            WebView wv;
            while (true) {
                synchronized (this.mutex) {
                    wv = this.instance;
                    if (wv == null) {
                        try {
                            this.mutex.wait();
                        } catch (InterruptedException ignored) {
                            Thread.currentThread().interrupt();
                        }
                    } else {
                        return wv;
                    }
                }
            }
        }

        @Override
        public void run() {
            try (WebView wv = this.jwv.create()) {
                if (this.configure != null) this.configure.accept(wv);
                synchronized (this.mutex) {
                    this.instance = wv;
                    this.mutex.notify();
                }
                wv.run();
            } catch (Exception e) {
                this.jwv.options.get(JWebViewOption.LOGGER)
                        .log(Level.WARNING, "Error in async WebView runner", e);
            }
        }

    }

}
