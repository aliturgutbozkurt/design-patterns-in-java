package io.github.aliturgutbozkurt.patterns.m02.examples.serviceloader;

import java.util.stream.Collectors;

/**
 * Plugin: one {@code key: value} line per field.
 *
 * @see "m02 lesson, section ServiceLoader"
 */
public final class YamlExporterProvider implements ExporterProvider {

    @Override
    public String format() {
        return "yaml";
    }

    @Override
    public FieldExporter create() {
        return fields -> fields.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue() + "\n")
                .collect(Collectors.joining());
    }
}
