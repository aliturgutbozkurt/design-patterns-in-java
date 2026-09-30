package io.github.aliturgutbozkurt.patterns.m02.exercises.ex01;

/**
 * Assignment 01 — your colour class. See assignments/01-colour-factories.en.md (Türkçe: 01-colour-factories.tr.md).
 * Keep the constructor private: callers use the static factories.
 */
public final class RgbColor implements Color {

    private RgbColor() {
        // TODO(ex01): store the three components.
    }

    /** Components 0–255; returns the cached instance when the value is a named colour. */
    public static RgbColor rgb(int red, int green, int blue) {
        // TODO(ex01)
        throw new UnsupportedOperationException("TODO(ex01): implement rgb(int, int, int)");
    }

    /** {@code #RRGGBB} or {@code #RGB}, case-insensitive. */
    public static RgbColor hex(String text) {
        // TODO(ex01)
        throw new UnsupportedOperationException("TODO(ex01): implement hex(String)");
    }

    /** One of the 8 basic colours, case-insensitive; always the same instance per colour. */
    public static RgbColor named(String name) {
        // TODO(ex01)
        throw new UnsupportedOperationException("TODO(ex01): implement named(String)");
    }

    @Override
    public int red() {
        throw new UnsupportedOperationException("TODO(ex01): implement red()");
    }

    @Override
    public int green() {
        throw new UnsupportedOperationException("TODO(ex01): implement green()");
    }

    @Override
    public int blue() {
        throw new UnsupportedOperationException("TODO(ex01): implement blue()");
    }

    @Override
    public String toHex() {
        throw new UnsupportedOperationException("TODO(ex01): implement toHex()");
    }

    // TODO(ex01): equals, hashCode and toString — two colours are equal when their components are equal.
}
