package io.github.aliturgutbozkurt.patterns.m09.examples.result.checkout;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import io.github.aliturgutbozkurt.patterns.m09.examples.result.checkout.CheckoutError.EmptyCart;
import io.github.aliturgutbozkurt.patterns.m09.examples.result.checkout.CheckoutError.InvalidCoupon;
import io.github.aliturgutbozkurt.patterns.m09.examples.result.checkout.CheckoutError.OutOfStock;
import io.github.aliturgutbozkurt.patterns.m09.examples.result.checkout.CheckoutError.PaymentDeclined;
import io.github.aliturgutbozkurt.patterns.m09.examples.result.core.Result;
import io.github.aliturgutbozkurt.patterns.m09.examples.result.core.Result.Err;
import io.github.aliturgutbozkurt.patterns.m09.examples.result.core.Result.Ok;
import io.github.aliturgutbozkurt.patterns.m09.support.Console;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.Test;

class CheckoutStylesTest {

    private static final CartItem MUGS = new CartItem("MUG-0001", 2, 12_50);
    private static final CartItem TEE = new CartItem("TEE-0002", 1, 20_00);

    private InMemoryInventory inventory;
    private ScriptedPaymentGateway gateway;

    private void freshFakes() {
        inventory = new InMemoryInventory(Map.of("MUG-0001", 5, "TEE-0002", 1));
        gateway = new ScriptedPaymentGateway(Set.of("card-declined"));
    }

    static Stream<Arguments> scenarios() {
        return Stream.of(
                Arguments.of("happy path", new CheckoutRequest(List.of(MUGS, TEE), "", "card-ok"),
                        "Receipt[paymentId=PAY-1, totalCents=4500]"),
                Arguments.of("coupon", new CheckoutRequest(List.of(MUGS, TEE), "SAVE10", "card-ok"),
                        "Receipt[paymentId=PAY-1, totalCents=4050]"),
                Arguments.of("empty cart", new CheckoutRequest(List.of(), "", "card-ok"), "EmptyCart"),
                Arguments.of("bad coupon", new CheckoutRequest(List.of(MUGS), "FREE", "card-ok"), "InvalidCoupon"),
                Arguments.of("short stock", new CheckoutRequest(List.of(new CartItem("TEE-0002", 2, 20_00)), "",
                        "card-ok"), "OutOfStock"),
                Arguments.of("declined", new CheckoutRequest(List.of(MUGS), "", "card-declined"), "PaymentDeclined"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("scenarios")
    void exceptionAndResultVersionsAgreeOnTheReceiptOrTheErrorKind(String name, CheckoutRequest request,
                                                                   String expected) {
        freshFakes();
        String byException;
        try {
            byException = new ExceptionCheckout(inventory, gateway).checkout(request).toString();
        } catch (CheckoutException e) {
            byException = e.getClass().getSimpleName();
        }
        freshFakes();
        String byResult = new ResultCheckout(inventory, gateway).checkout(request)
                .fold(Receipt::toString, error -> error.getClass().getSimpleName());
        assertThat(byException).as(name).isEqualTo(expected);
        assertThat(byResult).as(name).isEqualTo(expected);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("scenarios")
    void optionalVersionIsEmptyForEveryFailure(String name, CheckoutRequest request, String expected) {
        freshFakes();
        Optional<Receipt> receipt = new OptionalCheckout(inventory, gateway).checkout(request);
        assertThat(receipt.map(Receipt::toString).orElse("(empty)")).as(name)
                .isEqualTo(expected.startsWith("Receipt") ? expected : "(empty)");
    }

    @Test
    void theGatewayIsNotCalledWhenStockIsShort() {
        var request = new CheckoutRequest(List.of(new CartItem("TEE-0002", 2, 20_00)), "", "card-ok");
        freshFakes();
        assertThat(new ResultCheckout(inventory, gateway).checkout(request))
                .isEqualTo(new Err<>(new OutOfStock("TEE-0002", 2, 1)));
        assertThat(new OptionalCheckout(inventory, gateway).checkout(request)).isEmpty();
        assertThat(catchThrowableOfType(CheckoutException.class,
                () -> new ExceptionCheckout(inventory, gateway).checkout(request)))
                .isInstanceOf(CheckoutException.OutOfStock.class);
        assertThat(gateway.calls()).isZero();
        assertThat(inventory.log()).isEmpty();
    }

    @Test
    void aDeclinedPaymentReleasesTheReservedStock() {
        var request = new CheckoutRequest(List.of(MUGS), "", "card-declined");
        freshFakes();
        Result<Receipt, CheckoutError> result = new ResultCheckout(inventory, gateway).checkout(request);
        assertThat(result).isEqualTo(new Err<>(new PaymentDeclined(25_00)));
        assertThat(inventory.log()).containsExactly("reserve MUG-0001 x2", "release MUG-0001 x2");
        assertThat(inventory.available("MUG-0001")).isEqualTo(5);
        freshFakes();
        catchThrowableOfType(CheckoutException.class,
                () -> new ExceptionCheckout(inventory, gateway).checkout(request));
        assertThat(inventory.log()).containsExactly("reserve MUG-0001 x2", "release MUG-0001 x2");
    }

    @Test
    void aSuccessfulCheckoutKeepsTheReservation() {
        freshFakes();
        var result = new ResultCheckout(inventory, gateway)
                .checkout(new CheckoutRequest(List.of(MUGS), "", "card-ok"));
        assertThat(result).isEqualTo(new Ok<>(new Receipt("PAY-1", 25_00)));
        assertThat(inventory.available("MUG-0001")).isEqualTo(3);
        assertThat(gateway.calls()).isEqualTo(1);
    }

    @Test
    void checkoutErrorMessagesAreExact() {
        assertThat(CheckoutErrors.message(new EmptyCart())).isEqualTo("your cart is empty");
        assertThat(CheckoutErrors.message(new OutOfStock("TEE-0002", 2, 1)))
                .isEqualTo("only 1 x TEE-0002 left (you asked for 2)");
        assertThat(CheckoutErrors.message(new InvalidCoupon("FREE"))).isEqualTo("coupon FREE is not valid");
        assertThat(CheckoutErrors.message(new PaymentDeclined(25_00))).isEqualTo("payment of 25.00 was declined");
    }

    @Test
    void demoPrintsTheThreeStylesSideBySide() {
        assertThat(Console.capture(() -> CheckoutStylesDemo.main(new String[0]))).isEqualTo("""
                -- happy path
                exceptions: Receipt[paymentId=PAY-1, totalCents=4500]
                Optional:   Optional[Receipt[paymentId=PAY-1, totalCents=4500]]
                Result:     Ok[value=Receipt[paymentId=PAY-1, totalCents=4500]]
                -- short stock
                exceptions: threw OutOfStock: only 1 x TEE-0002 left (you asked for 2)
                Optional:   Optional.empty
                Result:     Err[error=OutOfStock[sku=TEE-0002, requested=2, available=1]]
                -- declined
                exceptions: threw PaymentDeclined: payment of 25.00 was declined
                Optional:   Optional.empty
                Result:     Err[error=PaymentDeclined[amountCents=2500]]
                -- the Result caller must handle every kind (exhaustive switch, no default)
                short stock -> only 1 x TEE-0002 left (you asked for 2)
                declined    -> payment of 25.00 was declined
                """);
    }
}
