package io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.transfer.config;

import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.transfer.domain.AccountId;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.transfer.domain.Money;
import java.util.List;

/** Run: {@code java modules/m11-architecture-enterprise/src/main/java/io/github/aliturgutbozkurt/patterns/m11/examples/hexagonal/transfer/config/TransferDemo.java} */
public final class TransferDemo {

    private TransferDemo() {}

    public static void main(String[] args) {
        TransferApp app = TransferApp.inMemory(Money.of("1000.00"));
        app.accounts().open(new AccountId("A-1"), Money.of("100.00"));
        app.accounts().open(new AccountId("A-2"), Money.of("20.00"));

        for (String line : List.of("transfer A-1 A-2 25.00", "transfer A-2 A-1 500.00", "transfer A-1 A-1 1.00",
                "transfer A-1 A-9 1.00", "transfer A-1 A-2 5000.00", "send money")) {
            System.out.println("> " + line);
            System.out.println(app.controller().handle(line));
        }
        System.out.println("balances: " + app.accounts().balances());
    }
}
