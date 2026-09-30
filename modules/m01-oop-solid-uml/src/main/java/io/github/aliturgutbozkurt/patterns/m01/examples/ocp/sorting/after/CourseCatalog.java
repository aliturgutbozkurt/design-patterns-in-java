package io.github.aliturgutbozkurt.patterns.m01.examples.ocp.sorting.after;

import io.github.aliturgutbozkurt.patterns.m01.examples.ocp.sorting.Course;
import java.util.Comparator;
import java.util.List;

/**
 * Open for extension through {@link Comparator}: callers compose any ordering; the catalog never changes.
 *
 * @see "m01 lesson, section OCP"
 */
public record CourseCatalog(List<Course> courses) {

    public CourseCatalog {
        courses = List.copyOf(courses);
    }

    /** A sorted copy; the catalog itself is unchanged. */
    public List<Course> sorted(Comparator<? super Course> order) {
        return courses.stream().sorted(order).toList();
    }
}
