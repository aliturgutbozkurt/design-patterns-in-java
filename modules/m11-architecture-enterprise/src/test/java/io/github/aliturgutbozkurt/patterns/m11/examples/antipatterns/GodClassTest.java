package io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.godclass.after.CheckoutFacade;
import io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.godclass.after.CheckoutRequest;
import io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.godclass.after.ConfirmationMailer;
import io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.godclass.after.OrderRepository;
import io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.godclass.after.OrderValidator;
import io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.godclass.after.PricingPolicy;
import io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.godclass.before.OrderManager;
import io.github.aliturgutbozkurt.patterns.m11.support.Console;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class GodClassTest {

    private final OrderManager before = new OrderManager();
    private final ConfirmationMailer mailer = new ConfirmationMailer();
    private final List<String> log = new ArrayList<>();
    private final CheckoutFacade after = new CheckoutFacade(OrderValidator.standard(), new OrderRepository(), mailer,
            log::add);

    /** Characterization test: written against the god class first, then run unchanged against the refactoring. */
    @ParameterizedTest(name = "{0} {1} {2} x {3}")
    @CsvSource(delimiter = '|', textBlock = """
            alice | REGULAR  |  3 |  3000 | OK order-1 90.00
            bob   | VIP      |  2 |  5000 | OK order-1 90.00
            carol | EMPLOYEE |  1 | 10000 | OK order-1 70.00
            dave  | VIP      | 10 |  1000 | OK order-1 85.50
            erin  | REGULAR  |  9 |  1000 | OK order-1 90.00
            ''    | REGULAR  |  1 |   100 | REJECTED missing customer
            frank | REGULAR  |  0 |   100 | REJECTED invalid quantity
            gina  | REGULAR  |  1 |     0 | REJECTED invalid price
            hank  | GOLD     |  1 |   100 | REJECTED unknown customer type: GOLD
            """)
    void beforeAndAfterGiveIdenticalResultsEmailsAndLogs(String customer, String type, int quantity, long price,
                                                         String expected) {
        String old = before.checkout(customer, type, quantity, price);
        String refactored = after.checkout(new CheckoutRequest(customer, type, quantity, price));
        assertThat(old).isEqualTo(expected);
        assertThat(refactored).isEqualTo(old);
        assertThat(mailer.sent()).isEqualTo(before.sentEmails());
        assertThat(log).isEqualTo(before.logLines());
    }

    @Test
    void sequencesOfCheckoutsStayIdentical() {
        for (int i = 1; i <= 3; i++) {
            assertThat(after.checkout(new CheckoutRequest("c" + i, "VIP", i, 1999)))
                    .isEqualTo(before.checkout("c" + i, "VIP", i, 1999));
        }
        assertThat(mailer.sent()).isEqualTo(before.sentEmails()).hasSize(3);
    }

    @ParameterizedTest
    @ValueSource(classes = {CheckoutFacade.class, OrderValidator.class, PricingPolicy.class, OrderRepository.class,
            ConfirmationMailer.class})
    void everyAfterClassIsSmall(Class<?> type) {
        assertThat(publicMethods(type)).isLessThanOrEqualTo(5);
        assertThat(type.getConstructors())
                .allSatisfy(c -> assertThat(c.getParameterCount()).isLessThanOrEqualTo(4));
    }

    @Test
    void theGodClassHasMoreThanTenPublicMethods() {
        assertThat(publicMethods(OrderManager.class)).isGreaterThan(10); // documents the smell
    }

    @Test
    void demoPrintsBothVersionsSideBySide() {
        assertThat(Console.capture(() -> GodClassDemo.main(new String[0]))).isEqualTo("""
                alice: before OK order-1 90.00 | after OK order-1 90.00
                bob: before OK order-2 85.50 | after OK order-2 85.50
                carol: before OK order-3 70.00 | after OK order-3 70.00
                dave: before REJECTED unknown customer type: GOLD | after REJECTED unknown customer type: GOLD
                erin: before REJECTED invalid quantity | after REJECTED invalid quantity
                same e-mails: true, same log: true
                public methods: OrderManager 12
                after the split: CheckoutFacade 1, OrderValidator 2, PricingPolicy 2, OrderRepository 4, ConfirmationMailer 2
                """);
    }

    private static long publicMethods(Class<?> type) {
        return Arrays.stream(type.getDeclaredMethods())
                .filter(m -> Modifier.isPublic(m.getModifiers()) && !m.isSynthetic())
                .count();
    }
}
