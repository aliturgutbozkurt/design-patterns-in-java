package io.github.aliturgutbozkurt.patterns.m09.exercises.ex01.legacy;

/** GIVEN — do not modify. An intern with a monthly stipend, possibly paid by the university. */
public class LegacyIntern extends LegacyEmployee {

    private final long stipendCents;
    private final boolean universityFunded;

    public LegacyIntern(String id, String name, long stipendCents, boolean universityFunded) {
        super(id, name);
        this.stipendCents = stipendCents;
        this.universityFunded = universityFunded;
    }

    public long getStipendCents() {
        return stipendCents;
    }

    public boolean isUniversityFunded() {
        return universityFunded;
    }

    @Override
    public <R> R accept(EmployeeVisitor<R> visitor) {
        return visitor.visitIntern(this);
    }
}
