package io.github.aliturgutbozkurt.patterns.m05.examples.composite;

import io.github.aliturgutbozkurt.patterns.m05.examples.composite.orgchart.Designer;
import io.github.aliturgutbozkurt.patterns.m05.examples.composite.orgchart.Employee;
import io.github.aliturgutbozkurt.patterns.m05.examples.composite.orgchart.Engineer;
import io.github.aliturgutbozkurt.patterns.m05.examples.composite.orgchart.Manager;

/** Run: {@code java modules/m05-structural-composition/src/main/java/io/github/aliturgutbozkurt/patterns/m05/examples/composite/OrgChartDemo.java} */
public final class OrgChartDemo {

    private OrgChartDemo() {}

    public static void main(String[] args) {
        var grace = new Manager("Grace", 9000);
        var alan = new Manager("Alan", 8000);
        var ada = new Engineer("Ada", 7000);
        var linus = new Engineer("Linus", 6500);
        alan.add(ada);
        alan.add(linus);
        grace.add(alan);
        grace.add(new Designer("Dieter", 6000));

        System.out.print(grace.print(0));
        report("company", grace);
        report("engineering", alan);
        report("Ada alone", ada);

        alan.remove(linus);
        report("after Linus leaves", grace);
        try {
            alan.add(grace);
        } catch (IllegalArgumentException e) {
            System.out.println("rejected: " + e.getMessage());
        }
    }

    // The client treats one person and a whole department the same way.
    private static void report(String label, Employee employee) {
        System.out.println(label + ": headcount " + employee.headcount() + ", salary cost " + employee.salary());
    }
}
