package io.github.wasabithumb.jwebview.param;

import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.OverrideOnly
@FunctionalInterface
public interface BindCallback {

    /**
     * Called when the JS runtime invokes
     * the binding.
     * @param arguments A JSON array containing the arguments passed.
     * @return A JSON value or an empty string or null for {@code undefined}.
     */
    @Nullable @Language("JSON") String invoke(@NotNull @Language("JSON") String arguments) throws Exception;

}
