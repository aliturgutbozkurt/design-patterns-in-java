package io.github.aliturgutbozkurt.patterns.m08.examples.state;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import io.github.aliturgutbozkurt.patterns.m08.examples.state.vending.Dispensed;
import io.github.aliturgutbozkurt.patterns.m08.examples.state.vending.Product;
import io.github.aliturgutbozkurt.patterns.m08.examples.state.vending.VendingMachine;
import io.github.aliturgutbozkurt.patterns.m08.support.Console;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VendingMachineTest {

    private static final Product COLA = new Product("Cola", 150);
    private static final Product CHIPS = new Product("Chips", 120);

    private VendingMachine machine;

    @BeforeEach
    void stockedMachine() {
        machine = new VendingMachine(Map.of("A1", COLA, "A2", CHIPS));
        machine.restock("A1", 2);
        machine.restock("A2", 1);
    }

    @Test
    void newMachineWithoutStockIsSoldOut() {
        assertThat(new VendingMachine(Map.of("A1", COLA)).stateName()).isEqualTo("SoldOut");
    }

    @Test
    void coinInIdleMovesToHasCredit() {
        assertThat(machine.stateName()).isEqualTo("Idle");
        assertThat(machine.insertCoin(100)).isEqualTo("credit 100");
        assertThat(machine.stateName()).isEqualTo("HasCredit");
        assertThat(machine.insertCoin(50)).isEqualTo("credit 150");
    }

    @Test
    void selectingWithEnoughCreditDispensesExactChangeAndReturnsToIdle() {
        machine.insertCoin(200);
        assertThat(machine.select("A1")).isEqualTo("dispensed Cola, change 50");
        assertThat(machine.tray()).containsExactly(new Dispensed(COLA, 50));
        assertThat(machine.stateName()).isEqualTo("Idle");
        assertThat(machine.refund()).isZero();
    }

    @Test
    void notEnoughCreditNamesTheMissingAmountAndKeepsTheState() {
        machine.insertCoin(100);
        assertThat(machine.select("A1")).isEqualTo("insert 50 more for Cola");
        assertThat(machine.stateName()).isEqualTo("HasCredit");
        assertThat(machine.tray()).isEmpty();
        assertThat(machine.refund()).isEqualTo(100);
    }

    @Test
    void selectInIdleIsRefused() {
        assertThat(machine.select("A1")).isEqualTo("insert coins first");
        assertThat(machine.stateName()).isEqualTo("Idle");
        assertThat(machine.tray()).isEmpty();
    }

    @Test
    void refundReturnsTheWholeCreditAndGoesIdle() {
        machine.insertCoin(100);
        machine.insertCoin(20);
        assertThat(machine.refund()).isEqualTo(120);
        assertThat(machine.stateName()).isEqualTo("Idle");
        assertThat(machine.refund()).isZero();
    }

    @Test
    void anEmptySlotIsRefusedWhileOthersAreStocked() {
        machine.insertCoin(200);
        machine.select("A2");
        machine.insertCoin(200);
        assertThat(machine.select("A2")).isEqualTo("Chips is sold out, choose another");
        assertThat(machine.stateName()).isEqualTo("HasCredit");
    }

    @Test
    void sellingTheLastItemOfTheLastStockedSlotMovesToSoldOut() {
        machine.insertCoin(150);
        machine.select("A1");
        machine.insertCoin(120);
        machine.select("A2");
        assertThat(machine.stateName()).isEqualTo("Idle");
        machine.insertCoin(150);
        assertThat(machine.select("A1")).isEqualTo("dispensed Cola, change 0");
        assertThat(machine.stateName()).isEqualTo("SoldOut");
    }

    @Test
    void soldOutReturnsInsertedCoinsAndRestockLeavesSoldOut() {
        var empty = new VendingMachine(Map.of("A1", COLA));
        assertThat(empty.insertCoin(100)).isEqualTo("sold out, returned 100");
        assertThat(empty.stateName()).isEqualTo("SoldOut");
        assertThat(empty.refund()).isZero();
        assertThat(empty.select("A1")).isEqualTo("sold out");
        assertThat(empty.restock("A1", 3)).isEqualTo("restocked 3 x Cola in A1");
        assertThat(empty.stateName()).isEqualTo("Idle");
    }

    @Test
    void invalidArgumentsAreRejectedByTheContext() {
        assertThatIllegalArgumentException().isThrownBy(() -> machine.insertCoin(0))
                .withMessage("coin must be positive: 0");
        assertThatIllegalArgumentException().isThrownBy(() -> machine.select("Z9"))
                .withMessage("unknown slot: Z9");
        assertThatIllegalArgumentException().isThrownBy(() -> machine.restock("A1", 0))
                .withMessage("count must be positive: 0");
        assertThatIllegalArgumentException().isThrownBy(() -> new Product("Cola", 0));
    }

    @Test
    void aScriptedSessionVisitsTheExpectedStates() {
        var names = new ArrayList<String>();
        var fresh = new VendingMachine(Map.of("A1", COLA));
        names.add(fresh.stateName());
        fresh.restock("A1", 1);
        names.add(fresh.stateName());
        fresh.insertCoin(100);
        names.add(fresh.stateName());
        fresh.select("A1");
        names.add(fresh.stateName());
        fresh.insertCoin(100);
        names.add(fresh.stateName());
        fresh.select("A1");
        names.add(fresh.stateName());
        fresh.insertCoin(100);
        names.add(fresh.stateName());
        assertThat(names).isEqualTo(List.of("SoldOut", "Idle", "HasCredit", "HasCredit", "HasCredit", "SoldOut",
                "SoldOut"));
    }

    @Test
    void demoPrintsEveryStepWithTheStateBeforeAndAfter() {
        assertThat(Console.capture(() -> VendingMachineDemo.main(new String[0]))).isEqualTo("""
                before     action         after      message
                SoldOut    insert 100     SoldOut    sold out, returned 100
                SoldOut    restock A1 2   Idle       restocked 2 x Cola in A1
                Idle       restock A2 1   Idle       restocked 1 x Chips in A2
                Idle       select A1      Idle       insert coins first
                Idle       insert 100     HasCredit  credit 100
                HasCredit  select A1      HasCredit  insert 50 more for Cola
                HasCredit  insert 100     HasCredit  credit 200
                HasCredit  select A1      Idle       dispensed Cola, change 50
                Idle       insert 200     HasCredit  credit 200
                HasCredit  refund         Idle       refunded 200
                Idle       insert 120     HasCredit  credit 120
                HasCredit  select A2      Idle       dispensed Chips, change 0
                Idle       insert 150     HasCredit  credit 150
                HasCredit  select A1      SoldOut    dispensed Cola, change 0
                tray: [Cola (change 50), Chips (change 0), Cola (change 0)]
                """);
    }
}
