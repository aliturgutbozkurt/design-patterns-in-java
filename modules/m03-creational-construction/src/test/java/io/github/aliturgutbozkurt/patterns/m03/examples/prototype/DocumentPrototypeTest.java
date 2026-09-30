package io.github.aliturgutbozkurt.patterns.m03.examples.prototype;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.aliturgutbozkurt.patterns.m03.examples.prototype.documents.DocumentTemplate;
import io.github.aliturgutbozkurt.patterns.m03.support.Console;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class DocumentPrototypeTest {

    static DocumentTemplate template() {
        return new DocumentTemplate("Invoice", List.of("Header", "Lines", "Totals"), Map.of("lang", "en"));
    }

    /** Documents the bug on purpose: {@code super.clone()} copies the reference to the section list, not the list. */
    @Test
    void shallowCloneSharesTheMutableParts() {
        DocumentTemplate original = template();
        DocumentTemplate clone = original.clone();
        clone.addSection("Signature");
        clone.putMetadata("lang", "tr");
        assertThat(original.sections()).containsExactly("Header", "Lines", "Totals", "Signature");
        assertThat(original.metadata()).containsEntry("lang", "tr");
    }

    @Test
    void shallowCloneStillCopiesImmutableFields() {
        DocumentTemplate original = template();
        DocumentTemplate clone = original.clone();
        clone.setTitle("Receipt");
        assertThat(original.title()).isEqualTo("Invoice");
    }

    @Test
    void copyConstructorGivesAnIndependentDeepCopy() {
        DocumentTemplate original = template();
        DocumentTemplate copy = new DocumentTemplate(original);
        copy.addSection("Signature");
        copy.putMetadata("lang", "tr");
        copy.setTitle("Receipt");
        assertThat(original.sections()).containsExactly("Header", "Lines", "Totals");
        assertThat(original.metadata()).containsEntry("lang", "en");
        assertThat(original.title()).isEqualTo("Invoice");
        assertThat(copy.sections()).containsExactly("Header", "Lines", "Totals", "Signature");
    }

    @Test
    void demoShowsBothKindsOfCopy() {
        assertThat(Console.capture(() -> DocumentPrototypeDemo.main(new String[0]))).isEqualTo("""
                template: Invoice [Header, Lines, Totals] {lang=en}
                == clone() then edit the copy ==
                copy:     Receipt [Header, Lines, Totals, Signature] {lang=tr}
                template: Invoice [Header, Lines, Totals, Signature] {lang=tr}   <- changed too!
                == copy constructor then edit the copy ==
                copy:     Receipt [Header, Lines, Totals, Signature] {lang=tr}
                template: Invoice [Header, Lines, Totals] {lang=en}
                """);
    }
}
