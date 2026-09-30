package io.github.aliturgutbozkurt.patterns.m08.examples.state.vending;

/**
 * Concrete state: stocked and waiting for a coin. A coin moves the machine to {@link HasCreditState}.
 *
 * @see "m08 lesson, section State — Classic Java"
 */
public final class IdleState implements VendingState {

    @Override
    public String name() {
        return "Idle";
    }

    @Override
    public String insertCoin(VendingMachine machine, int cents) {
        machine.changeState(new HasCreditState(cents));
        return "credit " + cents;
    }

    @Override
    public String select(VendingMachine machine, String slot) {
        return "insert coins first";
    }

    @Override
    public int refund(VendingMachine machine) {
        return 0;
    }

    @Override
    public String restock(VendingMachine machine, String slot, int count) {
        return machine.addStock(slot, count);
    }
}
