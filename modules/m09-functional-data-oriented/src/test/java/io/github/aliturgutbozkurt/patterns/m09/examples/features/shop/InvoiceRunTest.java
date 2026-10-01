package io.github.aliturgutbozkurt.patterns.m09.examples.features.shop;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.aliturgutbozkurt.patterns.m09.examples.features.shop.classic.InvoiceRun;
import io.github.aliturgutbozkurt.patterns.m09.support.Console;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class InvoiceRunTest {

    private static final String CLASSIC = InvoiceRun.run(InvoiceRun.sampleOrders());
    private static final String MODERN =
            io.github.aliturgutbozkurt.patterns.m09.examples.features.shop.modern.InvoiceRun.run(
                    io.github.aliturgutbozkurt.patterns.m09.examples.features.shop.modern.InvoiceRun.sampleOrders());

    @Test
    void bothVersionsProduceByteIdenticalInvoices() {
        assertThat(MODERN).isEqualTo(CLASSIC);
        assertThat(CLASSIC).contains("""
                INVOICE A-3 for Linus
                  LAMP-0003 x1                 60.00
                  TEE-0002 x3                  60.00
                  subtotal                    120.00
                  discount BULK                -6.00
                  tax                          24.00
                  total                       138.00
                  shipping weight 1800 g
                """);
    }

    @Test
    void bothVersionsRunThePostActionsInTheSameOrder() {
        String expected = """
                -- post-actions
                email invoice A-1 to Ada
                archive invoice A-1
                book shipping A-1 (700 g)
                email invoice A-2 to Grace
                archive invoice A-2
                email invoice A-3 to Linus
                archive invoice A-3
                book shipping A-3 (1800 g)
                email invoice A-4 to Alan
                archive invoice A-4
                """;
        assertThat(CLASSIC).endsWith(expected);
        assertThat(MODERN).endsWith(expected);
    }

    @Test
    void theModernPackageHasNoAbstractClassAndNoAcceptMethod() throws IOException, URISyntaxException {
        List<Class<?>> modern = compiledClasses("modern", true);
        assertThat(modern).isNotEmpty();
        assertThat(modern).noneMatch(c -> !c.isInterface() && Modifier.isAbstract(c.getModifiers()));
        assertThat(modern).flatMap(c -> Arrays.asList(c.getDeclaredMethods()))
                .noneMatch(m -> m.getName().equals("accept"));
        assertThat(compiledClasses("classic", true)).anyMatch(c -> !c.isInterface()
                && Modifier.isAbstract(c.getModifiers()));
    }

    @Test
    void theModernPackageDeclaresFewerTopLevelTypes() throws IOException, URISyntaxException {
        var modernTypes = io.github.aliturgutbozkurt.patterns.m09.examples.features.shop.modern.InvoiceRun.TYPES;
        assertThat(compiledClasses("classic", false)).containsExactlyInAnyOrderElementsOf(InvoiceRun.TYPES);
        assertThat(compiledClasses("modern", false)).containsExactlyInAnyOrderElementsOf(modernTypes);
        assertThat(modernTypes).hasSize(6);
        assertThat(InvoiceRun.TYPES).hasSize(21);
    }

    @Test
    void demosPrintTheInvoicesAndTheTypeCounts() {
        String classic = Console.capture(() ->
                io.github.aliturgutbozkurt.patterns.m09.examples.features.shop.classic.InvoiceRunDemo.main(new String[0]));
        String modern = Console.capture(() ->
                io.github.aliturgutbozkurt.patterns.m09.examples.features.shop.modern.InvoiceRunDemo.main(new String[0]));
        assertThat(classic).isEqualTo(CLASSIC + "-- classic package: 21 top-level types\n");
        assertThat(modern).isEqualTo(MODERN + "-- modern package: 6 top-level types (classic: 21)\n");
    }

    /** The classes compiled into {@code features.shop.<sub>}, with or without nested classes. */
    private static List<Class<?>> compiledClasses(String sub, boolean withNested)
            throws IOException, URISyntaxException {
        String pkg = InvoiceRunTest.class.getPackageName() + "." + sub;
        Path root = Path.of(InvoiceRun.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        try (Stream<Path> files = Files.list(root.resolve(pkg.replace('.', '/')))) {
            return files.map(p -> p.getFileName().toString())
                    .filter(name -> name.endsWith(".class"))
                    .map(name -> name.substring(0, name.length() - ".class".length()))
                    .filter(name -> withNested || !name.contains("$"))
                    .filter(name -> !name.matches(".*\\$\\d+.*"))
                    .<Class<?>>map(name -> load(pkg + "." + name))
                    .toList();
        }
    }

    private static Class<?> load(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }
}
