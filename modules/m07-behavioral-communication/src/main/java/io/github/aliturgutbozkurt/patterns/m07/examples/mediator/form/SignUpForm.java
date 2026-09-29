package io.github.aliturgutbozkurt.patterns.m07.examples.mediator.form;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * GUI Mediator (headless): every rule about how the sign-up widgets depend on each other lives here, in
 * {@link #changed(Widget)}, instead of in the widgets referencing each other.
 *
 * @see "m07 lesson, section Mediator — GUI forms"
 */
public final class SignUpForm {

    private static final Pattern EMAIL = Pattern.compile("[^@\\s]+@[^@\\s]+\\.[^@\\s]+");
    private static final int MIN_PASSWORD_LENGTH = 8;

    private final TextField email = new TextField("email", this);
    private final TextField password = new TextField("password", this);
    private final Checkbox terms = new Checkbox("terms", this);
    private final Checkbox business = new Checkbox("business account", this);
    private final TextField company = new TextField("company", this);
    private final Button submit = new Button("submit", this);
    private final List<String> submissions = new ArrayList<>();

    public SignUpForm() {
        company.setEnabled(false);
        submit.setEnabled(false);
    }

    /** The mediator's single entry point: a widget reports that the user changed it. */
    void changed(Widget source) {
        if (source == submit) {
            submissions.add(email.text() + (business.isChecked() ? " (business: " + company.text() + ")" : " (personal)"));
            return;
        }
        if (source == business) {
            if (!business.isChecked()) {
                company.clear();
            }
            company.setEnabled(business.isChecked());
        }
        submit.setEnabled(isComplete());
    }

    private boolean isComplete() {
        return EMAIL.matcher(email.text()).matches()
                && password.text().length() >= MIN_PASSWORD_LENGTH
                && terms.isChecked()
                && (!business.isChecked() || !company.text().isBlank());
    }

    public TextField email() {
        return email;
    }

    public TextField password() {
        return password;
    }

    public Checkbox terms() {
        return terms;
    }

    public Checkbox business() {
        return business;
    }

    public TextField company() {
        return company;
    }

    public Button submit() {
        return submit;
    }

    /** What was submitted so far, e.g. {@code ada@example.com (personal)}. */
    public List<String> submissions() {
        return List.copyOf(submissions);
    }
}
