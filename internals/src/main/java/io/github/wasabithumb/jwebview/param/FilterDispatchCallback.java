package io.github.wasabithumb.jwebview.param;

import io.github.wasabithumb.jwebview.WebView;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public abstract class FilterDispatchCallback implements DispatchCallback {

    protected final DispatchCallback backing;

    protected FilterDispatchCallback(@NotNull DispatchCallback backing) {
        this.backing = backing;
    }

    //

    @Override
    public void invoke(@NotNull WebView webView) throws Exception {
        this.backing.invoke(webView);
    }

}
