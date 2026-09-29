package io.github.aliturgutbozkurt.patterns.m01.examples.isp.before;

/**
 * Forced to implement methods it cannot honour, so it throws — callers only find out at run time.
 *
 * @see "m01 lesson, section ISP"
 */
public final class BasicPrinter implements MultiFunctionDevice {

    @Override
    public String print(String document) {
        return "BasicPrinter printed " + document;
    }

    @Override
    public String scan(String page) {
        throw new UnsupportedOperationException("BasicPrinter cannot scan");
    }

    @Override
    public String fax(String document, String number) {
        throw new UnsupportedOperationException("BasicPrinter cannot fax");
    }
}
