package io.github.aliturgutbozkurt.patterns.m08.examples.state.vending;

/**
 * Concrete state: coins are in. The credit is the state's own data, so it cannot exist in any other state.
 *
 * @see "m08 lesson, section State — Classic Java"
 */
public final class HasCreditState implements VendingState {

    private final int credit;

    public HasCreditState(int credit) {
        if (credit <= 0) {
            throw new IllegalArgumentException("credit must be positive: " + credit);
        }
        this.credit = credit;
    }

    @Override
    public String name() {
        return "HasCredit";
    }

    @Override
    public String insertCoin(VendingMachine machine, int cents) {
        machine.changeState(new HasCreditState(credit + cents));
        return "credit " + (credit + cents);
    }

    @Override
    public String select(VendingMachine machine, String slot) {
        Product product = machine.product(slot);
        if (machine.stock(slot) == 0) {
            return product.name() + " is sold out, choose another";
        }
        if (credit < product.priceCents()) {
            return "insert " + (product.priceCents() - credit) + " more for " + product.name();
        }
        int change = credit - product.priceCents();
        machine.dispense(slot, new Dispensed(product, change));
        machine.changeState(machine.hasStock() ? new IdleState() : new SoldOutState());
        return "dispensed " + product.name() + ", change " + change;
    }

    @Override
    public int refund(VendingMachine machine) {
        machine.changeState(new IdleState());
        return credit;
    }

    @Override
    public String restock(VendingMachine machine, String slot, int count) {
        return machine.addStock(slot, count);
    }
}
