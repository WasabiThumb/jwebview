package io.github.wasabithumb.jwebview;

import io.github.wasabithumb.jwebview.option.JWebViewOptions;
import io.github.wasabithumb.jwebview.util.Natives;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

@SuppressWarnings("unused")
@ApiStatus.Internal
final class JWebViewProviderImpl extends JWebViewProvider {

    private static native int setup();

    //

    /** Called by JWebViewProvider */
    public JWebViewProviderImpl() throws IOException {
        Natives.load();
        int stat = setup();
        if (setup() != 0) {
            throw new IllegalStateException("Failed to setup library (error code " + stat + ")");
        }
    }

    //

    @Override
    @Contract("null -> fail")
    public @NotNull JWebView newInstance(JWebViewOptions options) {
        if (options == null) throw new NullPointerException("Options may not be null");
        return new JWebViewImpl(options);
    }

}
