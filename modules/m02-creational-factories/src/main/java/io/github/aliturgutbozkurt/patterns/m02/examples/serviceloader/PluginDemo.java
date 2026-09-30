package io.github.aliturgutbozkurt.patterns.m02.examples.serviceloader;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.SequencedMap;

/**
 * Run: {@code java modules/m02-creational-factories/src/main/java/io/github/aliturgutbozkurt/patterns/m02/examples/serviceloader/PluginDemo.java}
 * — the source launcher finds {@code META-INF/services} in the source root, so no build is needed.
 */
public final class PluginDemo {

    private PluginDemo() {}

    public static void main(String[] args) {
        System.out.println("providers on the class path: " + PluginRegistry.providerTypeNames());
        var registry = PluginRegistry.load();
        System.out.println("formats: " + registry.formats());

        SequencedMap<String, String> fields = new LinkedHashMap<>();
        fields.put("name", "Ada");
        fields.put("city", "Ankara");
        for (String format : List.of("json", "yaml", "xml")) {
            System.out.println("-- " + format);
            String text = registry.exporterFor(format).map(exporter -> exporter.export(fields))
                    .orElse("no plugin for " + format);
            System.out.print(text.endsWith("\n") ? text : text + "\n");
        }
    }
}
