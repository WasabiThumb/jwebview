package io.github.wasabithumb.jwebview.bridge;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

@ApiStatus.Internal
final class ForwardingGsonBindCallback implements GsonBindCallback {

    static final Gson GSON = (new GsonBuilder())
            .serializeNulls()
            .serializeSpecialFloatingPointValues()
            .create();

    //

    private final Function<JsonArray, JsonElement> fn;

    ForwardingGsonBindCallback(@NotNull Function<JsonArray, JsonElement> fn) {
        this.fn = fn;
    }

    //

    @Override
    public @Nullable JsonElement invoke(@NotNull JsonArray array) {
        return this.fn.apply(array);
    }

}
