package io.github.aliturgutbozkurt.patterns.m00.examples.sealedtypes;

/**
 * A closed set of shapes: only the permitted records can implement it, so a {@code switch} over a {@code Shape}
 * can be checked for exhaustiveness by the compiler.
 *
 * @see "m00 lesson, section Sealed hierarchies and pattern matching"
 */
public sealed interface Shape permits Circle, Rectangle, Triangle {

    /** Behaviour that belongs to every shape can still be an ordinary method (object-oriented style). */
    double area();
}
