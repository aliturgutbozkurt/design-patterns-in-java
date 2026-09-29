package io.github.aliturgutbozkurt.patterns.m01.examples.isp.after;

/**
 * One class may play several roles.
 *
 * @see "m01 lesson, section ISP"
 */
public final class OfficeMachine implements Printer, DocumentScanner, Fax {

    @Override
    public String print(String document) {
        return "OfficeMachine printed " + document;
    }

    @Override
    public String scan(String page) {
        return "OfficeMachine scanned " + page;
    }

    @Override
    public String fax(String document, String number) {
        return "OfficeMachine faxed " + document + " to " + number;
    }
}
