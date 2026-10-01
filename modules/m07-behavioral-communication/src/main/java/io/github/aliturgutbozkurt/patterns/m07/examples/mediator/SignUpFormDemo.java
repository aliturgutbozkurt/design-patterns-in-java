package io.github.aliturgutbozkurt.patterns.m07.examples.mediator;

import io.github.aliturgutbozkurt.patterns.m07.examples.mediator.form.SignUpForm;
import java.util.Locale;

/**
 * Run: {@code java modules/m07-behavioral-communication/src/main/java/io/github/aliturgutbozkurt/patterns/m07/examples/mediator/SignUpFormDemo.java}
 *
 * @see "m07 lesson, section Mediator"
 */
public final class SignUpFormDemo {

    private SignUpFormDemo() {}

    public static void main(String[] args) {
        var form = new SignUpForm();
        show("new form", form);
        form.email().type("ada@example.com");
        form.password().type("s3cret-pass");
        show("e-mail + password", form);
        form.terms().check();
        show("terms accepted", form);
        form.business().check();
        show("business account", form);
        form.company().type("Analytical Engines");
        show("company named", form);
        form.business().uncheck();
        show("personal again", form);
        form.submit().click();
        System.out.println("submitted: " + form.submissions());
    }

    private static void show(String step, SignUpForm form) {
        String company = form.company().isEnabled() ? "enabled '" + form.company().text() + "'" : "disabled";
        String submit = form.submit().isEnabled() ? "enabled" : "disabled";
        System.out.println(String.format(Locale.ROOT, "%-18s -> submit %s, company %s", step, submit, company));
    }
}
