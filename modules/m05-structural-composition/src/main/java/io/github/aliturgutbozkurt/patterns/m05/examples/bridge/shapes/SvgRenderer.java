package io.github.aliturgutbozkurt.patterns.m05.examples.bridge.shapes;

/**
 * Concrete implementor: SVG elements.
 *
 * @see "m05 lesson, section Bridge"
 */
public final class SvgRenderer implements Renderer {

    @Override
    public String circle(int x, int y, int radius) {
        return "<circle cx=\"" + x + "\" cy=\"" + y + "\" r=\"" + radius + "\"/>";
    }

    @Override
    public String rectangle(int x, int y, int width, int height) {
        return "<rect x=\"" + x + "\" y=\"" + y + "\" width=\"" + width + "\" height=\"" + height + "\"/>";
    }
}
