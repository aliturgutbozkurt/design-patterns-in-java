package io.github.aliturgutbozkurt.patterns.m01.examples.isp.before;

/**
 * ISP violation: a "fat" interface. Every implementer must offer printing, scanning and faxing — even a device that
 * can only print.
 *
 * @see "m01 lesson, section ISP"
 */
public interface MultiFunctionDevice {

    String print(String document);

    String scan(String page);

    String fax(String document, String number);
}
