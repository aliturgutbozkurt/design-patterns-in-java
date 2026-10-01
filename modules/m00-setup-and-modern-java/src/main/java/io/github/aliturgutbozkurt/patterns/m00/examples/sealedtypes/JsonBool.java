package io.github.aliturgutbozkurt.patterns.m00.examples.sealedtypes;

/**
 * JSON {@code true} / {@code false}.
 *
 * @see "m00 lesson, section A recursive example: JSON"
 */
public record JsonBool(boolean value) implements Json {}
