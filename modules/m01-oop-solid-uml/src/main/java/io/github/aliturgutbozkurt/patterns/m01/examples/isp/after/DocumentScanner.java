package io.github.aliturgutbozkurt.patterns.m01.examples.isp.after;

/**
 * Role interface: something that scans a page. (Named to avoid confusion with {@code java.util.Scanner}.)
 *
 * @see "m01 lesson, section ISP"
 */
@FunctionalInterface
public interface DocumentScanner {

    String scan(String page);
}
