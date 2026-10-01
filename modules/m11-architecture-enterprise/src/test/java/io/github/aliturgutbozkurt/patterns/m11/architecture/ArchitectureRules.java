package io.github.aliturgutbozkurt.patterns.m11.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;

/** Rules shared by the architecture tests: written once, applied to the healthy hexagons and to the eroded code. */
final class ArchitectureRules {

    static final String ROOT = "io.github.aliturgutbozkurt.patterns.m11";

    private ArchitectureRules() {}

    /** The production classes (target/classes only) of {@code packageName} and its subpackages. */
    static JavaClasses importMain(String packageName) {
        return new ClassFileImporter().withImportOption(new ImportOption.DoNotIncludeTests()).importPackages(packageName);
    }

    /** A domain may use the JDK's basic packages and itself — nothing from the application, adapters or config. */
    static ArchRule domainDependsOnlyOnTheJdkAndItself(String domainPackage) {
        return classes().that().resideInAPackage(domainPackage)
                .should().onlyDependOnClassesThat()
                .resideInAnyPackage("java.lang..", "java.util..", "java.math..", "java.time..", domainPackage)
                .as("domain classes in " + domainPackage + " depend only on the JDK and the domain");
    }

    /** The slices matched by {@code pattern} (e.g. {@code "..shop.(**)"}) form no dependency cycle. */
    static ArchRule noCyclesBetween(String pattern) {
        return slices().matching(pattern).should().beFreeOfCycles();
    }
}
