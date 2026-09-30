package io.github.aliturgutbozkurt.patterns.m05.examples.bridge.shapes;

/**
 * Concrete implementor: text art for a terminal. It draws the shape itself and ignores the position.
 *
 * @see "m05 lesson, section Bridge"
 */
public final class AsciiRenderer implements Renderer {

    @Override
    public String circle(int x, int y, int radius) {
        var art = new StringBuilder();
        for (int dy = -radius; dy <= radius; dy++) {
            var row = new StringBuilder();
            for (int dx = -radius; dx <= radius; dx++) {
                row.append(dx * dx + dy * dy <= radius * radius ? '*' : ' ');
            }
            art.append(row.toString().stripTrailing()).append('\n');
        }
        return art.toString();
    }

    @Override
    public String rectangle(int x, int y, int width, int height) {
        var art = new StringBuilder();
        for (int row = 0; row < height; row++) {
            boolean edgeRow = row == 0 || row == height - 1;
            for (int column = 0; column < width; column++) {
                boolean edgeColumn = column == 0 || column == width - 1;
                art.append(edgeRow && edgeColumn ? '+' : edgeRow ? '-' : edgeColumn ? '|' : ' ');
            }
            art.append('\n');
        }
        return art.toString();
    }
}
