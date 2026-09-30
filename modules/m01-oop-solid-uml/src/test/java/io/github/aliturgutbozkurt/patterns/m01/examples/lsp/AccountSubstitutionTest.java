package io.github.aliturgutbozkurt.patterns.m01.examples.lsp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.aliturgutbozkurt.patterns.m01.examples.lsp.accounts.after.Account;
import io.github.aliturgutbozkurt.patterns.m01.examples.lsp.accounts.after.BillPayer;
import io.github.aliturgutbozkurt.patterns.m01.examples.lsp.accounts.after.CheckingAccount;
import io.github.aliturgutbozkurt.patterns.m01.examples.lsp.accounts.after.FixedDepositAccount;
import io.github.aliturgutbozkurt.patterns.m01.examples.lsp.accounts.after.Withdrawable;
import io.github.aliturgutbozkurt.patterns.m01.support.Console;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class AccountSubstitutionTest {

    static BigDecimal amount(String value) {
        return new BigDecimal(value);
    }

    @Test
    void beforeClientWorksForAPlainAccount() {
        var account = new io.github.aliturgutbozkurt.patterns.m01.examples.lsp.accounts.before.Account("C-1", amount("100"));
        assertThat(io.github.aliturgutbozkurt.patterns.m01.examples.lsp.accounts.before.BillPayer.pay(account, amount("40")))
                .isEqualTo(amount("60.00"));
    }

    /** Documents the violation on purpose: a subtype strengthens the precondition of {@code withdraw}. */
    @Test
    void beforeFixedDepositBreaksTheClient() {
        var deposit = new io.github.aliturgutbozkurt.patterns.m01.examples.lsp.accounts.before.FixedDepositAccount(
                "F-1", amount("1000"));
        assertThatThrownBy(() -> io.github.aliturgutbozkurt.patterns.m01.examples.lsp.accounts.before.BillPayer
                .pay(deposit, amount("40")))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("no withdrawals from a fixed deposit");
    }

    @Test
    void afterBillPayerWorksForEveryWithdrawable() {
        var checking = new CheckingAccount("C-1", amount("100"));
        assertThat(BillPayer.pay(checking, amount("40"))).isEqualTo(amount("60.00"));
        Withdrawable wallet = new Withdrawable() {
            private BigDecimal balance = amount("10");
            @Override public void withdraw(BigDecimal value) { balance = balance.subtract(value); }
            @Override public BigDecimal balance() { return balance; }
        };
        assertThat(BillPayer.pay(wallet, amount("2.50"))).isEqualTo(amount("7.50"));
    }

    @Test
    void afterFixedDepositIsNotWithdrawable() {
        assertThat(Withdrawable.class.isAssignableFrom(FixedDepositAccount.class)).isFalse();
        assertThat(Withdrawable.class.isAssignableFrom(CheckingAccount.class)).isTrue();
    }

    @Test
    void afterBalanceNeverGoesNegative() {
        var checking = new CheckingAccount("C-1", amount("30"));
        assertThatIllegalStateException().isThrownBy(() -> checking.withdraw(amount("30.01")))
                .withMessage("insufficient funds: balance 30.00, requested 30.01");
        assertThat(checking.balance()).isEqualTo(amount("30.00"));
    }

    @Test
    void afterAllAccountsShareTheAccountContract() {
        List<Account> accounts = List.of(new CheckingAccount("C-1", amount("60")), new FixedDepositAccount("F-1", amount("1000")));
        accounts.forEach(account -> account.deposit(amount("5")));
        assertThat(accounts.stream().map(Account::balance).reduce(BigDecimal.ZERO, BigDecimal::add))
                .isEqualTo(amount("1070.00"));
    }

    @Test
    void afterRejectsNonPositiveAmounts() {
        var checking = new CheckingAccount("C-1", amount("30"));
        assertThatIllegalArgumentException().isThrownBy(() -> checking.withdraw(BigDecimal.ZERO));
        assertThatIllegalArgumentException().isThrownBy(() -> checking.deposit(amount("-1")));
        assertThatIllegalArgumentException().isThrownBy(() -> new FixedDepositAccount("F-1", amount("-1")));
    }

    @Test
    void demoShowsTheViolationAndTheFix() {
        assertThat(Console.capture(() -> AccountDemo.main(new String[0]))).isEqualTo("""
                == before: FixedDepositAccount extends Account ==
                checking pays 40.00 -> balance 60.00
                fixed deposit pays 40.00 -> UnsupportedOperationException: no withdrawals from a fixed deposit
                == after: only Withdrawable accounts can pay bills ==
                checking pays 40.00 -> balance 60.00
                fixed deposit is Withdrawable? false
                total balance: 1060.00
                """);
    }
}
