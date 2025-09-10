package io.github.wasabithumb.jwebview;

import io.github.wasabithumb.jwebview.option.JWebViewOptions;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

@ApiStatus.Internal
abstract class JWebViewProvider {

    private static boolean INIT = false;
    private static JWebViewProvider PROVIDER = null;

    static synchronized @NotNull JWebViewProvider get() {
        if (!INIT) {
            Class<?> cls;
            try {
                cls = Class.forName("io.github.wasabithumb.jwebview.JWebViewProviderImpl");
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("Cannot find implementation (is the environment misconfigured?)", e);
            }

            Object impl;
            try {
                Constructor<?> con = cls.getDeclaredConstructor();
                con.setAccessible(true);
                impl = con.newInstance();
            } catch (InvocationTargetException | ExceptionInInitializerError e) {
                Throwable cause = e.getCause();
                if (cause == null) cause = e;
                if (cause instanceof RuntimeException) throw (RuntimeException) cause;
                throw new IllegalStateException("Failed to initialize JWebView", e);
            } catch (ReflectiveOperationException | SecurityException e) {
                throw new AssertionError("Unexpected reflection error", e);
            }

            PROVIDER = (JWebViewProvider) impl;
            INIT = true;
        }

        return PROVIDER;
    }

    //

    @Contract("null -> fail; _ -> new")
    public abstract @NotNull JWebView newInstance(JWebViewOptions options);

}
