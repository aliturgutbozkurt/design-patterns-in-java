package io.github.aliturgutbozkurt.patterns.m06.exercises.ex01;

/** GIVEN — do not modify. One change to the text, as data: {@link Insert}, {@link Delete} or {@link Replace}. */
public sealed interface Edit permits Insert, Delete, Replace {}
