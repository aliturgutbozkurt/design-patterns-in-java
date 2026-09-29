package io.github.aliturgutbozkurt.patterns.m02.examples.singleton;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.aliturgutbozkurt.patterns.m02.examples.singleton.testability.after.IdSource;
import io.github.aliturgutbozkurt.patterns.m02.examples.singleton.testability.after.OrderService;
import io.github.aliturgutbozkurt.patterns.m02.examples.singleton.testability.after.SequentialIds;
import io.github.aliturgutbozkurt.patterns.m02.support.Console;
import org.junit.jupiter.api.Test;

class SingletonTestabilityTest {

    static int number(String orderNumber) {
        return Integer.parseInt(orderNumber.substring("ORD-".length()));
    }

    /**
     * Documents the problem on purpose: two "independent" services share hidden global state, so this test cannot
     * even know which number comes first — it depends on what ran before it in the JVM.
     */
    @Test
    void beforeServicesShareAHiddenGlobalCounter() {
        var first = new io.github.aliturgutbozkurt.patterns.m02.examples.singleton.testability.before.OrderService();
        var second = new io.github.aliturgutbozkurt.patterns.m02.examples.singleton.testability.before.OrderService();
        String a = first.placeOrder();
        String b = second.placeOrder();
        assertThat(number(b)).isEqualTo(number(a) + 1);
    }

    @Test
    void afterAFixedIdSourceMakesTheResultPredictable() {
        IdSource fixed = () -> 42;
        assertThat(new OrderService(fixed).placeOrder()).isEqualTo("ORD-42");
    }

    @Test
    void afterSeparateSourcesDoNotInterfere() {
        var first = new OrderService(new SequentialIds());
        var second = new OrderService(new SequentialIds());
        assertThat(first.placeOrder()).isEqualTo("ORD-1");
        assertThat(second.placeOrder()).isEqualTo("ORD-1");
        assertThat(first.placeOrder()).isEqualTo("ORD-2");
    }

    @Test
    void afterSharingIsADecisionOfTheCaller() {
        var shared = new SequentialIds();
        assertThat(new OrderService(shared).placeOrder()).isEqualTo("ORD-1");
        assertThat(new OrderService(shared).placeOrder()).isEqualTo("ORD-2");
    }

    @Test
    void demoPrintsBothVersions() {
        assertThat(Console.capture(() -> SingletonTestabilityDemo.main(new String[0]))).isEqualTo("""
                == before: OrderService calls SequenceGenerator.getInstance() ==
                a second OrderService continues the first one's numbers: true
                == after: the IdSource is injected ==
                shared on purpose: ORD-1, ORD-2
                separate sources: ORD-1, ORD-1
                fixed for a test: ORD-42
                """);
    }
}
