package io.github.aliturgutbozkurt.patterns.m05.examples.composite.orgchart;

/**
 * Composite, classic GoF form: the component interface that a single person and a whole department both implement,
 * so a client can ask either one for its salary cost or headcount.
 *
 * @see "m05 lesson, section Composite"
 */
public interface Employee {

    String name();

    /** Salary cost: for a person their own salary, for a manager the whole department including the manager. */
    int salary();

    /** Number of people: 1 for a person, the whole subtree for a manager. */
    int headcount();

    /** Renders this node and everything below it, two spaces of indent per level, one line each. */
    String print(int indent);
}
