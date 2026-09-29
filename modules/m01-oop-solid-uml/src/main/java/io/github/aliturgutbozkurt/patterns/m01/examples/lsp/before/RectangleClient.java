package io.github.aliturgutbozkurt.patterns.m01.examples.lsp.before;

/**
 * Client code written against {@link Rectangle} and its contract.
 *
 * @see "m01 lesson, section LSP"
 */
public final class RectangleClient {

    private RectangleClient() {}

    /** Resizes to 5 × 4 and returns the area — the client reasonably expects 20. */
    public static double resizeTo5By4(Rectangle rectangle) {
        rectangle.setWidth(5);
        rectangle.setHeight(4);
        return rectangle.area();
    }
}
