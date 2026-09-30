package io.github.aliturgutbozkurt.patterns.m01.examples.isp.after;

/**
 * Role interface: something that prints.
 *
 * @see "m01 lesson, section ISP"
 */
@FunctionalInterface
public interface Printer {

    String print(String document);
}
