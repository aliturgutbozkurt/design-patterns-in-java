package io.github.aliturgutbozkurt.patterns.m11.architecture;

import static io.github.aliturgutbozkurt.patterns.m11.architecture.ArchitectureRules.ROOT;
import static io.github.aliturgutbozkurt.patterns.m11.architecture.ArchitectureRules.domainDependsOnlyOnTheJdkAndItself;
import static io.github.aliturgutbozkurt.patterns.m11.architecture.ArchitectureRules.importMain;
import static io.github.aliturgutbozkurt.patterns.m11.architecture.ArchitectureRules.noCyclesBetween;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tngtech.archunit.core.domain.JavaClasses;
import io.github.aliturgutbozkurt.patterns.m11.examples.erosion.ErosionDemo;
import io.github.aliturgutbozkurt.patterns.m11.support.Console;
import org.junit.jupiter.api.Test;

/** The eroded code compiles and runs; these tests prove that the rules — and only the rules — catch it. */
class ErosionRulesTest {

    private static final JavaClasses EROSION = importMain(ROOT + ".examples.erosion");

    @Test
    void theCodeWorks() {
        assertThat(Console.capture(() -> ErosionDemo.main(new String[0]))).isEqualTo("""
                stored invoice INV-1 (99.00)
                compiles and runs — the broken dependency direction is invisible to javac
                """);
    }

    @Test
    void domainIsolationRuleFailsAndNamesInvoiceSave() {
        assertThatThrownBy(() -> domainDependsOnlyOnTheJdkAndItself("..erosion.domain..").check(EROSION))
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("Architecture Violation")
                .hasMessageContaining("Invoice.save()")
                .hasMessageContaining("InvoiceDao");
    }

    @Test
    void noCyclesRuleFailsAndPrintsTheCycle() {
        assertThatThrownBy(() -> noCyclesBetween("..erosion.(*)..").check(EROSION))
                .isInstanceOf(AssertionError.class)
                .satisfies(error -> assertThat(error.getMessage().replaceAll("\\s+", " ")) // ArchUnit wraps the cycle
                        .contains("Cycle detected: Slice adapter -> Slice domain -> Slice adapter")
                        .contains("Invoice.save()"));
    }
}
