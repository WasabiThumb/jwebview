package io.github.wasabithumb.jwebview.param;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Window metrics
 */
public final class Metrics {

    public static @NotNull Metrics defaults() {
        return builder().build();
    }

    @Contract("-> new")
    public static @NotNull Builder builder() {
        return new Builder();
    }

    @Contract("_, _ -> new")
    public static @NotNull Metrics of(int width, int height) {
        return builder()
                .set(SizeHint.NONE, width, height)
                .build();
    }

    //

    private final ByteBuffer buffer;

    Metrics(@NotNull ByteBuffer buffer) {
        this.buffer = buffer;
    }

    //

    @ApiStatus.Internal
    public @NotNull ByteBuffer buffer() {
        return this.buffer.asReadOnlyBuffer();
    }

    public boolean isSet(@NotNull SizeHint hint) {
        return this.buffer.getInt(hint.value() << 3) != 0;
    }

    public int width(@NotNull SizeHint hint) throws IllegalArgumentException {
        final int v = this.buffer.get(hint.value() << 3);
        if (v == 0) throw new IllegalArgumentException("Dimensions are not set for " + hint.name());
        return v;
    }

    public int height(@NotNull SizeHint hint) throws IllegalArgumentException {
        final int v = this.buffer.get((hint.value() << 3) | 4);
        if (v == 0) throw new IllegalArgumentException("Dimensions are not set for " + hint.name());
        return v;
    }

    //

    public static final class Builder {

        private final ByteBuffer buffer = ByteBuffer.allocateDirect(32)
                .order(ByteOrder.nativeOrder());

        Builder() { }

        //

        @Contract("_ -> this")
        public @NotNull Builder clear(@NotNull SizeHint hint) {
            final int index = hint.value() << 3;
            this.buffer.putInt(index, 0);
            this.buffer.putInt(index | 4, 0);
            return this;
        }

        @Contract("_, _, _ -> this")
        public @NotNull Builder set(@NotNull SizeHint hint, int width, int height) {
            if (width < 1 || height < 1)
                throw new IllegalArgumentException("Invalid dimensions (" + width + " x " + height + ")");

            final int index = hint.value() << 3;
            this.buffer.putInt(index, width);
            this.buffer.putInt(index | 4, height);
            return this;
        }

        @Contract("-> new")
        public @NotNull Metrics build() {
            return new Metrics(this.buffer.asReadOnlyBuffer());
        }

    }

}
