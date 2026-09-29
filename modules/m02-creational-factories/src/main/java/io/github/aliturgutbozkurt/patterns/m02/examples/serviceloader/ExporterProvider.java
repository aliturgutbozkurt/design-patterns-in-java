package io.github.aliturgutbozkurt.patterns.m02.examples.serviceloader;

/**
 * The service provider interface (SPI). Implementations are listed in
 * {@code META-INF/services/io.github.aliturgutbozkurt.patterns.m02.examples.serviceloader.ExporterProvider} and
 * need a public no-argument constructor.
 *
 * @see "m02 lesson, section ServiceLoader"
 */
public interface ExporterProvider {

    /** The format name, lower case, e.g. {@code "json"}. */
    String format();

    /** A factory method: creates the exporter for this format. */
    FieldExporter create();
}
