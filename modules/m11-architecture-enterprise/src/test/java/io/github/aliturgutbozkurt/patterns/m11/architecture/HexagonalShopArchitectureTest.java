package io.github.aliturgutbozkurt.patterns.m11.architecture;

import static com.tngtech.archunit.base.DescribedPredicate.alwaysTrue;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.Architectures.onionArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;
import static io.github.aliturgutbozkurt.patterns.m11.architecture.ArchitectureRules.ROOT;
import static io.github.aliturgutbozkurt.patterns.m11.architecture.ArchitectureRules.domainDependsOnlyOnTheJdkAndItself;
import static io.github.aliturgutbozkurt.patterns.m11.architecture.ArchitectureRules.importMain;
import static io.github.aliturgutbozkurt.patterns.m11.architecture.ArchitectureRules.noCyclesBetween;

import com.tngtech.archunit.core.domain.JavaClasses;
import org.junit.jupiter.api.Test;

/**
 * The hexagon's shape as executable rules (plain JUnit style: import once, {@code rule.check(classes)}). The same
 * rules run on the PatternShop hexagon and on the smaller money-transfer one.
 */
class HexagonalShopArchitectureTest {

    private static final JavaClasses SHOP = importMain(ROOT + ".examples.hexagonal.shop");
    private static final JavaClasses TRANSFER = importMain(ROOT + ".examples.hexagonal.transfer");

    @Test
    void shopIsAnOnionWithTheCompositionRootOutside() {
        onionArchitecture()
                .domainModels("..shop.domain..")
                .applicationServices("..shop.application..")
                .adapter("cli", "..shop.adapter.inbound.cli..")
                .adapter("memory", "..shop.adapter.outbound.memory..")
                .adapter("file", "..shop.adapter.outbound.file..")
                .adapter("payment", "..shop.adapter.outbound.payment..")
                .adapter("events", "..shop.adapter.outbound.events..")
                .withOptionalLayers(true) // there is no "domain service" layer here
                .ignoreDependency(resideInAPackage("..shop.config.."), alwaysTrue()) // the root may wire anything
                .check(SHOP);
    }

    @Test
    void shopDomainDependsOnlyOnTheJdkAndItself() {
        domainDependsOnlyOnTheJdkAndItself("..shop.domain..").check(SHOP);
    }

    @Test
    void shopAdaptersDoNotDependOnEachOther() {
        slices().matching("..shop.adapter.(*).(*)..").should().notDependOnEachOther().check(SHOP);
    }

    @Test
    void shopOutboundPortsAreInterfaces() {
        classes().that().resideInAPackage("..shop.application.port.outbound..").should().beInterfaces().check(SHOP);
    }

    @Test
    void shopAdaptersAreOnlyAccessedFromAdaptersAndTheCompositionRoot() {
        classes().that().resideInAPackage("..shop.adapter..")
                .should().onlyBeAccessed().byAnyPackage("..shop.adapter..", "..shop.config..").check(SHOP);
    }

    @Test
    void shopPackagesAreFreeOfCycles() {
        noCyclesBetween("..hexagonal.shop.(**)").check(SHOP);
    }

    @Test
    void transferIsAnOnionToo() {
        onionArchitecture()
                .domainModels("..transfer.domain..")
                .applicationServices("..transfer.application..")
                .adapter("inbound", "..transfer.adapter.inbound..")
                .adapter("outbound", "..transfer.adapter.outbound..")
                .withOptionalLayers(true)
                .ignoreDependency(resideInAPackage("..transfer.config.."), alwaysTrue())
                .check(TRANSFER);
    }

    @Test
    void transferDomainDependsOnlyOnTheJdkAndItselfAndPortsAreInterfaces() {
        domainDependsOnlyOnTheJdkAndItself("..transfer.domain..").check(TRANSFER);
        classes().that().resideInAPackage("..transfer.application..").and().haveSimpleNameEndingWith("Port")
                .should().beInterfaces().check(TRANSFER);
        slices().matching("..transfer.adapter.(*)..").should().notDependOnEachOther().check(TRANSFER);
        classes().that().resideInAPackage("..transfer.adapter..")
                .should().onlyBeAccessed().byAnyPackage("..transfer.adapter..", "..transfer.config..").check(TRANSFER);
        noCyclesBetween("..hexagonal.transfer.(**)").check(TRANSFER);
    }
}
