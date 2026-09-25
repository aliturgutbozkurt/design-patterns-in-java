package io.github.aliturgutbozkurt.patterns.sample;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static org.assertj.core.api.Assertions.assertThat;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

/** Spike (F01): proves ArchUnit can read class files compiled with --release 27. */
class ArchUnitJdk27SpikeTest {

    private final JavaClasses classes =
            new ClassFileImporter().importPackages("io.github.aliturgutbozkurt.patterns.sample");

    @Test
    void importsJdk27ClassFiles() {
        assertThat(classes.contain(Greeting.class)).isTrue();
    }

    @Test
    void sampleCodeDoesNotUseJavaUtilLogging() {
        noClasses().should().dependOnClassesThat().resideInAPackage("java.util.logging..").check(classes);
    }
}
