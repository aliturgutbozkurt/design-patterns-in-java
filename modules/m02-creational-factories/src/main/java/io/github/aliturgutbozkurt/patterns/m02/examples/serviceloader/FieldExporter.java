package io.github.aliturgutbozkurt.patterns.m02.examples.serviceloader;

import java.util.SequencedMap;

/**
 * What a plugin produces: turns ordered fields into text.
 *
 * @see "m02 lesson, section ServiceLoader"
 */
@FunctionalInterface
public interface FieldExporter {

    String export(SequencedMap<String, String> fields);
}
