package io.github.aliturgutbozkurt.patterns.m05.examples.composite;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.aliturgutbozkurt.patterns.m05.examples.composite.orgchart.Designer;
import io.github.aliturgutbozkurt.patterns.m05.examples.composite.orgchart.Employee;
import io.github.aliturgutbozkurt.patterns.m05.examples.composite.orgchart.Engineer;
import io.github.aliturgutbozkurt.patterns.m05.examples.composite.orgchart.Manager;
import io.github.aliturgutbozkurt.patterns.m05.support.Console;
import org.junit.jupiter.api.Test;

class OrgChartTest {

    private final Manager cto = new Manager("Grace", 9000);
    private final Manager lead = new Manager("Alan", 8000);
    private final Engineer ada = new Engineer("Ada", 7000);
    private final Engineer linus = new Engineer("Linus", 6500);
    private final Designer dieter = new Designer("Dieter", 6000);

    OrgChartTest() {
        lead.add(ada);
        lead.add(linus);
        cto.add(lead);
        cto.add(dieter);
    }

    @Test
    void leafAndCompositeAnswerTheSameQuestions() {
        Employee single = ada;
        Employee department = lead;
        assertThat(single.salary()).isEqualTo(7000);
        assertThat(single.headcount()).isEqualTo(1);
        assertThat(department.salary()).isEqualTo(8000 + 7000 + 6500);
        assertThat(department.headcount()).isEqualTo(3);
    }

    @Test
    void departmentSalaryCostSumsTheWholeSubtree() {
        assertThat(cto.salary()).isEqualTo(9000 + 8000 + 7000 + 6500 + 6000);
        assertThat(cto.ownSalary()).isEqualTo(9000);
    }

    @Test
    void headcountIncludesTheManager() {
        assertThat(cto.headcount()).isEqualTo(5);
        assertThat(new Manager("Solo", 5000).headcount()).isEqualTo(1);
    }

    @Test
    void removeShrinksSalaryAndHeadcount() {
        assertThat(lead.remove(linus)).isTrue();
        assertThat(lead.remove(linus)).isFalse();
        assertThat(cto.headcount()).isEqualTo(4);
        assertThat(cto.salary()).isEqualTo(9000 + 8000 + 7000 + 6000);
    }

    @Test
    void rejectsCycles() {
        assertThatIllegalArgumentException().isThrownBy(() -> lead.add(cto))
                .withMessage("Grace cannot report to Alan: that would create a cycle");
        assertThatIllegalArgumentException().isThrownBy(() -> cto.add(cto))
                .withMessage("Grace cannot report to Grace: that would create a cycle");
        assertThat(lead.headcount()).isEqualTo(3);
    }

    @Test
    void rejectsInvalidEmployees() {
        assertThatIllegalArgumentException().isThrownBy(() -> new Engineer(" ", 1000));
        assertThatIllegalArgumentException().isThrownBy(() -> new Designer("Eve", -1));
        assertThatIllegalArgumentException().isThrownBy(() -> new Manager("Bob", -1));
        assertThatThrownBy(() -> lead.add(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void printGivesTheIndentedChart() {
        assertThat(cto.print(0)).isEqualTo("""
                Grace (Manager) 9000
                  Alan (Manager) 8000
                    Ada (Engineer) 7000
                    Linus (Engineer) 6500
                  Dieter (Designer) 6000
                """);
        assertThat(ada.print(2)).isEqualTo("    Ada (Engineer) 7000\n");
    }

    @Test
    void reportsIsAnUnmodifiableView() {
        assertThat(lead.reports()).containsExactly(ada, linus);
        assertThatThrownBy(() -> lead.reports().add(dieter)).isInstanceOf(UnsupportedOperationException.class);
        lead.remove(ada);
        assertThat(lead.reports()).containsExactly(linus);
    }

    @Test
    void demoPrintsTheChartAndDepartmentTotals() {
        assertThat(Console.capture(() -> OrgChartDemo.main(new String[0]))).isEqualTo("""
                Grace (Manager) 9000
                  Alan (Manager) 8000
                    Ada (Engineer) 7000
                    Linus (Engineer) 6500
                  Dieter (Designer) 6000
                company: headcount 5, salary cost 36500
                engineering: headcount 3, salary cost 21500
                Ada alone: headcount 1, salary cost 7000
                after Linus leaves: headcount 4, salary cost 30000
                rejected: Grace cannot report to Alan: that would create a cycle
                """);
    }
}
