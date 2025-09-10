package io.github.wasabithumb.jwebview.param;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;
import java.util.logging.Logger;

@ApiStatus.Internal
public final class LoggingBindCallback extends FilterBindCallback {

    public static @NotNull LoggingBindCallback of(@NotNull BindCallback backing, @NotNull Logger logger) {
        return new LoggingBindCallback(backing, logger);
    }

    //

    private final Logger logger;

    private LoggingBindCallback(@NotNull BindCallback backing, @NotNull Logger logger) {
        super(backing);
        this.logger = logger;
    }

    //


    @Override
    public @Nullable String invoke(@NotNull String arguments) throws Exception {
        try {
            return super.invoke(arguments);
        } catch (Exception e) {
            this.logger.log(Level.WARNING, "Error in bind callback", e);
            throw e;
        }
    }

}
