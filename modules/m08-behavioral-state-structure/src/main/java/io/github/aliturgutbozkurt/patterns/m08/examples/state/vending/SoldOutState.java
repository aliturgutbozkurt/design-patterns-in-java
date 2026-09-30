package io.github.aliturgutbozkurt.patterns.m08.examples.state.vending;

/**
 * Concrete state: nothing left to sell. Coins come straight back; only a restock leaves this state.
 *
 * @see "m08 lesson, section State — Classic Java"
 */
public final class SoldOutState implements VendingState {

    @Override
    public String name() {
        return "SoldOut";
    }

    @Override
    public String insertCoin(VendingMachine machine, int cents) {
        return "sold out, returned " + cents;
    }

    @Override
    public String select(VendingMachine machine, String slot) {
        return "sold out";
    }

    @Override
    public int refund(VendingMachine machine) {
        return 0;
    }

    @Override
    public String restock(VendingMachine machine, String slot, int count) {
        String message = machine.addStock(slot, count);
        machine.changeState(new IdleState());
        return message;
    }
}
