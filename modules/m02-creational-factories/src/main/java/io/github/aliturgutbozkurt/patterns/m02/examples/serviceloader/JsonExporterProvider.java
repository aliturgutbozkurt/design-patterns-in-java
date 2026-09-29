package io.github.aliturgutbozkurt.patterns.m02.examples.serviceloader;

import java.util.stream.Collectors;

/**
 * Plugin: flat JSON object (string values, escaped quotes and backslashes).
 *
 * @see "m02 lesson, section ServiceLoader"
 */
public final class JsonExporterProvider implements ExporterProvider {

    @Override
    public String format() {
        return "json";
    }

    @Override
    public FieldExporter create() {
        return fields -> fields.entrySet().stream()
                .map(entry -> quote(entry.getKey()) + ":" + quote(entry.getValue()))
                .collect(Collectors.joining(",", "{", "}"));
    }

    private static String quote(String text) {
        return "\"" + text.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }
}
