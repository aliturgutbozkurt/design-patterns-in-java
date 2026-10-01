package io.github.aliturgutbozkurt.patterns.m11.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

/**
 * Course conventions (CLAUDE.md §5) as rules, in the ArchUnit JUnit-engine style: the engine imports the classes
 * once and evaluates every {@code @ArchTest} field. Covers all of m11's production code (examples, exercises,
 * solutions).
 */
@AnalyzeClasses(packages = "io.github.aliturgutbozkurt.patterns.m11", importOptions = ImportOption.DoNotIncludeTests.class)
class CourseConventionsArchTest {

    @ArchTest
    static final ArchRule mainHasNoExternalDependencies = classes()
            .should().onlyDependOnClassesThat().resideInAnyPackage("java..", "io.github.aliturgutbozkurt.patterns.m11..")
            .as("src/main depends only on the JDK and the course's own packages");

    @ArchTest
    static final ArchRule topLevelExamplePackagesAreFreeOfCycles = slices()
            .matching("io.github.aliturgutbozkurt.patterns.m11.examples.(*)..")
            .should().beFreeOfCycles(); // erosion's cycle is inside one top-level package, see ErosionRulesTest

    @ArchTest
    static final ArchRule nobodyCallsSystemExit = noClasses()
            .should().callMethod(System.class, "exit", int.class);

    @ArchTest
    static final ArchRule mutableStaticStateOnlyInTheGlobalStateAntiPattern = fields()
            .that().areStatic().and().areNotFinal()
            .should().beDeclaredInClassesThat().resideInAPackage("..antipatterns.globalstate.before..")
            .as("non-final static fields exist only in antipatterns.globalstate.before (owner decision 2)");
}
