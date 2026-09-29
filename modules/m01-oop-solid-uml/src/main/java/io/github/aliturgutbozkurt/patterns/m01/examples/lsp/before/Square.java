package io.github.aliturgutbozkurt.patterns.m01.examples.lsp.before;

/**
 * LSP violation: "a square is a rectangle" in geometry, but not as a <em>mutable</em> subtype — keeping the sides
 * equal forces each setter to change both sides, which breaks the {@link Rectangle} contract.
 *
 * @see "m01 lesson, section LSP"
 */
public final class Square extends Rectangle {

    public Square(double side) {
        // Flexible constructor body (JEP 513): validate with Square's own message before super(...) runs.
        if (!(side > 0)) {
            throw new IllegalArgumentException("side must be positive: " + side);
        }
        super(side, side);
    }

    @Override
    public void setWidth(double width) {
        super.setWidth(width);
        super.setHeight(width);
    }

    @Override
    public void setHeight(double height) {
        super.setWidth(height);
        super.setHeight(height);
    }
}
