package io.github.wasabithumb.jwebview.option;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A collection of configuration options for
 * {@link io.github.wasabithumb.jwebview.JWebView JWebView}.
 */
public final class JWebViewOptions {

    private static final int COUNT = JWebViewOption.values().length;
    private static final JWebViewOptions EMPTY = new JWebViewOptions(0, new Object[0]);

    public static @NotNull JWebViewOptions defaults() {
        return EMPTY;
    }

    @Contract("-> new")
    public static @NotNull Builder builder() {
        return new Builder();
    }

    //

    private final int offset;
    private final Object[] values;

    private JWebViewOptions(
            int offset,
            @Nullable Object @NotNull [] values
    ) {
        this.offset = offset;
        this.values = values;
    }

    //

    public <T> @NotNull T get(@NotNull JWebViewOption<T> option) {
        int index = option.ordinal() - this.offset;
        if (index < 0 || index >= this.values.length) return option.defaultValue();
        Object set = this.values[index];
        if (set == null) return option.defaultValue();
        return option.valueClass().cast(set);
    }

    public boolean get(@NotNull JWebViewOption.Bool option) {
        return this.get((JWebViewOption<Boolean>) option);
    }

    //

    public static final class Builder {

        private final Object[] values = new Object[COUNT];
        private int min = -1;
        private int max = -1;

        Builder() { }

        //

        @Contract("_, null -> fail; _, _ -> this")
        public <T> @NotNull Builder set(@NotNull JWebViewOption<T> option, T value) {
            int index = option.ordinal();
            if (index < 0 || index >= COUNT) throw new IllegalStateException("Illegal option ordinal " + index);
            if (value == null) throw new NullPointerException("Cannot set option " + option.name() + " to null");

            this.values[index] = value;
            if (this.min == -1) {
                this.min = index;
                this.max = index + 1;
            } else if (index < this.min) {
                this.min = index;
            } else if (index >= this.max) {
                this.max = index + 1;
            }
            return this;
        }

        @Contract("-> new")
        public @NotNull JWebViewOptions build() {
            int len = this.max - this.min;
            Object[] cpy = new Object[len];
            if (len != 0) System.arraycopy(this.values, this.min, cpy, 0, len);
            return new JWebViewOptions(this.min, cpy);
        }

    }

}
