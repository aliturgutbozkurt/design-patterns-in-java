package io.github.aliturgutbozkurt.patterns.m02.examples.abstractfactory.ui;

/**
 * Common view of every product in a widget family.
 *
 * @see "m02 lesson, section Abstract Factory"
 */
public interface Widget {

    String platform();

    String render();
}
