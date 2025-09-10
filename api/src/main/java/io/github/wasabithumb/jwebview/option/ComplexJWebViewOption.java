package io.github.wasabithumb.jwebview.option;

import org.jetbrains.annotations.NotNull;

final class ComplexJWebViewOption<T> extends AbstractJWebViewOption<T> {

    static <R> @NotNull ComplexJWebViewOption<R> of(
            @NotNull String name,
            @NotNull Class<R> valueClass,
            @NotNull R defaultValue
    ) {
        return new ComplexJWebViewOption<>(name, valueClass, defaultValue);
    }

    //

    private final Class<T> valueClass;

    private ComplexJWebViewOption(
            @NotNull String name,
            @NotNull Class<T> valueClass,
            @NotNull T defaultValue
    ) {
        super(name, defaultValue);
        this.valueClass = valueClass;
    }

    //

    @Override
    public @NotNull Class<T> valueClass() {
        return this.valueClass;
    }

}
