package io.github.wasabithumb.jwebview.option;

import static io.github.wasabithumb.jwebview.option.BooleanJWebViewOption.of;
import static io.github.wasabithumb.jwebview.option.ComplexJWebViewOption.of;

import io.github.wasabithumb.jwebview.param.Metrics;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Logger;

/**
 * A configuration option for
 * {@link io.github.wasabithumb.jwebview.JWebView JWebView},
 */
@ApiStatus.NonExtendable
public interface JWebViewOption<T> {

    /**
     * If set to true, developer tools are enabled
     * in created windows.
     */
    Bool DEVTOOLS = of("DEVTOOLS", false);

    /**
     * The logger to receive messages
     * related to usage of JWebView.
     */
    JWebViewOption<Logger> LOGGER = of("LOGGER", Logger.class, Logger.getAnonymousLogger());

    /**
     * The {@link ThreadFactory} used to create
     * handler threads.
     */
    JWebViewOption<ThreadFactory> THREAD_FACTORY = of("THREAD_FACTORY", ThreadFactory.class, Executors.defaultThreadFactory());

    /**
     * The initial metrics of newly created
     * windows.
     */
    JWebViewOption<Metrics> INITIAL_METRICS = of("INITIAL_METRICS", Metrics.class, Metrics.defaults());

    /**
     * Provides a newly created array containing all
     * defined option constants sorted by {@link #ordinal()}.
     */
    static @NotNull JWebViewOption<?> @NotNull[] values() {
        final Field[] fields = JWebViewOption.class.getDeclaredFields();
        final int capacity = fields.length;
        JWebViewOption<?>[] ret = new JWebViewOption[capacity];
        int head = 0;

        // Iterate over fields
        for (int i = 0; i < capacity; i++) {
            Field next = fields[i];
            if (!Modifier.isStatic(next.getModifiers())) continue;
            if (!JWebViewOption.class.isAssignableFrom(next.getType())) continue;

            JWebViewOption<?> value;
            try {
                value = (JWebViewOption<?>) next.get(null);
            } catch (ReflectiveOperationException e) {
                throw new AssertionError(
                        "Failed to read constant \"" + next.getName() + "\"",
                        e
                );
            }
            ret[head++] = value;
        }

        // Shrink (precaution)
        if (head != capacity) {
            JWebViewOption<?>[] cpy = new JWebViewOption<?>[head];
            System.arraycopy(ret, 0, cpy, 0, head);
            ret = cpy;
        }

        // Sort (precaution)
        int i = 0;
        while (i < (head - 1)) {
            JWebViewOption<?> next = ret[i];
            int j = next.ordinal();
            if (j == i) {
                i++;
            } else {
                ret[i] = ret[j];
                ret[j] = next;
            }
        }

        return ret;
    }

    //

    @ApiStatus.Internal
    int ordinal();

    /**
     * Name of this option constant
     */
    @NotNull String name();

    /**
     * Value class of this option constant
     */
    @NotNull Class<T> valueClass();

    /**
     * Default value of this option constant
     */
    @NotNull T defaultValue();

    //

    @ApiStatus.NonExtendable
    interface Bool extends JWebViewOption<Boolean> {

        @Override
        default @NotNull Class<Boolean> valueClass() {
            return Boolean.class;
        }

    }

}
