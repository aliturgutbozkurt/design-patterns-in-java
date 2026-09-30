package io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.patternitis.after.Greetings;
import io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.patternitis.before.FormalGreeter;
import io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.patternitis.before.GreeterFactoryProvider;
import io.github.aliturgutbozkurt.patterns.m11.support.Console;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class PatternitisTest {

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"Ada", "  Alan ", "Grace Hopper", "   ", "Ümit"})
    void bothVersionsProduceIdenticalGreetings(String name) {
        assertThat(Greetings.greet(name)).isEqualTo(GreeterFactoryProvider.defaultFactory().create().greet(name));
    }

    @Test
    void greetingsAreFormal() {
        assertThat(Greetings.greet(" Ada ")).isEqualTo("Good day, Ada.");
        assertThat(Greetings.greet(null)).isEqualTo("Good day, guest.");
    }

    @Test
    void afterNeedsOneTypeInsteadOfFive() throws IOException, URISyntaxException {
        assertThat(typesInPackageOf(FormalGreeter.class)).isEqualTo(5);
        assertThat(typesInPackageOf(Greetings.class)).isEqualTo(1);
    }

    @Test
    void demoPrintsBothVersions() {
        assertThat(Console.capture(() -> PatternitisDemo.main(new String[0]))).isEqualTo("""
                before: Good day, Ada. | after: Good day, Ada.
                before: Good day, Alan. | after: Good day, Alan.
                before: Good day, guest. | after: Good day, guest.
                types: before 5 (GreeterFactoryProvider, GreeterFactory, GreetingStrategy, AbstractGreeter, FormalGreeter), after 1 (Greetings)
                """);
    }

    /** Counts the compiled top-level and nested classes in the package directory (lambdas create no class file). */
    private static long typesInPackageOf(Class<?> type) throws IOException, URISyntaxException {
        Path directory = Path.of(type.getResource(type.getSimpleName() + ".class").toURI()).getParent();
        try (Stream<Path> files = Files.list(directory)) {
            return files.filter(file -> file.toString().endsWith(".class")).count();
        }
    }
}
