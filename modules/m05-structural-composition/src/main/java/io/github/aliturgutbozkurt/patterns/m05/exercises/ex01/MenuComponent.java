package io.github.aliturgutbozkurt.patterns.m05.exercises.ex01;

/** GIVEN — do not modify. A node of a restaurant menu: a single item or a (sub)menu holding other nodes. */
public sealed interface MenuComponent permits MenuItem, Menu {

    String name();
}
