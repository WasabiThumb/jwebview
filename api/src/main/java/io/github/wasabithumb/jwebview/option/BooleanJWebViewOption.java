package io.github.wasabithumb.jwebview.option;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
final class BooleanJWebViewOption extends AbstractJWebViewOption<Boolean> implements JWebViewOption.Bool {

    @Contract("_, _ -> new")
    static @NotNull JWebViewOption.Bool of(@NotNull String name, boolean defaultValue) {
        return new BooleanJWebViewOption(name, defaultValue);
    }

    //

    private BooleanJWebViewOption(
            @NotNull String name,
            boolean defaultValue
    ) {
        super(name, defaultValue);
    }

}
