package io.github.aliturgutbozkurt.patterns.m09.solutions.ex01;

import io.github.aliturgutbozkurt.patterns.m09.exercises.ex01.Ex01Contract;
import io.github.aliturgutbozkurt.patterns.m09.exercises.ex01.Payroll;
import io.github.aliturgutbozkurt.patterns.m09.exercises.ex01.legacy.EmployeeVisitor;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class Ex01SolutionTest extends Ex01Contract {

    @Override
    protected Payroll newPayroll() {
        return new DataOrientedPayroll();
    }

    @Test
    void referenceSolutionDeclaresNoVisitor() {
        assertThat(DataOrientedPayroll.class.getInterfaces()).doesNotContain(EmployeeVisitor.class);
        assertThat(Arrays.stream(DataOrientedPayroll.class.getDeclaredClasses()))
                .noneMatch(EmployeeVisitor.class::isAssignableFrom);
    }
}
