package io.github.aliturgutbozkurt.patterns.m01.examples.lsp;

import io.github.aliturgutbozkurt.patterns.m01.examples.lsp.accounts.after.Account;
import io.github.aliturgutbozkurt.patterns.m01.examples.lsp.accounts.after.BillPayer;
import io.github.aliturgutbozkurt.patterns.m01.examples.lsp.accounts.after.CheckingAccount;
import io.github.aliturgutbozkurt.patterns.m01.examples.lsp.accounts.after.FixedDepositAccount;
import io.github.aliturgutbozkurt.patterns.m01.examples.lsp.accounts.after.Withdrawable;
import java.math.BigDecimal;
import java.util.List;

/** Run: {@code java modules/m01-oop-solid-uml/src/main/java/io/github/aliturgutbozkurt/patterns/m01/examples/lsp/AccountDemo.java} */
public final class AccountDemo {

    private AccountDemo() {}

    public static void main(String[] args) {
        var bill = new BigDecimal("40.00");

        System.out.println("== before: FixedDepositAccount extends Account ==");
        var oldChecking = new io.github.aliturgutbozkurt.patterns.m01.examples.lsp.accounts.before.Account(
                "C-1", new BigDecimal("100"));
        System.out.println("checking pays " + bill + " -> balance "
                + io.github.aliturgutbozkurt.patterns.m01.examples.lsp.accounts.before.BillPayer.pay(oldChecking, bill));
        var oldDeposit = new io.github.aliturgutbozkurt.patterns.m01.examples.lsp.accounts.before.FixedDepositAccount(
                "F-1", new BigDecimal("1000"));
        try {
            io.github.aliturgutbozkurt.patterns.m01.examples.lsp.accounts.before.BillPayer.pay(oldDeposit, bill);
        } catch (UnsupportedOperationException e) {
            System.out.println("fixed deposit pays " + bill + " -> UnsupportedOperationException: " + e.getMessage());
        }

        System.out.println("== after: only Withdrawable accounts can pay bills ==");
        var checking = new CheckingAccount("C-1", new BigDecimal("100"));
        var deposit = new FixedDepositAccount("F-1", new BigDecimal("1000"));
        System.out.println("checking pays " + bill + " -> balance " + BillPayer.pay(checking, bill));
        // BillPayer.pay(deposit, bill);  // does not compile: FixedDepositAccount is not Withdrawable
        System.out.println("fixed deposit is Withdrawable? " + (((Account) deposit) instanceof Withdrawable));
        BigDecimal total = List.<Account>of(checking, deposit).stream()
                .map(Account::balance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        System.out.println("total balance: " + total);
    }
}
