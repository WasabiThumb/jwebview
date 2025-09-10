package io.github.wasabithumb.jwebview.option;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
abstract class AbstractJWebViewOption<T> implements JWebViewOption<T> {

    private static int ORDINAL_HEAD = 0;

    private static synchronized int nextOrdinal() {
        return ORDINAL_HEAD++;
    }

    //

    private final int ordinal;
    private final String name;
    private final T defaultValue;

    protected AbstractJWebViewOption(
            @NotNull String name,
            @NotNull T defaultValue
    ) {
        this.ordinal = nextOrdinal();
        this.name = name;
        this.defaultValue = defaultValue;
    }

    //

    @Override
    public int ordinal() {
        return this.ordinal;
    }

    @Override
    public @NotNull String name() {
        return this.name;
    }

    @Override
    public @NotNull T defaultValue() {
        return this.defaultValue;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(this.ordinal);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof JWebViewOption<?>)) return false;
        return this.ordinal == ((JWebViewOption<?>) obj).ordinal();
    }

    @Override
    public @NotNull String toString() {
        return this.name;
    }

}
