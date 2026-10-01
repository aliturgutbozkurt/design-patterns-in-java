package io.github.aliturgutbozkurt.patterns.m09.examples.optional.directory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.aliturgutbozkurt.patterns.m09.examples.optional.directory.Referral.Direct;
import io.github.aliturgutbozkurt.patterns.m09.examples.optional.directory.Referral.ReferredBy;
import io.github.aliturgutbozkurt.patterns.m09.support.Console;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Executable;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class OptionalDoneRightTest {

    private final CustomerDirectory directory = CustomerDirectory.sample();

    @Test
    void findChainsGiveExactNames() {
        assertThat(directory.findByEmail("ada@example.com").map(Customer::name)).contains("Ada");
        assertThat(directory.findByEmail("  ADA@example.com ").map(Customer::name)).contains("Ada");
        assertThat(directory.findByEmail("nobody@example.com")).isEmpty();
        assertThat(directory.findById(new CustomerId("C-2")).map(Customer::name)).contains("Grace");
        assertThat(directory.findById(new CustomerId("C-9"))).isEmpty();
    }

    @Test
    void referralChainFallsBackToNobody() {
        assertThat(directory.referrerName("grace@example.com")).isEqualTo("Ada");
        assertThat(directory.referrerName("ada@example.com")).isEqualTo("nobody");
        assertThat(directory.referrerName("linus@example.com")).isEqualTo("nobody");
        assertThat(directory.referrerName("unknown@example.com")).isEqualTo("nobody");
    }

    @Test
    void absenceInsideDataIsASealedTypeAndReferrerIsAConvenienceView() {
        var ada = directory.findById(new CustomerId("C-1")).orElseThrow();
        var grace = directory.findById(new CustomerId("C-2")).orElseThrow();
        assertThat(ada.referral()).isEqualTo(new Direct());
        assertThat(grace.referral()).isEqualTo(new ReferredBy(new CustomerId("C-1")));
        assertThat(ada.referrer()).isEmpty();
        assertThat(grace.referrer()).contains(new CustomerId("C-1"));
    }

    @Test
    void orElseEvaluatesItsArgumentEvenWhenAValueIsPresentButOrElseGetDoesNot() {
        var calls = new AtomicInteger();
        Optional<String> present = Optional.of("Ada");
        assertThat(present.orElse(expensive(calls))).isEqualTo("Ada");
        assertThat(calls).hasValue(1);
        assertThat(present.orElseGet(() -> expensive(calls))).isEqualTo("Ada");
        assertThat(calls).hasValue(1);
    }

    @Test
    void orElseThrowCarriesAMessage() {
        assertThatThrownBy(() -> directory.require(new CustomerId("C-9")))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("no customer with id C-9");
    }

    @Test
    void optionalStreamDropsEmptiesInOrder() {
        assertThat(directory.namesOf(List.of("linus@example.com", "x@example.com", "ada@example.com")))
                .containsExactly("Linus", "Ada");
    }

    @Test
    void orTriesASecondLookup() {
        assertThat(directory.findByEmailOrId("C-3").map(Customer::name)).contains("Linus");
        assertThat(directory.findByEmailOrId("ada@example.com").map(Customer::name)).contains("Ada");
    }

    @Test
    void noOrdersIsAnEmptyListNotAnEmptyOptional() {
        assertThat(directory.ordersOf(new CustomerId("C-9"))).isEmpty();
        assertThat(directory.ordersOf(new CustomerId("C-1"))).extracting(OrderSummary::orderId)
                .containsExactly("A-1", "A-3");
    }

    @Test
    void optionalIsNotSerializable() {
        assertThat(Serializable.class.isAssignableFrom(Optional.class)).isFalse();
    }

    @Test
    void optionalIsUsedOnlyAsAReturnTypeInThisPackage() throws IOException, URISyntaxException {
        List<Class<?>> types = packageClasses();
        assertThat(types).contains(Customer.class, CustomerDirectory.class, Referral.class, ReferredBy.class);
        for (Class<?> type : types) {
            assertThat(type.getDeclaredFields()).as("fields of %s", type)
                    .noneMatch(f -> f.getType() == Optional.class);
            if (type.isRecord()) {
                assertThat(type.getRecordComponents()).as("components of %s", type)
                        .noneMatch(c -> c.getType() == Optional.class);
            }
            Stream.<Executable>concat(Arrays.stream(type.getDeclaredMethods()),
                            Arrays.stream(type.getDeclaredConstructors()))
                    .filter(m -> Modifier.isPublic(m.getModifiers()))
                    .forEach(m -> assertThat(m.getParameterTypes()).as("parameters of %s", m)
                            .doesNotContain(Optional.class));
        }
    }

    @Test
    void demoPrintsOptionalUsedWell() {
        assertThat(Console.capture(() -> OptionalDoneRightDemo.main(new String[0]))).isEqualTo("""
                -- find, then map
                findByEmail(ada@example.com)    -> Ada
                findByEmail(nobody@example.com) -> (not found)
                -- a chain of lookups: email -> referrer id -> customer -> name
                grace@example.com was referred by Ada
                ada@example.com was referred by nobody
                -- orElse vs. orElseGet
                orElse:    fallback computed 1 time(s)
                orElseGet: fallback computed 0 time(s)
                -- orElseThrow with a message
                require(C-9) -> NoSuchElementException: no customer with id C-9
                -- Optional::stream drops the misses
                [Linus, Ada]
                -- no orders is an empty list
                orders of C-3: []
                orders of C-1: [A-1, A-3]
                """);
    }

    private static String expensive(AtomicInteger calls) {
        calls.incrementAndGet();
        return "fallback";
    }

    /** Every class compiled into this example package, found on the class path. */
    private static List<Class<?>> packageClasses() throws IOException, URISyntaxException {
        String pkg = Customer.class.getPackageName();
        Path root = Path.of(Customer.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        try (Stream<Path> files = Files.list(root.resolve(pkg.replace('.', '/')))) {
            return files.map(p -> p.getFileName().toString())
                    .filter(name -> name.endsWith(".class"))
                    .map(name -> pkg + "." + name.substring(0, name.length() - ".class".length()))
                    .<Class<?>>map(OptionalDoneRightTest::load)
                    .toList();
        }
    }

    private static Class<?> load(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }
}
