package io.github.aliturgutbozkurt.patterns.m03.exercises.ex02;

/** GIVEN — do not modify. */
public record Point(int x, int y) {

    public Point moved(int dx, int dy) {
        return new Point(x + dx, y + dy);
    }
}
