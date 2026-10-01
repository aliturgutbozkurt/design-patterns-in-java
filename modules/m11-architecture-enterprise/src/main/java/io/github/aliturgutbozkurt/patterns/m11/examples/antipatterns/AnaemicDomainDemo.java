package io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns;

import io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.anaemic.after.Money;
import io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.anaemic.before.AccountService;
import java.math.BigDecimal;

/** Run: {@code java modules/m11-architecture-enterprise/src/main/java/io/github/aliturgutbozkurt/patterns/m11/examples/antipatterns/AnaemicDomainDemo.java} */
public final class AnaemicDomainDemo {

    private AnaemicDomainDemo() {}

    public static void main(String[] args) {
        var service = new AccountService();
        var anaemic = new io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.anaemic.before.Account();
        anaemic.setOwner("ada");
        service.deposit(anaemic, 100_00);
        service.withdraw(anaemic, 30_00);
        service.deposit(anaemic, 5_00);
        System.out.println("before: balance " + BigDecimal.valueOf(anaemic.getBalanceCents(), 2));

        var rich = new io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.anaemic.after.Account("ada");
        rich.deposit(Money.of("100.00"));
        rich.withdraw(Money.of("30.00"));
        rich.deposit(Money.of("5.00"));
        System.out.println("after:  balance " + rich.balance());

        anaemic.setBalanceCents(-50_00); // nothing stops a caller from skipping the service
        System.out.println("before: setBalanceCents(-5000) accepted, balance " + BigDecimal.valueOf(anaemic.getBalanceCents(), 2));
        try {
            rich.withdraw(Money.of("80.00"));
        } catch (IllegalArgumentException e) {
            System.out.println("after:  withdraw 80.00 -> " + e.getMessage() + ", balance still " + rich.balance());
        }
        rich.close();
        try {
            rich.deposit(Money.of("1.00"));
        } catch (IllegalStateException e) {
            System.out.println("after:  deposit to a closed account -> " + e.getMessage());
        }
    }
}
