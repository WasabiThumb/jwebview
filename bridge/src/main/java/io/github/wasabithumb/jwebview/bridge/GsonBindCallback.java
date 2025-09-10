package io.github.wasabithumb.jwebview.bridge;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import io.github.wasabithumb.jwebview.param.BindCallback;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

import static io.github.wasabithumb.jwebview.bridge.ForwardingGsonBindCallback.GSON;

/**
 * A {@link BindCallback} that uses Gson for basic
 * syntax parsing.
 */
@ApiStatus.OverrideOnly
@FunctionalInterface
public interface GsonBindCallback extends BindCallback {

    @Contract("_ -> new")
    static @NotNull GsonBindCallback of(@NotNull Function<JsonArray, JsonElement> fn) {
        return new ForwardingGsonBindCallback(fn);
    }

    @ApiStatus.Experimental
    @Contract("_, _ -> new")
    static @NotNull GsonBindCallback reflect(@NotNull Object object, @NotNull String methodName) {
        return new ReflectGsonBindCallback(object, methodName);
    }

    //

    @Nullable JsonElement invoke(@NotNull JsonArray array) throws Exception;

    @Override
    default @Nullable String invoke(@NotNull String arguments) throws Exception {
        JsonArray array = GSON.fromJson(arguments, JsonArray.class);
        JsonElement element = this.invoke(array);
        if (element == null) return null;
        return GSON.toJson(element);
    }

}
