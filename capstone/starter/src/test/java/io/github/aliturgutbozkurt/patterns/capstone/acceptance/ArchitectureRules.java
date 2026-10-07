package io.github.aliturgutbozkurt.patterns.capstone.acceptance;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

/**
 * The seven hexagonal-architecture rules of brief §5, checked on the application's production classes (imported once
 * per binding). They run in every build, are green on the skeleton and must stay green. Rules over packages that may
 * still be empty allow an empty selection, so an unfinished project is not red for having no adapters yet.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class ArchitectureRules extends AcceptanceContract {

    private static final String API = "io.github.aliturgutbozkurt.patterns.capstone.api";
    private static final String COURSE = "io.github.aliturgutbozkurt.patterns.capstone..";

    private JavaClasses imported;

    private JavaClasses imported() {
        if (imported == null) {
            imported = new ClassFileImporter().withImportOption(new ImportOption.DoNotIncludeTests())
                    .importPackages(applicationRootPackage());
        }
        return imported;
    }

    private String root(String subpackage) {
        return applicationRootPackage() + subpackage;
    }

    @Test
    void domainDependsOnlyOnJdkAndApiValues() {
        classes().that().resideInAPackage(root(".domain.."))
                .should().onlyDependOnClassesThat().resideInAnyPackage(
                        "java.lang..", "java.util..", "java.math..", "java.time..", root(".domain.."),
                        API + ".model..", API + ".event..", API + ".pattern..")
                .allowEmptyShould(true)
                .because("the domain knows only the JDK, itself and the GIVEN values, events and annotations")
                .check(imported());
    }

    @Test
    void applicationDoesNotDependOnAdaptersOrConfig() {
        noClasses().that().resideInAPackage(root(".application.."))
                .should().dependOnClassesThat().resideInAnyPackage(
                        root(".adapter.."), root(".config.."), API + ".external..", API + ".sim..")
                .allowEmptyShould(true)
                .because("the application talks to the outside world only through its own ports")
                .check(imported());
    }

    @Test
    void externalSystemsAreReachedOnlyThroughOutboundAdapters() {
        noClasses().that().resideOutsideOfPackages(root(".adapter.out.."), root(".config.."))
                .should().dependOnClassesThat().resideInAPackage(API + ".external..")
                .because("payment, warehouse and notifications are reached only through outbound adapters")
                .check(imported());
        noClasses().that().resideOutsideOfPackage(root(".config.."))
                .should().dependOnClassesThat().resideInAPackage(API + ".sim..")
                .because("only the composition root may choose the simulators")
                .check(imported());
    }

    @Test
    void adaptersDoNotDependOnEachOther() {
        slices().matching(root(".adapter.(*).(*)..")).should().notDependOnEachOther()
                .allowEmptyShould(true)
                .check(imported());
    }

    @Test
    void adaptersAreWiredOnlyInConfig() {
        classes().that().resideInAPackage(root(".adapter.."))
                .should().onlyBeAccessed().byAnyPackage(root(".adapter.."), root(".config.."))
                .allowEmptyShould(true)
                .check(imported());
        classes().that().resideInAPackage(root(".config.."))
                .should().onlyBeAccessed().byAnyPackage(root(".config.."))
                .because("nothing depends on the composition root")
                .check(imported());
    }

    @Test
    void noCyclesBetweenPackages() {
        slices().matching(root(".(*)..")).should().beFreeOfCycles()
                .allowEmptyShould(true)
                .check(imported());
    }

    @Test
    void productionCodeIsSelfContained() {
        classes().should().onlyDependOnClassesThat().resideInAnyPackage("java..", COURSE)
                .because("src/main has no external dependencies")
                .check(imported());
        noClasses().should().callMethod(System.class, "exit", int.class)
                .check(imported());
        fields().that().areStatic().should().beFinal()
                .allowEmptyShould(true)
                .because("no mutable static state")
                .check(imported());
    }
}
