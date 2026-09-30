package io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.anaemic.after.Account;
import io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.anaemic.after.Money;
import io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.anaemic.before.AccountService;
import io.github.aliturgutbozkurt.patterns.m11.support.Console;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.Test;

class AnaemicDomainTest {

    private final Account rich = new Account("ada");

    /** Same valid operations → same balances, in both models. Operations: +deposit, -withdraw (cents). */
    @ParameterizedTest
    @CsvSource(delimiter = '|', textBlock = """
            +10000 -3000 +500      | 7500
            +1                     | 1
            +5000 -5000            | 0
            +250 +250 -100 -400    | 0
            """)
    void sameValidScenariosGiveSameBalances(String operations, long expectedCents) {
        var service = new AccountService();
        var anaemic = new io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.anaemic.before.Account();
        for (String op : operations.split(" ")) {
            long cents = Long.parseLong(op.substring(1));
            if (op.startsWith("+")) {
                service.deposit(anaemic, cents);
                rich.deposit(new Money(cents));
            } else {
                service.withdraw(anaemic, cents);
                rich.withdraw(new Money(cents));
            }
        }
        assertThat(anaemic.getBalanceCents()).isEqualTo(expectedCents);
        assertThat(rich.balance()).isEqualTo(new Money(expectedCents));
    }

    @Test
    void beforeAcceptsANegativeBalanceThroughItsSetter() {
        var anaemic = new io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.anaemic.before.Account();
        anaemic.setBalanceCents(-50);
        assertThat(anaemic.getBalanceCents()).isEqualTo(-50); // documents the hole
    }

    @Test
    void afterRejectsOverdraftAndLeavesTheBalanceUnchanged() {
        rich.deposit(Money.of("10.00"));
        assertThatIllegalArgumentException().isThrownBy(() -> rich.withdraw(Money.of("10.01")))
                .withMessage("insufficient funds");
        assertThat(rich.balance()).isEqualTo(Money.of("10.00"));
    }

    @Test
    void afterRejectsNegativeAndZeroAmounts() {
        rich.deposit(Money.of("10.00"));
        assertThatIllegalArgumentException().isThrownBy(() -> rich.withdraw(new Money(-50)));
        assertThatIllegalArgumentException().isThrownBy(() -> rich.deposit(Money.ZERO))
                .withMessage("amount must be positive");
        assertThat(rich.balance()).isEqualTo(Money.of("10.00"));
    }

    @Test
    void afterRejectsDepositsToAClosedAccount() {
        rich.deposit(Money.of("10.00"));
        rich.close();
        assertThatIllegalStateException().isThrownBy(() -> rich.deposit(Money.of("1.00")))
                .withMessage("account is closed");
        assertThat(rich.balance()).isEqualTo(Money.of("10.00"));
    }

    @Test
    void afterAccountHasNoPublicSetters() {
        assertThat(Arrays.stream(Account.class.getDeclaredMethods())
                .filter(m -> Modifier.isPublic(m.getModifiers()))
                .map(m -> m.getName()))
                .noneMatch(name -> name.startsWith("set"));
    }

    @Test
    void demoPrintsTheHoleAndTheGuards() {
        assertThat(Console.capture(() -> AnaemicDomainDemo.main(new String[0]))).isEqualTo("""
                before: balance 75.00
                after:  balance 75.00
                before: setBalanceCents(-5000) accepted, balance -50.00
                after:  withdraw 80.00 -> insufficient funds, balance still 75.00
                after:  deposit to a closed account -> account is closed
                """);
    }
}
