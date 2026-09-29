package io.github.aliturgutbozkurt.patterns.m07.examples.mediator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import io.github.aliturgutbozkurt.patterns.m07.examples.mediator.form.SignUpForm;
import io.github.aliturgutbozkurt.patterns.m07.examples.mediator.form.Widget;
import io.github.aliturgutbozkurt.patterns.m07.support.Console;
import java.lang.reflect.Field;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

class SignUpFormTest {

    private final SignUpForm form = new SignUpForm();

    private void fillPersonal() {
        form.email().type("ada@example.com");
        form.password().type("12345678");
        form.terms().check();
    }

    @Test
    void submitIsDisabledInitially() {
        assertThat(form.submit().isEnabled()).isFalse();
        assertThat(form.company().isEnabled()).isFalse();
        assertThatIllegalStateException().isThrownBy(form.submit()::click).withMessage("submit is disabled");
    }

    @Test
    void submitIsEnabledOnlyWhenEmailPasswordAndTermsAreValid() {
        form.email().type("ada@example");
        form.password().type("12345678");
        form.terms().check();
        assertThat(form.submit().isEnabled()).as("invalid e-mail").isFalse();
        form.email().type("ada@example.com");
        assertThat(form.submit().isEnabled()).isTrue();
        form.password().type("1234567");
        assertThat(form.submit().isEnabled()).as("7-character password").isFalse();
        form.password().type("12345678");
        form.terms().uncheck();
        assertThat(form.submit().isEnabled()).as("terms not accepted").isFalse();
    }

    @Test
    void checkingBusinessEnablesCompanyAndUncheckingClearsAndDisablesIt() {
        form.business().check();
        assertThat(form.company().isEnabled()).isTrue();
        form.company().type("Acme");
        form.business().uncheck();
        assertThat(form.company().isEnabled()).isFalse();
        assertThat(form.company().text()).isEmpty();
    }

    @Test
    void companyIsRequiredOnlyForBusinessAccounts() {
        fillPersonal();
        assertThat(form.submit().isEnabled()).isTrue();
        form.business().check();
        assertThat(form.submit().isEnabled()).isFalse();
        form.company().type("Acme");
        assertThat(form.submit().isEnabled()).isTrue();
        form.submit().click();
        assertThat(form.submissions()).containsExactly("ada@example.com (business: Acme)");
    }

    @Test
    void widgetsDoNotReferenceEachOther() {
        assertThat(Arrays.stream(Widget.class.getDeclaredFields()).<Class<?>>map(Field::getType).toList())
                .noneMatch(Widget.class::isAssignableFrom)
                .contains(SignUpForm.class);
    }

    @Test
    void demoPrintsHowTheFormReactsToEachStep() {
        assertThat(Console.capture(() -> SignUpFormDemo.main(new String[0]))).isEqualTo("""
                new form           -> submit disabled, company disabled
                e-mail + password  -> submit disabled, company disabled
                terms accepted     -> submit enabled, company disabled
                business account   -> submit disabled, company enabled ''
                company named      -> submit enabled, company enabled 'Analytical Engines'
                personal again     -> submit enabled, company disabled
                submitted: [ada@example.com (personal)]
                """);
    }
}
