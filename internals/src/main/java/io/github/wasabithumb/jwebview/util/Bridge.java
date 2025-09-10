package io.github.wasabithumb.jwebview.util;

import io.github.wasabithumb.jwebview.except.WebViewException;
import io.github.wasabithumb.jwebview.except.WebViewExceptionImpl;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.StandardCharsets;

/**
 * Utilities used by native code
 */
@ApiStatus.Internal
public final class Bridge {

    @Pinned
    @Contract("_ -> fail")
    public static void raise(int code) throws WebViewException {
        throw new WebViewExceptionImpl(code);
    }

    @Pinned
    public static long handleOf(@NotNull RemoteObject remote) {
        return remote.handle();
    }

    @Pinned
    @Contract("null -> null; !null -> !null")
    public static @Direct ByteBuffer stringEncode(String string) {
        if (string == null) return null;
        CharBuffer src = CharBuffer.wrap(string);
        ByteBuffer target = ByteBuffer.allocateDirect(string.length() + 1);
        CharsetEncoder encoder = StandardCharsets.UTF_8.newEncoder();
        CoderResult result;

        while (src.hasRemaining()) {
            result = encoder.encode(src, target, true);
            if (result.isOverflow()) {
                target = resize(target, target.limit() << 1);
            } else if (result.isUnderflow()) {
                break;
            } else {
                try {
                    result.throwException();
                } catch (CharacterCodingException e) {
                    throw new AssertionError("Failed to encode string", e);
                }
            }
        }

        if (!target.hasRemaining()) target = resize(target, target.limit() + 1);
        target.put((byte) 0);
        return target;
    }

    @Pinned
    @Contract("null -> null; !null -> !null")
    public static String stringDecode(ByteBuffer buf) {
        if (buf == null) return null;
        try {
            return StandardCharsets.UTF_8
                    .newDecoder()
                    .decode(buf)
                    .toString();
        } catch (CharacterCodingException e) {
            throw new AssertionError("Failed to decode string", e);
        }
    }

    @Pinned
    public static @NotNull @Direct ByteBuffer describe(@NotNull Throwable t) {
        return stringEncode(escape(t.getClass().getSimpleName() + ": " + t.getMessage()));
    }

    private static @NotNull String escape(@NotNull String s) {
        final int len = s.length();
        StringBuilder ret = new StringBuilder(len + 2);
        ret.append('"');
        char c;

        for (int i = 0; i < len; i++) {
            c = s.charAt(i);
            switch (c) {
                case '\\':
                case '"':
                case '/':
                    ret.append('\\');
                    ret.append(c);
                    break;
                case '\b':
                    ret.append("\\b");
                    break;
                case '\t':
                    ret.append("\\t");
                    break;
                case '\n':
                    ret.append("\\n");
                    break;
                case '\f':
                    ret.append("\\f");
                    break;
                case '\r':
                    ret.append("\\r");
                    break;
                default:
                    if (c < ' ') {
                        String t = "000" + Integer.toHexString(c);
                        ret.append("\\u").append(t.substring(t.length() - 4));
                    } else {
                        ret.append(c);
                    }
                    break;
            }
        }

        ret.append('"');
        return ret.toString();
    }

    private static @NotNull @Direct ByteBuffer resize(@NotNull ByteBuffer buf, int size) {
        ByteBuffer cpy = ByteBuffer.allocateDirect(size);
        int position = buf.position();

        buf.position(0);
        cpy.put(buf);
        cpy.position(position);

        return cpy;
    }

    //

    private Bridge() { }

}
