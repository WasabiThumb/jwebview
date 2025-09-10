package io.github.wasabithumb.jwebview.util;

import org.jetbrains.annotations.ApiStatus;

import java.lang.annotation.*;

/**
 * Signifies that a {@link java.nio.ByteBuffer ByteBuffer} field/parameter/variable
 * is or must be {@link java.nio.ByteBuffer#isDirect() direct}. Often this is not
 * strictly validated and failure to use a direct buffer will cause undefined behavior.
 */
@ApiStatus.Internal
@Target({ElementType.FIELD, ElementType.LOCAL_VARIABLE, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.SOURCE)
@Documented
public @interface Direct { }
