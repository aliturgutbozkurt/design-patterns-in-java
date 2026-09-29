package io.github.aliturgutbozkurt.patterns.m02.examples.factorymethod.export;

import java.util.List;

/**
 * The product of the factory method: knows how one format writes a title, a header row, a data row and the end.
 *
 * @see "m02 lesson, section Factory Method"
 */
public interface Formatter {

    String begin(String title);

    String header(List<String> cells);

    String row(List<String> cells);

    String end();
}
