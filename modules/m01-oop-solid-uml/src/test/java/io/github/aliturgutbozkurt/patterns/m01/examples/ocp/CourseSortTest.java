package io.github.aliturgutbozkurt.patterns.m01.examples.ocp;

import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingInt;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import io.github.aliturgutbozkurt.patterns.m01.examples.ocp.sorting.Course;
import io.github.aliturgutbozkurt.patterns.m01.examples.ocp.sorting.after.CourseCatalog;
import io.github.aliturgutbozkurt.patterns.m01.examples.ocp.sorting.before.CourseSorter;
import io.github.aliturgutbozkurt.patterns.m01.support.Console;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.Test;

class CourseSortTest {

    static final List<Course> COURSES = List.of(
            new Course("CENG213", "Data Structures", 7),
            new Course("MATH119", "Calculus", 7),
            new Course("CENG101", "Programming I", 6),
            new Course("CENG315", "Algorithms", 8),
            new Course("CENG242", "Programming Languages", 6));

    static List<String> codes(List<Course> courses) {
        return courses.stream().map(Course::code).toList();
    }

    @Test
    void beforeSortsByEachKnownKey() {
        var sorter = new CourseSorter();
        assertThat(codes(sorter.sort(COURSES, "code")))
                .containsExactly("CENG101", "CENG213", "CENG242", "CENG315", "MATH119");
        assertThat(codes(sorter.sort(COURSES, "credits")))
                .containsExactly("CENG315", "CENG213", "MATH119", "CENG101", "CENG242");
        assertThat(codes(sorter.sort(COURSES, "title")))
                .containsExactly("CENG315", "MATH119", "CENG213", "CENG101", "CENG242");
    }

    @Test
    void beforeRejectsUnknownKey() {
        assertThatIllegalArgumentException().isThrownBy(() -> new CourseSorter().sort(COURSES, "room"))
                .withMessage("unknown sort key: room");
    }

    @Test
    void afterGivesTheSameOrderingsWithComparators() {
        var catalog = new CourseCatalog(COURSES);
        var sorter = new CourseSorter();
        assertThat(catalog.sorted(comparing(Course::code))).isEqualTo(sorter.sort(COURSES, "code"));
        assertThat(catalog.sorted(comparingInt(Course::credits).reversed().thenComparing(Course::code)))
                .isEqualTo(sorter.sort(COURSES, "credits"));
        assertThat(catalog.sorted(comparing(Course::title))).isEqualTo(sorter.sort(COURSES, "title"));
    }

    @Test
    void anOrderingWrittenOnlyInThisTestWorksWithoutChangingTheCatalog() {
        Comparator<Course> shortestTitleFirst = comparingInt((Course c) -> c.title().length()).thenComparing(Course::code);
        assertThat(codes(new CourseCatalog(COURSES).sorted(shortestTitleFirst)))
                .containsExactly("MATH119", "CENG315", "CENG101", "CENG213", "CENG242");
    }

    @Test
    void sortingDoesNotChangeTheCatalog() {
        var catalog = new CourseCatalog(COURSES);
        catalog.sorted(comparing(Course::title));
        assertThat(catalog.courses()).isEqualTo(COURSES);
    }

    @Test
    void courseKnowsItsDepartmentAndValidates() {
        assertThat(new Course("CENG101", "Programming I", 6).department()).isEqualTo("CENG");
        assertThatIllegalArgumentException().isThrownBy(() -> new Course("CENG101", "Programming I", 0));
    }

    @Test
    void demoPrintsBothVersions() {
        assertThat(Console.capture(() -> CourseSortDemo.main(new String[0]))).isEqualTo("""
                == before: switch on a sort-key string ==
                code: CENG101, CENG213, CENG242, CENG315, MATH119
                credits: CENG315, CENG213, MATH119, CENG101, CENG242
                title: CENG315, MATH119, CENG213, CENG101, CENG242
                == after: orderings are Comparators ==
                code: CENG101, CENG213, CENG242, CENG315, MATH119
                credits: CENG315, CENG213, MATH119, CENG101, CENG242
                title: CENG315, MATH119, CENG213, CENG101, CENG242
                department, then credits: CENG315, CENG213, CENG101, CENG242, MATH119 (new ordering, catalog unchanged)
                """);
    }
}
