package io.github.aliturgutbozkurt.patterns.m01.examples.isp.after;

/**
 * Role interface: something that sends a fax.
 *
 * @see "m01 lesson, section ISP"
 */
@FunctionalInterface
public interface Fax {

    String fax(String document, String number);
}
