package io.github.aliturgutbozkurt.patterns.m02.examples.serviceloader;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Finds exporter plugins at run time. The code never names {@code JsonExporterProvider} or
 * {@code YamlExporterProvider}: adding a format means adding a JAR (or a class plus one line in
 * {@code META-INF/services}), not editing this class.
 *
 * @see "m02 lesson, section ServiceLoader"
 */
public final class PluginRegistry {

    private final Map<String, ExporterProvider> byFormat;

    /** A registry over the given providers — handy for tests. */
    public PluginRegistry(List<ExporterProvider> providers) {
        byFormat = providers.stream().collect(Collectors.toMap(
                provider -> provider.format().toLowerCase(Locale.ROOT), provider -> provider,
                (first, second) -> first, TreeMap::new));
    }

    /** A registry over every provider {@link ServiceLoader} finds on the class path. */
    public static PluginRegistry load() {
        return new PluginRegistry(ServiceLoader.load(ExporterProvider.class).stream()
                .map(ServiceLoader.Provider::get)
                .toList());
    }

    /** Provider class names, found <em>without</em> instantiating any provider. */
    public static List<String> providerTypeNames() {
        return ServiceLoader.load(ExporterProvider.class).stream()
                .map(provider -> provider.type().getSimpleName())
                .sorted()
                .toList();
    }

    /** Known formats in alphabetical order. */
    public List<String> formats() {
        return List.copyOf(byFormat.keySet());
    }

    /** A new exporter for {@code format} (case-insensitive), or empty if no plugin provides it. */
    public Optional<FieldExporter> exporterFor(String format) {
        return Optional.ofNullable(byFormat.get(format.toLowerCase(Locale.ROOT))).map(ExporterProvider::create);
    }
}
