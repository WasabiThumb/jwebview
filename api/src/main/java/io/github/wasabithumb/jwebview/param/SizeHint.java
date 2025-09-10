package io.github.wasabithumb.jwebview.param;

/**
 * Determines how the dimensions passed to
 * {@link io.github.wasabithumb.jwebview.WebView#setSize(int, int, SizeHint) WebView#setSize}
 * should be interpreted.
 *
 * @see SizeHint#NONE
 * @see SizeHint#MIN
 * @see SizeHint#MAX
 * @see SizeHint#FIXED
 */
public enum SizeHint {
    /** Width and height are default size */
    NONE(0),

    /** Width and height are minimum bounds */
    MIN(1),

    /** Width and height are maximum bounds */
    MAX(2),

    /** Window size cannot be changed by user */
    FIXED(3);

    //

    private final int value;

    SizeHint(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }

}
