package io.github.wasabithumb.jwebview.param;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public abstract class FilterBindCallback implements BindCallback {

    protected final BindCallback backing;

    protected FilterBindCallback(@NotNull BindCallback backing) {
        this.backing = backing;
    }

    //

    @Override
    public @Nullable String invoke(@NotNull String arguments) throws Exception {
        return this.backing.invoke(arguments);
    }

}
