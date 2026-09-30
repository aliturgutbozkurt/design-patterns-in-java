package io.github.aliturgutbozkurt.patterns.m05.examples.bridge.shapes;

/**
 * Bridge implementor: the primitive drawing operations one output format offers. Shapes are written against this
 * interface only, so a new format never touches the shape hierarchy.
 *
 * @see "m05 lesson, section Bridge"
 */
public interface Renderer {

    String circle(int x, int y, int radius);

    String rectangle(int x, int y, int width, int height);
}
