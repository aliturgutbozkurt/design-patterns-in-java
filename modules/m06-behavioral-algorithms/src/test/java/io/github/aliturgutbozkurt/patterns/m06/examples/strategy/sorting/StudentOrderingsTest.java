package io.github.aliturgutbozkurt.patterns.m06.examples.strategy.sorting;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import io.github.aliturgutbozkurt.patterns.m06.support.Console;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.Test;

class StudentOrderingsTest {

    private static final List<Student> ROSTER = List.of(
            new Student("Deniz", 3.2, 2, "Turing"),
            new Student("Ece", 3.5, 3, null),
            new Student("Ada", 3.9, 2, "Turing"),
            new Student("Cem", 3.9, 3, "Hopper"),
            new Student("Bora", 3.5, 1, null));

    private static List<String> names(List<Student> students) {
        return students.stream().map(Student::name).toList();
    }

    private static List<Student> sorted(Comparator<Student> order) {
        var copy = new ArrayList<>(ROSTER);
        copy.sort(order);
        return copy;
    }

    @Test
    void sortsByGpaDescendingThenByName() {
        assertThat(names(sorted(StudentOrderings.BY_GPA_DESC_THEN_NAME)))
                .containsExactly("Ada", "Cem", "Bora", "Ece", "Deniz");
    }

    @Test
    void studentsWithoutAnAdvisorSortLast() {
        assertThat(names(sorted(StudentOrderings.BY_ADVISOR_NULLS_LAST_THEN_NAME)))
                .containsExactly("Cem", "Ada", "Deniz", "Bora", "Ece");
    }

    @Test
    void sortingBySecondKeyKeepsEarlierOrderForEqualKeys() {
        var students = new ArrayList<>(ROSTER);
        students.sort(StudentOrderings.BY_NAME);
        students.sort(StudentOrderings.BY_YEAR);  // List.sort is stable: equal years keep the name order
        assertThat(names(students)).containsExactly("Bora", "Ada", "Deniz", "Cem", "Ece");
    }

    @Test
    void studentNeedsAName() {
        assertThatNullPointerException().isThrownBy(() -> new Student(null, 3.0, 1, null));
    }

    @Test
    void demoPrintsThreeOrderings() {
        assertThat(Console.capture(() -> ComparatorDemo.main(new String[0]))).isEqualTo("""
                by GPA desc, then name:  Ada 3.9, Cem 3.9, Bora 3.5, Ece 3.5, Deniz 3.2
                by advisor, none last:   Cem (Hopper), Ada (Turing), Deniz (Turing), Bora (-), Ece (-)
                by name, then by year:   Bora y1, Ada y2, Deniz y2, Cem y3, Ece y3
                """);
    }
}
