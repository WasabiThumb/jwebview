package io.github.wasabithumb.jwebview;

import io.github.wasabithumb.jwebview.param.*;
import io.github.wasabithumb.jwebview.util.Direct;
import io.github.wasabithumb.jwebview.util.RemoteObject;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@ApiStatus.Internal
final class WebViewImpl implements WebView, RemoteObject {

    private final long handle;
    private final Logger logger;
    private final Map<String, @Direct ByteBuffer> bindings;
    private volatile boolean open;

    WebViewImpl(long handle, @NotNull Logger logger) {
        this.handle = handle;
        this.logger = logger;
        this.bindings = Collections.synchronizedMap(new HashMap<>());
        this.open = true;
    }

    //

    @Override
    public boolean isOpen() {
        return this.open;
    }

    @Override
    public long handle() {
        return this.handle;
    }

    @Override
    public void run() {
        this.checkOpen();
        this.run0();
    }

    @Override
    public void terminate() {
        this.checkOpen();
        this.terminate0();
    }

    @Override
    public void dispatch(@NotNull DispatchCallback fn) {
        this.checkOpen();
        this.checkNonNull(fn, "fn");
        this.dispatch0(LoggingDispatchCallback.of(fn, this.logger));
    }

    @Override
    public void setTitle(@NotNull String title) {
        this.checkOpen();
        this.checkNonNull(title, "title");
        this.setTitle0(title);
    }

    @Override
    public void setSize(int width, int height, @NotNull SizeHint hint) {
        this.checkOpen();
        this.checkNonNull(hint, "hint");
        if (width < 1 || height < 1)
            throw new IllegalArgumentException("Invalid dimensions (" + width + " x " + height + ")");
        this.setSize0(width, height, hint.value());
    }

    @Override
    public void navigate(@NotNull String url) {
        this.checkOpen();
        this.checkNonNull(url, "url");
        this.navigate0(url);
    }

    @Override
    public void setHtml(@NotNull String html) {
        this.checkOpen();
        this.checkNonNull(html, "html");
        this.setHtml0(html);
    }

    @Override
    public void init(@NotNull String js) {
        this.checkOpen();
        this.checkNonNull(js, "js");
        this.init0(js);
    }

    @Override
    public void eval(@NotNull String js) {
        this.checkOpen();
        this.checkNonNull(js, "js");
        this.eval0(js);
    }

    @Override
    public void bind(@NotNull String name, @NotNull BindCallback fn) {
        this.checkOpen();
        this.checkNonNull(name, "name");
        this.checkNonNull(fn, "fn");
        fn = LoggingBindCallback.of(fn, this.logger);

        synchronized (this.bindings) {
            if (this.bindings.containsKey(name))
                throw new IllegalStateException("Binding with name \"" + name + "\" already exists");

            ByteBuffer buf = ByteBuffer.allocateDirect(this.bindDataSize());
            this.bind0(name, fn, buf);
            this.bindings.put(name, buf);
        }
    }

    @Override
    public void unbind(@NotNull String name) {
        this.checkOpen();
        this.checkNonNull(name, "name");

        synchronized (this.bindings) {
            ByteBuffer buf = this.bindings.remove(name);
            if (buf == null) throw new IllegalArgumentException("Binding with name \"" + name + "\" does not exist");
            this.unbind0(name, buf);
        }
    }

    @Override
    public void close() {
        this.checkOpen();
        this.open = false;
        this.destroy();
        this.bindings.clear();
    }

    @Override
    public int hashCode() {
        return Long.hashCode(this.handle);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof WebViewImpl)) return false;
        return this.handle == ((WebViewImpl) obj).handle;
    }

    private void checkOpen() {
        if (!this.open) throw new IllegalStateException("Cannot use WebView after closing");
    }

    private void checkNonNull(@Nullable Object parameter, @NotNull String ref) {
        if (parameter != null) return;
        throw new NullPointerException(ref + " may not be null");
    }

    private native void run0();

    private native void terminate0();

    private native void dispatch0(@NotNull DispatchCallback fn);

    private native void setTitle0(@NotNull String title);

    private native void setSize0(int width, int height, int hint);

    private native void navigate0(@NotNull String url);

    private native void setHtml0(@NotNull String html);

    private native void init0(@NotNull String url);

    private native void eval0(@NotNull String url);

    private native int bindDataSize();

    private native void bind0(@NotNull String name, @NotNull BindCallback fn, @NotNull @Direct ByteBuffer buf);

    private native void unbind0(@NotNull String name, @NotNull @Direct ByteBuffer buf);

    private native void destroy();

}
