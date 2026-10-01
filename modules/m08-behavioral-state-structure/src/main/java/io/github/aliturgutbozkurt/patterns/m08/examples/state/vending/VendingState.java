package io.github.aliturgutbozkurt.patterns.m08.examples.state.vending;

/**
 * State: one implementation per machine state. Every operation of the context is here, and each state decides both
 * the answer and the machine's next state.
 *
 * @see "m08 lesson, section State — Classic Java"
 */
public interface VendingState {

    /** The state's name as shown on the machine's display. */
    String name();

    String insertCoin(VendingMachine machine, int cents);

    String select(VendingMachine machine, String slot);

    /** Hands back the credit, in cents. */
    int refund(VendingMachine machine);

    String restock(VendingMachine machine, String slot, int count);
}
