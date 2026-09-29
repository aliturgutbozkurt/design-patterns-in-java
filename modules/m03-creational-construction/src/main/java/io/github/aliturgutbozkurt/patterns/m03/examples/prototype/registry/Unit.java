package io.github.aliturgutbozkurt.patterns.m03.examples.prototype.registry;

/**
 * The prototype interface: every unit knows how to copy itself.
 *
 * @see "m03 lesson, section Prototype"
 */
public interface Unit {

    String type();

    Stats stats();

    Position position();

    void moveTo(int x, int y);

    /** A new, independent unit in the same state. */
    Unit copy();

    default String describe() {
        return type() + " at " + position() + " " + stats();
    }
}
