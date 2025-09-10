package io.github.wasabithumb.jwebview.util;

import org.jetbrains.annotations.ApiStatus;

import java.lang.annotation.*;

/**
 * Signifies that a member's existence and contract (if applicable) is strictly
 * demanded by native code.
 */
@ApiStatus.Internal
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.SOURCE)
@Documented
public @interface Pinned { }
