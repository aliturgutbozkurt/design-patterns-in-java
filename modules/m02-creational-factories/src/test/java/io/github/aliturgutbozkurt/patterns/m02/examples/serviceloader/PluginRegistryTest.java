package io.github.aliturgutbozkurt.patterns.m02.examples.serviceloader;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.aliturgutbozkurt.patterns.m02.support.Console;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.SequencedMap;
import org.junit.jupiter.api.Test;

class PluginRegistryTest {

    static SequencedMap<String, String> fields() {
        SequencedMap<String, String> fields = new LinkedHashMap<>();
        fields.put("name", "Ada");
        fields.put("city", "Ankara");
        return fields;
    }

    @Test
    void serviceLoaderFindsBothProvidersInSortedOrder() {
        assertThat(PluginRegistry.load().formats()).containsExactly("json", "yaml");
    }

    @Test
    void providerTypesAreListedWithoutCreatingThem() {
        assertThat(PluginRegistry.providerTypeNames()).containsExactly("JsonExporterProvider", "YamlExporterProvider");
    }

    @Test
    void loadedExportersWork() {
        var registry = PluginRegistry.load();
        assertThat(registry.exporterFor("json").orElseThrow().export(fields()))
                .isEqualTo("{\"name\":\"Ada\",\"city\":\"Ankara\"}");
        assertThat(registry.exporterFor("YAML").orElseThrow().export(fields())).isEqualTo("""
                name: Ada
                city: Ankara
                """);
    }

    @Test
    void unknownFormatGivesEmpty() {
        assertThat(PluginRegistry.load().exporterFor("xml")).isEmpty();
    }

    @Test
    void registryAlsoAcceptsProvidersDirectly() {
        ExporterProvider plain = new ExporterProvider() {
            @Override public String format() { return "plain"; }
            @Override public FieldExporter create() { return fields -> String.join(";", fields.values()); }
        };
        var registry = new PluginRegistry(List.of(plain));
        assertThat(registry.formats()).containsExactly("plain");
        assertThat(registry.exporterFor("plain").orElseThrow().export(fields())).isEqualTo("Ada;Ankara");
    }

    @Test
    void demoListsAndUsesThePlugins() {
        assertThat(Console.capture(() -> PluginDemo.main(new String[0]))).isEqualTo("""
                providers on the class path: [JsonExporterProvider, YamlExporterProvider]
                formats: [json, yaml]
                -- json
                {"name":"Ada","city":"Ankara"}
                -- yaml
                name: Ada
                city: Ankara
                -- xml
                no plugin for xml
                """);
    }
}
