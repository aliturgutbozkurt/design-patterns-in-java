package io.github.aliturgutbozkurt.patterns.m01.examples.isp.after;

/**
 * Implements only the role it can play — nothing to throw.
 *
 * @see "m01 lesson, section ISP"
 */
public final class BasicPrinter implements Printer {

    @Override
    public String print(String document) {
        return "BasicPrinter printed " + document;
    }
}
